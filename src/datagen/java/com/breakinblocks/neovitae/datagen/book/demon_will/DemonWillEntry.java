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
        this.pageText("Once you have a **Blood Altar**, you'll need to gather a few **Demon Wills**. "
                + "There are two ways to get Demon Will:\n"
                + "- Killing a mob that has been hit with a **Soul Snare** and is killed when white particle effects appear.\n"
                + "- By killing a hostile mob with a **Sentient Sword**.\\\n\\\n"
                + "Since you are just beginning to use the mod, you will not yet have a **Sentient Sword**,");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("and thus will need to use a **Soul Snare**.\\\n\\\n"
                + "**Demon Will** is a recurring resource in Neo Vitae, and is used to power the **Hellfire Forge**.\\\n\\\n"
                + "In the lore of Neo Vitae, Demon Will is the residual effect of when a demon imbues its will "
                + "into the bodies of the dead or other monsters.");

        this.page("image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/demon_will/demon_will.png"))
                .withTitle("Demon Will")
                .withBorder(true));

        this.page("next_steps", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Once you have some Will, you can use it to craft useful tools in the **Hellfire Forge**. "
                + "If you find them cluttering up your inventory, perhaps a **Tartaric Gem** may help.");
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
