package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class SpectralSightFlaskEntry extends EntryProvider {

    public SpectralSightFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spectral Sight");
        this.pageText("**Spectral Sight** illuminates nearby creatures as though they had the Glowing buff. "
                + "The base range is 24 blocks, and higher levels increase the range by an additional 32 blocks "
                + "per level.\n\n"
                + "It's made from a potion of **Night Vision**.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Spectral Sight (3:00)** - neovitae:flask/night_to_spectral\n"
                + "- **Spectral Sight (8:00)** - neovitae:flask/length_spectral_sight\n"
                + "- **Spectral Sight II (1:30)** - neovitae:flask/potency_spectral_sight");

        this.page("advanced", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Adding Standard Catalysts can further boost your potions.\n\n"
                + "- **Spectral Sight III (0:45)** - neovitae:flask/potency_average_spectral_sight\n"
                + "- **Spectral Sight (21:20)** - neovitae:flask/length_average_spectral_sight");
    }

    @Override
    protected String entryName() {
        return "Spectral Sight";
    }

    @Override
    protected String entryDescription() {
        return "Illuminates nearby creatures with Glowing.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.GLOWSTONE_DUST);
    }

    @Override
    protected String entryId() {
        return "flask_spectral_sight";
    }
}
