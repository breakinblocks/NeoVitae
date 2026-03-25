package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class HydrationEntry extends EntryProvider {

    public HydrationEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("ARC Hydration");
        this.pageText("With a Primitive Hydration Cell, the ARC can aggressively hydrate and moisturize all sorts "
                + "of substances. Don't forget to supply it with Water!\\\n\\\n"
                + "As with most tools used within the ARC, Hydration Cells degrade over time, but can be enchanted "
                + "with unbreaking or mending to compensate.");

        this.page("recipe_hydration", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "primitive_hydration_cell"))
                .withText(this.context().pageText()));
        this.pageText("Among other things, the Hydration Cell can be used to make Clay, the Cornerstone of Balance.");

        this.page("clay", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Clay and Terracotta");
        this.pageText("ARC Hydration Recipes:\n"
                + "- Clay from Sand (requires Water)\n"
                + "- Clay from Terracotta (requires Water)");

        this.page("dye_removal", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Removing Dye");
        this.pageText("The Hydration Cell can wash dye from various blocks:\n"
                + "- Beds (any color to white)\n"
                + "- Wool (any color to white)\n"
                + "- Carpet (any color to white)\n"
                + "- Glass (stained to clear)\n"
                + "- Glass Panes (stained to clear)\\\n\\\n"
                + "All dye removal recipes require Water.");

        this.page("mud", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dirt and Tau");
        this.pageText("Additional Hydration recipes:\n"
                + "- Dirt into Mud (requires Water)\n"
                + "- Saturated Tau into Weak Blood Shard (requires Life Essence)");

        this.page("concrete", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Concrete Production");
        this.pageText("All 16 colors of Concrete Powder can be solidified into Concrete using the Hydration Cell "
                + "with Water. This is a convenient alternative to the usual water-placement method:\\\n\\\n"
                + "White, Light Gray, Gray, Black, Brown, Red, Orange, Yellow, Lime, Green, Cyan, Light Blue, "
                + "Blue, Purple, Magenta, and Pink.");

        this.page("moss", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spreading Moss");
        this.pageText("The Hydration Cell can spread moss to various stone blocks:\n"
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
        this.pageTitle("Oxidising Copper");
        this.pageText("The Hydration Cell can accelerate copper oxidation through each stage:\n"
                + "- Copper Block to Exposed, Exposed to Weathered, Weathered to Oxidized\n"
                + "- Cut Copper through all oxidation stages\n"
                + "- Cut Copper Stairs through all oxidation stages\n"
                + "- Cut Copper Slab through all oxidation stages\\\n\\\n"
                + "All copper oxidation recipes require Water.");
    }

    @Override
    protected String entryName() {
        return "ARC Hydration";
    }

    @Override
    protected String entryDescription() {
        return "Hydration Cell recipes for the ARC.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
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
