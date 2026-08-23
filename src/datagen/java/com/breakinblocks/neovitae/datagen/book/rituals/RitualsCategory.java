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
        // Basics + prerequisite spine up top, then ritual bands by altar-tier access:
        // rows 3-5 Tier <=2 (weak crystal), row 7 Tier 3 (tenebrae runes), rows 9-10 Tier 4 (awakened crystal).
        return new String[]{
                "_________R_________",
                "____S__D__C__T_____",
                "___________________",
                "__a_b_c_d_e_f_g____",
                "__h_i_j_k_l_m_n____",
                "__o_p_q_r_s_8______",
                "___________________",
                "__t_u_v_w_x________",
                "___________________",
                "__y_z_1_2_3________",
                "__4_5_6_7_9________"
        };
    }

    @Override
    protected void generateEntries() {
        // ---- Prerequisites: read these before any ritual ----
        var basics = this.add(new RitualBasicsEntry(this).generate('R'));

        var stones = this.add(new RitualStonesEntry(this).generate('S'));
        stones.withParent(this.parent(basics));
        stones.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        stones.hideWhileLocked(false);

        var diviner = this.add(new RitualDivinerEntry(this).generate('D'));
        diviner.withParent(this.parent(stones));
        diviner.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_stones"));
        diviner.hideWhileLocked(false);

        var crystals = this.add(new ActivationCrystalsEntry(this).generate('C'));
        crystals.withParent(this.parent(diviner));
        crystals.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_diviner"));
        crystals.hideWhileLocked(false);

        var tinkerer = this.add(new RitualTinkererEntry(this).generate('T'));
        tinkerer.withParent(this.parent(basics));
        tinkerer.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_basics"));
        tinkerer.hideWhileLocked(false);

        // ---- Tier <=2: basic runes, Weak Activation Crystal ----
        var water = this.add(new RitualWaterEntry(this).generate('a'));
        water.withParent(this.parent(crystals));
        water.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        water.hideWhileLocked(false);

        var lava = this.add(new RitualLavaEntry(this).generate('b'));
        lava.withParent(this.parent(crystals));
        lava.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        lava.hideWhileLocked(false);

        var greenGrove = this.add(new RitualGreenGroveEntry(this).generate('c'));
        greenGrove.withParent(this.parent(crystals));
        greenGrove.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        greenGrove.hideWhileLocked(false);

        var harvest = this.add(new RitualHarvestEntry(this).generate('d'));
        harvest.withParent(this.parent(crystals));
        harvest.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        harvest.hideWhileLocked(false);

        var felling = this.add(new RitualFellingEntry(this).generate('e'));
        felling.withParent(this.parent(crystals));
        felling.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        felling.hideWhileLocked(false);

        var shepherd = this.add(new RitualShepherdEntry(this).generate('f'));
        shepherd.withParent(this.parent(crystals));
        shepherd.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        shepherd.hideWhileLocked(false);

        var butchering = this.add(new RitualButcheringEntry(this).generate('8'));
        butchering.withParent(this.parent(shepherd));
        butchering.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        butchering.hideWhileLocked(false);

        var regeneration = this.add(new RitualRegenerationEntry(this).generate('g'));
        regeneration.withParent(this.parent(crystals));
        regeneration.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        regeneration.hideWhileLocked(false);

        var fullStomach = this.add(new RitualFullStomachEntry(this).generate('h'));
        fullStomach.withParent(this.parent(crystals));
        fullStomach.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        fullStomach.hideWhileLocked(false);

        var containment = this.add(new RitualContainmentEntry(this).generate('i'));
        containment.withParent(this.parent(crystals));
        containment.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        containment.hideWhileLocked(false);

        var expulsion = this.add(new RitualExpulsionEntry(this).generate('j'));
        expulsion.withParent(this.parent(crystals));
        expulsion.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        expulsion.hideWhileLocked(false);

        var suppression = this.add(new RitualSuppressionEntry(this).generate('k'));
        suppression.withParent(this.parent(crystals));
        suppression.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        suppression.hideWhileLocked(false);

        var speed = this.add(new RitualSpeedEntry(this).generate('l'));
        speed.withParent(this.parent(crystals));
        speed.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        speed.hideWhileLocked(false);

        var magnetism = this.add(new RitualMagneticEntry(this).generate('m'));
        magnetism.withParent(this.parent(crystals));
        magnetism.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        magnetism.hideWhileLocked(false);

        var phantomBridge = this.add(new RitualPhantomBridgeEntry(this).generate('n'));
        phantomBridge.withParent(this.parent(crystals));
        phantomBridge.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        phantomBridge.hideWhileLocked(false);

        var grounding = this.add(new RitualGroundingEntry(this).generate('o'));
        grounding.withParent(this.parent(crystals));
        grounding.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        grounding.hideWhileLocked(false);

        var zephyr = this.add(new RitualZephyrEntry(this).generate('p'));
        zephyr.withParent(this.parent(crystals));
        zephyr.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        zephyr.hideWhileLocked(false);

        var pump = this.add(new RitualPumpEntry(this).generate('q'));
        pump.withParent(this.parent(crystals));
        pump.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        pump.hideWhileLocked(false);

        var placer = this.add(new RitualPlacerEntry(this).generate('r'));
        placer.withParent(this.parent(crystals));
        placer.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        placer.hideWhileLocked(false);

        var simpleDungeon = this.add(new RitualSimpleDungeonEntry(this).generate('s'));
        simpleDungeon.withParent(this.parent(crystals));
        simpleDungeon.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        simpleDungeon.hideWhileLocked(false);

        // ---- Tier 3: requires Tenebrae runes (Weak Activation Crystal) ----
        var featheredKnife = this.add(new RitualFeatheredKnifeEntry(this).generate('t'));
        featheredKnife.withParent(this.parent(stones));
        featheredKnife.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_stones"));
        featheredKnife.hideWhileLocked(false);

        var sphereCreate = this.add(new RitualSphereCreateEntry(this).generate('u'));
        sphereCreate.withParent(this.parent(stones));
        sphereCreate.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_stones"));
        sphereCreate.hideWhileLocked(false);

        var yawningVoid = this.add(new RitualYawningVoidEntry(this).generate('v'));
        yawningVoid.withParent(this.parent(stones));
        yawningVoid.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_stones"));
        yawningVoid.hideWhileLocked(false);

        var wellOfSuffering = this.add(new RitualWellOfSufferingEntry(this).generate('w'));
        wellOfSuffering.withParent(this.parent(stones));
        wellOfSuffering.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_stones"));
        wellOfSuffering.hideWhileLocked(false);

        var standardDungeon = this.add(new RitualStandardDungeonEntry(this).generate('x'));
        standardDungeon.withParent(this.parent(stones));
        standardDungeon.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/ritual_stones"));
        standardDungeon.hideWhileLocked(false);

        // ---- Tier 4: requires the Awakened Activation Crystal ----
        var condor = this.add(new RitualCondorEntry(this).generate('y'));
        condor.withParent(this.parent(crystals));
        condor.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        condor.hideWhileLocked(false);

        var crafting = this.add(new RitualCraftingEntry(this).generate('z'));
        crafting.withParent(this.parent(crystals));
        crafting.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        crafting.hideWhileLocked(false);

        var crystallumFractura = this.add(new RitualCrystallumFracturaEntry(this).generate('1'));
        crystallumFractura.withParent(this.parent(crystals));
        crystallumFractura.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        crystallumFractura.hideWhileLocked(false);

        var meteor = this.add(new RitualMeteorEntry(this).generate('2'));
        meteor.withParent(this.parent(crystals));
        meteor.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        meteor.hideWhileLocked(false);

        var livingDowngrade = this.add(new RitualSentientDowngradeEntry(this).generate('3'));
        livingDowngrade.withParent(this.parent(crystals));
        livingDowngrade.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        livingDowngrade.hideWhileLocked(false);

        var armourEvolve = this.add(new RitualSentientArmourEvolveEntry(this).generate('4'));
        armourEvolve.withParent(this.parent(crystals));
        armourEvolve.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        armourEvolve.hideWhileLocked(false);

        var upgradeRemove = this.add(new RitualUpgradeRemoveEntry(this).generate('5'));
        upgradeRemove.withParent(this.parent(crystals));
        upgradeRemove.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        upgradeRemove.hideWhileLocked(false);

        var tormentNexus = this.add(new RitualTormentNexusEntry(this).generate('6'));
        tormentNexus.withParent(this.parent(crystals));
        tormentNexus.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        tormentNexus.hideWhileLocked(false);

        var crystalCatalyst = this.add(new RitualCrystalCatalystEntry(this).generate('7'));
        crystalCatalyst.withParent(this.parent(crystals));
        crystalCatalyst.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        crystalCatalyst.hideWhileLocked(false);

        var enchantedVitae = this.add(new RitualEnchantedVitaeEntry(this).generate('9'));
        enchantedVitae.withParent(this.parent(crystals));
        enchantedVitae.withCondition(BookEntryReadConditionModel.create().withEntry("neovitae:rituals/activation_crystals"));
        enchantedVitae.hideWhileLocked(false);
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
