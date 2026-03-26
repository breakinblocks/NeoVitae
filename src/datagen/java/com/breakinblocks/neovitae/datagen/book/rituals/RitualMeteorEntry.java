package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class RitualMeteorEntry extends EntryProvider {

    public RitualMeteorEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/meteor"))
                .withMultiblockName("Mark of the Falling Tower")
                .withText(this.context().pageText()));
        this.pageText("Use a [#](8B0000)Ritual Diviner [Dusk][#]() for easier construction.");

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("Using the vast powers at your disposal, the most logical conclusion is that it is a good idea to summon a meteor from outer space to crash upon the world."
                + "\\\n\\\nBy dropping an appropriate offering on top of the MRS, the ritual will consume the item (plus a large amount of LP, default of a million LP) and summon a meteor at the build height of the map. Once the meteor hits something, it will detonate and leave behind many resources to mine and process.");

        this.page("offerings", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Meteor Offerings");
        this.pageText("The size and resources gained vary based on the offering given. Normal offerings include: [#](8B0000)Diamond[#](), [#](8B0000)Block of Iron[#](), [#](8B0000)Glowstone Dust[#](), and [#](8B0000)Stone[#]()."
                + "\\\n\\\nMod items include: Ice and Fire [#](8B0000)Dragon Bones[#](), AE2 [#](8B0000)Certus Quartz[#](), Create [#](8B0000)Andesite Alloy[#](), IE [#](8B0000)Copper Wire Coil[#](), Mystical Agriculture [#](8B0000)Prosperity Shard[#](), Thermal [#](8B0000)RF Coil[#](), and Mekanism [#](8B0000)Advanced Alloy[#]().");
    }

    @Override
    protected String entryName() {
        return "Mark of the Falling Tower";
    }

    @Override
    protected String entryDescription() {
        return "Summons a resource-filled meteor.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.FIRE_CHARGE);
    }

    @Override
    protected String entryId() {
        return "ritual_meteor";
    }
}
