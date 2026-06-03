package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.blockentity.DemonLanternBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.NVTiles;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;
import org.jetbrains.annotations.Nullable;

public class DemonLanternBlock extends LanternBlock implements EntityBlock {

    public static BlockBehaviour.Properties defaultProperties() {
        return BlockBehaviour.Properties.ofFullCopy(Blocks.SOUL_LANTERN)
                .lightLevel(state -> 0);
    }

    public DemonLanternBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DemonLanternBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide()) return;
        if (level.getBlockEntity(pos) instanceof DemonLanternBlockEntity lantern) {
            Binding binding = stack.get(NVDataComponents.BINDING.get());
            if (binding != null && !binding.isEmpty()) {
                lantern.setBinding(binding);
            } else if (placer instanceof Player player) {
                lantern.setBinding(new Binding(player.getUUID(), player.getName().getString()));
            }
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return BlockEntityHelper.getTicker(type, NVTiles.DEMON_LANTERN_TYPE.get(), DemonLanternBlockEntity::tick);
    }
}
