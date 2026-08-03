package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;

public class HydrationEntry extends EntryProvider {

    public HydrationEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Hydration Cell");
        this.pageText("Fitted with a [#](8B0000)Primitive Hydration Cell[#](), the Athanor becomes a font of "
                + "elemental water, saturating and transforming matter at the molecular level. "
                + "Ensure you supply it with Water, or the cell thirsts in vain.\\\n\\\n"
                + "[#](2E8B57)Hydration Cells degrade with use, but Unbreaking or Mending enchantments "
                + "extend their service considerably.[#]()");

        this.page("recipe_hydration", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath("neovitae", "primitive_hydration_cell"))
                .withText(this.context().pageText()));
        this.pageText("Among its many uses, the Hydration Cell can produce Clay - the [#](4A0080)Cornerstone of Balance[#]().");

        this.page("clay", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Clay and Terracotta");
        this.pageText("The cell's waters can coax clay from the driest materials:\n\n"
                + "- Sand to Clay (requires Water)\n"
                + "- Terracotta to Clay (requires Water)");

        this.page("dye_removal", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Washing Away Color");
        this.pageText("The Hydration Cell's waters can strip dye from woven and vitreous materials:\n\n"
                + "- Beds (any color to white)\n"
                + "- Wool (any color to white)\n"
                + "- Carpet (any color to white)\n"
                + "- Glass (stained to clear)\n"
                + "- Glass Panes (stained to clear)\\\n\\\n"
                + "All dye removal requires Water.");

        this.page("mud", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Earth and Essence");
        this.pageText("Further transmutations the cell enables:\n\n"
                + "- Dirt to Mud (requires Water)\n"
                + "- [#](8B0000)Saturated Tau[#]() to [#](8B0000)Weak Blood Shard[#]() "
                + "(requires [#](4A0080)Essentia Vitae[#]())");

        this.page("concrete", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Setting Concrete");
        this.pageText("All sixteen hues of Concrete Powder can be solidified into Concrete within the Athanor "
                + "using the Hydration Cell and Water - a far tidier method than dumping powder into a stream.\\\n\\\n"
                + "White, Light Gray, Gray, Black, Brown, Red, Orange, Yellow, Lime, Green, Cyan, Light Blue, "
                + "Blue, Purple, Magenta, and Pink.");

        this.page("moss", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Cultivating Moss");
        this.pageText("The cell's persistent moisture encourages moss growth upon worked stone:\n\n"
                + "- Cobblestone to Mossy Cobblestone\n"
                + "- Cobblestone Stairs to Mossy Cobblestone Stairs\n"
                + "- Cobblestone Slab to Mossy Cobblestone Slab\n"
                + "- Cobblestone Wall to Mossy Cobblestone Wall\n"
                + "- Stone Bricks to Mossy Stone Bricks\n"
                + "- Stone Brick Stairs to Mossy Stone Brick Stairs\n"
                + "- Stone Brick Slab to Mossy Stone Brick Slab\n"
                + "- Stone Brick Wall to Mossy Stone Brick Wall");

        this.page("copper", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Hastening Patina");
        this.pageText("The cell accelerates the slow breath of oxidation upon copper, advancing it through each stage:\n\n"
                + "- Copper Block to Exposed, Exposed to Weathered, Weathered to Oxidized\n"
                + "- Cut Copper through all oxidation stages\n"
                + "- Cut Copper Stairs through all stages\n"
                + "- Cut Copper Slab through all stages\\\n\\\n"
                + "All copper oxidation requires Water.");
    }

    @Override
    protected String entryName() {
        return "Athanor Hydration";
    }

    @Override
    protected String entryDescription() {
        return "The Hydration Cell saturates, dissolves, and transforms within the Athanor.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.PRIMITIVE_HYDRATION_CELL.get());
    }

    @Override
    protected String entryId() {
        return "hydration";
    }
}
