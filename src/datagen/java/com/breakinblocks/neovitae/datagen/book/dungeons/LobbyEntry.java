package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

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
        this.pageText("When the [#](8B0000)Breaching the Edge of Demon Realm[#]() ritual completes, you will find yourself "
                + "standing before an [#](8B0000)Inversion Pillar[#](). Touch it, and you are drawn across the threshold "
                + "into the [#](4A0080)Antechamber[#](), the outermost vestibule of the [#](4A0080)Demon Realm[#]().\\\n\\\n"
                + "Take stock of your surroundings:\n\n"
                + "- A solitary chest, sometimes containing [#](8B0000)Simple Dungeon Keys[#]().");

        this.page("intro2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("- Several [#](8B0000)Doorways[#](), each barred by a [#](8B0000)Dungeon Seal[#](). Present a key "
                + "to a seal to unseal the chamber beyond.\n\n"
                + "- A second [#](8B0000)Inversion Pillar[#](), mirroring the one that summoned you. This is your "
                + "lifeline home.\\\n\\\n"
                + "[#](2E8B57)The stonework here is unique to the Demon Realm. If its aesthetics appeal to you, "
                + "help yourself.[#]()");

        this.page("rooms", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Each sealed chamber you open may contain treasure, hostile denizens, traps, or "
                + "dormant rituals. Vigilance is the price of survival here.\\\n\\\n"
                + "Among the spoils you may claim:\n\n"
                + "- Enchanted Books\n\n"
                + "- Enchanted Weapons and Armor\n\n"
                + "- Various Anointments\n\n"
                + "- Spiritus\n\n"
                + "- Potion Ingredients");

        this.page("rooms2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("- Tau Fruit\n\n"
                + "- Saturated Tau\n\n"
                + "- Tau Oil\\\n\\\n"
                + "[#](2E8B57)The Antechamber is merely a foretaste. Prove yourself here, and the deeper "
                + "reaches of the realm will open to you in time.[#]()");
    }

    @Override
    protected String entryName() {
        return "The Antechamber";
    }

    @Override
    protected String entryDescription() {
        return "The threshold between worlds, where the Demon Realm first reveals itself.";
    }

    @Override
    protected GuiSprite entryBackground() {
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
