package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.datagen.book.page.BookHellfireForgeRecipePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class BloodTankEntry extends EntryProvider {

    public BloodTankEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Blood Tank");
        this.pageText("The [#](8B0000)Blood Tank[#]() is a portable fluid storage block capable of holding [#](8B0000)Life Essence[#]() "
                + "and other fluids. It can be filled and emptied by right-clicking with a bucket or other fluid "
                + "container. The tank retains its contents when broken, making it easy to transport large "
                + "quantities of [#](8B0000)Life Essence[#]() between locations.\\\n\\\n"
                + "The tank emits light based on the fluid it contains, and each tier doubles the capacity of "
                + "the previous one, starting at [#](8B0000)16 Buckets[#]() and reaching a maximum of [#](8B0000)524,288 Buckets[#]() "
                + "at tier 16.");

        this.page("recipe", () -> BookHellfireForgeRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "hellfire_forge/blood_tank")));

        this.page("upgrading", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Upgrading");
        this.pageText("To increase its capacity, combine two [#](8B0000)Blood Tanks[#]() with [#](8B0000)Glass[#]() and a [#](8B0000)Bloodstone[#]() "
                + "in a crafting table. The resulting tank will be one tier higher than the primary input, and the "
                + "fluid contents of both tanks will be merged together.\\\n\\\n"
                + "If both tanks hold the same fluid, their amounts are added together. If only one contains "
                + "fluid, the result keeps that fluid. If they hold different fluids, the primary tank's fluid "
                + "is kept.");

        this.page("upgrade_recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Upgrade Recipe");
        this.pageText("Place two [#](8B0000)Blood Tanks[#](), [#](8B0000)Glass[#](), and a [#](8B0000)Bloodstone[#]() in a crafting table to upgrade. "
                + "Each tier doubles the previous tier's capacity, so upgrading early and often is worthwhile.");
    }

    @Override
    protected String entryName() {
        return "Blood Tank";
    }

    @Override
    protected String entryDescription() {
        return "A portable fluid storage block for Life Essence.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.BLOOD_TANK.asItem());
    }

    @Override
    protected String entryId() {
        return "blood_tank";
    }
}
