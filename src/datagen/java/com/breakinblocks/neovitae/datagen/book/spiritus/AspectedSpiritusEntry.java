package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.Identifier;

public class AspectedSpiritusEntry extends EntryProvider {

    public AspectedSpiritusEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Aspects of Spiritus");
        this.pageText("Unleashing [#](8B0000)Spiritus[#]() into the atmosphere has proven most fruitful, empowering "
                + "[#](8B0000)Rituals[#]() and producing [#](8B0000)Crystal Clusters[#]() of satisfying geometry. Yet these raw crystals "
                + "feel somehow... [#](4A0080)conflicted[#](), as though warring natures strain against one another within.\\\n\\\n"
                + "A focused mote of pure animus, paired with the right [#](8B0000)Spiritus Catalyst[#](), can coax these "
                + "hidden facets into purer forms.");

        this.page("image", () -> BookImagePageModel.create()
                .withImages(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/spiritus/will_splitting.png"))
                .withTitle("Aspects of Spiritus")
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("A fully-grown Raw cluster yields to the catalyst's resonance.");

        this.page("aspects", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The four distinct [#](4A0080)Aspects[#]() of Spiritus:\n\n"
                + "- [#](8B0000)Spiritus Invictus[#](): the unbroken; a fortress of intent that refuses to be undone.\n\n"
                + "- [#](8B0000)Spiritus Nihilum[#](): the final silence; weight without haste, an ending given form.\n\n"
                + "- [#](8B0000)Spiritus Vindicta[#](): the swift reckoning; vengeance taken before the offence is voiced.\n\n"
                + "- [#](8B0000)Spiritus Ruina[#](): the slow undoing; patient decay that wears all things to ruin.\\\n\\\n"
                + "Each Aspect can be burned in the [#](8B0000)Vas Maleficum[#]() just as Raw Spiritus can, feeding the "
                + "[#](8B0000)Aura[#]() with its particular resonance for your Rituals to draw upon.");

        this.page("sentient_aspects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sentient Tool Aspects");
        this.pageText("Each Aspect also transforms the behavior of your [#](8B0000)Sentient Tools[#]():\n\n"
                + "- [#](8B0000)Raw Spiritus[#](): Pure damage increase.\n\n"
                + "- [#](8B0000)Spiritus Ruina[#](): Strikes may inflict poison or wither. Otherwise identical to Raw.\n\n"
                + "- [#](8B0000)Spiritus Vindicta[#](): Moderate damage increase, heightened attack speed, and a movement speed "
                + "boon that intensifies with greater Spiritus reserves.");

        this.page("sentient_aspects2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("- [#](8B0000)Spiritus Invictus[#](): Moderate damage increase, and slaying a foe grants you a protective "
                + "shield of Absorption.\n\n"
                + "- [#](8B0000)Spiritus Nihilum[#](): The greatest raw damage of any Aspect, at the cost of reduced attack speed.");

        this.page("filling_gems", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("To fill a [#](8B0000)Spiritus Gem[#]() with a specific Aspect, place an empty gem into a "
                + "[#](8B0000)Hellfire Forge[#]() situated in the same chunk as a [#](8B0000)Vas Maleficum[#](). Feed the Vas "
                + "Maleficum with Spiritus Crystals of the desired Aspect, and the gem will drink deeply.\\\n\\\n"
                + "[#](2E8B57)Right-click while holding a Sentient Tool to recalibrate it to the dominant Aspect "
                + "in your inventory.[#]()");

        this.page("notes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Your [#](8B0000)Sentient Tools[#]() attune to whichever Aspect you carry in greatest quantity. "
                + "Ten Spiritus Ruina in one [#](8B0000)Spiritus Gem[#]() and a thousand Raw in another means the blade "
                + "remains Raw.\\\n\\\n"
                + "[#](2E8B57)The Hellfire Forge accepts any Aspect of Spiritus for crafting, so there is no need to "
                + "juggle multiple gems between stations.[#]()");
    }

    @Override
    protected String entryName() {
        return "Aspects of Spiritus";
    }

    @Override
    protected String entryDescription() {
        return "Fracturing raw Spiritus into four elemental temperaments.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SPIRITUS_VINDICTA_CRYSTAL_ITEM.get());
    }

    @Override
    protected String entryId() {
        return "aspected_spiritus";
    }
}
