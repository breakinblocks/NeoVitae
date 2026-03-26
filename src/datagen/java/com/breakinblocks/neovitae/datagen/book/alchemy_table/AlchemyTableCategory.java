package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryReadConditionModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;

public class AlchemyTableCategory extends CategoryProvider {

    public AlchemyTableCategory(ModonomiconProviderBase parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        // Top-down: AlchemyTable at top, anointments left, potions right
        return new String[]{
                "______a__________",
                "__b_________c____",
                "_d_e_f__p_q_r_s_u",
                "_g_h_i__v_x_y_z_A",
                "_j_k_l__D_E_J_M__",
                "_m_n_o__B_t______",
                "________w_N______",
                "________G_C_K____",
                "________H________",
                "________I_L______",
                "________F________"
        };
    }

    @Override
    protected void generateEntries() {
        var alchemyTable = this.add(new AlchemyTableEntry(this).generate('a'));

        var anointments = this.add(new AnointmentsEntry(this).generate('b'));
        anointments.withParent(this.parent(alchemyTable));
        anointments.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/alchemy_table"));
        anointments.hideWhileLocked(false);

        var potions = this.add(new PotionCraftingEntry(this).generate('c'));
        potions.withParent(this.parent(alchemyTable));
        potions.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/alchemy_table"));
        potions.hideWhileLocked(false);

        var melee = this.add(new MeleeAnointmentEntry(this).generate('d'));
        melee.withParent(this.parent(anointments));
        melee.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/anointments"));
        melee.hideWhileLocked(false);

        var fortune = this.add(new FortuneAnointmentEntry(this).generate('e'));
        fortune.withParent(this.parent(anointments));
        fortune.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/anointments"));
        fortune.hideWhileLocked(false);

        var looting = this.add(new LootingAnointmentEntry(this).generate('f'));
        looting.withParent(this.parent(anointments));
        looting.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/anointments"));
        looting.hideWhileLocked(false);

        var holyWater = this.add(new HolyWaterAnointmentEntry(this).generate('g'));
        holyWater.withParent(this.parent(anointments));
        holyWater.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/anointments"));
        holyWater.hideWhileLocked(false);

        var hiddenKnowledge = this.add(new HiddenKnowledgeAnointmentEntry(this).generate('h'));
        hiddenKnowledge.withParent(this.parent(anointments));
        hiddenKnowledge.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/anointments"));
        hiddenKnowledge.hideWhileLocked(false);

        var silkTouch = this.add(new SilkTouchAnointmentEntry(this).generate('i'));
        silkTouch.withParent(this.parent(anointments));
        silkTouch.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/anointments"));
        silkTouch.hideWhileLocked(false);

        var smelting = this.add(new SmeltingAnointmentEntry(this).generate('j'));
        smelting.withParent(this.parent(anointments));
        smelting.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/anointments"));
        smelting.hideWhileLocked(false);

        var voiding = this.add(new VoidingAnointmentEntry(this).generate('k'));
        voiding.withParent(this.parent(anointments));
        voiding.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/anointments"));
        voiding.hideWhileLocked(false);

        var weaponRepair = this.add(new WeaponRepairAnointmentEntry(this).generate('l'));
        weaponRepair.withParent(this.parent(anointments));
        weaponRepair.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/anointments"));
        weaponRepair.hideWhileLocked(false);

        var bowPower = this.add(new BowPowerAnointmentEntry(this).generate('m'));
        bowPower.withParent(this.parent(anointments));
        bowPower.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/anointments"));
        bowPower.hideWhileLocked(false);

        var bowVelocity = this.add(new BowVelocityAnointmentEntry(this).generate('n'));
        bowVelocity.withParent(this.parent(anointments));
        bowVelocity.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/anointments"));
        bowVelocity.hideWhileLocked(false);

        var quickDraw = this.add(new QuickDrawAnointmentEntry(this).generate('o'));
        quickDraw.withParent(this.parent(anointments));
        quickDraw.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/anointments"));
        quickDraw.hideWhileLocked(false);

        // Base flasks - parent is potions
        var speed = this.add(new SpeedFlaskEntry(this).generate('p'));
        speed.withParent(this.parent(potions));
        speed.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/potion_crafting"));
        speed.hideWhileLocked(false);

        var strength = this.add(new StrengthFlaskEntry(this).generate('q'));
        strength.withParent(this.parent(potions));
        strength.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/potion_crafting"));
        strength.hideWhileLocked(false);

        var regen = this.add(new RegenerationFlaskEntry(this).generate('r'));
        regen.withParent(this.parent(potions));
        regen.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/potion_crafting"));
        regen.hideWhileLocked(false);

        var health = this.add(new InstantHealthFlaskEntry(this).generate('s'));
        health.withParent(this.parent(potions));
        health.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/potion_crafting"));
        health.hideWhileLocked(false);

        var poison = this.add(new PoisonFlaskEntry(this).generate('u'));
        poison.withParent(this.parent(potions));
        poison.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/potion_crafting"));
        poison.hideWhileLocked(false);

        var weakness = this.add(new WeaknessFlaskEntry(this).generate('D'));
        weakness.withParent(this.parent(potions));
        weakness.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/potion_crafting"));
        weakness.hideWhileLocked(false);

        var bounce = this.add(new BounceFlaskEntry(this).generate('E'));
        bounce.withParent(this.parent(potions));
        bounce.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/potion_crafting"));
        bounce.hideWhileLocked(false);

        var hardCloak = this.add(new HardCloakFlaskEntry(this).generate('J'));
        hardCloak.withParent(this.parent(potions));
        hardCloak.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/potion_crafting"));
        hardCloak.hideWhileLocked(false);

        var passive = this.add(new PassiveFlaskEntry(this).generate('M'));
        passive.withParent(this.parent(potions));
        passive.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/potion_crafting"));
        passive.hideWhileLocked(false);

        var nightVision = this.add(new NightVisionFlaskEntry(this).generate('v'));
        nightVision.withParent(this.parent(potions));
        nightVision.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/potion_crafting"));
        nightVision.hideWhileLocked(false);

        var fireResistance = this.add(new FireResistanceFlaskEntry(this).generate('x'));
        fireResistance.withParent(this.parent(potions));
        fireResistance.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/potion_crafting"));
        fireResistance.hideWhileLocked(false);

        var waterBreathing = this.add(new WaterBreathingFlaskEntry(this).generate('y'));
        waterBreathing.withParent(this.parent(potions));
        waterBreathing.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/potion_crafting"));
        waterBreathing.hideWhileLocked(false);

        var jumpBoost = this.add(new JumpBoostFlaskEntry(this).generate('z'));
        jumpBoost.withParent(this.parent(potions));
        jumpBoost.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/potion_crafting"));
        jumpBoost.hideWhileLocked(false);

        var slowFalling = this.add(new SlowFallingFlaskEntry(this).generate('A'));
        slowFalling.withParent(this.parent(potions));
        slowFalling.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/potion_crafting"));
        slowFalling.hideWhileLocked(false);

        // Derived flasks - parent is the flask they're crafted from
        var slowness = this.add(new SlownessFlaskEntry(this).generate('B'));
        slowness.withParent(this.parent(speed));
        slowness.withParent(this.parent(jumpBoost));
        slowness.showWhenAnyParentUnlocked(true);
        slowness.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/flask_speed"));
        slowness.hideWhileLocked(false);

        var damage = this.add(new InstantDamageFlaskEntry(this).generate('t'));
        damage.withParent(this.parent(health));
        damage.withParent(this.parent(poison));
        damage.showWhenAnyParentUnlocked(true);
        damage.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/flask_instant_health"));
        damage.hideWhileLocked(false);

        var invisibility = this.add(new InvisibilityFlaskEntry(this).generate('w'));
        invisibility.withParent(this.parent(nightVision));
        invisibility.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/flask_night_vision"));
        invisibility.hideWhileLocked(false);

        var spectralSight = this.add(new SpectralSightFlaskEntry(this).generate('N'));
        spectralSight.withParent(this.parent(nightVision));
        spectralSight.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/flask_night_vision"));
        spectralSight.hideWhileLocked(false);

        var levitation = this.add(new LevitationFlaskEntry(this).generate('C'));
        levitation.withParent(this.parent(slowFalling));
        levitation.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/flask_slow_falling"));
        levitation.hideWhileLocked(false);

        var grounded = this.add(new GroundedFlaskEntry(this).generate('G'));
        grounded.withParent(this.parent(jumpBoost));
        grounded.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/flask_jump_boost"));
        grounded.hideWhileLocked(false);

        var gravity = this.add(new GravityFlaskEntry(this).generate('H'));
        gravity.withParent(this.parent(grounded));
        gravity.withParent(this.parent(slowFalling));
        gravity.showWhenAnyParentUnlocked(true);
        gravity.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/flask_grounded"));
        gravity.hideWhileLocked(false);

        var obsidianCloak = this.add(new ObsidianCloakFlaskEntry(this).generate('K'));
        obsidianCloak.withParent(this.parent(hardCloak));
        obsidianCloak.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/flask_hard_cloak"));
        obsidianCloak.hideWhileLocked(false);

        var suspended = this.add(new SuspendedFlaskEntry(this).generate('I'));
        suspended.withParent(this.parent(gravity));
        suspended.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/flask_gravity"));
        suspended.hideWhileLocked(false);

        var heavyHeart = this.add(new HeavyHeartFlaskEntry(this).generate('L'));
        heavyHeart.withParent(this.parent(gravity));
        heavyHeart.withParent(this.parent(health));
        heavyHeart.showWhenAnyParentUnlocked(true);
        heavyHeart.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/flask_gravity"));
        heavyHeart.hideWhileLocked(false);

        var flight = this.add(new FlightFlaskEntry(this).generate('F'));
        flight.withParent(this.parent(suspended));
        flight.withParent(this.parent(levitation));
        flight.showWhenAnyParentUnlocked(true);
        flight.withCondition(BookEntryReadConditionModel.create().withEntry("alchemy_table/flask_suspended"));
        flight.hideWhileLocked(false);
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return super.additionalSetup(category)
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/alchemy_table_base.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/alchemy_table_layer_1.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/alchemy_table_layer_2.png"));
    }

    @Override
    protected String categoryName() {
        return "Alchemy Table";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(NVBlocks.ALCHEMY_TABLE.asItem());
    }

    @Override
    public String categoryId() {
        return "alchemy_table";
    }
}
