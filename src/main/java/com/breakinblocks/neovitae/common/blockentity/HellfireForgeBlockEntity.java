package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.client.sound.LoopSoundManager;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.event.NeoVitaeCraftedEvent;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.menu.HellfireForgeMenu;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeInput;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.common.tag.NVTags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HellfireForgeBlockEntity extends BaseBlockEntity implements MenuProvider {
    public final Inv inv = new Inv();

    public class Inv extends ItemStacksResourceHandler {
        Inv() { super(6); }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            if (index == OUTPUT_SLOT) return false;
            if (index == GEM_SLOT && !resource.has(NVDataComponents.SPIRITUS_AMOUNT)) return false;
            return true;
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

    // one day mojang is going to change Direction. But not today
    public static final int SOUTH = Direction.SOUTH.get2DDataValue(); // 0
    public static final int WEST = Direction.WEST.get2DDataValue(); // 1
    public static final int NORTH = Direction.NORTH.get2DDataValue(); // 2
    public static final int EAST = Direction.EAST.get2DDataValue(); // 3

    public static final int GEM_SLOT = 4;
    public static final int OUTPUT_SLOT = 5;

    public static final int MAX_PROGRESS = 100;
    protected int progress = 0;

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return progress;
        }

        @Override
        public void set(int index, int value) {
            progress = value;
        }

        @Override
        public int getCount() {
            return 1;
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

        ForgeInput input = tile.getInput();
        Optional<RecipeHolder<ForgeRecipe>> recipeOptional = level instanceof ServerLevel serverLevel
                ? serverLevel.recipeAccess().getRecipeFor(NVRecipes.HELLFIRE_FORGE_TYPE.get(), input, serverLevel)
                : Optional.empty();
        if (recipeOptional.isEmpty()) {
            if (tile.progress > 0) {
                tile.progress = 0;
                tile.setChanged();
            }
            return;
        }

        ForgeRecipe recipe = recipeOptional.get().value();
        ItemStack output = recipe.assemble(input);
        if (output.isEmpty()) {
            if (tile.progress > 0) {
                tile.progress = 0;
                tile.setChanged();
            }
            return;
        }

        ItemStack currentOutput = tile.inv.getStackInSlot(OUTPUT_SLOT);
        if (!currentOutput.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(currentOutput, output) ||
                    currentOutput.getCount() + output.getCount() > currentOutput.getMaxStackSize()) {
                if (tile.progress > 0) {
                    tile.progress = 0;
                    tile.setChanged();
                }
                return;
            }
        }

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

        ItemStack gemStack = tile.inv.getStackInSlot(GEM_SLOT);
        if (!gemStack.isEmpty()) {
            double will = gemStack.getOrDefault(NVDataComponents.SPIRITUS_AMOUNT, 0D);
            will -= recipe.usedWill;
            if (will <= 0 && gemStack.is(NVItems.RAW_SPIRITUS)) {
                tile.inv.setStackInSlot(GEM_SLOT, ItemStack.EMPTY);
            } else {
                gemStack.set(NVDataComponents.SPIRITUS_AMOUNT, Math.max(0, will));
                tile.inv.setStackInSlot(GEM_SLOT, gemStack);
            }
        }

        for (int i = SOUTH; i < GEM_SLOT; i++) {
            ItemStack item = tile.inv.getStackInSlot(i);
            if (item.isEmpty()) {
                continue;
            }
            var template = item.getCraftingRemainder();
            ItemStack remainder = template != null ? template.create() : ItemStack.EMPTY;
            if (!remainder.isEmpty()) {
                tile.inv.setStackInSlot(i, remainder);
                continue;
            }
            item.shrink(1);
            tile.inv.setStackInSlot(i, item);
        }

        if (currentOutput.isEmpty()) {
            tile.inv.setStackInSlot(OUTPUT_SLOT, event.getOutput());
        } else {
            currentOutput.grow(event.getOutput().getCount());
            tile.inv.setStackInSlot(OUTPUT_SLOT, currentOutput);
        }

        tile.progress = 0;
        tile.setChanged();
    }

    public ForgeInput getInput() {
        ItemStack gemStack = inv.getStackInSlot(GEM_SLOT);
        List<ItemStack> stacks = new ArrayList<>();
        int gemIndex = GEM_SLOT;
        for (int i = SOUTH; i < GEM_SLOT; i++) {
            ItemStack testStack = inv.getStackInSlot(i);
            stacks.add(testStack);
            if (testStack.is(NVTags.Items.SPIRITUS_GEM)) {
                gemStack = testStack;
                gemIndex = i;
            }
        }
        return new ForgeInput(stacks, gemStack, gemIndex);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        tag.child("inventory").ifPresent(inv::deserialize);
        progress = tag.getIntOr("progress", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        inv.serialize(tag.child("inventory"));
        tag.putInt("progress", progress);
    }

    public @Nullable ResourceHandler<ItemResource> getInventory(@Nullable Direction side) {
        if (side == null) {
            return inv;
        }
        return switch (side) {
            case UP -> RangedResourceHandler.ofSingleIndex(inv, GEM_SLOT);
            case DOWN -> RangedResourceHandler.ofSingleIndex(inv, OUTPUT_SLOT);
            default -> RangedResourceHandler.ofSingleIndex(inv, side.get2DDataValue());
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
