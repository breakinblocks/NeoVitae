package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.datagen.book.page.BookHellfireForgeRecipePageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import net.minecraft.resources.Identifier;

public class DemonLanternEntry extends EntryProvider {

    public DemonLanternEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Lantern");
        this.pageText("Take a [#](8B0000)Blood Lantern[#]() back to the [#](8B0000)Hellfire Forge[#]() "
                + "and let the demonic fires work it over, and its crimson flame curdles into the same "
                + "restless hue worn by [#](8B0000)Sentient Armor[#](). The [#](8B0000)Demon Lantern[#]() "
                + "still wards away timid life, but it no longer merely quiets a place. It [#](8B0000)stirs[#]() it.");

        this.page("recipe", () -> BookHellfireForgeRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath("neovitae", "hellfire_forge/demon_lantern"))
                .withText(this.context().pageText()));
        this.pageText("Forge a [#](8B0000)Blood Lantern[#]() together with a [#](8B0000)Simple Catalyst[#](), "
                + "[#](8B0000)Soul Sand[#](), and an [#](8B0000)Emerald[#]() in a charged "
                + "[#](8B0000)Hellfire Forge[#]().");

        this.page("hunger", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("A Hungry Glow");
        this.pageText("Within sixteen blocks the lantern still smothers [#](8B0000)passive and ambient[#]() "
                + "spawns, but it now forces [#](8B0000)hostile[#]() ones into being across the same span, "
                + "wringing extra births from every surface around it. [#](2E8B57)It pays no mind to the "
                + "light[#](); a brightly lit hall will seethe with monsters all the same.\\\n\\\n"
                + "It feeds on the [#](4A0080)Spiritus[#]() steeped into the surrounding land, of any aspect: "
                + "barren ground stirs only once every few seconds, while a chunk saturated to five hundred motes "
                + "seethes anew every tick. From time to time it draws a "
                + "single mote of a random aspect down into itself as payment. Though it shines like a lantern, "
                + "it casts [#](2E8B57)no true light[#]() - the glow will neither brighten your halls nor "
                + "calm the dark it stirs.\\\n\\\n"
                + "The lantern [#](2E8B57)binds to whoever sets it down[#]() (or right-click it in hand "
                + "beforehand to bind it for another), drawing thereafter on that soul's "
                + "[#](4A0080)Soul Network[#](): a steady toll of [#](4A0080)Essentia Vitae[#]() each second it "
                + "runs, falling dormant the moment the well runs dry.\\\n\\\n"
                + "Feed it a [#](8B0000)redstone[#]() signal and it falls still; while powered it neither wards "
                + "nor stirs nor feeds.");
    }

    @Override
    protected String entryName() {
        return "Demon Lantern";
    }

    @Override
    protected String entryDescription() {
        return "A Blood Lantern reforged to seethe with hostile life and feed on spiritus.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.DEMON_LANTERN.asItem());
    }

    @Override
    protected String entryId() {
        return "demon_lantern";
    }
}
