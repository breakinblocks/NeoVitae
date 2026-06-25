package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryReadConditionModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;

public class AlchemyArraysCategory extends CategoryProvider {

    public AlchemyArraysCategory(ModonomiconProviderBase parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                "__a_b_c_d_e________",
                "____________________",
                "__f_g_h_i_j_k_l_m__",
                "____n_o_p_q_r_s_W__",
                "____________________",
                "__t_u_v_w_1_2_3_4_5_",
                "____________________",
                "x_y_z_A_B_C_D_E_F__",
                "_G_H_I_J_K_L_M_N_O_",
                "P_Q_R_S_T_U_V_X_Y_Z",
                "6_7_8_9_0_______________"
        };
    }

    @Override
    protected void generateEntries() {
        var arcaneAsh = this.add(new ArcaneAshEntry(this).generate('a'));

        var craftingArray = this.add(new CraftingArrayEntry(this).generate('b'));
        craftingArray.withParent(this.parent(arcaneAsh));
        craftingArray.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/arcane_ash"));
        craftingArray.hideWhileLocked(false);

        var movementArrays = this.add(new MovementArraysEntry(this).generate('c'));
        movementArrays.withParent(this.parent(arcaneAsh));
        movementArrays.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/arcane_ash"));
        movementArrays.hideWhileLocked(false);

        var spikeArray = this.add(new SpikeArrayEntry(this).generate('d'));
        spikeArray.withParent(this.parent(arcaneAsh));
        spikeArray.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/arcane_ash"));
        spikeArray.hideWhileLocked(false);

        var telepositionArray = this.add(new TelepositionArrayEntry(this).generate('E'));
        telepositionArray.withParent(this.parent(arcaneAsh));
        telepositionArray.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/arcane_ash"));
        telepositionArray.hideWhileLocked(false);

        var timeArrays = this.add(new TimeArraysEntry(this).generate('e'));
        timeArrays.withParent(this.parent(arcaneAsh));
        timeArrays.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/arcane_ash"));
        timeArrays.hideWhileLocked(false);

        var divination = this.add(new DivinationSigilEntry(this).generate('f'));

        var seer = this.add(new SeerSigilEntry(this).generate('g'));
        seer.withParent(this.parent(divination));
        seer.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        seer.hideWhileLocked(false);

        var air = this.add(new AirSigilEntry(this).generate('h'));
        air.withParent(this.parent(divination));
        air.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        air.hideWhileLocked(false);

        var water = this.add(new WaterSigilEntry(this).generate('i'));
        water.withParent(this.parent(divination));
        water.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        water.hideWhileLocked(false);

        var lava = this.add(new LavaSigilEntry(this).generate('j'));
        lava.withParent(this.parent(divination));
        lava.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        lava.hideWhileLocked(false);

        var voidSigil = this.add(new VoidSigilEntry(this).generate('k'));
        voidSigil.withParent(this.parent(divination));
        voidSigil.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        voidSigil.hideWhileLocked(false);

        var greenGrove = this.add(new GreenGroveSigilEntry(this).generate('l'));
        greenGrove.withParent(this.parent(divination));
        greenGrove.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        greenGrove.hideWhileLocked(false);

        var bloodLamp = this.add(new BloodLampSigilEntry(this).generate('m'));
        bloodLamp.withParent(this.parent(divination));
        bloodLamp.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        bloodLamp.hideWhileLocked(false);

        var mining = this.add(new MiningSigilEntry(this).generate('n'));
        mining.withParent(this.parent(divination));
        mining.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        mining.hideWhileLocked(false);

        var magnetism = this.add(new MagnetismSigilEntry(this).generate('o'));
        magnetism.withParent(this.parent(divination));
        magnetism.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        magnetism.hideWhileLocked(false);

        var holding = this.add(new HoldingSigilEntry(this).generate('p'));
        holding.withParent(this.parent(divination));
        holding.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        holding.hideWhileLocked(false);

        var suppression = this.add(new SuppressionSigilEntry(this).generate('q'));
        suppression.withParent(this.parent(divination));
        suppression.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        suppression.hideWhileLocked(false);

        var phantomBridge = this.add(new PhantomBridgeSigilEntry(this).generate('r'));
        phantomBridge.withParent(this.parent(divination));
        phantomBridge.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        phantomBridge.hideWhileLocked(false);

        var teleposition = this.add(new TelepositionSigilEntry(this).generate('s'));
        teleposition.withParent(this.parent(divination));
        teleposition.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        teleposition.hideWhileLocked(false);

        var ice = this.add(new IceSigilEntry(this).generate('W'));
        ice.withParent(this.parent(divination));
        ice.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        ice.hideWhileLocked(false);

        var sentientEquipment = this.add(new SentientEquipmentEntry(this).generate('t'));

        var sentientUpgrades = this.add(new SentientUpgradesEntry(this).generate('u'));
        sentientUpgrades.withParent(this.parent(sentientEquipment));
        sentientUpgrades.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_equipment"));
        sentientUpgrades.hideWhileLocked(false);

        var upgradeTomes = this.add(new UpgradeTomesEntry(this).generate('v'));
        upgradeTomes.withParent(this.parent(sentientEquipment));
        upgradeTomes.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_equipment"));
        upgradeTomes.hideWhileLocked(false);

        var trainingBracelet = this.add(new TrainingBraceletEntry(this).generate('w'));
        trainingBracelet.withParent(this.parent(sentientEquipment));
        trainingBracelet.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_equipment"));
        trainingBracelet.hideWhileLocked(false);

        var bodyBuilder = this.add(new BodyBuilderUpgradeEntry(this).generate('x'));
        bodyBuilder.withParent(this.parent(sentientUpgrades));
        bodyBuilder.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        bodyBuilder.hideWhileLocked(false);

        var brilliance = this.add(new BrillianceUpgradeEntry(this).generate('y'));
        brilliance.withParent(this.parent(sentientUpgrades));
        brilliance.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        brilliance.hideWhileLocked(false);

        var chargingStrike = this.add(new ChargingStrikeUpgradeEntry(this).generate('z'));
        chargingStrike.withParent(this.parent(sentientUpgrades));
        chargingStrike.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        chargingStrike.hideWhileLocked(false);

        var curiosSockets = this.add(new CuriosSocketsUpgradeEntry(this).generate('A'));
        curiosSockets.withParent(this.parent(sentientUpgrades));
        curiosSockets.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        curiosSockets.hideWhileLocked(false);

        var dwarvenMight = this.add(new DwarvenMightUpgradeEntry(this).generate('B'));
        dwarvenMight.withParent(this.parent(sentientUpgrades));
        dwarvenMight.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        dwarvenMight.hideWhileLocked(false);

        var elytra = this.add(new ElytraUpgradeEntry(this).generate('C'));
        elytra.withParent(this.parent(sentientUpgrades));
        elytra.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        elytra.hideWhileLocked(false);

        var experienced = this.add(new ExperiencedUpgradeEntry(this).generate('D'));
        experienced.withParent(this.parent(sentientUpgrades));
        experienced.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        experienced.hideWhileLocked(false);

        var fierceStrike = this.add(new FierceStrikeUpgradeEntry(this).generate('E'));
        fierceStrike.withParent(this.parent(sentientUpgrades));
        fierceStrike.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        fierceStrike.hideWhileLocked(false);

        var giftOfIgnis = this.add(new GiftOfIgnisUpgradeEntry(this).generate('F'));
        giftOfIgnis.withParent(this.parent(sentientUpgrades));
        giftOfIgnis.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        giftOfIgnis.hideWhileLocked(false);

        var gilded = this.add(new GildedUpgradeEntry(this).generate('G'));
        gilded.withParent(this.parent(sentientUpgrades));
        gilded.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        gilded.hideWhileLocked(false);

        var healthy = this.add(new HealthyUpgradeEntry(this).generate('H'));
        healthy.withParent(this.parent(sentientUpgrades));
        healthy.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        healthy.hideWhileLocked(false);

        var pinCushion = this.add(new PinCushionUpgradeEntry(this).generate('I'));
        pinCushion.withParent(this.parent(sentientUpgrades));
        pinCushion.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        pinCushion.hideWhileLocked(false);

        var poisonResistance = this.add(new PoisonResistanceUpgradeEntry(this).generate('J'));
        poisonResistance.withParent(this.parent(sentientUpgrades));
        poisonResistance.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        poisonResistance.hideWhileLocked(false);

        var quickFeet = this.add(new QuickFeetUpgradeEntry(this).generate('K'));
        quickFeet.withParent(this.parent(sentientUpgrades));
        quickFeet.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        quickFeet.hideWhileLocked(false);

        var repair = this.add(new RepairUpgradeEntry(this).generate('L'));
        repair.withParent(this.parent(sentientUpgrades));
        repair.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        repair.hideWhileLocked(false);

        var softFall = this.add(new SoftFallUpgradeEntry(this).generate('M'));
        softFall.withParent(this.parent(sentientUpgrades));
        softFall.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        softFall.hideWhileLocked(false);

        var strongLegs = this.add(new StrongLegsUpgradeEntry(this).generate('N'));
        strongLegs.withParent(this.parent(sentientUpgrades));
        strongLegs.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        strongLegs.hideWhileLocked(false);

        var tough = this.add(new ToughUpgradeEntry(this).generate('O'));
        tough.withParent(this.parent(sentientUpgrades));
        tough.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        tough.hideWhileLocked(false);

        var toughPalms = this.add(new ToughPalmsUpgradeEntry(this).generate('P'));
        toughPalms.withParent(this.parent(sentientUpgrades));
        toughPalms.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        toughPalms.hideWhileLocked(false);

        var battleHungry = this.add(new BattleHungryDowngradeEntry(this).generate('Q'));
        battleHungry.withParent(this.parent(sentientUpgrades));
        battleHungry.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        battleHungry.hideWhileLocked(false);

        var crippledArm = this.add(new CrippledArmDowngradeEntry(this).generate('R'));
        crippledArm.withParent(this.parent(sentientUpgrades));
        crippledArm.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        crippledArm.hideWhileLocked(false);

        var leadenedPick = this.add(new LeadenedPickDowngradeEntry(this).generate('S'));
        leadenedPick.withParent(this.parent(sentientUpgrades));
        leadenedPick.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        leadenedPick.hideWhileLocked(false);

        var dulledBlade = this.add(new DulledBladeDowngradeEntry(this).generate('T'));
        dulledBlade.withParent(this.parent(sentientUpgrades));
        dulledBlade.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        dulledBlade.hideWhileLocked(false);

        var quenched = this.add(new QuenchedDowngradeEntry(this).generate('U'));
        quenched.withParent(this.parent(sentientUpgrades));
        quenched.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        quenched.hideWhileLocked(false);

        var diseased = this.add(new DiseasedDowngradeEntry(this).generate('V'));
        diseased.withParent(this.parent(sentientUpgrades));
        diseased.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        diseased.hideWhileLocked(false);

        var limpLeg = this.add(new LimpLegDowngradeEntry(this).generate('X'));
        limpLeg.withParent(this.parent(sentientUpgrades));
        limpLeg.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        limpLeg.hideWhileLocked(false);

        var stormTrooper = this.add(new StormTrooperDowngradeEntry(this).generate('Y'));
        stormTrooper.withParent(this.parent(sentientUpgrades));
        stormTrooper.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        stormTrooper.hideWhileLocked(false);

        var concreteShoes = this.add(new ConcreteShoesDowngradeEntry(this).generate('Z'));
        concreteShoes.withParent(this.parent(sentientUpgrades));
        concreteShoes.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sentient_upgrades"));
        concreteShoes.hideWhileLocked(false);

        var defenseArrays = this.add(new DefenseArraysEntry(this).generate('1'));
        defenseArrays.withParent(this.parent(arcaneAsh));
        defenseArrays.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/arcane_ash"));
        defenseArrays.hideWhileLocked(false);

        var utilityArrays = this.add(new UtilityArraysEntry(this).generate('2'));
        utilityArrays.withParent(this.parent(arcaneAsh));
        utilityArrays.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/arcane_ash"));
        utilityArrays.hideWhileLocked(false);

        var environmentArrays = this.add(new EnvironmentArraysEntry(this).generate('3'));
        environmentArrays.withParent(this.parent(arcaneAsh));
        environmentArrays.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/arcane_ash"));
        environmentArrays.hideWhileLocked(false);

        var redstoneArrays = this.add(new RedstoneArraysEntry(this).generate('4'));
        redstoneArrays.withParent(this.parent(arcaneAsh));
        redstoneArrays.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/arcane_ash"));
        redstoneArrays.hideWhileLocked(false);

        var spiritSiphonArray = this.add(new SpiritSiphonArrayEntry(this).generate('5'));
        spiritSiphonArray.withParent(this.parent(arcaneAsh));
        spiritSiphonArray.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/arcane_ash"));
        spiritSiphonArray.hideWhileLocked(false);

        var necromancy = this.add(new NecromancySigilEntry(this).generate('6'));
        necromancy.withParent(this.parent(divination));
        necromancy.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        necromancy.hideWhileLocked(false);

        var loyalFriends = this.add(new LoyalFriendsArrayEntry(this).generate('7'));
        loyalFriends.withParent(this.parent(arcaneAsh));
        loyalFriends.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/arcane_ash"));
        loyalFriends.hideWhileLocked(false);

        var boundTreasures = this.add(new BoundTreasuresSigilEntry(this).generate('8'));
        boundTreasures.withParent(this.parent(divination));
        boundTreasures.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/sigil_divination"));
        boundTreasures.hideWhileLocked(false);

        var vortexSigil = this.add(new VortexSigilEntry(this).generate('9'));
        vortexSigil.withParent(this.parent(arcaneAsh));
        vortexSigil.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/arcane_ash"));
        vortexSigil.hideWhileLocked(false);

        var imprisonment = this.add(new ImprisonmentArrayEntry(this).generate('0'));
        imprisonment.withParent(this.parent(arcaneAsh));
        imprisonment.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:alchemy_arrays/arcane_ash"));
        imprisonment.hideWhileLocked(false);
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return super.additionalSetup(category)
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/alchemy_array_base.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/alchemy_array_layer_1.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/alchemy_array_layer_2.png"));
    }

    @Override
    protected String categoryName() {
        return "Alchemy Arrays";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(NVItems.ARCANE_SCRIBE_TOOL.get());
    }

    @Override
    public String categoryId() {
        return "alchemy_arrays";
    }
}
