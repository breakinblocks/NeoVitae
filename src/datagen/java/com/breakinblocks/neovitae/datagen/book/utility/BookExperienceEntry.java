package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class BookExperienceEntry extends EntryProvider {

    public BookExperienceEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tome of Peritia");
        this.pageText("The [#](8B0000)Tome of Peritia[#]() is a vessel for captured wisdom. Within its pages, "
                + "your accumulated experience crystallizes into a form that cannot be lost to death.\\\n\\\n"
                + "[#](2E8B57)Sneak and Use to store one level. Use alone to retrieve. "
                + "Hold either action to transfer multiple levels rapidly.[#]()");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "experience_tome"))
                .withText(this.context().pageText()));
        this.pageText("Bind an [#](8B0000)Enchanted Book[#]() in gold, lapis, string, and [#](8B0000)Tabula Animata[#]() around a [#](8B0000)Veneficus Orb of Vitae[#]() to forge the [#](8B0000)Tome of Peritia[#]().");

        this.page("curios", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Wearing the Tome");
        this.pageText("With the Curios API present, you may equip the Tome as a charm upon your person, "
                + "freeing your hands for more pressing matters.\\\n\\\n"
                + "[#](2E8B57)If you find yourself wanting more charm slots, the Sigil of Holding "
                + "or the Socketed Upgrade for Sentient Armor may serve you well.[#]()");
    }

    @Override
    protected String entryName() {
        return "Tome of Peritia";
    }

    @Override
    protected String entryDescription() {
        return "A repository for experience, safe from the grasp of death.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.EXPERIENCE_TOME.get());
    }

    @Override
    protected String entryId() {
        return "book_experience";
    }
}
