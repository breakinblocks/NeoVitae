package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class SpikeTrapEntry extends EntryProvider {

    public SpikeTrapEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "spike_trap"))
                .withText(this.context().pageText()));
        this.pageText("[#](8B0000)Spike traps[#]() look only mildly suspicious until they receive a redstone signal, whereupon "
                + "vicious blades jam out into whatever is in their way. Whoever (or perhaps whatever) designed these "
                + "clearly wanted to keep out intruders.");
    }

    @Override
    protected String entryName() {
        return "Spike Trap";
    }

    @Override
    protected String entryDescription() {
        return "Redstone-activated deadly traps.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(DungeonBlocks.SPIKE_TRAP.asItem());
    }

    @Override
    protected String entryId() {
        return "spike_trap";
    }
}
