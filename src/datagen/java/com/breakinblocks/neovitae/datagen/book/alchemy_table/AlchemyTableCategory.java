package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.common.block.NVBlocks;

public class AlchemyTableCategory extends CategoryProvider {

    public AlchemyTableCategory(ModonomiconProviderBase parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        // Left-to-right tree: AlchemyTable → branches → children → derived
        return new String[]{
                "a_______d_e_f__________________________________",
                "________g_h_i__________________________________",
                "____b___j_k_l__________________________________",
                "________m_n_o__________________________________",
                "________p_______B______________________________",
                "________q______________________________________",
                "________r______________________________________",
                "____c___s_______t______________________________",
                "________u______________________________________",
                "________v_______w______________________________",
                "________x_______N______________________________",
                "________y______________________________________",
                "________z_______G_______H_______I_______F______",
                "________A_______C_______L______________________",
                "________D_E____________________________________",
                "________J_______K______________________________",
                "________M______________________________________"
        };
    }

    @Override
    protected void generateEntries() {
        var alchemyTable = this.add(new AlchemyTableEntry(this).generate('a'));

        var anointments = this.add(new AnointmentsEntry(this).generate('b'));
        anointments.withParent(this.parent(alchemyTable));

        var potions = this.add(new PotionCraftingEntry(this).generate('c'));
        potions.withParent(this.parent(alchemyTable));

        var melee = this.add(new MeleeAnointmentEntry(this).generate('d'));
        melee.withParent(this.parent(anointments));

        var fortune = this.add(new FortuneAnointmentEntry(this).generate('e'));
        fortune.withParent(this.parent(anointments));

        var looting = this.add(new LootingAnointmentEntry(this).generate('f'));
        looting.withParent(this.parent(anointments));

        var holyWater = this.add(new HolyWaterAnointmentEntry(this).generate('g'));
        holyWater.withParent(this.parent(anointments));

        var hiddenKnowledge = this.add(new HiddenKnowledgeAnointmentEntry(this).generate('h'));
        hiddenKnowledge.withParent(this.parent(anointments));

        var silkTouch = this.add(new SilkTouchAnointmentEntry(this).generate('i'));
        silkTouch.withParent(this.parent(anointments));

        var smelting = this.add(new SmeltingAnointmentEntry(this).generate('j'));
        smelting.withParent(this.parent(anointments));

        var voiding = this.add(new VoidingAnointmentEntry(this).generate('k'));
        voiding.withParent(this.parent(anointments));

        var weaponRepair = this.add(new WeaponRepairAnointmentEntry(this).generate('l'));
        weaponRepair.withParent(this.parent(anointments));

        var bowPower = this.add(new BowPowerAnointmentEntry(this).generate('m'));
        bowPower.withParent(this.parent(anointments));

        var bowVelocity = this.add(new BowVelocityAnointmentEntry(this).generate('n'));
        bowVelocity.withParent(this.parent(anointments));

        var quickDraw = this.add(new QuickDrawAnointmentEntry(this).generate('o'));
        quickDraw.withParent(this.parent(anointments));

        // Base flasks - parent is potions
        var speed = this.add(new SpeedFlaskEntry(this).generate('p'));
        speed.withParent(this.parent(potions));

        var strength = this.add(new StrengthFlaskEntry(this).generate('q'));
        strength.withParent(this.parent(potions));

        var regen = this.add(new RegenerationFlaskEntry(this).generate('r'));
        regen.withParent(this.parent(potions));

        var health = this.add(new InstantHealthFlaskEntry(this).generate('s'));
        health.withParent(this.parent(potions));

        var poison = this.add(new PoisonFlaskEntry(this).generate('u'));
        poison.withParent(this.parent(potions));

        var weakness = this.add(new WeaknessFlaskEntry(this).generate('D'));
        weakness.withParent(this.parent(potions));

        var bounce = this.add(new BounceFlaskEntry(this).generate('E'));
        bounce.withParent(this.parent(potions));

        var hardCloak = this.add(new HardCloakFlaskEntry(this).generate('J'));
        hardCloak.withParent(this.parent(potions));

        var passive = this.add(new PassiveFlaskEntry(this).generate('M'));
        passive.withParent(this.parent(potions));

        var nightVision = this.add(new NightVisionFlaskEntry(this).generate('v'));
        nightVision.withParent(this.parent(potions));

        var fireResistance = this.add(new FireResistanceFlaskEntry(this).generate('x'));
        fireResistance.withParent(this.parent(potions));

        var waterBreathing = this.add(new WaterBreathingFlaskEntry(this).generate('y'));
        waterBreathing.withParent(this.parent(potions));

        var jumpBoost = this.add(new JumpBoostFlaskEntry(this).generate('z'));
        jumpBoost.withParent(this.parent(potions));

        var slowFalling = this.add(new SlowFallingFlaskEntry(this).generate('A'));
        slowFalling.withParent(this.parent(potions));

        // Derived flasks - parent is the flask they're crafted from
        var slowness = this.add(new SlownessFlaskEntry(this).generate('B'));
        slowness.withParent(this.parent(speed));
        slowness.withParent(this.parent(jumpBoost));

        var damage = this.add(new InstantDamageFlaskEntry(this).generate('t'));
        damage.withParent(this.parent(health));
        damage.withParent(this.parent(poison));

        var invisibility = this.add(new InvisibilityFlaskEntry(this).generate('w'));
        invisibility.withParent(this.parent(nightVision));

        var spectralSight = this.add(new SpectralSightFlaskEntry(this).generate('N'));
        spectralSight.withParent(this.parent(nightVision));

        var levitation = this.add(new LevitationFlaskEntry(this).generate('C'));
        levitation.withParent(this.parent(slowFalling));

        var grounded = this.add(new GroundedFlaskEntry(this).generate('G'));
        grounded.withParent(this.parent(jumpBoost));

        var gravity = this.add(new GravityFlaskEntry(this).generate('H'));
        gravity.withParent(this.parent(grounded));
        gravity.withParent(this.parent(slowFalling));

        var obsidianCloak = this.add(new ObsidianCloakFlaskEntry(this).generate('K'));
        obsidianCloak.withParent(this.parent(hardCloak));

        var suspended = this.add(new SuspendedFlaskEntry(this).generate('I'));
        suspended.withParent(this.parent(gravity));

        var heavyHeart = this.add(new HeavyHeartFlaskEntry(this).generate('L'));
        heavyHeart.withParent(this.parent(gravity));
        heavyHeart.withParent(this.parent(health));

        var flight = this.add(new FlightFlaskEntry(this).generate('F'));
        flight.withParent(this.parent(suspended));
        flight.withParent(this.parent(levitation));
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
