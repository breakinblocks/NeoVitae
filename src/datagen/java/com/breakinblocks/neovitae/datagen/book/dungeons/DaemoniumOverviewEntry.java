package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class DaemoniumOverviewEntry extends EntryProvider {

    public DaemoniumOverviewEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Daemonium");
        this.pageText("The [#](4A0080)Demon Realm[#]() is home to creatures unlike anything found on the surface. "
                + "Practitioners call them [#](8B0000)Daemonium[#](); twisted entities of malice that guard "
                + "the halls and mines of the endless depths.\\\n\\\n"
                + "They come in many forms, from shambling fodder to towering brutes, and each carries "
                + "within it fragments of power that a resourceful vitaemancer can harvest.");

        this.page("tiers", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Threat Assessment");
        this.pageText("The Daemonium fall into rough tiers of threat:\\\n\\\n"
                + "[#](2E8B57)Fodder[#](): Cruoris, Pestis, Animaris, Rancoris. Individually weak but numerous.\\\n\\\n"
                + "[#](B8860B)Standard[#](): Voraxis. Stronger, with life-draining attacks.\\\n\\\n"
                + "[#](8B0000)Elite[#](): Corrodis, Fervidis, Doloris. Dangerous foes with complex attack patterns.\\\n\\\n"
                + "[#](4A0080)Apex[#](): Ignis, Glaciaris. Boss-tier creatures with devastating abilities.");

        this.page("loot", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spoils of the Damned");
        this.pageText("Every Daemonium drops [#](8B0000)Tainted Flesh[#](), an edible but risky food source.\\\n\\\n"
                + "More importantly, each species carries a [#](8B0000)unique trophy material[#]() that serves "
                + "as a crafting component for powerful items unavailable through any other means.");
    }

    @Override
    protected String entryName() {
        return "The Daemonium";
    }

    @Override
    protected String entryDescription() {
        return "The twisted denizens of the Demon Realm, each bearing unique spoils.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.TAINTED_FLESH.get());
    }

    @Override
    protected String entryId() {
        return "daemonium_overview";
    }
}
