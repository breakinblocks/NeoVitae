package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.client.sound.LoopSoundManager;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.SanguineCasingBlock;
import com.breakinblocks.neovitae.common.block.SanguineCrafterBlock;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.event.NeoVitaeCraftedEvent;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.athanor.IAthanorTool;
import com.breakinblocks.neovitae.common.menu.GhostItemHandler;
import com.breakinblocks.neovitae.common.menu.SanguineCrafterMenu;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeInput;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeInput;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeRecipe;
import com.breakinblocks.neovitae.common.sideconfig.OutputFaceConfigurable;
import com.breakinblocks.neovitae.common.sideconfig.OutputPusher;
import com.breakinblocks.neovitae.common.sideconfig.SideConfigItemHandler;
import com.breakinblocks.neovitae.common.sideconfig.SideConfigurable;
import com.breakinblocks.neovitae.common.sideconfig.SlotSideConfig;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.common.world.SanguineCrafterChunkLoader;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SanguineCrafterBlockEntity extends BaseBlockEntity implements MenuProvider, SideConfigurable, OutputFaceConfigurable, IRitualAccelerated {

    public enum Mode {
        AUTO, CRAFTING, TABULA, FORGE, PACKING, UNPACKING;

        private static final Mode[] VALUES = values();

        public static Mode byIndex(int index) {
            return index >= 0 && index < VALUES.length ? VALUES[index] : AUTO;
        }

        public Mode next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }
    }

    public enum RedstoneMode {
        IGNORED, HIGH, LOW;

        private static final RedstoneMode[] VALUES = values();

        public static RedstoneMode byIndex(int index) {
            return index >= 0 && index < VALUES.length ? VALUES[index] : IGNORED;
        }

        public RedstoneMode next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }
    }

    public enum Status {
        NOT_FORMED, NO_OWNER, DISABLED, IDLE, NO_TEMPLATE, NO_RECIPE, MISSING_INGREDIENTS, NO_ORB, TIER_TOO_LOW,
        NOT_ENOUGH_EV, NOT_ENOUGH_SPIRITUS, OUTPUT_FULL, LIMIT_REACHED, CRAFTING;

        private static final Status[] VALUES = values();

        public static Status byIndex(int index) {
            return index >= 0 && index < VALUES.length ? VALUES[index] : IDLE;
        }
    }

    public enum ChunkLoading {
        DISABLED, INACTIVE, ACTIVE, OWNER_OFFLINE, LIMIT_REACHED, TURNED_OFF;

        private static final ChunkLoading[] VALUES = values();

        public static ChunkLoading byIndex(int index) {
            return index >= 0 && index < VALUES.length ? VALUES[index] : DISABLED;
        }
    }

    public static final int INPUT_START = 0;
    public static final int INPUT_COUNT = 18;
    public static final int OUTPUT_START = 18;
    public static final int OUTPUT_COUNT = 9;
    public static final int ORB_SLOT = 27;
    public static final int GEM_SLOT = 28;
    public static final int SLOT_COUNT = 29;
    public static final int TEMPLATE_SIZE = 9;

    public static final int GROUP_INPUT = 0;
    public static final int GROUP_OUTPUT = 1;
    public static final int GROUP_ORB = 2;
    public static final int GROUP_GEM = 3;
    public static final int GROUP_COUNT = 4;

    private static final int STRUCTURE_CHECK_INTERVAL = 20;
    private static final int ACTIVE_HOLD_TICKS = 20;

    private static final boolean[][] DEFAULT_SIDE_CONFIG = new boolean[][] {
            { false, true, true, true, true, true },
            { true, false, false, false, false, false },
            { false, false, false, false, false, false },
            { false, false, false, false, false, false }
    };

    public final Inv inv = new Inv();
    public final GhostItemHandler template = new GhostItemHandler(TEMPLATE_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            cachedCraftingRecipe = null;
            setChanged();
        }
    };

    private final SlotSideConfig sideConfig = new SlotSideConfig(GROUP_COUNT, DEFAULT_SIDE_CONFIG);

    @Nullable
    private UUID owner;
    private String ownerName = "";
    private Mode mode = Mode.AUTO;
    private RedstoneMode redstoneMode = RedstoneMode.IGNORED;
    private int outputLimit = 0;
    private boolean chunkLoadingEnabled = true;
    @Nullable
    private Direction outputFace = null;
    private boolean formed = false;
    private int hellforgedCasings = 0;
    private int swiftCasings = 0;
    private int progress = 0;
    private final RitualAcceleration ritualAcceleration = new RitualAcceleration();
    private int craftTime = 0;
    private Status status = Status.NOT_FORMED;
    private ChunkLoading chunkLoading = ChunkLoading.DISABLED;
    private int structureTimer = 0;
    private int activeTicks = 0;
    @Nullable
    private RecipeHolder<CraftingRecipe> cachedCraftingRecipe;
    private final List<ItemStack> displaced = new ArrayList<>();

    public int activeGroup = -1;

    public class Inv extends ItemStackHandler {
        Inv() { super(SLOT_COUNT); }

        @Override
        public boolean isItemValid(int index, ItemStack stack) {
            if (stack.isEmpty()) return true;
            if (index >= OUTPUT_START && index < OUTPUT_START + OUTPUT_COUNT) return false;
            return switch (index) {
                case ORB_SLOT -> stack.getItem() instanceof BloodOrbItem;
                case GEM_SLOT -> stack.has(NVDataComponents.SPIRITUS_AMOUNT);
                default -> true;
            };
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider registries, CompoundTag nbt) {
            super.deserializeNBT(registries, nbt);
            if (stacks.size() <= SLOT_COUNT) return;
            List<ItemStack> loaded = new ArrayList<>(stacks);
            setSize(SLOT_COUNT);
            for (int i = 0; i < loaded.size(); i++) {
                ItemStack stack = loaded.get(i);
                if (i < SLOT_COUNT) stacks.set(i, stack);
                else if (!stack.isEmpty()) displaced.add(stack);
            }
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChangedNoSync();
        }
    }

    private record Consume(int slot, boolean cuttingFluid) {}

    private record Plan(ItemStack result, List<ItemStack> byproducts, List<Consume> consumes,
                        int recipeEv, @Nullable Anima recipeNetwork, double gemDrain, boolean craftingGem,
                        @Nullable Mode station, int ticks, @Nullable ItemStack[] eventInputs) {}

    public SanguineCrafterBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.SANGUINE_CRAFTER_TYPE.get(), pos, state);
    }

    public static int groupOf(int slot) {
        if (slot >= INPUT_START && slot < INPUT_START + INPUT_COUNT) return GROUP_INPUT;
        if (slot >= OUTPUT_START && slot < OUTPUT_START + OUTPUT_COUNT) return GROUP_OUTPUT;
        return switch (slot) {
            case ORB_SLOT -> GROUP_ORB;
            default -> GROUP_GEM;
        };
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SanguineCrafterBlockEntity tile) {
        if (level instanceof ServerLevel serverLevel) {
            tile.serverTick(serverLevel);
            tile.updateActive(serverLevel);
        } else if (state.getValue(SanguineCrafterBlock.ACTIVE)) {
            LoopSoundManager.tryStartLoop(NVSounds.SANGUINE_CRAFTER_WORKING.get(), 0.35f, level, pos,
                    be -> be.getBlockState().is(NVBlocks.SANGUINE_CRAFTER.block().get())
                            && be.getBlockState().getValue(SanguineCrafterBlock.ACTIVE));
        }
    }

    private void updateActive(ServerLevel level) {
        if (status == Status.CRAFTING) {
            activeTicks = ACTIVE_HOLD_TICKS;
        } else if (activeTicks > 0) {
            activeTicks--;
        }
        boolean active = formed && activeTicks > 0;
        BlockState state = getBlockState();
        if (state.getValue(SanguineCrafterBlock.ACTIVE) != active) {
            level.setBlock(worldPosition, state.setValue(SanguineCrafterBlock.ACTIVE, active), Block.UPDATE_CLIENTS);
            if (active) {
                level.playSound(null, worldPosition, NVSounds.TABULA_VITAE_ACTIVATE.get(), SoundSource.BLOCKS, 0.3f, 0.8f);
            }
        }
    }

    private void serverTick(ServerLevel level) {
        if (!displaced.isEmpty()) {
            displaced.forEach(this::insertOutput);
            displaced.clear();
            setChanged();
        }

        if (--structureTimer <= 0) {
            structureTimer = STRUCTURE_CHECK_INTERVAL;
            checkStructure(level);
            chunkLoading = SanguineCrafterChunkLoader.update(level, this, formed && owner != null && redstoneAllows(level), chunkLoadingEnabled);
        }

        if (formed && OutputPusher.shouldPush(level)) {
            pushOutputs(level);
        }

        if (!formed) {
            idle(Status.NOT_FORMED);
            return;
        }
        if (owner == null) {
            idle(Status.NO_OWNER);
            return;
        }
        if (!redstoneAllows(level)) {
            idle(Status.DISABLED);
            return;
        }

        ItemStack gem = inv.getStackInSlot(GEM_SLOT);
        if (HellfireForgeBlockEntity.absorbIntoGem(level, worldPosition, gem)) {
            inv.setStackInSlot(GEM_SLOT, gem);
        }

        if (progress == 0) {
            Plan plan = plan(level);
            if (plan == null) return;
            craftTime = plan.ticks();
            status = Status.CRAFTING;
        }

        progress += ritualAcceleration.factor(level);
        ritualAcceleration.markWorked(level);
        if (level.getGameTime() % 5 == 0) {
            BlockPos center = getCenter();
            level.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xAA0000),
                    center.getX() + 0.5, center.getY() + 1.1, center.getZ() + 0.5, 2, 0.25, 0.05, 0.25, 0.01);
        }
        if (progress < craftTime) {
            setChangedNoSync();
            return;
        }

        int rounds = Math.max(1, progress / Math.max(1, craftTime));
        progress = 0;
        int crafted = 0;
        int parallel = getParallel() * rounds;
        for (int i = 0; i < parallel; i++) {
            Plan plan = plan(level);
            if (plan == null) break;
            execute(level, plan);
            crafted++;
        }
        if (crafted > 0) {
            BlockPos center = getCenter();
            level.playSound(null, worldPosition, NVSounds.TABULA_VITAE_COMPLETE.get(), SoundSource.BLOCKS, 0.2f, 1.0f);
            level.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xAA0000),
                    center.getX() + 0.5, center.getY() + 1.1, center.getZ() + 0.5, 8, 0.3, 0.1, 0.3, 0);
        }
        setChangedNoSync();
    }

    private void idle(Status reason) {
        status = reason;
        progress = 0;
    }

    private boolean redstoneAllows(Level level) {
        return switch (redstoneMode) {
            case IGNORED -> true;
            case HIGH -> level.hasNeighborSignal(worldPosition);
            case LOW -> !level.hasNeighborSignal(worldPosition);
        };
    }

    public BlockPos getCenter() {
        return worldPosition.relative(getBlockState().getValue(SanguineCrafterBlock.FACING).getOpposite());
    }

    public boolean isFormed() {
        return formed;
    }

    public void checkStructure(Level level) {
        BlockPos center = getCenter();
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dz = -1; dz <= 1; dz += 2) {
                if (!level.isLoaded(center.offset(dx, 0, dz))) return;
            }
        }
        boolean ok = true;
        int hellforged = 0;
        int swift = 0;
        for (BlockPos pos : structureParts(center)) {
            BlockState state = level.getBlockState(pos);
            if (!state.is(NVTags.Blocks.SANGUINE_CASINGS)) {
                ok = false;
                break;
            }
            if (state.is(NVBlocks.HELLFORGED_SANGUINE_CASING.block().get())) hellforged++;
            else if (state.is(NVBlocks.SWIFT_SANGUINE_CASING.block().get())) swift++;
        }
        hellforgedCasings = ok ? hellforged : 0;
        swiftCasings = ok ? swift : 0;
        if (ok != formed) {
            formed = ok;
            BlockState state = getBlockState();
            if (state.getValue(SanguineCrafterBlock.FORMED) != ok) {
                level.setBlock(worldPosition, state.setValue(SanguineCrafterBlock.FORMED, ok), 3);
            }
            if (!ok) {
                updateCasingStates(level, center, false);
            }
            invalidateStructureCapabilities(level, center);
            setChanged();
        }
        if (formed) {
            updateCasingStates(level, center, true);
        }
    }

    private List<BlockPos> structureParts(BlockPos center) {
        List<BlockPos> parts = new ArrayList<>(17);
        for (int dy = -1; dy <= 0; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (!pos.equals(center) && !pos.equals(worldPosition)) {
                        parts.add(pos);
                    }
                }
            }
        }
        return parts;
    }

    private static boolean inStructure(BlockPos center, BlockPos pos) {
        int dy = pos.getY() - center.getY();
        return Math.abs(pos.getX() - center.getX()) <= 1 && Math.abs(pos.getZ() - center.getZ()) <= 1 && dy >= -1 && dy <= 0;
    }

    private void updateCasingStates(Level level, BlockPos center, boolean form) {
        for (BlockPos pos : structureParts(center)) {
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof SanguineCasingBlock)) continue;
            BlockState target = form
                    ? SanguineCasingBlock.formedAt(state, pos.getX() - center.getX() + 1, pos.getY() - center.getY() + 1, pos.getZ() - center.getZ() + 1)
                    : SanguineCasingBlock.unformed(state);
            if (target != state) {
                level.setBlock(pos, target, Block.UPDATE_CLIENTS);
            }
        }
    }

    private void invalidateStructureCapabilities(Level level, BlockPos center) {
        for (int dy = -1; dy <= 0; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    level.invalidateCapabilities(center.offset(dx, dy, dz));
                }
            }
        }
    }

    public int getParallel() {
        return Math.max(1, Math.min(1 + hellforgedCasings, NeoVitae.SERVER_CONFIG.SANGUINE_CRAFTER_MAX_PARALLEL.get()));
    }

    public double getSpeedMultiplier() {
        return NeoVitae.SERVER_CONFIG.SANGUINE_CRAFTER_BASE_SPEED.get()
                * (1.0 + swiftCasings * NeoVitae.SERVER_CONFIG.SANGUINE_CRAFTER_SPEED_PER_SWIFT_CASING.get());
    }

    public double getEffectiveSpeedMultiplier() {
        return getSpeedMultiplier() * ritualAcceleration.factor(level);
    }

    @Override
    public RitualAcceleration getRitualAcceleration() {
        return ritualAcceleration;
    }

    private int scaledTicks(int baseTicks) {
        return Math.max(1, (int) Math.round(baseTicks / getSpeedMultiplier()));
    }

    @Nullable
    private Plan plan(ServerLevel level) {
        return switch (mode) {
            case AUTO -> planAuto(level);
            case CRAFTING -> planCrafting(level);
            case TABULA -> planTabula(level, templateConsumes());
            case FORGE -> planForge(level, templateConsumes());
            case PACKING -> planPacking(level, false);
            case UNPACKING -> planPacking(level, true);
        };
    }

    private boolean templateEmpty() {
        for (int i = 0; i < TEMPLATE_SIZE; i++) {
            if (!template.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Nullable
    private List<Consume> templateConsumes() {
        if (templateEmpty()) {
            status = Status.NO_TEMPLATE;
            return null;
        }
        int[] reserved = new int[INPUT_COUNT];
        List<Consume> consumes = new ArrayList<>();
        for (int i = 0; i < TEMPLATE_SIZE; i++) {
            ItemStack wanted = template.getStackInSlot(i);
            if (wanted.isEmpty()) continue;
            int found = -1;
            for (int s = 0; s < INPUT_COUNT; s++) {
                ItemStack have = inv.getStackInSlot(INPUT_START + s);
                if (have.getCount() - reserved[s] > 0 && ItemStack.isSameItemSameComponents(have, wanted)) {
                    found = s;
                    break;
                }
            }
            if (found < 0) {
                status = Status.MISSING_INGREDIENTS;
                return null;
            }
            reserved[found]++;
            consumes.add(new Consume(INPUT_START + found, inv.getStackInSlot(INPUT_START + found).is(NVTags.Items.CUTTING_FLUIDS)));
        }
        return consumes;
    }

    private List<Consume> bufferConsumes() {
        List<Consume> consumes = new ArrayList<>();
        for (int s = 0; s < INPUT_COUNT; s++) {
            ItemStack have = inv.getStackInSlot(INPUT_START + s);
            if (!have.isEmpty()) {
                consumes.add(new Consume(INPUT_START + s, have.is(NVTags.Items.CUTTING_FLUIDS)));
            }
        }
        return consumes;
    }

    private List<ItemStack> itemsOf(List<Consume> consumes) {
        List<ItemStack> items = new ArrayList<>(consumes.size());
        for (Consume c : consumes) {
            items.add(inv.getStackInSlot(c.slot()).copyWithCount(1));
        }
        return items;
    }

    @Nullable
    private Plan planAuto(ServerLevel level) {
        List<Consume> slots = bufferConsumes();
        if (slots.isEmpty()) {
            status = Status.IDLE;
            return null;
        }
        Plan plan = planAutoFor(level, slots);
        if (plan != null || slots.size() >= TabulaVitaeRecipe.MAX_INPUTS) return plan;
        Status reason = status;
        int[] caps = new int[slots.size()];
        boolean stacked = false;
        for (int i = 0; i < caps.length; i++) {
            Consume c = slots.get(i);
            caps[i] = c.cuttingFluid() ? 1 : Math.min(inv.getStackInSlot(c.slot()).getCount(), TabulaVitaeRecipe.MAX_INPUTS);
            if (caps[i] > 1) stacked = true;
        }
        if (!stacked) return null;
        for (int total = slots.size() + 1; total <= TabulaVitaeRecipe.MAX_INPUTS; total++) {
            for (int[] counts : distributions(caps, total)) {
                List<Consume> consumes = new ArrayList<>(total);
                for (int i = 0; i < counts.length; i++) {
                    for (int n = 0; n < counts[i]; n++) consumes.add(slots.get(i));
                }
                plan = planAutoFor(level, consumes);
                if (plan != null) return plan;
                if (reason == Status.NO_RECIPE) reason = status;
            }
        }
        status = reason;
        return null;
    }

    private static List<int[]> distributions(int[] caps, int total) {
        List<int[]> out = new ArrayList<>();
        distribute(caps, 0, total, new int[caps.length], out);
        return out;
    }

    private static void distribute(int[] caps, int index, int left, int[] current, List<int[]> out) {
        int remainingSlots = caps.length - index;
        if (remainingSlots == 0) {
            if (left == 0) out.add(current.clone());
            return;
        }
        int maxHere = Math.min(caps[index], left - (remainingSlots - 1));
        for (int n = 1; n <= maxHere; n++) {
            current[index] = n;
            distribute(caps, index + 1, left - n, current, out);
        }
    }

    @Nullable
    private Plan planAutoFor(ServerLevel level, List<Consume> consumes) {
        Status forgeStatus = null;
        if (consumes.size() <= 4) {
            Plan plan = planForge(level, consumes);
            if (plan != null) return plan;
            forgeStatus = status;
        }
        if (consumes.size() <= TabulaVitaeRecipe.MAX_INPUTS) {
            Plan plan = planTabula(level, consumes);
            if (plan != null) return plan;
            if (status == Status.NO_RECIPE && forgeStatus != null) status = forgeStatus;
            return null;
        }
        if (forgeStatus == null) status = Status.NO_RECIPE;
        return null;
    }

    @Nullable
    private Plan planCrafting(ServerLevel level) {
        if (templateEmpty()) {
            status = Status.NO_TEMPLATE;
            return null;
        }
        List<Consume> consumes = templateConsumes();
        if (consumes == null) return null;
        NonNullList<ItemStack> grid = NonNullList.withSize(TEMPLATE_SIZE, ItemStack.EMPTY);
        int next = 0;
        for (int i = 0; i < TEMPLATE_SIZE; i++) {
            if (template.getStackInSlot(i).isEmpty()) continue;
            grid.set(i, inv.getStackInSlot(consumes.get(next++).slot()).copyWithCount(1));
        }
        CraftingInput input = CraftingInput.of(3, 3, grid);
        RecipeHolder<CraftingRecipe> holder = cachedCraftingRecipe;
        if (holder == null || !holder.value().matches(input, level)) {
            holder = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level).orElse(null);
            cachedCraftingRecipe = holder;
        }
        if (holder == null) {
            status = Status.NO_RECIPE;
            return null;
        }
        ItemStack result = holder.value().assemble(input, level.registryAccess());
        if (result.isEmpty()) {
            status = Status.NO_RECIPE;
            return null;
        }
        List<ItemStack> byproducts = new ArrayList<>();
        for (ItemStack remainder : holder.value().getRemainingItems(input)) {
            if (!remainder.isEmpty()) byproducts.add(remainder);
        }
        int ticks = scaledTicks(NeoVitae.SERVER_CONFIG.SANGUINE_CRAFTER_CRAFTING_TICKS.get());
        return finish(new Plan(result, byproducts, stripCuttingFluid(consumes), 0, null, 0, false, null, ticks, null));
    }

    // Both directions must be real, lossless crafting recipes. Never derive an output from tags or names.
    @Nullable
    private Plan planPacking(ServerLevel level, boolean unpack) {
        List<ItemStack> checked = new ArrayList<>();
        for (int slot = INPUT_START; slot < INPUT_START + INPUT_COUNT; slot++) {
            ItemStack item = inv.getStackInSlot(slot);
            if (item.isEmpty() || checked.stream().anyMatch(other -> ItemStack.isSameItemSameComponents(other, item))) continue;
            checked.add(item);
            for (int size = unpack ? 1 : 3; size >= (unpack ? 1 : 2); size--) {
                int count = size * size;
                List<Consume> consumes = new ArrayList<>();
                for (int s = INPUT_START; s < INPUT_START + INPUT_COUNT && consumes.size() < count; s++) {
                    ItemStack available = inv.getStackInSlot(s);
                    if (!ItemStack.isSameItemSameComponents(available, item)) continue;
                    for (int n = 0; n < available.getCount() && consumes.size() < count; n++) {
                        consumes.add(new Consume(s, false));
                    }
                }
                if (consumes.size() != count) continue;
                ItemStack result = packingResult(level, item, size);
                if (result.isEmpty()) continue;
                int reverseSize;
                if (unpack) {
                    reverseSize = result.getCount() == 9 ? 3 : result.getCount() == 4 ? 2 : 0;
                    if (reverseSize == 0) continue;
                } else {
                    if (result.getCount() != 1) continue;
                    reverseSize = 1;
                }
                ItemStack reverse = packingResult(level, result, reverseSize);
                if (reverse.getCount() != count || !ItemStack.isSameItemSameComponents(reverse, item)) continue;
                int ticks = scaledTicks(NeoVitae.SERVER_CONFIG.SANGUINE_CRAFTER_CRAFTING_TICKS.get());
                return finish(new Plan(result, List.of(), consumes, 0, null, 0, false, null, ticks, null));
            }
        }
        status = Status.NO_RECIPE;
        return null;
    }

    private static ItemStack packingResult(ServerLevel level, ItemStack item, int size) {
        List<ItemStack> grid = new ArrayList<>(size * size);
        for (int i = 0; i < size * size; i++) grid.add(item.copyWithCount(1));
        CraftingInput input = CraftingInput.of(size, size, grid);
        RecipeHolder<CraftingRecipe> holder = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level).orElse(null);
        if (holder == null) return ItemStack.EMPTY;
        // Container returns would make the pair non-lossless even if the main outputs match.
        for (ItemStack remainder : holder.value().getRemainingItems(input)) {
            if (!remainder.isEmpty()) return ItemStack.EMPTY;
        }
        return holder.value().assemble(input, level.registryAccess());
    }

    private static List<Consume> stripCuttingFluid(List<Consume> consumes) {
        List<Consume> out = new ArrayList<>(consumes.size());
        for (Consume c : consumes) out.add(new Consume(c.slot(), false));
        return out;
    }

    @Nullable
    private Plan planTabula(ServerLevel level, @Nullable List<Consume> consumes) {
        if (consumes == null) return null;
        if (consumes.size() > TabulaVitaeRecipe.MAX_INPUTS) {
            status = Status.NO_RECIPE;
            return null;
        }
        ItemStack orb = inv.getStackInSlot(ORB_SLOT);
        int orbTier = orb.getItem() instanceof BloodOrbItem orbItem ? orbItem.getOrbTier(orb) : 0;
        List<ItemStack> items = itemsOf(consumes);
        TabulaVitaeInput input = new TabulaVitaeInput(items, orbTier);
        Optional<TabulaVitaeRecipe> recipeOpt = level.getRecipeManager()
                .getRecipeFor(NVRecipes.TABULA_VITAE_TYPE.get(), input, level)
                .map(RecipeHolder::value);
        if (recipeOpt.isEmpty()) {
            status = Status.NO_RECIPE;
            return null;
        }
        TabulaVitaeRecipe recipe = recipeOpt.get();
        if (orbTier < recipe.getMinimumTier()) {
            status = orb.isEmpty() ? Status.NO_ORB : Status.TIER_TOO_LOW;
            return null;
        }
        for (ItemStack item : items) {
            if (item.is(NVTags.Items.CUTTING_FLUIDS) && IAthanorTool.readinessOf(item) != IAthanorTool.Readiness.READY) {
                status = Status.NOT_ENOUGH_EV;
                return null;
            }
        }
        Anima network = null;
        if (recipe.getSyphon() > 0) {
            Binding binding = orb.getItem() instanceof BloodOrbItem
                    ? orb.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY)
                    : Binding.EMPTY;
            if (binding.isEmpty()) {
                status = Status.NO_ORB;
                return null;
            }
            network = AnimaHelper.getAnima(binding);
            if (network == null) {
                status = Status.NO_ORB;
                return null;
            }
        }
        ItemStack result = recipe.assemble(input, level.registryAccess());
        if (result.isEmpty()) {
            status = Status.NO_RECIPE;
            return null;
        }
        List<ItemStack> byproducts = new ArrayList<>();
        for (Consume c : consumes) {
            if (c.cuttingFluid()) continue;
            ItemStack remainder = inv.getStackInSlot(c.slot()).getCraftingRemainingItem();
            if (!remainder.isEmpty()) byproducts.add(remainder);
        }
        int ticks = scaledTicks(Math.max(1, recipe.getTicks()));
        return finish(new Plan(result, byproducts, consumes, recipe.getSyphon(), network, 0, false, Mode.TABULA, ticks, items.toArray(new ItemStack[0])));
    }

    @Nullable
    private Plan planForge(ServerLevel level, @Nullable List<Consume> consumes) {
        if (consumes == null) return null;
        if (consumes.size() > 4) {
            status = Status.NO_RECIPE;
            return null;
        }
        List<ItemStack> items = itemsOf(consumes);
        ItemStack fuel = inv.getStackInSlot(GEM_SLOT);
        ForgeInput input = new ForgeInput(items, fuel, HellfireForgeBlockEntity.GEM_SLOT);
        Optional<ForgeRecipe> recipeOpt = findForge(level, input);
        boolean craftingGem = false;
        if (recipeOpt.isEmpty() || !recipeOpt.get().hasEnoughSpiritus(input)) {
            for (int i = 0; i < items.size(); i++) {
                if (!items.get(i).is(NVTags.Items.SPIRITUS_GEM)) continue;
                ForgeInput gemInput = new ForgeInput(items, items.get(i), i);
                Optional<ForgeRecipe> fallback = findForge(level, gemInput);
                if (fallback.isPresent() && fallback.get().hasEnoughSpiritus(gemInput)) {
                    input = gemInput;
                    recipeOpt = fallback;
                    craftingGem = true;
                }
                break;
            }
        }
        if (recipeOpt.isEmpty()) {
            status = Status.NO_RECIPE;
            return null;
        }
        ForgeRecipe recipe = recipeOpt.get();
        if (!recipe.hasEnoughSpiritus(input)) {
            status = Status.NOT_ENOUGH_SPIRITUS;
            return null;
        }
        ItemStack result = recipe.assemble(input, level.registryAccess());
        if (result.isEmpty()) {
            status = Status.NO_RECIPE;
            return null;
        }
        List<ItemStack> byproducts = new ArrayList<>();
        for (Consume c : consumes) {
            ItemStack remainder = inv.getStackInSlot(c.slot()).getCraftingRemainingItem();
            if (!remainder.isEmpty()) byproducts.add(remainder);
        }
        int ticks = scaledTicks(NeoVitae.SERVER_CONFIG.SANGUINE_CRAFTER_FORGE_TICKS.get());
        return finish(new Plan(result, byproducts, stripCuttingFluid(consumes), 0, null,
                craftingGem ? 0 : recipe.usedSpiritus, craftingGem, Mode.FORGE, ticks, input.asArray()));
    }

    private static Optional<ForgeRecipe> findForge(ServerLevel level, ForgeInput input) {
        return level.getRecipeManager().getRecipeFor(NVRecipes.HELLFIRE_FORGE_TYPE.get(), input, level).map(RecipeHolder::value);
    }

    @Nullable
    private Plan finish(Plan plan) {
        Anima ownerNetwork = owner == null ? null : AnimaHelper.getAnima(owner);
        int baseEv = NeoVitae.SERVER_CONFIG.SANGUINE_CRAFTER_EV_PER_CRAFT.get();
        if (baseEv > 0 && (ownerNetwork == null || ownerNetwork.getCurrentEV() < baseEv)) {
            status = Status.NOT_ENOUGH_EV;
            return null;
        }
        if (plan.recipeEv() > 0) {
            int needed = plan.recipeEv() + (plan.recipeNetwork() == ownerNetwork ? baseEv : 0);
            if (plan.recipeNetwork() == null || plan.recipeNetwork().getCurrentEV() < needed) {
                status = Status.NOT_ENOUGH_EV;
                return null;
            }
        }
        if (outputLimit > 0 && countInOutput(plan.result()) >= outputLimit) {
            status = Status.LIMIT_REACHED;
            return null;
        }
        List<ItemStack> produced = new ArrayList<>();
        produced.add(plan.result());
        produced.addAll(plan.byproducts());
        if (!fitsInOutput(produced)) {
            status = Status.OUTPUT_FULL;
            return null;
        }
        return plan;
    }

    private int countInOutput(ItemStack result) {
        int count = 0;
        for (int i = 0; i < OUTPUT_COUNT; i++) {
            ItemStack stack = inv.getStackInSlot(OUTPUT_START + i);
            if (ItemStack.isSameItemSameComponents(stack, result)) count += stack.getCount();
        }
        return count;
    }

    private boolean fitsInOutput(List<ItemStack> produced) {
        List<ItemStack> sim = new ArrayList<>(OUTPUT_COUNT);
        for (int i = 0; i < OUTPUT_COUNT; i++) sim.add(inv.getStackInSlot(OUTPUT_START + i).copy());
        for (ItemStack stack : produced) {
            int left = stack.getCount();
            for (int i = 0; i < OUTPUT_COUNT && left > 0; i++) {
                ItemStack slot = sim.get(i);
                if (!slot.isEmpty() && ItemStack.isSameItemSameComponents(slot, stack)) {
                    int move = Math.min(left, slot.getMaxStackSize() - slot.getCount());
                    if (move > 0) {
                        slot.grow(move);
                        left -= move;
                    }
                }
            }
            for (int i = 0; i < OUTPUT_COUNT && left > 0; i++) {
                if (sim.get(i).isEmpty()) {
                    int move = Math.min(left, stack.getMaxStackSize());
                    sim.set(i, stack.copyWithCount(move));
                    left -= move;
                }
            }
            if (left > 0) return false;
        }
        return true;
    }

    private void execute(ServerLevel level, Plan plan) {
        ItemStack result = plan.result();
        if (plan.eventInputs() != null) {
            NeoVitaeCraftedEvent event = plan.station() == Mode.TABULA
                    ? new NeoVitaeCraftedEvent.AlchemyTable(result, plan.eventInputs())
                    : new NeoVitaeCraftedEvent.Forge(result, plan.eventInputs());
            NeoForge.EVENT_BUS.post(event);
            result = event.getOutput();
        }

        for (Consume c : plan.consumes()) {
            ItemStack stack = inv.getStackInSlot(c.slot());
            if (c.cuttingFluid()) {
                damageCuttingFluid(c.slot(), stack);
            } else {
                stack.shrink(1);
                inv.setStackInSlot(c.slot(), stack);
            }
        }

        if (plan.gemDrain() > 0) {
            ItemStack gem = inv.getStackInSlot(GEM_SLOT);
            double left = gem.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D) - plan.gemDrain();
            if (left <= 0 && gem.is(NVItems.RAW_SPIRITUS)) {
                inv.setStackInSlot(GEM_SLOT, ItemStack.EMPTY);
            } else {
                gem.set(NVDataComponents.SPIRITUS_AMOUNT, Math.max(0, left));
                inv.setStackInSlot(GEM_SLOT, gem);
            }
        }

        int baseEv = NeoVitae.SERVER_CONFIG.SANGUINE_CRAFTER_EV_PER_CRAFT.get();
        if (baseEv > 0 && owner != null) {
            AnimaHelper.getAnima(owner).syphon(AnimaTicket.create(baseEv));
        }
        if (plan.recipeEv() > 0 && plan.recipeNetwork() != null) {
            plan.recipeNetwork().syphon(AnimaTicket.create(plan.recipeEv()));
        }

        insertOutput(result);
        for (ItemStack byproduct : plan.byproducts()) {
            insertOutput(byproduct);
        }
    }

    private void damageCuttingFluid(int slot, ItemStack stack) {
        if (IAthanorTool.tryConsumeUse(stack)) {
            inv.setStackInSlot(slot, stack);
        } else if (stack.has(DataComponents.MAX_DAMAGE)) {
            int newDamage = stack.getOrDefault(DataComponents.DAMAGE, 0) + 1;
            if (newDamage >= stack.getMaxDamage()) {
                inv.setStackInSlot(slot, ItemStack.EMPTY);
            } else {
                stack.set(DataComponents.DAMAGE, newDamage);
                inv.setStackInSlot(slot, stack);
            }
        } else {
            stack.shrink(1);
            inv.setStackInSlot(slot, stack);
        }
    }

    private void insertOutput(ItemStack stack) {
        if (stack.isEmpty()) return;
        int left = stack.getCount();
        for (int i = 0; i < OUTPUT_COUNT && left > 0; i++) {
            ItemStack slot = inv.getStackInSlot(OUTPUT_START + i);
            if (!slot.isEmpty() && ItemStack.isSameItemSameComponents(slot, stack)) {
                int move = Math.min(left, slot.getMaxStackSize() - slot.getCount());
                if (move > 0) {
                    slot.grow(move);
                    inv.setStackInSlot(OUTPUT_START + i, slot);
                    left -= move;
                }
            }
        }
        for (int i = 0; i < OUTPUT_COUNT && left > 0; i++) {
            if (inv.getStackInSlot(OUTPUT_START + i).isEmpty()) {
                int move = Math.min(left, stack.getMaxStackSize());
                inv.setStackInSlot(OUTPUT_START + i, stack.copyWithCount(move));
                left -= move;
            }
        }
        if (left > 0 && level != null) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(), stack.copyWithCount(left));
        }
    }

    private void pushOutputs(Level level) {
        if (outputFace == null) return;
        BlockPos center = getCenter();
        for (int dy = -1; dy <= 0; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (!inStructure(center, pos.relative(outputFace))) {
                        OutputPusher.push(level, pos, outputFace, inv, OUTPUT_START, OUTPUT_START + OUTPUT_COUNT - 1);
                    }
                }
            }
        }
    }

    public IItemHandler getInventory(@Nullable Direction side) {
        if (side == null) return inv;
        return new SideConfigItemHandler(inv, sideConfig, side, SanguineCrafterBlockEntity::groupOf);
    }

    @Nullable
    public static SanguineCrafterBlockEntity findController(Level level, BlockPos partPos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos center = partPos.offset(dx, dy, dz);
                    for (Direction dir : Direction.Plane.HORIZONTAL) {
                        BlockPos controllerPos = center.relative(dir);
                        if (!level.isLoaded(controllerPos)) continue;
                        if (level.getBlockEntity(controllerPos) instanceof SanguineCrafterBlockEntity crafter
                                && crafter.isFormed() && crafter.getCenter().equals(center)) {
                            return crafter;
                        }
                    }
                }
            }
        }
        return null;
    }

    public void setOwner(Player player) {
        owner = player.getUUID();
        ownerName = player.getGameProfile().getName();
        setChanged();
    }

    @Nullable
    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        progress = 0;
        cachedCraftingRecipe = null;
        setChanged();
    }

    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public void setRedstoneMode(RedstoneMode redstoneMode) {
        this.redstoneMode = redstoneMode;
        setChanged();
    }

    public int getOutputLimit() {
        return outputLimit;
    }

    public void setOutputLimit(int outputLimit) {
        this.outputLimit = Math.max(0, Math.min(outputLimit, 64 * OUTPUT_COUNT));
        setChanged();
    }

    public void setTemplate(Mode mode, List<ItemStack> stacks) {
        for (int i = 0; i < TEMPLATE_SIZE; i++) {
            ItemStack stack = i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY;
            template.setStackInSlot(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
        }
        setMode(mode);
    }

    public Status getStatus() {
        return status;
    }

    public ChunkLoading getChunkLoading() {
        return chunkLoading;
    }

    public boolean isChunkLoadingEnabled() {
        return chunkLoadingEnabled;
    }

    public void setChunkLoadingEnabled(boolean enabled) {
        this.chunkLoadingEnabled = enabled;
        if (!enabled && level instanceof ServerLevel serverLevel) {
            SanguineCrafterChunkLoader.release(serverLevel, worldPosition);
            chunkLoading = ChunkLoading.TURNED_OFF;
        }
        structureTimer = 0;
        setChanged();
    }

    public int getProgress() {
        return progress;
    }

    public int getCraftTime() {
        return craftTime;
    }

    public int getHellforgedCasings() {
        return hellforgedCasings;
    }

    public int getSwiftCasings() {
        return swiftCasings;
    }

    @Override
    public SlotSideConfig getSideConfig() {
        return sideConfig;
    }

    @Override
    @Nullable
    public Direction getOutputFace() {
        return outputFace;
    }

    @Override
    public void setOutputFace(@Nullable Direction face) {
        outputFace = face;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inv.deserializeNBT(registries, tag.getCompound("inventory"));
        template.deserializeNBT(registries, tag.getCompound("template"));
        sideConfig.load(tag);
        outputFace = OutputPusher.load(tag);
        owner = tag.hasUUID("owner") ? tag.getUUID("owner") : null;
        ownerName = tag.getString("ownerName");
        mode = Mode.byIndex(tag.getInt("mode"));
        redstoneMode = RedstoneMode.byIndex(tag.getInt("redstoneMode"));
        outputLimit = tag.getInt("outputLimit");
        chunkLoadingEnabled = !tag.contains("chunkLoading") || tag.getBoolean("chunkLoading");
        formed = tag.getBoolean("formed");
        hellforgedCasings = tag.getInt("hellforgedCasings");
        swiftCasings = tag.getInt("swiftCasings");
        progress = tag.getInt("progress");
        craftTime = tag.getInt("craftTime");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inv.serializeNBT(registries));
        tag.put("template", template.serializeNBT(registries));
        sideConfig.save(tag);
        OutputPusher.save(tag, outputFace);
        if (owner != null) {
            tag.putUUID("owner", owner);
        }
        tag.putString("ownerName", ownerName);
        tag.putInt("mode", mode.ordinal());
        tag.putInt("redstoneMode", redstoneMode.ordinal());
        tag.putInt("outputLimit", outputLimit);
        tag.putBoolean("chunkLoading", chunkLoadingEnabled);
        tag.putBoolean("formed", formed);
        tag.putInt("hellforgedCasings", hellforgedCasings);
        tag.putInt("swiftCasings", swiftCasings);
        tag.putInt("progress", progress);
        tag.putInt("craftTime", craftTime);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.neovitae.sanguine_crafter");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SanguineCrafterMenu(containerId, playerInventory, this);
    }

    public void onBlockRemoved(BlockPos pos, BlockState state) {
        if (level != null && !level.isClientSide()) {
            for (int i = 0; i < SLOT_COUNT; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                    inv.setStackInSlot(i, ItemStack.EMPTY);
                }
            }
            if (level instanceof ServerLevel serverLevel) {
                SanguineCrafterChunkLoader.release(serverLevel, pos);
            }
            if (formed) {
                BlockPos center = pos.relative(state.getValue(SanguineCrafterBlock.FACING).getOpposite());
                updateCasingStates(level, center, false);
                invalidateStructureCapabilities(level, center);
            }
        }
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(worldPosition);
    }
}
