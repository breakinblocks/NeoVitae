package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class SpiritusCatalystsEntry extends EntryProvider {

    public SpiritusCatalystsEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus Catalysts");
        this.pageText("Five catalysts exist, one for each Aspect of Spiritus: Raw, Ruina, Nihilum, Vindicta, "
                + "and Invictus. Each is forged in the [#](8B0000)Hellfire Forge[#]() and serves two distinct purposes "
                + "depending on the cluster you click with it.");

        this.page("bootstrap", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Bootstrapping Aspected Crystals");
        this.pageText("A [#](8B0000)Crystallarium Maleficum[#]() will only grow [#](8B0000)Raw Spiritus[#]() clusters until "
                + "the chunk's Aura is dominated by a different Aspect, and that demands aspected crystals "
                + "you do not yet possess. The catalyst is the only way to break this cycle.\\\n\\\n"
                + "Right-click a [#](8B0000)fully-grown Raw cluster[#]() with an aspected catalyst (Ruina, Nihilum, "
                + "Vindicta, or Invictus) and the cluster transmutes into the catalyst's Aspect, restarting "
                + "at age zero. The catalyst is consumed in the act.");

        this.page("animus_cost", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Transmutation also demands one [#](8B0000)Animus Mote[#]() be present in your inventory; "
                + "the mote is what splinters the cluster's natures into a single focused Aspect.\\\n\\\n"
                + "Animus Motes drop from [#](8B0000)Daemonium Animaris[#]() in [#](2E8B57)Standard Dungeons[#](), and appear in "
                + "[#](2E8B57)great_loot[#]() and [#](2E8B57)decent_loot[#]() Standard Dungeon chests. Four motes are enough to seed "
                + "every Aspect lineage; thereafter you only need them when starting a new farm or seeding a "
                + "fresh cluster type.\\\n\\\n"
                + "[#](2E8B57)Once you have one aspected cluster of a given Aspect, burn its harvested shards in a Vas "
                + "Maleficum to make that Aspect dominant in the chunk. The Crystallarium will then form new "
                + "clusters of that Aspect natively, no further motes required.[#]()");

        this.page("acceleration", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Accelerating Existing Clusters");
        this.pageText("Right-click a cluster with a [#](8B0000)matching-Aspect[#]() catalyst (e.g., Ruina catalyst on a "
                + "Ruina cluster) and the cluster's growth is supercharged: the per-spire Spiritus cost drops "
                + "from 45 to [#](B8860B)25[#](), and growth accelerates tenfold. One dose fuels ten spires, netting "
                + "[#](B8860B)+200 Spiritus per catalyst[#]() versus passive aura growth.\\\n\\\n"
                + "A second dose extends the effect to twenty growths total. Same-aspect acceleration does "
                + "[#](2E8B57)not[#]() consume an Animus Mote.");

        this.page("automation_loop", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Pair the catalyst with the [#](8B0000)Crystallum Fractura[#]() ritual for a hands-off farm: "
                + "the ritual auto-harvests fully-grown clusters, doubles ambient growth, and (with the "
                + "Master Ritual Stone attuned via the Ritual Reader) can bias the +25%% injection bonus toward "
                + "any Aspect you choose.\\\n\\\n"
                + "A modest [#](8B0000)Routing Node[#]() arrangement can then ship harvested shards back into a "
                + "[#](8B0000)Vas Maleficum[#]() to maintain saturation, closing the loop. The exact layout is left as "
                + "an exercise for the ambitious.");
    }

    @Override
    protected String entryName() {
        return "Spiritus Catalysts";
    }

    @Override
    protected String entryDescription() {
        return "Supercharging crystal growth for automated Spiritus production.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.RAW_SPIRITUS_CATALYST.get());
    }

    @Override
    protected String entryId() {
        return "spiritus_catalysts";
    }
}
