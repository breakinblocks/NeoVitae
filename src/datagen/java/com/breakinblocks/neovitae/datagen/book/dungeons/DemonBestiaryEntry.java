package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class DemonBestiaryEntry extends EntryProvider {

    public DemonBestiaryEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("cruoris", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Daemonium Cruoris");
        this.pageText("The [#](8B0000)Blood Demon[#](). A shambling melee creature that attacks with "
                + "necrotic claw swipes and grave leaps. Relatively fragile at 30 HP but can close "
                + "distance quickly.\\\n\\\n"
                + "[#](2E8B57)Drops[#](): Gore-Clotted Fang, Tainted Flesh, Default/Destructive Spiritus");

        this.page("pestis", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Daemonium Pestis");
        this.pageText("The [#](2E8B57)Pestilence Spider[#](). A venomous creature with fang bites and "
                + "shadow lunges. Low health (30 HP) but its poison is deadly in groups.\\\n\\\n"
                + "[#](2E8B57)Drops[#](): Venomgland Sac, Tainted Flesh, Corrosive/Default Spiritus, Spider Eye");

        this.page("rancoris", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Daemonium Rancoris");
        this.pageText("The [#](4A0080)Spite Phantom[#](). A spectral ranged attacker that fires spectral "
                + "bolts and ectoplasmic bursts. Fragile (45 HP) but dangerous at range.\\\n\\\n"
                + "[#](2E8B57)Drops[#](): Ectoplasmic Residue, Tainted Flesh, Vengeful/Steadfast Spiritus");

        this.page("animaris", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Daemonium Animaris");
        this.pageText("The [#](4A0080)Soul Wraith[#](). A flying spectral entity that charges through "
                + "targets. Elusive and difficult to pin down at 30 HP.\\\n\\\n"
                + "[#](2E8B57)Drops[#](): Animus Mote, Steadfast/Vengeful Spiritus, Phantom Membrane (rare)");

        this.page("voraxis", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Daemonium Voraxis");
        this.pageText("The [#](8B0000)Voracious Oni[#](). A mid-tier threat with life-draining slash "
                + "attacks and hunger effects. Sturdy at 60 HP with decent armour.\\\n\\\n"
                + "[#](2E8B57)Drops[#](): Hollow Gut, Tainted Flesh, Destructive/Corrosive Spiritus, "
                + "Raw Demonite (rare)");

        this.page("corrodis", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Daemonium Corrodis");
        this.pageText("[#](8B0000)The Wither Knight[#](). An elite melee combatant (100 HP, 15 armour) "
                + "with three attack phases. All hits inflict Wither II, with a 60%% chance of Weakness.\\\n\\\n"
                + "[#](2E8B57)Drops[#](): Blight Marrow, Raw Demonite, Corrosive/Vengeful Spiritus, "
                + "Wither Skeleton Skull (rare)");

        this.page("fervidis", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Daemonium Fervidis");
        this.pageText("The [#](B8860B)Undying Brute[#](). A boss-adjacent horror (150 HP) with decay "
                + "swings, revenant smash leaps, and a defensive bear stance that grants "
                + "near-invulnerability.\\\n\\\n"
                + "[#](2E8B57)Drops[#](): Revenant Plate, Gore-Clotted Fang (rare), Raw Demonite, "
                + "Destructive/Steadfast Spiritus");

        this.page("doloris", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Daemonium Doloris");
        this.pageText("The [#](4A0080)Wendigo of Pain[#](). An elite hunter (200 HP) with three-hit "
                + "combos, leap slams, and a ghost howl that phases it out of reality. Enters a dangerous "
                + "second phase at low health.\\\n\\\n"
                + "[#](2E8B57)Drops[#](): Frozen Marrow Shard, Ectoplasmic Residue (rare), Raw Demonite, "
                + "Vengeful/Destructive Spiritus");

        this.page("ignis", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Daemonium Ignis");
        this.pageText("The [#](8B0000)Fire Demon[#](). An apex predator (60 HP but high armour and fire "
                + "immunity) that alternates between fireball barrages, ground slams, and rapid sword "
                + "combos with Slowness IV.\\\n\\\n"
                + "[#](2E8B57)Drops[#](): Cinder Heart Fragment, Raw Demonite, Destructive/Corrosive "
                + "Spiritus, Blaze Rod (rare)");

        this.page("glaciaris", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Daemonium Glaciaris");
        this.pageText("The [#](4A0080)Ice Colossus[#](). The most dangerous creature in the Demon Realm "
                + "(250 HP) with ice projectiles, beam attacks, ice wall summons, shard bursts, and a "
                + "mist phase that renders it briefly invulnerable.\\\n\\\n"
                + "[#](2E8B57)Drops[#](): Permafrost Core, Frozen Marrow Shard (rare), Raw Demonite, "
                + "all Spiritus types");
    }

    @Override
    protected String entryName() {
        return "Bestiary";
    }

    @Override
    protected String entryDescription() {
        return "A field guide to every known species of Daemonium.";
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
