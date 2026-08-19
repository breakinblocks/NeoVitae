package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.event.NeoVitaeCraftedEvent;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.menu.HellfireForgeMenu;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeInput;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.client.sound.LoopSoundManager;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.spiritus.SpiritusHelper;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HellfireForgeBlockEntity extends BaseBlockEntity implements MenuProvider {
    public ItemStackHandler inv = new ItemStackHandler(6) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == OUTPUT_SLOT) {
                return false;
            }

            if (slot == GEM_SLOT && !stack.has(NVDataComponents.SPIRITUS_AMOUNT)) {
                return false;
            }

            return true;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };

    // one day mojang is going to change Direction. But not today
    public static final int SOUTH = Direction.SOUTH.get2DDataValue(); // 0
    public static final int WEST = Direction.WEST.get2DDataValue(); // 1
    public static final int NORTH = Direction.NORTH.get2DDataValue(); // 2
    public static final int EAST = Direction.EAST.get2DDataValue(); // 3

    public static final int GEM_SLOT = 4;
    public static final int OUTPUT_SLOT = 5;

    public static final int MAX_PROGRESS = 100;

    public static final int DATA_PROGRESS = 0;
    public static final int DATA_STATUS = 1;
    public static final int DATA_REQUIRED_SPIRITUS = 2;
    public static final int DATA_STORED_SPIRITUS = 3;
    public static final int DATA_COUNT = 4;

    public static final int STATUS_IDLE = 0;
    public static final int STATUS_CRAFTING = 1;
    public static final int STATUS_NEEDS_SPIRITUS = 2;

    protected int progress = 0;
    protected int status = STATUS_IDLE;
    protected int requiredSpiritus = 0;
    protected int storedSpiritus = 0;

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_PROGRESS -> progress;
                case DATA_STATUS -> status;
                case DATA_REQUIRED_SPIRITUS -> requiredSpiritus;
                case DATA_STORED_SPIRITUS -> storedSpiritus;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case DATA_PROGRESS -> progress = value;
                case DATA_STATUS -> status = value;
                case DATA_REQUIRED_SPIRITUS -> requiredSpiritus = value;
                case DATA_STORED_SPIRITUS -> storedSpiritus = value;
                default -> { }
            }
        }

        @Override
        public int getCount() {
            return DATA_COUNT;
        }
    };

    public HellfireForgeBlockEntity(BlockPos pos, BlockState blockState) {
        super(NVTiles.HELLFIRE_FORGE_TYPE.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HellfireForgeBlockEntity tile) {
        if (level.isClientSide()) {
            if (tile.progress > 0) {
                LoopSoundManager.tryStartLoop(
                        NVSounds.HELLFIRE_FORGE_AMBIENT.get(), 0.2f, level, pos,
                        be -> be instanceof HellfireForgeBlockEntity forge && forge.progress > 0
                );
            }
            return;
        }

        absorbSpiritusFromChunk(level, pos, tile);

        ForgeInput input = tile.getInput();
        Optional<RecipeHolder<ForgeRecipe>> recipeOptional = findRecipe(level, input);

        if (recipeOptional.isEmpty() || !recipeOptional.get().value().hasEnoughSpiritus(input)) {
            ForgeInput craftingGemInput = tile.getCraftingGemInput();
            if (craftingGemInput.getGemIndex() != input.getGemIndex()) {
                Optional<RecipeHolder<ForgeRecipe>> fallback = findRecipe(level, craftingGemInput);
                if (fallback.isPresent() && fallback.get().value().hasEnoughSpiritus(craftingGemInput)) {
                    input = craftingGemInput;
                    recipeOptional = fallback;
                }
            }
        }

        if (recipeOptional.isEmpty()) {
            tile.abortCraft(STATUS_IDLE, 0, 0);
            return;
        }

        ForgeRecipe recipe = recipeOptional.get().value();
        if (!recipe.hasEnoughSpiritus(input)) {
            tile.abortCraft(STATUS_NEEDS_SPIRITUS, recipe.minSpiritus,
                    input.getGem().getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D));
            return;
        }

        ItemStack output = recipe.assemble(input, level.registryAccess());
        if (output.isEmpty()) {
            tile.abortCraft(STATUS_IDLE, 0, 0);
            return;
        }

        ItemStack currentOutput = tile.inv.getStackInSlot(OUTPUT_SLOT);
        if (!currentOutput.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(currentOutput, output) ||
                    currentOutput.getCount() + output.getCount() > currentOutput.getMaxStackSize()) {
                tile.abortCraft(STATUS_IDLE, 0, 0);
                return;
            }
        }

        tile.status = STATUS_CRAFTING;
        if (tile.progress == 0) {
            level.playSound(null, pos, NVSounds.HELLFIRE_FORGE_CRAFT.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
        }
        tile.progress++;
        if (tile.progress < MAX_PROGRESS) {
            if (tile.progress % 4 == 0) {
                ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xFF4400), pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 2, 0.1, 0, 0.1, 0.02);
            }
            return;
        }

        level.playSound(null, pos, NVSounds.HELLFIRE_FORGE_COMPLETE.get(), SoundSource.BLOCKS, 0.6f, 1.0f);
        ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xFF4400), pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 8, 0.3, 0.2, 0.3, 0);

        NeoVitaeCraftedEvent.Forge event = new NeoVitaeCraftedEvent.Forge(output, input.asArray());
        NeoForge.EVENT_BUS.post(event);

        if (input.getGemIndex() == GEM_SLOT) {
            ItemStack gemStack = tile.inv.getStackInSlot(GEM_SLOT);
            if (!gemStack.isEmpty()) {
                double will = gemStack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);
                will -= recipe.usedSpiritus;
                if (will <= 0 && gemStack.is(NVItems.RAW_SPIRITUS)) {
                    tile.inv.setStackInSlot(GEM_SLOT, ItemStack.EMPTY);
                } else {
                    gemStack.set(NVDataComponents.SPIRITUS_AMOUNT, Math.max(0, will));
                }
            }
        }

        for (int i = SOUTH; i < GEM_SLOT; i++) {
            ItemStack item = tile.inv.getStackInSlot(i);
            if (item.isEmpty()) {
                continue;
            }
            if (item.hasCraftingRemainingItem()) {
                tile.inv.setStackInSlot(i, item.getCraftingRemainingItem());
                continue;
            }
            item.shrink(1);
            if (item.isEmpty()) {
                tile.inv.setStackInSlot(i, ItemStack.EMPTY);
            }
        }

        if (currentOutput.isEmpty()) {
            tile.inv.setStackInSlot(OUTPUT_SLOT, event.getOutput());
        } else {
            currentOutput.grow(event.getOutput().getCount());
        }

        tile.progress = 0;
        tile.status = STATUS_IDLE;
        tile.setChanged();
    }

    private static Optional<RecipeHolder<ForgeRecipe>> findRecipe(Level level, ForgeInput input) {
        return level.getRecipeManager().getRecipeFor(NVRecipes.HELLFIRE_FORGE_TYPE.get(), input, level);
    }

    private void abortCraft(int newStatus, double required, double stored) {
        boolean wasCrafting = progress > 0;
        progress = 0;
        status = newStatus;
        requiredSpiritus = (int) Math.ceil(required);
        storedSpiritus = (int) Math.floor(stored);
        if (wasCrafting) {
            setChanged();
        }
    }

    private static void absorbSpiritusFromChunk(Level level, BlockPos pos, HellfireForgeBlockEntity tile) {
        ItemStack gemStack = tile.inv.getStackInSlot(GEM_SLOT);
        if (gemStack.isEmpty() || !SpiritusHelper.isRechargeable(gemStack)) {
            return;
        }
        for (SpiritusType type : SpiritusType.values()) {
            double currentChunkSpiritus = WorldSpiritusHandler.getCurrentSpiritus(level, pos, type);
            if (currentChunkSpiritus <= 0) continue;

            double fillAmount = Math.min(VasMaleficumBlockEntity.GEM_DRAIN_RATE, currentChunkSpiritus);
            double canFill = SpiritusHelper.fillSpiritus(gemStack, type, fillAmount, false);
            if (canFill > 0) {
                double drained = WorldSpiritusHandler.drainSpiritusFromChunk(level, pos, type, canFill);
                if (drained > 0) {
                    SpiritusHelper.fillSpiritus(gemStack, type, drained, true);
                    tile.setChanged();
                }
            }
        }
    }

    public ForgeInput getInput() {
        return buildInput(true);
    }

    public ForgeInput getCraftingGemInput() {
        return buildInput(false);
    }

    private ForgeInput buildInput(boolean preferGemSlot) {
        ItemStack fuelGem = inv.getStackInSlot(GEM_SLOT);
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack craftingGem = ItemStack.EMPTY;
        int craftingGemIndex = -1;
        for (int i = SOUTH; i < GEM_SLOT; i++) {
            ItemStack testStack = inv.getStackInSlot(i);
            stacks.add(testStack);
            if (testStack.is(NVTags.Items.SPIRITUS_GEM)) {
                craftingGem = testStack;
                craftingGemIndex = i;
            }
        }

        if (preferGemSlot && !fuelGem.isEmpty()) {
            return new ForgeInput(stacks, fuelGem, GEM_SLOT);
        }
        if (craftingGemIndex >= 0) {
            return new ForgeInput(stacks, craftingGem, craftingGemIndex);
        }
        return new ForgeInput(stacks, fuelGem, GEM_SLOT);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inv.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("progress");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inv.serializeNBT(registries));
        tag.putInt("progress", progress);
    }

    public @Nullable IItemHandler getInventory(Direction side) {
        if (side == null) {
            return inv;
        }

        return switch (side) {
            case UP -> new RangedWrapper(inv, GEM_SLOT, GEM_SLOT + 1);
            case DOWN -> new RangedWrapper(inv, OUTPUT_SLOT, OUTPUT_SLOT + 1);
            default -> new RangedWrapper(inv, side.get2DDataValue(), side.get2DDataValue() + 1);
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.neovitae.hellfire_forge");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new HellfireForgeMenu(containerId, playerInventory, this);
    }

    public int getProgress() { return progress; }

    public double getProgressForGui() {
        return (double) progress / (double) MAX_PROGRESS;
    }
}
