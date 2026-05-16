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
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import net.minecraft.world.item.ItemStack;
import com.mojang.datafixers.util.Pair;

public class FlightFlaskEntry extends EntryProvider {

    public FlightFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Flight");
        this.pageText("The [#](8B0000)Flight[#]() elixir is the crowning achievement of [#](4A0080)gravitational alchemy[#](), "
                + "true, unfettered flight, as free as the wind itself. Higher levels grant greater speed "
                + "through the air.\\\n\\\n"
                + "To synthesize it, brew both [#](8B0000)Suspended[#]() and [#](8B0000)Levitation[#]() into a single flask, "
                + "then fuse them with a [#](8B0000)Combinational Catalyst[#]() at the Tabula Vitae.");

        this.page("recipe1", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/suspended_to_flight")
                .withRecipeId2("neovitae:flask/length_flight"));
        this.page("recipe2", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/potency_flight")
                .withRecipeId2("neovitae:flask/potency_average_flight"));
        this.page("recipe3", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/length_average_flight"));
    }

    @Override
    protected String entryName() {
        return "Flight";
    }

    @Override
    protected String entryDescription() {
        return "The pinnacle of gravitational alchemy, true flight.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.ALCHEMY_FLASK.get());
    }

    @Override
    protected String entryId() {
        return "flask_flight";
    }
}
