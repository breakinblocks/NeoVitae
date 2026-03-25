package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;

public class RitualFeatheredKnifeEntry extends EntryProvider {

    public RitualFeatheredKnifeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/feathered_knife"))
                .withMultiblockName("Ritual of the Feathered Knife")
                .withText(this.context().pageText()));
        this.pageText("Use a **Ritual Diviner [Dusk]** for easier construction.");

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("This ritual drains health from nearby players and converts it into **LP** deposited into a nearby **Blood Altar**. The efficiency is affected by **Runes of Self Sacrifice** and the **Tough Palms** Living Armor upgrade.");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Effects");
        this.pageText("- **Raw Will**: Increases the LP gained per health point."
                + "\n- **Corrosive Will**: Applies the **Incense Bonus** from a nearby **Incense Altar**."
                + "\n- **Vengeful Will**: When combined with **Steadfast** will, increases drain rate."
                + "\n- **Destructive Will**: Increases the maximum health that can be drained per tick."
                + "\n- **Steadfast Will**: Prevents the ritual from killing the player.");
    }

    @Override
    protected String entryName() {
        return "Ritual of the Feathered Knife";
    }

    @Override
    protected String entryDescription() {
        return "Drains player health for LP.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SACRIFICIAL_DAGGER.get());
    }

    @Override
    protected String entryId() {
        return "ritual_feathered_knife";
    }
}
