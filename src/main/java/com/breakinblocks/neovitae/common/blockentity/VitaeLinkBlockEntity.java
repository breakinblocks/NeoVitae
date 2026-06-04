package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.client.event.VitaeLinkBeamHandler;
import com.breakinblocks.neovitae.api.recipe.AraVitaeInput;
import com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.util.AltarUtil;

public class VitaeLinkBlockEntity extends BaseBlockEntity {

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    private static final int BIND_RADIUS = 8;
    private static final int REVALIDATE_INTERVAL = 40;
    private static final int ARBITRATION_INTERVAL = 4;
    private static final int SIPHON_INTERVAL = 8;

    public final ItemStackHandler inv = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            if (slot == INPUT_SLOT && !ignoreInputChange) {
                recipeDirty = true;
                craftedThisOccupancy = false;
                progress = 0;
            }
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == INPUT_SLOT && getStackInSlot(OUTPUT_SLOT).isEmpty();
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == INPUT_SLOT ? 1 : super.getSlotLimit(slot);
        }
    };

    private int craftTier = 0;
    private int progress = 0;
    private boolean craftedThisOccupancy = false;
    @Nullable private BlockPos altarPos = null;
    private boolean crafting = false;

    private transient AraVitaeTile cachedAltar;
    private transient int cachedAltarTier = Integer.MIN_VALUE;
    private transient int maxLinkTier = -1;
    private transient AraVitaeRecipe cachedRecipe;
    private transient boolean recipeDirty = true;
    private transient boolean bindingNeedsValidation = true;
    private transient boolean grantedThisWindow = false;
    private transient boolean ignoreInputChange = false;
    private transient int ticks;

    public VitaeLinkBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.VITAE_LINK_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VitaeLinkBlockEntity be) {
        if (level.isClientSide) return;
        be.serverTick();
    }

    private void serverTick() {
        ticks++;
        if (bindingNeedsValidation) validateBinding();
        if (ticks % REVALIDATE_INTERVAL == 0) refreshAltar();

        AraVitaeTile altar = getAltar();
        boolean nowCrafting = false;

        if (altar != null) {
            ItemStack input = inv.getStackInSlot(INPUT_SLOT);
            AraVitaeRecipe recipe = getRecipe();

            if (recipe == null && craftedThisOccupancy && !input.isEmpty()) {
                moveInputToOutput();
                input = inv.getStackInSlot(INPUT_SLOT);
                recipe = getRecipe();
            }

            boolean wants = recipe != null;
            altar.reportLink(worldPosition, effTier(), wants);

            if (wants) {
                if (ticks % ARBITRATION_INTERVAL == 0) {
                    grantedThisWindow = !altar.isActive() && altar.grantsCraftTo(worldPosition);
                }
                if (grantedThisWindow) {
                    nowCrafting = tickCraft(altar, input, recipe);
                }
            } else {
                grantedThisWindow = false;
            }
        }

        if (nowCrafting != crafting) {
            crafting = nowCrafting;
            setChanged();
        }
    }

    private int effTier() {
        return Math.min(craftTier, maxLinkTier);
    }

    @Nullable
    private AraVitaeTile getAltar() {
        if (cachedAltar != null && !cachedAltar.isRemoved()) return cachedAltar;
        cachedAltar = null;
        if (altarPos == null || level == null) return null;
        if (level.getBlockEntity(altarPos) instanceof AraVitaeTile altar) {
            cachedAltar = altar;
            return altar;
        }
        return null;
    }

    private void validateBinding() {
        if (level == null) return;
        if (altarPos != null) {
            if (!level.hasChunk(altarPos.getX() >> 4, altarPos.getZ() >> 4)) return;
            if (!(level.getBlockEntity(altarPos) instanceof AraVitaeTile)) {
                clearAltar();
            }
        }
        if (altarPos == null) {
            tryBind();
        }
        bindingNeedsValidation = false;
    }

    private void refreshAltar() {
        AraVitaeTile altar = getAltar();
        if (altar == null) {
            tryBind();
            altar = getAltar();
        }
        if (altar == null) {
            clearAltar();
            return;
        }
        int t = altar.getTier();
        if (t != cachedAltarTier) {
            cachedAltarTier = t;
            int newMax = t - 1;
            if (newMax != maxLinkTier) {
                maxLinkTier = newMax;
                if (craftTier > maxLinkTier) {
                    craftTier = Math.max(0, maxLinkTier);
                }
                recipeDirty = true;
                setChanged();
            }
        }
    }

    private void tryBind() {
        if (level == null) return;
        BlockPos found = AltarUtil.findAltar(level, worldPosition, BIND_RADIUS);
        if (found != null && level.getBlockEntity(found) instanceof AraVitaeTile) {
            altarPos = found.immutable();
            cachedAltar = null;
            cachedAltarTier = Integer.MIN_VALUE;
            recipeDirty = true;
            fireBindBolt(found);
            setChanged();
        }
    }

    private void clearAltar() {
        if (altarPos != null) {
            altarPos = null;
            cachedAltar = null;
            cachedAltarTier = Integer.MIN_VALUE;
            maxLinkTier = -1;
            recipeDirty = true;
            setChanged();
        }
    }

    @Nullable
    private AraVitaeRecipe getRecipe() {
        if (!recipeDirty) return cachedRecipe;
        recipeDirty = false;
        ItemStack in = inv.getStackInSlot(INPUT_SLOT);
        int eff = effTier();
        if (in.isEmpty() || eff < 0 || level == null) {
            cachedRecipe = null;
            return null;
        }
        cachedRecipe = level.getRecipeManager()
                .getRecipeFor(NVRecipes.ARA_VITAE_TYPE.get(), new AraVitaeInput(in, eff), level)
                .map(RecipeHolder::value)
                .orElse(null);
        return cachedRecipe;
    }

    private boolean tickCraft(AraVitaeTile altar, ItemStack input, AraVitaeRecipe recipe) {
        int totalRequired = recipe.getTotalBlood() * input.getCount();
        boolean hasOperated = false;

        int charging = altar.getChargingTank();
        if (charging > 0) {
            int take = Math.min(totalRequired - progress, charging);
            if (take > 0) {
                altar.drainChargingTank(take);
                progress += take;
                hasOperated = true;
            }
        }
        if (altar.getMainTank() > 0) {
            int speed = (int) (recipe.getCraftSpeed() * (1 + altar.getSpeedBonus()));
            int drained = Math.min(altar.getMainTank(), speed);
            drained = Math.min(drained, totalRequired - progress);
            if (drained > 0) {
                altar.drainMainTank(drained);
                progress += drained;
                hasOperated = true;
            }
        } else if (!hasOperated && progress > 0) {
            progress -= (int) (recipe.getDrainSpeed() * (1 + altar.getEfficiency()));
            if (progress <= 0) {
                progress = 0;
                emitStarveFailure();
            }
        }

        if (hasOperated && ticks % SIPHON_INTERVAL == 0) playFillSound();

        if (hasOperated && progress >= totalRequired) {
            completeCraft(altar, input, recipe);
            return true;
        }
        return hasOperated || progress > 0;
    }

    private void emitStarveFailure() {
        if (!(level instanceof ServerLevel server)) return;
        StreamPresets.voidMark(worldPosition).build().sendToNearby(server, worldPosition, 64);
        level.playSound(null, worldPosition, NVSounds.BLOOD_ALTAR_FAIL.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
    }

    private void completeCraft(AraVitaeTile altar, ItemStack input, AraVitaeRecipe recipe) {
        AraVitaeInput ri = new AraVitaeInput(input, effTier());
        ItemStack result = recipe.assemble(ri, level.registryAccess());
        result.setCount(input.getCount());

        ignoreInputChange = true;
        try {
            inv.setStackInSlot(INPUT_SLOT, result);
        } finally {
            ignoreInputChange = false;
        }
        progress = 0;
        craftedThisOccupancy = true;
        recipeDirty = true;
        level.playSound(null, worldPosition, NVSounds.BLOOD_ALTAR_CRAFT_COMPLETE.get(), SoundSource.BLOCKS, 0.5f, 1.2f);

        if (getRecipe() == null) {
            moveInputToOutput();
        }
        setChanged();
    }

    private void moveInputToOutput() {
        ItemStack input = inv.getStackInSlot(INPUT_SLOT);
        if (input.isEmpty()) return;
        ItemStack out = inv.getStackInSlot(OUTPUT_SLOT);

        ignoreInputChange = true;
        try {
            if (out.isEmpty()) {
                inv.setStackInSlot(OUTPUT_SLOT, input.copy());
                inv.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
                craftedThisOccupancy = false;
            } else if (ItemStack.isSameItemSameComponents(out, input)) {
                int space = out.getMaxStackSize() - out.getCount();
                int move = Math.min(space, input.getCount());
                if (move > 0) {
                    out.grow(move);
                    inv.setStackInSlot(OUTPUT_SLOT, out);
                    ItemStack remaining = input.copy();
                    remaining.shrink(move);
                    inv.setStackInSlot(INPUT_SLOT, remaining);
                    if (remaining.isEmpty()) craftedThisOccupancy = false;
                }
            }
        } finally {
            ignoreInputChange = false;
        }
        recipeDirty = true;
    }

    private void playFillSound() {
        if (!(level instanceof ServerLevel)) return;
        level.playSound(null, worldPosition, NVSounds.BLOOD_TANK_FILL.get(), SoundSource.BLOCKS, 0.4f, 1.0f);
    }

    private void fireBindBolt(BlockPos target) {
        if (level instanceof ServerLevel server) {
            StreamPresets.arcaneBolt(worldPosition, target).build().sendToNearby(server, worldPosition, 128);
        }
    }

    public boolean cycleCraftTier() {
        if (crafting) return false;
        int next = craftTier + 1;
        craftTier = (next > maxLinkTier) ? 0 : next;
        recipeDirty = true;
        setChanged();
        return true;
    }

    public boolean cancelCraft(Player player) {
        ItemStack input = inv.getStackInSlot(INPUT_SLOT);
        if (input.isEmpty()) return false;
        player.getInventory().placeItemBackInInventory(input.copy());
        inv.setStackInSlot(INPUT_SLOT, ItemStack.EMPTY);
        return true;
    }

    public int getCraftTier() {
        return craftTier;
    }

    public int getMaxLinkTier() {
        return maxLinkTier;
    }

    public boolean isLinked() {
        return altarPos != null;
    }

    @Nullable
    public BlockPos getAltarPos() {
        return altarPos;
    }

    public boolean isClientCrafting() {
        return crafting;
    }

    public static IItemHandler getItemHandler(VitaeLinkBlockEntity tile, @Nullable Direction side) {
        if (side == null) return tile.inv;
        return switch (side) {
            case DOWN -> new RangedWrapper(tile.inv, OUTPUT_SLOT, OUTPUT_SLOT + 1);
            default -> new RangedWrapper(tile.inv, INPUT_SLOT, INPUT_SLOT + 1);
        };
    }

    @Override
    public void onLoad() {
        super.onLoad();
        bindingNeedsValidation = true;
        if (level != null && level.isClientSide) {
            VitaeLinkBeamHandler.register(this);
        }
    }

    @Override
    public void setRemoved() {
        if (level != null && level.isClientSide) {
            VitaeLinkBeamHandler.unregister(this);
        } else {
            AraVitaeTile altar = getAltar();
            if (altar != null) altar.unregisterLink(worldPosition);
        }
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inv.serializeNBT(registries));
        tag.putInt("craftTier", craftTier);
        tag.putInt("progress", progress);
        tag.putBoolean("crafted", craftedThisOccupancy);
        tag.putBoolean("crafting", crafting);
        if (altarPos != null) tag.putLong("altarPos", altarPos.asLong());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) inv.deserializeNBT(registries, tag.getCompound("inventory"));
        craftTier = tag.getInt("craftTier");
        progress = tag.getInt("progress");
        craftedThisOccupancy = tag.getBoolean("crafted");
        crafting = tag.getBoolean("crafting");
        altarPos = tag.contains("altarPos") ? BlockPos.of(tag.getLong("altarPos")) : null;
        cachedAltar = null;
        cachedAltarTier = Integer.MIN_VALUE;
        recipeDirty = true;
        bindingNeedsValidation = true;
    }
}
