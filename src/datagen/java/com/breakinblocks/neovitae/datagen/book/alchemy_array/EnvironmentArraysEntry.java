package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.world.item.Items;

public class EnvironmentArraysEntry extends EntryProvider {

    public EnvironmentArraysEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Environmental Arrays");
        this.pageText("These arrays reshape the world around them.\\\n\\\n"
                + "The [#](8B0000)Tempest Array[#]() commands the weather, toggling rain on or off at a cost "
                + "of 500 EV from the owner's soul network. Like the time arrays, it is consumed on use.\\\n\\\n"
                + "The [#](8B0000)Growth Array[#]() coaxes nearby crops and plants to grow faster in a 2-block "
                + "radius, applying a gentle acceleration each second.");

        this.page("freeze", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Freeze Array[#]() reaches out to nearby water sources and converts them "
                + "to ice, while covering exposed solid ground in a thin layer of snow. The effect spreads "
                + "in a 3-block radius.\\\n\\\n"
                + "Once the array has frozen everything in reach, it dissipates.\\\n\\\n"
                + "[#](4A0080)The vitaemancer's will made manifest in frost and bloom.[#]()");

        this.page("undertow", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Undertow Array[#]() is scribed with [#](8B0000)Kelp[#]() and awakened by "
                + "[#](8B0000)Redstone Dust[#](). Once active, it opens a [#](8B0000)vitaemantic current[#]() "
                + "through the open column of air directly above the glyph, lifting or drowning any entity that "
                + "enters, no water required.\\\n\\\n"
                + "[#](8B0000)Right-click with an empty hand[#]() to reverse the flow. Upward currents push "
                + "entities skyward; downward currents drag them to the bottom, mirroring a magma block's pull. "
                + "The array is persistent and remembers its direction across world reloads.\\\n\\\n"
                + "[#](8B0000)Scaling[#](): the strength of the current is read live from the array's own slots. "
                + "Adding more [#](8B0000)Redstone Dust[#]() to the redstone slot strengthens the acceleration; "
                + "adding more [#](8B0000)Kelp[#]() to the kelp slot raises the terminal velocity. Simply "
                + "right-click the array with additional ingredients to stack them into place.\\\n\\\n"
                + "[#](2E8B57)Tip: the Arcane Scribe Tool can inscribe arrays directly into water source blocks, "
                + "so an Undertow Array can still be placed at the bottom of a pool without draining it first.[#]()");
    }

    @Override
    protected String entryName() {
        return "Environmental Arrays";
    }

    @Override
    protected String entryDescription() {
        return "Arrays that command weather, growth, and frost.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SNOWBALL);
    }

    @Override
    protected String entryId() {
        return "environment_arrays";
    }
}
