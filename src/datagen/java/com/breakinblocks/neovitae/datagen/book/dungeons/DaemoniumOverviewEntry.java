package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

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
        this.pageText("Every Daemonium drops [#](8B0000)Tainted Flesh[#](), an edible but risky food source. "
                + "Most also yield [#](4A0080)Spiritus Essences[#]() aligned with their nature; corrosive "
                + "demons favor corrosive will, destructive brutes yield destructive essence.\\\n\\\n"
                + "More importantly, each species carries a [#](8B0000)unique trophy material[#]() that serves "
                + "as a crafting component for powerful items unavailable through any other means.");

        this.page("will_types", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Will Affinities");
        this.pageText("Each demon species has a natural [#](4A0080)Spiritus[#]() affinity that determines "
                + "which type of essence it drops:\\\n\\\n"
                + "Cruoris: Raw/Nihilum\\\n"
                + "Corrodis: Ruina/Vindicta\\\n"
                + "Pestis: Ruina/Raw\\\n"
                + "Voraxis: Nihilum/Ruina\\\n"
                + "Rancoris: Vindicta/Invictus\\\n"
                + "Animaris: Invictus/Vindicta\\\n"
                + "Fervidis: Nihilum/Invictus\\\n"
                + "Doloris: Vindicta/Nihilum\\\n"
                + "Ignis: Nihilum/Ruina\\\n"
                + "Glaciaris: All types");
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
    protected Pair<Integer, Integer> entryBackground() {
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
