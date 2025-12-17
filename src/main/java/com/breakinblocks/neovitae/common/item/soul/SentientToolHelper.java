package com.breakinblocks.neovitae.common.item.soul;

import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.will.PlayerDemonWillHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared utility class for all sentient tools.
 * Contains common constants and logic used across SentientSwordItem, SentientAxeItem,
 * SentientPickaxeItem, SentientShovelItem, and SentientScytheItem.
 *
 * <p>This eliminates code duplication by centralizing:
 * <ul>
 *   <li>Soul bracket thresholds for power levels</li>
 *   <li>Effect duration and intensity arrays</li>
 *   <li>Soul drain and drop calculations</li>
 *   <li>On-hit effect application logic</li>
 *   <li>Demon will drop generation</li>
 *   <li>Data component access methods</li>
 * </ul>
 */
public final class SentientToolHelper {

    private SentientToolHelper() {} // Utility class - no instantiation

    // ==================== SHARED CONSTANTS ====================

    /** Soul thresholds that determine power level (0-6). Each threshold unlocks stronger effects. */
    public static final int[] SOUL_BRACKET = {16, 60, 200, 400, 1000, 2000, 4000};

    /** Will drain per swing at each power level. */
    public static final double[] SOUL_DRAIN_PER_SWING = {0.05, 0.1, 0.2, 0.4, 0.75, 1, 1.25};

    /** Variable soul drop amount multiplier at each power level. */
    public static final double[] SOUL_DROP = {2, 4, 7, 10, 13, 15, 18};

    /** Base soul drop amount at each power level. */
    public static final double[] STATIC_DROP = {1, 1, 2, 3, 3, 4, 4};

    /** Absorption effect duration in ticks at each power level (for Steadfast will). */
    public static final int[] ABSORPTION_TIME = {200, 300, 400, 500, 600, 700, 800};

    /** Maximum absorption hearts that can be accumulated. */
    public static final double MAX_ABSORPTION_HEARTS = 10;

    /** Wither effect duration in ticks at each power level (for Corrosive will). */
    public static final int[] POISON_TIME = {25, 50, 60, 80, 100, 120, 150};

    /** Wither effect amplifier at each power level (for Corrosive will). */
    public static final int[] POISON_LEVEL = {0, 0, 0, 1, 1, 1, 1};

    /** Dig speed bonus at each power level (for mining tools). */
    public static final double[] DEFAULT_DIG_SPEED_ADDED = {1, 1.5, 2, 3, 4, 5, 6};

    // ==================== LEVEL CALCULATION ====================

    /**
     * Calculates the power level (0-6) based on available soul amount.
     *
     * @param soulsRemaining the amount of demon will available
     * @return power level from 0-6, or -1 if below minimum threshold
     */
    public static int getLevel(double soulsRemaining) {
        int lvl = -1;
        for (int i = 0; i < SOUL_BRACKET.length; i++) {
            if (soulsRemaining >= SOUL_BRACKET[i]) {
                lvl = i;
            }
        }
        return lvl;
    }

    // ==================== EFFECT APPLICATION ====================

    /**
     * Applies will-type-specific effects when hitting an enemy.
     *
     * <ul>
     *   <li>CORROSIVE: Applies wither effect to the target</li>
     *   <li>STEADFAST: Grants absorption hearts when target dies</li>
     * </ul>
     *
     * @param type the current will type
     * @param willBracket the power level (0-6)
     * @param target the entity being hit
     * @param attacker the attacking entity
     */
    public static void applyEffectToEntity(EnumWillType type, int willBracket, LivingEntity target, LivingEntity attacker) {
        if (willBracket < 0 || willBracket >= POISON_TIME.length) return;

        switch (type) {
            case CORROSIVE -> target.addEffect(
                new MobEffectInstance(MobEffects.WITHER, POISON_TIME[willBracket], POISON_LEVEL[willBracket]));
            case STEADFAST -> {
                if (!target.isAlive()) {
                    float absorption = attacker.getAbsorptionAmount();
                    attacker.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, ABSORPTION_TIME[willBracket], 127, false, false));
                    attacker.setAbsorptionAmount((float) Math.min(absorption + target.getMaxHealth() * 0.05f, MAX_ABSORPTION_HEARTS));
                }
            }
            default -> {}
        }
    }

    // ==================== SOUL DROP GENERATION ====================

    /**
     * Gets the appropriate monster soul item for the given will type.
     *
     * @param type the will type
     * @return the MonsterSoulItem for that type
     */
    public static MonsterSoulItem getSoulItemForType(EnumWillType type) {
        return switch (type) {
            case CORROSIVE -> BMItems.MONSTER_SOUL_CORROSIVE.get();
            case DESTRUCTIVE -> BMItems.MONSTER_SOUL_DESTRUCTIVE.get();
            case VENGEFUL -> BMItems.MONSTER_SOUL_VENGEFUL.get();
            case STEADFAST -> BMItems.MONSTER_SOUL_STEADFAST.get();
            default -> BMItems.MONSTER_SOUL_RAW.get();
        };
    }

    /**
     * Generates random demon will drops when an entity is killed with a sentient tool.
     *
     * @param killedEntity the entity that was killed
     * @param attackingEntity the entity that made the kill
     * @param stack the sentient tool item stack
     * @param looting the looting enchantment level
     * @return list of monster soul item stacks to drop
     */
    public static List<ItemStack> getRandomDemonWillDrop(LivingEntity killedEntity, LivingEntity attackingEntity,
            ItemStack stack, int looting) {
        List<ItemStack> soulList = new ArrayList<>();

        if (killedEntity.level().getDifficulty() == Difficulty.PEACEFUL) {
            return soulList;
        }

        if (!(killedEntity instanceof Enemy)) {
            return soulList;
        }

        double willModifier = killedEntity instanceof Slime ? 0.67 : 1;
        EnumWillType type = getCurrentType(stack);
        MonsterSoulItem soulItem = getSoulItemForType(type);

        double soulDropAmount = getSoulDrop(stack);
        double staticDropAmount = getStaticDrop(stack);

        for (int i = 0; i <= looting; i++) {
            if (i == 0 || attackingEntity.level().random.nextDouble() < 0.4) {
                double soulAmount = willModifier * (soulDropAmount * attackingEntity.level().random.nextDouble()
                    + staticDropAmount) * killedEntity.getMaxHealth() / 20d;
                soulList.add(soulItem.createWill(soulAmount));
            }
        }

        return soulList;
    }

    // ==================== WILL DRAIN HANDLING ====================

    /**
     * Handles will drain when attacking with a sentient tool.
     *
     * @param stack the sentient tool
     * @param player the attacking player
     * @return true if the attack should be cancelled (not enough will), false otherwise
     */
    public static boolean handleWillDrain(ItemStack stack, Player player) {
        double drain = getDrainAmount(stack);
        if (drain > 0) {
            EnumWillType type = getCurrentType(stack);
            double soulsRemaining = PlayerDemonWillHandler.getTotalDemonWill(type, player);

            if (drain > soulsRemaining) {
                return true; // Cancel attack - not enough will
            } else {
                PlayerDemonWillHandler.consumeDemonWill(type, player, drain);
            }
        }
        return false; // Continue with attack
    }

    // ==================== DATA COMPONENT ACCESSORS ====================

    public static EnumWillType getCurrentType(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT);
    }

    public static void setCurrentType(ItemStack stack, EnumWillType type) {
        stack.set(BMDataComponents.DEMON_WILL_TYPE, type);
    }

    public static double getDrainAmount(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.SENTIENT_SWORD_DRAIN, 0.0);
    }

    public static void setDrainAmount(ItemStack stack, double drain) {
        stack.set(BMDataComponents.SENTIENT_SWORD_DRAIN, drain);
    }

    public static double getDamageBonus(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.SENTIENT_SWORD_DAMAGE, 0.0);
    }

    public static void setDamageBonus(ItemStack stack, double damage) {
        stack.set(BMDataComponents.SENTIENT_SWORD_DAMAGE, damage);
    }

    public static double getStaticDrop(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.SENTIENT_SWORD_STATIC_DROP, 1.0);
    }

    public static void setStaticDrop(ItemStack stack, double drop) {
        stack.set(BMDataComponents.SENTIENT_SWORD_STATIC_DROP, drop);
    }

    public static double getSoulDrop(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.SENTIENT_SWORD_DROP, 0.0);
    }

    public static void setSoulDrop(ItemStack stack, double drop) {
        stack.set(BMDataComponents.SENTIENT_SWORD_DROP, drop);
    }

    public static double getDigSpeedBonus(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.SENTIENT_TOOL_SPEED, 0.0);
    }

    public static void setDigSpeedBonus(ItemStack stack, double speed) {
        stack.set(BMDataComponents.SENTIENT_TOOL_SPEED, speed);
    }
}
