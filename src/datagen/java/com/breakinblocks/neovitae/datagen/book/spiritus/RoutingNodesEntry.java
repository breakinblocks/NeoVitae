package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;

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
                + "that transports, sorts, and filters [#](8B0000)items[#](), [#](8B0000)fluids[#](), "
                + "[#](8B0000)Forge Energy[#](), and the [#](4A0080)Aura[#]() itself "
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
        this.pageText("The brain of your network. Open its interface to review connected nodes, install "
                + "[#](8B0000)Stack[#]() and [#](8B0000)Speed[#]() upgrades, and tune how aggressively the network "
                + "transfers energy. All traffic, items, fluids, and [#](8B0000)Forge Energy[#]() passes through "
                + "the Master's governance.");

        this.page("master_energy", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Energy Throttle");
        this.pageText("Below the upgrade slots sits an [#](8B0000)Energy (FE/t)[#]() field. The value you "
                + "type is the rate the network [#](8B0000)requests[#]() from the energy channel each pulse, "
                + "defaulting to [#](8B0000)500 FE/t[#]().\\\n\\\n"
                + "The true ceiling, however, is set by the [#](8B0000)Stack Upgrades[#]() installed above. "
                + "Each upgrade raises the maximum FE/t the network can physically move. The field's label "
                + "displays this ceiling so you always know your headroom. Type a figure higher than the "
                + "ceiling if you wish, and the ceiling will prevail: the network only ever moves the "
                + "smaller of the two numbers.");

        this.page("master_energy2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Why throttle at all? A modest setting spares your generators and prevents an "
                + "entire network's draw from collapsing a fragile power grid. Start low while you wire "
                + "things up; raise the figure once you are confident your sources can sustain it.\\\n\\\n"
                + "[#](2E8B57)The throttle only affects Forge Energy. Item and fluid transfer rates scale "
                + "automatically with Stack Upgrades and cannot be limited from this field.[#]()");

        this.page("other_nodes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("A Master alone accomplishes nothing. [#](8B0000)Input Routing Nodes[#]() draw resources into "
                + "the network; [#](8B0000)Output Routing Nodes[#]() deliver them to their destination. "
                + "[#](8B0000)Routing Conduits[#]() serve as the wires between them, extending the network's "
                + "reach beyond the 16-block limit of a single connection.");

        this.page("node_recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Routing Conduit");
        this.pageText("A passive relay carrying items, fluids and energy alike. Unremarkable alone, but "
                + "essential for bridging distances between active nodes in your [#](4A0080)Routing Network[#]().");

        this.page("io_recipes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Input & Output Routing Nodes");
        this.pageText("The working hands of the network; one draws in, the other sends forth.");

        this.page("filters", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("When placed, an Input or Output Node detects every adjacent block, but it remains "
                + "[#](8B0000)dormant[#]() on every side until you configure them. No external filter items are "
                + "required; each of the six faces carries its own [#](8B0000)filter[#]() woven directly into the "
                + "node itself.\\\n\\\n"
                + "By default every side is [#](8B0000)disabled[#](); nothing flows. You must open the node's "
                + "interface and explicitly enable each face you wish to carry traffic.");

        this.page("node_image", () -> BookImagePageModel.create()
                .withImages(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/node_demo.png"))
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("An Output Node manifested in the world.");

        this.page("gui_right", () -> BookImagePageModel.create()
                .withImages(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/node_gui_right.png"))
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
                .withImages(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/node_gui_left.png"))
                .withBorder(true));

        this.page("gui_filter", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The left column carries three buttons: [#](8B0000)Enable[#](), [#](8B0000)Items/Fluids[#](), and "
                + "[#](8B0000)Mode[#](). Use [#](8B0000)Enable[#]() to wake the selected side: a disabled face blocks "
                + "everything regardless of its filter configuration.\\\n\\\n"
                + "At the center sits a nine-wide [#](8B0000)ghost grid[#](). The currently-selected side uses it "
                + "to record what it permits or denies; fill the visible slots and a fresh [#](8B0000)page[#]() opens, "
                + "paged with the arrows above. [#](B8860B)Priority[#]() buttons raise and lower the side's weight; "
                + "higher values are served first.");

        this.page("gui_items", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("With the [#](8B0000)Items[#]() tab selected, the ghost grid records which items may "
                + "pass. [#](8B0000)Left-click[#]() a slot while holding an item to copy it into the ghost; "
                + "[#](8B0000)right-click[#]() to clear.\\\n\\\n"
                + "The [#](8B0000)Mode[#]() button toggles between [#](2E8B57)Whitelist[#]() and [#](8B0000)Blacklist[#]():\\\n"
                + "- [#](2E8B57)Whitelist[#]() with an empty grid: nothing passes.\\\n"
                + "- [#](2E8B57)Whitelist[#]() with ghosts: only listed items pass.\\\n"
                + "- [#](8B0000)Blacklist[#]() with an empty grid: everything passes.\\\n"
                + "- [#](8B0000)Blacklist[#]() with ghosts: everything passes except the listed items.");

        this.page("gui_fluids", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Switching the tab to [#](8B0000)Fluids[#]() reveals a parallel filter just for liquids. "
                + "Left-click a slot while holding a [#](8B0000)bucket[#]() or any vessel containing a fluid, "
                + "and the node extracts that fluid's essence into the ghost slot. Right-click clears it.\\\n\\\n"
                + "Fluids support a third mode unavailable to items: [#](4A0080)Auto-Match[#](), which mirrors "
                + "whatever fluid is currently inside the neighbor tank. This is the default for fresh sides, "
                + "so an enabled face pointed at a Blood Tank begins moving [#](4A0080)Essentia Vitae[#]() "
                + "immediately without any further configuration.");

        this.page("keep_amounts", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Keeping a Reserve");
        this.pageText("Every [#](2E8B57)Whitelist[#]() ghost may carry a [#](8B0000)keep amount[#](). "
                + "[#](8B0000)Scroll[#]() over a ghost slot to raise or lower it; hold [#](8B0000)Shift[#]() for "
                + "larger steps and [#](8B0000)Ctrl[#]() for larger still. The figure rests in the corner of the "
                + "slot, and [#](8B0000)zero[#]() means [#](2E8B57)unlimited[#]().\\\n\\\n"
                + "On an [#](8B0000)Output[#]() face the amount is a [#](8B0000)target[#](): the node fills the "
                + "destination until it holds that many, counting what is already there, then rests. On an "
                + "[#](8B0000)Input[#]() face the amount is a [#](8B0000)reserve[#](): the node leaves that many "
                + "behind and draws only the surplus.\\\n\\\n"
                + "[#](2E8B57)Fluids honor the same rule, measured in millibuckets.[#]()");

        this.page("spiritus_channel", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Spiritus Channel");
        this.pageText("An [#](8B0000)Output Node[#]() carries a third tab, [#](8B0000)Spiritus[#](), unlike any "
                + "other. It has no ghost grid and no chosen face, for it serves no adjacent block at all: "
                + "it seeds the [#](4A0080)Aura[#]() of the very chunk the node stands in.\\\n\\\n"
                + "Name an [#](8B0000)Aspect[#]() and a [#](8B0000)Keep[#]() figure, and the node holds that "
                + "chunk at that level, drawing on any [#](8B0000)Spirit Accumulator[#]() bound to the "
                + "network. It will not push the Aura past its natural ceiling.\\\n\\\n"
                + "[#](2E8B57)Set Type to Off, or Keep to zero, and the channel sleeps.[#]()");

        this.page("auto_bind", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Instinctive Binding");
        this.pageText("Place an Input, Output, or Conduit node within [#](8B0000)16 blocks[#]() of an "
                + "existing network and it will seek a connection on its own:\\\n\\\n"
                + "- First it looks for the nearest [#](8B0000)Master Routing Node[#]() in range.\\\n"
                + "- If no Master is found, it attaches to the closest already-bound routing node, "
                + "inheriting that node's master.\\\n"
                + "- If neither exists within range, the new node simply sits unbound until you "
                + "link it manually.\\\n\\\n"
                + "A successful auto-bind manifests as a [#](AA00FF)violet thread[#]() arcing from the "
                + "new node to its chosen partner.");

        this.page("chain_binding", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Node Router");
        this.pageText("When auto-bind is insufficient, the [#](8B0000)Node Router[#]() performs manual "
                + "linking. [#](8B0000)Shift-right-click[#]() a Master to store its position; the router "
                + "holds onto that Master until you empty it on an empty block. Every subsequent "
                + "[#](8B0000)right-click[#]() on a non-master node in range binds it to the stored "
                + "Master, letting you wire an entire farm with a single selection.\\\n\\\n"
                + "If the router instead holds a non-master position, right-clicking another "
                + "non-master node links them and the stored position [#](8B0000)advances[#]() to the "
                + "just-clicked node. Walk your way down a chain and the router follows along, "
                + "linking each step to the previous one.\\\n\\\n"
                + "[#](2E8B57)Each successful link emits a violet arcane bolt so you can see which "
                + "nodes were wired together.[#]()");

        this.page("setup", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Most of the time you need only place your nodes near a [#](8B0000)Master Routing Node[#]() "
                + "and watch them bind themselves. Once the chain of connections reaches a Master and the "
                + "desired face of each node has been [#](2E8B57)enabled[#](), the network awakens. "
                + "Resources flow according to priority and the filter rules configured on each side.\\\n\\\n"
                + "If an auto-bind fails, reach for the [#](8B0000)Node Router[#]() to wire the remaining "
                + "nodes by hand.");

        this.page("network_image", () -> BookImagePageModel.create()
                .withImages(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/network_demo.png"))
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("Nodes need not connect directly to the Master; only to some node on the network.");

        this.page("unloaded_conduits", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Reach Beyond the Loaded");
        this.pageText("Your Master remembers the full shape of its network, and the [#](8B0000)Routing Conduits[#]() "
                + "threading it together need not be kept in loaded chunks for the network to function.\\\n\\\n"
                + "So long as the [#](8B0000)Master[#]() itself and whichever [#](8B0000)Input[#]() or "
                + "[#](8B0000)Output[#]() nodes you care about are within a loaded chunk, the conduits "
                + "between them may rest in the cold dark of unloaded space. Resources still flow along "
                + "the invisible line the Master remembers. Break a conduit mid-network while you are "
                + "present and the Master forgets it instantly; everything downstream recalculates.\\\n\\\n"
                + "[#](2E8B57)A practical consequence: you may run long conduit trunks through unloaded "
                + "wilderness without fear of the network severing itself.[#]()");

        this.page("self_healing", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Self-Mending Networks");
        this.pageText("A network that remembers its own shape also notices when that shape is violated. "
                + "Once per minute the Master sweeps the loaded portions of its graph, scrubbing any "
                + "position that no longer holds a routing-node block. Conduits that vanish through "
                + "sinister means, broken commands, careless world edits, or the loss of a region file "
                + "while the server slumbered, are forgotten as soon as the player revisits them.\\\n\\\n"
                + "Individual nodes are just as wary. Every Input, Output, or Conduit stamps its own "
                + "coordinates into its memory as it saves. If it later awakens at a different location, "
                + "it recognizes the tampering, discards its old allegiance, and seeks a new Master from "
                + "its new position, just as if it had been freshly placed.");

        this.page("emergency_rescan", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("For a truly ruined network, an administrator may invoke "
                + "[#](8B0000)/neovitae routing rescan[#]() while looking at, or standing near, the "
                + "afflicted [#](8B0000)Master Routing Node[#](). The Master will abandon every remembered "
                + "connection and walk the nearby loaded chunks, rebuilding its graph from scratch using "
                + "whatever routing nodes it can find. Use it after save-file surgery, chunk regeneration, "
                + "or any other catastrophe that bypasses the normal break rituals.");

        this.page("fluid_energy", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fluid & Energy Routing");
        this.pageText("Routing Nodes move more than items; they transfer [#](8B0000)fluids[#]() and [#](8B0000)Forge Energy[#]() "
                + "as well. Any [#](2E8B57)enabled[#]() side automatically carries all three resource types to "
                + "compatible adjacent blocks; you need only flip a face on in the node's interface.\\\n\\\n"
                + "An enabled side facing a [#](8B0000)Blood Tank[#]() allows [#](4A0080)Essentia Vitae[#]() to flow "
                + "through the network; one facing a machine's power input transfers energy. Energy is "
                + "gated solely by the [#](8B0000)Enable[#]() flag; it has no whitelist or blacklist of its own.");

        this.page("fluid_energy2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Fluid and energy routing share the same priority system as items. The "
                + "[#](8B0000)Stack Upgrade[#]() increases transfer rates for all three simultaneously.\\\n\\\n"
                + "The fluid filter defaults to [#](4A0080)Auto-Match[#](), which mirrors whatever fluid is "
                + "currently in the neighbor tank. Switch the fluid mode to [#](2E8B57)Whitelist[#]() or "
                + "[#](8B0000)Blacklist[#]() only when you need to constrain a tank that holds many liquids, "
                + "or to deliberately reject a specific fluid.");
    }

    @Override
    protected String entryName() {
        return "Routing Nodes";
    }

    @Override
    protected String entryDescription() {
        return "An arcane logistics network for items, fluids, energy, and Aura.";
    }

    @Override
    protected GuiSprite entryBackground() {
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
