package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
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
import com.breakinblocks.neovitae.common.item.OrbFluidHandler;
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
import com.breakinblocks.neovitae.client.sound.LoopSoundManager;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.api.stream.StreamEffect;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.registry.AltarEffect;
import com.breakinblocks.neovitae.common.registry.AltarTier;
import com.breakinblocks.neovitae.common.structure.NVMultiblock;
import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.sounds.SoundSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AraVitaeTile extends BaseBlockEntity implements IAraVitae, GeoBlockEntity {

    private final AnimatableInstanceCache geoCache =
            GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation RITUAL_ANIM =
            RawAnimation.begin().thenPlay("animation.ara_vitae.ritual");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<AraVitaeTile>("main", 0, state -> PlayState.STOP)
                .triggerableAnim("ritual", RITUAL_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }


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

    public final Inv inv = new Inv();

    public class Inv extends ItemStacksResourceHandler {
        Inv() { super(1); }

        @Override
        protected int getCapacity(int index, ItemResource resource) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
        }

        public ItemStack getStackInSlot(int slot) {
            ItemResource r = getResource(slot);
            return r.isEmpty() ? ItemStack.EMPTY : r.toStack(getAmountAsInt(slot));
        }

        public void setStackInSlot(int slot, ItemStack stack) {
            set(slot, ItemResource.of(stack), stack.getCount());
        }
    }

    public final AltarFluidHandler fluidHandler = new AltarFluidHandler();

    public class AltarFluidHandler implements ResourceHandler<FluidResource> {
        private final TankJournal journal = new TankJournal();

        @Override public int size() { return 3; }

        @Override
        public FluidResource getResource(int index) {
            return switch (index) {
                case 0, 1, 2 -> tankAmountAt(index) > 0
                        ? FluidResource.of(NVFluids.ESSENTIA_VITAE_SOURCE.get())
                        : FluidResource.EMPTY;
                default -> FluidResource.EMPTY;
            };
        }

        @Override
        public long getAmountAsLong(int index) { return tankAmountAt(index); }

        @Override
        public long getCapacityAsLong(int index, FluidResource resource) {
            return switch (index) {
                case 0 -> getMainCapacity();
                case 1, 2 -> getIOCapacity();
                default -> 0;
            };
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            return resource.isEmpty() || resource.getFluid() == NVFluids.ESSENTIA_VITAE_SOURCE.get();
        }

        @Override
        public int insert(int index, FluidResource resource, int amount, TransactionContext tx) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            if (!isValid(index, resource) || index != 1) return 0;
            int space = Math.max(getIOCapacity() - inputTank, 0);
            int toFill = Math.min(space, amount);
            if (toFill <= 0) return 0;
            journal.updateSnapshots(tx);
            inputTank += toFill;
            return toFill;
        }

        @Override
        public int extract(int index, FluidResource resource, int amount, TransactionContext tx) {
            TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
            if (!isValid(index, resource) || index != 2) return 0;
            int toDrain = Math.min(outputTank, amount);
            if (toDrain <= 0) return 0;
            journal.updateSnapshots(tx);
            outputTank -= toDrain;
            return toDrain;
        }

        @Override
        public int insert(FluidResource resource, int amount, TransactionContext tx) {
            return insert(1, resource, amount, tx);
        }

        @Override
        public int extract(FluidResource resource, int amount, TransactionContext tx) {
            return extract(2, resource, amount, tx);
        }

        private int tankAmountAt(int index) {
            return switch (index) {
                case 0 -> mainTank;
                case 1 -> inputTank;
                case 2 -> outputTank;
                default -> 0;
            };
        }

        private class TankJournal extends SnapshotJournal<int[]> {
            @Override
            protected int[] createSnapshot() { return new int[]{mainTank, inputTank, outputTank}; }

            @Override
            protected void revertToSnapshot(int[] snapshot) {
                mainTank = snapshot[0];
                inputTank = snapshot[1];
                outputTank = snapshot[2];
            }

            @Override
            protected void onRootCommit(int[] originalState) {
                setChanged();
            }
        }
    }

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
        if (level.isClientSide()) {
            if (tile.isActive()) {
                LoopSoundManager.tryStartLoop(
                        NVSounds.BLOOD_ALTAR_AMBIENT.get(), 0.3f, level, pos,
                        be -> be instanceof AraVitaeTile ara && ara.isActive()
                );
            }
            return;
        }

        if (tile.isSignaling()) tile.setSignaling(false);
        tile.incrementTicks();
        if (tile.getCapacityGraceTicks() > 0) tile.decrementCapacityGraceTicks();

        if (tile.getTicks() % AltarConstants.STRUCTURE_CHECK_INTERVAL == 0) {
            tile.tickStructureCheck();
        }

        if (tile.getTicks() % Math.max(tile.modifiers.getTickRate(), 1) == 0) {
            tile.tickFluidTransfer();
        }

        // Ambient blood drip when altar has LP
        if (tile.getMainTank() > 0 && tile.getTicks() % 10 == 0) {
            ((ServerLevel) level).sendParticles(
                    new ColoredParticleOptions(NVParticles.BLOOD_DRIP.get(), 0x990011),
                    pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                    1, 0.3, 0.0, 0.3, 0);
        }

        if (!tile.isActive() && tile.getCooldownAfterCrafting() > 0) {
            tile.setCooldownAfterCrafting(tile.getCooldownAfterCrafting() - 1);
            if (tile.getCooldownAfterCrafting() <= 0) tile.checkAction();
            tile.tickTierEffects();
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
                ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xAA0000), worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 3, 0.3, 0.0, 0.3, 0.02);
            }
            tickTierEffects();
        } else if (!hasOperated && getProgress() > 0) {
            setProgress(getProgress() - (int) (getCurrentRecipe().getDrainSpeed() * (1 + modifiers.getEfficiencyMod())));
            if (getProgress() < 0) setProgress(0);
            if (getTicks() % AltarConstants.PARTICLE_FREQUENCY_SMOKE == 0) {
                ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0x330000), worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 1, 0.1, 1.0, 0.1, 0);
            }
            if (getProgress() <= 0) {
                ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0x330000), worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 8, 0.3, 0.2, 0.3, 0.01);
                StreamPresets.voidMark(worldPosition).build().sendToNearby((ServerLevel) level, worldPosition, 64);
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
        ItemStack result = recipe.assemble(recipeInput);
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
        triggerAnim("main", "ritual");
        setProgress(0);
        setCooldownAfterCrafting(AltarConstants.CRAFTING_COOLDOWN_TICKS);
        setActive(false);
        setCurrentRecipe(null);
        ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xAA0000), worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 15, 0.3, 0.5, 0.3, 0.05);
        StreamPresets.emberMote(worldPosition).color(0x990011).lifetime(30).scale(0.15f).build().sendToNearby((ServerLevel) level, worldPosition, 64);
        level.playSound(null, worldPosition, NVSounds.BLOOD_ALTAR_CRAFT_COMPLETE.get(), SoundSource.BLOCKS, 0.7f, 1.0f);

        NeoForge.EVENT_BUS.post(new AraVitaeCraftEvent.Crafted(this, recipe, inputStack, legacyEvent.getOutput()));
    }

    private void tickOrbFilling(ItemStack inputStack) {
        Binding binding = inputStack.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
        BloodOrb orb = inputStack.typeHolder().getData(NVDataMaps.BLOOD_ORB_STATS);
        if (binding.isEmpty() || orb == null) return;

        SimpleFluidContent orbFluid = inputStack.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
        int orbAmount = orbFluid.isEmpty() ? 0 : orbFluid.getAmount();

        if (orbAmount > 0) {
            int altarRoom = getMainCapacity() - getMainTank();

            if (altarRoom >= 1000) {
                int transferRate = (int) (orb.fillRate() * 10 * (1 + modifiers.getConsumptionMod()));
                int toDrain = Math.min(transferRate, Math.min(orbAmount, altarRoom));
                if (toDrain > 0) {
                    FluidStack drained = OrbFluidHandler.drainInternal(inputStack, toDrain, true);
                    setMainTank(getMainTank() + drained.getAmount());
                    if (!drained.isEmpty() && getTicks() % 2 == 0) {
                        double angle = (getTicks() * 0.15) % (Math.PI * 2);
                        double radius = 0.2;
                        double offsetX = Math.cos(angle) * radius;
                        double offsetZ = Math.sin(angle) * radius;
                        double cx = worldPosition.getX() + 0.5;
                        double cy = worldPosition.getY() + 1.0;
                        double cz = worldPosition.getZ() + 0.5;

                        ((ServerLevel) level).sendParticles(
                                new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0x881100),
                                cx + offsetX, cy + 0.3, cz + offsetZ,
                                0, -offsetX * 0.02, -0.06, -offsetZ * 0.02, 1);
                        ((ServerLevel) level).sendParticles(
                                new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0x664400),
                                cx - offsetX * 0.5, cy + 0.2, cz - offsetZ * 0.5,
                                0, offsetX * 0.01, -0.04, offsetZ * 0.01, 1);
                    }
                }
            } else {
                int networkFill = Math.min(orbAmount, (int) (orb.fillRate() * (1 + modifiers.getConsumptionMod())));
                FluidStack drained = OrbFluidHandler.drainInternal(inputStack, networkFill, true);
                if (!drained.isEmpty()) {
                    AnimaHelper.getAnima(binding.uuid()).add(AnimaTicket.create(drained.getAmount()),
                            (int) (orb.animaCapacity() * (1 + modifiers.getOrbCapacityMod())));
                }
            }
        } else if (getMainTank() > 0) {
            int available = Math.min(getMainTank(), (int) (orb.fillRate() * (1 + modifiers.getConsumptionMod())));
            int drained = AnimaHelper.getAnima(binding.uuid()).add(AnimaTicket.create(available), (int) (orb.animaCapacity() * (1 + modifiers.getOrbCapacityMod())));
            setMainTank(getMainTank() - drained);
            if (drained > 0 && getTicks() % 2 == 0) {
                double angle = (getTicks() * 0.15) % (Math.PI * 2);
                double radius = 0.2;
                double offsetX = Math.cos(angle) * radius;
                double offsetZ = Math.sin(angle) * radius;
                double cx = worldPosition.getX() + 0.5;
                double cy = worldPosition.getY() + 1.0;
                double cz = worldPosition.getZ() + 0.5;

                ((ServerLevel) level).sendParticles(
                        new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0x880022),
                        cx + offsetX, cy, cz + offsetZ,
                        0, offsetX * 0.02, 0.08, offsetZ * 0.02, 1);
                ((ServerLevel) level).sendParticles(
                        new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0x440066),
                        cx - offsetX * 0.5, cy + 0.1, cz - offsetZ * 0.5,
                        0, -offsetX * 0.01, 0.06, -offsetZ * 0.01, 1);
            }
            if (drained > 0 && getTicks() % 8 == 0) {
                ((ServerLevel) level).sendParticles(
                        new ColoredParticleOptions(NVParticles.BLOOD_BUBBLE.get(), 0x990011),
                        worldPosition.getX() + 0.5, worldPosition.getY() + 0.7, worldPosition.getZ() + 0.5,
                        1, 0.2, 0.0, 0.2, 0);
            }
        }
    }

    private static final int ORBIT_TICKS = 30;
    private static final int CYCLE_TICKS = 60;
    private static final int[] SPIRAL_STAGGER_OFFSETS = {0, 18, 37, 9};

    private void tickTierEffects() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        int currentTier = getTier();
        if (currentTier < 0 || currentTier >= NVMultiblock.TIER_LIST.length) return;
        AltarTier tierData =
                NVMultiblock.TIER_LIST[currentTier];
        if (tierData == null || tierData.effects().isEmpty()) return;

        int tick = getTicks();
        double ax = worldPosition.getX() + 0.5;
        double ay = worldPosition.getY() + 0.5;
        double az = worldPosition.getZ() + 0.5;

        for (AltarEffect effect : tierData.effects()) {
            runAltarEffect(serverLevel, effect, tick, ax, ay, az);
        }
    }

    private void runAltarEffect(ServerLevel serverLevel,
                                AltarEffect effect,
                                int tick, double ax, double ay, double az) {
        List<BlockPos> origins = effect.origins();
        if (origins.isEmpty()) return;
        int color = effect.color();
        switch (effect.type()) {
            case CAP_ORBIT_LIFE_PULSE ->
                    tickCapOrbitAndFire(serverLevel, tick, origins, color, 1.2, CYCLE_TICKS, ax, ay, az, true);
            case CAP_ORBIT_SPIRAL_STAGGERED -> {
                for (int i = 0; i < origins.size(); i++) {
                    int staggeredTick = tick + SPIRAL_STAGGER_OFFSETS[i % SPIRAL_STAGGER_OFFSETS.length];
                    tickSingleCapOrbitAndFire(serverLevel, staggeredTick, origins.get(i), color,
                            1.5, CYCLE_TICKS + 15, ax, ay, az);
                }
            }
            case CAP_BURST -> {
                if (tick % 5 != 0) return;
                for (BlockPos origin : origins) {
                    double cx = worldPosition.getX() + origin.getX() + 0.5;
                    double cy = worldPosition.getY() + origin.getY() + 0.5;
                    double cz = worldPosition.getZ() + origin.getZ() + 0.5;
                    serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), color),
                            cx, cy, cz, 1, 0.3, 0.3, 0.3, 0.01);
                }
            }
            case CAP_CRYSTAL_CASCADE -> tickCrystalCascade(serverLevel, tick, origins, color);
            case CAP_RENDER_HOVER_ARRAY -> {
                // Client-side renderer reads this entry directly; server emits nothing.
            }
        }
    }

    private void tickCapOrbitAndFire(ServerLevel serverLevel, int tick,
                                     List<BlockPos> caps, int color,
                                     double orbitRadius, int cyclePeriod, double ax, double ay, double az,
                                     boolean useLifePulse) {
        int phase = tick % cyclePeriod;

        if (phase < ORBIT_TICKS && tick % 2 == 0) {
            double angle = (phase / (double) ORBIT_TICKS) * Math.PI * 2 * (1 + (tick / cyclePeriod) % 3);
            for (BlockPos cap : caps) {
                double cx = worldPosition.getX() + cap.getX() + 0.5;
                double cy = worldPosition.getY() + cap.getY() + 0.5;
                double cz = worldPosition.getZ() + cap.getZ() + 0.5;
                double px = cx + Math.cos(angle) * orbitRadius;
                double pz = cz + Math.sin(angle) * orbitRadius;
                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), color),
                        px, cy, pz, 0, 0, 0.01, 0, 1);
                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), color),
                        px, cy, pz, 0, 0, 0, 0, 0);
            }
        }

        if (phase == ORBIT_TICKS) {
            for (BlockPos cap : caps) {
                double cx = worldPosition.getX() + cap.getX() + 0.5;
                double cy = worldPosition.getY() + cap.getY() + 0.5;
                double cz = worldPosition.getZ() + cap.getZ() + 0.5;
                if (useLifePulse) {
                    StreamPresets.lifePulse(worldPosition.offset(cap), worldPosition)
                            .scale(0.1f).build().sendToNearby(serverLevel, worldPosition, 128);
                } else {
                    StreamEffect.builder(cx, cy, cz)
                            .to(ax, ay, az)
                            .color(color).scale(0.2f).speed(2.5f).gravity(0.05f)
                            .approachHeight(0.5f)
                            .spiralInto(false).wobble(0.005f)
                            .alphaStart(1.0f).alphaEnd(1.0f)
                            .build().sendToNearby(serverLevel, worldPosition, 128);
                }
            }
        }
    }

    private void tickSingleCapOrbitAndFire(ServerLevel serverLevel, int tick, BlockPos cap, int color,
                                              double orbitRadius, int cyclePeriod, double ax, double ay, double az) {
        int phase = tick % cyclePeriod;
        double cx = worldPosition.getX() + cap.getX() + 0.5;
        double cy = worldPosition.getY() + cap.getY() + 0.5;
        double cz = worldPosition.getZ() + cap.getZ() + 0.5;

        if (phase < ORBIT_TICKS && tick % 3 == 0) {
            double angle = (phase / (double) ORBIT_TICKS) * Math.PI * 2 * (1 + (tick / cyclePeriod) % 3);
            double px = cx + Math.cos(angle) * orbitRadius;
            double pz = cz + Math.sin(angle) * orbitRadius;
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), color),
                    px, cy, pz, 0, 0, 0.01, 0, 1);
        }

        if (phase == ORBIT_TICKS) {
            float scale = 0.12f + (float) (Math.sin(tick * 0.1) * 0.05);
            float speed = 1.8f + (float) (Math.sin(tick * 0.07) * 0.5);
            StreamEffect.builder(cx, cy, cz)
                    .to(ax, ay, az)
                    .color(color).scale(scale).speed(speed).gravity(0.08f)
                    .approachHeight(0.3f)
                    .spiralInto(true).spiralRadius(0.15f).spiralSpeed(0.2f)
                    .wobble(0.01f)
                    .alphaStart(0.5f).alphaEnd(0.85f)
                    .build().sendToNearby(serverLevel, worldPosition, 128);
        }
    }

    private void tickCrystalCascade(ServerLevel serverLevel, int tick,
                                    List<BlockPos> origins, int color) {
        for (BlockPos cap : origins) {
            double cx = worldPosition.getX() + cap.getX() + 0.5;
            double topY = worldPosition.getY() + cap.getY() + 1.5;
            double cz = worldPosition.getZ() + cap.getZ() + 0.5;
            double circleRadius = 1.2;

            if (tick % 2 == 0) {
                double angle = serverLevel.getRandom().nextDouble() * Math.PI * 2;
                double r = circleRadius + (serverLevel.getRandom().nextDouble() - 0.5) * 0.3;
                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), color),
                        cx + Math.cos(angle) * r, topY, cz + Math.sin(angle) * r, 0, 0, -0.12, 0, 1);
            }

            if (tick % 3 == 0) {
                double angle = serverLevel.getRandom().nextDouble() * Math.PI * 2;
                double r = circleRadius * 0.8 + (serverLevel.getRandom().nextDouble() - 0.5) * 0.2;
                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), color),
                        cx + Math.cos(angle) * r, topY, cz + Math.sin(angle) * r, 0, 0, -0.1, 0, 1);
            }

            if (tick % 4 == 0) {
                double angle = serverLevel.getRandom().nextDouble() * Math.PI * 2;
                double r = circleRadius * 0.6;
                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), color),
                        cx + Math.cos(angle) * r, topY - 0.5, cz + Math.sin(angle) * r, 1, 0.15, 0.3, 0.15, 0);
            }

            if (tick % 6 == 0) {
                double angle = serverLevel.getRandom().nextDouble() * Math.PI * 2;
                double r = circleRadius;
                serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_DRIP.get(), color),
                        cx + Math.cos(angle) * r, topY, cz + Math.sin(angle) * r, 0, 0, -0.08, 0, 1);
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
        Optional<RecipeHolder<AraVitaeRecipe>> optionalHolder = level instanceof ServerLevel serverLevel
                ? serverLevel.recipeAccess().getRecipeFor(NVRecipes.ARA_VITAE_TYPE.get(), new AraVitaeInput(inputStack, getTier()), serverLevel)
                : Optional.empty();
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
            BloodOrb orb = content.typeHolder().getData(NVDataMaps.BLOOD_ORB_STATS);
            if (binding.isEmpty() || orb == null) {
                return 0;
            }
            Anima network = AnimaHelper.getAnima(binding);
            if (network == null) {
                return 0;
            }
            float current = network.getCurrentEV();
            float max = (int) ((float) orb.animaCapacity() * (1 + modifiers.getOrbCapacityMod()));
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
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        CompoundTag stats = tag.read("stats", CompoundTag.CODEC).orElseGet(CompoundTag::new);
        ticks = stats.getIntOr("ticks", 0);
        modifiers = new AltarRuneModifiers(
                stats.getFloatOr("capacity", 0f),
                stats.getIntOr("tickrate", 0),
                stats.getFloatOr("consumption", 0f),
                stats.getFloatOr("sacrifice", 0f),
                stats.getFloatOr("selfsacrifice", 0f),
                stats.getFloatOr("dislocation", 0f),
                stats.getFloatOr("orb", 0f),
                stats.getFloatOr("chargeamount", 0f),
                stats.getFloatOr("chargecap", 0f),
                stats.getFloatOr("efficiency", 0f)
        );

        CompoundTag tanks = tag.read("tanks", CompoundTag.CODEC).orElseGet(CompoundTag::new);

        inputTank = tanks.getIntOr("input", 0);
        outputTank = tanks.getIntOr("output", 0);
        mainTank = tanks.getIntOr("main", 0);
        chargingTank = tanks.getIntOr("charging", 0);
        progress = tanks.getIntOr("progress", 0);

        tag.child("inventory").ifPresent(inv::deserialize);

        this.isSignaling = tag.getBooleanOr("signal", false);
        this.isActive = tag.getBooleanOr("active", false);
        this.cooldownAfterCrafting = tag.getIntOr("craftCooldown", 0);

        this.tier = tag.getIntOr("tier", 0);
        this.capacityGraceTicks = tag.getIntOr("capacityGrace", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
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

        inv.serialize(tag.child("inventory"));

        tag.store("tanks", CompoundTag.CODEC, tanks);
        tag.store("stats", CompoundTag.CODEC, stats);
        tag.putInt("tier", this.tier);
        tag.putBoolean("signal", isSignaling);
        tag.putBoolean("active", isActive);
        tag.putInt("craftCooldown", cooldownAfterCrafting);
        tag.putInt("capacityGrace", capacityGraceTicks);
    }

    public boolean isActive() { return isActive; }
    public boolean isVisuallyActive() { return isActive || cooldownAfterCrafting > 0; }
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

    public void drainMainTank(int amount) {
        if (amount <= 0) return;
        setMainTank(Math.max(0, getMainTank() - amount));
        setChanged();
    }

    public void drainChargingTank(int amount) {
        if (amount <= 0) return;
        setChargingTank(Math.max(0, getChargingTank() - amount));
        setChanged();
    }

    private void setSignaling(boolean signaling) { this.isSignaling = signaling; }
    private void incrementTicks() { this.ticks++; }
    private void decrementCapacityGraceTicks() { this.capacityGraceTicks--; }
    private void setMainTank(int mainTank) { this.mainTank = mainTank; }
    private void setInputTank(int inputTank) { this.inputTank = inputTank; }
    private void setOutputTank(int outputTank) { this.outputTank = outputTank; }
    private void setChargingTank(int chargingTank) { this.chargingTank = chargingTank; }
    private void setProgress(int progress) { this.progress = progress; }
    private void setCurrentRecipe(AraVitaeRecipe recipe) { this.currentRecipe = recipe; }
    public void setActive(boolean active) { this.isActive = active; }
    private void setCanFill(boolean canFill) { this.canFill = canFill; }
    public void setCooldownAfterCrafting(int cooldown) { this.cooldownAfterCrafting = cooldown; }
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
    public ResourceHandler<FluidResource> getFluidHandler() {
        return fluidHandler;
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
        if (level != null && !level.isClientSide()) {
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

    public AABB getRenderBoundingBox() {
        return new AABB(
                worldPosition.getX() - 64, worldPosition.getY() - 64, worldPosition.getZ() - 64,
                worldPosition.getX() + 64, worldPosition.getY() + 320, worldPosition.getZ() + 64);
    }

    private static final int LINK_STALE_TICKS = 12;
    private final Map<BlockPos, LinkEntry> linkRegistry = new HashMap<>();

    private record LinkEntry(int craftTier, boolean wants, long lastSeenTick) {}

    public void reportLink(BlockPos pos, int craftTier, boolean wants) {
        linkRegistry.put(pos.immutable(), new LinkEntry(craftTier, wants, ticks));
    }

    public void unregisterLink(BlockPos pos) {
        linkRegistry.remove(pos);
    }

    public boolean grantsCraftTo(BlockPos requester) {
        if (isActive()) return false;
        linkRegistry.entrySet().removeIf(e -> ticks - e.getValue().lastSeenTick() > LINK_STALE_TICKS);
        BlockPos winner = null;
        int bestTier = Integer.MIN_VALUE;
        for (Map.Entry<BlockPos, LinkEntry> e : linkRegistry.entrySet()) {
            LinkEntry v = e.getValue();
            if (!v.wants()) continue;
            if (v.craftTier() > bestTier
                    || (v.craftTier() == bestTier && (winner == null || e.getKey().asLong() < winner.asLong()))) {
                bestTier = v.craftTier();
                winner = e.getKey();
            }
        }
        return requester.equals(winner);
    }
}
