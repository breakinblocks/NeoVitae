package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.datagen.book.page.BookSentientUpgradeTablePageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class ChargingStrikeUpgradeEntry extends EntryProvider {

    public ChargingStrikeUpgradeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookSentientUpgradeTablePageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Charging Strike");
        this.pageText("The armour learns to channel your momentum into devastating force. Sprinting attacks "
                + "deal increased damage and knockback, up to +50%%.\\\n\\\n"
                + "[#](B8860B)Trained by[#](): Dealing damage while sprinting.\\\n\\\n"
                + "[#](B8860B)Maximum level[#](): 5");
    }

    @Override
    protected String entryName() {
        return "Charging Strike";
    }

    @Override
    protected String entryDescription() {
        return "Your momentum becomes a weapon; the faster you charge, the harder you strike.";
    }

    @Override
    protected GuiSprite entryBackground() {
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
