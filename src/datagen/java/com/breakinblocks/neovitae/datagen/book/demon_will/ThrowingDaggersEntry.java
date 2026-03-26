package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class ThrowingDaggersEntry extends EntryProvider {

    public ThrowingDaggersEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Throwing Daggers");
        this.pageText("[#](8B0000)Bows[#]() and [#](8B0000)Crossbows[#]() are fine enough in their way, but sifting through [#](8B0000)Gravel[#]() "
                + "for [#](8B0000)Flint[#]() and plucking chickens for their [#](8B0000)Feathers[#]() is, frankly, beneath you. These "
                + "shiny (and extremely sharp) [#](8B0000)Throwing Daggers[#]() also have some quite devious effects, if you "
                + "do say so yourself.");

        this.page("iron_dagger", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Iron Throwing Dagger");
        this.pageText("Craft the Iron Throwing Dagger in the Hellfire Forge.\\\n\\\n"
                + "The [#](8B0000)Iron Throwing Dagger[#]() is a fast-hitting attack, dealing 10 damage with a decent "
                + "cooldown. Not only that, but if you have some [#](8B0000)Demon Will[#]() on you (be it in its raw form, "
                + "or stored within a [#](8B0000)Tartaric Gem[#]()), it will drop Will as a [#](8B0000)Sentient Sword[#]() would.");

        this.page("syringe_dagger", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Syringe Throwing Dagger");
        this.pageText("Craft the Syringe Throwing Dagger in the Hellfire Forge.\\\n\\\n"
                + "The [#](8B0000)Syringe Throwing Dagger[#]() is for the Sanguimancer more interested in the acquisition "
                + "of [#](8B0000)Life Essence[#]() than Will. While it deals slightly less damage, it is noticeably cheaper, "
                + "and enemies killed by this weapon have a chance of dropping a [#](8B0000)Slate Ampoule[#]() - or more, "
                + "if they're hearty enough.");

        this.page("slate_ampoule", () -> BookSpotlightPageModel.create()
                .withItem(NVItems.SLATE_AMPOULE.get())
                .withTitle("Slate Ampoule")
                .withText(this.context().pageText()));
        this.pageText("These delightful little vials can be crushed when near a [#](8B0000)Ara Vitae[#]() in order to "
                + "transfer [#](8B0000)500 LP[#]() into it, destroying the [#](8B0000)Ampoule[#]() in the process. These gains are "
                + "unaffected by any [#](8B0000)Runes[#]() you may have.");

        this.page("amethyst_dagger", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Amethyst Throwing Dagger");
        this.pageText("Craft the Amethyst Throwing Dagger in the Hellfire Forge.\\\n\\\n"
                + "The [#](8B0000)Amethyst Throwing Dagger[#]() does as much damage as an [#](8B0000)Iron Throwing Dagger[#](), but "
                + "mobs do not drop Will when killed. Instead, eight of them can be crafted with a [#](8B0000)Lingering "
                + "Alchemy Flask[#](8B0000) in the [#]()Athanor[#](8B0000) to create [#]()Tipped Amethyst Throwing "
                + "Daggers[#]().");

        this.page("tipped_dagger", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tipped Throwing Dagger");
        this.pageText("Craft Tipped Throwing Daggers in the Athanor.\\\n\\\n"
                + "These will transmit their effect to any mob they hit, the same as if they'd walked into the "
                + "cloud left by a [#](8B0000)Lingering Alchemy Flask[#](). Experiment with combined effects to find the "
                + "most debilitating, diabolical daggers you can make!");
    }

    @Override
    protected String entryName() {
        return "Throwing Daggers";
    }

    @Override
    protected String entryDescription() {
        return "Ranged weapons with unique Will-harvesting and potion effects.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.THROWING_DAGGER.get());
    }

    @Override
    protected String entryId() {
        return "throwing_daggers";
    }
}
