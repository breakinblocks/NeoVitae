package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class RitualLavaEntry extends EntryProvider {

    public RitualLavaEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/lava"))
                .withMultiblockName("Serenade of the Nether")
                .withText(this.context().pageText()));
        this.pageText("Use a [#](8B0000)Ritual Diviner[#]() for easier construction.");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withTitle("Ritual Stats")
                .withText(RitualStatsHelper.generateStats("lava")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("This ritual places lava source blocks above the [#](8B0000)Master Ritual Stone[#](). A powerful ritual for generating lava on demand.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Effects");
        this.pageText("- [#](8B0000)Raw Will[#](): Allows lava to be pumped into a tank placed above the ritual instead of placed in the world, reducing [#](8B0000)LP[#]() cost."
                + "\n- [#](8B0000)Corrosive Will[#](): Sets nearby hostile mobs on fire. Particularly useful against denizens of The Nether."
                + "\n- [#](8B0000)Vengeful Will[#](): Applies the [#](8B0000)Fire Fuse[#]() debuff to hostile mobs. When the debuff ends, they will do a wonderful impression of a firework."
                + "\n- [#](8B0000)Destructive Will[#](): Increases the rate of lava generation."
                + "\n- [#](8B0000)Steadfast Will[#](): Grants [#](8B0000)Fire Resistance[#]() to nearby players.");
    }

    @Override
    protected String entryName() {
        return "Serenade of the Nether";
    }

    @Override
    protected String entryDescription() {
        return "Generates lava source blocks.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.LAVA_BUCKET);
    }

    @Override
    protected String entryId() {
        return "ritual_lava";
    }
}
