package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class WillCatalystsEntry extends EntryProvider {

    public WillCatalystsEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Will Catalysts");
        this.pageText("If Demon Will has one drawback, it's that collecting it is a slow, tedious process. "
                + "Even with an almost full [#](8B0000)Tartaric Gem[#]() and a [#](8B0000)Sentient Sword[#]() enchanted with "
                + "[#](8B0000)Looting III[#]() and further buffed with [#](8B0000)Plunderer's Glint II[#](), it's still a manual "
                + "process, and you have better things to do with your genius than scramble about splatting "
                + "spiders and slaying skeletons.");

        this.page("automation", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Fortunately, it's possible to completely automate this procedure, leaving you with more "
                + "time on your hands to expand your evil empire.\\\n\\\n"
                + "The first step is to get some [#](8B0000)Will Crystals[#](). We can use any kind - Raw, Steadfast, "
                + "Destructive, Vengeful or Corrosive Will, as long as we have 4 of the same kind of crystal.");

        this.page("clusters", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crystal Clusters");
        this.pageText("Craft the Crystal Clusters (Raw, Steadfast, Destructive, Vengeful, and Corrosive) "
                + "in the Hellfire Forge using 4 crystals of the matching type.\\\n\\\n"
                + "Once you have a cluster, simply place it down in any chunk, supply the chunk with will of "
                + "the matching type, and wait. Eventually, new spires will grow, just like clusters growing "
                + "atop a [#](8B0000)Crystallarium Maleficum[#]().");

        this.page("harvesting", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("You can even automate the breaking of these additional spires with the [#](8B0000)Crack of the "
                + "Fractured Crystal[#](8B0000) ritual, and the collection of the resulting crystals with the [#]()Call of "
                + "the Zephyr[#]().\\\n\\\n"
                + "With a basic [#](8B0000)Routing Node system[#](), you can even feed these excess crystals back into a "
                + "[#](8B0000)Vas Maleficum[#]() for a totally automatic, net-positive loop.\\\n\\\n"
                + "You may have noticed a bit of a problem, however - this setup is slow. Very, very slow. "
                + "Each crystal only sprouts a new spire once every few minutes, and takes almost as much will "
                + "to spawn as you gain from burning it - on average, it comes out to about 1 will / minute / spire.");

        this.page("catalyst_recipes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Will Catalysts");
        this.pageText("Craft the Will Catalysts (Raw, Steadfast, Destructive, Vengeful, and Corrosive) in "
                + "the Hellfire Forge.");

        this.page("catalyst_usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Fortunately, this is where our new friends, [#](8B0000)Will Catalysts[#]() step in. While holding "
                + "one of these Catalysts, simply right-click on a [#](8B0000)Crystal Cluster[#]() of the same type to "
                + "turbocharge its growth!\\\n\\\n"
                + "Each catalyst reduces the amount of will required to grow a spire "
                + "from 45 to just 25, and it speeds the growth up tenfold. Every dose is good for ten spires "
                + "worth of growth, which makes for a net bonus of 200 will per Catalyst.");

        this.page("double_dosing", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("You can even double-dose your clusters - though this only makes the effect last for 20 "
                + "growths instead of 10 and has no other benefit.\\\n\\\n"
                + "Of course, the canny Sanguimancer will have realised that this has replaced one manual "
                + "problem - running around and bopping monsters with a sword - with another one - running "
                + "around and bopping crystals with a catalyst.");

        this.page("full_automation", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Fortunately, there's a ritual for that too! The [#](8B0000)Gathering of the Forsaken Souls[#]() "
                + "will automatically apply catalysts to any crystals in its area of effect.\\\n\\\n"
                + "With a few farms "
                + "and a very, very clever [#](8B0000)Routing Node[#]() setup, you can automate the whole thing, top to "
                + "bottom... But as they say, that's left as an exercise for the reader.");
    }

    @Override
    protected String entryName() {
        return "Will Catalysts";
    }

    @Override
    protected String entryDescription() {
        return "Turbocharging crystal growth for automated Demon Will production.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.RAW_CRYSTAL_CATALYST.get());
    }

    @Override
    protected String entryId() {
        return "will_catalysts";
    }
}
