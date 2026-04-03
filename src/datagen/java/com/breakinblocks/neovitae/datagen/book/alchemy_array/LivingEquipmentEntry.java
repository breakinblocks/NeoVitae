package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class LivingEquipmentEntry extends EntryProvider {

    public LivingEquipmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Living Equipment");
        this.pageText("To create [#](8B0000)Living Equipment[#](), you require [#](8B0000)Iron Armour[#](), "
                + "the [#](8B0000)Arcane Scribe Tool[#](), a [#](8B0000)Binding Reagent[#](), and at least a "
                + "[#](8B0000)Common Spiritus Gem[#]() charged with sufficient [#](4A0080)Spiritus[#](). "
                + "What emerges is no longer mere metal; it is alive, and it learns from you.");

        this.page("reagent", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Binding Reagent[#]() is forged in the [#](8B0000)Tabula Vitae[#]().\\\n\\\n"
                + "[#](8B0000)Living Equipment[#]() matches [#](B8860B)Diamond[#]() in durability and can be "
                + "repaired in an [#](8B0000)Anvil[#]() with additional [#](8B0000)Binding Reagent[#]().");

        this.page("crafting", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Inscribe an [#](8B0000)Alchemy Array[#](), then apply the [#](8B0000)Binding Reagent[#]() "
                + "as base. Place your [#](8B0000)Iron Helmet[#](), [#](8B0000)Iron Chestplate[#](), "
                + "[#](8B0000)Iron Leggings[#](), or [#](8B0000)Iron Boots[#]() as catalyst, and step back.\\\n\\\n"
                + "The armour begins equivalent to iron, but possesses 100 [#](B8860B)Upgrade Points[#]() "
                + "that it will spend as it grows alongside you. There may be ways to surpass this "
                + "limitation...");

        this.page("binding", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Ritual of Binding[#](): [#](8B0000)Binding Reagent[#]() as base, an iron "
                + "armour piece as catalyst.\\\n\\\n"
                + "The armour is alive, and it watches everything you do. Be deliberate in what you teach "
                + "it. [#](2E8B57)Hold sneak while examining a piece to see what it has learned so far.[#]()");
    }

    @Override
    protected String entryName() {
        return "Living Equipment";
    }

    @Override
    protected String entryDescription() {
        return "Armour that breathes, learns, and grows alongside its wearer.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.REAGENT_BINDING.get());
    }

    @Override
    protected String entryId() {
        return "living_equipment";
    }
}
