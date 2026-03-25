package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;

public class RitualLivingDowngradeEntry extends EntryProvider {

    public RitualLivingDowngradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/downgrade"))
                .withMultiblockName("Penance of the Leaden Soul")
                .withText(this.context().pageText()));
        this.pageText("Use a **Ritual Diviner [Dusk]** for easier construction.");

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("This ritual consumes excess **Upgrade Points** (in the form of **Tomes**, **Scraps**, and **Synthetic Upgrade Points**) alongside one **Key Item** per level, in order to apply Downgrades to your worn **Living Armour**. The key item is different for each downgrade.");

        this.page("downgrades", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("These downgrades will harshly limit your abilities, but will provide you with a wealth of additional **Upgrade Points** to play around with, allowing for much more specialisation than was previously available to you.");

        this.page("synthetic", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "synthetic_point"))
                .withText(this.context().pageText()));
        this.pageText("If you do not have enough points available to you, you can craft Synthetic Upgrade Points. Each one of these is worth a single Upgrade Point.");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("To use the ritual, place your tomes (or other sources of Upgrade Points) and the required Item(s) for the particular downgrade into the attached chest, while wearing your Living Armour. The ritual will consume the points and apply the downgrade. The order of consumption is **Living Armour Upgrade Scraps**, then **Living Armour Upgrade Tomes**, and finally **Synthetic Upgrade Points**.");

        this.page("details", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Each item that matches a particular downgrade will increase the desired downgrade's level by one. Multiple downgrades can be applied at once. For example, Battle Hungry level 3 requires 3 **Rotten Flesh** and items worth 35 **Upgrade Points**."
                + "\n\nThe ritual respects your **Living Armour Training Bracelet** settings. Excess points are returned as **Living Armour Upgrade Scraps**.");
    }

    @Override
    protected String entryName() {
        return "Penance of the Leaden Soul";
    }

    @Override
    protected String entryDescription() {
        return "Applies downgrades for extra upgrade points.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.UPGRADE_SCRAP.get());
    }

    @Override
    protected String entryId() {
        return "ritual_living_downgrade";
    }
}
