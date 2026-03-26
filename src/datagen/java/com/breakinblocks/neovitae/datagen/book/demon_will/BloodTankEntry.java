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
        this.pageText("The [#](8B0000)Blood Tank[#]() is a portable reservoir, capable of holding [#](4A0080)Essentia Vitae[#]() "
                + "and other fluids. Fill or drain it by right-clicking with a bucket or similar vessel. "
                + "The tank faithfully retains its contents when broken, making it ideal for transporting "
                + "large volumes between locations.\\\n\\\n"
                + "It glows faintly according to its contents. Each tier doubles the previous capacity, "
                + "beginning at [#](B8860B)16 Buckets[#]() and reaching a staggering [#](B8860B)524,288 Buckets[#]() at tier 16.");

        this.page("recipe", () -> BookHellfireForgeRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "hellfire_forge/blood_tank")));

        this.page("upgrading", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Reinforcing the Vessel");
        this.pageText("Combine two [#](8B0000)Blood Tanks[#]() with [#](8B0000)Glass[#]() and a [#](8B0000)Bloodstone[#]() in a crafting "
                + "table. The resulting tank rises one tier above the primary input, and both tanks' fluid "
                + "contents merge together.\\\n\\\n"
                + "If both tanks hold the same fluid, volumes are added. If only one contains fluid, the "
                + "result retains it. If they hold different fluids, the primary tank's contents take precedence.");

        this.page("upgrade_recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Upgrade Recipe");
        this.pageText("[#](2E8B57)Each tier doubles the previous tier's capacity, so reinforce early and often. "
                + "A few tiers of upgrading can mean the difference between a trickle and a torrent.[#]()");
    }

    @Override
    protected String entryName() {
        return "Blood Tank";
    }

    @Override
    protected String entryDescription() {
        return "A portable reservoir for Essentia Vitae and other vital fluids.";
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
