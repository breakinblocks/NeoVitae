package com.breakinblocks.neovitae.datagen.book.tabula_vitae;

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
        this.pageTitle("The Alchemist's Craft");
        this.pageText("Potion-brewing is a pedestrian art. Any hedge-witch with a cauldron can boil nether wart "
                + "into a serviceable tincture. But a [#](4A0080)Vitaemancer[#]() does not settle for serviceable.\\\n\\\n"
                + "By weaving [#](4A0080)Essentia Vitae[#]() into the brewing process, you transcend the crude limitations "
                + "of conventional alchemy entirely.");

        this.page("intro2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Through ingenious [#](4A0080)Vitaemancy[#](), you have learned to imbue common glass with far greater "
                + "capacity than any bottle should rightly hold. More remarkable still, the use of specialized "
                + "[#](8B0000)Catalysts[#]() allows you to layer multiple effects within a single flask without them "
                + "muddying together or cancelling one another out.\\\n\\\n"
                + "The result is an elixir worthy of the name.");

        this.page("flask", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Alchemy Flask");
        this.pageText("The [#](8B0000)Alchemy Flask[#]() is forged upon the [#](8B0000)Ara Vitae[#]() (recipe: neovitae:ara_vitae/alchemy_flask). "
                + "Far sturdier and more capacious than any common bottle, it holds eight doses of whatever "
                + "elixir you fill it with, a vessel truly befitting the art.");

        this.page("effects", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("A vessel, however fine, is nothing without something to fill it.\\\n\\\n"
                + "Beyond the familiar elixirs, [#](8B0000)Water Breathing[#](), [#](8B0000)Regeneration[#](), [#](8B0000)Night Vision[#]() "
                + ", the discipline offers far stranger brews: [#](8B0000)Flight[#](), [#](8B0000)Obsidian Cloak[#](), even "
                + "[#](8B0000)Passive[#](). Each is documented in the pages that follow.");

        this.page("refill", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Once you have drained a flask to its dregs, simply rinse it with water to prepare a fresh "
                + "vessel for your next concoction. [#](2E8B57)Consult JEI for the refilling recipe.[#]()");

        this.page("splash_linger", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Not every elixir need be sipped. Flasks may be transmuted into projectile forms:\\\n\\\n"
                + "[#](8B0000)Splash Alchemy Flask[#]() (recipe: neovitae:flask/flask_splash)\n"
                + "[#](8B0000)Lingering Alchemy Flask[#]() (recipe: neovitae:flask/flask_lingering)\\\n\\\n"
                + "A [#](8B0000)Lingering Flask[#]() may be combined with 8 [#](8B0000)Amethyst Throwing Daggers[#]() in the "
                + "[#](8B0000)Athanor[#]() to forge [#](8B0000)Tipped Amethyst Throwing Daggers[#](). "
                + "Any creature struck by such a dagger suffers the flask's effects on impact.");

        this.page("multi_effects", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Layering multiple effects within a single flask is elegantly simple. Brew your first "
                + "elixir, then use the resulting flask in place of an empty one for the second brewing.\\\n\\\n"
                + "For example, brew a flask of [#](8B0000)Bounce[#](), then use that same flask as the base for a "
                + "[#](8B0000)Night Vision[#]() recipe. The result: an 8-dose flask bestowing both "
                + "[#](8B0000)Bounce[#]() and [#](8B0000)Night Vision[#]() with every sip.");

        this.page("catalysts_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Central to the art of flask-brewing are [#](8B0000)Catalysts[#](), reagents that shape and "
                + "amplify your elixirs. Begin with these three: the [#](8B0000)Simple Catalyst[#](), the "
                + "[#](8B0000)Small Power Catalyst[#](), and the [#](8B0000)Small Lengthening Catalyst[#](). The first serves "
                + "as the foundation for nearly every alchemical brew, much as [#](8B0000)Nether Wart[#]() anchors "
                + "lesser potions.");

        this.page("simple_catalyst", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Simple Catalyst");
        this.pageText("The [#](8B0000)Simple Catalyst[#]() is brewed at the Tabula Vitae (recipe: neovitae:alchemytable/simple_catalyst). "
                + "It forms the alchemical bedrock of nearly every flask recipe. "
                + "[#](2E8B57)Prepare these in quantity; you will consume them ceaselessly.[#]()");

        this.page("power_catalyst", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Small Power Catalyst");
        this.pageText("The [#](8B0000)Small Power Catalyst[#]() is brewed at the Tabula Vitae (recipe: neovitae:alchemytable/mundane_power). "
                + "It intensifies the topmost effect within a flask at the cost of shortened duration, "
                + "analogous to what [#](8B0000)Glowstone[#]() achieves in cruder potions.\\\n\\\n"
                + "If the topmost effect cannot accept further potency, the catalyst reaches deeper, "
                + "boosting the next eligible effect in sequence.");

        this.page("lengthening_catalyst", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Small Lengthening Catalyst");
        this.pageText("The [#](8B0000)Small Lengthening Catalyst[#]() is brewed at the Tabula Vitae (recipe: neovitae:alchemytable/mundane_lengthening). "
                + "It stretches the duration of the topmost effect within a flask, much as [#](8B0000)Redstone[#]() "
                + "prolongs a common potion.\\\n\\\n"
                + "As with Power Catalysts, if the topmost effect cannot be further extended, the catalyst "
                + "seeks the next eligible effect below it.");

        this.page("combinational_catalyst", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Combinational Catalyst");
        this.pageText("The [#](8B0000)Combinational Catalyst[#]() is brewed at the Tabula Vitae (recipe: neovitae:alchemytable/combinational). "
                + "This is the reagent of [#](4A0080)synthesis[#](); it fuses two existing effects within a flask into "
                + "an entirely new one. A flask bearing both [#](8B0000)Suspended[#]() and [#](8B0000)Levitation[#](), when treated "
                + "with this catalyst, yields the coveted elixir of [#](8B0000)Flight[#]().");

        this.page("filling_agent", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Weak Filling Agent");
        this.pageText("The [#](8B0000)Weak Filling Agent[#]() is brewed at the Tabula Vitae (recipe: neovitae:alchemytable/weak_filling). "
                + "It replenishes a partially drained flask, though at a cost; only the topmost effect survives "
                + "the restoration. All others are lost.\\\n\\\n"
                + "[#](2E8B57)If you wish to preserve a deeper effect, first reorder the flask with a "
                + "Simple Cycling Catalyst.[#]()");

        this.page("cycling_catalyst", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Simple Cycling Catalyst");
        this.pageText("The [#](8B0000)Simple Cycling Catalyst[#]() is brewed at the Tabula Vitae (recipe: neovitae:alchemytable/cycling_catalyst). "
                + "It rotates the order of effects layered within a flask, bringing a different effect to the "
                + "top position, and thus changing which effect is targeted by subsequent catalysts or agents.");

        this.page("advanced_intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Advanced Catalysis");
        this.pageText("Should your current brews prove insufficient, the answer lies in deeper materials. "
                + "With [#](8B0000)Glow Berries[#](), [#](8B0000)Cobbled Deepslate[#](), and a measure of "
                + "[#](8B0000)Hellforged Sand[#](), you may forge catalysts of considerably greater potency, "
                + "pushing both duration and power well beyond their former limits.");

        this.page("strengthened_catalyst", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Strengthened Catalyst");
        this.pageText("The [#](8B0000)Strengthened Catalyst[#]() is brewed at the Tabula Vitae (recipe: neovitae:alchemytable/strengthened_catalyst). "
                + "A refined alchemical substrate that serves as the foundation for all advanced-tier catalysts.");

        this.page("avg_lengthening", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Standard Lengthening");
        this.pageText("The [#](8B0000)Standard Lengthening Catalyst[#]() is brewed at the Tabula Vitae (recipe: neovitae:alchemytable/average_lengthening). "
                + "It extends the topmost effect's duration from a mere 3:00 to a formidable 21:20. "
                + "Across eight doses, that is a truly staggering span of sustained power.");

        this.page("avg_power", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Standard Power Catalyst");
        this.pageText("The [#](8B0000)Standard Power Catalyst[#]() is brewed at the Tabula Vitae (recipe: neovitae:alchemytable/average_power). "
                + "A concentrated refinement of its lesser counterpart, it drives effects to [#](B8860B)level III[#]() "
                + "potency, though the duration contracts to a brief 45 seconds per dose. "
                + "Devastating, but fleeting.");
    }

    @Override
    protected String entryName() {
        return "The Alchemist's Craft";
    }

    @Override
    protected String entryDescription() {
        return "Flask-brewing, catalysts, and the layering of effects.";
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
