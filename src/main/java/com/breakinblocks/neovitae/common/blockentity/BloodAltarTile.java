package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.api.altar.rune.AltarRuneModifiers;
import com.breakinblocks.neovitae.api.altar.rune.IAltarRuneType;
import com.breakinblocks.neovitae.api.altar.rune.RuneInstance;
import com.breakinblocks.neovitae.api.event.AltarRuneEvent;
import com.breakinblocks.neovitae.common.datamap.AltarRuneStats;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datamap.BMDataMaps;
import com.breakinblocks.neovitae.common.datamap.BloodOrb;
import com.breakinblocks.neovitae.common.event.BloodAltarCraftEvent;
import com.breakinblocks.neovitae.common.event.NeoVitaeCraftedEvent;
import com.breakinblocks.neovitae.common.fluid.BMFluids;
import com.breakinblocks.neovitae.common.recipe.BMRecipes;
import com.breakinblocks.neovitae.api.recipe.BloodAltarInput;
import com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe;
import com.breakinblocks.neovitae.common.tag.BMTags;
import com.breakinblocks.neovitae.api.altar.IBloodAltar;
import com.breakinblocks.neovitae.util.AltarScanResult;
import com.breakinblocks.neovitae.util.AltarUtil;
import com.breakinblocks.neovitae.api.soul.SoulTicket;
import com.breakinblocks.neovitae.util.helper.SoulNetworkHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BloodAltarTile extends BaseTile implements IFluidHandler, IBloodAltar {

    private volatile boolean isActive = false;
    private volatile boolean canFill = false;
    private BloodAltarRecipe currentRecipe = null;
    private int cooldownAfterCrafting = 0;
    private int progress = 0;
    private int tier = 0;
    private int ticks;
    private int inputTank = 0;
    private int outputTank = 0;
    private int mainTank = 0;
    private int chargingTank = 0;
    private volatile boolean isSignaling = false;

    // Grace period (in ticks) before enforcing capacity limits after structure change
    // Prevents fluid loss when reconfiguring the altar
    private static final int CAPACITY_GRACE_PERIOD = 100; // 5 seconds
    private int capacityGraceTicks = 0;
    private int previousMainCapacity = 0;
    private int previousIOCapacity = 0;
    private int previousChargingCapacity = 0;

    public ItemStackHandler inv = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };

    private float capacityMod = 1;
    private int tickRate = 20;
    private float consumptionMod = 1;
    private float sacrificeMod = 1;
    private float selfSacMod = 1;
    private float dislocationMod = 1;
    private float orbCapMod = 1;
    private float chargeAmountMod = 1;
    private float chargeCapMod = 1;
    private float efficiencyMod = 1;

    public BloodAltarTile(BlockPos pos, BlockState blockState) {
        super(BMTiles.BLOOD_ALTAR_TYPE.get(), pos, blockState);
    }

    /**
     * Calculates altar stats from runes using the datamap system.
     *
     * <p>This method looks up each rune block's stats from the {@code altar_rune_stats}
     * datamap and accumulates the bonuses. After calculating base stats, it fires the
     * {@link AltarRuneEvent.CalculateStats} event to allow dynamic modifications.</p>
     *
     * <h2>Static vs Dynamic Runes</h2>
     * <ul>
     *   <li><b>Static runes</b>: Get their stats from the datamap (datapack-customizable)</li>
     *   <li><b>Dynamic runes</b>: Can modify stats in the CalculateStats event based on runtime state</li>
     * </ul>
     *
     * @param allRunes Unified map of all rune types (both built-in and custom) to counts
     * @param runeInstances List of individual rune instances for addon inspection
     */
    public void calculateStats(Map<IAltarRuneType, Integer> allRunes, List<RuneInstance> runeInstances) {
        // Accumulate stats from datamap for each physical rune block
        double totalCapacityMod = 0;
        double augCapacityMultiplier = 1.0;
        double totalConsumptionMod = 0;
        double totalSacrificeMod = 0;
        double totalSelfSacrificeMod = 0;
        double dislocationMultiplier = 1.0;
        double totalOrbCapacityMod = 0;
        int totalAccelerationMod = 0;
        int totalChargeAmountMod = 0;
        double efficiencyMultiplier = 1.0;
        int chargingRuneCount = 0;

        // Look up each rune block's stats from the datamap
        for (RuneInstance instance : runeInstances) {
            AltarRuneStats stats = BuiltInRegistries.BLOCK.wrapAsHolder(instance.block()).getData(BMDataMaps.ALTAR_RUNE_STATS);
            if (stats != null) {
                // Additive stats
                totalCapacityMod += stats.getCapacityMod(0);
                totalConsumptionMod += stats.getConsumptionMod(0);
                totalSacrificeMod += stats.getSacrificeMod(0);
                totalSelfSacrificeMod += stats.getSelfSacrificeMod(0);
                totalOrbCapacityMod += stats.getOrbCapacityMod(0);
                totalAccelerationMod += stats.getAccelerationMod(0);
                totalChargeAmountMod += stats.getChargeAmountMod(0);

                // Multiplicative stats (compound)
                double augPower = stats.getAugmentedCapacityPower(1.0);
                if (augPower != 1.0) {
                    augCapacityMultiplier *= augPower;
                }

                double disPower = stats.getDislocationPower(1.0);
                if (disPower != 1.0) {
                    dislocationMultiplier *= disPower;
                }

                double effPower = stats.getEfficiencyPower(1.0);
                if (effPower != 1.0) {
                    efficiencyMultiplier *= effPower;
                }

                // Track charging rune count for charge capacity calculation
                if (stats.chargeAmountMod().isPresent()) {
                    chargingRuneCount++;
                }
            }
        }

        // Calculate final base stats
        float baseCapacityMod = (float) ((1.0 + totalCapacityMod) * augCapacityMultiplier);
        int baseTickRate = Math.max(AltarConstants.MIN_TICK_RATE, AltarConstants.BASE_TICK_RATE - totalAccelerationMod);
        float baseConsumptionMod = (float) totalConsumptionMod;
        float baseSacrificeMod = (float) totalSacrificeMod;
        float baseSelfSacMod = (float) totalSelfSacrificeMod;
        float baseDislocationMod = (float) dislocationMultiplier;
        float baseOrbCapMod = (float) totalOrbCapacityMod;
        float baseChargeAmountMod = (float) (totalChargeAmountMod * (1 + baseConsumptionMod / 2));
        float baseChargeCapMod = (float) Math.max(AltarConstants.CHARGE_CAPACITY_MIN_FACTOR * baseCapacityMod, 1) * chargingRuneCount;
        float baseEfficiencyMod = (float) efficiencyMultiplier;

        // Create modifiers container with base values
        AltarRuneModifiers modifiers = new AltarRuneModifiers(
                baseCapacityMod, baseTickRate, baseConsumptionMod,
                baseSacrificeMod, baseSelfSacMod, baseDislocationMod,
                baseOrbCapMod, baseChargeAmountMod, baseChargeCapMod,
                baseEfficiencyMod
        );

        // Fire CalculateStats event to allow mods to modify based on all runes
        // This is where dynamic runes (like Animus) can modify stats based on runtime state
        AltarRuneEvent.CalculateStats calculateEvent = new AltarRuneEvent.CalculateStats(
                this, level, worldPosition, tier, modifiers, allRunes, runeInstances
        );
        NeoForge.EVENT_BUS.post(calculateEvent);

        // Apply final values from the modifiers
        this.capacityMod = modifiers.getCapacityMod();
        this.tickRate = modifiers.getTickRate();
        this.consumptionMod = modifiers.getConsumptionMod();
        this.sacrificeMod = modifiers.getSacrificeMod();
        this.selfSacMod = modifiers.getSelfSacrificeMod();
        this.dislocationMod = modifiers.getDislocationMod();
        this.orbCapMod = modifiers.getOrbCapacityMod();
        this.chargeAmountMod = modifiers.getChargeAmountMod();
        this.chargeCapMod = modifiers.getChargeCapacityMod();
        this.efficiencyMod = modifiers.getEfficiencyMod();

        // Fire PostCalculate event for informational purposes
        NeoForge.EVENT_BUS.post(new AltarRuneEvent.PostCalculate(
                this, level, worldPosition, tier, modifiers, runeInstances
        ));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BloodAltarTile tile) {
        if (level.isClientSide) {
            return;
        }

        if (tile.isSignaling()) {
            tile.setSignaling(false);
        }

        tile.incrementTicks();

        // Decrement grace period
        if (tile.getCapacityGraceTicks() > 0) {
            tile.decrementCapacityGraceTicks();
        }

        if (tile.getTicks() % AltarConstants.STRUCTURE_CHECK_INTERVAL == 0) {
            int newTier = AltarUtil.getTier(level, pos);
            tile.setTier(newTier); // Update the stored tier

            // Scan for all runes in the structure (returns both counts and instances)
            AltarScanResult scanResult = AltarUtil.scanForRunes(newTier, level, pos);
            Map<IAltarRuneType, Integer> allRunes = new HashMap<>(scanResult.runeCounts());
            List<RuneInstance> runeInstances = scanResult.runeInstances();

            // Fire GatherRunes event to allow mods to add additional runes
            AltarRuneEvent.GatherRunes gatherEvent = new AltarRuneEvent.GatherRunes(
                    tile, level, pos, newTier, allRunes, runeInstances
            );
            NeoForge.EVENT_BUS.post(gatherEvent);

            // Calculate stats with the gathered runes (events are fired inside)
            tile.calculateStats(allRunes, runeInstances);

            // Check if capacity decreased - if so, start grace period
            int newMainCapacity = tile.getMainCapacity();
            int newIOCapacity = tile.getIOCapacity();
            int newChargingCapacity = tile.getChargingCapacity();

            if (newMainCapacity < tile.previousMainCapacity ||
                newIOCapacity < tile.previousIOCapacity ||
                newChargingCapacity < tile.previousChargingCapacity) {
                tile.capacityGraceTicks = AltarConstants.CAPACITY_GRACE_PERIOD;
            }

            tile.previousMainCapacity = newMainCapacity;
            tile.previousIOCapacity = newIOCapacity;
            tile.previousChargingCapacity = newChargingCapacity;

            // Only enforce capacity limits when grace period is over
            if (tile.getCapacityGraceTicks() == 0) {
                tile.setMainTank(Math.min(tile.getMainTank(), newMainCapacity));
                tile.setInputTank(Math.min(tile.getInputTank(), newIOCapacity));
                tile.setOutputTank(Math.min(tile.getOutputTank(), newIOCapacity));
                tile.setChargingTank(Math.min(tile.getChargingTank(), newChargingCapacity));
            }

            tile.setChanged();
            if (tile.isActive() || tile.getCooldownAfterCrafting() <= 0) {
                tile.checkAction();
            }
        }

        if (tile.getTicks() % Math.max(tile.tickRate, 1) == 0) {
            float ioAmount = AltarConstants.BASE_IO_RATE * tile.dislocationMod;
            int input = (int) Math.min(tile.getInputTank(), ioAmount);
            input = (int) Math.min(input, tile.getMainCapacity() - tile.getMainTank());
            tile.setInputTank(tile.getInputTank() - input);
            tile.setMainTank(tile.getMainTank() + input);

            int output = (int) Math.min(tile.getMainTank(), ioAmount);
            output = (int) Math.min(output, tile.getIOCapacity() - tile.getOutputTank());
            tile.setMainTank(tile.getMainTank() - output);
            tile.setOutputTank(tile.getOutputTank() + output);

            if (!tile.isActive()) {
                tile.setProgress(0);
                int charge = (int) Math.min(tile.getMainTank(), tile.chargeAmountMod);
                charge = (int) Math.min(charge, tile.getChargingTank() - tile.getChargingTank());
                tile.setMainTank(tile.getMainTank() - charge);
                tile.setChargingTank(tile.getChargingTank() + charge);
            }
        }

        if (!tile.isActive() && tile.getCooldownAfterCrafting() > 0) {
            tile.setCooldownAfterCrafting(tile.getCooldownAfterCrafting() - 1);
            if (tile.getCooldownAfterCrafting() <= 0) {
                tile.checkAction();
            }
            return;
        }

        if (!tile.canFill() && tile.getCurrentRecipe() == null) {
            tile.checkAction();
            return;
        }

        ItemStack inputStack = tile.inv.getStackInSlot(0);
        if (inputStack.isEmpty()) {
            return;
        }

        if (!tile.canFill()) {
            boolean hasOperated = false;
            int inputSize = inputStack.getCount();
            if (tile.getChargingTank() > 0) {
                int chargeDrained = Math.min(tile.getCurrentRecipe().getTotalBlood() * inputSize - tile.getProgress(), tile.getChargingTank());
                tile.setChargingTank(tile.getChargingTank() - chargeDrained);
                tile.setProgress(tile.getProgress() + chargeDrained);
                hasOperated = true;
            }
            if (tile.getMainTank() > 0) {
                int drained = Math.min(tile.getMainTank(), (int) (tile.getCurrentRecipe().getCraftSpeed() * (1 + tile.consumptionMod)));
                drained = Math.min(drained, tile.getCurrentRecipe().getTotalBlood() * inputSize - tile.getProgress());
                tile.setMainTank(tile.getMainTank() - drained);
                tile.setProgress(tile.getProgress() + drained);
                hasOperated = true;

                if (tile.getTicks() % AltarConstants.PARTICLE_FREQUENCY_REDSTONE == 0) {
                    ((ServerLevel) level).sendParticles(DustParticleOptions.REDSTONE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 1, 0.2, 1.0, 0.2, 0);
                }
            } else if (!hasOperated && tile.getProgress() > 0) {
                tile.setProgress(tile.getProgress() - (int) (tile.getCurrentRecipe().getDrainSpeed() * (1 + tile.efficiencyMod)));
                if (tile.getProgress() < 0) {
                    tile.setProgress(0);
                }
                if (tile.getTicks() % AltarConstants.PARTICLE_FREQUENCY_SMOKE == 0) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 1, 0.1, 1.0, 0.1, 0);
                }
            }

            if (hasOperated && tile.getProgress() >= tile.getCurrentRecipe().getTotalBlood() * inputSize) {
                BloodAltarRecipe recipe = tile.getCurrentRecipe();
                // Use assemble() to properly handle component copying from input to output
                BloodAltarInput recipeInput = new BloodAltarInput(inputStack, tile.getTier());
                ItemStack result = recipe.assemble(recipeInput, level.registryAccess());
                result.setCount(inputSize);

                // Fire pre-craft event (cancellable)
                BloodAltarCraftEvent.Crafting craftingEvent = new BloodAltarCraftEvent.Crafting(
                        tile, recipe, inputStack, result);
                if (NeoForge.EVENT_BUS.post(craftingEvent).isCanceled()) {
                    // Cancelled - reset progress but don't produce output
                    tile.setProgress(0);
                    tile.setCooldownAfterCrafting(AltarConstants.CRAFTING_COOLDOWN_TICKS);
                    tile.setActive(false);
                    tile.setCurrentRecipe(null);
                    return;
                }

                // Use potentially modified output from event
                ItemStack finalOutput = craftingEvent.getOutput();

                // Legacy event for backwards compatibility
                NeoVitaeCraftedEvent.Altar legacyEvent = new NeoVitaeCraftedEvent.Altar(finalOutput, inputStack);
                NeoForge.EVENT_BUS.post(legacyEvent);
                tile.inv.setStackInSlot(0, legacyEvent.getOutput());

                if (level.getBlockState(pos.below()).is(BMTags.Blocks.PULSE_ON_CRAFTING)) {
                    tile.setSignaling(true);
                }
                tile.setProgress(0);
                tile.setCooldownAfterCrafting(AltarConstants.CRAFTING_COOLDOWN_TICKS);
                tile.setActive(false);
                tile.setCurrentRecipe(null);
                ((ServerLevel) level).sendParticles(DustParticleOptions.REDSTONE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 40, 0.3, 0.0, 0.3, 0);

                // Fire post-craft event (not cancellable)
                NeoForge.EVENT_BUS.post(new BloodAltarCraftEvent.Crafted(
                        tile, recipe, inputStack, legacyEvent.getOutput()));
            }
        } else {
            Binding binding = inputStack.getOrDefault(BMDataComponents.BINDING, Binding.EMPTY);
            BloodOrb orb = inputStack.getItemHolder().getData(BMDataMaps.BLOOD_ORB_STATS);
            if (binding.isEmpty() || orb == null) {
                return;
            }
            if (tile.getMainTank() > 0) {
                int available = Math.min(tile.getMainTank(), (int) (orb.fillRate() * (1 + tile.consumptionMod)));
                int drained = SoulNetworkHelper.getSoulNetwork(binding.uuid()).add(SoulTicket.create(available), (int) (orb.capacity() * (1 + tile.orbCapMod)));
                tile.setMainTank(tile.getMainTank() - drained);
                if (drained > 0) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.WITCH, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 1, 0, 0, 0, 0.001);
                }
            }
        }

        tile.setChanged();
    }

    public void sacrificialDaggerCall(int lpAdded, boolean isSacrifice) {
        setMainTank(getMainTank() + Math.min((getMainCapacity() - getMainTank()), (int) ((isSacrifice ? 1 + sacrificeMod : 1 + selfSacMod) * lpAdded)));
        setChanged();
    }

    public void checkAction() {
        if (!isActive()) {
            setProgress(0);
        }

        ItemStack inputStack = inv.getStackInSlot(0);
        Binding inputBinding = inputStack.get(BMDataComponents.BINDING);
        Optional<RecipeHolder<com.breakinblocks.neovitae.api.recipe.BloodAltarRecipe>> optionalHolder = level.getRecipeManager().getRecipeFor(BMRecipes.BLOOD_ALTAR_TYPE.get(), new BloodAltarInput(inputStack, getTier()), level);
        if (!(inputBinding == null || inputBinding.isEmpty())) {
            setCanFill(true);
            setActive(true);
            setCurrentRecipe(null);
            return;
        } else if (optionalHolder.isPresent()) {
            setCurrentRecipe(optionalHolder.get().value());
            setActive(true);
            setCanFill(false);
            return;
        }
        setActive(false);
    }

    public int analogSignal() {
        if (level.getBlockState(getBlockPos().below()).is(BMTags.Blocks.SOUL_NETWORK_COMPARATOR)) {
            ItemStack content = inv.getStackInSlot(0);
            Binding binding = content.getOrDefault(BMDataComponents.BINDING, Binding.EMPTY);
            BloodOrb orb = content.getItemHolder().getData(BMDataMaps.BLOOD_ORB_STATS);
            if (binding.isEmpty() || orb == null) {
                return 0;
            }
            float current = SoulNetworkHelper.getSoulNetwork(binding).getCurrentEssence();
            float max = (int) ((float) orb.capacity() * (1 + orbCapMod));
            return Mth.lerpDiscrete(current / max, 0, 15);
        }

        return Mth.lerpDiscrete((float) getMainTank() / (float) getMainCapacity(), 0, 15);
    }

    public int getMainCapacity() {
        return (int) ((float) FluidType.BUCKET_VOLUME * 10F * capacityMod);
    }

    public int getIOCapacity() {
        return (int) ((float) FluidType.BUCKET_VOLUME * 1F * capacityMod);
    }

    public int getChargingCapacity() {
        return (int) ((float) FluidType.BUCKET_VOLUME * chargeCapMod);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        CompoundTag stats = tag.getCompound("stats");
        tickRate = stats.getInt("tickrate");
        ticks = stats.getInt("ticks");
        capacityMod = stats.getFloat("capacity");
        consumptionMod = stats.getFloat("consumption");
        efficiencyMod = stats.getFloat("efficiency");
        sacrificeMod = stats.getFloat("sacrifice");
        selfSacMod = stats.getFloat("selfsacrifice");
        dislocationMod = stats.getFloat("dislocation");
        orbCapMod = stats.getFloat("orb");
        chargeAmountMod = stats.getFloat("chargeamount");
        chargeCapMod = stats.getFloat("chargecap");

        CompoundTag tanks = tag.getCompound("tanks");

        inputTank = tanks.getInt("input");
        outputTank = tanks.getInt("output");
        mainTank = tanks.getInt("main");
        chargingTank = tanks.getInt("charging");
        progress = tanks.getInt("progress");

        inv.deserializeNBT(registries, tag.getCompound("inventory"));

        this.isSignaling = tag.getBoolean("signal");

        this.tier = tag.getInt("tier");
        this.capacityGraceTicks = tag.getInt("capacityGrace");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag stats = new CompoundTag();
        stats.putInt("tickrate", tickRate);
        stats.putInt("ticks", ticks % 2048);
        stats.putFloat("capacity", capacityMod);
        stats.putFloat("consumption", consumptionMod);
        stats.putFloat("efficiency", efficiencyMod);
        stats.putFloat("sacrifice", sacrificeMod);
        stats.putFloat("selfsacrifice", selfSacMod);
        stats.putFloat("dislocation", dislocationMod);
        stats.putFloat("orb", orbCapMod);
        stats.putFloat("chargeamount", chargeAmountMod);
        stats.putFloat("chargecap", chargeCapMod);

        CompoundTag tanks = new CompoundTag();
        tanks.putInt("input", inputTank);
        tanks.putInt("output", outputTank);
        tanks.putInt("main", mainTank);
        tanks.putInt("charging", chargingTank);
        tanks.putInt("progress", progress);

        CompoundTag inventory = inv.serializeNBT(registries);

        tag.put("tanks", tanks);

        tag.put("inventory", inventory);

        tag.put("stats", stats);
        tag.putInt("tier", this.tier);
        tag.putBoolean("signal", isSignaling);
        tag.putInt("capacityGrace", capacityGraceTicks);
    }

    @Override
    public int getTanks() {
        return 3;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return switch (tank) {
            case 0 -> new FluidStack(BMFluids.LIFE_ESSENCE_SOURCE, getMainTank());
            case 1 -> new FluidStack(BMFluids.LIFE_ESSENCE_SOURCE, getInputTank());
            case 2 -> new FluidStack(BMFluids.LIFE_ESSENCE_SOURCE, getOutputTank());
            default -> FluidStack.EMPTY;
        };
    }

    @Override
    public int getTankCapacity(int tank) {
        return switch (tank) {
          case 0 -> getMainCapacity();
          case 1,2 -> getIOCapacity();
          default -> 0;
        };
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        // Use fluid tag for flexibility - allows other mods to add compatible fluids
        return stack.is(BMTags.Fluids.LIFE_ESSENCE);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (!isFluidValid(0, resource)) {
            return 0;
        }
        int availableSpace = Math.max(getIOCapacity() - getInputTank(), 0);
        int canFill = Math.min(availableSpace, resource.getAmount());

        if (action.execute()) {
            setInputTank(getInputTank() + canFill);
            this.setChanged();
        }

        return canFill;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (!isFluidValid(0, resource)) {
            return FluidStack.EMPTY;
        }

        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        int toDrain = Math.min(getOutputTank(), maxDrain);

        if (action.execute()) {
            setOutputTank(getOutputTank() - toDrain);
            this.setChanged();
        }

        return new FluidStack(BMFluids.LIFE_ESSENCE_SOURCE, toDrain);
    }

    // Getters
    public boolean isActive() { return isActive; }
    public boolean canFill() { return canFill; }
    public BloodAltarRecipe getCurrentRecipe() { return currentRecipe; }
    public int getCooldownAfterCrafting() { return cooldownAfterCrafting; }
    public int getProgress() { return progress; }
    public int getTier() { return tier; }
    public int getTicks() { return ticks; }
    public int getInputTank() { return inputTank; }
    public int getOutputTank() { return outputTank; }
    public int getMainTank() { return mainTank; }
    public int getChargingTank() { return chargingTank; }
    public boolean isSignaling() { return isSignaling; }
    public int getCapacityGraceTicks() { return capacityGraceTicks; }
    public int getPreviousMainCapacity() { return previousMainCapacity; }
    public int getPreviousIOCapacity() { return previousIOCapacity; }
    public int getPreviousChargingCapacity() { return previousChargingCapacity; }

    // Setters / Incremeters
    private void setSignaling(boolean signaling) { this.isSignaling = signaling; }
    private void incrementTicks() { this.ticks++; }
    private void decrementCapacityGraceTicks() { this.capacityGraceTicks--; }
    private void setMainTank(int mainTank) { this.mainTank = mainTank; }
    private void setInputTank(int inputTank) { this.inputTank = inputTank; }
    private void setOutputTank(int outputTank) { this.outputTank = outputTank; }
    private void setChargingTank(int chargingTank) { this.chargingTank = chargingTank; }
    private void setProgress(int progress) { this.progress = progress; }
    private void setCurrentRecipe(BloodAltarRecipe recipe) { this.currentRecipe = recipe; }
    private void setActive(boolean active) { this.isActive = active; }
    private void setCanFill(boolean canFill) { this.canFill = canFill; }
    private void setCooldownAfterCrafting(int cooldown) { this.cooldownAfterCrafting = cooldown; }
    private void setTier(int tier) { this.tier = tier; }

    // IBloodAltar interface implementation

    @Override
    public int getCurrentBlood() {
        return getMainTank();
    }

    @Override
    public int getCapacity() {
        return getMainCapacity();
    }

    @Override
    public float getProgressFloat() {
        if (currentRecipe == null) {
            return 0f;
        }
        int requiredBlood = getLiquidRequired();
        if (requiredBlood <= 0) {
            return 0f;
        }
        return (float) progress / requiredBlood;
    }

    @Override
    public int getConsumptionRate() {
        if (currentRecipe == null) {
            return 0;
        }
        return (int) (currentRecipe.getCraftSpeed() * (1 + consumptionMod));
    }

    @Override
    public int getDrainRate() {
        if (currentRecipe == null) {
            return 0;
        }
        return (int) (currentRecipe.getDrainSpeed() * (1 + efficiencyMod));
    }

    @Override
    public ItemStack getStackInSlot() {
        return inv.getStackInSlot(0);
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return this;
    }

    @Override
    public void checkTier() {
        if (level != null && !level.isClientSide) {
            int newTier = AltarUtil.getTier(level, worldPosition);
            setTier(newTier);

            // Scan for all runes in the structure
            AltarScanResult scanResult = AltarUtil.scanForRunes(newTier, level, worldPosition);
            Map<IAltarRuneType, Integer> allRunes = new HashMap<>(scanResult.runeCounts());
            List<RuneInstance> runeInstances = scanResult.runeInstances();

            // Fire GatherRunes event
            AltarRuneEvent.GatherRunes gatherEvent = new AltarRuneEvent.GatherRunes(
                    this, level, worldPosition, newTier, allRunes, runeInstances
            );
            NeoForge.EVENT_BUS.post(gatherEvent);

            // Calculate stats with events
            calculateStats(allRunes, runeInstances);
            setChanged();
        }
    }

    @Override
    public int getLiquidRequired() {
        if (currentRecipe == null) {
            return 0;
        }
        return currentRecipe.getTotalBlood() * inv.getStackInSlot(0).getCount();
    }

    @Override
    public int getTotalCraftingTime() {
        if (currentRecipe == null || getConsumptionRate() <= 0) {
            return 0;
        }
        return getLiquidRequired() / getConsumptionRate();
    }

    @Override
    public int getCraftingProgress() {
        return progress;
    }

    @Override
    public int getChargingRate() {
        return (int) chargeAmountMod;
    }

    @Override
    public int getChargingFrequency() {
        return tickRate;
    }

    @Override
    public float getBonusCapacity() {
        return capacityMod;
    }

    @Override
    public float getEfficiency() {
        return efficiencyMod;
    }

    @Override
    public float getSelfSacrificeBonus() {
        return selfSacMod;
    }

    @Override
    public float getSacrificeBonus() {
        return sacrificeMod;
    }

    @Override
    public float getSpeedBonus() {
        return consumptionMod;
    }

    @Override
    public float getDislocationBonus() {
        return dislocationMod;
    }

    @Override
    public float getOrbCapacityBonus() {
        return orbCapMod;
    }

    @Override
    public int getTickRate() {
        return tickRate;
    }
}
