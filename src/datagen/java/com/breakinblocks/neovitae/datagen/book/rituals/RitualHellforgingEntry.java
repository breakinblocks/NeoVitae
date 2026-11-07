package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import com.breakinblocks.neovitae.ritual.types.RitualHellforging;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;

public class RitualHellforgingEntry extends EntryProvider {

    public RitualHellforgingEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/" + RitualHellforging.NAME))
                .withMultiblockName("Ritual of Hellforging")
                .withText(this.context().pageText()));
        this.pageText("The same circle as the [#](8B0000)Ritual of Sentient Evolution[#](), with its Tenebrae "
                + "stones inscribed as [#](B8860B)Deus[#]() stones instead. Wake it with a [#](8B0000)Divinus "
                + "Activation Crystal[#]().");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats(RitualHellforging.NAME)));

        this.page("offering", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Offering");
        this.pageText("Throw three things onto the circle above the [#](8B0000)Master Ritual Stone[#](): a piece of "
                + "[#](8B0000)Sentient Armor[#](), a [#](8B0000)Sentient tool[#]() or the [#](8B0000)Lex Vitae[#](); "
                + "a [#](8B0000)Prismatic Spiritus Gem[#](); and a [#](8B0000)Hellforged Ingot[#](). The gem and the "
                + "ingot are consumed and the item is Hellforged. One item is forged at a time.\\\n\\\n"
                + "A chestplate must first ascend to [#](B8860B)600 Upgrade Points[#]() through the "
                + "[#](8B0000)Deus Sentient Evolution[#](). Other pieces and tools need no preparation.");

        this.page("armor", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Hellforged Armor");
        this.pageText("Each Hellforged piece gains armor, toughness and knockback resistance to match Netherite, "
                + "and no longer burns in fire or lava.\\\n\\\n"
                + "A Hellforged chestplate holds [#](B8860B)100[#]() more Upgrade Points, to "
                + "[#](B8860B)700[#](), and learns [#](B8860B)half again as fast[#]() from training. Tomes and "
                + "rituals are unaffected.");

        this.page("tools", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Hellforged Tools");
        this.pageText("A Hellforged Sentient tool mines, drops and lasts like Netherite even with no Spiritus "
                + "carried, deals [#](B8860B)30%% more damage[#]() and drains a quarter less Spiritus per swing.\\\n\\\n"
                + "The Lex Vitae takes the same damage and drain bonuses, grows half again as fast, and can switch "
                + "between Fortune and Silk Touch: hold sneak and press the [#](8B0000)Cycle Mode[#]() key.");

        this.page("overcharge", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Overcharge");
        this.pageText("A Hellforged tool or Lex Vitae keeps growing stronger past "
                + "[#](4A0080)4,000 Spiritus[#](). Each doubling of what you carry adds the same amount, up to "
                + "[#](4A0080)32,000[#](), where the bonuses Spiritus grants to damage, speed, drops and reach are "
                + "half again their usual peak. "
                + "The drain per swing rises with it.");
    }

    @Override
    protected String entryName() {
        return "Ritual of Hellforging";
    }

    @Override
    protected String entryDescription() {
        return "Hellforges Sentient Armor, Sentient tools and the Lex Vitae.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.HELLFORGED_INGOT.get());
    }

    @Override
    protected String entryId() {
        return "ritual_" + RitualHellforging.NAME;
    }
}
