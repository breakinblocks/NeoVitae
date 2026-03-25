package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class EndlessRealmEntry extends EntryProvider {

    public EndlessRealmEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Endless Realm");
        this.pageText("Much as with its little brother, the Edge of the Hidden Realm ritual, you can access the "
                + "**Demon Realm** proper with the Pathway to the Endless Realm. You will once again find yourself "
                + "looking at an **Inversion Pillar**. As with the **Antechamber**, you'll find a simple loot chest "
                + "left by a careless demon (or perhaps a less-lucky sanguimancer) and a number of doorways, alongside");

        this.page("intro2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("another Inversion Pillar to return you home.\\\n\\\n"
                + "These dungeon rooms will be filled with more fearsome foes than you found in the antechamber, "
                + "and will have loot to match. Be sure to keep your eyes peeled for any **Spatial Distortions**, "
                + "as well as rare keys such as the **Foreman's Key**.");

        this.page("distortions", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Spatial Distortions will announce themselves with a chat message - if you see one of these "
                + "messages, it means the room you're in has a very special lock. Currently, it will be either the "
                + "entrance to **The Mines**, or the entrance to a small room containing **The Foreman's Key**, "
                + "which can be opened with an **Iron Key**. The Mine entrance will always spawn before the key, "
                + "so some backtracking might be needed.");

        this.page("foreman_key_img", () -> BookImagePageModel.create()
                .withTitle("The Foreman's Key")
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/dungeon/mine_key.png"))
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("This door can be opened with an Iron Key, and hides the Foreman's Key behind it.");

        this.page("mine_entrance_img", () -> BookImagePageModel.create()
                .withTitle("The Mine Entrance")
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/dungeon/mine_entrance.png"))
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("This door can only be opened with the Foreman's Key. You may need to backtrack!");

        this.page("mines", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Mines");
        this.pageText("The Foreman's Key will allow entrance into **The Mines**, which is another step up in "
                + "both difficulty and rewards. Along with **Demonite Ore**, which is essential for high tier "
                + "potioncrafting and the **Tier-5 Blood Altar** and its accompanying Orb, you'll find scads of "
                + "more mundane loot, enchanted weapons and tools, anointments, potions, and more.");

        this.page("hellforged_parts", () -> BookSpotlightPageModel.create()
                .withItem(NVItems.HELLFORGED_PARTS.get())
                .withTitle("Intricate Hellforged Parts")
                .withText(this.context().pageText()));
        this.pageText("Explore enough, and you may be lucky enough to find **Intricate Hellforged Parts**, "
                + "which can be used to double the power of your existing **Runes**. It's like having two "
                + "Blood Altars in one!");
    }

    @Override
    protected String entryName() {
        return "The Endless Realm";
    }

    @Override
    protected String entryDescription() {
        return "The Demon Realm proper and its deeper dungeons.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.INVERSION_PILLAR.asItem());
    }

    @Override
    protected String entryId() {
        return "endless_realm";
    }
}
