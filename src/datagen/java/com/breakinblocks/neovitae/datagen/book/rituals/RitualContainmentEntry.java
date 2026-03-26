package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class RitualContainmentEntry extends EntryProvider {

    public RitualContainmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/containment"))
                .withMultiblockName("Ritual of Binding")
                .withText(this.context().pageText()));
        this.pageText("Use a [#](8B0000)Ritual Diviner[#]() for easier construction.");

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("This ritual creates an invisible barrier that prevents mobs from leaving the defined area. Any creature that attempts to escape will be pushed back toward the center of the ritual. Players are unaffected by this containment field, allowing you to create pens for animals or traps for hostile mobs.");
    }

    @Override
    protected String entryName() {
        return "Ritual of Binding";
    }

    @Override
    protected String entryDescription() {
        return "Contains mobs within an area.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.LEAD);
    }

    @Override
    protected String entryId() {
        return "ritual_containment";
    }
}
