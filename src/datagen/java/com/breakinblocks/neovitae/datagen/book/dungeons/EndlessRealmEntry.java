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
import net.minecraft.resources.Identifier;

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
                + "As you explore, the realm will warn you of your progress. Listen for ominous messages "
                + "and watch for [#](8B0000)Spatial Distortions[#](); they herald the path to the mines.");

        this.page("progression", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Dungeon Progression");
        this.pageText("The deeper you delve, the more the realm stirs. After exploring several rooms, "
                + "you will receive a warning: [#](4A0080)\"Strange noises and creatures begin to stir in the depths...\"[#]() "
                + "This signals that a [#](8B0000)Spatial Distortion[#]() has appeared somewhere in the dungeon, "
                + "marked by dark tendrils of void energy and an ominous sound.\\\n\\\n"
                + "Press deeper still and a second warning echoes: [#](4A0080)\"A monstrous roar echoes through "
                + "the corridors...\"[#]() The [#](8B0000)Foreman's Key[#]() chamber has manifested.");

        this.page("distortions", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spatial Distortions");
        this.pageText("[#](4A0080)Spatial Distortions[#]() are fractures in the architecture of the realm. "
                + "When a room harboring one is unsealed, dark void tendrils erupt from its center "
                + "and a message appears on your screen. These distortions conceal two things: "
                + "the entrance to [#](8B0000)The Mines[#]() and the sealed chamber of the "
                + "[#](8B0000)Foreman's Key[#]().");

        this.page("foreman", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Foreman");
        this.pageText("The mine entrance is no empty passage. Within lurks [#](8B0000)The Foreman[#](), "
                + "a greatly empowered [#](8B0000)Daemonium Doloris[#]() that serves as guardian of the mines. "
                + "This boss-tier creature is far stronger than its lesser kin, boasting immense health, "
                + "devastating attacks, and a visible boss bar.\\\n\\\n"
                + "Defeat The Foreman and it will drop the [#](8B0000)Foreman's Key[#](), which unseals the "
                + "passage into the mine tunnels. The key chamber requires an [#](8B0000)Iron Key[#]() to open.");

        this.page("mine_entrance_img", () -> BookImagePageModel.create()
                .withTitle("The Mine Entrance")
                .withImages(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/dungeon/mine_entrance.png"))
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("Only the Foreman's Key will open the sealed passage beyond. If you have not yet defeated The Foreman, you must venture back.");

        this.page("rift", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Spatial Rift");
        this.pageText("When every sealed doorway in the dungeon has been opened or collapsed, a "
                + "[#](8B0000)Spatial Rift[#]() tears open near the exit portal. Dark tendrils of blood "
                + "energy radiate outward from the fracture.\\\n\\\n"
                + "Step into the rift to be transported to a [#](4A0080)brand new Endless Realm[#]() dungeon. "
                + "A return portal in the new dungeon leads back to the previous one, and the standard "
                + "[#](8B0000)Inversion Pillar[#]() always takes you home to the overworld. "
                + "This process can repeat infinitely; each rift leads deeper into the realm.");

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
