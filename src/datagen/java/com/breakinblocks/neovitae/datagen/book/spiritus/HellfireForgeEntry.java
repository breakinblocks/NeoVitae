package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;

public class HellfireForgeEntry extends EntryProvider {

    public HellfireForgeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Hellfire Forge");
        this.pageText("The [#](8B0000)Hellfire Forge[#]() stands as a second pillar of the art, twin to the "
                + "[#](8B0000)Ara Vitae[#]() itself. Where the altar works with [#](4A0080)Essentia Vitae[#](), the Forge "
                + "consumes [#](8B0000)Spiritus[#](), reshaping raw malice into [#](8B0000)Sentient Tools[#](), "
                + "[#](8B0000)Spiritus Gems[#](), [#](8B0000)Arcane Ash[#](), reagents, and many things besides.\\\n\\\n"
                + "No practitioner of [#](4A0080)Vitaemancy[#]() can progress far without one.");

        this.page("crafting", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "hellfire_forge")));

        this.page("gem_absorption", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Drawing from the Aura");
        this.pageText("Crafting in the Forge spends the [#](8B0000)Spiritus[#]() held in its [#](8B0000)Spiritus Gem[#](). "
                + "Left to itself that reserve would soon run dry, so the Forge draws upon its surroundings: a gem "
                + "seated in the Forge steadily drinks [#](8B0000)Spiritus[#]() from the chunk's [#](4A0080)Aura[#](), at "
                + "the same pace a [#](8B0000)Spiritus Crucible[#]() would fill it.\\\n\\\n"
                + "[#](2E8B57)Feed the local Aura with a Crystallum Fractura farm and the Forge can craft "
                + "unattended, its gem refilling as quickly as it is spent.[#]()");
    }

    @Override
    protected String entryName() {
        return "Hellfire Forge";
    }

    @Override
    protected String entryDescription() {
        return "The infernal anvil where Spiritus is shaped into power.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.HELLFIRE_FORGE.asItem());
    }

    @Override
    protected String entryId() {
        return "hellfire_forge";
    }
}
