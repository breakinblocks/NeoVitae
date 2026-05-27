package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class DefenseArraysEntry extends EntryProvider {

    public DefenseArraysEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Defensive Arrays");
        this.pageText("Where the Spike Array punishes intruders with pain, these arrays offer more "
                + "sophisticated defenses.\\\n\\\n"
                + "The [#](8B0000)Repulsion Array[#]() creates an invisible ward that pushes hostile "
                + "creatures away in a 5-block radius. Anything that means you harm will find itself "
                + "unable to approach.\\\n\\\n"
                + "The [#](8B0000)Deflection Array[#]() projects a column of protective force above it, "
                + "reflecting any projectile that passes through back the way it came.");

        this.page("details", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The Deflection Array requires an [#](8B0000)Tabula Animata[#]() and a diamond to inscribe, "
                + "reflecting its advanced nature. It protects a column roughly 6 blocks tall and 3 blocks wide.\\\n\\\n"
                + "The Repulsion Array needs only iron and lapis, making it accessible "
                + "even to apprentice vitaemancers.\\\n\\\n"
                + "[#](4A0080)The best defense is one your enemy never sees.[#]()");
    }

    @Override
    protected String entryName() {
        return "Defensive Arrays";
    }

    @Override
    protected String entryDescription() {
        return "Wards that repel foes and deflect projectiles.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SHIELD);
    }

    @Override
    protected String entryId() {
        return "defense_arrays";
    }
}
