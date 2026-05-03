package com.breakinblocks.neovitae.datagen.book.spiritus;

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
        this.pageText("Bows and crossbows serve their purpose well enough, but sifting through gravel for flint "
                + "and plucking chickens for feathers is, frankly, beneath a Vitaemancer of your caliber. "
                + "These razor-edged [#](8B0000)Throwing Daggers[#]() require no ammunition and carry some rather "
                + "devious secondary effects.\\\n\\\n"
                + "The basic Iron variant is forged at the [#](8B0000)Tabula Vitae[#]() with a [#](8B0000)Novicius Orb[#]() "
                + "and a handful of materials.");

        this.page("iron_dagger", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Iron Throwing Dagger");
        this.pageText("A swift, precise strike dealing [#](B8860B)10 damage[#]() with a moderate cooldown.\\\n\\\n"
                + "Every wound it inflicts upon a [#](8B0000)hostile creature[#]() binds spectral motes to its form. "
                + "Slay the marked quarry and it yields [#](8B0000)Raw Spiritus[#](); the only path to your first "
                + "harvest before a [#](8B0000)Sentient Sword[#]() is at hand.\\\n\\\n"
                + "[#](2E8B57)The Looting enchantment increases the Spiritus dropped.[#]()");

        this.page("syringe_dagger", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Syringe Throwing Dagger");
        this.pageText("For the practitioner more interested in [#](4A0080)Essentia Vitae[#]() than Spiritus. Slightly less "
                + "damaging and noticeably cheaper to forge, creatures slain by this weapon have a chance of "
                + "yielding a [#](8B0000)Slate Ampoule[#](), or more, if the quarry is sufficiently hearty.");

        this.page("slate_ampoule", () -> BookSpotlightPageModel.create()
                .withItem(NVItems.TABULA_AMPOULE.get())
                .withTitle("Slate Ampoule")
                .withText(this.context().pageText()));
        this.pageText("Crush these delicate vials near an [#](8B0000)Ara Vitae[#]() to transfer [#](B8860B)500 EV[#]() "
                + "directly into the altar, destroying the ampoule in the process. "
                + "[#](2E8B57)This infusion is unaffected by Runes.[#]()");

        this.page("amethyst_dagger", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Amethyst Throwing Dagger");
        this.pageText("Matches the Iron dagger in damage but yields no Spiritus from kills. Its true purpose lies "
                + "elsewhere: eight Amethyst daggers combined with a [#](8B0000)Lingering Alchemy Flask[#]() in the "
                + "[#](8B0000)Athanor[#]() produce [#](8B0000)Tipped Amethyst Throwing Daggers[#]().");

        this.page("tipped_dagger", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tipped Throwing Dagger");
        this.pageText("Each strike delivers the flask's effect to the target, as though it had walked through "
                + "a lingering cloud. Experiment with combined alchemical effects to craft the most debilitating "
                + "projectiles your ingenuity allows.");
    }

    @Override
    protected String entryName() {
        return "Throwing Daggers";
    }

    @Override
    protected String entryDescription() {
        return "Razor-edged projectiles that harvest Spiritus, Essence, or deliver alchemical ruin.";
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
