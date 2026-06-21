package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;

public class AltarCategory extends CategoryProvider {

    public AltarCategory(ModonomiconProviderBase parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        // Ara Vitae centered, Soul Network & Slates branching, Runes grouped, Reinforced below
        return new String[]{
                "______a____________",
                "__b___c___d___w____",
                "______e___f________",
                "______g____________",
                "__h_i_j_k_l_m______",
                "______u_v__________",
                "__n_o_p_q_r_s_t____"
        };
    }

    @Override
    protected void generateEntries() {
        var bloodAltar = this.add(new AraVitaeEntry(this).generate('a'));
        var anima = this.add(new AnimaEntry(this).generate('b'));
        anima.withParent(this.parent(bloodAltar));
        anima.withCondition(this.condition().entryViewedOnce(bloodAltar));
        anima.hideWhileLocked(false);

        var slates = this.add(new SlatesEntry(this).generate('c'));
        slates.withParent(this.parent(bloodAltar));
        slates.withCondition(this.condition().entryViewedOnce(bloodAltar));
        slates.hideWhileLocked(false);

        var redstone = this.add(new RedstoneAutomationEntry(this).generate('d'));
        redstone.withParent(this.parent(bloodAltar));
        redstone.withCondition(this.condition().entryViewedOnce(bloodAltar));
        redstone.hideWhileLocked(false);

        var vitaeLink = this.add(new VitaeLinkEntry(this).generate('w'));
        vitaeLink.withParent(this.parent(redstone));
        vitaeLink.withCondition(this.condition().entryViewedOnce(redstone));
        vitaeLink.hideWhileLocked(false);

        var selfSacrifice = this.add(new SelfSacrificeRuneEntry(this).generate('e'));
        selfSacrifice.withParent(this.parent(bloodAltar));
        selfSacrifice.withCondition(this.condition().entryViewedOnce(bloodAltar));
        selfSacrifice.hideWhileLocked(false);

        var sacrifice = this.add(new SacrificeRuneEntry(this).generate('f'));
        sacrifice.withParent(this.parent(bloodAltar));
        sacrifice.withCondition(this.condition().entryViewedOnce(bloodAltar));
        sacrifice.hideWhileLocked(false);

        var orbRune = this.add(new OrbRuneEntry(this).generate('g'));
        orbRune.withParent(this.parent(anima));
        orbRune.withCondition(this.condition().entryViewedOnce(anima));
        orbRune.hideWhileLocked(false);

        var speed = this.add(new SpeedRuneEntry(this).generate('h'));
        speed.withParent(this.parent(bloodAltar));
        speed.withCondition(this.condition().entryViewedOnce(bloodAltar));
        speed.hideWhileLocked(false);

        var acceleration = this.add(new AccelerationRuneEntry(this).generate('i'));
        acceleration.withParent(this.parent(bloodAltar));
        acceleration.withCondition(this.condition().entryViewedOnce(bloodAltar));
        acceleration.hideWhileLocked(false);

        var charging = this.add(new ChargingRuneEntry(this).generate('j'));
        charging.withParent(this.parent(bloodAltar));
        charging.withCondition(this.condition().entryViewedOnce(bloodAltar));
        charging.hideWhileLocked(false);

        var dislocation = this.add(new DislocationRuneEntry(this).generate('k'));
        dislocation.withParent(this.parent(bloodAltar));
        dislocation.withCondition(this.condition().entryViewedOnce(bloodAltar));
        dislocation.hideWhileLocked(false);

        var capacity = this.add(new CapacityRuneEntry(this).generate('l'));
        capacity.withParent(this.parent(bloodAltar));
        capacity.withCondition(this.condition().entryViewedOnce(bloodAltar));
        capacity.hideWhileLocked(false);

        var augCapacity = this.add(new AugCapacityRuneEntry(this).generate('m'));
        augCapacity.withParent(this.parent(capacity));
        augCapacity.withCondition(this.condition().entryViewedOnce(capacity));
        augCapacity.hideWhileLocked(false);

        var reinforcedOrb = this.add(new ReinforcedOrbRuneEntry(this).generate('n'));
        reinforcedOrb.withParent(this.parent(orbRune));
        reinforcedOrb.withCondition(this.condition().entryViewedOnce(orbRune));
        reinforcedOrb.hideWhileLocked(false);

        var reinforcedSpeed = this.add(new ReinforcedSpeedRuneEntry(this).generate('o'));
        reinforcedSpeed.withParent(this.parent(speed));
        reinforcedSpeed.withCondition(this.condition().entryViewedOnce(speed));
        reinforcedSpeed.hideWhileLocked(false);

        var reinforcedAcceleration = this.add(new ReinforcedAccelerationRuneEntry(this).generate('p'));
        reinforcedAcceleration.withParent(this.parent(acceleration));
        reinforcedAcceleration.withCondition(this.condition().entryViewedOnce(acceleration));
        reinforcedAcceleration.hideWhileLocked(false);

        var reinforcedCharging = this.add(new ReinforcedChargingRuneEntry(this).generate('q'));
        reinforcedCharging.withParent(this.parent(charging));
        reinforcedCharging.withCondition(this.condition().entryViewedOnce(charging));
        reinforcedCharging.hideWhileLocked(false);

        var reinforcedDislocation = this.add(new ReinforcedDislocationRuneEntry(this).generate('r'));
        reinforcedDislocation.withParent(this.parent(dislocation));
        reinforcedDislocation.withCondition(this.condition().entryViewedOnce(dislocation));
        reinforcedDislocation.hideWhileLocked(false);

        var reinforcedCapacity = this.add(new ReinforcedCapacityRuneEntry(this).generate('s'));
        reinforcedCapacity.withParent(this.parent(capacity));
        reinforcedCapacity.withCondition(this.condition().entryViewedOnce(capacity));
        reinforcedCapacity.hideWhileLocked(false);

        var reinforcedAugCapacity = this.add(new ReinforcedAugCapacityRuneEntry(this).generate('t'));
        reinforcedAugCapacity.withParent(this.parent(augCapacity));
        reinforcedAugCapacity.withCondition(this.condition().entryViewedOnce(augCapacity));
        reinforcedAugCapacity.hideWhileLocked(false);

        var reinforcedSelfSacrifice = this.add(new ReinforcedSelfSacrificeRuneEntry(this).generate('u'));
        reinforcedSelfSacrifice.withParent(this.parent(selfSacrifice));
        reinforcedSelfSacrifice.withCondition(this.condition().entryViewedOnce(selfSacrifice));
        reinforcedSelfSacrifice.hideWhileLocked(false);

        var reinforcedSacrifice = this.add(new ReinforcedSacrificeRuneEntry(this).generate('v'));
        reinforcedSacrifice.withParent(this.parent(sacrifice));
        reinforcedSacrifice.withCondition(this.condition().entryViewedOnce(sacrifice));
        reinforcedSacrifice.hideWhileLocked(false);
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return super.additionalSetup(category)
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/altar_base.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/altar_layer_1.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/altar_layer_2.png"));
    }

    @Override
    protected String categoryName() {
        return "Ara Vitaes";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(NVBlocks.ARA_VITAE.asItem());
    }

    @Override
    public String categoryId() {
        return "altar";
    }
}
