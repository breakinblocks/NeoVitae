package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class KeysEntry extends EntryProvider {

    public KeysEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dungeon Keys");
        this.pageText("**Keys** are the initial way of navigating **Dungeons**. They can be found inside Dungeon "
                + "chests within the **Demon Realm**, or they can be crafted. The most basic kind are **Iron Keys**, "
                + "which spawn inside **The Antechamber** as well as within the closest levels of the **Demon Realm** proper.");

        this.page("distortions", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Eventually, you may come across a **Spatial Distortion** when searching through a Dungeon. "
                + "These will either lead you to a unique, uncraftable key, or to a door that only said key can open. "
                + "Spatial Distortions only form within the Demon Realm proper, and you will never find them within an Antechamber.");

        this.page("mines_key", () -> BookSpotlightPageModel.create()
                .withItem(NVItems.MINE_ENTRANCE_KEY.get())
                .withTitle("The Mines")
                .withText(this.context().pageText()));
        this.pageText("The entrance to **The Mines** can only be opened with the uncraftable **Foreman's Key**, "
                + "while all other doors beyond that can be opened with **Miner's Keys**. The mines will grant you "
                + "access to **Demonite ore** alongside some rarer loot, but comes with challenges to match.");

        this.page("iron_key", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Iron Key");
        this.pageText("Iron Keys can be crafted in the Hellfire Forge. *If it's so simple, why can't we just pick the lock?*");

        this.page("miners_key", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Miner's Key");
        this.pageText("Miner's Keys can be crafted in the Hellfire Forge. *It's off to work we go..!*");
    }

    @Override
    protected String entryName() {
        return "Dungeon Keys";
    }

    @Override
    protected String entryDescription() {
        return "Keys used to navigate the Demon Realm.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SIMPLE_KEY.get());
    }

    @Override
    protected String entryId() {
        return "keys";
    }
}
