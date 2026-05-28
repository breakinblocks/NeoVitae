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
import com.breakinblocks.neovitae.datagen.book.page.BookHellfireForgeRecipePageModel;
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
        this.pageText("Every practitioner of [#](4A0080)Vitaemancy[#]() must begin here, at the [#](8B0000)Ara Vitae[#]() "
                + ", the Altar of Life. This sacred basin transmutes raw blood into [#](4A0080)Essentia Vitae[#](), "
                + "the refined lifeforce that fuels all vitaemantic works. It begins as a humble stone vessel, "
                + "but do not be deceived: as your mastery deepens, the Ara Vitae grows into a towering monument "
                + "of rune-carved power.");

        this.page("crafting", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ara_vitae")));

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Once placed, the [#](8B0000)Ara Vitae[#]() awakens. It drinks the blood offered to it "
                + "and distills it into [#](4A0080)Essentia Vitae[#](), which it then uses to [#](4A0080)transfigure[#]() items "
                + "placed within its basin. Right-click the Ara Vitae to lay an item upon it; right-click with an "
                + "empty hand to retrieve it.");

        this.page("tier1", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_one"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 0 Ara Vitae");
        this.pageText("The simplest form. A lone Ara Vitae, unadorned by runes.");

        this.page("knife", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("To feed the Ara Vitae, you must first forge a [#](8B0000)Sacrificial Knife[#](). By drawing the blade "
                + "across your own flesh (right-click while aiming at air), you spill one heart's worth of blood "
                + "into a nearby [#](8B0000)Ara Vitae[#](), yielding [#](8B0000)200 Essentia Vitae[#]().\\\n\\\n"
                + "The basin holds [#](8B0000)10,000 EV[#]() at first. Watch the crimson pool within; its level "
                + "reveals how full the vessel is. A [#](8B0000)Divination Sigil[#]() reveals the exact figures.");

        this.page("knife_recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "sacrificial_dagger"))
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Be aware:[#]() 10%% of the Ara Vitae's total capacity seeps into a hidden internal reservoir, "
                + "used for fluid transfer operations. If your numbers seem off, this unseen vessel is likely the cause.");

        this.page("crafting_process", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The moment an item is laid upon the Ara Vitae, the [#](4A0080)transmutation[#]() begins. "
                + "[#](4A0080)Essentia Vitae[#]() drains steadily from the basin; crimson motes rising from the surface "
                + "confirm the process is underway.\\\n\\\nShould the Ara Vitae run dry, gray smoke rises as the working "
                + "unravels and progress is lost. Once sufficient EV has been consumed (cost multiplied by the "
                + "stack size), the full stack is transfigured into something new.");

        this.page("first_craft", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Your first creation should be the [#](8B0000)Novicius Orb of Vitae[#](), a diamond offered to "
                + "a Tier 0 altar along with [#](8B0000)2,000 EV[#](). This orb is the key to your "
                + "[#](4A0080)Anima[#](), the invisible network that binds your soul to all your vitaemantic instruments. "
                + "Consult JEI for all recipes the Ara Vitae can perform.");

        this.page("blank_rune", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("To ascend to greater tiers, you must inscribe [#](8B0000)Blood Runes[#]() and arrange them "
                + "around the Ara Vitae in precise patterns. Specialized runes confer different blessings upon the "
                + "altar's workings. The most basic, the [#](8B0000)Blank Rune[#](), carries no enchantment of its own, "
                + "serving only as structural scaffolding for the Ara Vitae's ascension.");

        this.page("blank_rune_recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "rune_blank")));

        this.page("tier2_text", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](B8860B)Tier 1[#]() demands 8 [#](8B0000)Blood Runes[#]() arranged in a ring around the Ara Vitae. "
                + "The four cardinal runes may be replaced with specialized variants; the corner runes remain "
                + "inert until Tier 2 unlocks their potential.");

        this.page("tier2", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_two"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 1 Ara Vitae");
        this.pageText("Eight runes encircle the basin. The Ara Vitae stirs with new hunger.");

        this.page("tier3_text", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](B8860B)Tier 2[#]() expands the structure outward: 5 runes along each edge, set one level "
                + "down and two blocks out from the previous ring. Stone pillars rise at each corner, capped with "
                + "[#](8B0000)Blood Stained Glass[#](), light to guide the essence. Use a [#](8B0000)Divination Sigil[#]() to confirm "
                + "the upgrade. Any solid block suffices for the pillar bodies.");

        this.page("tier3", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_three"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 2 Ara Vitae");
        this.pageText("28 total runes. The altar's appetite deepens.");

        this.page("tier4_text", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](B8860B)Tier 3[#]() demands 7 runes per edge, another level down and two blocks further out. "
                + "Four-block pillars rise at each corner, crowned with [#](8B0000)Bloodstone Bricks[#](), for which you "
                + "will need [#](8B0000)Tau Fruit[#](), harvested from beyond the [#](4A0080)Breaching the Edge of Demon Realm[#]() ritual.");

        this.page("tier4", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_four"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 3 Ara Vitae");
        this.pageText("56 total runes. The structure groans with barely-contained power.");

        this.page("tier5_text", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](B8860B)Tier 4[#]() calls for 13 runes per edge, three blocks out from the previous ring, "
                + "with a one-block gap at each end. [#](8B0000)Hellforged Blocks[#]() anchor the corners, rare metal "
                + "wrested from the [#](4A0080)Demon Realm[#]() itself. The Ara Vitae now commands fearsome energies.");

        this.page("tier5", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_five"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 4 Ara Vitae");
        this.pageText("108 total runes. Few Vitaemancers dare reach this height.");

        this.page("tier6_text", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](B8860B)Tier 5[#](), the apex of the art. 19 runes per edge sprawl outward, three blocks "
                + "beyond the last ring. No corner blocks at rune level; instead, pillars ascend one tier higher, "
                + "crowned with [#](8B0000)Crystal Clusters[#]() that sing with resonant energy. A monument to mastery."
                + "\\\n\\\nForge Crystal Clusters in the [#](8B0000)Hellfire Forge[#]() from [#](8B0000)Sculk[#](), "
                + "an [#](8B0000)Tabula Aetherea[#](), a [#](8B0000)Weak Blood Shard[#](), and a [#](8B0000)Nether Star[#](). "
                + "[#](8B0000)Crystal Cluster Bricks[#]() also serve as valid capstones.");

        this.page("tier6_recipe", () -> BookHellfireForgeRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "hellfire_forge/crystal_cluster")));

        this.page("tier6", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_six"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 5 Ara Vitae");
        this.pageText("184 total runes. The Ara Vitae becomes a cathedral of blood and stone.");
    }

    @Override
    protected String entryName() {
        return "The Ara Vitae";
    }

    @Override
    protected String entryDescription() {
        return "The sacred Ara Vitae where blood becomes power, growing through six tiers of mastery.";
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
