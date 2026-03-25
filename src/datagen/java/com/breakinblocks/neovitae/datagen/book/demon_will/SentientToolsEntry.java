package com.breakinblocks.neovitae.datagen.book.demon_will;

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
        this.pageText("The **Sentient Sword** has proven to be a resounding success. You find yourself wondering "
                + "how other tools may react to a similar treatment...\\\n\\\n"
                + "Note that these tools, as with the **Sentient Sword**, can be repaired with **Crystallized "
                + "Will** in an Anvil.\\\n\\\n"
                + "Did we mention that Sentient Tools and Weapons are all highly enchantable?");

        this.page("pickaxe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sentient Pickaxe");
        this.pageText("Craft the Sentient Pickaxe in the Hellfire Forge.\\\n\\\n"
                + "This pickaxe improves with Will, cutting through stone with ease. With no Will to power it, "
                + "it is only slightly better than the **Iron Pickaxe** it was crafted from; However, with a full "
                + "enough **Tartaric Gem**, you foresee it surpassing even a **Netherite Pickaxe**.");

        this.page("scythe_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The **Sentient Scythe** is a slightly different tool to its iron counterpart. Infusing it "
                + "with will has transmuted it into a fearsome weapon. While slow and not as powerful as the other "
                + "weapons, its great swings will deal full damage to all enemies in its range, making it an "
                + "excellent choice for crowd control.");

        this.page("scythe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sentient Scythe");
        this.pageText("Craft the Sentient Scythe in the Hellfire Forge.\\\n\\\n"
                + "As with the pickaxe, with no Will to power your scythe, it is comparatively blunt and "
                + "unwieldy; However, with a full enough **Tartaric Gem**, you foresee it becoming a devastating "
                + "tool.\n"
                + "Did we mention that it still functions as a hoe?");

        this.page("axe_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Much like the **Sentient Pickaxe**, the **Sentient Axe** is a noticeable improvement over "
                + "its Iron counterpart. Additionally, it gets a significant buff in its damage output, making it "
                + "a fearsome weapon for those who don't mind its unwieldy nature.");

        this.page("axe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sentient Axe");
        this.pageText("Craft the Sentient Axe in the Hellfire Forge.\\\n\\\n"
                + "As with the pickaxe, with no Will to power your axe, it is only slightly better than the "
                + "**Iron Axe** it was crafted from; However, with a full enough **Tartaric Gem**, you foresee "
                + "it surpassing even a **Netherite Axe**.");

        this.page("shovel_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Much like the **Sentient Pickaxe**, the **Sentient Shovel** is a noticeable improvement "
                + "over its Iron counterpart, even without additional **Demon Will** to power it.");

        this.page("shovel", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sentient Shovel");
        this.pageText("Craft the Sentient Shovel in the Hellfire Forge.\\\n\\\n"
                + "As with the pickaxe, with no Will to power your shovel, it is only slightly better than the "
                + "**Iron Shovel** it was crafted from; However, with a full enough **Tartaric Gem**, you "
                + "foresee it surpassing even a **Netherite Shovel**.");
    }

    @Override
    protected String entryName() {
        return "Sentient Tools";
    }

    @Override
    protected String entryDescription() {
        return "Demon Will-powered tools that scale with your Will reserves.";
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
