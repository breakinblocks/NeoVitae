package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class SentientToolsEntry extends EntryProvider {

    public SentientToolsEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sentient Tools");
        this.pageText("The success of the [#](8B0000)Sentient Sword[#]() has inspired further experimentation. If a blade "
                + "can be imbued with [#](4A0080)demonic will[#](), why not a pickaxe? An axe? A shovel?\\\n\\\n"
                + "[#](2E8B57)All Sentient equipment can be repaired with Crystallized Will in an Anvil, and all are "
                + "highly receptive to enchantment.[#]()");

        this.page("pickaxe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sentient Pickaxe");
        this.pageText("Without Will to sustain it, this pick is scarcely better than the [#](8B0000)Iron Pickaxe[#]() "
                + "from which it was wrought. Feed it, however, fill your [#](8B0000)Spiritus Gem[#]() to the brim, "
                + "and it shall surpass even [#](8B0000)Netherite[#](), cleaving through stone as though it were clay.");

        this.page("scythe_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Sentient Scythe[#]() is a beast of a different nature. The infusion of Will has "
                + "transmuted it from a humble farming tool into a fearsome weapon. Though slower and less "
                + "devastating per blow than the sword, its sweeping arc delivers full damage to every creature "
                + "caught within its reach, ideal for carving through hordes.");

        this.page("scythe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sentient Scythe");
        this.pageText("Starved of Will, the scythe is blunt and unwieldy. Fed generously from a full "
                + "[#](8B0000)Spiritus Gem[#](), it becomes a devastating instrument of reaping.\\\n\\\n"
                + "[#](2E8B57)It still functions as a hoe, for the practical-minded.[#]()");

        this.page("axe_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Much like the pickaxe, the [#](8B0000)Sentient Axe[#]() scales dramatically with the Will you carry. "
                + "It also receives a significant boost to its damage, making it a formidable weapon for those who "
                + "do not mind its heft.");

        this.page("axe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sentient Axe");
        this.pageText("Without Will, merely a step above its [#](8B0000)Iron[#]() ancestor. With a brimming "
                + "[#](8B0000)Spiritus Gem[#](), it rivals, and surpasses, [#](8B0000)Netherite[#]().");

        this.page("shovel_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Sentient Shovel[#]() follows the same pattern as its kin, a noticeable "
                + "improvement over iron even without additional [#](8B0000)Spiritus[#]() to empower it.");

        this.page("shovel", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sentient Shovel");
        this.pageText("Humble in its unpowered state, transcendent when gorged on Will. With a full "
                + "[#](8B0000)Spiritus Gem[#](), earth and gravel part before it like water.");
    }

    @Override
    protected String entryName() {
        return "Sentient Tools";
    }

    @Override
    protected String entryDescription() {
        return "Will-hungry implements that grow stronger as your reserves deepen.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SENTIENT_PICKAXE.get());
    }

    @Override
    protected String entryId() {
        return "sentient_tools";
    }
}
