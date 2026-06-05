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

public class BloodstoneBricksEntry extends EntryProvider {

    public BloodstoneBricksEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Bloodstone Bricks");
        this.pageText("[#](8B0000)Bloodstone Bricks[#]() are stone saturated with crystallized life force, "
                + "dark and warm to the touch. Beyond their unsettling beauty, they serve as the "
                + "[#](B8860B)capstones for the Tier III Ara Vitae[#]().\\\n\\\n"
                + "Their creation requires [#](8B0000)Weak Blood Shards[#](), obtained by placing "
                + "[#](8B0000)Saturated Tau[#]() into the Athanor with a Sanguine Reverter.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath("neovitae", "bloodstone_brick")));
    }

    @Override
    protected String entryName() {
        return "Bloodstone Bricks";
    }

    @Override
    protected String entryDescription() {
        return "Stone steeped in life force - decorative, and essential for the fourth Ara Vitae tier.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.BLOODSTONE_BRICK.asItem());
    }

    @Override
    protected String entryId() {
        return "bloodstone_bricks";
    }
}
