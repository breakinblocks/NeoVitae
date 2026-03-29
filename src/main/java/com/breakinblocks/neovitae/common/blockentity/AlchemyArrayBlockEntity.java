package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffect;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.AlchemyArrayInput;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;

public class AlchemyArrayBlockEntity extends BaseBlockEntity {
    public boolean isActive = false;
    public int activeCounter = 0;
    public Direction rotation = Direction.NORTH;
    public int rotateCooldown = 0;
    private boolean craftComplete = false;

    public AlchemyArrayEffect arrayEffect;
    private boolean doDropIngredients = true;

    public final ItemStackHandler inv = new ItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 0 || slot == 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    public AlchemyArrayBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.ALCHEMY_ARRAY_TYPE.get(), pos, state);
    }

    public void onEntityCollidedWithBlock(BlockState state, Entity entity) {
        if (arrayEffect != null) {
            arrayEffect.onEntityCollidedWithBlock(this, getLevel(), worldPosition, state, entity);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.isActive = tag.getBoolean("isActive");
        this.activeCounter = tag.getInt("activeCounter");
        if (!tag.contains("doDropIngredients")) {
            this.doDropIngredients = true;
        } else {
            this.doDropIngredients = tag.getBoolean("doDropIngredients");
        }
        this.rotation = Direction.from2DDataValue(tag.getInt("direction"));
        inv.deserializeNBT(registries, tag.getCompound("inventory"));
    }

    public void doDropIngredients(boolean drop) {
        this.doDropIngredients = drop;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("isActive", isActive);
        tag.putInt("activeCounter", activeCounter);
        tag.putBoolean("doDropIngredients", doDropIngredients);
        tag.putInt("direction", rotation.get2DDataValue());
        tag.put("inventory", inv.serializeNBT(registries));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AlchemyArrayBlockEntity tile) {
        tile.tick();
    }

    public void tick() {
        if (craftComplete) {
            return;
        }
        if (isActive && attemptCraft()) {
            activeCounter++;
        } else {
            isActive = false;
            doDropIngredients = true;
            activeCounter = 0;
            arrayEffect = null;
        }
        if (rotateCooldown > 0)
            rotateCooldown--;
    }

    public boolean attemptCraft() {
        if (craftComplete) {
            return false;
        }

        if (arrayEffect != null) {
            isActive = true;
        } else {
            AlchemyArrayEffect effect = getEffect();
            if (effect == null) {
                return false;
            } else {
                arrayEffect = effect;
                if (level != null && !level.isClientSide) {
                    level.playSound(null, worldPosition, NVSounds.ALCHEMY_ARRAY_ACTIVATE.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
                    for (int i = 0; i < 6; i++) {
                        double angle = (2 * Math.PI / 6) * i;
                        double px = worldPosition.getX() + 0.5 + Math.cos(angle) * 0.4;
                        double pz = worldPosition.getZ() + 0.5 + Math.sin(angle) * 0.4;
                        ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xAA0000), px, worldPosition.getY() + 0.5, pz, 1, 0, 0, 0, 0);
                    }
                }
            }
        }

        if (arrayEffect != null) {
            isActive = true;
            if (arrayEffect.update(this, this.activeCounter)) {
                craftComplete = true;
                if (level != null && !level.isClientSide) {
                    level.playSound(null, worldPosition, NVSounds.ALCHEMY_ARRAY_CRAFT.get(), SoundSource.BLOCKS, 0.7f, 1.0f);
                    ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xAA0000), worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, 10, 0.2, 0.0, 0.2, 0.05);
                }
                inv.extractItem(0, 1, false);
                inv.extractItem(1, 1, false);
                this.getLevel().setBlockAndUpdate(getBlockPos(), Blocks.AIR.defaultBlockState());
            }

            return true;
        }
        return false;
    }

    private AlchemyArrayEffect getEffect() {
        if (level == null) return null;

        ItemStack base = inv.getStackInSlot(0);
        ItemStack added = inv.getStackInSlot(1);

        if (base.isEmpty() || added.isEmpty()) return null;

        AlchemyArrayInput input = new AlchemyArrayInput(base, added);

        return level.getRecipeManager()
                .getRecipeFor(NVRecipes.ALCHEMY_ARRAY_TYPE.get(), input, level)
                .map(holder -> {
                    AlchemyArrayRecipe recipe = holder.value();
                    AlchemyArrayEffectType effectType = recipe.getEffectType();

                    // For crafting/binding effects, we need the output item
                    if (effectType == AlchemyArrayEffectType.CRAFTING || effectType == AlchemyArrayEffectType.BINDING) {
                        if (recipe.getOutput().isEmpty()) {
                            return null;
                        }
                        return effectType.createCraftingEffect(recipe.getOutput());
                    }

                    // For other effects (bounce, spike, etc.), create the effect directly
                    return effectType.createEffect();
                })
                .orElse(null);
    }

    public Direction getRotation() {
        return rotation;
    }

    public void setRotation(Direction rotation) {
        this.rotation = rotation;
    }

    public ItemStack getItem(int slot) {
        return inv.getStackInSlot(slot);
    }

    public void dropItems() {
        if (doDropIngredients && level != null && !level.isClientSide) {
            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
                }
            }
        }
    }
}
