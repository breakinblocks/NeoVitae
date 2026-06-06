// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

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
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.blockentity.DungeonControllerBlockEntity;

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
        return new DungeonControllerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return type == NVTiles.DUNGEON_CONTROLLER_TYPE.get()
                ? (lvl, pos, st, be) -> DungeonControllerBlockEntity.tick(lvl, pos, st, (DungeonControllerBlockEntity) be)
                : null;
    }
}
