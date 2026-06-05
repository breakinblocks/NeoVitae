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

public class PoisonFlaskEntry extends EntryProvider {

    public PoisonFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Poison");
        this.pageText("The [#](8B0000)Poison[#]() elixir introduces a wasting venom that gnaws at the target's vitality. "
                + "It cannot kill on its own; the victim will linger at death's threshold, weakened "
                + "and vulnerable. Higher levels hasten the decay.");

        this.page("recipe1", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/poison")
                .withRecipeId2("neovitae:flask/length_poison"));
        this.page("recipe2", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/potency_poison")
                .withRecipeId2("neovitae:flask/potency_average_poison"));
        this.page("recipe3", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/length_average_poison"));
    }

    @Override
    protected String entryName() {
        return "Poison";
    }

    @Override
    protected String entryDescription() {
        return "A wasting venom that gnaws but does not kill.";
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
        return "flask_poison";
    }
}
