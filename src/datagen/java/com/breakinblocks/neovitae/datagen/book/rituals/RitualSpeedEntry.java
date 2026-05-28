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
import com.mojang.datafixers.util.Pair;

public class RitualSpeedEntry extends EntryProvider {

    public RitualSpeedEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/speed"))
                .withMultiblockName("Ritual of Speed")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("speed")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Quickened Blood");
        this.pageText("Every non-sneaking creature in the area is hurled in the [#](8B0000)Master Ritual Stone's facing direction[#](). "
                + "Approach the circle while [#](2E8B57)sneaking[#]() and the ritual instead grants you [#](8B0000)Speed II for 30 minutes[#](). "
                + "Spectators are ignored. Use the [#](8B0000)Ritual Tinkerer[#]() to rotate the master stone's facing if the launch direction is wrong.");

        this.page("activation_tip", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Pulse Activation");
        this.pageText("Because the launcher fires every tick, standing on a permanently-active circle will pin you in place. "
                + "Mount the rite on an [#](8B0000)Inverted Master Ritual Stone[#]() and wire a [#](2E8B57)pressure plate, button, or redstone pulse[#]() to it. "
                + "The ritual then runs only while the signal is active, letting you trigger a single launch as you walk over the plate without being held in the field afterwards.");

        this.page("spiritus_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus Resonance");
        this.pageText("- [#](8B0000)Raw Spiritus[#](): Scales launch velocity with chunk saturation."
                + "\n\n- [#](8B0000)Spiritus Ruina[#](): Adds further horizontal speed."
                + "\n\n- [#](8B0000)Spiritus Nihilum[#](): Only baby creatures (and players) are launched."
                + "\n\n- [#](8B0000)Spiritus Vindicta[#](): Only adult creatures are launched."
                + "\n\n- [#](8B0000)Spiritus Invictus[#](): Launched targets receive Soft Fall.");
    }

    @Override
    protected String entryName() {
        return "Ritual of Speed";
    }

    @Override
    protected String entryDescription() {
        return "Launches entities in a direction; sneak inside for a Speed II buff instead.";
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
        return "ritual_speed";
    }
}
