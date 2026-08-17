package com.breakinblocks.neovitae.common.item.block;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import com.breakinblocks.neovitae.common.blockentity.SpiritAccumulatorBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.AccumulatorContent;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.soul.SpiritusTooltipHelper;
import com.breakinblocks.neovitae.util.helper.BlockEntityHelper;

import java.text.DecimalFormat;
import java.util.List;

public class SpiritAccumulatorBlockItem extends BlockItem {

    private static final DecimalFormat FORMAT = new DecimalFormat("#,###");

    public SpiritAccumulatorBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        AccumulatorContent content = stack.get(NVDataComponents.ACCUMULATOR_CONTENT.get());
        SpiritusType type = content == null ? null : content.typeOrNull();
        if (type == null) {
            tooltip.add(BlockEntityHelper.translatableHover("tooltip.neovitae.spirit_accumulator.unattuned"));
            return;
        }

        Component typeName = Component.translatable("tooltip.neovitae.spiritus." + type.getSerializedName())
                .withColor(SpiritusTooltipHelper.spiritusColor(type));
        tooltip.add(BlockEntityHelper.translatableHover("tooltip.neovitae.spirit_accumulator.stored",
                typeName, FORMAT.format(content.stored()), FORMAT.format(SpiritAccumulatorBlockEntity.CAPACITY)));

        if (!content.locked()) {
            tooltip.add(Component.translatable("tooltip.neovitae.spirit_accumulator.unlocked").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
