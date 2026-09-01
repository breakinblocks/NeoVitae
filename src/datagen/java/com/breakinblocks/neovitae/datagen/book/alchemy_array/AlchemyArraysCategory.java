package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;

public class AlchemyArraysCategory extends CategoryProvider {

    public AlchemyArraysCategory(ModonomiconProviderBase parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                "____a___________f____",
                "b_c_d_e_@___g_h_i_j_k",
                "1_2_3_4_5___l_m_n_o_p",
                "__7_9_0_#___q_r_s_W_6",
                "________________8____",
                "_____________________",
                "__________t__________",
                "______v___u___w______",
                "_____________________",
                "____x_y_z_A_B_C_D____",
                "____E_F_G_H_I_J_K____",
                "____$_L_M_N_O_P______",
                "__________%__________",
                "____Q_R_S_T_U_V_X____",
                "______&_Y___Z_+______"
        };
    }

    @Override
    protected void generateEntries() {
        var arcaneAsh = this.add(new ArcaneAshEntry(this).generate('a'));

        var craftingArray = this.add(new CraftingArrayEntry(this).generate('b'));
        craftingArray.withParent(this.parent(arcaneAsh));
        craftingArray.withCondition(this.condition().entryViewedOnce(arcaneAsh));
        craftingArray.hideWhileLocked(false);

        var movementArrays = this.add(new MovementArraysEntry(this).generate('c'));
        movementArrays.withParent(this.parent(arcaneAsh));
        movementArrays.withCondition(this.condition().entryViewedOnce(arcaneAsh));
        movementArrays.hideWhileLocked(false);

        var spikeArray = this.add(new SpikeArrayEntry(this).generate('d'));
        spikeArray.withParent(this.parent(arcaneAsh));
        spikeArray.withCondition(this.condition().entryViewedOnce(arcaneAsh));
        spikeArray.hideWhileLocked(false);

        var telepositionArray = this.add(new TelepositionArrayEntry(this).generate('@'));
        telepositionArray.withParent(this.parent(arcaneAsh));
        telepositionArray.withCondition(this.condition().entryViewedOnce(arcaneAsh));
        telepositionArray.hideWhileLocked(false);

        var timeArrays = this.add(new TimeArraysEntry(this).generate('e'));
        timeArrays.withParent(this.parent(arcaneAsh));
        timeArrays.withCondition(this.condition().entryViewedOnce(arcaneAsh));
        timeArrays.hideWhileLocked(false);

        var divination = this.add(new DivinationSigilEntry(this).generate('f'));

        var seer = this.add(new SeerSigilEntry(this).generate('g'));
        seer.withParent(this.parent(divination));
        seer.withCondition(this.condition().entryViewedOnce(divination));
        seer.hideWhileLocked(false);

        var air = this.add(new AirSigilEntry(this).generate('h'));
        air.withParent(this.parent(divination));
        air.withCondition(this.condition().entryViewedOnce(divination));
        air.hideWhileLocked(false);

        var water = this.add(new WaterSigilEntry(this).generate('i'));
        water.withParent(this.parent(divination));
        water.withCondition(this.condition().entryViewedOnce(divination));
        water.hideWhileLocked(false);

        var lava = this.add(new LavaSigilEntry(this).generate('j'));
        lava.withParent(this.parent(divination));
        lava.withCondition(this.condition().entryViewedOnce(divination));
        lava.hideWhileLocked(false);

        var voidSigil = this.add(new VoidSigilEntry(this).generate('k'));
        voidSigil.withParent(this.parent(divination));
        voidSigil.withCondition(this.condition().entryViewedOnce(divination));
        voidSigil.hideWhileLocked(false);

        var greenGrove = this.add(new GreenGroveSigilEntry(this).generate('l'));
        greenGrove.withParent(this.parent(divination));
        greenGrove.withCondition(this.condition().entryViewedOnce(divination));
        greenGrove.hideWhileLocked(false);

        var bloodLamp = this.add(new BloodLampSigilEntry(this).generate('m'));
        bloodLamp.withParent(this.parent(divination));
        bloodLamp.withCondition(this.condition().entryViewedOnce(divination));
        bloodLamp.hideWhileLocked(false);

        var mining = this.add(new MiningSigilEntry(this).generate('n'));
        mining.withParent(this.parent(divination));
        mining.withCondition(this.condition().entryViewedOnce(divination));
        mining.hideWhileLocked(false);

        var magnetism = this.add(new MagnetismSigilEntry(this).generate('o'));
        magnetism.withParent(this.parent(divination));
        magnetism.withCondition(this.condition().entryViewedOnce(divination));
        magnetism.hideWhileLocked(false);

        var holding = this.add(new HoldingSigilEntry(this).generate('p'));
        holding.withParent(this.parent(divination));
        holding.withCondition(this.condition().entryViewedOnce(divination));
        holding.hideWhileLocked(false);

        var suppression = this.add(new SuppressionSigilEntry(this).generate('q'));
        suppression.withParent(this.parent(divination));
        suppression.withCondition(this.condition().entryViewedOnce(divination));
        suppression.hideWhileLocked(false);

        var phantomBridge = this.add(new PhantomBridgeSigilEntry(this).generate('r'));
        phantomBridge.withParent(this.parent(divination));
        phantomBridge.withCondition(this.condition().entryViewedOnce(divination));
        phantomBridge.hideWhileLocked(false);

        var teleposition = this.add(new TelepositionSigilEntry(this).generate('s'));
        teleposition.withParent(this.parent(divination));
        teleposition.withCondition(this.condition().entryViewedOnce(divination));
        teleposition.hideWhileLocked(false);

        var ice = this.add(new IceSigilEntry(this).generate('W'));
        ice.withParent(this.parent(divination));
        ice.withCondition(this.condition().entryViewedOnce(divination));
        ice.hideWhileLocked(false);

        var sentientEquipment = this.add(new SentientEquipmentEntry(this).generate('t'));

        var sentientUpgrades = this.add(new SentientUpgradesEntry(this).generate('u'));
        sentientUpgrades.withParent(this.parent(sentientEquipment));
        sentientUpgrades.withCondition(this.condition().entryViewedOnce(sentientEquipment));
        sentientUpgrades.hideWhileLocked(false);

        var upgradeTomes = this.add(new UpgradeTomesEntry(this).generate('v'));
        upgradeTomes.withParent(this.parent(sentientEquipment));
        upgradeTomes.withCondition(this.condition().entryViewedOnce(sentientEquipment));
        upgradeTomes.hideWhileLocked(false);

        var trainingBracelet = this.add(new TrainingBraceletEntry(this).generate('w'));
        trainingBracelet.withParent(this.parent(sentientEquipment));
        trainingBracelet.withCondition(this.condition().entryViewedOnce(sentientEquipment));
        trainingBracelet.hideWhileLocked(false);

        var bodyBuilder = this.add(new BodyBuilderUpgradeEntry(this).generate('x'));
        bodyBuilder.withParent(this.parent(sentientUpgrades));
        bodyBuilder.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        bodyBuilder.hideWhileLocked(false);

        var brilliance = this.add(new BrillianceUpgradeEntry(this).generate('y'));
        brilliance.withParent(this.parent(sentientUpgrades));
        brilliance.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        brilliance.hideWhileLocked(false);

        var chargingStrike = this.add(new ChargingStrikeUpgradeEntry(this).generate('z'));
        chargingStrike.withParent(this.parent(sentientUpgrades));
        chargingStrike.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        chargingStrike.hideWhileLocked(false);

        var curiosSockets = this.add(new CuriosSocketsUpgradeEntry(this).generate('A'));
        curiosSockets.withParent(this.parent(sentientUpgrades));
        curiosSockets.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        curiosSockets.hideWhileLocked(false);

        var dwarvenMight = this.add(new DwarvenMightUpgradeEntry(this).generate('B'));
        dwarvenMight.withParent(this.parent(sentientUpgrades));
        dwarvenMight.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        dwarvenMight.hideWhileLocked(false);

        var elytra = this.add(new ElytraUpgradeEntry(this).generate('C'));
        elytra.withParent(this.parent(sentientUpgrades));
        elytra.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        elytra.hideWhileLocked(false);

        var experienced = this.add(new ExperiencedUpgradeEntry(this).generate('D'));
        experienced.withParent(this.parent(sentientUpgrades));
        experienced.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        experienced.hideWhileLocked(false);

        var fierceStrike = this.add(new FierceStrikeUpgradeEntry(this).generate('E'));
        fierceStrike.withParent(this.parent(sentientUpgrades));
        fierceStrike.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        fierceStrike.hideWhileLocked(false);

        var giftOfIgnis = this.add(new GiftOfIgnisUpgradeEntry(this).generate('F'));
        giftOfIgnis.withParent(this.parent(sentientUpgrades));
        giftOfIgnis.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        giftOfIgnis.hideWhileLocked(false);

        var gilded = this.add(new GildedUpgradeEntry(this).generate('G'));
        gilded.withParent(this.parent(sentientUpgrades));
        gilded.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        gilded.hideWhileLocked(false);

        var healthy = this.add(new HealthyUpgradeEntry(this).generate('H'));
        healthy.withParent(this.parent(sentientUpgrades));
        healthy.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        healthy.hideWhileLocked(false);

        var pinCushion = this.add(new PinCushionUpgradeEntry(this).generate('I'));
        pinCushion.withParent(this.parent(sentientUpgrades));
        pinCushion.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        pinCushion.hideWhileLocked(false);

        var poisonResistance = this.add(new PoisonResistanceUpgradeEntry(this).generate('J'));
        poisonResistance.withParent(this.parent(sentientUpgrades));
        poisonResistance.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        poisonResistance.hideWhileLocked(false);

        var quickFeet = this.add(new QuickFeetUpgradeEntry(this).generate('K'));
        quickFeet.withParent(this.parent(sentientUpgrades));
        quickFeet.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        quickFeet.hideWhileLocked(false);

        var repair = this.add(new RepairUpgradeEntry(this).generate('L'));
        repair.withParent(this.parent(sentientUpgrades));
        repair.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        repair.hideWhileLocked(false);

        var softFall = this.add(new SoftFallUpgradeEntry(this).generate('M'));
        softFall.withParent(this.parent(sentientUpgrades));
        softFall.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        softFall.hideWhileLocked(false);

        var strongLegs = this.add(new StrongLegsUpgradeEntry(this).generate('N'));
        strongLegs.withParent(this.parent(sentientUpgrades));
        strongLegs.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        strongLegs.hideWhileLocked(false);

        var tough = this.add(new ToughUpgradeEntry(this).generate('O'));
        tough.withParent(this.parent(sentientUpgrades));
        tough.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        tough.hideWhileLocked(false);

        var toughPalms = this.add(new ToughPalmsUpgradeEntry(this).generate('P'));
        toughPalms.withParent(this.parent(sentientUpgrades));
        toughPalms.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        toughPalms.hideWhileLocked(false);

        var skilled = this.add(new SkilledUpgradeEntry(this).generate('$'));
        skilled.withParent(this.parent(sentientUpgrades));
        skilled.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        skilled.hideWhileLocked(false);

        var downgrades = this.add(new DowngradesEntry(this).generate('%'));
        downgrades.withParent(this.parent(sentientUpgrades));
        downgrades.withCondition(this.condition().entryViewedOnce(sentientUpgrades));
        downgrades.hideWhileLocked(false);

        var battleHungry = this.add(new BattleHungryDowngradeEntry(this).generate('Q'));
        battleHungry.withParent(this.parent(downgrades));
        battleHungry.withCondition(this.condition().entryViewedOnce(downgrades));
        battleHungry.hideWhileLocked(false);

        var crippledArm = this.add(new CrippledArmDowngradeEntry(this).generate('R'));
        crippledArm.withParent(this.parent(downgrades));
        crippledArm.withCondition(this.condition().entryViewedOnce(downgrades));
        crippledArm.hideWhileLocked(false);

        var leadenedPick = this.add(new LeadenedPickDowngradeEntry(this).generate('S'));
        leadenedPick.withParent(this.parent(downgrades));
        leadenedPick.withCondition(this.condition().entryViewedOnce(downgrades));
        leadenedPick.hideWhileLocked(false);

        var dulledBlade = this.add(new DulledBladeDowngradeEntry(this).generate('T'));
        dulledBlade.withParent(this.parent(downgrades));
        dulledBlade.withCondition(this.condition().entryViewedOnce(downgrades));
        dulledBlade.hideWhileLocked(false);

        var quenched = this.add(new QuenchedDowngradeEntry(this).generate('U'));
        quenched.withParent(this.parent(downgrades));
        quenched.withCondition(this.condition().entryViewedOnce(downgrades));
        quenched.hideWhileLocked(false);

        var diseased = this.add(new DiseasedDowngradeEntry(this).generate('V'));
        diseased.withParent(this.parent(downgrades));
        diseased.withCondition(this.condition().entryViewedOnce(downgrades));
        diseased.hideWhileLocked(false);

        var limpLeg = this.add(new LimpLegDowngradeEntry(this).generate('X'));
        limpLeg.withParent(this.parent(downgrades));
        limpLeg.withCondition(this.condition().entryViewedOnce(downgrades));
        limpLeg.hideWhileLocked(false);

        var stormTrooper = this.add(new StormTrooperDowngradeEntry(this).generate('Y'));
        stormTrooper.withParent(this.parent(downgrades));
        stormTrooper.withCondition(this.condition().entryViewedOnce(downgrades));
        stormTrooper.hideWhileLocked(false);

        var concreteShoes = this.add(new ConcreteShoesDowngradeEntry(this).generate('Z'));
        concreteShoes.withParent(this.parent(downgrades));
        concreteShoes.withCondition(this.condition().entryViewedOnce(downgrades));
        concreteShoes.hideWhileLocked(false);

        var poisonedBlood = this.add(new PoisonedBloodDowngradeEntry(this).generate('&'));
        poisonedBlood.withParent(this.parent(downgrades));
        poisonedBlood.withCondition(this.condition().entryViewedOnce(downgrades));
        poisonedBlood.hideWhileLocked(false);

        var hollowHunger = this.add(new HollowHungerDowngradeEntry(this).generate('+'));
        hollowHunger.withParent(this.parent(downgrades));
        hollowHunger.withCondition(this.condition().entryViewedOnce(downgrades));
        hollowHunger.hideWhileLocked(false);

        var defenseArrays = this.add(new DefenseArraysEntry(this).generate('1'));
        defenseArrays.withParent(this.parent(arcaneAsh));
        defenseArrays.withCondition(this.condition().entryViewedOnce(arcaneAsh));
        defenseArrays.hideWhileLocked(false);

        var utilityArrays = this.add(new UtilityArraysEntry(this).generate('2'));
        utilityArrays.withParent(this.parent(arcaneAsh));
        utilityArrays.withCondition(this.condition().entryViewedOnce(arcaneAsh));
        utilityArrays.hideWhileLocked(false);

        var environmentArrays = this.add(new EnvironmentArraysEntry(this).generate('3'));
        environmentArrays.withParent(this.parent(arcaneAsh));
        environmentArrays.withCondition(this.condition().entryViewedOnce(arcaneAsh));
        environmentArrays.hideWhileLocked(false);

        var redstoneArrays = this.add(new RedstoneArraysEntry(this).generate('4'));
        redstoneArrays.withParent(this.parent(arcaneAsh));
        redstoneArrays.withCondition(this.condition().entryViewedOnce(arcaneAsh));
        redstoneArrays.hideWhileLocked(false);

        var spiritSiphonArray = this.add(new SpiritSiphonArrayEntry(this).generate('5'));
        spiritSiphonArray.withParent(this.parent(arcaneAsh));
        spiritSiphonArray.withCondition(this.condition().entryViewedOnce(arcaneAsh));
        spiritSiphonArray.hideWhileLocked(false);

        var necromancy = this.add(new NecromancySigilEntry(this).generate('6'));
        necromancy.withParent(this.parent(divination));
        necromancy.withCondition(this.condition().entryViewedOnce(divination));
        necromancy.hideWhileLocked(false);

        var loyalFriends = this.add(new LoyalFriendsArrayEntry(this).generate('7'));
        loyalFriends.withParent(this.parent(arcaneAsh));
        loyalFriends.withCondition(this.condition().entryViewedOnce(arcaneAsh));
        loyalFriends.hideWhileLocked(false);

        var boundTreasures = this.add(new BoundTreasuresSigilEntry(this).generate('8'));
        boundTreasures.withParent(this.parent(divination));
        boundTreasures.withCondition(this.condition().entryViewedOnce(divination));
        boundTreasures.hideWhileLocked(false);

        var vortexSigil = this.add(new VortexSigilEntry(this).generate('9'));
        vortexSigil.withParent(this.parent(arcaneAsh));
        vortexSigil.withCondition(this.condition().entryViewedOnce(arcaneAsh));
        vortexSigil.hideWhileLocked(false);

        var imprisonment = this.add(new ImprisonmentArrayEntry(this).generate('0'));
        imprisonment.withParent(this.parent(arcaneAsh));
        imprisonment.withCondition(this.condition().entryViewedOnce(arcaneAsh));
        imprisonment.hideWhileLocked(false);

        var liquifiedExperience = this.add(new LiquifiedExperienceArrayEntry(this).generate('#'));
        liquifiedExperience.withParent(this.parent(arcaneAsh));
        liquifiedExperience.withCondition(this.condition().entryViewedOnce(arcaneAsh));
        liquifiedExperience.hideWhileLocked(false);
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
