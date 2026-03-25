package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.mojang.datafixers.util.Pair;

public class LobbyEntry extends EntryProvider {

    public LobbyEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Antechamber");
        this.pageText("Upon performing the Edge of the Hidden Realm ritual, you will find yourself looking at "
                + "an **Inversion Pillar**. Right click this pillar to be transported to the Antechamber, your "
                + "first foray into the **Demon Realm.**\n"
                + "There are a few things of note in this first room:\n"
                + "- A single treasure chest containing some basic loot and, if you are lucky, a few **Iron Keys**.");

        this.page("intro2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("- Secondly, a number of **Doorways**, each locked with a **Dungeon Seal.** Use a **Key** "
                + "on these seals to unlock the next room.\n"
                + "- Thirdly, an **Inversion Pillar**, a mirror of the one you summoned previously. Use this to "
                + "return to your world.\n\n"
                + "There's also a large number of decorative blocks here, should you feel your home base needs some pizazz.");

        this.page("rooms", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Any additional rooms you open from this antechamber will contain some combination of loot, "
                + "monsters, traps and possibly some unactivated rituals. Keep your eyes peeled!\n\n"
                + "A few examples of loot that you might find:\n"
                + "- Enchanted Books\n"
                + "- Enchanted Weapons and Armour\n"
                + "- Various Anointments\n"
                + "- Demon Will\n"
                + "- Potion Ingredients");

        this.page("rooms2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("- Tau Fruit\n"
                + "- Saturated Tau\n"
                + "- Tau Oil\n\n"
                + "So get hunting and keep an eye out for treasure and traps alike!");
    }

    @Override
    protected String entryName() {
        return "The Antechamber";
    }

    @Override
    protected String entryDescription() {
        return "Your first foray into the Demon Realm.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.DUNGEON_SEAL.asItem());
    }

    @Override
    protected String entryId() {
        return "lobby";
    }
}
