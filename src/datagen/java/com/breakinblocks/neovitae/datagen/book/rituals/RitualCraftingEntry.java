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

public class RitualCraftingEntry extends EntryProvider {

    public RitualCraftingEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/crafting"))
                .withMultiblockName("Rhythm of the Beating Anvil")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("crafting")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Tireless Smith");
        this.pageText("A versatile and powerful ritual, the [#](8B0000)Rhythm of the Beating Anvil[#]() automates crafting, standard recipes by default, or [#](8B0000)Tabula Vitae[#]() and [#](8B0000)Hellfire Forge[#]() recipes with the proper augmentation. Its configuration is more involved than most, so study the following pages carefully."
                + "\\\n\\\nEach ritual handles exactly one recipe at a time.");

        this.page("containers", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Specifying a Recipe");
        this.pageText("Place a container one block [#](8B0000)above[#]() the master ritual stone to serve "
                + "as the [#](8B0000)input[#](), and another one block [#](8B0000)below[#]() to serve as the "
                + "[#](8B0000)output[#](). Any block that exposes an item handler, chests, barrels, hoppers, "
                + "will suffice.\\\n\\\n"
                + "The ritual reads whatever ingredients you place into the first nine slots of the input "
                + "container and attempts to match them against a known recipe.");

        this.page("layout", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Those nine slots are treated as a [#](8B0000)3x3 crafting grid[#](). Arrange your "
                + "ingredients exactly as you would on a workbench and leave empty slots where the shape "
                + "demands them. Each time the ritual fires it consumes one of every ingredient present "
                + "and deposits the result into the output container.\\\n\\\n"
                + "[#](2E8B57)If two of the same item are needed, place a copy in each appropriate slot.[#]()");

        this.page("spiritus_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus Resonance");
        this.pageText("The default behavior mirrors a vanilla [#](8B0000)Crafting Table[#](). Feed the ritual "
                + "a fragment of spiritus and its craft shifts accordingly:\\\n\\\n"
                + "- [#](8B0000)Spiritus Invictus[#](): attempts a [#](8B0000)Hellfire Forge[#]() recipe first, "
                + "falling back to vanilla crafting if none match.\\\n"
                + "- [#](8B0000)Spiritus Ruina[#](): attempts a [#](8B0000)Tabula Vitae[#]() recipe first, "
                + "falling back to vanilla crafting if none match.\\\n\\\n"
                + "[#](2E8B57)Only one recipe is attempted per ritual pulse; queue as much raw material as "
                + "you like and the ritual will work its way through the stack.[#]()");
    }

    @Override
    protected String entryName() {
        return "Rhythm of the Beating Anvil";
    }

    @Override
    protected String entryDescription() {
        return "Automates crafting through vitaemantic labor.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.CRAFTING_TABLE);
    }

    @Override
    protected String entryId() {
        return "ritual_crafting";
    }
}
