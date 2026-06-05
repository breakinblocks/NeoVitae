package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class RitualMagneticEntry extends EntryProvider {

    public RitualMagneticEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/magnetism"))
                .withMultiblockName("The Endless Quarry")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("magnetism")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Deep Earth Communion");
        this.pageText("The earth surrenders her hidden veins, one block at a time. The circle scans the volume "
                + "[#](8B0000)beneath the Master Ritual Stone[#]() and reaps each ore it finds, sending it "
                + "[#](8B0000)above[#]() the master stone for collection.\\\n\\\n"
                + "Each teleport drains [#](8B0000)50 EV[#](); the ritual checks up to one hundred blocks per "
                + "refresh, moves up to [#](8B0000)three ores[#]() per refresh, and remembers where it paused so a "
                + "full sweep resumes incrementally across many refreshes. The ritual will [#](8B0000)load unloaded "
                + "chunks[#]() as the scan reaches them, so a quarry tucked into one corner of a base still reaps "
                + "ore from distant columns. Ores in claim-protected territory are left alone.");

        this.page("collection", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Receiving the Bounty");
        this.pageText("Place a [#](8B0000)container[#]() (chest, barrel, ender chest, modded storage of any "
                + "stripe) [#](8B0000)directly on top[#]() of the Master Ritual Stone, and each reaped ore is "
                + "inserted into the container as an [#](8B0000)item[#]() - one block of ore becomes one item of "
                + "the same ore.\\\n\\\n"
                + "If there is no container above the stone, or the container is full, the ritual falls back to "
                + "placing the ore [#](8B0000)as a block[#]() in the first empty slot of a [#](B8860B)3x3x3[#]() "
                + "volume directly above the master stone. Wrap that volume in walls and the ores will stack up "
                + "until you mine them out. When the volume is also full, the ritual idles patiently until you "
                + "make room.");

        this.page("foundation", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Foundation Stone");
        this.pageText("The scan radius is set by the [#](8B0000)block placed directly beneath the Master Ritual "
                + "Stone[#](). The richer the foundation, the wider the ritual's reach:\\\n\\\n"
                + "- [#](8B0000)Any other block[#](): radius 3 (7x7 footprint, down to bedrock)\\\n"
                + "- [#](8B0000)Iron Block[#](): radius 7 (15x15 footprint)\\\n"
                + "- [#](8B0000)Gold Block[#](): radius 15 (31x31 footprint)\\\n"
                + "- [#](8B0000)Diamond Block[#](): radius 31 (63x63 footprint)\\\n"
                + "- [#](8B0000)Netherite Block[#](): radius 63 ([#](2E8B57)127x127 footprint[#]())\\\n\\\n"
                + "With a netherite foundation a single ritual will, given time, hollow an entire chunk-sized "
                + "column of ore beneath itself.");
    }

    @Override
    protected String entryName() {
        return "The Endless Quarry";
    }

    @Override
    protected String entryDescription() {
        return "Teleports ores from the depths below into a placement volume above the ritual.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.IRON_INGOT);
    }

    @Override
    protected String entryId() {
        return "ritual_magnetic";
    }
}
