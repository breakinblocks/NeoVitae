package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookLivingUpgradeTablePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class ChargingStrikeUpgradeEntry extends EntryProvider {

    public ChargingStrikeUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookLivingUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Charging Strike");
        this.pageText("Effect: Increases damage and knockback from sprinting attacks, up to +50%%.\\\n\\\n"
                + "Trained by: Dealing damage while sprinting.\\\n\\\nMaximum level: 5");
    }

    @Override
    protected String entryName() {
        return "Charging Strike";
    }

    @Override
    protected String entryDescription() {
        return "Bonus damage from sprinting attacks.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.GOLDEN_AXE);
    }

    @Override
    protected String entryId() {
        return "upgrade_charging_strike";
    }
}
