package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class AspectedWillEntry extends EntryProvider {

    public AspectedWillEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Will Aspects");
        this.pageText("Unleashing [#](8B0000)Demon Will[#]() into the atmosphere was definitely an excellent idea. Not only "
                + "has it proven most useful in empowering [#](8B0000)Rituals[#](), you have also successfully condensed it "
                + "into a [#](8B0000)Crystal Cluster[#](), and are wondering what to turn your eye to next.\\\n\\\n"
                + "These [#](8B0000)Crystals[#]() feel somehow... conflicted, to you. A certain [#](8B0000)Ritual[#]() may help coax "
                + "them out into purer forms...");

        this.page("image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/demon_will/will_splitting.png"))
                .withTitle("Aspects of Will")
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Resonance of the Faceted Crystal[#]() ritual in action.");

        this.page("aspects", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Now we have [#](8B0000)Crystallized Will[#]() in four spicy new flavours! On the Water Rune we have "
                + "[#](8B0000)Steadfast Will[#](), on the Air Rune we get [#](8B0000)Destructive Will[#](), on the Fire Rune comes "
                + "[#](8B0000)Vengeful Will[#]() and on the Earth Rune we find [#](8B0000)Corrosive Will[#]().\\\n\\\n"
                + "These various new types of Will can be burned in the [#](8B0000)Demon Crucible[#]() just like Raw Will, "
                + "and from there can be fed into various Rituals to great and fascinating effect.");

        this.page("sentient_aspects", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sentient Tool Aspects");
        this.pageText("However, they also change how your Sentient Tools behave, making them more powerful.\n"
                + "- [#](8B0000)Raw Will[#](): Increases damage.\n"
                + "- [#](8B0000)Corrosive Will[#](): Attacks have a chance to apply poison or wither to your foes, otherwise same as Raw.\n"
                + "- [#](8B0000)Vengeful Will[#](): Increases damage, but not as much as Raw. Increases attack speed. Gives a movement speed buff that increases with higher amounts of Will.");

        this.page("sentient_aspects2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("- [#](8B0000)Steadfast Will[#](): Increases damage (but not as much as Raw) and grants Absorption after a kill.\n"
                + "- [#](8B0000)Destructive Will[#](): Increases damage more than any other will type, but decreases attack speed.");

        this.page("filling_gems", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("You may be wondering: \"How on earth do I get this will into a usable form?\" Well, the "
                + "answer is simple. Just place an EMPTY [#](8B0000)Tartaric Gem[#]() into a [#](8B0000)Hellfire Forge[#]() in the same "
                + "chunk as a [#](8B0000)Demon Crucible[#](), then feed the Demon Crucible with Will Crystals of the desired "
                + "aspect. Your [#](8B0000)Tartaric Gem[#]() will fill with that aspect of will. You can change which kind of "
                + "will your Sentient Tools use by pressing right-click while holding them.");

        this.page("notes", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Note that your [#](8B0000)Sentient Tools[#]() will take on the aspect of the largest amount of Will "
                + "in your inventory. If you're carrying 10 Corrosive Will in one [#](8B0000)Tartaric Gem[#](), and 1,000 "
                + "Raw Will in another, then your sword will remain Raw.\\\n\\\n"
                + "The [#](8B0000)Hellfire Forge[#]() can accept any kind of will for crafting with, so don't worry about "
                + "having to juggle multiple types of will across different [#](8B0000)Tartaric Gems[#]().");
    }

    @Override
    protected String entryName() {
        return "Demon Will Aspects";
    }

    @Override
    protected String entryDescription() {
        return "Splitting Demon Will into its four aspected forms.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.VENGEFUL_CRYSTAL.get());
    }

    @Override
    protected String entryId() {
        return "aspected_will";
    }
}
