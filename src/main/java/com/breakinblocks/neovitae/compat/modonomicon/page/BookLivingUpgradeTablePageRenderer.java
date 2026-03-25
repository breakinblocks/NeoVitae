package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Style;

public class BookLivingUpgradeTablePageRenderer extends BookPageRenderer<BookLivingUpgradeTablePage> {

    public BookLivingUpgradeTablePageRenderer(BookLivingUpgradeTablePage page) {
        super(page);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int y = 0;

        if (this.page.hasTitle()) {
            this.renderTitle(guiGraphics, this.page.getTitle(), false, BookEntryScreen.PAGE_WIDTH / 2, 0);
            y += 16;
        }

        if (!this.page.getText().isEmpty()) {
            this.renderBookTextHolder(guiGraphics, this.page.getText(), 0, y, BookEntryScreen.PAGE_WIDTH);
        }
    }

    @Override
    public Style getClickedComponentStyleAt(double x, double y) {
        return null;
    }
}
