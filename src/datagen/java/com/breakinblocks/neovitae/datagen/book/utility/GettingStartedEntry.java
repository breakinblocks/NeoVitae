package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class GettingStartedEntry extends EntryProvider {

    public GettingStartedEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Path Before You");
        this.pageText("So you wish to walk the path of [#](4A0080)Vitaemancy[#](). Very well. "
                + "This grimoire shall guide you from your first tentative cuts to the mastery of forces "
                + "that reshape reality itself.\\\n\\\n"
                + "What follows is an overview of the journey ahead, tier by tier, so that you may orient yourself "
                + "amid the mysteries to come.");

        this.page("tier1_altar", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Ara Vitae (Tier I)");
        this.pageText("Your first act as a Vitaemancer is to construct an [#](8B0000)Ara Vitae[#]() and a "
                + "[#](8B0000)Sacrificial Knife[#](). With blade in hand, you offer your own blood to the altar, "
                + "and it drinks deeply, refining it into [#](4A0080)Essentia Vitae[#]().\\\n\\\n"
                + "Use this to forge a [#](8B0000)Weak Blood Orb[#](), several [#](8B0000)Blank Slates[#](), "
                + "and a handful of [#](8B0000)Soul Snares[#]().");

        this.page("tier1_alchemy", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Tabula Vitae (Tier I)");
        this.pageText("The [#](8B0000)Tabula Vitae[#]() draws [#](4A0080)Essentia Vitae[#]() from your "
                + "[#](4A0080)Anima[#]() through the Blood Orb placed within, channeling it into a myriad of creations:\n\n"
                + "- [#](8B0000)Arcane Ashes[#]() for array work\n"
                + "- Reagents for Sigil crafting\n"
                + "- Anointments for your blades\n"
                + "- 2x Ore Processing\n"
                + "- Various esoteric sundries");

        this.page("tier1_array", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Alchemy Arrays (Tier I)");
        this.pageText("Scatter [#](8B0000)Arcane Ashes[#]() upon the ground, and a circle of power awakens. "
                + "This is the [#](8B0000)Alchemy Array[#]() - a versatile glyph that accepts two offerings.\\\n\\\n"
                + "Some combinations yield crafted objects, such as the [#](8B0000)Divination Sigil[#](). "
                + "Others invoke immediate effects upon the world, bending time or light to your will.");

        this.page("tier1_forge", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Hellfire Forge (Tier I)");
        this.pageText("The [#](8B0000)Hellfire Forge[#]() is fueled by [#](4A0080)Spiritus[#]() - "
                + "that strange substance torn from slain creatures with your Soul Snares. A "
                + "[#](8B0000)Sentient Sword[#]() harvests it far more efficiently.\\\n\\\n"
                + "From this forge spring [#](8B0000)Spiritus Gems[#](), Sentient Tools, "
                + "Explosive Charges, and Throwing Daggers.");

        this.page("tier2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Second Tier");
        this.pageText("[#](B8860B)Tier II[#]() grants the [#](8B0000)Dagger of Sacrifice[#](), a blade that lets you "
                + "harvest [#](4A0080)Essentia Vitae[#]() from slain creatures, sparing your own veins. "
                + "Upgrade Runes become available for the Ara Vitae, and new Sigils emerge.\\\n\\\n"
                + "As always, continue expanding and upgrading your altar. Its hunger grows with your ambition.");

        this.page("tier2_potions", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Potioncrafting (Tier II)");
        this.pageText("Should the altar's construction grow tiresome, a welcome diversion awaits. "
                + "The potioncrafting system opens at [#](B8860B)Tier II[#](), allowing you to prepare "
                + "[#](8B0000)Alchemy Flasks[#]() or tip your [#](8B0000)Amethyst Throwing Daggers[#]() "
                + "with dozens of potent effects.\\\n\\\n"
                + "A Vitaemancer with a well-stocked flask belt is never truly in danger.");

        this.page("tier3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Third Tier");
        this.pageText("At [#](B8860B)Tier III[#](), the true scope of Vitaemancy reveals itself. "
                + "You gain the ability to inscribe [#](4A0080)Rituals[#]() and forge "
                + "[#](8B0000)Living Armour[#]() - equipment that grows stronger through the trials you endure.\\\n\\\n"
                + "Turn your attention to upgrading both the Ara Vitae and your "
                + "[#](8B0000)Ritual Diviner[#]() to unlock more potent rites. But how?");

        this.page("tier3_dungeon", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dungeoneering (Tier III)");
        this.pageText("By performing the [#](4A0080)Edge of the Hidden Realm[#](), you tear a wound in the veil "
                + "and gain limited passage into the [#](4A0080)Demon Realm[#](). There, among the horrors, "
                + "you may find [#](8B0000)Tau Fruit[#]().\\\n\\\n"
                + "Cultivate it into [#](8B0000)Saturated Tau[#](), then refine it within the Athanor to produce "
                + "[#](8B0000)Weak Blood Shards[#]() - the key to the fourth tier, stronger Anointments, and Potion Catalysts.");

        this.page("tier4", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Fourth Tier");
        this.pageText("[#](B8860B)Tier IV[#]() unlocks the [#](8B0000)Ritual Diviner [Dusk][#](), and with it, "
                + "a vast repertoire of advanced rites. Automate your [#](4A0080)Essentia Vitae[#]() supply, "
                + "soar in unfettered flight within your sanctum, or call down a devastating "
                + "[#](4A0080)meteor[#]() laden with precious bounty from the heavens above.");

        this.page("tier4_armour", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Living Evolution (Tier IV)");
        this.pageText("Your [#](8B0000)Living Armour[#]() may have begun to feel constrained by its modest "
                + "point cap. With the Dusk Diviner, you may now perform the "
                + "[#](4A0080)Ritual of Living Evolution[#](), raising your armour's capacity from "
                + "[#](B8860B)100[#]() to [#](B8860B)300[#]() points.");

        this.page("tier4_armour2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Mastering Upgrades");
        this.pageText("The [#](4A0080)Sound of the Cleansing Soul[#]() strips upgrades from your armour, "
                + "producing [#](8B0000)Upgrade Tomes[#]() that may be copied into a "
                + "[#](8B0000)Training Bracelet[#](), or re-applied to ensure you train only the upgrades you desire.\\\n\\\n"
                + "Surplus tomes serve as fuel for the [#](4A0080)Penance of the Leaden Soul[#](), "
                + "a rite that inscribes [#](4A0080)Downgrades[#]() onto your armour.");

        this.page("tier4_armour3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Price of Power");
        this.pageText("Downgrades are costly, and each exacts a heavy toll upon the body. Yet their negative "
                + "point cost frees room for greater upgrades.\\\n\\\n"
                + "[#](2E8B57)Favour resilience over agility? Try Body Builder V, Brilliance V, Healthy X, "
                + "and Tough X combined with Leadened Pick X and Limp Leg X.[#]()");

        this.page("tier4_armour4", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Many Configurations");
        this.pageText("[#](2E8B57)Prefer speed and exploration? Perhaps Strong Legs X, Quick Feet X, "
                + "and Crippled Arm suit you better.[#]()\\\n\\\n"
                + "Craft multiple chestplates and swap between them as the situation demands. "
                + "Dozens of upgrades and downgrades exist - experiment freely, apprentice. "
                + "The armour responds to your will.");

        this.page("tier4_will", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Aspected Will (Tier IV)");
        this.pageText("Investigate the [#](8B0000)Ritual Tinkerer[#]() and the various "
                + "[#](4A0080)Aspects of Spiritus[#]() now available. These aspects can refine your rituals "
                + "and reshape how your Sentient Tools behave.\\\n\\\n"
                + "To progress beyond this threshold, however, a deeper plunge into the "
                + "[#](4A0080)Demon Realm[#]() awaits...");

        this.page("tier4_demon", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Demon Realm (Tier IV)");
        this.pageText("The [#](4A0080)Pathway to the Endless Realm[#]() tears open a permanent gate into "
                + "the demon dimension. Come armed and armoured - the realm does not suffer the unprepared.\\\n\\\n"
                + "Delve deep enough, and you may unearth [#](8B0000)Demonite Ore[#](), "
                + "which in block form serves as capstone for the ultimate altar tiers, alongside the "
                + "[#](8B0000)Archmage's Blood Orb[#]().");

        this.page("tier5", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Fifth Tier");
        this.pageText("[#](B8860B)Tier V[#]() grants access to the most devastating rituals and the "
                + "most potent crafting recipes in all of Vitaemancy.\\\n\\\n"
                + "Continue your expeditions into the Demon Realm. With fortune, you may recover "
                + "[#](8B0000)Intricate Hellforged Parts[#](), which, combined with Netherite Scrap, "
                + "can double the power of every rune upon your altar.");

        this.page("tier6", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Sixth Tier");
        this.pageText("The [#](B8860B)ultimate Ara Vitae[#](). [#](4A0080)Crystal Clusters[#]() crown its pillars, "
                + "and an immense ring of nineteen runes per side girds its foundation.\\\n\\\n"
                + "At this zenith, the altar's capacity for [#](4A0080)Essentia Vitae[#]() and its crafting "
                + "power are without equal. You stand at the pinnacle of Vitaemancy, apprentice. "
                + "What you do with that power is yours to decide.");
    }

    @Override
    protected String entryName() {
        return "The Path of Ascension";
    }

    @Override
    protected String entryDescription() {
        return "A map of the Vitaemancer's journey, from first blood to final mastery.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.CATEGORY_START;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SACRIFICIAL_DAGGER.get());
    }

    @Override
    protected String entryId() {
        return "getting_started";
    }
}
