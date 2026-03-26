package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryReadConditionModel;
import com.breakinblocks.neovitae.NeoVitae;
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
        stones.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        stones.hideWhileLocked(false);
        var diviner = this.add(new RitualDivinerEntry(this).generate('D'));
        diviner.withParent(this.parent(basics));
        diviner.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        diviner.hideWhileLocked(false);
        var crystals = this.add(new ActivationCrystalsEntry(this).generate('C'));
        crystals.withParent(this.parent(basics));
        crystals.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        crystals.hideWhileLocked(false);
        var tinkerer = this.add(new RitualTinkererEntry(this).generate('T'));
        tinkerer.withParent(this.parent(basics));
        tinkerer.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        tinkerer.hideWhileLocked(false);

        var water = this.add(new RitualWaterEntry(this).generate('a'));
        water.withParent(this.parent(basics));
        water.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        water.hideWhileLocked(false);
        var lava = this.add(new RitualLavaEntry(this).generate('b'));
        lava.withParent(this.parent(basics));
        lava.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        lava.hideWhileLocked(false);
        var greenGrove = this.add(new RitualGreenGroveEntry(this).generate('c'));
        greenGrove.withParent(this.parent(basics));
        greenGrove.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        greenGrove.hideWhileLocked(false);
        var harvest = this.add(new RitualHarvestEntry(this).generate('d'));
        harvest.withParent(this.parent(basics));
        harvest.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        harvest.hideWhileLocked(false);
        var felling = this.add(new RitualFellingEntry(this).generate('e'));
        felling.withParent(this.parent(basics));
        felling.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        felling.hideWhileLocked(false);
        var animalGrowth = this.add(new RitualAnimalGrowthEntry(this).generate('f'));
        animalGrowth.withParent(this.parent(basics));
        animalGrowth.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        animalGrowth.hideWhileLocked(false);
        var wellOfSuffering = this.add(new RitualWellOfSufferingEntry(this).generate('g'));
        wellOfSuffering.withParent(this.parent(basics));
        wellOfSuffering.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        wellOfSuffering.hideWhileLocked(false);
        var featheredKnife = this.add(new RitualFeatheredKnifeEntry(this).generate('h'));
        featheredKnife.withParent(this.parent(basics));
        featheredKnife.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        featheredKnife.hideWhileLocked(false);
        var regeneration = this.add(new RitualRegenerationEntry(this).generate('i'));
        regeneration.withParent(this.parent(basics));
        regeneration.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        regeneration.hideWhileLocked(false);
        var fullStomach = this.add(new RitualFullStomachEntry(this).generate('j'));
        fullStomach.withParent(this.parent(basics));
        fullStomach.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        fullStomach.hideWhileLocked(false);
        var containment = this.add(new RitualContainmentEntry(this).generate('k'));
        containment.withParent(this.parent(basics));
        containment.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        containment.hideWhileLocked(false);

        var expulsion = this.add(new RitualExpulsionEntry(this).generate('l'));
        expulsion.withParent(this.parent(basics));
        expulsion.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        expulsion.hideWhileLocked(false);
        var suppression = this.add(new RitualSuppressionEntry(this).generate('m'));
        suppression.withParent(this.parent(basics));
        suppression.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        suppression.hideWhileLocked(false);
        var speed = this.add(new RitualSpeedEntry(this).generate('n'));
        speed.withParent(this.parent(basics));
        speed.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        speed.hideWhileLocked(false);
        var jump = this.add(new RitualJumpEntry(this).generate('o'));
        jump.withParent(this.parent(basics));
        jump.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        jump.hideWhileLocked(false);
        var condor = this.add(new RitualCondorEntry(this).generate('p'));
        condor.withParent(this.parent(basics));
        condor.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        condor.hideWhileLocked(false);
        var magnetism = this.add(new RitualMagneticEntry(this).generate('q'));
        magnetism.withParent(this.parent(basics));
        magnetism.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        magnetism.hideWhileLocked(false);
        var crushing = this.add(new RitualCrushingEntry(this).generate('r'));
        crushing.withParent(this.parent(basics));
        crushing.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        crushing.hideWhileLocked(false);
        var phantomBridge = this.add(new RitualPhantomBridgeEntry(this).generate('s'));
        phantomBridge.withParent(this.parent(basics));
        phantomBridge.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        phantomBridge.hideWhileLocked(false);
        var grounding = this.add(new RitualGroundingEntry(this).generate('t'));
        grounding.withParent(this.parent(basics));
        grounding.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        grounding.hideWhileLocked(false);
        var zephyr = this.add(new RitualZephyrEntry(this).generate('u'));
        zephyr.withParent(this.parent(basics));
        zephyr.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        zephyr.hideWhileLocked(false);
        var pump = this.add(new RitualPumpEntry(this).generate('v'));
        pump.withParent(this.parent(basics));
        pump.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        pump.hideWhileLocked(false);

        var simpleDungeon = this.add(new RitualSimpleDungeonEntry(this).generate('w'));
        simpleDungeon.withParent(this.parent(basics));
        simpleDungeon.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        simpleDungeon.hideWhileLocked(false);
        var standardDungeon = this.add(new RitualStandardDungeonEntry(this).generate('x'));
        standardDungeon.withParent(this.parent(basics));
        standardDungeon.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        standardDungeon.hideWhileLocked(false);
        var meteor = this.add(new RitualMeteorEntry(this).generate('y'));
        meteor.withParent(this.parent(basics));
        meteor.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        meteor.hideWhileLocked(false);
        var crafting = this.add(new RitualCraftingEntry(this).generate('z'));
        crafting.withParent(this.parent(basics));
        crafting.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        crafting.hideWhileLocked(false);
        var ellipse = this.add(new RitualEllipseEntry(this).generate('0'));
        ellipse.withParent(this.parent(basics));
        ellipse.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        ellipse.hideWhileLocked(false);
        var sphereCreate = this.add(new RitualSphereCreateEntry(this).generate('1'));
        sphereCreate.withParent(this.parent(basics));
        sphereCreate.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        sphereCreate.hideWhileLocked(false);
        var placer = this.add(new RitualPlacerEntry(this).generate('2'));
        placer.withParent(this.parent(basics));
        placer.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        placer.hideWhileLocked(false);
        var yawningVoid = this.add(new RitualYawningVoidEntry(this).generate('3'));
        yawningVoid.withParent(this.parent(basics));
        yawningVoid.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        yawningVoid.hideWhileLocked(false);
        var geode = this.add(new RitualGeodeEntry(this).generate('4'));
        geode.withParent(this.parent(basics));
        geode.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        geode.hideWhileLocked(false);
        var crystalHarvest = this.add(new RitualCrystalHarvestEntry(this).generate('5'));
        crystalHarvest.withParent(this.parent(basics));
        crystalHarvest.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        crystalHarvest.hideWhileLocked(false);
        var crystalSplit = this.add(new RitualCrystalSplitEntry(this).generate('6'));
        crystalSplit.withParent(this.parent(basics));
        crystalSplit.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        crystalSplit.hideWhileLocked(false);

        var crystalCatalyst = this.add(new RitualCrystalCatalystEntry(this).generate('7'));
        crystalCatalyst.withParent(this.parent(basics));
        crystalCatalyst.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        crystalCatalyst.hideWhileLocked(false);
        var armourEvolve = this.add(new RitualArmourEvolveEntry(this).generate('8'));
        armourEvolve.withParent(this.parent(basics));
        armourEvolve.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        armourEvolve.hideWhileLocked(false);
        var upgradeRemove = this.add(new RitualUpgradeRemoveEntry(this).generate('9'));
        upgradeRemove.withParent(this.parent(basics));
        upgradeRemove.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        upgradeRemove.hideWhileLocked(false);
        var livingDowngrade = this.add(new RitualLivingDowngradeEntry(this).generate('A'));
        livingDowngrade.withParent(this.parent(basics));
        livingDowngrade.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        livingDowngrade.hideWhileLocked(false);
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return super.additionalSetup(category)
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/rituals_base.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/rituals_layer_1.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/rituals_layer_2.png"));
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
