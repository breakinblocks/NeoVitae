package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class AnimaEntry extends EntryProvider {

    public AnimaEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Anima");
        this.pageText("Beneath the surface of the world lies an invisible lattice of living energy, your "
                + "[#](4A0080)Anima[#](). This is the network that binds your soul to every sigil you wield, every "
                + "ritual you ignite, every vitaemantic instrument you have ever touched. Think of it as a vast, "
                + "hidden reservoir of [#](8B0000)Essentia Vitae[#](), life-force refined and stored beyond the physical realm, "
                + "unique to you alone.");

        this.page("binding", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("When you first grip a bindable item and will it to life, it imprints upon your "
                + "[#](4A0080)Anima[#](). From that moment forward, any [#](8B0000)Essentia Vitae[#]() cost the item demands is drawn "
                + "from your network. Should the [#](4A0080)Anima[#]() run dry, some items will take payment directly "
                + "from your flesh instead.\\\n\\\nOther workings, such as a hungry [#](8B0000)Ritual[#]() that has "
                + "drained you completely, inflict unceasing nausea until the ritual is silenced or your "
                + "[#](4A0080)Anima[#]() is replenished.");

        this.page("filling", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("To fill your [#](4A0080)Anima[#](), you require an [#](8B0000)Orb of Vitae[#](), a crystallized anchor "
                + "between your soul and the altar's power."
                + "\n\n- Right-click with the orb in hand to sacrifice one heart, channeling [#](8B0000)200 EV[#]() "
                + "into your [#](4A0080)Anima[#]()."
                + "\n\n- Place the orb within an [#](8B0000)Ara Vitae[#]() brimming with [#](4A0080)Essentia Vitae[#](). "
                + "The orb drinks deeply, limited only by your altar's [#](8B0000)Speed Runes[#]().");

        this.page("orb_tiers", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Each tier of the [#](8B0000)Ara Vitae[#]() unlocks a more potent orb, expanding the boundaries "
                + "of your [#](4A0080)Anima[#]()."
                + "\n\n- [#](B8860B)Novicius Orb of Vitae[#](): Capacity: [#](8B0000)5,000 EV[#]()."
                + "\n\n- [#](B8860B)Discipulus Orb of Vitae[#](): Capacity: [#](8B0000)25,000 EV[#]()."
                + "\n\n- [#](B8860B)Veneficus Orb of Vitae[#](): Capacity: [#](8B0000)150,000 EV[#]()."
                + "\n\n- [#](B8860B)Magus Orb of Vitae[#](): Capacity: [#](8B0000)1,000,000 EV[#]()."
                + "\n\n- [#](B8860B)Dominus Orb of Vitae[#](): Capacity: [#](8B0000)10,000,000 EV[#]().");

        this.page("weak_apprentice", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Orb of Vitae Recipes");
        this.pageText("Forge the [#](8B0000)Novicius Orb of Vitae[#]() in the Ara Vitae ([#](B8860B)Tier 1[#](), cost: 2,000 EV)."
                + "\\\n\\\nForge the [#](8B0000)Discipulus Orb of Vitae[#]() in the Ara Vitae ([#](B8860B)Tier 2[#](), cost: 5,000 EV).");

        this.page("magician_master", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Forge the [#](8B0000)Veneficus Orb of Vitae[#]() in the Ara Vitae ([#](B8860B)Tier 3[#](), cost: 25,000 EV)."
                + "\\\n\\\nForge the [#](8B0000)Magus Orb of Vitae[#]() in the Ara Vitae ([#](B8860B)Tier 4[#](), cost: 50,000 EV).");

        this.page("archmage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Forge the [#](8B0000)Dominus Orb of Vitae[#]() in the Ara Vitae ([#](B8860B)Tier 5[#](), cost: 80,000 EV)."
                + "\\\n\\\n[#](2E8B57)Should even this not sate your ambitions, the Runes of the Orb can stretch your "
                + "Anima further still.[#]()");

        this.page("orb_fluid_tank", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Orb's Inner Reservoir");
        this.pageText("Each [#](8B0000)Orb of Vitae[#]() harbours a hidden internal reservoir of "
                + "[#](4A0080)Essentia Vitae[#](). When the altar fills your [#](4A0080)Anima[#](), "
                + "a portion of that EV seeps into the orb's reservoir as well."
                + "\\\n\\\nWhen you place an orb containing stored fluid back upon an "
                + "[#](8B0000)Ara Vitae[#](), the altar reverses the flow. It drains the orb's "
                + "reservoir at [#](B8860B)10 times[#]() the normal fill rate, rapidly refilling "
                + "the basin. If the altar is nearly full, overflow is channeled "
                + "into your [#](4A0080)Anima[#]() instead. Once the orb is empty, "
                + "normal behavior resumes and the altar fills your network as usual.");
    }

    @Override
    protected String entryName() {
        return "The Anima";
    }

    @Override
    protected String entryDescription() {
        return "The invisible reservoir of EV that binds your soul to all vitaemantic works.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.ORB_WEAK.get());
    }

    @Override
    protected String entryId() {
        return "anima";
    }
}
