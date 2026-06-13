package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffect;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectLight;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectType;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.AlchemyArrayInput;
import com.breakinblocks.neovitae.common.recipe.alchemyarray.AlchemyArrayRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import java.util.Optional;
import net.minecraft.world.item.crafting.RecipeHolder;
import java.util.Objects;

public class AlchemyArrayBlockEntity extends BaseBlockEntity {
    public boolean isActive = false;
    public int activeCounter = 0;
    public Direction rotation = Direction.NORTH;
    public int rotateCooldown = 0;
    private boolean craftComplete = false;

    public AlchemyArrayEffect arrayEffect;
    private boolean doDropIngredients = true;
    private Binding ownerBinding = Binding.EMPTY;
    private DyeColor arrayColor = null;
    private CompoundTag pendingEffectNbt = null;
    private Identifier cachedTexture = null;

    public final Inv inv = new Inv();

    public class Inv extends ItemStacksResourceHandler {
        Inv() { super(2); }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }

        public ItemStack getStackInSlot(int slot) {
            ItemResource r = getResource(slot);
            return r.isEmpty() ? ItemStack.EMPTY : r.toStack(getAmountAsInt(slot));
        }

        public void setStackInSlot(int slot, ItemStack stack) {
            set(slot, ItemResource.of(stack), stack.getCount());
        }

        public int getSlots() { return size(); }

        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemResource r = getResource(slot);
            if (r.isEmpty() || amount <= 0) return ItemStack.EMPTY;
            try (Transaction tx = Transaction.openRoot()) {
                int extracted = extract(slot, r, amount, tx);
                if (extracted <= 0) return ItemStack.EMPTY;
                if (!simulate) tx.commit();
                return r.toStack(extracted);
            }
        }
    }

    public AlchemyArrayBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.ALCHEMY_ARRAY_TYPE.get(), pos, state);
    }

    public void onEntityCollidedWithBlock(BlockState state, Entity entity) {
        if (arrayEffect != null) {
            arrayEffect.onEntityCollidedWithBlock(this, getLevel(), worldPosition, state, entity);
        }
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        this.isActive = tag.getBooleanOr("isActive", false);
        this.activeCounter = tag.getIntOr("activeCounter", 0);
        this.doDropIngredients = tag.getBooleanOr("doDropIngredients", true);
        this.rotation = Direction.from2DDataValue(tag.getIntOr("direction", 0));
        tag.child("inventory").ifPresent(inv::deserialize);

        this.ownerBinding = tag.read("ownerBinding", Binding.BASIC_CODEC).orElse(Binding.EMPTY);

        var colorOpt = tag.getInt("arrayColor");
        this.arrayColor = colorOpt.map(DyeColor::byId).orElse(null);

        this.cachedTexture = tag.read("cachedTexture", Identifier.CODEC).orElse(null);

        pendingEffectNbt = tag.read("effectState", CompoundTag.CODEC).orElse(null);
        if (pendingEffectNbt != null && arrayEffect != null) {
            arrayEffect.readFromNBT(pendingEffectNbt);
            pendingEffectNbt = null;
        }
    }

    public void doDropIngredients(boolean drop) {
        this.doDropIngredients = drop;
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putBoolean("isActive", isActive);
        tag.putInt("activeCounter", activeCounter);
        tag.putBoolean("doDropIngredients", doDropIngredients);
        tag.putInt("direction", rotation.get2DDataValue());
        inv.serialize(tag.child("inventory"));
        if (!ownerBinding.isEmpty()) {
            tag.store("ownerBinding", Binding.BASIC_CODEC, ownerBinding);
        }
        if (arrayColor != null) {
            tag.putInt("arrayColor", arrayColor.getId());
        }
        if (cachedTexture != null) {
            tag.store("cachedTexture", Identifier.CODEC, cachedTexture);
        }
        if (arrayEffect != null) {
            CompoundTag effectTag = new CompoundTag();
            arrayEffect.writeToNBT(effectTag);
            if (!effectTag.isEmpty()) {
                tag.store("effectState", CompoundTag.CODEC, effectTag);
            }
        }
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
                if (pendingEffectNbt != null) {
                    arrayEffect.readFromNBT(pendingEffectNbt);
                    pendingEffectNbt = null;
                }
                if (level != null && !level.isClientSide()) {
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
                doDropIngredients = false;
                if (level != null && !level.isClientSide()) {
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

        Optional<RecipeHolder<AlchemyArrayRecipe>> holderOpt = level instanceof ServerLevel serverLevel
                ? serverLevel.recipeAccess().getRecipeFor(NVRecipes.ALCHEMY_ARRAY_TYPE.get(), input, serverLevel)
                : Optional.empty();
        holderOpt.ifPresent(h -> {
            Identifier tex = h.value().getTexture();
            if (!Objects.equals(tex, cachedTexture)) {
                cachedTexture = tex;
                setChanged();
                if (level != null && !level.isClientSide()) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
            }
        });
        return holderOpt
                .map(holder -> {
                    AlchemyArrayRecipe recipe = holder.value();
                    AlchemyArrayEffectType effectType = recipe.getEffectType();

                    AlchemyArrayEffect effect;
                    if (effectType == AlchemyArrayEffectType.CRAFTING || effectType == AlchemyArrayEffectType.BINDING) {
                        if (recipe.getOutput().isEmpty()) {
                            return null;
                        }
                        effect = effectType.createCraftingEffect(recipe.getOutput());
                    } else {
                        effect = effectType.createEffect();
                    }
                    if (effect != null) {
                        effect.setEvCost(recipe.getEvCost());
                    }
                    return effect;
                })
                .orElse(null);
    }

    public Direction getRotation() {
        return rotation;
    }

    @Nullable
    public Identifier getCachedTexture() {
        return cachedTexture;
    }

    public void setRotation(Direction rotation) {
        this.rotation = rotation;
    }

    public Binding getOwnerBinding() {
        return ownerBinding;
    }

    public void setOwnerBinding(Binding binding) {
        this.ownerBinding = binding;
        setChanged();
    }

    public DyeColor getArrayColor() {
        return arrayColor;
    }

    public void setArrayColor(DyeColor color) {
        this.arrayColor = color;
        setChanged();
    }

    public ItemStack getItem(int slot) {
        return inv.getStackInSlot(slot);
    }

    public void dropItems() {
        if (doDropIngredients && level != null && !level.isClientSide()) {
            for (int i = 0; i < inv.getSlots(); i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
                }
            }
        }
        if (arrayEffect instanceof AlchemyArrayEffectLight lightEffect && level != null && !level.isClientSide()) {
            lightEffect.removeLights(level);
        }
    }

    public int getRedstoneSignal() {
        if (arrayEffect != null) {
            return arrayEffect.getRedstoneSignal(this);
        }
        return 0;
    }

    public boolean isSignalSource() {
        return arrayEffect != null && arrayEffect.isSignalSource();
    }

    public void onNeighborChanged(BlockPos neighborPos) {
        if (arrayEffect != null) {
            arrayEffect.onNeighborChanged(this, neighborPos);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        dropItems();
        super.preRemoveSideEffects(pos, state);
    }

}
