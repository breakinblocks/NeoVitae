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
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.datamap.BloodOrb;
import com.breakinblocks.neovitae.common.event.AraVitaeCraftEvent;
import com.breakinblocks.neovitae.common.event.NeoVitaeCraftedEvent;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.api.recipe.AraVitaeInput;
import com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.api.altar.IAraVitae;
import com.breakinblocks.neovitae.util.AltarScanResult;
import com.breakinblocks.neovitae.util.AltarUtil;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;
import com.breakinblocks.neovitae.common.NVSounds;
import net.minecraft.sounds.SoundSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AraVitaeTile extends BaseBlockEntity implements IFluidHandler, IAraVitae {

    private volatile boolean isActive = false;
    private volatile boolean canFill = false;
    private AraVitaeRecipe currentRecipe = null;
    private int cooldownAfterCrafting = 0;
    private int progress = 0;
    private int tier = 0;
    private int ticks;
    private int inputTank = 0;
    private int outputTank = 0;
    private int mainTank = 0;
    private int chargingTank = 0;
    private volatile boolean isSignaling = false;

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

    private AltarRuneModifiers modifiers = new AltarRuneModifiers(1, 20, 1, 1, 1, 1, 1, 1, 1, 1);

    public AraVitaeTile(BlockPos pos, BlockState blockState) {
        super(NVTiles.ARA_VITAE_TYPE.get(), pos, blockState);
    }

    /**
     * Calculates altar stats from runes using the datamap system.
     *
     * @param allRunes Unified map of all rune types (both built-in and custom) to counts
     * @param runeInstances List of individual rune instances for addon inspection
     */
    public void calculateStats(Map<IAltarRuneType, Integer> allRunes, List<RuneInstance> runeInstances) {
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

        for (RuneInstance instance : runeInstances) {
            AltarRuneStats stats = BuiltInRegistries.BLOCK.wrapAsHolder(instance.block()).getData(NVDataMaps.ALTAR_RUNE_STATS);
            if (stats != null) {
                totalCapacityMod += stats.getCapacityMod(0);
                totalConsumptionMod += stats.getConsumptionMod(0);
                totalSacrificeMod += stats.getSacrificeMod(0);
                totalSelfSacrificeMod += stats.getSelfSacrificeMod(0);
                totalOrbCapacityMod += stats.getOrbCapacityMod(0);
                totalAccelerationMod += stats.getAccelerationMod(0);
                totalChargeAmountMod += stats.getChargeAmountMod(0);

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

                if (stats.chargeAmountMod().isPresent()) {
                    chargingRuneCount++;
                }
            }
        }

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

        AltarRuneModifiers modifiers = new AltarRuneModifiers(
                baseCapacityMod, baseTickRate, baseConsumptionMod,
                baseSacrificeMod, baseSelfSacMod, baseDislocationMod,
                baseOrbCapMod, baseChargeAmountMod, baseChargeCapMod,
                baseEfficiencyMod
        );

        AltarRuneEvent.CalculateStats calculateEvent = new AltarRuneEvent.CalculateStats(
                this, level, worldPosition, tier, modifiers, allRunes, runeInstances
        );
        NeoForge.EVENT_BUS.post(calculateEvent);

        this.modifiers = modifiers;

        NeoForge.EVENT_BUS.post(new AltarRuneEvent.PostCalculate(
                this, level, worldPosition, tier, modifiers, runeInstances
        ));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AraVitaeTile tile) {
        if (level.isClientSide) return;

        if (tile.isSignaling()) tile.setSignaling(false);
        tile.incrementTicks();
        if (tile.getCapacityGraceTicks() > 0) tile.decrementCapacityGraceTicks();

        if (tile.getTicks() % AltarConstants.STRUCTURE_CHECK_INTERVAL == 0) {
            tile.tickStructureCheck();
        }

        if (tile.getTicks() % Math.max(tile.modifiers.getTickRate(), 1) == 0) {
            tile.tickFluidTransfer();
        }

        if (!tile.isActive() && tile.getCooldownAfterCrafting() > 0) {
            tile.setCooldownAfterCrafting(tile.getCooldownAfterCrafting() - 1);
            if (tile.getCooldownAfterCrafting() <= 0) tile.checkAction();
            return;
        }

        if (!tile.canFill() && tile.getCurrentRecipe() == null) {
            tile.checkAction();
            return;
        }

        ItemStack inputStack = tile.inv.getStackInSlot(0);
        if (inputStack.isEmpty()) return;

        if (!tile.canFill()) {
            tile.tickRecipeCrafting(inputStack);
        } else {
            tile.tickOrbFilling(inputStack);
        }

        tile.setChanged();
    }

    private void tickStructureCheck() {
        scanAndRecalculate();

        int newMainCapacity = getMainCapacity();
        int newIOCapacity = getIOCapacity();
        int newChargingCapacity = getChargingCapacity();

        if (newMainCapacity < previousMainCapacity ||
            newIOCapacity < previousIOCapacity ||
            newChargingCapacity < previousChargingCapacity) {
            capacityGraceTicks = AltarConstants.CAPACITY_GRACE_PERIOD;
        }

        previousMainCapacity = newMainCapacity;
        previousIOCapacity = newIOCapacity;
        previousChargingCapacity = newChargingCapacity;

        if (getCapacityGraceTicks() == 0) {
            setMainTank(Math.min(getMainTank(), newMainCapacity));
            setInputTank(Math.min(getInputTank(), newIOCapacity));
            setOutputTank(Math.min(getOutputTank(), newIOCapacity));
            setChargingTank(Math.min(getChargingTank(), newChargingCapacity));
        }

        setChanged();
        if (isActive() || getCooldownAfterCrafting() <= 0) {
            checkAction();
        }
    }

    private void tickFluidTransfer() {
        float ioAmount = AltarConstants.BASE_IO_RATE * modifiers.getDislocationMod();
        int input = (int) Math.min(getInputTank(), ioAmount);
        input = (int) Math.min(input, getMainCapacity() - getMainTank());
        setInputTank(getInputTank() - input);
        setMainTank(getMainTank() + input);

        int output = (int) Math.min(getMainTank(), ioAmount);
        output = (int) Math.min(output, getIOCapacity() - getOutputTank());
        setMainTank(getMainTank() - output);
        setOutputTank(getOutputTank() + output);

        if (!isActive()) {
            setProgress(0);
            int charge = (int) Math.min(getMainTank(), modifiers.getChargeAmountMod());
            charge = (int) Math.min(charge, getChargingCapacity() - getChargingTank());
            setMainTank(getMainTank() - charge);
            setChargingTank(getChargingTank() + charge);
        }
    }

    private void tickRecipeCrafting(ItemStack inputStack) {
        boolean hasOperated = false;
        int inputSize = inputStack.getCount();
        int totalRequired = getCurrentRecipe().getTotalBlood() * inputSize;

        if (getChargingTank() > 0) {
            int chargeDrained = Math.min(totalRequired - getProgress(), getChargingTank());
            setChargingTank(getChargingTank() - chargeDrained);
            setProgress(getProgress() + chargeDrained);
            hasOperated = true;
        }
        if (getMainTank() > 0) {
            int drained = Math.min(getMainTank(), (int) (getCurrentRecipe().getCraftSpeed() * (1 + modifiers.getConsumptionMod())));
            drained = Math.min(drained, totalRequired - getProgress());
            setMainTank(getMainTank() - drained);
            setProgress(getProgress() + drained);
            hasOperated = true;

            if (getTicks() % AltarConstants.PARTICLE_FREQUENCY_REDSTONE == 0) {
                ((ServerLevel) level).sendParticles(DustParticleOptions.REDSTONE, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 1, 0.2, 1.0, 0.2, 0);
            }
        } else if (!hasOperated && getProgress() > 0) {
            setProgress(getProgress() - (int) (getCurrentRecipe().getDrainSpeed() * (1 + modifiers.getEfficiencyMod())));
            if (getProgress() < 0) setProgress(0);
            if (getTicks() % AltarConstants.PARTICLE_FREQUENCY_SMOKE == 0) {
                ((ServerLevel) level).sendParticles(ParticleTypes.LARGE_SMOKE, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 1, 0.1, 1.0, 0.1, 0);
            }
            if (getProgress() <= 0) {
                level.playSound(null, worldPosition, NVSounds.BLOOD_ALTAR_FAIL.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
            }
        }

        if (hasOperated && getProgress() >= totalRequired) {
            completeCrafting(inputStack);
        }
    }

    private void completeCrafting(ItemStack inputStack) {
        AraVitaeRecipe recipe = getCurrentRecipe();
        AraVitaeInput recipeInput = new AraVitaeInput(inputStack, getTier());
        ItemStack result = recipe.assemble(recipeInput, level.registryAccess());
        result.setCount(inputStack.getCount());

        AraVitaeCraftEvent.Crafting craftingEvent = new AraVitaeCraftEvent.Crafting(this, recipe, inputStack, result);
        if (NeoForge.EVENT_BUS.post(craftingEvent).isCanceled()) {
            setProgress(0);
            setCooldownAfterCrafting(AltarConstants.CRAFTING_COOLDOWN_TICKS);
            setActive(false);
            setCurrentRecipe(null);
            return;
        }

        NeoVitaeCraftedEvent.Altar legacyEvent = new NeoVitaeCraftedEvent.Altar(craftingEvent.getOutput(), inputStack);
        NeoForge.EVENT_BUS.post(legacyEvent);
        inv.setStackInSlot(0, legacyEvent.getOutput());

        if (level.getBlockState(worldPosition.below()).is(NVTags.Blocks.PULSE_ON_CRAFTING)) {
            setSignaling(true);
        }
        setProgress(0);
        setCooldownAfterCrafting(AltarConstants.CRAFTING_COOLDOWN_TICKS);
        setActive(false);
        setCurrentRecipe(null);
        ((ServerLevel) level).sendParticles(DustParticleOptions.REDSTONE, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 40, 0.3, 0.0, 0.3, 0);
        level.playSound(null, worldPosition, NVSounds.BLOOD_ALTAR_CRAFT_COMPLETE.get(), SoundSource.BLOCKS, 0.7f, 1.0f);

        NeoForge.EVENT_BUS.post(new AraVitaeCraftEvent.Crafted(this, recipe, inputStack, legacyEvent.getOutput()));
    }

    private void tickOrbFilling(ItemStack inputStack) {
        Binding binding = inputStack.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
        BloodOrb orb = inputStack.getItemHolder().getData(NVDataMaps.BLOOD_ORB_STATS);
        if (binding.isEmpty() || orb == null) return;

        if (getMainTank() > 0) {
            int available = Math.min(getMainTank(), (int) (orb.fillRate() * (1 + modifiers.getConsumptionMod())));
            int drained = AnimaHelper.getAnima(binding.uuid()).add(AnimaTicket.create(available), (int) (orb.capacity() * (1 + modifiers.getOrbCapacityMod())));
            setMainTank(getMainTank() - drained);
            if (drained > 0) {
                ((ServerLevel) level).sendParticles(ParticleTypes.WITCH, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 1, 0, 0, 0, 0.001);
            }
        }
    }

    public void addSacrificeEV(int evAdded, boolean isSacrifice) {
        setMainTank(getMainTank() + Math.min((getMainCapacity() - getMainTank()), (int) ((isSacrifice ? 1 + modifiers.getSacrificeMod() : 1 + modifiers.getSelfSacrificeMod()) * evAdded)));
        setChanged();
    }

    public void checkAction() {
        if (!isActive()) {
            setProgress(0);
        }

        ItemStack inputStack = inv.getStackInSlot(0);
        Binding inputBinding = inputStack.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
        Optional<RecipeHolder<com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe>> optionalHolder = level.getRecipeManager().getRecipeFor(NVRecipes.ARA_VITAE_TYPE.get(), new AraVitaeInput(inputStack, getTier()), level);
        if (!inputBinding.isEmpty()) {
            setCanFill(true);
            setActive(true);
            setCurrentRecipe(null);
            return;
        } else if (optionalHolder.isPresent()) {
            setCurrentRecipe(optionalHolder.get().value());
            setActive(true);
            setCanFill(false);
            level.playSound(null, worldPosition, NVSounds.BLOOD_ALTAR_CRAFT_START.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
            return;
        }
        setActive(false);
    }

    public int analogSignal() {
        if (level.getBlockState(getBlockPos().below()).is(NVTags.Blocks.ANIMA_COMPARATOR)) {
            ItemStack content = inv.getStackInSlot(0);
            Binding binding = content.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
            BloodOrb orb = content.getItemHolder().getData(NVDataMaps.BLOOD_ORB_STATS);
            if (binding.isEmpty() || orb == null) {
                return 0;
            }
            Anima network = AnimaHelper.getAnima(binding);
            if (network == null) {
                return 0;
            }
            float current = network.getCurrentEV();
            float max = (int) ((float) orb.capacity() * (1 + modifiers.getOrbCapacityMod()));
            return Mth.lerpDiscrete(current / max, 0, 15);
        }

        return Mth.lerpDiscrete((float) getMainTank() / (float) getMainCapacity(), 0, 15);
    }

    public int getMainCapacity() {
        return (int) ((float) FluidType.BUCKET_VOLUME * 10F * modifiers.getCapacityMod());
    }

    public int getIOCapacity() {
        return (int) ((float) FluidType.BUCKET_VOLUME * 1F * modifiers.getCapacityMod());
    }

    public int getChargingCapacity() {
        return (int) ((float) FluidType.BUCKET_VOLUME * modifiers.getChargeCapacityMod());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        CompoundTag stats = tag.getCompound("stats");
        ticks = stats.getInt("ticks");
        modifiers = new AltarRuneModifiers(
                stats.getFloat("capacity"),
                stats.getInt("tickrate"),
                stats.getFloat("consumption"),
                stats.getFloat("sacrifice"),
                stats.getFloat("selfsacrifice"),
                stats.getFloat("dislocation"),
                stats.getFloat("orb"),
                stats.getFloat("chargeamount"),
                stats.getFloat("chargecap"),
                stats.getFloat("efficiency")
        );

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
        stats.putInt("tickrate", modifiers.getTickRate());
        stats.putInt("ticks", ticks % 2048);
        stats.putFloat("capacity", modifiers.getCapacityMod());
        stats.putFloat("consumption", modifiers.getConsumptionMod());
        stats.putFloat("efficiency", modifiers.getEfficiencyMod());
        stats.putFloat("sacrifice", modifiers.getSacrificeMod());
        stats.putFloat("selfsacrifice", modifiers.getSelfSacrificeMod());
        stats.putFloat("dislocation", modifiers.getDislocationMod());
        stats.putFloat("orb", modifiers.getOrbCapacityMod());
        stats.putFloat("chargeamount", modifiers.getChargeAmountMod());
        stats.putFloat("chargecap", modifiers.getChargeCapacityMod());

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
            case 0 -> new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE, getMainTank());
            case 1 -> new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE, getInputTank());
            case 2 -> new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE, getOutputTank());
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
        return stack.is(NVTags.Fluids.ESSENTIA_VITAE);
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

        return new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE, toDrain);
    }

    public boolean isActive() { return isActive; }
    public boolean canFill() { return canFill; }
    public AraVitaeRecipe getCurrentRecipe() { return currentRecipe; }
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

    private void setSignaling(boolean signaling) { this.isSignaling = signaling; }
    private void incrementTicks() { this.ticks++; }
    private void decrementCapacityGraceTicks() { this.capacityGraceTicks--; }
    private void setMainTank(int mainTank) { this.mainTank = mainTank; }
    private void setInputTank(int inputTank) { this.inputTank = inputTank; }
    private void setOutputTank(int outputTank) { this.outputTank = outputTank; }
    private void setChargingTank(int chargingTank) { this.chargingTank = chargingTank; }
    private void setProgress(int progress) { this.progress = progress; }
    private void setCurrentRecipe(AraVitaeRecipe recipe) { this.currentRecipe = recipe; }
    private void setActive(boolean active) { this.isActive = active; }
    private void setCanFill(boolean canFill) { this.canFill = canFill; }
    private void setCooldownAfterCrafting(int cooldown) { this.cooldownAfterCrafting = cooldown; }
    private void setTier(int tier) { this.tier = tier; }

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
        return (int) (currentRecipe.getCraftSpeed() * (1 + modifiers.getConsumptionMod()));
    }

    @Override
    public int getDrainRate() {
        if (currentRecipe == null) {
            return 0;
        }
        return (int) (currentRecipe.getDrainSpeed() * (1 + modifiers.getEfficiencyMod()));
    }

    @Override
    public ItemStack getStackInSlot() {
        return inv.getStackInSlot(0);
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return this;
    }

    private void scanAndRecalculate() {
        int newTier = AltarUtil.getTier(level, worldPosition);
        setTier(newTier);

        AltarScanResult scanResult = AltarUtil.scanForRunes(newTier, level, worldPosition);
        Map<IAltarRuneType, Integer> allRunes = new HashMap<>(scanResult.runeCounts());
        List<RuneInstance> runeInstances = scanResult.runeInstances();

        AltarRuneEvent.GatherRunes gatherEvent = new AltarRuneEvent.GatherRunes(
                this, level, worldPosition, newTier, allRunes, runeInstances
        );
        NeoForge.EVENT_BUS.post(gatherEvent);

        calculateStats(allRunes, runeInstances);
    }

    @Override
    public void checkTier() {
        if (level != null && !level.isClientSide) {
            scanAndRecalculate();
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
        return (int) modifiers.getChargeAmountMod();
    }

    @Override
    public int getChargingFrequency() {
        return modifiers.getTickRate();
    }

    @Override
    public float getBonusCapacity() {
        return modifiers.getCapacityMod();
    }

    @Override
    public float getEfficiency() {
        return modifiers.getEfficiencyMod();
    }

    @Override
    public float getSelfSacrificeBonus() {
        return modifiers.getSelfSacrificeMod();
    }

    @Override
    public float getSacrificeBonus() {
        return modifiers.getSacrificeMod();
    }

    @Override
    public float getSpeedBonus() {
        return modifiers.getConsumptionMod();
    }

    @Override
    public float getDislocationBonus() {
        return modifiers.getDislocationMod();
    }

    @Override
    public float getOrbCapacityBonus() {
        return modifiers.getOrbCapacityMod();
    }

    @Override
    public int getTickRate() {
        return modifiers.getTickRate();
    }
}
