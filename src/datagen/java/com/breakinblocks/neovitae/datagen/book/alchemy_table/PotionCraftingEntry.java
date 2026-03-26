package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class PotionCraftingEntry extends EntryProvider {

    public PotionCraftingEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Potion Crafting");
        this.pageText("The basics of [#](8B0000)Potion Crafting[#]() are known to all and sundry throughout the land. "
                + "Even common Clerics and Witches have a firm grasp of the basics of brewing - take a handful "
                + "of semi-rare ingredients, mix them together in the correct proportions, and consume the result "
                + "(while holding your nose, if needed), or throw it at your foes.");

        this.page("intro2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The addition of [#](8B0000)Life Essence[#]() and some frankly ingenious Sanguimancy has allowed you "
                + "to turbocharge the practice, however.\\\n\\\n"
                + "By imbuing ordinary [#](8B0000)Glass Bottles[#]() with your powers, you are able to hold far more than a "
                + "single swig of liquid, and with the use of various [#](8B0000)Catalysts[#]() you have figured out how to "
                + "combine multiple effects in one flask without them muddying together and cancelling out.");

        this.page("flask", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Alchemy Flask");
        this.pageText("Craft the [#](8B0000)Alchemy Flask[#]() in the Ara Vitae (recipe: neovitae:ara_vitae/alchemy_flask). "
                + "A sturdy Flask that is far more capacious than any measly bottle!");

        this.page("effects", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Of course, a bottle, no matter how advanced, is all but useless without something to fill it."
                + "\\\n\\\nAlongside all the usual well known [#](8B0000)Effects[#]() such as [#](8B0000)Water Breathing[#](), [#](8B0000)Regeneration[#](), "
                + "or [#](8B0000)Night Vision[#](), we have also perfected a variety of other, more specialised recipes, such as "
                + "[#](8B0000)Flight[#](), [#](8B0000)Obsidian Cloak[#](), or even [#](8B0000)Passive[#](). These potions and many more are documented "
                + "further on in the book.");

        this.page("refill", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Once you have used up any kind of alchemy flask, simply wash out and refill it with water, "
                + "to get a fresh new flask ready for use. View the Alchemy Flask recipe in JEI.");

        this.page("splash_linger", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Flasks also have their equivalent to [#](8B0000)Splash[#]() and [#](8B0000)Lingering Potions[#]().\\\n\\\n"
                + "[#](8B0000)Splash Alchemy Flask[#]() (recipe: neovitae:flask/flask_splash)\n"
                + "[#](8B0000)Lingering Alchemy Flask[#]() (recipe: neovitae:flask/flask_lingering)\\\n\\\n"
                + "[#](8B0000)Lingering Potions[#]() can be combined with 8 [#](8B0000)Amethyst Throwing Daggers[#]() in the "
                + "[#](8B0000)Alchemical Reaction Chamber[#]() to create [#](8B0000)Tipped Amethyst Throwing Daggers[#](). "
                + "Any entity hit by one of these daggers will have the potion's effects transferred to it.");

        this.page("multi_effects", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("You may recall the ability to make multiple [#](8B0000)effects[#]() in a single [#](8B0000)Flask[#](). "
                + "Luckily, this process couldn't be easier. Simply brew up a potion, and use the resulting "
                + "Flask in place of your empty Flask in the second brewing.\\\n\\\n"
                + "For example, if you made a Flask "
                + "of [#](8B0000)Bounce[#](), then took that flask and used it as an ingredient in a flask of [#](8B0000)Night Vision[#](), "
                + "you'd end up with an 8-dose Flask that gives you [#](8B0000)Bounce[#]() and [#](8B0000)Night Vision[#]() with every swig!");

        this.page("catalysts_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Next up, we have [#](8B0000)Catalysts[#](). There are a few kinds of catalyst, with a few different "
                + "effects, so let's start off with the simplest three - [#](8B0000)Simple Catalyst[#](), [#](8B0000)Small Power Catalyst[#](), "
                + "and [#](8B0000)Small Lengthening Catalyst[#](). Of these, the first is the most straightforward - it provides "
                + "a base to almost every Alchemical Potion, much as [#](8B0000)Nether Wart[#]() does for standard Potions.");

        this.page("simple_catalyst", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Simple Catalyst");
        this.pageText("Craft the [#](8B0000)Simple Catalyst[#]() in the Alchemy Table (recipe: neovitae:alchemytable/simple_catalyst). "
                + "A good base for nearly any effect. Stock up, you'll want a lot of these.");

        this.page("power_catalyst", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Small Power Catalyst");
        this.pageText("Craft the [#](8B0000)Small Power Catalyst[#]() in the Alchemy Table (recipe: neovitae:alchemytable/mundane_power). "
                + "Power Catalysts increase the potency of the topmost effect inside the Flask, while decreasing the "
                + "duration of the effect. It is roughly analogous to [#](8B0000)Glowstone[#]() in more standard potions.\\\n\\\n"
                + "If the most recently applied effect cannot accept the catalyst, it will attempt to boost the "
                + "second topmost, and so on.");

        this.page("lengthening_catalyst", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Small Lengthening Catalyst");
        this.pageText("Craft the [#](8B0000)Small Lengthening Catalyst[#]() in the Alchemy Table (recipe: neovitae:alchemytable/mundane_lengthening). "
                + "Lengthening Catalysts increase the duration of the topmost effect inside the Flask. It is roughly "
                + "analogous to [#](8B0000)Redstone[#]() in more standard potions.\\\n\\\n"
                + "If the most recently applied effect cannot "
                + "accept the catalyst, it will attempt to boost the second topmost, and so on.");

        this.page("combinational_catalyst", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Combinational Catalyst");
        this.pageText("Craft the [#](8B0000)Combinational Catalyst[#]() in the Alchemy Table (recipe: neovitae:alchemytable/combinational). "
                + "[#](8B0000)Combinational Catalysts[#]() are a little more niche - they take two defined effects and "
                + "synthesise a third, related effect from them. For example, a Flask with [#](8B0000)Suspended[#]() and "
                + "[#](8B0000)Levitation[#]() would, when brewed with this catalyst, produce a Flask of [#](8B0000)Flight[#]().");

        this.page("filling_agent", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Weak Filling Agent");
        this.pageText("Craft the [#](8B0000)Weak Filling Agent[#]() in the Alchemy Table (recipe: neovitae:alchemytable/weak_filling). "
                + "Filling Agents can be used to refresh the contents of a Flask, at the cost of losing some of "
                + "the effects - the Weak Filling Agent can only preserve the topmost effect on the Flask. "
                + "If you wish to preserve a different effect, consider using the [#](8B0000)Simple Cycling Catalyst[#]().");

        this.page("cycling_catalyst", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Simple Cycling Catalyst");
        this.pageText("Craft the [#](8B0000)Simple Cycling Catalyst[#]() in the Alchemy Table (recipe: neovitae:alchemytable/cycling_catalyst). "
                + "The Cycling Catalyst can be used to alter the order of effects inside the Flask, and thus "
                + "change which effect is altered by another Catalyst or Agent.");

        this.page("advanced_intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Potion Crafting 201");
        this.pageText("If the potions you've crafted so far just aren't enough for you, then perhaps you need "
                + "to do a little exploring? With some [#](8B0000)Glow berries[#](), some [#](8B0000)Cobbled Deepslate[#]() and a sprinkling "
                + "of [#](8B0000)Hellforged Sand[#](), you can boost the duration and power of your flasks significantly.");

        this.page("strengthened_catalyst", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Strengthened Catalyst");
        this.pageText("Craft the [#](8B0000)Strengthened Catalyst[#]() in the Alchemy Table (recipe: neovitae:alchemytable/strengthened_catalyst). "
                + "A more potent base for more potent catalysts.");

        this.page("avg_lengthening", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Standard Lengthening");
        this.pageText("Craft the [#](8B0000)Standard Lengthening Catalyst[#]() in the Alchemy Table (recipe: neovitae:alchemytable/average_lengthening). "
                + "The Standard Lengthening Catalyst will increase the duration of the topmost effect from 3:00 to "
                + "21:20. Multiply that by the 8 doses each flask provides, and that's a heck of a long time!");

        this.page("avg_power", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Standard Power Catalyst");
        this.pageText("Craft the [#](8B0000)Standard Power Catalyst[#]() in the Alchemy Table (recipe: neovitae:alchemytable/average_power). "
                + "The Standard Power Catalyst is a souped-up version of the Small Power Catalyst, allowing you "
                + "to get up to level III potion effects in your flask, while reducing the duration to just "
                + "45 seconds per dose.");
    }

    @Override
    protected String entryName() {
        return "Potion Crafting";
    }

    @Override
    protected String entryDescription() {
        return "Advanced potion brewing with Alchemy Flasks and Catalysts.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.ALCHEMY_FLASK.get());
    }

    @Override
    protected String entryId() {
        return "potion_crafting";
    }
}
