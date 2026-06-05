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

public class InstantHealthFlaskEntry extends EntryProvider {

    public InstantHealthFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Instant Health");
        this.pageText("The [#](8B0000)Instant Health[#]() elixir is no gentle salve; it floods the body with raw "
                + "vitality, restoring 4 health per level in an instant. Against the [#](8B0000)undead[#](), "
                + "this same vitality becomes anathema, dealing 4 points of harm per level instead.");

        this.page("recipe1", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/health")
                .withRecipeId2("neovitae:flask/potency_health"));
        this.page("recipe2", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/potency_average_health"));
    }

    @Override
    protected String entryName() {
        return "Instant Health";
    }

    @Override
    protected String entryDescription() {
        return "A surge of raw vitality, instant and potent.";
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
        return "flask_instant_health";
    }
}
