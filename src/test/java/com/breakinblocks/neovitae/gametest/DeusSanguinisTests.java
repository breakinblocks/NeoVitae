package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.attribute.NVAttributes;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.effect.AnimaSeveranceEffect;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.event.DeusSanguinisTrainingHandler;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.UUID;

public final class DeusSanguinisTests {

    private DeusSanguinisTests() {}

    public static void register(NVTestRegistrar r) {
        r.add("deus_sanguinis/needs_hellforged_chestplate", 20, DeusSanguinisTests::needsHellforgedChestplate);
        r.add("deus_sanguinis/training_credit_has_cooldown", 20, DeusSanguinisTests::trainingCreditHasCooldown);
        r.add("deus_sanguinis/blood_shield_caps_at_configured_percent", 40, DeusSanguinisTests::bloodShieldCapsAtConfiguredPercent);
        r.add("deus_sanguinis/blood_siphon_is_percent_of_damage", 40, DeusSanguinisTests::bloodSiphonIsPercentOfDamage);
        r.add("deus_sanguinis/anima_severance_cannot_be_cured", 20, DeusSanguinisTests::animaSeveranceCannotBeCured);
    }


    private static Holder<SentientUpgrade> upgrade(GameTestHelper helper) {
        return helper.getLevel().registryAccess().lookupOrThrow(NVRegistries.Keys.SENTIENT_UPGRADES)
                .getOrThrow(DeusSanguinisTrainingHandler.DEUS_SANGUINIS);
    }

    private static ItemStack plate(int maxPoints) {
        ItemStack plate = new ItemStack(NVItems.SENTIENT_PLATE.get());
        plate.set(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS, maxPoints);
        return plate;
    }

    private static void needsHellforgedChestplate(GameTestHelper helper) {
        Holder<SentientUpgrade> deusSanguinis = upgrade(helper);
        helper.assertTrue(!SentientHelper.meetsEvolutionRequirement(plate(600), deusSanguinis),
                "A 600-point chestplate should reject Deus Sanguinis");
        helper.assertTrue(SentientHelper.meetsEvolutionRequirement(plate(700), deusSanguinis),
                "A 700-point chestplate should accept Deus Sanguinis");
        helper.assertTrue(deusSanguinis.value().levels().levelToCost().get(10) == 200,
                "Deus Sanguinis should cost 200 points at level 10");
        helper.succeed();
    }

    private static void trainingCreditHasCooldown(GameTestHelper helper) {
        UUID owner = UUID.randomUUID();
        int cooldown = NeoVitae.SERVER_CONFIG.DEUS_SANGUINIS_COOLDOWN.get();
        int minEv = Math.max(1, NeoVitae.SERVER_CONFIG.DEUS_SANGUINIS_MIN_EV.get());
        boolean first = DeusSanguinisTrainingHandler.recordSpend(owner, minEv, 1000);
        boolean burst = DeusSanguinisTrainingHandler.recordSpend(owner, minEv * 50, 1001);
        boolean afterCooldown = DeusSanguinisTrainingHandler.recordSpend(owner, 0, 1000 + cooldown);
        helper.assertTrue(first, "The first qualifying spend should earn a credit");
        helper.assertTrue(!burst, "A burst of spending inside the cooldown should not earn another credit");
        helper.assertTrue(afterCooldown, "Spending saved up during the cooldown should earn a credit once it ends");
        helper.succeed();
    }

    private static void bloodShieldCapsAtConfiguredPercent(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getAttribute(NVAttributes.BLOOD_SHIELD).setBaseValue(100.0);
        Anima anima = AnimaHelper.getAnima(player.getUUID());
        anima.add(AnimaTicket.create(1000000), 10000000);
        player.setHealth(20.0F);
        player.invulnerableTime = 0;
        player.hurt(helper.getLevel().damageSources().generic(), 10.0F);
        double cap = NeoVitae.SERVER_CONFIG.BLOOD_SHIELD_CAP.get();
        float expectedLoss = (float) (10.0 * (1.0 - cap / 100.0));
        float loss = 20.0F - player.getHealth();
        helper.assertTrue(Math.abs(loss - expectedLoss) < 0.05F,
                "Blood Shield 100 should prevent " + cap + "% of damage, lost " + loss + " expected " + expectedLoss);
        helper.succeed();
    }

    private static void bloodSiphonIsPercentOfDamage(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getAttribute(NVAttributes.BLOOD_SIPHON).setBaseValue(40.0);
        Anima anima = AnimaHelper.getAnima(player.getUUID());
        int before = anima.getCurrentEV();
        Pig pig = helper.spawn(EntityType.PIG, 2, 2, 2);
        pig.hurt(player.damageSources().playerAttack(player), 5.0F);
        int gained = anima.getCurrentEV() - before;
        int expected = (int) (5.0 * 0.40 * NeoVitae.SERVER_CONFIG.BLOOD_SIPHON_MOB_MULTIPLIER.get());
        pig.discard();
        helper.assertTrue(gained == expected, "Blood Siphon 40 on 5 damage should give " + expected + " EV, gave " + gained);
        helper.succeed();
    }

    private static void animaSeveranceCannotBeCured(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.addEffect(new MobEffectInstance(NVMobEffects.ANIMA_SEVERANCE, AnimaSeveranceEffect.DURATION_TICKS));
        player.removeAllEffects();
        helper.assertTrue(player.hasEffect(NVMobEffects.ANIMA_SEVERANCE), "Milk-style clearing should not remove Anima Severance");
        helper.assertTrue(AnimaSeveranceEffect.DURATION_TICKS == 200, "Anima Severance should last 10 seconds");
        helper.succeed();
    }
}
