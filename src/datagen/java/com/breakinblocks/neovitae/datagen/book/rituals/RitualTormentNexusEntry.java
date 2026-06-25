package com.breakinblocks.neovitae.datagen.book.rituals;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public class RitualTormentNexusEntry extends EntryProvider {

    public RitualTormentNexusEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/torment_nexus"))
                .withMultiblockName("The Torment Nexus")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()\\\n\\\n"
                + "Requires an [#](8B0000)Awakened Activation Crystal[#]() to start.");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("torment_nexus")));

        this.page("purpose", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Why It Exists");
        this.pageText("On a populated server, [#](8B0000)the Well of Suffering[#]() strains under the weight"
                + " of every player's mob farm. Dozens or hundreds of mobs spawned, killed, and dropped at once,"
                + " each dragging the tick rate down a little further.\\\n\\\n"
                + "[#](4A0080)The Torment Nexus[#]() exists so endgame practitioners can extract the same EV"
                + " stream without spawning a single mob. The kills are simulated. The chunks stay quiet. The"
                + " server stays playable.");

        this.page("operation", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("How It Operates");
        this.pageText("On activation the ritual binds every [#](8B0000)mob spawner[#]() and"
                + " [#](8B0000)trial spawner[#]() inside its working area (default 11x11x11, configurable). For"
                + " each bound spawner it reads the cadence and mob roster the spawner already has, then"
                + " every second it [#](4A0080)imagines[#]() the mobs the spawner would have spawned and processes"
                + " each as a kill:\\\n\\\n"
                + "Real spawning is [#](2E8B57)suppressed[#]() entirely.\\\n"
                + "EV is paid out of the network and deposited into the nearest Ara Vitae.\\\n"
                + "Loot tables are rolled and inserted into a chest atop the Master Ritual Stone.\\\n"
                + "XP from each kill is fed into an [#](8B0000)Experience Tome[#]() in that same chest, if one is present.");

        this.page("roster", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Choosing the Quarry");
        this.pageText("The Nexus harvests whatever the bound spawners are set to spawn, so the roster is yours to"
                + " decide. To retune a spawner, inscribe an [#](8B0000)Array of Imprisonment[#]() atop it and slay the"
                + " creature you want within an [#](8B0000)11x11x11[#]() region of the array; the spawner takes on that"
                + " creature and the array is spent.\\\n\\\n"
                + "Stock a ring of spawners around the Nexus this way to farm exactly the drops you need without a"
                + " single mob ever drawing breath.");

        this.page("ethics", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("On the Ethics");
        this.pageText("Long before our generation, the elder vitaemancers warned in their journals of a working"
                + " they called [#](4A0080)the Torment Nexus[#](): a circle that would render every"
                + " bound horror's death real enough to harvest, but unreal enough to require no body.\\\n\\\n"
                + "They were quite explicit. [#](8B0000)Do not build the Torment Nexus[#](), they wrote.\\\n\\\n"
                + "At long last, we have built the Torment Nexus from the classic warning"
                + " [#](4A0080)Do Not Build the Torment Nexus[#](). The TPS gains are excellent.");

        this.page("tuning", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Pack Tuning");
        this.pageText("Two server-config knobs balance the ritual:\\\n\\\n"
                + "[#](8B0000)ev_per_kill[#](): EV deducted from your network per simulated kill"
                + " (default 75).\\\n"
                + "[#](8B0000)ev_modifier_percent[#](): scales the EV deposited per kill from 1 (1%) to 1000"
                + " (10x). Pack authors can crank this down to make the ritual a quality-of-life replacement"
                + " for old farms, or up to make it a true endgame multiplier.\\\n\\\n"
                + "Range can be tuned with the [#](2E8B57)Ritual Configurator[#]() per ritual stone.");
    }

    @Override
    protected String entryName() {
        return "The Torment Nexus";
    }

    @Override
    protected String entryDescription() {
        return "Server-friendly endgame EV: simulate spawner kills without spawning anything.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SPAWNER);
    }

    @Override
    protected String entryId() {
        return "ritual_torment_nexus";
    }
}
