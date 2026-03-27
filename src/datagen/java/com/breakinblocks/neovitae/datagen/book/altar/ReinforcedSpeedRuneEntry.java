package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;

public class ReinforcedSpeedRuneEntry extends EntryProvider {

    public ReinforcedSpeedRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Reinforced Speed Rune");
        this.pageText("Forged with [#](8B0000)Netherite Scrap[#]() and [#](8B0000)Intricate Hellforged Parts[#]() plundered "
                + "from the [#](4A0080)Demon Realm[#](), this rune doubles the tempo of its lesser counterpart. "
                + "The altar's transmutations quicken by [#](8B0000)+40%%[#]() per rune, additively; the basin "
                + "practically hums with impatience.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "rune_2_speed")));

        this.page("reversion", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Should you have need of the base rune again, the Athanor can strip the "
                + "reinforcement and return it to its original form.[#]()");
    }

    @Override
    protected String entryName() {
        return "Reinforced Speed Rune";
    }

    @Override
    protected String entryDescription() {
        return "A rune tempered in hellfire, doubling the altar's haste.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_2_SPEED.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_2_speed";
    }
}
