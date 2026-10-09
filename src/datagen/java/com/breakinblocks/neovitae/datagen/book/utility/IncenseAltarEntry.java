package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;

public class IncenseAltarEntry extends EntryProvider {

    public IncenseAltarEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Incense Altar");
        this.pageText("The [#](8B0000)Incense Altar[#]() is a meditative focus - a multiblock apparatus that "
                + "stills the mind and opens the veins. When you stand within its calming aura and draw your blade, "
                + "the surrounding [#](4A0080)Tranquility[#]() deepens the sacrifice, multiplying the "
                + "[#](4A0080)Essentia Vitae[#]() gained from self-offering.");

        this.page("recipe_incense", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Consult JEI for the [#](8B0000)Incense Altar[#]() recipe.");

        this.page("basic_setup", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Constructing the Focus");
        this.pageText("[#](2E8B57)Place the altar upon a 3x3 platform of solid blocks (this foundation is important "
                + "for later expansion). Remain within five blocks of the altar to receive its blessing.[#]()\\\n\\\n"
                + "When the altar awakens, flame particles dance from its crown and a "
                + "[#](8B0000)Blessed Sacrifice[#]() settles upon you, lasting thirty seconds. While it holds, the next "
                + "time you right-click your [#](8B0000)Orb of Vitae[#]() you spill a single, potent offering of nearly all "
                + "your blood into the orb, then fall into [#](8B0000)Soul Fray[#]() until you may be blessed again.");

        this.page("setup_image", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Foundation Layout");
        this.pageText("[Image: Basic setup showing the 3x3 square of blocks before the path blocks.]");

        this.page("tranquility", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Weight of Tranquility");
        this.pageText("The strength of your enhanced sacrifice scales with the area's total "
                + "[#](4A0080)Tranquility[#](). Hover over the Incense Altar with a "
                + "[#](8B0000)Divination Sigil[#]() or [#](8B0000)Seer's Sigil[#]() to reveal two readings: "
                + "the total Tranquility above, and the percentage bonus below.\\\n\\\n"
                + "When you sacrifice, your normal yield is multiplied by (1 + bonus/100).");

        this.page("hud_image", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Reading the Signs");
        this.pageText("[Image: Incense HUD, default in top left corner, showing a self-sacrifice bonus of +20%%.]");

        this.page("paths_intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Walking Paths of Peace");
        this.pageText("To deepen the area's [#](4A0080)Tranquility[#](), lay four paths of consecrated "
                + "[#](8B0000)Path blocks[#]() outward from the 3x3 foundation, one toward each cardinal direction, "
                + "each three blocks wide.\\\n\\\n"
                + "Every step outward along the paths is a [#](4A0080)ring[#](): the square two blocks from the altar "
                + "is the first ring, three blocks out the second, and so on. Only the path blocks are required, "
                + "three on each side, twelve per ring. The rest of each square is yours to fill.");

        this.page("path_wood", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath("neovitae", "path/path_wood_brick"))
                .withRecipeId2(Identifier.fromNamespaceAndPath("neovitae", "path/path_wood_tile")));

        this.page("path_stone", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath("neovitae", "path/path_stone_brick"))
                .withRecipeId2(Identifier.fromNamespaceAndPath("neovitae", "path/path_stone_tile")));

        this.page("path_wornstone", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath("neovitae", "path/path_worn_stone_brick"))
                .withRecipeId2(Identifier.fromNamespaceAndPath("neovitae", "path/path_worn_stone_tile")));

        this.page("path_obsidian", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath("neovitae", "path/path_obsidian_brick"))
                .withRecipeId2(Identifier.fromNamespaceAndPath("neovitae", "path/path_obsidian_tile")));

        this.page("path_rules", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Laws of the Path Rings");
        this.pageText("Each ring obeys ancient geometric constraints:\n\n"
                + "- The twelve path blocks of a ring must share the same elevation.\n"
                + "- A ring may sit up to five blocks higher or lower than the ring inside it.\n"
                + "- A ring counts only when every ring inside it is complete.\n"
                + "- Every block on a counted ring's square, from path level to two blocks above it, adds its "
                + "[#](4A0080)Tranquility[#]() to the total.");

        this.page("path_distance", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Path Reach");
        this.pageText("Each path material reaches only so far: [#](8B0000)Wooden[#]() paths serve the first "
                + "three rings, [#](8B0000)Stone[#]() paths the first five, [#](8B0000)Worn Stone[#]() paths the first seven, "
                + "and [#](8B0000)Obsidian[#]() paths all eleven. Stronger paths work in any nearer ring, so the "
                + "materials may be mixed.\\\n\\\n"
                + "Not every block contributes Tranquility. You require growing things, earth, "
                + "and even... lava?");

        this.page("tranquility_types", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sources of Tranquility");
        this.pageText("Seven categories of [#](4A0080)Tranquility[#]() exist: "
                + "[#](2E8B57)Plants, Crops, Trees, Earthen, Water, Fire, and Lava[#]().\\\n\\\n"
                + "The altar surveys all blocks within its range, tallies each type, "
                + "then takes the square root of each category's total and sums them. "
                + "Diversity, not mere volume, is the key to great Tranquility.");

        this.page("tranquility_blocks", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Contributing Blocks");
        this.pageText("Blocks that resonate with [#](4A0080)Tranquility[#](): "
                + "Lava, Water (including most waterlogged blocks), Essentia Vitae, "
                + "Netherrack, Dirt, Farmland, Potatoes, Carrots, Wheat, Nether Wart, "
                + "Beetroots, Leaves, Logs, Fire, and Grass.\\\n\\\n"
                + "[#](2E8B57)For advanced setups, cultivate as many different types as possible.[#]()");

        this.page("caps", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ceiling of Serenity");
        this.pageText("The bonus is bounded by how many complete rings surround the altar:\n\n"
                + "- [#](B8860B)No rings:[#]() 20%%\n"
                + "- [#](B8860B)1 to 3 rings:[#]() 60%%\n"
                + "- [#](B8860B)4 to 5 rings:[#]() 120%%\n"
                + "- [#](B8860B)6 to 7 rings:[#]() 200%%\n"
                + "- [#](B8860B)8 to 9 rings:[#]() 300%%\n"
                + "- [#](B8860B)10 or more:[#]() 450%%\\\n\\\n"
                + "[#](2E8B57)Within that bound, Tranquility decides how much of it you receive.[#]()");

        this.page("example_image", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("A Garden of Calm");
        this.pageText("[Image: A simple Incense Altar setup. Note the optional mixing of different path blocks.]");
    }

    @Override
    protected String entryName() {
        return "Incense Altar";
    }

    @Override
    protected String entryDescription() {
        return "A meditative focus that deepens the sacrifice through Tranquility.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.INCENSE_ALTAR.asItem());
    }

    @Override
    protected String entryId() {
        return "incense_altar";
    }
}
