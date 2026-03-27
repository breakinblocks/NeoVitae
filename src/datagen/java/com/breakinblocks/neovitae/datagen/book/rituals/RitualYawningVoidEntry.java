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

public class RitualYawningVoidEntry extends EntryProvider {

    public RitualYawningVoidEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/yawning_void"))
                .withMultiblockName("Yawning of the Void")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("yawning_void")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Devouring Maw");
        this.pageText("This ritual opens an insatiable hunger in the earth, consuming blocks layer by layer and collecting their drops, a quarry driven not by pistons and gears, but by [#](4A0080)vitaemantic will[#]().");

        this.page("will_effects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus Resonance");
        this.pageText("- [#](8B0000)Raw Spiritus[#](): Accelerates the excavation rate.\\\n\\\n"
                + "- [#](8B0000)Corrosive Will[#](): Enables block filtering. Place an [#](8B0000)Item Filter[#]() in the linked chest to restrict what is mined. Standard, Tag, Mod, and Composite filters are all accepted. Blacklisting works as well.\\\n\\\n"
                + "- [#](8B0000)Steadfast Will[#](): Rather than destroying blocks, relocates them above the ritual, creating a surface copy of the excavated terrain.");
    }

    @Override
    protected String entryName() {
        return "Yawning of the Void";
    }

    @Override
    protected String entryDescription() {
        return "Devours the earth, layer by insatiable layer.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.BLACK_STAINED_GLASS);
    }

    @Override
    protected String entryId() {
        return "ritual_yawning_void";
    }
}
