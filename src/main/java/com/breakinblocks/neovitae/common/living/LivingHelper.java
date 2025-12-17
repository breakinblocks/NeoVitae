package com.breakinblocks.neovitae.common.living;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.mutable.MutableFloat;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.LivingStats;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeLimits;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeTome;
import com.breakinblocks.neovitae.common.event.LivingArmourEvent;
import com.breakinblocks.neovitae.common.registry.BMRegistries;
import com.breakinblocks.neovitae.common.tag.BMTags;
import com.breakinblocks.neovitae.util.ChatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class LivingHelper {
    public record UpgradeInstance(Holder<LivingUpgrade> upgrade, int level) {}

    public static boolean hasFullSet(Player player) {
        ItemStack chestStack = getChest(player);
        TagKey<Item> set = chestStack.get(BMDataComponents.REQUIRED_SET);
        if (set == null) {
            return false;
        }
        if (chestStack.getDamageValue() +1 >= chestStack.getMaxDamage()) {
            return false;
        }

        for (ItemStack stack : player.getArmorSlots()) {
            if (!stack.is(set)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isNeverValid(Player player) {
        return isNeverValid(getChest(player));
    }

    public static boolean isNeverValid(ItemStack plate) {
        return !plate.has(BMDataComponents.REQUIRED_SET);
    }

    public static ItemStack getChest(Player player) {
        return player.getItemBySlot(EquipmentSlot.CHEST);
    }

    public static boolean has(ItemStack stack, DataComponentType<?> type) {
		for (UpgradeInstance instance : getUpgrades(stack)) {
			if (instance.upgrade().value().effects().has(type)) {
				return true;
			}
		}
		return false;
    }

    public static boolean has(Player player, DataComponentType<?> type) {
        return has(getChest(player), type);
    }

    public static int getLevel(ItemStack stack, DataComponentType<?> type) {
		int maxLevel = 0;
		for (UpgradeInstance instance : getUpgrades(stack)) {
			if (instance.upgrade().value().effects().has(type)) {
				maxLevel = Math.max(maxLevel, instance.level());
			}
		}
		return maxLevel;
    }

    public static int getLevel(Player player, DataComponentType<?> type) {
        return getLevel(getChest(player), type);
    }

	public static List<UpgradeInstance> getUpgrades(Player player) {
		return getUpgrades(getChest(player));
	}

	public static List<UpgradeInstance> getUpgrades(ItemStack stack) {
		List<UpgradeInstance> instances = new ArrayList<>();
		Object2FloatOpenHashMap<Holder<LivingUpgrade>> upgrades = stack.getOrDefault(BMDataComponents.UPGRADES, LivingStats.EMPTY).upgrades();

		for (Object2FloatMap.Entry<Holder<LivingUpgrade>> entry : upgrades.object2FloatEntrySet()) {
			int level = getLevelFromXp(entry.getKey(), entry.getFloatValue());
			if (level < 1) {
				continue;
			}
			instances.add(new UpgradeInstance(entry.getKey(), level));
		}
		return instances;
	}

    public static void runIterationOnPlayer(Player player, BiConsumer<Holder<LivingUpgrade>, Integer> visitor) {
        runIterationOnItem(getChest(player), visitor);
    }

    public static final Object2FloatOpenHashMap<Holder<LivingUpgrade>> EMPTY_UPGRADE_MAP = new Object2FloatOpenHashMap<>();
    public static void runIterationOnItem(ItemStack stack, BiConsumer<Holder<LivingUpgrade>, Integer> visitor) {
        Object2FloatOpenHashMap<Holder<LivingUpgrade>> upgrades = stack.getOrDefault(BMDataComponents.UPGRADES, LivingStats.EMPTY).upgrades();

        for (Object2FloatMap.Entry<Holder<LivingUpgrade>> entry : upgrades.object2FloatEntrySet()) {
            int level = getLevelFromXp(entry.getKey(), entry.getFloatValue());
            if (level < 1) {
                continue;
            }
            visitor.accept(entry.getKey(), level);
        }
    }

    public static int getLevelFromXp(Holder<LivingUpgrade> upgrade, float exp) {
        Map.Entry<Integer, Integer> level = upgrade.value().levels().expToLevel().floorEntry((int) exp);
        return level == null ? 0 : level.getValue();
    }

    public static int getLevelFromXp(ItemStack tomeStack) {
        if (tomeStack.isEmpty()) {
            return 0;
        }
        UpgradeTome tome = tomeStack.get(BMDataComponents.UPGRADE_TOME_DATA);
        if (tome == null) {
            return 0;
        }

        return getLevelFromXp(tome.upgrade(), tome.exp());
    }

    public static int nextLevelExp(Holder<LivingUpgrade> upgrade, float exp) {
        Map.Entry<Integer, Integer> level = upgrade.value().levels().expToLevel().ceilingEntry((int) exp + 1); // otherwise it'll get the same level again
        return level == null ? 0 : level.getKey();
    }

    public static float modifyKnockback(Player player, LivingEntity victim, DamageSource damageSource, float knockback) {
		float finalValue = knockback;
		for (UpgradeInstance instance : getUpgrades(player)) {
			finalValue = instance.upgrade().value().modifyKnockback(instance.level(), victim, damageSource, finalValue);
		}
		return finalValue;
    }

    public static int modifyExperience(Player player, int startingValue) {
		float finalValue = startingValue;
		for (UpgradeInstance instance : getUpgrades(player)) {
			finalValue = instance.upgrade().value().modifyExperience(instance.level(), player, finalValue);
		}

		float mod = finalValue % 1;
		int toAdd = player.level().random.nextFloat() < mod ? 1 : 0;
		return (int) Math.floor(finalValue) + toAdd;
    }

    public static float modifyHealing(Player player, float amount) {
		float finalValue = amount;
		for (UpgradeInstance instance : getUpgrades(player)) {
			finalValue = instance.upgrade().value().modifyHealing(instance.level(), player, finalValue);
		}
		return finalValue;
    }

    public static float modifyDamageDealt(Player playerCauser, LivingEntity victim, DamageSource source, float originalDamage) {
		float finalValue = originalDamage;
		for (UpgradeInstance instance : getUpgrades(playerCauser)) {
			finalValue = instance.upgrade().value().modifyDamageDealt(instance.level(), victim, source, finalValue);
		}
		return finalValue;
    }

    public static float modifyDamageTaken(Player playerVictim, DamageSource source, float newDamage) {
		float finalValue = newDamage;
		for (UpgradeInstance instance : getUpgrades(playerVictim)) {
			finalValue = instance.upgrade().value().modifyDamageTaken(instance.level(), playerVictim, source, finalValue);
		}
		return finalValue;
    }

    public static void reactToDamageDealt(Player playerCauser, LivingEntity victim, DamageSource source, float newDamage) {
        runIterationOnPlayer(playerCauser, (holder, level) -> holder.value().reactToDamageDealt(level, victim, source, newDamage));
    }

    public static void reactToDamageTaken(Player playerVictim, DamageSource source, float newDamage) {
        runIterationOnPlayer(playerVictim, (holder, level) -> holder.value().reactToDamageTaken(level, playerVictim, source, newDamage));
    }

    public static void runBlockBroken(Player player, BlockState state) {
        runIterationOnPlayer(player, (holder, level) -> holder.value().blockBroken(level, player, state));
    }

    public static void runTick(Player player) {
        runIterationOnPlayer(player, (holder, level) -> holder.value().tick(level, player));
    }

    public static void runProjectile(Player player, Projectile projectile) {
        runIterationOnPlayer(player, (holder, level) -> holder.value().modifyProjectile(level, player, projectile));
    }

    public static void getAttributes(ItemStack chestStack, ItemAttributeModifiers.Builder builder) {
        runIterationOnItem(chestStack, (holder, level) -> holder.value().collectAttributes(level, builder::add));
    }

    private record ExpContext(
            Player wearer,
            Holder<LivingUpgrade> upgrade,
            float amount,
            boolean fromTome,
            ItemStack chest,
            Object2FloatOpenHashMap<Holder<LivingUpgrade>> upgrades,
            UpgradeLimits limits,
            int maxPoints,
            int currentPoints
    ) {}

    private record ExpResult(
            float newExp,
            float expConsumed,
            int newTotalPoints,
            boolean leveledUp,
            int oldLevel,
            int newLevel
    ) {
        public static final ExpResult NO_CHANGE = new ExpResult(0, 0, 0, false, 0, 0);
    }

    public static float applyExpToCap(Player wearer, Holder<LivingUpgrade> upgrade, float amount, boolean fromTome) {
        float rest = amount;
        float previous;
        do {
            previous = rest;
            rest -= applyExp(wearer, upgrade, rest, fromTome);
        } while (rest != 0 && rest != previous);

        return amount - rest;
    }

    public static float applyExp(Player wearer, Holder<LivingUpgrade> upgrade, float amount) {
        return applyExp(wearer, upgrade, amount, false);
    }

    public static float applyExp(Player wearer, Holder<LivingUpgrade> upgrade, float amount, boolean fromTome) {
        ExpContext context = gatherExpContext(wearer, upgrade, amount, fromTome);
        if (context == null || context.amount <= 0) {
            return 0;
        }

        float currentExp = context.upgrades.getOrDefault(upgrade, 0f);
        ExpResult result = calculateExpChange(context, currentExp);

        if (result == ExpResult.NO_CHANGE || result.expConsumed <= 0) {
            return 0;
        }

        applyExpChange(context, result);

        return result.expConsumed;
    }

    private static ExpContext gatherExpContext(Player wearer, Holder<LivingUpgrade> upgrade, float amount, boolean fromTome) {
        ItemStack chest = getChest(wearer);
        LivingArmourEvent.ExpGain event = NeoForge.EVENT_BUS.post(new LivingArmourEvent.ExpGain(wearer, upgrade, amount, fromTome));
        float eventAmount = event.getCurrentAmount();
        if (eventAmount <= 0) {
            return null;
        }

        Object2FloatOpenHashMap<Holder<LivingUpgrade>> upgrades = chest.getOrDefault(BMDataComponents.UPGRADES, LivingStats.EMPTY).upgrades().clone();
        UpgradeLimits limits = chest.getOrDefault(BMDataComponents.LIMITS, UpgradeLimits.EMPTY);
        int maxPoints = chest.getOrDefault(BMDataComponents.CURRENT_MAX_UPGRADE_POINTS, 0);
        int currentPoints = chest.getOrDefault(BMDataComponents.CURRENT_UPGRADE_POINTS, 0);

        return new ExpContext(wearer, upgrade, eventAmount, fromTome, chest, upgrades, limits, maxPoints, currentPoints);
    }

    private static ExpResult calculateExpChange(ExpContext context, float currentExp) {
        float xpToAdd = context.amount;

        // 1. Apply upgrade XP cap
        float maxExp = context.limits.getLimit(context.upgrade);
        if (maxExp != -1) {
            xpToAdd = Math.min(maxExp - currentExp, xpToAdd);
        }

        if (xpToAdd <= 0) {
            return ExpResult.NO_CHANGE;
        }

        // 2. Check for level up
        int oldLevel = getLevelFromXp(context.upgrade, currentExp);
        int oldCost = context.upgrade.value().levels().levelToCost().getOrDefault(oldLevel, 0);
        int nextCost = context.upgrade.value().levels().levelToCost().getOrDefault(oldLevel + 1, -1);

        // Max level reached, just add XP
        if (nextCost == -1) {
            return new ExpResult(currentExp + xpToAdd, xpToAdd, context.currentPoints, false, oldLevel, oldLevel);
        }

        float nextLevelXp = nextLevelExp(context.upgrade, currentExp);
        boolean canLevelUp = (currentExp + xpToAdd) >= nextLevelXp;

        if (canLevelUp) {
            int theoreticalPoints = context.currentPoints - oldCost + nextCost;
            if (theoreticalPoints <= context.maxPoints) {
                // Level up is possible and allowed
                float consumed = nextLevelXp - currentExp;
                return new ExpResult(nextLevelXp, consumed, theoreticalPoints, true, oldLevel, oldLevel + 1);
            } else {
                // Not enough points, add XP up to the level boundary
                float maxAdd = Math.max(0, (nextLevelXp - 1) - currentExp);
                xpToAdd = Math.min(maxAdd, xpToAdd);
            }
        }

        return new ExpResult(currentExp + xpToAdd, xpToAdd, context.currentPoints, false, oldLevel, oldLevel);
    }

    private static void applyExpChange(ExpContext context, ExpResult result) {
        context.upgrades.put(context.upgrade, result.newExp);
        context.chest.set(BMDataComponents.UPGRADES, new LivingStats(context.upgrades));

        if (result.leveledUp) {
            context.chest.set(BMDataComponents.CURRENT_UPGRADE_POINTS, result.newTotalPoints);
            NeoForge.EVENT_BUS.post(new LivingArmourEvent.LevelUp(context.wearer, context.upgrade, result.oldLevel, result.newLevel));
            context.wearer.displayClientMessage(Component.translatable("chat.neovitae.living_upgrade.level_up", Component.translatable(LivingUpgrade.descriptionId(context.upgrade.getKey())), result.newLevel), true);
        }
    }
    
    public static Component getTooltip(Holder<LivingUpgrade> upgrade, float exp, boolean hasShiftDown) {
        int level = getLevelFromXp(upgrade, exp);
        int nextExp = nextLevelExp(upgrade, exp);
        Component levelComp = Component.literal(ChatUtil.toRoman(level));
        Component expComp = Component.literal("%s/%s".formatted((int) exp, nextExp));
        if (nextExp == 0) {
            expComp = Component.literal("%s/".formatted((int) exp)).append(Component.literal(Integer.toString((int) exp)).withStyle(ChatFormatting.OBFUSCATED));
        }

        ChatFormatting colour = ChatFormatting.YELLOW;
        if (upgrade.is(BMTags.Living.IS_DOWNGRADE)) {
            colour = ChatFormatting.RED;
        }

        ChatFormatting style = colour;
        if (level < 1) {
            style = ChatFormatting.ITALIC;
            levelComp = Component.literal("0").withStyle(ChatFormatting.OBFUSCATED);
        }

        MutableComponent mutable = Component.translatable(LivingUpgrade.descriptionId(upgrade.getKey())).withStyle(style, colour);

        if (hasShiftDown) {
            mutable.append(CommonComponents.SPACE).append(expComp);
        } else {
            mutable.append(CommonComponents.SPACE).append(levelComp);
        }

        return mutable;
    }

    public static Object2FloatOpenHashMap<Holder<LivingUpgrade>> fromHolderSet(HolderSet<LivingUpgrade> template) {
        return fromHolderSet(template, 1);
    }

    public static Object2FloatOpenHashMap<Holder<LivingUpgrade>> fromHolderSet(HolderSet<LivingUpgrade> template, float val) {
        Object2FloatOpenHashMap<Holder<LivingUpgrade>> ret = new Object2FloatOpenHashMap<>();
        template.forEach(holder -> ret.put(holder, val));
        return ret;
    }

    public static void setDefaultLiving(ItemStack livingPlate, HolderLookup.Provider holders) {
        HolderSet<LivingUpgrade> set = holders.lookupOrThrow(BMRegistries.Keys.LIVING_UPGRADES).get(BMTags.Living.LIVING_START).orElseThrow();
        livingPlate.set(BMDataComponents.UPGRADES, new LivingStats(fromHolderSet(set)));
        livingPlate.set(BMDataComponents.CURRENT_MAX_UPGRADE_POINTS, NeoVitae.SERVER_CONFIG.DEFAULT_UPGRADE_POINTS.get());
    }

    public static int recalcPoints(Player player) {
        ItemStack chest = getChest(player);
        Object2FloatOpenHashMap<Holder<LivingUpgrade>> upgrades = chest.getOrDefault(BMDataComponents.UPGRADES, LivingStats.EMPTY).upgrades();

        int total = 0;
        for (Map.Entry<Holder<LivingUpgrade>, Float> entry : upgrades.object2FloatEntrySet()) {
            total += entry.getKey().value().levels().levelToCost().getOrDefault(getLevelFromXp(entry.getKey(), entry.getValue()), 0);
        }

        chest.set(BMDataComponents.CURRENT_UPGRADE_POINTS, total);

        return total;
    }

    public static Pair<Integer, Float> scrapFromTome(UpgradeTome tome) {
        Map.Entry<Integer, Integer> expEntry = tome.upgrade().value().levels().expToLevel().floorEntry((int) tome.exp());
        int scrap = 0;
        float expUsed = 0;
        if (expEntry != null) {
            scrap = tome.upgrade().value().levels().levelToCost().getOrDefault(expEntry.getValue(), 0);
            expUsed = expEntry.getKey();
        }

        return Pair.of(scrap, expUsed);
    }

    public static int getExpForLevel(Holder<LivingUpgrade> upgrade, int level) {
        AtomicInteger exp = new AtomicInteger(-1);
        upgrade.value().levels().expToLevel().forEach((k, v) -> {
            if (v == level) {
                exp.set(k);
            }
        });

        return exp.get();
    }

    public static int getMaxLevel(Holder<LivingUpgrade> upgrade) {
        return upgrade.value().levels().levelToCost().size();
    }
}
