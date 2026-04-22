package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BookSpiritusGemStatsPageRenderer extends BookPageRenderer<BookSpiritusGemStatsPage> {

    private static final int ROW_HEIGHT = 20;
    private static final int ICON_X = 6;
    private static final int TEXT_X = 28;

    public BookSpiritusGemStatsPageRenderer(BookSpiritusGemStatsPage page) {
        super(page);
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int y = 0;
        if (this.page.hasTitle()) {
            this.renderTitle(guiGraphics, this.page.getTitle(), false, BookEntryScreen.PAGE_WIDTH / 2, 0);
            y += 14;
        }

        for (GemEntry entry : collectGems()) {
            this.parentScreen.renderItemStack(guiGraphics, ICON_X, y + 1, mouseX, mouseY, entry.stack);

            Component name = entry.stack.getHoverName().copy();
            guiGraphics.text(this.font, name, TEXT_X, y, 0x4A0080);

            String line = String.format("%s Spiritus", formatNumber(entry.max));
            guiGraphics.text(this.font, line, TEXT_X, y + 10, 0x555555);
            y += ROW_HEIGHT;
        }
    }

    private static List<GemEntry> collectGems() {
        List<GemEntry> list = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            Double max = BuiltInRegistries.ITEM.wrapAsHolder(item).getData(NVDataMaps.SPIRITUS_GEM_MAX_AMOUNTS);
            if (max != null) {
                list.add(new GemEntry(new ItemStack(item), max));
            }
        }
        list.sort(Comparator.comparingDouble(e -> e.max));
        return list;
    }

    private static String formatNumber(double value) {
        if (value == Math.floor(value)) {
            return String.format("%,d", (long) value);
        }
        return String.format("%,.2f", value);
    }

    @Override
    public Style getClickedComponentStyleAt(double x, double y) {
        return null;
    }

    private record GemEntry(ItemStack stack, double max) {}
}
