package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.dimension.DungeonDimensionHelper;

import java.util.Optional;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class BloodLanternSpawnHandler {

    private static final int RADIUS = 16;

    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {
        MobCategory category = event.getEntity().getType().getCategory();
        if (category == MobCategory.MONSTER) return;

        ServerLevelAccessor levelAcc = event.getLevel();
        if (levelAcc.getLevel().dimension().equals(DungeonDimensionHelper.DUNGEON_DIMENSION)) return;

        Block target = NVBlocks.BLOOD_LANTERN.block().get();
        BlockPos center = BlockPos.containing(event.getX(), event.getY(), event.getZ());
        Optional<BlockPos> match = BlockPos.findClosestMatch(center, RADIUS, RADIUS,
                pos -> levelAcc.getBlockState(pos).is(target));

        if (match.isPresent()) {
            event.setSpawnCancelled(true);
            event.setCanceled(true);
        }
    }
}
