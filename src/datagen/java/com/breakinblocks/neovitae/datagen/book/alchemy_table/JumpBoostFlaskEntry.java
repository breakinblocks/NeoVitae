package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class JumpBoostFlaskEntry extends EntryProvider {

    public JumpBoostFlaskEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Jump Boost");
        this.pageText("**Jump Boost** increases the target's jump height by 50% per level. "
                + "It also reduces fall damage by 1 point per level.");

        this.page("recipes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flask recipes:\n"
                + "- **Jump Boost (3:00)** - neovitae:flask/jump_boost\n"
                + "- **Jump Boost (8:00)** - neovitae:flask/length_jump_boost\n"
                + "- **Jump Boost II (1:30)** - neovitae:flask/potency_jump_boost");

        this.page("advanced", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Adding Standard Catalysts can further boost your potions.\n\n"
                + "- **Jump Boost III (0:45)** - neovitae:flask/potency_average_jump_boost\n"
                + "- **Jump Boost (21:20)** - neovitae:flask/length_average_jump_boost");
    }

    @Override
    protected String entryName() {
        return "Jump Boost";
    }

    @Override
    protected String entryDescription() {
        return "Jump higher and take less fall damage.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.RABBIT_FOOT);
    }

    @Override
    protected String entryId() {
        return "flask_jump_boost";
    }
}
