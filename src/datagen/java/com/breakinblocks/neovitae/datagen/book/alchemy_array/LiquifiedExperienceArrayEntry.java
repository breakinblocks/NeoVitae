package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.datagen.book.page.BookAlchemyArrayRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;

public class LiquifiedExperienceArrayEntry extends EntryProvider {

    public LiquifiedExperienceArrayEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Array of Liquified Experience");
        this.pageText("Learning is a fluid thing, and the [#](8B0000)Array of Liquified Experience[#]() proves it "
                + "literally. Inscribe the glyph atop a chest and set a tank against it.\\\n\\\n"
                + "Every [#](8B0000)Tome of Peritia[#]() resting in that chest is bled of what it holds, and the "
                + "learning runs out as [#](4A0080)Liquified Experience[#]() into the waiting vessel.");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The tank may sit against the array or against the chest itself; the glyph finds it either way. "
                + "It works once a second and cares nothing for whether you are watching.\\\n\\\n"
                + "Feed the array a [#](8B0000)redstone signal[#]() and the current runs backwards: the tank is drained "
                + "and the tomes drink it back down.\\\n\\\n"
                + "[#](4A0080)Knowledge poured out is knowledge that can be poured back.[#]()");

        this.page("compat", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Neo Vitae brews its own [#](4A0080)Liquified Experience[#](), so the array needs nothing else "
                + "installed to work.\\\n\\\n"
                + "Should your world already hold another mod's experience fluid, name it in the server config under "
                + "[#](8B0000)liquified_experience.preferred_fluid[#]() and the array will read and write that instead. "
                + "The same section sets how many millibuckets a single point of experience is worth.");

        this.page("recipe", () -> BookAlchemyArrayRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "array/liquified_experience")));
    }

    @Override
    protected String entryName() {
        return "Array of Liquified Experience";
    }

    @Override
    protected String entryDescription() {
        return "Drains Tomes of Peritia into a tank, or fills them back from one.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVFluids.LIQUIFIED_EXPERIENCE_BUCKET.get());
    }

    @Override
    protected String entryId() {
        return "liquified_experience_array";
    }
}
