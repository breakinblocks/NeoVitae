package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import com.breakinblocks.neovitae.ritual.types.RitualCrystallumFractura;
import net.minecraft.resources.Identifier;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class RitualCrystallumFracturaEntry extends EntryProvider {

    public RitualCrystallumFracturaEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/crystallum_fractura"))
                .withMultiblockName("Ritual of Crystallum Fractura")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats(RitualCrystallumFractura.NAME)));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crystallum Fractura");
        this.pageText("A unified harvest engine that gathers fully-grown [#](8B0000)Spiritus Crystals[#]() and any cluster tagged [#](2E8B57)neovitae:geode_harvestable[#](). Within its aura, crystal growth doubles, and any Spiritus injected into a chunk is amplified by [#](B8860B)+25%%[#]().");

        this.page("how_to_generate", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Generating Spiritus");
        this.pageText("To turn this ritual into a Spiritus farm:"
                + "\n\n1. Burn Spiritus in a [#](8B0000)Vas Maleficum[#]() within range to seed the chunk."
                + "\n2. Place a [#](8B0000)Crystallarium Maleficum[#]() so it forms [#](8B0000)Spiritus Crystal[#]() clusters from the saturated Aura."
                + "\n3. The ritual's [#](B8860B)2x growth aura[#]() drives clusters to maturity at double speed."
                + "\n4. Once fully grown, the ritual auto-harvests them and pops the shards as items, ready for the next cycle."
                + "\n\n[#](2E8B57)Each Vas Maleficum-burned crystal yielded only 5 net Spiritus by hand; with the +25%% injection bonus and 2x growth, the same crystals net you considerably more per cycle.[#]()");

        this.page("fortune", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Fracture's Edge");
        this.pageText("Harvested clusters drop with [#](8B0000)scaling Fortune[#]() based on the chunk's local Raw Spiritus density:"
                + "\n\n- below [#](B8860B)30 Raw[#](): no Fortune"
                + "\n- [#](B8860B)30-100 Raw[#](): linearly scales from Fortune I to Fortune III"
                + "\n- at or above [#](B8860B)100 Raw[#](): Fortune III"
                + "\n\nWhen Fortune is active, the ritual probabilistically consumes [#](8B0000)Raw Spiritus[#]() at an average rate of one per twelve seconds; keep the chunk fed.");

        this.page("aspect_bias", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Aspect Bias");
        this.pageText("Attune the Master Ritual Stone to a single Spiritus aspect with the [#](8B0000)Ritual Reader[#]() in [#](2E8B57)Set Spiritus Consumed[#]() mode while holding exactly one matching crystal in your hotbar. The +25%% portion of any [#](8B0000)Raw Spiritus[#]() injection within the aura is then redirected to that aspect's pool, letting you farm a chosen aspect while the ritual is running.");
    }

    @Override
    protected String entryName() {
        return "Crystallum Fractura";
    }

    @Override
    protected String entryDescription() {
        return "Harvests mature crystal clusters and weaves a growth aura around them.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get());
    }

    @Override
    protected String entryId() {
        return "ritual_crystallum_fractura";
    }
}
