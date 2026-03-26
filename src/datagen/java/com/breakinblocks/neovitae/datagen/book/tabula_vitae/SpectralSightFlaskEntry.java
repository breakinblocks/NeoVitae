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

public class SpectralSightFlaskEntry extends EntryProvider {

    public SpectralSightFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spectral Sight");
        this.pageText("[#](8B0000)Spectral Sight[#]() illuminates nearby creatures as though they had the Glowing buff. "
                + "The base range is 24 blocks, and higher levels increase the range by an additional 32 blocks "
                + "per level.\\\n\\\n"
                + "It's made from a potion of [#](8B0000)Night Vision[#]().");

        this.page("recipe1", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/night_to_spectral")
                .withRecipeId2("neovitae:flask/length_spectral_sight"));
        this.page("recipe2", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/potency_spectral_sight")
                .withRecipeId2("neovitae:flask/potency_average_spectral_sight"));
        this.page("recipe3", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/length_average_spectral_sight"));
    }

    @Override
    protected String entryName() {
        return "Spectral Sight";
    }

    @Override
    protected String entryDescription() {
        return "Illuminates nearby creatures with Glowing.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        ItemStack flask = new ItemStack(NVItems.ALCHEMY_FLASK.get());
        ItemAlchemyFlask.setFlaskEffects(flask, FlaskEffects.single(EffectHolder.create(NVMobEffects.SPECTRAL_SIGHT.getDelegate(), 3600, 0)));
        return BookIconModel.create(flask);
    }

    @Override
    protected String entryId() {
        return "flask_spectral_sight";
    }
}
