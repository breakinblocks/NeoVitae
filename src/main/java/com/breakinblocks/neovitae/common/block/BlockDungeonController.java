package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.BMTiles;
import com.breakinblocks.neovitae.common.blockentity.TileDungeonController;

/**
 * Dungeon Controller block - the central control block for procedural dungeons.
 * Placed when a dungeon is generated and manages room placement.
 */
public class BlockDungeonController extends Block implements EntityBlock {

    public BlockDungeonController() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.STONE)
                .strength(-1.0F, 3600000.0F)  // Unbreakable in survival
                .noLootTable());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileDungeonController(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return type == BMTiles.DUNGEON_CONTROLLER_TYPE.get()
                ? (lvl, pos, st, be) -> TileDungeonController.tick(lvl, pos, st, (TileDungeonController) be)
                : null;
    }
}
