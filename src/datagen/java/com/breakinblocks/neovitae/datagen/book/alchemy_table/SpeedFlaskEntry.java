package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class SpeedFlaskEntry extends EntryProvider {

    public SpeedFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Speed");
        this.pageText("**Speed** increases the target's movement speed by 20% per level.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Speed (3:00)** - neovitae:flask/speed_boost\n"
                + "- **Speed (8:00)** - neovitae:flask/length_speed_boost\n"
                + "- **Speed II (1:30)** - neovitae:flask/potency_speed_boost");

        this.page("advanced", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Adding Standard Catalysts can further boost your potions.\n\n"
                + "- **Speed III (0:45)** - neovitae:flask/potency_average_speed_boost\n"
                + "- **Speed (21:20)** - neovitae:flask/length_average_speed_boost");
    }

    @Override
    protected String entryName() {
        return "Speed";
    }

    @Override
    protected String entryDescription() {
        return "Increases movement speed.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SUGAR);
    }

    @Override
    protected String entryId() {
        return "flask_speed";
    }
}
