package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.common.datamap.BloodOrb;
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

public class BookBloodOrbStatsPageRenderer extends BookPageRenderer<BookBloodOrbStatsPage> {

    private static final int ROW_HEIGHT = 20;
    private static final int ICON_X = 6;
    private static final int TEXT_X = 28;

    public BookBloodOrbStatsPageRenderer(BookBloodOrbStatsPage page) {
        super(page);
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int y = 0;
        if (this.page.hasTitle()) {
            this.renderTitle(guiGraphics, this.page.getTitle(), false, BookEntryScreen.PAGE_WIDTH / 2, 0);
            y += 14;
        }

        List<OrbEntry> entries = collectOrbs();
        for (OrbEntry entry : entries) {
            this.parentScreen.renderItemStack(guiGraphics, ICON_X, y + 1, mouseX, mouseY, entry.stack);

            Component name = entry.stack.getHoverName().copy();
            guiGraphics.text(this.font, name, TEXT_X, y, 0x4A0080);

            String line = String.format("T%d \u00B7 %s EV \u00B7 %s mB \u00B7 %d/t",
                    entry.orb.tier(),
                    formatNumber(entry.orb.animaCapacity()),
                    formatNumber(entry.orb.fluidCapacity()),
                    entry.orb.fillRate());
            guiGraphics.text(this.font, line, TEXT_X, y + 10, 0x555555);
            y += ROW_HEIGHT;
        }
    }

    private static List<OrbEntry> collectOrbs() {
        List<OrbEntry> list = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            BloodOrb orb = BuiltInRegistries.ITEM.wrapAsHolder(item).getData(NVDataMaps.BLOOD_ORB_STATS);
            if (orb != null) {
                list.add(new OrbEntry(new ItemStack(item), orb));
            }
        }
        list.sort(Comparator.comparingInt(e -> e.orb.tier()));
        return list;
    }

    private static String formatNumber(int value) {
        return String.format("%,d", value);
    }

    @Override
    public Style getClickedComponentStyleAt(double x, double y) {
        return null;
    }

    private record OrbEntry(ItemStack stack, BloodOrb orb) {}
}
