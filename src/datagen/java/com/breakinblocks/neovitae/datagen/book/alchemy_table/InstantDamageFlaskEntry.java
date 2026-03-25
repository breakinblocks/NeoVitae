package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class InstantDamageFlaskEntry extends EntryProvider {

    public InstantDamageFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Instant Damage");
        this.pageText("**Instant Damage** inflicts 6 points of **Magic Damage** to the target per level. "
                + "If the target is **Undead**, they will be healed for 6 points per level instead.\n\n"
                + "It's made from a potion of either **Instant Health** or **Poison**.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Damage from Health** - neovitae:flask/health_to_harm\n"
                + "- **Damage from Poison** - neovitae:flask/poison_to_harm\n"
                + "- **Instant Damage II** - neovitae:flask/potency_harm");

        this.page("advanced", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Adding Standard Catalysts can further boost your potions.\n\n"
                + "- **Instant Damage III** - neovitae:flask/potency_average_harm");
    }

    @Override
    protected String entryName() {
        return "Instant Damage";
    }

    @Override
    protected String entryDescription() {
        return "Inflicts magic damage instantly.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.IRON_SWORD);
    }

    @Override
    protected String entryId() {
        return "flask_instant_damage";
    }
}
