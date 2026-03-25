package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.common.item.NVItems;

public class RitualsCategory extends CategoryProvider {

    public RitualsCategory(ModonomiconProviderBase parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                "___________S_D_____________________",
                "_____R_____________________________",
                "___________C_T_____________________",
                "___________________________________",
                "__a_b_c_d_e_f_g_h_i_j_k___________",
                "___________________________________",
                "__l_m_n_o_p_q_r_s_t_u_v___________",
                "___________________________________",
                "__w_x_y_z_0_1_2_3_4_5_6___________",
                "___________________________________",
                "__7_8_9_A__________________________"
        };
    }

    @Override
    protected void generateEntries() {
        var basics = this.add(new RitualBasicsEntry(this).generate('R'));

        var stones = this.add(new RitualStonesEntry(this).generate('S'));
        stones.withParent(this.parent(basics));
        var diviner = this.add(new RitualDivinerEntry(this).generate('D'));
        diviner.withParent(this.parent(basics));
        var crystals = this.add(new ActivationCrystalsEntry(this).generate('C'));
        crystals.withParent(this.parent(basics));
        var tinkerer = this.add(new RitualTinkererEntry(this).generate('T'));
        tinkerer.withParent(this.parent(basics));

        var water = this.add(new RitualWaterEntry(this).generate('a'));
        water.withParent(this.parent(basics));
        var lava = this.add(new RitualLavaEntry(this).generate('b'));
        lava.withParent(this.parent(basics));
        var greenGrove = this.add(new RitualGreenGroveEntry(this).generate('c'));
        greenGrove.withParent(this.parent(basics));
        var harvest = this.add(new RitualHarvestEntry(this).generate('d'));
        harvest.withParent(this.parent(basics));
        var felling = this.add(new RitualFellingEntry(this).generate('e'));
        felling.withParent(this.parent(basics));
        var animalGrowth = this.add(new RitualAnimalGrowthEntry(this).generate('f'));
        animalGrowth.withParent(this.parent(basics));
        var wellOfSuffering = this.add(new RitualWellOfSufferingEntry(this).generate('g'));
        wellOfSuffering.withParent(this.parent(basics));
        var featheredKnife = this.add(new RitualFeatheredKnifeEntry(this).generate('h'));
        featheredKnife.withParent(this.parent(basics));
        var regeneration = this.add(new RitualRegenerationEntry(this).generate('i'));
        regeneration.withParent(this.parent(basics));
        var fullStomach = this.add(new RitualFullStomachEntry(this).generate('j'));
        fullStomach.withParent(this.parent(basics));
        var containment = this.add(new RitualContainmentEntry(this).generate('k'));
        containment.withParent(this.parent(basics));

        var expulsion = this.add(new RitualExpulsionEntry(this).generate('l'));
        expulsion.withParent(this.parent(basics));
        var suppression = this.add(new RitualSuppressionEntry(this).generate('m'));
        suppression.withParent(this.parent(basics));
        var speed = this.add(new RitualSpeedEntry(this).generate('n'));
        speed.withParent(this.parent(basics));
        var jump = this.add(new RitualJumpEntry(this).generate('o'));
        jump.withParent(this.parent(basics));
        var condor = this.add(new RitualCondorEntry(this).generate('p'));
        condor.withParent(this.parent(basics));
        var magnetism = this.add(new RitualMagneticEntry(this).generate('q'));
        magnetism.withParent(this.parent(basics));
        var crushing = this.add(new RitualCrushingEntry(this).generate('r'));
        crushing.withParent(this.parent(basics));
        var phantomBridge = this.add(new RitualPhantomBridgeEntry(this).generate('s'));
        phantomBridge.withParent(this.parent(basics));
        var grounding = this.add(new RitualGroundingEntry(this).generate('t'));
        grounding.withParent(this.parent(basics));
        var zephyr = this.add(new RitualZephyrEntry(this).generate('u'));
        zephyr.withParent(this.parent(basics));
        var pump = this.add(new RitualPumpEntry(this).generate('v'));
        pump.withParent(this.parent(basics));

        var simpleDungeon = this.add(new RitualSimpleDungeonEntry(this).generate('w'));
        simpleDungeon.withParent(this.parent(basics));
        var standardDungeon = this.add(new RitualStandardDungeonEntry(this).generate('x'));
        standardDungeon.withParent(this.parent(basics));
        var meteor = this.add(new RitualMeteorEntry(this).generate('y'));
        meteor.withParent(this.parent(basics));
        var crafting = this.add(new RitualCraftingEntry(this).generate('z'));
        crafting.withParent(this.parent(basics));
        var ellipse = this.add(new RitualEllipseEntry(this).generate('0'));
        ellipse.withParent(this.parent(basics));
        var sphereCreate = this.add(new RitualSphereCreateEntry(this).generate('1'));
        sphereCreate.withParent(this.parent(basics));
        var placer = this.add(new RitualPlacerEntry(this).generate('2'));
        placer.withParent(this.parent(basics));
        var yawningVoid = this.add(new RitualYawningVoidEntry(this).generate('3'));
        yawningVoid.withParent(this.parent(basics));
        var geode = this.add(new RitualGeodeEntry(this).generate('4'));
        geode.withParent(this.parent(basics));
        var crystalHarvest = this.add(new RitualCrystalHarvestEntry(this).generate('5'));
        crystalHarvest.withParent(this.parent(basics));
        var crystalSplit = this.add(new RitualCrystalSplitEntry(this).generate('6'));
        crystalSplit.withParent(this.parent(basics));

        var crystalCatalyst = this.add(new RitualCrystalCatalystEntry(this).generate('7'));
        crystalCatalyst.withParent(this.parent(basics));
        var armourEvolve = this.add(new RitualArmourEvolveEntry(this).generate('8'));
        armourEvolve.withParent(this.parent(basics));
        var upgradeRemove = this.add(new RitualUpgradeRemoveEntry(this).generate('9'));
        upgradeRemove.withParent(this.parent(basics));
        var livingDowngrade = this.add(new RitualLivingDowngradeEntry(this).generate('A'));
        livingDowngrade.withParent(this.parent(basics));
    }

    @Override
    protected String categoryName() {
        return "Rituals";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(NVItems.ACTIVATION_CRYSTAL_CREATIVE.get());
    }

    @Override
    public String categoryId() {
        return "rituals";
    }
}
