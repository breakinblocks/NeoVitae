package com.breakinblocks.neovitae.common.blockentity;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datamap.BloodOrb;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.util.AltarUtil;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

public class OrbFillingLinkBlockEntity extends BaseBlockEntity {

    public static final int ORB_SLOT = 0;

    private static final int BIND_RADIUS = 8;
    private static final int REVALIDATE_INTERVAL = 40;
    private static final int SIPHON_INTERVAL = 8;

    public final Inv inv = new Inv();

    public class Inv extends ItemStacksResourceHandler {
        Inv() { super(1); }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return resource.isEmpty() || resource.toStack(1).getItem() instanceof BloodOrbItem;
        }

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

    @Nullable private BlockPos altarPos = null;

    private transient AraVitaeTile cachedAltar;
    private transient boolean bindingNeedsValidation = true;
    private transient int ticks;
    private transient int lastSignal = 0;

    public OrbFillingLinkBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.ORB_FILLING_LINK_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, OrbFillingLinkBlockEntity be) {
        if (level.isClientSide()) return;
        be.serverTick();
    }

    private void serverTick() {
        ticks++;
        if (bindingNeedsValidation) validateBinding();
        if (ticks % REVALIDATE_INTERVAL == 0) refreshAltar();

        AraVitaeTile altar = getAltar();
        if (altar != null && !isPowered()) pump(altar);

        int signal = getComparatorSignal();
        if (signal != lastSignal) {
            lastSignal = signal;
            setChanged();
            if (level != null) {
                level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            }
        }
    }

    private void pump(AraVitaeTile altar) {
        if (altar.isActive() || altar.anyLinkWantsCraft()) return;
        if (altar.getMainTank() <= 0) return;
        ItemStack orbStack = inv.getStackInSlot(ORB_SLOT);
        if (!(orbStack.getItem() instanceof BloodOrbItem)) return;
        Binding binding = orbStack.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
        if (binding.isEmpty()) return;
        BloodOrb orb = orbStack.typeHolder().getData(NVDataMaps.BLOOD_ORB_STATS);
        if (orb == null) return;

        int maxCap = (int) (orb.animaCapacity() * (1 + altar.getOrbCapacityBonus()));
        int available = Math.min(altar.getMainTank(), (int) (orb.fillRate() * (1 + altar.getSpeedBonus())));
        if (available <= 0) return;

        Anima network = AnimaHelper.getAnima(binding.uuid());
        int drained = network.add(AnimaTicket.create(available), maxCap);
        if (drained > 0) {
            altar.drainMainTank(drained);
            if (ticks % SIPHON_INTERVAL == 0 && level instanceof ServerLevel server) {
                StreamPresets.bloodTendril(altar.getBlockPos(), worldPosition).build().sendToNearby(server, worldPosition, 32);
                level.playSound(null, worldPosition, NVSounds.BLOOD_TANK_FILL.get(), SoundSource.BLOCKS, 0.4f, 1.0f);
            }
        }
    }

    public int getComparatorSignal() {
        ItemStack orbStack = inv.getStackInSlot(ORB_SLOT);
        if (!(orbStack.getItem() instanceof BloodOrbItem)) return 0;
        Binding binding = orbStack.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
        if (binding.isEmpty()) return 0;
        BloodOrb orb = orbStack.typeHolder().getData(NVDataMaps.BLOOD_ORB_STATS);
        if (orb == null || orb.animaCapacity() <= 0) return 0;
        Anima network = AnimaHelper.getAnima(binding.uuid());
        return Mth.clamp((int) Math.ceil(15.0 * network.getCurrentEV() / orb.animaCapacity()), 0, 15);
    }

    public int getNetworkPercent() {
        ItemStack orbStack = inv.getStackInSlot(ORB_SLOT);
        if (!(orbStack.getItem() instanceof BloodOrbItem)) return 0;
        Binding binding = orbStack.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
        if (binding.isEmpty()) return 0;
        BloodOrb orb = orbStack.typeHolder().getData(NVDataMaps.BLOOD_ORB_STATS);
        if (orb == null || orb.animaCapacity() <= 0) return 0;
        Anima network = AnimaHelper.getAnima(binding.uuid());
        return Mth.clamp((int) (100L * network.getCurrentEV() / orb.animaCapacity()), 0, 100);
    }

    public boolean hasOrb() {
        return inv.getStackInSlot(ORB_SLOT).getItem() instanceof BloodOrbItem;
    }

    private boolean isPowered() {
        return level != null && level.hasNeighborSignal(worldPosition);
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
            if (!(level.getBlockEntity(altarPos) instanceof AraVitaeTile)) clearAltar();
        }
        if (altarPos == null) tryBind();
        bindingNeedsValidation = false;
    }

    private void refreshAltar() {
        if (getAltar() == null) {
            tryBind();
            if (getAltar() == null) clearAltar();
        }
    }

    private void tryBind() {
        if (level == null) return;
        BlockPos found = AltarUtil.findAltar(level, worldPosition, BIND_RADIUS);
        if (found != null && level.getBlockEntity(found) instanceof AraVitaeTile) {
            altarPos = found.immutable();
            cachedAltar = null;
            if (level instanceof ServerLevel server) {
                StreamPresets.arcaneBolt(worldPosition, found).build().sendToNearby(server, worldPosition, 128);
            }
            setChanged();
        }
    }

    private void clearAltar() {
        if (altarPos != null) {
            altarPos = null;
            cachedAltar = null;
            setChanged();
        }
    }

    public boolean isLinked() {
        return altarPos != null;
    }

    @Nullable
    public BlockPos getAltarPos() {
        return altarPos;
    }

    public static ResourceHandler<ItemResource> getItemHandler(OrbFillingLinkBlockEntity tile, @Nullable Direction side) {
        return tile.inv;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        bindingNeedsValidation = true;
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        inv.serialize(tag.child("inventory"));
        if (altarPos != null) tag.putLong("altarPos", altarPos.asLong());
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        tag.child("inventory").ifPresent(inv::deserialize);
        altarPos = tag.read("altarPos", Codec.LONG).map(BlockPos::of).orElse(null);
        cachedAltar = null;
        bindingNeedsValidation = true;
    }
}
