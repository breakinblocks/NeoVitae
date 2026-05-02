package com.breakinblocks.neovitae.common.blockentity;


import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.block.BloodLightBlock;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.util.helper.ColorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BloodLightBlockEntity extends BaseBlockEntity {

    private DyeColor color = DyeColor.RED;
    private boolean redstoneControlled = false;

    public BloodLightBlockEntity(BlockPos pos, BlockState blockState) {
        super(NVTiles.BLOOD_LIGHT.get(), pos, blockState);
    }

    public DyeColor getColor() {
        return color;
    }

    public void setColor(DyeColor color) {
        this.color = color;
        setChanged();
    }

    public boolean isRedstoneControlled() {
        return redstoneControlled;
    }

    public void setRedstoneControlled(boolean redstoneControlled) {
        this.redstoneControlled = redstoneControlled;
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putInt("Color", color.getId());
        tag.putBoolean("RedstoneControlled", redstoneControlled);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        color = DyeColor.byId(tag.getIntOr("Color", color.getId()));
        redstoneControlled = tag.getBooleanOr("RedstoneControlled", redstoneControlled);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientTick(Level level, BlockPos pos, BlockState state, BloodLightBlockEntity be) {
        if (!state.getValue(BloodLightBlock.POWERED)) return;

        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (!camera.isInitialized()) return;

        double dx = pos.getX() + 0.5 - camera.position().x;
        double dy = pos.getY() + 0.5 - camera.position().y;
        double dz = pos.getZ() + 0.5 - camera.position().z;
        if (dx * dx + dy * dy + dz * dz > 128.0 * 128.0) return;

        RandomSource random = level.getRandom();
        int color = ColorHelper.fromDye(be.getColor());
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;

        for (int i = 0; i < 2; i++) {
            double ox = (random.nextDouble() - 0.5) * 0.2;
            double oy = (random.nextDouble() - 0.5) * 0.2;
            double oz = (random.nextDouble() - 0.5) * 0.2;

            double vx = ox * 0.3;
            double vy = oy * 0.3 + 0.005;
            double vz = oz * 0.3;

            level.addAlwaysVisibleParticle(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), color),
                    true, cx + ox, cy + oy, cz + oz, vx, vy, vz);
        }

        if (level.getGameTime() % 24L == 0L) {
            level.addAlwaysVisibleParticle(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), color),
                    true, cx, cy, cz, 0, 0, 0);
        }
    }
}
