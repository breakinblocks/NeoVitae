package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class RoutingNodesEntry extends EntryProvider {

    public RoutingNodesEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Routing Nodes");
        this.pageText("Hauling materials by hand is beneath a blood mage of your stature. With [#](8B0000)Spiritus[#]() "
                + "at your command, you have devised [#](8B0000)Routing Nodes[#](), an arcane logistics network "
                + "that transports, sorts, and filters [#](8B0000)items[#](), [#](8B0000)fluids[#](), and [#](8B0000)Forge Energy[#]() "
                + "through invisible channels at your decree.");

        this.page("components", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("A [#](4A0080)Routing Network[#]() comprises three elements: [#](8B0000)Input Nodes[#](), [#](8B0000)Output Nodes[#](), "
                + "and a single [#](8B0000)Master Routing Node[#]().\\\n\\\n"
                + "Every network requires exactly one Master. All other nodes must trace a path back to it, "
                + "whether directly or through intermediaries.\\\n\\\n"
                + "Input and Output Nodes interact with any adjacent block that supports item, fluid, or "
                + "energy transfer.");

        this.page("master_recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Master Routing Node");
        this.pageText("The brain of your network. It accepts upgrades to improve throughput and speed, "
                + "but for now it serves a single vital purpose: directing all traffic.");

        this.page("other_nodes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("A Master alone accomplishes nothing. [#](8B0000)Input Routing Nodes[#]() draw resources into "
                + "the network; [#](8B0000)Output Routing Nodes[#]() deliver them to their destination. Plain "
                + "[#](8B0000)Routing Nodes[#]() serve as relays, extending the network's reach beyond the "
                + "16-block limit of a single connection.");

        this.page("node_recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Routing Node");
        this.pageText("A passive relay. Unremarkable alone, but essential for bridging distances between "
                + "active nodes in your [#](4A0080)Routing Network[#]().");

        this.page("io_recipes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Input & Output Routing Nodes");
        this.pageText("The working hands of the network; one draws in, the other sends forth.");

        this.page("filters", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("When placed, an Input or Output Node automatically detects all adjacent inventories, "
                + "but it remains dormant without a [#](8B0000)Filter[#]() in at least one of its directional slots. "
                + "For example: a [#](8B0000)Standard Item Filter[#]() set to Iron Ore atop a Furnace, a coal filter "
                + "on its side, and an Input Node beneath pulling Iron Ingots.");

        this.page("node_image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/node_demo.png"))
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("An Output Node manifested in the world.");

        this.page("gui_right", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/node_gui_right.png"))
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("Open the node's interface to configure its connections.");

        this.page("gui_directions", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("On the right: directional buttons: [#](8B0000)D[#]()own, [#](8B0000)U[#]()p, [#](8B0000)N[#]()orth, "
                + "[#](8B0000)S[#]()outh, [#](8B0000)W[#]()est, [#](8B0000)E[#]()ast. A small block icon appears on sides facing "
                + "attached inventories.\\\n\\\n"
                + "The interface opens to the side facing an inventory, or Down if none is present. "
                + "Button orientation follows your facing direction; the top button is always 'forward.'");

        this.page("gui_left", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/node_gui_left.png"))
                .withBorder(true));

        this.page("gui_filter", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("On the left: a slot for one [#](8B0000)Filter[#]() per side, and a [#](B8860B)Priority[#]() value. "
                + "Higher numbers are served first.\\\n\\\n"
                + "Select the desired side, insert your filter, and the node knows what to move and where.");

        this.page("setup", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Once your Input and Output Nodes are placed, linked via a [#](8B0000)Node Router[#]() to a network "
                + "containing exactly one [#](8B0000)Master Routing Node[#](), and each has a filter installed to the "
                + "correct side, the network awakens. Resources flow according to priority and filter rules.");

        this.page("network_image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/network_demo.png"))
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("Nodes need not connect directly to the Master; only to some node on the network.");

        this.page("fluid_energy", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fluid & Energy Routing");
        this.pageText("Routing Nodes move more than items; they transfer [#](8B0000)fluids[#]() and [#](8B0000)Forge Energy[#]() "
                + "as well. Any side with a filter installed automatically routes all three resource types "
                + "to compatible adjacent blocks.\\\n\\\n"
                + "A filter facing a [#](8B0000)Blood Tank[#]() allows [#](4A0080)Essentia Vitae[#]() to flow through the network; "
                + "one facing a machine's power input transfers energy.");

        this.page("fluid_energy2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Fluid and energy routing share the same priority system as items. The "
                + "[#](8B0000)Stack Upgrade[#]() increases transfer rates for all three simultaneously.\\\n\\\n"
                + "[#](2E8B57)No special filter configuration is needed for fluids or energy; any routing "
                + "filter in the slot enables transfer for all resource types on that side.[#]()");
    }

    @Override
    protected String entryName() {
        return "Routing Nodes";
    }

    @Override
    protected String entryDescription() {
        return "An arcane logistics network for items, fluids, and energy.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.MASTER_ROUTING_NODE.asItem());
    }

    @Override
    protected String entryId() {
        return "routing_nodes";
    }
}
