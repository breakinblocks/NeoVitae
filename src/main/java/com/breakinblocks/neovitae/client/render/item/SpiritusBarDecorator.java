package com.breakinblocks.neovitae.client.render.item;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.spiritus.SpiritusHelper;

public class SpiritusBarDecorator implements IItemDecorator {

    private static final int BAR_WIDTH = 13;

    public static void registerAll(RegisterItemDecorationsEvent event) {
        SpiritusBarDecorator decorator = new SpiritusBarDecorator();
        for (Item item : BuiltInRegistries.ITEM) {
            event.register(item, decorator);
        }
    }

    @Override
    public boolean render(GuiGraphicsExtractor guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (!SpiritusHelper.hasSpiritus(stack)) return false;

        double max = SpiritusHelper.resolveMaxSpiritus(stack);
        if (max <= 0) return false;

        double ratio = SpiritusHelper.getFillRatio(stack);
        int width = Math.round((float) (ratio * BAR_WIDTH));
        int color = getColorForType(SpiritusHelper.getCurrentType(stack));

        int x = xOffset + 2;
        int y = yOffset + 1;

        guiGraphics.fill(x, y, x + BAR_WIDTH, y + 2, 0xFF000000);
        guiGraphics.fill(x, y, x + width, y + 1, color | 0xFF000000);

        return false;
    }

    private static int getColorForType(SpiritusType type) {
        return switch (type) {
            case RAW -> 0x00CCCC;
            case RUINA -> 0x00CC00;
            case NIHILUM -> 0xCC6600;
            case VINDICTA -> 0x9900CC;
            case INVICTUS -> 0xCCCCCC;
        };
    }
}
