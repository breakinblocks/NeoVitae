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

public class RitualCraftingEntry extends EntryProvider {

    public RitualCraftingEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/crafting"))
                .withMultiblockName("Rhythm of the Beating Anvil")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("crafting")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Tireless Smith");
        this.pageText("A versatile and powerful ritual, the [#](8B0000)Rhythm of the Beating Anvil[#]() automates crafting — standard recipes by default, or [#](8B0000)Tabula Vitae[#]() and [#](8B0000)Hellfire Forge[#]() recipes with the proper augmentation. Its configuration is more involved than most, so study the following pages carefully."
                + "\\\n\\\nEach ritual handles exactly one recipe at a time.");

        this.page("filter_setup", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Specifying a Recipe");
        this.pageText("You must define the recipe using an [#](8B0000)Item Filter[#](). The ritual accepts only one filter at a time. The following types are recognized:"
                + "\n\n- [#](8B0000)Standard Item Filter[#](): Specifies exact items for each slot."
                + "\n\n- [#](8B0000)Tag Item Filter[#](): Uses item tags to define valid ingredients."
                + "\n\n- [#](8B0000)Mod Item Filter[#](): Accepts any item from the specified source.");

        this.page("placement", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Place the filter in an [#](8B0000)Item Frame[#]() on the ritual structure, or inside the linked chest. If multiple filters exist in the chest, only the first is used."
                + "\\\n\\\n[#](2E8B57)By default, input and output share the same chest. Use the Ritual Tinkerer to separate them.[#]()");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Resonance");
        this.pageText("- [#](8B0000)Steadfast Will[#](): Redirects crafting to a linked [#](8B0000)Hellfire Forge[#]()."
                + "\n\n- [#](8B0000)Corrosive Will[#](): Redirects crafting to a linked [#](8B0000)Tabula Vitae[#]()."
                + "\\\n\\\n[#](2E8B57)All recipes are treated as shapeless. If two of the same item are needed, specify it twice in the filter.[#]()");
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
