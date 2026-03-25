package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.mojang.datafixers.util.Pair;

public class TauFruitEntry extends EntryProvider {

    public TauFruitEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tau Fruit");
        this.pageText("Tau Fruit can be found within **Simple Dungeons**, after which it can be farmed. Being a "
                + "native denizen of the **Demon Realm**, its lifecycle is unfortunately a tad more complex than that "
                + "of the humble **Potato**.\n\n"
                + "The Tau Fruit will mature into one of two varieties, depending on the conditions in which it was "
                + "raised. By default, it will grow into **Tau Fruit**, which can be converted into **Tau Oil**.");

        this.page("tau_oil", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tau Oil");
        this.pageText("Tau Oil can be created in the Alchemy Table. *Tastes like a Blood Orange, except different.*");

        this.page("tau_oil_uses", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Tau Oil currently has two main uses. It can extend the duration of any **anointment** by a "
                + "factor of four, or it can be used to craft **Intermediate Cutting Fluid**, which functions similarly "
                + "to Basic Cutting Fluid, but lasts eight times longer and provides a 25% speed boost.");

        this.page("saturated_tau", () -> BookSpotlightPageModel.create()
                .withItem(NVBlocks.STRONG_TAU.asItem())
                .withTitle("Saturated Tau")
                .withText(this.context().pageText()));
        this.pageText("However, **Tau Fruit** has an alternate, more challenging route of growth. If the plant "
                + "matures while a **Mob** (a cow, for example) is standing atop it, it will leach health from the "
                + "entity to satiate its dark hungers. In this way, **Saturated Tau** can be grown.");
    }

    @Override
    protected String entryName() {
        return "Tau Fruit";
    }

    @Override
    protected String entryDescription() {
        return "A demonic fruit with unique growth mechanics.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.WEAK_TAU.asItem());
    }

    @Override
    protected String entryId() {
        return "tau_fruit";
    }
}
