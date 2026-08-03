package com.breakinblocks.neovitae.compat.modonomicon.page;

import com.breakinblocks.neovitae.common.registry.AltarComponent;
import com.breakinblocks.neovitae.common.registry.AltarTier;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen;
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BookAltarTierInfoPageRenderer extends BookPageRenderer<BookAltarTierInfoPage> {

    private static final int ROW_HEIGHT = 11;
    private static final int TIER_X = 6;
    private static final int RUNE_X = 60;

    public BookAltarTierInfoPageRenderer(BookAltarTierInfoPage page) {
        super(page);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int y = 0;
        if (this.page.hasTitle()) {
            this.renderTitle(guiGraphics, this.page.getTitle(), false, BookEntryScreen.PAGE_WIDTH / 2, 0);
            y += 14;
        }

        guiGraphics.drawString(this.font,
                Component.translatable("book.neovitae.altar_tier_info.header.tier"), TIER_X, y, 0x4A0080, false);
        guiGraphics.drawString(this.font,
                Component.translatable("book.neovitae.altar_tier_info.header.runes"), RUNE_X, y, 0x4A0080, false);
        y += ROW_HEIGHT + 2;

        for (TierRow row : collectTiers()) {
            guiGraphics.drawString(this.font, String.valueOf(row.tier), TIER_X, y, 0x555555, false);
            guiGraphics.drawString(this.font, String.valueOf(row.runeBlocks), RUNE_X, y, 0x555555, false);
            y += ROW_HEIGHT;
        }
    }

    private static List<TierRow> collectTiers() {
        List<TierRow> rows = new ArrayList<>();
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return rows;
        }

        HolderLookup.RegistryLookup<AltarTier> registry = level.registryAccess()
                .lookup(NVRegistries.Keys.ALTAR_TIER_KEY)
                .orElse(null);
        if (registry == null) {
            return rows;
        }

        for (AltarTier tier : registry.listElements().map(Holder::value).toList()) {
            int runeBlocks = 0;
            for (AltarComponent component : tier.components()) {
                if (isRune(component)) {
                    runeBlocks++;
                }
            }
            if (runeBlocks > 0 || tier.tier() > 0) {
                rows.add(new TierRow(tier.tier(), runeBlocks));
            }
        }
        rows.sort(Comparator.comparingInt(TierRow::tier));
        return rows;
    }

    private static boolean isRune(AltarComponent component) {
        var material = component.material();
        return material.tag() && material.id().equals(NVTags.Blocks.RUNES.location());
    }

    @Override
    public Style getClickedComponentStyleAt(double x, double y) {
        return null;
    }

    private record TierRow(int tier, int runeBlocks) {}
}
