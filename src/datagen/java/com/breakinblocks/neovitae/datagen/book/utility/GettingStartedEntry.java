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
        this.pageTitle("Tiers & Getting Started");
        this.pageText("Welcome to Neo Vitae! This guide provides a quick overview of the mod's progression, "
                + "from your first Ara Vitae all the way to the highest tiers of power.");

        this.page("tier1_altar", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ara Vitae (Tier-1)");
        this.pageText("The first step of Neo Vitae is to build a Ara Vitae and Sacrificial Knife. "
                + "Use these to generate Life Essence from Self-Sacrificing.\\\n\\\n"
                + "Use this Life Essence to craft a Weak Blood Orb, several Blank Slates, and a few Soul Snares.");

        this.page("tier1_alchemy", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tabula Vitae (Tier-1)");
        this.pageText("The Tabula Vitae uses LP from a player's Soul Network (drawn from the bound Blood Orb in it) "
                + "to craft various different objects, such as:\n"
                + "- Arcane Ashes\n"
                + "- Reagents for Sigils\n"
                + "- Anointments\n"
                + "- 2x Ore Processing\n"
                + "- and various other odds and ends.");

        this.page("tier1_array", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Alchemy Array (Tier-1)");
        this.pageText("An Alchemy Array is made by placing some Arcane Ashes on the ground. "
                + "The Alchemy Array can have 2 items inserted into it via Use, and will either craft an item "
                + "(such as a Divination Sigil) or perform some kind of function (such as turning day into night).");

        this.page("tier1_forge", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Hellfire Forge (Tier-1)");
        this.pageText("The Hellfire Forge crafts using Demon Will. You get your first Will by using Soul Snares, "
                + "though upgrading to a Sentient Sword is recommended. The Hellfire Forge is used for stuff directly "
                + "related to Demon Will (like Tartaric Gems and Sentient Tools), and consumables (like Explosive "
                + "Charges and Throwing Daggers).");

        this.page("tier2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier Two");
        this.pageText("At this point, you will be able to craft a Lamina Exhauriens in order to slaughter mobs "
                + "for more Life Essence. Various Upgrade Runes should be available for your Ara Vitae, "
                + "and some more Sigils will be available. As before, you should focus on further upgrading your Altar.");

        this.page("tier2_potions", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Potioncrafting (Tier-2)");
        this.pageText("If building your Altar is getting tiresome and you feel in need of a distraction, "
                + "why not try out the newly available Potioncrafting system? You will be able to craft "
                + "Alchemy Flasks or Tipped Amethyst Throwing Daggers and imbue them with literally dozens of effects.");

        this.page("tier3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier Three");
        this.pageText("By this point, you will have the ability to create some basic Rituals and Living Armour as well. "
                + "This armour is very versatile, though you'll have to work hard to unlock its full potential.\\\n\\\n"
                + "At this point you should look into upgrading your Altar and your Ritual Diviner to unlock "
                + "more powerful Rituals. How, you say?");

        this.page("tier3_dungeon", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dungeoneering (Tier-3)");
        this.pageText("By performing the Edge of the Hidden Realm, that's how! This will allow you limited access "
                + "to the Demon Realm, and hopefully to Tau Fruit, which can be cultivated into Saturated Tau and "
                + "then converted into Weak Blood Shards in the Athanor. These can be used to "
                + "make the Tier IV Altar, more powerful Anointments, and Potion Catalysts.");

        this.page("tier4", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier Four");
        this.pageText("At Tier 4, you will gain access to the Ritual Diviner [Dusk], and with it, a plethora "
                + "of more advanced Rituals, allowing such feats as automating your Life Essence supply, "
                + "unlimited creative flight within your base, and even summoning a devastating meteor from "
                + "the heavens, chock full of goodies!");

        this.page("tier4_armour", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Better Armour (Tier-4)");
        this.pageText("You may have tinkered around with Living Armour by this point, but the relatively low cap "
                + "of points may have started to chafe. With the Ritual Diviner [Dusk], you are now free to augment "
                + "your armour like never before. The Ritual of Living Evolution will allow you to raise your points "
                + "cap from 100 to 300.");

        this.page("tier4_armour2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Upgrade Management");
        this.pageText("If that's not enough, you can use the Sound of the Cleansing Soul to strip your armour "
                + "of its upgrades. These tomes can be copied into a Training Bracelet, or re-applied to your "
                + "armour to ensure you only train Upgrades that you actually want.\\\n\\\n"
                + "Any leftover Upgrade Tomes can be kept as fuel for the Penance of the Leaden Soul. "
                + "This ritual allows you to apply Downgrades to your Armour.");

        this.page("tier4_armour3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Downgrade Combos");
        this.pageText("While these are expensive (and each comes with a hefty penalty), their negative point-cost "
                + "will give you more room to further improve your upgrades.\\\n\\\n"
                + "Want to be incredibly tough and healthy, and don't mind being slow? Try combining "
                + "Body Builder V, Brilliance V, Healthy X, and Tough X with Leadened Pick X and Limp Leg X.");

        this.page("tier4_armour4", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Mix and Match");
        this.pageText("Feel more like exploring and going fast? Perhaps you don't care about your offhand? "
                + "Maybe Strong Legs X, Quick Feet X, and Crippled Arm would be more your speed.\\\n\\\n"
                + "Feel like having both at different times? Make multiple chestplates and swap between them! "
                + "There are dozens of upgrades and downgrades, so mix and match to find your favourite combinations.");

        this.page("tier4_will", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Aspected Will (Tier-4)");
        this.pageText("You may want to look into the Ritual Tinkerer and the various kinds of Will Aspects "
                + "available to you, and consider how they may be used to refine your existing rituals and alter "
                + "how your Sentient Tools and Weapons work. To progress beyond this point, however, a delve "
                + "into the Demon Realm will be needed...");

        this.page("tier4_demon", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Demon Realm (Tier-4)");
        this.pageText("By performing the Pathway to the Endless Realm, you can gain access to the Demon Realm "
                + "proper, along with all of its treasures and terrors. Come equipped for a fight! Delve deep, "
                + "and you may find a rich source of Demonite Ore, which can be combined into block form and used "
                + "for the capstones on your altar, along with the Archmage's Blood Orb.");

        this.page("tier5", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier Five");
        this.pageText("At Tier 5, you have access to the most powerful rituals and crafting recipes. "
                + "Continue to delve into the Demon Realm to hunt for rare treasures, and you may be lucky enough "
                + "to find Intricate Hellforged Parts, which can be used alongside some Netherite Scrap to double "
                + "the power of each of your altar's Runes!");

        this.page("tier6", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier Six");
        this.pageText("The ultimate Ara Vitae. Tier 6 requires Crystal Clusters at the peaks of its pillars "
                + "and an enormous ring of 19 runes per side. At this level, the altar reaches its maximum potential "
                + "with unparalleled LP storage and crafting capabilities.");
    }

    @Override
    protected String entryName() {
        return "Tiers & Getting Started";
    }

    @Override
    protected String entryDescription() {
        return "A quick overview of Neo Vitae progression.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.CATEGORY_START;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.LAMINA_MALEFICUS.get());
    }

    @Override
    protected String entryId() {
        return "getting_started";
    }
}
