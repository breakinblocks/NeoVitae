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
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class ObsidianCloakFlaskEntry extends EntryProvider {

    public ObsidianCloakFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Obsidian Cloak");
        this.pageText("The [#](8B0000)Obsidian Cloak[#]() elixir sheathes the body in an invisible mantle of volcanic "
                + "hardness, reducing all non-magical damage by 20%% per level. Arrows glance. Blades "
                + "skid. Only [#](4A0080)arcane forces[#]() bypass it.\\\n\\\n"
                + "Brewed by combining a flask of [#](8B0000)Hard Cloak[#]() with [#](8B0000)Diamond[#]() and [#](8B0000)Crying Obsidian[#]() "
                + "at the Tabula Vitae.");

        this.page("recipe1", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/hard_to_obsidian")
                .withRecipeId2("neovitae:flask/length_obsidian_cloak"));
        this.page("recipe2", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/potency_obsidian_cloak")
                .withRecipeId2("neovitae:flask/potency_average_obsidian_cloak"));
        this.page("recipe3", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/length_average_obsidian_cloak"));
    }

    @Override
    protected String entryName() {
        return "Obsidian Cloak";
    }

    @Override
    protected String entryDescription() {
        return "A mantle of volcanic hardness against physical harm.";
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
        return "flask_obsidian_cloak";
    }
}
