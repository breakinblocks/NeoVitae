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
        this.pageText("Where the Antechamber merely grazes the surface of the [#](4A0080)Demon Realm[#](), the "
                + "[#](8B0000)Pathway to the Endless Realm[#]() ritual tears the veil wide open. You emerge before "
                + "another [#](8B0000)Inversion Pillar[#](), but the air here is thicker, the shadows deeper. A lone "
                + "chest awaits, left by some prior explorer who did not leave by choice, and doorways branch "
                + "outward into the labyrinth beyond.");

        this.page("intro2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("A second [#](8B0000)Inversion Pillar[#]() stands ready to return you home. Use it wisely; "
                + "the creatures that dwell here are far more formidable than those of the Antechamber, "
                + "and the rewards they guard are proportionally greater.\\\n\\\n"
                + "[#](2E8B57)Watch for messages warning of [#](8B0000)Spatial Distortions[#]() as you explore, "
                + "and keep any [#](8B0000)Foreman's Keys[#]() you discover close at hand.[#]()");

        this.page("distortions", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](4A0080)Spatial Distortions[#]() are fractures in the architecture of the realm itself. "
                + "A whispered message will alert you when you have entered a room harboring one. "
                + "These anomalies conceal either the entrance to [#](8B0000)The Mines[#]() or a sealed chamber "
                + "containing the [#](8B0000)Foreman's Key[#](), the latter opened with an [#](8B0000)Iron Key[#](). "
                + "The mine entrance always manifests before the key chamber, so be prepared to retrace your steps.");

        this.page("foreman_key_img", () -> BookImagePageModel.create()
                .withTitle("The Foreman's Key")
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/dungeon/mine_key.png"))
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("An Iron Key will unseal this chamber. Within lies the Foreman's Key, coveted prize of the deep.");

        this.page("mine_entrance_img", () -> BookImagePageModel.create()
                .withTitle("The Mine Entrance")
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/dungeon/mine_entrance.png"))
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("Only the Foreman's Key will open this passage. If you have not yet found it, you must venture back.");

        this.page("mines", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Mines");
        this.pageText("Beyond the sealed passage lie [#](8B0000)The Mines[#](), a [#](B8860B)significant escalation[#]() in both "
                + "peril and plunder. Veins of [#](8B0000)Demonite Ore[#]() thread through the walls here, essential for "
                + "advanced potion-craft and the construction of the [#](B8860B)Tier-5 Ara Vitae[#]() and its "
                + "accompanying Orb. Alongside the ore, you will find enchanted armaments, anointments, "
                + "potions, and other spoils worthy of the risk.");

        this.page("hellforged_parts", () -> BookSpotlightPageModel.create()
                .withItem(NVItems.HELLFORGED_PARTS.get())
                .withTitle("Intricate Hellforged Parts")
                .withText(this.context().pageText()));
        this.pageText("Delve deep enough and fortune may smile upon you with [#](8B0000)Intricate Hellforged Parts[#](). "
                + "These rare components can be used to [#](4A0080)double the potency[#]() of your existing "
                + "[#](8B0000)Runes[#](), effectively granting the power of two Ara Vitaes in a single structure.");
    }

    @Override
    protected String entryName() {
        return "The Endless Realm";
    }

    @Override
    protected String entryDescription() {
        return "The heart of the Demon Realm, where darkness breeds treasure and peril in equal measure.";
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
