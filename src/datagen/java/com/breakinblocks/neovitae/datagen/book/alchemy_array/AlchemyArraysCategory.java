package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
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
                "__t_u_v_w___________",
                "____________________",
                "x_y_z_A_B_C_D_E_F__",
                "_G_H_I_J_K_L_M_N_O_",
                "P_Q_R_S_T_U_V_X_Y_Z"
        };
    }

    @Override
    protected void generateEntries() {
        var arcaneAsh = this.add(new ArcaneAshEntry(this).generate('a'));

        var craftingArray = this.add(new CraftingArrayEntry(this).generate('b'));
        craftingArray.withParent(this.parent(arcaneAsh));

        var movementArrays = this.add(new MovementArraysEntry(this).generate('c'));
        movementArrays.withParent(this.parent(arcaneAsh));

        var spikeArray = this.add(new SpikeArrayEntry(this).generate('d'));
        spikeArray.withParent(this.parent(arcaneAsh));

        var timeArrays = this.add(new TimeArraysEntry(this).generate('e'));
        timeArrays.withParent(this.parent(arcaneAsh));

        var divination = this.add(new DivinationSigilEntry(this).generate('f'));

        var seer = this.add(new SeerSigilEntry(this).generate('g'));
        seer.withParent(this.parent(divination));

        var air = this.add(new AirSigilEntry(this).generate('h'));
        air.withParent(this.parent(divination));

        var water = this.add(new WaterSigilEntry(this).generate('i'));
        water.withParent(this.parent(divination));

        var lava = this.add(new LavaSigilEntry(this).generate('j'));
        lava.withParent(this.parent(divination));

        var voidSigil = this.add(new VoidSigilEntry(this).generate('k'));
        voidSigil.withParent(this.parent(divination));

        var greenGrove = this.add(new GreenGroveSigilEntry(this).generate('l'));
        greenGrove.withParent(this.parent(divination));

        var bloodLamp = this.add(new BloodLampSigilEntry(this).generate('m'));
        bloodLamp.withParent(this.parent(divination));

        var mining = this.add(new MiningSigilEntry(this).generate('n'));
        mining.withParent(this.parent(divination));

        var magnetism = this.add(new MagnetismSigilEntry(this).generate('o'));
        magnetism.withParent(this.parent(divination));

        var holding = this.add(new HoldingSigilEntry(this).generate('p'));
        holding.withParent(this.parent(divination));

        var suppression = this.add(new SuppressionSigilEntry(this).generate('q'));
        suppression.withParent(this.parent(divination));

        var phantomBridge = this.add(new PhantomBridgeSigilEntry(this).generate('r'));
        phantomBridge.withParent(this.parent(divination));

        var teleposition = this.add(new TelepositionSigilEntry(this).generate('s'));
        teleposition.withParent(this.parent(divination));

        var ice = this.add(new IceSigilEntry(this).generate('W'));
        ice.withParent(this.parent(divination));

        var livingEquipment = this.add(new LivingEquipmentEntry(this).generate('t'));

        var livingUpgrades = this.add(new LivingUpgradesEntry(this).generate('u'));
        livingUpgrades.withParent(this.parent(livingEquipment));

        var upgradeTomes = this.add(new UpgradeTomesEntry(this).generate('v'));
        upgradeTomes.withParent(this.parent(livingEquipment));

        var trainingBracelet = this.add(new TrainingBraceletEntry(this).generate('w'));
        trainingBracelet.withParent(this.parent(livingEquipment));

        var bodyBuilder = this.add(new BodyBuilderUpgradeEntry(this).generate('x'));
        bodyBuilder.withParent(this.parent(livingUpgrades));

        var brilliance = this.add(new BrillianceUpgradeEntry(this).generate('y'));
        brilliance.withParent(this.parent(livingUpgrades));

        var chargingStrike = this.add(new ChargingStrikeUpgradeEntry(this).generate('z'));
        chargingStrike.withParent(this.parent(livingUpgrades));

        var curiosSockets = this.add(new CuriosSocketsUpgradeEntry(this).generate('A'));
        curiosSockets.withParent(this.parent(livingUpgrades));

        var dwarvenMight = this.add(new DwarvenMightUpgradeEntry(this).generate('B'));
        dwarvenMight.withParent(this.parent(livingUpgrades));

        var elytra = this.add(new ElytraUpgradeEntry(this).generate('C'));
        elytra.withParent(this.parent(livingUpgrades));

        var experienced = this.add(new ExperiencedUpgradeEntry(this).generate('D'));
        experienced.withParent(this.parent(livingUpgrades));

        var fierceStrike = this.add(new FierceStrikeUpgradeEntry(this).generate('E'));
        fierceStrike.withParent(this.parent(livingUpgrades));

        var giftOfIgnis = this.add(new GiftOfIgnisUpgradeEntry(this).generate('F'));
        giftOfIgnis.withParent(this.parent(livingUpgrades));

        var gilded = this.add(new GildedUpgradeEntry(this).generate('G'));
        gilded.withParent(this.parent(livingUpgrades));

        var healthy = this.add(new HealthyUpgradeEntry(this).generate('H'));
        healthy.withParent(this.parent(livingUpgrades));

        var pinCushion = this.add(new PinCushionUpgradeEntry(this).generate('I'));
        pinCushion.withParent(this.parent(livingUpgrades));

        var poisonResistance = this.add(new PoisonResistanceUpgradeEntry(this).generate('J'));
        poisonResistance.withParent(this.parent(livingUpgrades));

        var quickFeet = this.add(new QuickFeetUpgradeEntry(this).generate('K'));
        quickFeet.withParent(this.parent(livingUpgrades));

        var repair = this.add(new RepairUpgradeEntry(this).generate('L'));
        repair.withParent(this.parent(livingUpgrades));

        var softFall = this.add(new SoftFallUpgradeEntry(this).generate('M'));
        softFall.withParent(this.parent(livingUpgrades));

        var strongLegs = this.add(new StrongLegsUpgradeEntry(this).generate('N'));
        strongLegs.withParent(this.parent(livingUpgrades));

        var tough = this.add(new ToughUpgradeEntry(this).generate('O'));
        tough.withParent(this.parent(livingUpgrades));

        var toughPalms = this.add(new ToughPalmsUpgradeEntry(this).generate('P'));
        toughPalms.withParent(this.parent(livingUpgrades));

        var battleHungry = this.add(new BattleHungryDowngradeEntry(this).generate('Q'));
        battleHungry.withParent(this.parent(livingUpgrades));

        var crippledArm = this.add(new CrippledArmDowngradeEntry(this).generate('R'));
        crippledArm.withParent(this.parent(livingUpgrades));

        var leadenedPick = this.add(new LeadenedPickDowngradeEntry(this).generate('S'));
        leadenedPick.withParent(this.parent(livingUpgrades));

        var dulledBlade = this.add(new DulledBladeDowngradeEntry(this).generate('T'));
        dulledBlade.withParent(this.parent(livingUpgrades));

        var quenched = this.add(new QuenchedDowngradeEntry(this).generate('U'));
        quenched.withParent(this.parent(livingUpgrades));

        var diseased = this.add(new DiseasedDowngradeEntry(this).generate('V'));
        diseased.withParent(this.parent(livingUpgrades));

        var limpLeg = this.add(new LimpLegDowngradeEntry(this).generate('X'));
        limpLeg.withParent(this.parent(livingUpgrades));

        var stormTrooper = this.add(new StormTrooperDowngradeEntry(this).generate('Y'));
        stormTrooper.withParent(this.parent(livingUpgrades));

        var concreteShoes = this.add(new ConcreteShoesDowngradeEntry(this).generate('Z'));
        concreteShoes.withParent(this.parent(livingUpgrades));
    }

    @Override
    protected String categoryName() {
        return "Alchemy Arrays";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(NVItems.ARCANE_ASHES.get());
    }

    @Override
    public String categoryId() {
        return "alchemy_arrays";
    }
}
