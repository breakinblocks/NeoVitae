package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class CrystallizedSpiritusEntry extends EntryProvider {

    public CrystallizedSpiritusEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crystallized Spiritus");
        this.pageText("Now that your [#](8B0000)Spiritus Gem[#]() brims with [#](8B0000)Spiritus[#](), you may wonder what happens "
                + "when that malice is unleashed upon the world itself. The answer begins with saturating "
                + "the [#](8B0000)Aura[#](), and continues with the [#](8B0000)Crystallarium Maleficum[#]().");

        this.page("crystallizer", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crystallarium Maleficum");
        this.pageText("Place it with open air above, and the Crystallarium [#](2E8B57)seeds the first spire[#]() of a "
                + "[#](8B0000)Spiritus Crystal[#]() cluster once the chunk's Aura holds at least [#](B8860B)99 Spiritus[#]() "
                + "of any single Aspect. The spire formed will be the chunk's [#](2E8B57)dominant Aspect[#](). After "
                + "the first spire stands, the [#](8B0000)Spiritus Crystal[#]() grows under its own power; the "
                + "Crystallarium has done its work.\\\n\\\n"
                + "A cluster may grow up to [#](B8860B)7 spires[#]() tall.");

        this.page("crystal_growth", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spire Growth");
        this.pageText("Spires grow whenever the chunk holds at least a trace of the cluster's Aspect; growth "
                + "scales with saturation, accelerating as the Aura fills. A chunk's natural cap is "
                + "[#](B8860B)100[#]() per Aspect, though certain Rituals can raise it.\\\n\\\n"
                + "Each new spire costs [#](B8860B)45 Spiritus[#]() when the chunk's dominant Aspect matches the "
                + "cluster, and yields 50 when burned in a Spiritus Crucible, a net gain of 5 per spire. If the "
                + "chunk's dominant Aspect [#](2E8B57)does not match[#]() the cluster (for instance, a Raw chunk "
                + "feeding a Ruina cluster), the cost rises to [#](B8860B)90 Spiritus[#]() per spire and growth "
                + "runs at [#](B8860B)60%% speed[#](). Keep the chunk biased toward the Aspect you want to farm.");

        this.page("harvesting", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("If you carry more than [#](B8860B)512 Spiritus of a single Aspect[#]() (summed across every "
                + "[#](8B0000)Spiritus Gem[#]() you hold), you may harvest a cluster by right-clicking it with an "
                + "empty hand. Each click takes one spire and leaves the cluster standing to regrow.\\\n\\\n"
                + "[#](2E8B57)Mining a cluster does not yield shards; it returns the cluster block itself, which "
                + "replants at a single spire.[#]()");

        this.page("related", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Each harvested shard is worth [#](B8860B)50 Spiritus[#]() in a Spiritus Crucible against the "
                + "[#](B8860B)45[#]() it cost to grow, a net gain of five per spire. To industrialise crystal "
                + "production, build the [#](8B0000)Crystallum Fractura[#]() ritual: it auto-harvests every fully-grown "
                + "cluster in range, doubles crystal growth speed, and amplifies any Spiritus injection by +25%%. "
                + "To bootstrap aspected lineages, apply a [#](8B0000)Spiritus Catalyst[#]() (one per aspect) to a "
                + "fully-grown Raw cluster; the catalyst consumes one [#](8B0000)Animus Mote[#]() and transmutes the "
                + "cluster into its target Aspect. See the Spiritus Catalysts entry for the full loop.");
    }

    @Override
    protected String entryName() {
        return "Crystallized Spiritus";
    }

    @Override
    protected String entryDescription() {
        return "Condensing raw malice from the Aura into physical crystal.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get());
    }

    @Override
    protected String entryId() {
        return "crystallized_spiritus";
    }
}
