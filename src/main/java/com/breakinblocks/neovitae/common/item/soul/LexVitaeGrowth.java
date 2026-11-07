package com.breakinblocks.neovitae.common.item.soul;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;

public final class LexVitaeGrowth {

    public static final double BASE_LEVEL_COST = 50.0;
    public static final double LEVEL_COST_GROWTH = 1.2;
    public static final double DIRECT_BLOCK_EXPERIENCE = 1.0;
    public static final double AREA_BLOCK_EXPERIENCE = 0.2;
    public static final double ORE_MULTIPLIER = 3.0;
    public static final double KILL_HEALTH_DIVISOR = 5.0;
    public static final double EFFICIENCY_PER_LEVEL = 1.0;
    public static final int LEVELS_PER_FORTUNE = 15;
    public static final double WEAR_HALF_LEVEL = 10.0;

    private LexVitaeGrowth() {}

    public static double getExperience(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.LEX_EXPERIENCE, 0.0);
    }

    public static double experienceForLevel(int level) {
        return BASE_LEVEL_COST * (Math.pow(LEVEL_COST_GROWTH, level) - 1.0) / (LEVEL_COST_GROWTH - 1.0);
    }

    public static int levelFor(double experience) {
        int level = (int) Math.floor(Math.log(experience * (LEVEL_COST_GROWTH - 1.0) / BASE_LEVEL_COST + 1.0)
                / Math.log(LEVEL_COST_GROWTH));
        while (level > 0 && experienceForLevel(level) > experience) level--;
        while (experienceForLevel(level + 1) <= experience) level++;
        return Math.max(0, level);
    }

    public static int getLevel(ItemStack stack) {
        return levelFor(getExperience(stack));
    }

    public static double progressToNext(ItemStack stack) {
        double experience = getExperience(stack);
        int level = levelFor(experience);
        double start = experienceForLevel(level);
        return (experience - start) / (experienceForLevel(level + 1) - start);
    }

    public static double efficiencyBonus(int level) {
        return level * EFFICIENCY_PER_LEVEL;
    }

    public static int fortuneBonus(int level) {
        return level / LEVELS_PER_FORTUNE;
    }

    public static double wearResistance(int level) {
        return level / (level + WEAR_HALF_LEVEL);
    }

    public static int resistWear(int amount, int level, RandomSource random) {
        double resistance = wearResistance(level);
        if (resistance <= 0) return amount;
        int kept = 0;
        for (int i = 0; i < amount; i++) {
            if (random.nextDouble() >= resistance) kept++;
        }
        return kept;
    }

    public static double blockExperience(BlockState state, float hardness, boolean area) {
        if (hardness <= 0) return 0;
        double experience = area ? AREA_BLOCK_EXPERIENCE : DIRECT_BLOCK_EXPERIENCE;
        return state.is(Tags.Blocks.ORES) ? experience * ORE_MULTIPLIER : experience;
    }

    public static double killExperience(LivingEntity target) {
        return target.getMaxHealth() / KILL_HEALTH_DIVISOR;
    }

    public static void addExperience(ItemStack stack, Player player, double amount) {
        if (amount <= 0 || player.level().isClientSide()) return;
        if (HellforgedHelper.isHellforged(stack)) {
            amount *= NeoVitae.SERVER_CONFIG.HELLFORGED_LEX_GROWTH_MULTIPLIER.get();
        }
        double before = getExperience(stack);
        int oldLevel = levelFor(before);
        stack.set(NVDataComponents.LEX_EXPERIENCE, before + amount);
        int newLevel = levelFor(before + amount);
        if (newLevel > oldLevel && player instanceof ServerPlayer sp) {
            sp.sendOverlayMessage(Component.translatable("message.neovitae.lex_vitae.level_up", newLevel));
            sp.level().playSound(null, sp.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.6F, 0.6F);
        }
    }
}
