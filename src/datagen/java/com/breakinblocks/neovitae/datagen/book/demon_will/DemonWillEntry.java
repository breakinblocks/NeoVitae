package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class DemonWillEntry extends EntryProvider {

    public DemonWillEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will");
        this.pageText("Once you have a [#](8B0000)Blood Altar[#](), you'll need to gather a few [#](8B0000)Demon Wills[#](). "
                + "There are two ways to get Demon Will:\n"
                + "- Killing a mob that has been hit with a [#](8B0000)Soul Snare[#]() and is killed when white particle effects appear.\n"
                + "- By killing a hostile mob with a [#](8B0000)Sentient Sword[#]().\\\n\\\n"
                + "Since you are just beginning to use the mod, you will not yet have a [#](8B0000)Sentient Sword[#](),");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("and thus will need to use a [#](8B0000)Soul Snare[#]().\\\n\\\n"
                + "[#](8B0000)Demon Will[#]() is a recurring resource in Neo Vitae, and is used to power the [#](8B0000)Hellfire Forge[#]().\\\n\\\n"
                + "In the lore of Neo Vitae, Demon Will is the residual effect of when a demon imbues its will "
                + "into the bodies of the dead or other monsters.");

        this.page("image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/demon_will/demon_will.png"))
                .withTitle("Demon Will")
                .withBorder(true));

        this.page("next_steps", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Once you have some Will, you can use it to craft useful tools in the [#](8B0000)Hellfire Forge[#](). "
                + "If you find them cluttering up your inventory, perhaps a [#](8B0000)Tartaric Gem[#]() may help.");
    }

    @Override
    protected String entryName() {
        return "Demon Will";
    }

    @Override
    protected String entryDescription() {
        return "The demonic essence harvested from slain monsters.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.MONSTER_SOUL_RAW.get());
    }

    @Override
    protected String entryId() {
        return "demon_will";
    }
}
