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
        this.pageTitle("Living Equipment Basics");
        this.pageText("To create [#](8B0000)Living Equipment[#](), you will first need [#](8B0000)Iron Armor[#](), some [#](8B0000)Arcane Ash[#](), "
                + "and some [#](8B0000)Binding Reagent[#](). You'll also need at least a [#](8B0000)Common Tartaric Gem[#]() in order "
                + "to hold the [#](8B0000)Demon Will[#]() required.");

        this.page("reagent", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Craft the [#](8B0000)Binding Reagent[#]() in the Alchemy Table.\\\n\\\n*It clings to me tightly...*\\\n\\\n"
                + "[#](8B0000)Living Equipment[#]() is equivalent in durability to [#](8B0000)Diamond Armour[#](), and can be repaired "
                + "in an [#](8B0000)Anvil[#]() with more [#](8B0000)Binding Reagent[#]().");

        this.page("crafting", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("As with any other Alchemy Array, press [Use] while looking at a block with the **Arcane "
                + "Ashes[#](8B0000) in hand, then apply the [#]()Binding Reagent[#](8B0000). Then place in your [#]()Iron Helmet**, "
                + "[#](8B0000)Iron Chestplate[#](), [#](8B0000)Iron Leggings[#]() or [#](8B0000)Iron Boots[#](), stand back, and watch the show.\\\n\\\n"
                + "Living Equipment starts off equivalent to Iron, but it has [#](8B0000)Upgrade Points[#]() that can, "
                + "with care, be spent to train it in specific ways. It starts with 100, but there may be "
                + "ways to surpass this limitation...");

        this.page("binding", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](8B0000)Ritual of Binding[#](): Created in an Alchemy Array with [#](8B0000)Binding Reagent[#]() (base) "
                + "and an Iron armor piece (catalyst).\\\n\\\n"
                + "It's alive, all right... and it's learning from you. You'd best be careful what you teach "
                + "it. You can keep a closer eye on what it's learned so far by holding sneak when you look at it.");
    }

    @Override
    protected String entryName() {
        return "Living Equipment Basics";
    }

    @Override
    protected String entryDescription() {
        return "How to create and use Living Equipment.";
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
