package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class MovementArraysEntry extends EntryProvider {

    public MovementArraysEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Movement Arrays");
        this.pageText("A pair of arrays designed to hurl anything that crosses their threshold, players, "
                + "creatures, loose items alike. One propels horizontally in the direction the entity faces; "
                + "the other launches straight upward.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](8B0000)Speed Array[#](): Launches entities horizontally in the direction they face. "
                + "Inscribed via [#](8B0000)Alchemy Array[#]().\\\n\\\n"
                + "[#](8B0000)Updraft Array[#](): Hurls entities skyward with considerable force. "
                + "Inscribed via [#](8B0000)Alchemy Array[#]().\\\n\\\n"
                + "[#](2E8B57)Useful for rapid transit systems, mob processing, or simply entertaining "
                + "oneself at a guest's expense.[#]()");
    }

    @Override
    protected String entryName() {
        return "Movement Arrays";
    }

    @Override
    protected String entryDescription() {
        return "Arrays of force, hurl creatures and objects with vitaemantic velocity.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.FEATHER);
    }

    @Override
    protected String entryId() {
        return "movement_arrays";
    }
}
