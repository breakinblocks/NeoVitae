package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;

public class RitualSentientDowngradeEntry extends EntryProvider {

    public RitualSentientDowngradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/downgrade"))
                .withMultiblockName("Penance of the Leaden Soul")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("downgrade")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Price of Power");
        this.pageText("This ritual consumes [#](8B0000)Upgrade Points[#]() (from [#](8B0000)Tomes[#](), [#](8B0000)Scraps[#](), or [#](8B0000)Synthetic Points[#]()) alongside a specific [#](8B0000)Key Item[#]() per level, inscribing [#](4A0080)Downgrades[#]() into your worn [#](8B0000)Sentient Armor[#](). Each downgrade demands a different key item.");

        this.page("downgrades", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Downgrades are harsh; they cripple specific abilities in exchange for a wealth of additional [#](8B0000)Upgrade Points[#](). This is the path of [#](4A0080)specialization[#](): sacrifice breadth to sharpen your edge in the areas that matter most.");

        this.page("synthetic", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "synthetic_point"))
                .withText(this.context().pageText()));
        this.pageText("If you lack sufficient points, you can craft [#](8B0000)Synthetic Upgrade Points[#](). Each is worth a single point, crude, but functional.");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Place your point sources and the required key items into the linked chest while wearing your [#](8B0000)Sentient Armor[#](). The ritual consumes them in order: [#](8B0000)Upgrade Scraps[#]() first, then [#](8B0000)Upgrade Tomes[#](), and finally [#](8B0000)Synthetic Points[#]().");

        this.page("details", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Each matching key item increases that downgrade by one level. Multiple downgrades can be applied at once; for example, Battle Hungry III requires 3 [#](8B0000)Rotten Flesh[#]() and items worth 35 [#](8B0000)Upgrade Points[#]()."
                + "\\\n\\\n[#](2E8B57)The ritual respects your Training Bracelet settings. Excess points are returned as Upgrade Scraps.[#]()");
    }

    @Override
    protected String entryName() {
        return "Penance of the Leaden Soul";
    }

    @Override
    protected String entryDescription() {
        return "Sacrifices ability for deeper specialization.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.UPGRADE_SCRAP.get());
    }

    @Override
    protected String entryId() {
        return "ritual_sentient_downgrade";
    }
}
