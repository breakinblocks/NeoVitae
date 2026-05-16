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

public class HeavyHeartFlaskEntry extends EntryProvider {

    public HeavyHeartFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Heavy Heart");
        this.pageText("The [#](8B0000)Heavy Heart[#]() elixir fills the chest with a leaden weight that drags the "
                + "victim steadily downward. Flight becomes a desperate struggle. Swimming becomes "
                + "a slow drowning.\\\n\\\n"
                + "To synthesize it, brew both [#](8B0000)Gravity[#]() and [#](8B0000)Instant Health[#]() into a single flask, "
                + "then fuse them with a [#](8B0000)Combinational Catalyst[#]() at the Tabula Vitae.");

        this.page("recipe1", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/gravity_to_heart")
                .withRecipeId2("neovitae:flask/length_heavy_heart"));
        this.page("recipe2", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/potency_heavy_heart")
                .withRecipeId2("neovitae:flask/potency_average_heavy_heart"));
        this.page("recipe3", () -> BookFlaskRecipePageModel.create()
                .withRecipeId1("neovitae:flask/length_average_heavy_heart"));
    }

    @Override
    protected String entryName() {
        return "Heavy Heart";
    }

    @Override
    protected String entryDescription() {
        return "A leaden weight that drags the victim earthward.";
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
        return "flask_heavy_heart";
    }
}
