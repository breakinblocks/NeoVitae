package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookEntityPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class DemonBestiaryEntry extends EntryProvider {

    public DemonBestiaryEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Bestiary");
        this.pageText("A field guide to the creatures of the [#](4A0080)Demon Realm[#]() and the "
                + "vitaemantic horrors that haunt its corridors. Study well; knowledge of your enemy is "
                + "the difference between plunder and a shallow grave.");

        // --- Slime of Vitae ---
        addMob("slime_vitae", "neovitae:slime_vitae", "Slime of Vitae", 0.4f,
                "A [#](8B0000)blood-red slime[#]() born of coagulated Essentia Vitae. Its touch "
                        + "chills the body, slowing movement, and upon death it detonates in a burst of "
                        + "[#](8B0000)raw magic damage[#](). Immune to magical attacks. Frequently "
                        + "encountered in dungeon trial spawners, appearing in groups of three.\\\n\\\n"
                        + "[#](2E8B57)Beware:[#]() if four small Slimes of Vitae gather nearby, they merge "
                        + "into a larger, deadlier variant.");

        // --- Daemonium Cruoris ---
        addMob("cruoris", "neovitae:daemonium_cruoris", "Daemonium Cruoris", 0.5f,
                "The [#](8B0000)Blood Demon[#](). A shambling melee creature that attacks with "
                        + "necrotic claw swipes and grave leaps. Relatively fragile at 30 HP but can close "
                        + "distance quickly.\\\n\\\n"
                        + "[#](2E8B57)Drops[#](): Gore-Clotted Fang, Tainted Flesh, Default/Spiritus Nihilum");

        // --- Daemonium Pestis ---
        addMob("pestis", "neovitae:daemonium_pestis", "Daemonium Pestis", 0.5f,
                "The [#](2E8B57)Pestilence Spider[#](). A venomous creature with fang bites and "
                        + "shadow lunges. Low health (30 HP) but its poison is deadly in groups.\\\n\\\n"
                        + "[#](2E8B57)Drops[#](): Venomgland Sac, Tainted Flesh, Ruina/Raw Spiritus, Spider Eye");

        // --- Daemonium Rancoris ---
        addMob("rancoris", "neovitae:daemonium_rancoris", "Daemonium Rancoris", 0.4f,
                "The [#](4A0080)Spite Phantom[#](). A spectral ranged attacker that fires spectral "
                        + "bolts and ectoplasmic bursts. Fragile (45 HP) but dangerous at range.\\\n\\\n"
                        + "[#](2E8B57)Drops[#](): Ectoplasmic Residue, Tainted Flesh, Vindicta/Spiritus Invictus");

        // --- Daemonium Animaris ---
        addMob("animaris", "neovitae:daemonium_animaris", "Daemonium Animaris", 0.6f,
                "The [#](4A0080)Soul Wraith[#](). A flying spectral entity that charges through "
                        + "targets. Elusive and difficult to pin down at 30 HP.\\\n\\\n"
                        + "[#](2E8B57)Drops[#](): Animus Mote, Invictus/Spiritus Vindicta, Phantom Membrane (rare)");

        // --- Daemonium Voraxis ---
        addMob("voraxis", "neovitae:daemonium_voraxis", "Daemonium Voraxis", 0.4f,
                "The [#](8B0000)Voracious Oni[#](). A mid-tier threat with life-draining slash "
                        + "attacks and hunger effects. Sturdy at 60 HP with decent armor.\\\n\\\n"
                        + "[#](2E8B57)Drops[#](): Hollow Gut, Tainted Flesh, Nihilum/Spiritus Ruina, "
                        + "Raw Demonite (rare)");

        // --- Daemonium Corrodis ---
        addMob("corrodis", "neovitae:daemonium_corrodis", "Daemonium Corrodis", 0.35f,
                "[#](8B0000)The Wither Knight[#](). An elite melee combatant clad in heavy armor "
                        + "with three attack phases. Every blow carries a withering curse that rots flesh on contact, "
                        + "and its strikes often leave victims drained and enfeebled.\\\n\\\n"
                        + "[#](2E8B57)Drops[#](): Blight Marrow, Raw Demonite, Ruina/Spiritus Vindicta, "
                        + "Wither Skeleton Skull (rare)");

        // --- Daemonium Ignis ---
        addMob("ignis", "neovitae:daemonium_ignis", "Daemonium Ignis", 0.3f,
                "The [#](8B0000)Fire Demon[#](). An apex predator wreathed in flame, immune to fire, "
                        + "that alternates between fireball barrages, ground slams, and rapid sword "
                        + "combos that leave victims barely able to move.\\\n\\\n"
                        + "[#](2E8B57)Drops[#](): Cinder Heart Fragment, Raw Demonite, Nihilum/Ruina "
                        + "Spiritus, Blaze Rod (rare)");

        // --- Daemonium Fervidis ---
        addMob("fervidis", "neovitae:daemonium_fervidis", "Daemonium Fervidis", 0.3f,
                "The [#](B8860B)Undying Brute[#](). A boss-adjacent horror (150 HP) with decay "
                        + "swings, revenant smash leaps, and a defensive bear stance that grants "
                        + "near-invulnerability.\\\n\\\n"
                        + "[#](2E8B57)Drops[#](): Revenant Plate, Gore-Clotted Fang (rare), Raw Demonite, "
                        + "Nihilum/Spiritus Invictus");

        // --- Daemonium Doloris ---
        addMob("doloris", "neovitae:daemonium_doloris", "Daemonium Doloris", 0.3f,
                "The [#](4A0080)Wendigo of Pain[#](). An elite hunter (200 HP) with three-hit "
                        + "combos, leap slams, and a ghost howl that phases it out of reality. Enters a dangerous "
                        + "second phase at low health.\\\n\\\n"
                        + "A vastly empowered variant known as [#](8B0000)The Foreman[#]() (600 HP, 25 damage, "
                        + "16 armor) guards the mine entrance. Defeating it drops a clutch of [#](8B0000)Mine Dungeon Keys[#]().\\\n\\\n"
                        + "[#](2E8B57)Drops[#](): Frozen Marrow Shard, Ectoplasmic Residue (rare), Raw Demonite, "
                        + "Vindicta/Spiritus Nihilum");

        // --- Daemonium Glaciaris ---
        addMob("glaciaris", "neovitae:daemonium_glaciaris", "Daemonium Glaciaris", 0.25f,
                "The [#](4A0080)Ice Colossus[#](). The most dangerous creature in the Demon Realm "
                        + "(250 HP) with ice projectiles, beam attacks, ice wall summons, shard bursts, and a "
                        + "mist phase that renders it briefly invulnerable.\\\n\\\n"
                        + "[#](2E8B57)Drops[#](): Permafrost Core, Frozen Marrow Shard (rare), Raw Demonite, "
                        + "all Spiritus types");
    }

    private void addMob(String id, String entityId, String name, float scale, String description) {
        this.page(id + "_entity", () -> BookEntityPageModel.create()
                .withEntityId(entityId)
                .withEntityName(name)
                .withScale(scale)
                .withText(this.context().pageText()));
        this.pageText(description);
    }

    @Override
    protected String entryName() {
        return "Bestiary";
    }

    @Override
    protected String entryDescription() {
        return "A field guide to every known creature of the Demon Realm and beyond.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.GORE_CLOTTED_FANG.get());
    }

    @Override
    protected String entryId() {
        return "demon_bestiary";
    }
}
