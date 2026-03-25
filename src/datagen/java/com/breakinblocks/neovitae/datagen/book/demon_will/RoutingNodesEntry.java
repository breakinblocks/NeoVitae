package com.breakinblocks.neovitae.datagen.book.demon_will;

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
        this.pageText("Hauling items around by hand may be all right for some people, but we are a Sanguimancer. "
                + "What's more, we have access to **Demon Will**! Surely there's a better way to go about "
                + "things.\\\n\\\n"
                + "As such, you've managed to come up with **Routing Nodes**. These offer a powerful way to "
                + "transport, sort, and filter **items**, **fluids**, and **Forge Energy**, sending them "
                + "magically through the air as you decree.");

        this.page("components", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("**Routing Node Networks** have 3 main components: **Input Nodes**, **Output Nodes**, and "
                + "the **Master Routing Node**.\\\n\\\n"
                + "Every network requires exactly 1 **Master Routing Node**, and every other Node in the network "
                + "must be able to trace a route back to the Master, whether directly, or via other Nodes.\\\n\\\n"
                + "Input and Output nodes can transfer items, fluids, and energy from any adjacent block that "
                + "supports them.");

        this.page("master_recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Master Routing Node");
        this.pageText("Craft the Master Routing Node in the Hellfire Forge.\\\n\\\n"
                + "The Master Node will be able to accept upgrades in the future, but for now it only serves to "
                + "control and direct the network, acting as its 'Brain'.");

        this.page("other_nodes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("A **Master Routing Node** is all well and good, but without the other two node types, "
                + "it's not particularly useful on its own, so let's get on to those next.\\\n\\\n"
                + "**Input Routing Nodes** draw items in to the network, **Output Routing Nodes** export them "
                + "out again, and plain old **Routing Nodes** serve to extend the reach of your network. As "
                + "Input and Output nodes are currently otherwise identical, we will be focusing on the Input "
                + "Routing Node unless otherwise specified.");

        this.page("node_recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Routing Node");
        this.pageText("Craft the Routing Node in the Hellfire Forge.\\\n\\\n"
                + "Doesn't do a whole lot on its own, but can be used to extend **Routing Networks** beyond "
                + "the 16-block reach of a single connection.");

        this.page("io_recipes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Input & Output Routing Nodes");
        this.pageText("Craft the Input and Output Routing Nodes in the Hellfire Forge.");

        this.page("filters", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("When you place an Input or Output Node down, it will automatically connect to **all "
                + "adjacent inventories**, but it won't do anything without a **Filter** of some kind in at "
                + "least one of its side's slots. For example, you could use a **Standard Item Filter** set to "
                + "**Iron Ore** on top of a **Furnace**, a second filter set to coal on the side of the "
                + "furnace, and an Input Node underneath set to Iron Ingots.");

        this.page("node_image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/node_demo.png"))
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("An Output Node in-world.");

        this.page("gui_right", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/node_gui_right.png"))
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("Once we've got our nodes set up, let's open up the Node GUI.");

        this.page("gui_directions", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Over on the right, we have our cardinal directions - **D**own, **U**p, **N**orth, "
                + "**S**outh, **W**est, and **E**ast. You'll notice that a little picture of a block appears "
                + "on some buttons, representing what's on that side of the node.\\\n\\\n"
                + "The GUI will open up on the "
                + "side facing an attached inventory, or Down if no inventory is present. The buttons themselves "
                + "follow the direction the player is facing, so the top button is 'forwards', the left button "
                + "is 'left', etcetera.");

        this.page("gui_left", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/node_gui_left.png"))
                .withBorder(true));

        this.page("gui_filter", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Over on the left, you'll see a space for us to insert a Filter for the selected side, "
                + "and a Priority. Bigger numbers = more important.\\\n\\\n"
                + "Nodes can have one filter per side - so we'll select the side we want, and put our filter "
                + "in it.");

        this.page("setup", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Once your Input and Output Nodes have been set up, have been linked together with the "
                + "help of a **Node Router** in a network that includes exactly one **Master Routing Node**, "
                + "and both have item filters inserted to the correct sides, you should be good to go! Items "
                + "will be routed according to the priorities and the rules contained within your filters.");

        this.page("network_image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/routing/network_demo.png"))
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("Nodes do not have to be linked directly to the **Master Routing Node**, merely to any "
                + "node on the network.");

        this.page("fluid_energy", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fluid & Energy Routing");
        this.pageText("Routing nodes don't just move items - they also transfer **fluids** and **Forge Energy** "
                + "(FE/RF). Any side with a filter installed will automatically route all three resource types "
                + "to adjacent blocks that support them.\\\n\\\n"
                + "For example, placing a filter on the side facing a **Blood Tank** will allow **Life Essence** "
                + "to flow through the network, while a filter facing a machine's power input will transfer energy.");

        this.page("fluid_energy2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Fluid and energy routing share the same priority system as items - higher priority sides "
                + "are served first. The **Stack Upgrade** increases the transfer rate for all three resource "
                + "types simultaneously.\\\n\\\n"
                + "No special filter configuration is needed for fluids or energy - any routing filter in the "
                + "slot will enable transfer on that side for all resource types.");
    }

    @Override
    protected String entryName() {
        return "Routing Nodes";
    }

    @Override
    protected String entryDescription() {
        return "A magical network for transporting items, fluids, and energy.";
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
