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
        this.pageTitle("Enchantment Item Filter");
        this.pageText("The [#](8B0000)Enchantment Item Filter[#]() sorts items by the magical bindings wrought upon "
                + "them. It shares the Standard Filter's quantity and Allow/Deny functions, augmented with "
                + "additional controls for enchantment matching.");

        this.page("recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Enchantment Item Filter");
        this.pageText("Any enchanted book suffices as a crafting component for this filter.");

        this.page("gui_image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/enchant_item_filter_gui.png"))
                .withTitle("Enchantment Filter")
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("Note the two additional buttons beside the Allow toggle.");

        this.page("options", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The first button selects whether to match [#](B8860B)every enchantment[#]() on an item, "
                + "[#](B8860B)any enchantment[#](), or [#](B8860B)one particular enchantment[#](). The second determines "
                + "whether level matters (e.g. 'Protection III' vs 'Protection' at any level).\\\n\\\n"
                + "[#](2E8B57)Insert an unenchanted item to create a blanket rule for any sort of enchantment -- "
                + "useful for separating enchanted gear from mundane.[#]()");
    }

    @Override
    protected String entryName() {
        return "Enchantment Item Filter";
    }

    @Override
    protected String entryDescription() {
        return "Sorting items by the magical bindings wrought upon them.";
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
