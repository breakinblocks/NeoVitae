package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;

public class AraVitaeEntry extends EntryProvider {

    public AraVitaeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Ara Vitae");
        this.pageText("The [#](8B0000)Ara Vitae[#]() is the central block of the mod, able to convert raw "
                + "blood into pure [#](8B0000)Life Essence[#](). While it may start off small and insignificant, "
                + "its strength and size grows throughout the mod, acting as a cornerstone for most of your power.");

        this.page("crafting", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ara_vitae")));

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("When placed into the world, the [#](8B0000)Ara Vitae[#]() converts blood into [#](8B0000)Life Essence[#](), "
                + "which it then uses to transfigure items placed into it. By right-clicking while looking at "
                + "the Altar, you may insert one item from your hand into the Altar's internal inventory. "
                + "Right-clicking with an empty hand will extract the item.");

        this.page("tier1", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_one"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 1 Ara Vitae");
        this.pageText("The Tier 1 Ara Vitae, which has no runes.");

        this.page("knife", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("In order to add [#](8B0000)Life Essence[#]() to the Altar, you first have to craft a [#](8B0000)Sacrificial Knife[#](). "
                + "By right-clicking while aiming at air with the knife, you can extract [#](8B0000)200 Life Essence[#]() for the "
                + "cost of one heart, placing it into a nearby Altar.\\\n\\\n"
                + "The Altar starts with a maximum capacity of "
                + "[#](8B0000)10,000 Life Essence[#](), and the blood level in the basin indicates the percentage filled. "
                + "The [#](8B0000)Divination Sigil[#]() allows more detailed information about the Altar.");

        this.page("knife_recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "lamina_maleficus"))
                .withText(this.context().pageText()));
        this.pageText("Keep in mind that 10%% of the total [#](8B0000)Life Essence[#]() the altar can hold will be absorbed "
                + "into an invisible internal 'tank' used for extracting and inserting [#](8B0000)Life Essence[#]() into the Altar.");

        this.page("crafting_process", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The Ara Vitae will attempt to start to craft as soon as an item is placed inside by a "
                + "player (or after a periodic 5 seconds). The [#](8B0000)Life Essence[#]() inside of the Altar will slowly "
                + "drain (indicated by red particles), transforming the item.\\\n\\\n"
                + "If there is no [#](8B0000)Life Essence[#]() in "
                + "the Altar, gray smoke will appear to indicate that the Altar is losing progress instead. Once "
                + "enough [#](8B0000)Life Essence[#]() is consumed (cost multiplied by number in the item stack), the full "
                + "stack will be transformed into a new item.");

        this.page("first_craft", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The first item that you will want to craft is a [#](8B0000)Weak Blood Orb[#](), which by default "
                + "is a diamond plus [#](8B0000)2000 Life Essence[#]() inside of a Tier 1 Ara Vitae. All items that can "
                + "be crafted by the Ara Vitae can be found using Just Enough Items (JEI).");

        this.page("blank_rune", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("To upgrade the Ara Vitae, you need to craft [#](8B0000)Blood Runes[#]() and place them around "
                + "the Altar. Blood Runes act as upgrades to the Altar, and by using more advanced versions "
                + "of the Blood Runes you can confer different effects on the Altar. The basic version, the "
                + "[#](8B0000)Blank Rune[#](), does not give any upgrades - it's only use is to upgrade the Tier of the Altar.");

        this.page("blank_rune_recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "rune_blank")));

        this.page("tier2_text", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("In order to upgrade the Ara Vitae to Tier 2, you must place 8 [#](8B0000)Blood Runes[#]() around "
                + "the Altar. The runes in the cardinals can be upgraded, but the corner runes cannot act as "
                + "upgrade runes until Tier 3.");

        this.page("tier2", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_two"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 2 Ara Vitae");
        this.pageText("The Tier 2 Ara Vitae, which has 8 total runes.");

        this.page("lamina_exhauriens", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Now that you have a Tier 2 Altar, you can look into getting [#](8B0000)Life Essence[#]() from "
                + "somewhere other than yourself. The [#](8B0000)Lamina Exhauriens[#]() will allow you to sacrifice any "
                + "mob (monster or passive) that stands within 2 blocks of your Altar, instantly killing them "
                + "and granting you a decent sum of [#](8B0000)Life Essence[#]().\\\n\\\n"
                + "You can increase the amount you get per "
                + "kill with [#](8B0000)Runes of Sacrifice[#](). Different entities give different amounts of [#](8B0000)Life Essence[#]().");

        this.page("dagger_recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Lamina Exhauriens");
        this.pageText("Craft the Lamina Exhauriens in the Ara Vitae (Tier 2, cost: 3000 LP).\\\n\\\n"
                + "Slaughtering villagers for fun and profit!");

        this.page("tier3_text", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("To upgrade the Ara Vitae to Tier 3, place 5 [#](8B0000)Blood Runes[#]() one block down and two "
                + "blocks away from the previous set of runes along each edge. Then place two blocks (indicated "
                + "by the [#](8B0000)Stone Bricks[#]()) in each corner, starting above the new ring of runes, and then cap "
                + "each pillar with [#](8B0000)Glowstone Blocks[#]().\\\n\\\nTo check that it is successfully upgraded, use a "
                + "[#](8B0000)Divination Sigil[#]() to check the tier. Note that any non-air block can be used for the "
                + "pillars below the Glowstone.");

        this.page("tier3", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_three"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 3 Ara Vitae");
        this.pageText("The Tier 3 Ara Vitae, which has 28 total runes, 20 more than a Tier 2, 5 on each side.");

        this.page("tier4_text", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("To upgrade the Ara Vitae to Tier 4, place 7 [#](8B0000)Blood Runes[#]() one block down and two "
                + "blocks away from the previous set of runes along each edge. Then place four solid blocks in "
                + "each corner, starting above the new ring of runes, and then cap each pillar with [#](8B0000)Bloodstone "
                + "Bricks[#](8B0000) and/or [#]()Large Bloodstone Bricks[#](8B0000). For these, you'll need [#]()Tau Fruit[#](), found via "
                + "the [#](8B0000)Edge of the Hidden Realm[#]() ritual.");

        this.page("tier4", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_four"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 4 Ara Vitae");
        this.pageText("The Tier 4 Ara Vitae, which has 56 total runes, 28 more than a Tier 3, 7 on each side.");

        this.page("tier5_text", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("To upgrade the Ara Vitae to Tier 5, place 13 [#](8B0000)Blood Runes[#]() one block down and three "
                + "blocks away from the previous set of runes along each edge. Leave a one-block gap on either "
                + "end, then place a [#](8B0000)Hellforged Block[#]() at each corner. You'll have to go delving deep into "
                + "the [#](8B0000)Demon Realm[#]() for this rare and exotic metal.");

        this.page("tier5", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_five"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 5 Ara Vitae");
        this.pageText("The Tier 5 Ara Vitae, which has 108 total runes, 52 more than a Tier 4, 13 on each side.");

        this.page("tier6_text", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("To upgrade the Ara Vitae to Tier 6, place 19 [#](8B0000)Blood Runes[#]() one block down and three "
                + "blocks away from the previous set of runes along each edge. Leave a one-block gap on either "
                + "end, but do not place corner blocks at this level. Instead, build pillars at each corner "
                + "starting one level up, topped with [#](8B0000)Crystal Clusters[#]() at the very top.");

        this.page("tier6", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_six"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 6 Ara Vitae");
        this.pageText("The Tier 6 Ara Vitae, which has 184 total runes, 76 more than a Tier 5, 19 on each side.");
    }

    @Override
    protected String entryName() {
        return "The Ara Vitae";
    }

    @Override
    protected String entryDescription() {
        return "The central crafting station and its multiblock tiers.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.ARA_VITAE.asItem());
    }

    @Override
    protected String entryId() {
        return "ara_vitae";
    }
}
