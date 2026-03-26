package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class HellfireForgeEntry extends EntryProvider {

    public HellfireForgeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Hellfire Forge");
        this.pageText("The **Hellfire Forge** is one of the core crafting mechanics of Neo Vitae, alongside the "
                + "**Blood Altar** itself. Here, you can work with the **Demon Will** you have harvested from "
                + "mobs, to allow you to create **Sentient Tools**, including the **Sentient Sword**, "
                + "**Tartaric Gems**, various reagents, **Arcane Ash**, and many things besides.");

        this.page("crafting", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "hellfire_forge")));
    }

    @Override
    protected String entryName() {
        return "Hellfire Forge";
    }

    @Override
    protected String entryDescription() {
        return "A crafting station powered by Demon Will.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.HELLFIRE_FORGE.asItem());
    }

    @Override
    protected String entryId() {
        return "hellfire_forge";
    }
}
