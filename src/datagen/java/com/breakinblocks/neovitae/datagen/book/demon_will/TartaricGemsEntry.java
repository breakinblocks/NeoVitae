package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class TartaricGemsEntry extends EntryProvider {

    public TartaricGemsEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tartaric Gems");
        this.pageText("**Demon Will** is a very useful resource, but the fragments you have been getting so far "
                + "are decidedly lacking in power. What you need is a storage item; A **Tartaric Gem** seems "
                + "just the thing.\\\n\\\n"
                + "What's more, it can absorb any leftover **Demon Will** you might have lying "
                + "around. Just drop them onto the floor and your shiny new gem will absorb them.");

        this.page("petty", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Petty Tartaric Gem");
        this.pageText("Craft the Petty Tartaric Gem in the Hellfire Forge.\\\n\\\n"
                + "Your first gem will hold a maximum of 64 Will. Much more compact than before!\\\n\\\n"
                + "If you ever want to transfer Will from one gem to another, simply right-click while holding "
                + "the gem you want to empty, and it will transfer its will into the first valid gem it finds "
                + "in your inventory.");

        this.page("lesser_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Your **Petty Tartaric Gem** is a useful tool, but it's clearly lacking in power. By "
                + "carefully working it with **Diamond**, **Lapis**, and **Redstone**, you have found a way "
                + "to quadruple its storage capabilities.");

        this.page("lesser", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Lesser Tartaric Gem");
        this.pageText("Craft the Lesser Tartaric Gem in the Hellfire Forge.\\\n\\\n"
                + "This reinforced gem can hold up to 256 Will.\\\n\\\n"
                + "Note: You only need one gem when upgrading - the Hellfire Forge will draw Will from the "
                + "gem it's crafting before trying to use will from the gem in its Gem Slot. Don't worry, "
                + "the newly crafted gem will hold any leftover Will from the process.");

        this.page("common_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Your **Lesser Tartaric Gem** is a noted improvement, but once more you chafe under its "
                + "limitations. To progress further will involve focusing on your **Blood Altar**, as you "
                + "require the powers of an **Imbued Slate**. Combining this slate with your gem and further "
                + "refining it with another **Diamond** and a **Block of Gold**, you have found a way to once "
                + "again quadruple its storage capabilities.");

        this.page("common", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Common Tartaric Gem");
        this.pageText("Craft the Common Tartaric Gem in the Hellfire Forge.\\\n\\\n"
                + "This intricate gem can hold an impressive 1,024 Will.");

        this.page("greater_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("You have clearly outdone yourself with the creation of the **Common Tartaric Gem**, "
                + "but you feel there is still more you can do. However, getting more out of your gem will "
                + "involve the culmination of all your work so far.\\\n\\\n"
                + "Not only do you need a **Demonic Slate**, "
                + "you also require a **Weak Blood Shard** and a **Demon Will Crystal**. Of course, it will "
                + "come with rewards to match, powering your **Sentient Tools** like nothing you have seen before...");

        this.page("greater", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Greater Tartaric Gem");
        this.pageText("Craft the Greater Tartaric Gem in the Hellfire Forge.\\\n\\\n"
                + "This masterpiece of artifice can hold an astounding 4,096 Will.");
    }

    @Override
    protected String entryName() {
        return "Tartaric Gems";
    }

    @Override
    protected String entryDescription() {
        return "Storage vessels for Demon Will in increasing capacities.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SOUL_GEM_GREATER.get());
    }

    @Override
    protected String entryId() {
        return "tartaric_gems";
    }
}
