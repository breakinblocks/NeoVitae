package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayTile;

/**
 * Alchemy array effect for binding items (like creating sigils).
 * Similar to crafting but with lightning effects during processing.
 */
public class AlchemyArrayEffectBinding extends AlchemyArrayEffectCrafting {

    private static final int LIGHTNING_START = 50;
    private static final int LIGHTNING_END = 250;
    private static final int LIGHTNING_INTERVAL = 50;

    public AlchemyArrayEffectBinding(ItemStack outputStack) {
        this(outputStack, 300);
    }

    public AlchemyArrayEffectBinding(ItemStack outputStack, int tickLimit) {
        super(outputStack, tickLimit);
    }

    @Override
    public boolean update(AlchemyArrayTile tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide) {
            return false;
        }

        // Spawn lightning effects during processing
        if (ticksActive >= LIGHTNING_START && ticksActive <= LIGHTNING_END && ticksActive % LIGHTNING_INTERVAL == 0) {
            spawnLightningEffect(tile, level, ticksActive);
        }

        if (ticksActive >= tickLimit) {
            BlockPos pos = tile.getBlockPos();
            ItemStack output = outputStack.copy();

            ItemEntity outputEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, output);
            level.addFreshEntity(outputEntity);

            return true;
        }

        return false;
    }

    private void spawnLightningEffect(AlchemyArrayTile tile, Level level, int ticksActive) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        BlockPos pos = tile.getBlockPos();

        // Calculate position on circle around the array
        double radius = 2.0;
        double angle = (ticksActive / (double)LIGHTNING_INTERVAL) * Math.PI * 2 / 4; // 4 lightning bolts around

        double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
        double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;

        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
        if (bolt != null) {
            bolt.moveTo(x, pos.getY(), z);
            bolt.setVisualOnly(true);
            serverLevel.addFreshEntity(bolt);
        }
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectBinding(outputStack, tickLimit);
    }
}
