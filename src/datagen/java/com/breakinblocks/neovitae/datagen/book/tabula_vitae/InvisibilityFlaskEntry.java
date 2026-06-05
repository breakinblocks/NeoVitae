package com.breakinblocks.neovitae.datagen.book.tabula_vitae;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookFlaskRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class InvisibilityFlaskEntry extends EntryProvider {

    public InvisibilityFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Invisibility");
        this.pageText("The [#](8B0000)Invisibility[#]() elixir bends light around the drinker's flesh, rendering them "
                + "unseen. Be warned: armour and held items remain stubbornly visible. A floating sword "
                + "is hardly subtle.\\\n\\\n"
                + "Derived from a flask of [#](8B0000)Night Vision[#]().");

        this.page("recipe1", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/night_to_invis")
                .withRecipeId2("neovitae:flask/length_invisibility"));
        this.page("recipe2", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/length_average_invisibility"));
    }

    @Override
    protected String entryName() {
        return "Invisibility";
    }

    @Override
    protected String entryDescription() {
        return "Bends light to conceal the flesh from sight.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.ALCHEMY_FLASK.get());
    }

    @Override
    protected String entryId() {
        return "flask_invisibility";
    }
}
