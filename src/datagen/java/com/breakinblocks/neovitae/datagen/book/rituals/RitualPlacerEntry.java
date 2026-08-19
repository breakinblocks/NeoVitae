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

public class RitualPlacerEntry extends EntryProvider {

    public RitualPlacerEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/placer"))
                .withMultiblockName("Ritual of the Mason")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("placer")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Mason's Spiritus");
        this.pageText("This ritual draws blocks from a nearby chest and places them into empty spaces within its domain, filling voids, building foundations, and shaping terrain with tireless precision. Where the [#](8B0000)Yawning of the Void[#]() devours, this circle creates.");

        this.page("shapes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Shaping the Work");
        this.pageText("The [#](8B0000)Ritual Configurator[#]() sets the shape the Mason lays into its domain:"
                + "\n\n- [#](B8860B)Solid[#](): the whole volume"
                + "\n- [#](B8860B)Shell[#](): the outer skin only"
                + "\n- [#](B8860B)Floor[#]() and [#](B8860B)Roof[#](): a single layer"
                + "\n- [#](B8860B)Walls[#](): the four upright sides"
                + "\n- [#](B8860B)Frame[#](): the twelve edges alone"
                + "\n\nA domain one block thick has no inside, so Shell fills it solid and Frame traces its outline.");

        this.page("aura", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Quickening Aura");
        this.pageText("Left to itself the Mason sets one block a pulse across a domain of 5,000. [#](8B0000)Raw Spiritus[#]() in the chunk quickens it and widens what you may mark out:"
                + "\n\n- [#](B8860B)20 raw[#](): four blocks a pulse, 20,000 of room"
                + "\n- [#](B8860B)50 raw[#](): eight blocks a pulse, 80,000 of room"
                + "\n\nThe aura is spent as it works, so the circle settles at whatever pace your supply sustains; it never stalls, only slows. Should the aura fade you keep the domain you marked, but you cannot widen it again until the Spiritus returns.");
    }

    @Override
    protected String entryName() {
        return "Ritual of the Mason";
    }

    @Override
    protected String entryDescription() {
        return "Fills the void with blocks drawn from a chest.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.DISPENSER);
    }

    @Override
    protected String entryId() {
        return "ritual_placer";
    }
}
