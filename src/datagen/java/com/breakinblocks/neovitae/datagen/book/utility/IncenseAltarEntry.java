package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class IncenseAltarEntry extends EntryProvider {

    public IncenseAltarEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Incense Altar");
        this.pageText("The **Incense Altar** is a multiblock structure that can be used to boost your self-sacrificing "
                + "at a Blood Altar. By standing near your setup, the Incense Altar will calm your soul based on "
                + "the area's total Tranquility, allowing you to significantly increase your LP gains.");

        this.page("recipe_incense", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "incense_altar")));

        this.page("basic_setup", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Basic Setup");
        this.pageText("The basic Tier 1 setup of an Incense Altar is the Altar itself; place it down anywhere "
                + "(you may want to set up a 3x3 block platform, this will be helpful later) and stay within a "
                + "5 block radius of the Altar.\n\n"
                + "While the Altar is working, it will emit flame particles from its top and transform your "
                + "Sacrificial Knife. Once your knife starts to shine, holding and releasing right click near a "
                + "Blood Altar will sacrifice 90% of your health all at once.");

        this.page("setup_image", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Basic Setup Layout");
        this.pageText("[Image: Basic setup showing the 3x3 square of blocks before the path blocks.]");

        this.page("tranquility", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tranquility Bonus");
        this.pageText("Based on the total Tranquility of the area, you will receive a bonus to the LP added to "
                + "the Blood Altar. Hovering over the Incense Altar with either a Divination Sigil or Seer's Sigil "
                + "will display the total Tranquility (top number) and the percentage bonus received when sacrificing "
                + "(bottom number). When you sacrifice, it will take the LP that you would normally get and multiply "
                + "it by (1 + bonus/100).");

        this.page("hud_image", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Incense HUD");
        this.pageText("[Image: Incense HUD, default in top left corner, showing a self-sacrifice bonus of +20%.]");

        this.page("paths_intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Increasing Tranquility");
        this.pageText("To increase the Tranquility of the area, you must place paths leading out from your Incense "
                + "Altar. These paths need to be constructed from a three wide set of Path blocks, such as the "
                + "Wooden Path, that extend from the 3x3 set of solid reference blocks in all four cardinal directions.");

        this.page("path_wood", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "path/path_wood"))
                .withRecipeId2(ResourceLocation.fromNamespaceAndPath("neovitae", "path/path_woodtile")));

        this.page("path_stone", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "path/path_stone"))
                .withRecipeId2(ResourceLocation.fromNamespaceAndPath("neovitae", "path/path_stonetile")));

        this.page("path_wornstone", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "path/path_wornstone"))
                .withRecipeId2(ResourceLocation.fromNamespaceAndPath("neovitae", "path/path_wornstonetile")));

        this.page("path_obsidian", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "path/path_obsidian"))
                .withRecipeId2(ResourceLocation.fromNamespaceAndPath("neovitae", "path/path_obsidiantile")));

        this.page("path_rules", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Path Ring Rules");
        this.pageText("Each new \"ring\" of path blocks follow a set of rules:\n"
                + "- All path blocks on the same ring have to be on the same y-level.\n"
                + "- The next ring of path blocks may not be more than 5 blocks higher/lower than the previous ring.\n"
                + "- The blocks that are the same level or up to two blocks above the path blocks' ring count "
                + "towards the total Tranquility.");

        this.page("path_distance", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Path Distance");
        this.pageText("The efficacy of each type of path block only lasts a certain distance: wooden paths can "
                + "only go three rings out from the centre, stone paths for up to five rings, worn stone paths "
                + "for seven rings, and obsidian paths for nine rings.\n\n"
                + "Not every type of block will count towards your Tranquility. We need crops, dirt, and even... lava?");

        this.page("tranquility_types", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tranquility Types");
        this.pageText("There are multiple block categories that count towards the total Tranquility: Plants, Crops, "
                + "Trees, Earthen, Water, Fire, and Lava. The Incense Altar will look at all of the blocks within "
                + "its range and tabulate the total Tranquility of each type. It then calculates the total by "
                + "square-rooting the Tranquility of each type and adding them together.");

        this.page("tranquility_blocks", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tranquility Blocks");
        this.pageText("For later-game setups, it is best to have many different types of Tranquility. "
                + "The blocks that contribute are: Lava, Water (including most Waterlogged blocks), Life Essence, "
                + "Netherrack, Dirt, Farmland, Potatoes, Carrots, Wheat, Nether Wart, Beetroots, Leaves, Logs, "
                + "Fire, and Grass.");

        this.page("caps", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tranquility Caps");
        this.pageText("The Tranquility bonus is capped by the size of your Altar (and thus, the tier of path "
                + "you are using). The caps are as follows:\n"
                + "- No Path: 20%\n"
                + "- Wooden Path: 60%\n"
                + "- Stone Path: 120%\n"
                + "- Worn Stone Path: 200%\n"
                + "- Obsidian Path: 300%");

        this.page("example_image", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Example Setup");
        this.pageText("[Image: A simple Incense Altar setup. Note the optional mixing of different path blocks.]");
    }

    @Override
    protected String entryName() {
        return "Incense Altar";
    }

    @Override
    protected String entryDescription() {
        return "Boost self-sacrifice LP gains with Tranquility.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
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
