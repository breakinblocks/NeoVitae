package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class EnchantFilterEntry extends EntryProvider {

    public EnchantFilterEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Enchantments Item Filter");
        this.pageText("The [#](8B0000)Enchantments Item Filter[#]() lets you sort items via any [#](8B0000)Enchantments[#]() that they "
                + "may (or may not) have. It operates similarly to the [#](8B0000)Standard Item Filter[#](), particularly "
                + "with regards to the quantity and allow/deny functions, but with a few extra buttons.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Enchantments Item Filter");
        this.pageText("Craft the Enchantments Item Filter in the Alchemy Table. Any enchanted book will work "
                + "for this recipe.");

        this.page("gui_image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/enchant_item_filter_gui.png"))
                .withTitle("Enchantments Filter GUI")
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("Note the two new buttons to the right of the 'Allow' button.");

        this.page("options", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The first button allows you to select whether to match [#](8B0000)Every Enchantment[#]() on an "
                + "enchanted item, [#](8B0000)Any Enchantment[#](), or [#](8B0000)one particular enchantment[#](). The second button "
                + "allows you to specify whether to pay attention to the level or not. (E.G. 'Protection III' "
                + "versus 'Protection').\\\n\\\n"
                + "If you insert [#](8B0000)an unenchanted item[#]() into the filter, you can effectively Allow or Deny "
                + "[#](8B0000)any sort of enchantment[#]() to be inserted into or removed from the specified inventory.");
    }

    @Override
    protected String entryName() {
        return "Enchantments Item Filter";
    }

    @Override
    protected String entryDescription() {
        return "A filter that sorts items based on their enchantments.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.ITEM_ENCHANT_FILTER.get());
    }

    @Override
    protected String entryId() {
        return "enchant_filter";
    }
}
