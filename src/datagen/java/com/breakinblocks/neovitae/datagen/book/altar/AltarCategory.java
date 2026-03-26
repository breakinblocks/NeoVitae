package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryReadConditionModel;
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
                "__b___c___d________",
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
        var soulNetwork = this.add(new SoulNetworkEntry(this).generate('b'));
        soulNetwork.withParent(this.parent(bloodAltar));
        soulNetwork.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/ara_vitae"));
        soulNetwork.hideWhileLocked(false);

        var slates = this.add(new SlatesEntry(this).generate('c'));
        slates.withParent(this.parent(bloodAltar));
        slates.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/ara_vitae"));
        slates.hideWhileLocked(false);

        var redstone = this.add(new RedstoneAutomationEntry(this).generate('d'));
        redstone.withParent(this.parent(bloodAltar));
        redstone.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/ara_vitae"));
        redstone.hideWhileLocked(false);

        var selfSacrifice = this.add(new SelfSacrificeRuneEntry(this).generate('e'));
        selfSacrifice.withParent(this.parent(bloodAltar));
        selfSacrifice.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/ara_vitae"));
        selfSacrifice.hideWhileLocked(false);

        var sacrifice = this.add(new SacrificeRuneEntry(this).generate('f'));
        sacrifice.withParent(this.parent(bloodAltar));
        sacrifice.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/ara_vitae"));
        sacrifice.hideWhileLocked(false);

        var orbRune = this.add(new OrbRuneEntry(this).generate('g'));
        orbRune.withParent(this.parent(soulNetwork));
        orbRune.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/soul_network"));
        orbRune.hideWhileLocked(false);

        var speed = this.add(new SpeedRuneEntry(this).generate('h'));
        speed.withParent(this.parent(bloodAltar));
        speed.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/ara_vitae"));
        speed.hideWhileLocked(false);

        var acceleration = this.add(new AccelerationRuneEntry(this).generate('i'));
        acceleration.withParent(this.parent(bloodAltar));
        acceleration.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/ara_vitae"));
        acceleration.hideWhileLocked(false);

        var charging = this.add(new ChargingRuneEntry(this).generate('j'));
        charging.withParent(this.parent(bloodAltar));
        charging.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/ara_vitae"));
        charging.hideWhileLocked(false);

        var dislocation = this.add(new DislocationRuneEntry(this).generate('k'));
        dislocation.withParent(this.parent(bloodAltar));
        dislocation.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/ara_vitae"));
        dislocation.hideWhileLocked(false);

        var capacity = this.add(new CapacityRuneEntry(this).generate('l'));
        capacity.withParent(this.parent(bloodAltar));
        capacity.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/ara_vitae"));
        capacity.hideWhileLocked(false);

        var augCapacity = this.add(new AugCapacityRuneEntry(this).generate('m'));
        augCapacity.withParent(this.parent(capacity));
        augCapacity.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/rune_capacity"));
        augCapacity.hideWhileLocked(false);

        var reinforcedOrb = this.add(new ReinforcedOrbRuneEntry(this).generate('n'));
        reinforcedOrb.withParent(this.parent(orbRune));
        reinforcedOrb.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/rune_orb"));
        reinforcedOrb.hideWhileLocked(false);

        var reinforcedSpeed = this.add(new ReinforcedSpeedRuneEntry(this).generate('o'));
        reinforcedSpeed.withParent(this.parent(speed));
        reinforcedSpeed.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/rune_speed"));
        reinforcedSpeed.hideWhileLocked(false);

        var reinforcedAcceleration = this.add(new ReinforcedAccelerationRuneEntry(this).generate('p'));
        reinforcedAcceleration.withParent(this.parent(acceleration));
        reinforcedAcceleration.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/rune_acceleration"));
        reinforcedAcceleration.hideWhileLocked(false);

        var reinforcedCharging = this.add(new ReinforcedChargingRuneEntry(this).generate('q'));
        reinforcedCharging.withParent(this.parent(charging));
        reinforcedCharging.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/rune_charging"));
        reinforcedCharging.hideWhileLocked(false);

        var reinforcedDislocation = this.add(new ReinforcedDislocationRuneEntry(this).generate('r'));
        reinforcedDislocation.withParent(this.parent(dislocation));
        reinforcedDislocation.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/rune_dislocation"));
        reinforcedDislocation.hideWhileLocked(false);

        var reinforcedCapacity = this.add(new ReinforcedCapacityRuneEntry(this).generate('s'));
        reinforcedCapacity.withParent(this.parent(capacity));
        reinforcedCapacity.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/rune_capacity"));
        reinforcedCapacity.hideWhileLocked(false);

        var reinforcedAugCapacity = this.add(new ReinforcedAugCapacityRuneEntry(this).generate('t'));
        reinforcedAugCapacity.withParent(this.parent(augCapacity));
        reinforcedAugCapacity.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/rune_aug_capacity"));
        reinforcedAugCapacity.hideWhileLocked(false);

        var reinforcedSelfSacrifice = this.add(new ReinforcedSelfSacrificeRuneEntry(this).generate('u'));
        reinforcedSelfSacrifice.withParent(this.parent(selfSacrifice));
        reinforcedSelfSacrifice.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/rune_self_sacrifice"));
        reinforcedSelfSacrifice.hideWhileLocked(false);

        var reinforcedSacrifice = this.add(new ReinforcedSacrificeRuneEntry(this).generate('v'));
        reinforcedSacrifice.withParent(this.parent(sacrifice));
        reinforcedSacrifice.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:altar/rune_sacrifice"));
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
