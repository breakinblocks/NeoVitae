package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookTabulaVitaeRecipePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class DeusSanguinisUpgradeEntry extends EntryProvider {

    public DeusSanguinisUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Deus Sanguinis");
        this.pageText("The armor learns to live on your blood. Every blow you land feeds your soul network, and every "
                + "blow you take is paid for from it instead of your flesh. It begins with the "
                + "[#](8B0000)Tome of Deus Sanguinis[#]() and grows from there.");

        this.page("effect", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Each level grants [#](8B0000)10 Blood Siphon[#]() and [#](8B0000)10 Blood Shield[#](), to "
                + "100 of each at level 10.\\\n\\\n"
                + "[#](8B0000)Blood Siphon[#]() turns that percentage of the damage you deal into Essentia Vitae. "
                + "[#](8B0000)Blood Shield[#]() prevents that percentage of the damage you take and draws "
                + "[#](4A0080)100 EV[#]() for each point prevented. Neither goes past [#](B8860B)95%%[#]().");

        this.page("severance", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Anima Severance");
        this.pageText("Blood Siphon turned on another Vitaemancer drains their soul network as well, and leaves them "
                + "[#](8B0000)Severed[#]() for ten seconds: their network takes in no Essentia Vitae, and every cost it "
                + "pays is doubled. No milk or totem lifts it; it must simply run its course.");

        this.page("training", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](B8860B)Requires[#](): a [#](8B0000)Hellforged[#]() chestplate holding "
                + "[#](B8860B)700 Upgrade Points[#](). Lesser armor rejects the tome and will not train it.\\\n\\\n"
                + "Costs up to [#](4A0080)200 Upgrade Points[#](). It trains as your network spends Essentia "
                + "Vitae, earning at most one lesson every ten seconds, so a flood of rituals teaches it no faster "
                + "than steady use.");

        this.page("recipe", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/deus_sanguinis_tome"));
    }

    @Override
    protected String entryName() {
        return "Deus Sanguinis";
    }

    @Override
    protected String entryDescription() {
        return "Hellforged armor that feeds on the blood it spills.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.TOTEM_OF_UNDYING);
    }

    @Override
    protected String entryId() {
        return "upgrade_deus_sanguinis";
    }
}
