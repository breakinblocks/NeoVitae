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

public class RitualYawningVoidEntry extends EntryProvider {

    public RitualYawningVoidEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/yawning_void"))
                .withMultiblockName("All Consuming Void")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("yawning_void")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Devouring Maw");
        this.pageText("The circle opens a hunger in the earth that simply [#](8B0000)consumes[#](). One block per "
                + "refresh is unmade beneath the [#](8B0000)Master Ritual Stone[#]() and returned to nothing; "
                + "no drops, no items, no echo of what it was. The scan begins at the top of the volume and works "
                + "its way downward, with the cursor saved between refreshes so a paused or interrupted dig "
                + "resumes where it left off.\\\n\\\n"
                + "The default scan volume is a small 3x3x3 box directly beneath the master stone. Use the "
                + "[#](8B0000)Ritual Configurator[#]() to expand the volume; it can reach 64 blocks straight down "
                + "and 32 wide, opening genuine chasms in the world.");

        this.page("spiritus_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus Resonance");
        this.pageText("- [#](8B0000)Raw Spiritus[#](): Accelerates the refresh rate. With enough raw aura in the "
                + "chunk, the ritual approaches one block per game tick.\\\n\\\n"
                + "- [#](8B0000)Spiritus Invictus[#](): Rather than erasing the block, [#](8B0000)moves "
                + "it[#]() to the first empty slot of a 3x3x3 placement volume directly above the master stone. "
                + "Useful for sorting blocks out of a layer without destroying them. If the placement volume is "
                + "full the ritual idles until you clear it.\\\n\\\n"
                + "- [#](8B0000)Spiritus Ruina[#](): Reads items in a chest directly above the master "
                + "stone as a [#](8B0000)whitelist[#](). Only blocks whose own item form matches one of those "
                + "items are consumed; everything else is left intact. An empty chest disables the ritual "
                + "entirely in this mode.");
    }

    @Override
    protected String entryName() {
        return "All Consuming Void";
    }

    @Override
    protected String entryDescription() {
        return "Erases the earth beneath the ritual, one block at a time.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.BLACK_STAINED_GLASS);
    }

    @Override
    protected String entryId() {
        return "ritual_yawning_void";
    }
}
