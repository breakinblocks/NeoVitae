package com.breakinblocks.neovitae.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.LivingStats;
import com.breakinblocks.neovitae.common.living.LivingUpgrade;
import com.breakinblocks.neovitae.common.registry.BMRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Loot function that adds a living upgrade with random experience to items.
 */
public class SetLivingUpgrade extends LootItemConditionalFunction {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetLivingUpgrade.class);

    public static final MapCodec<SetLivingUpgrade> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance).and(instance.group(
                    LivingUpgrade.HOLDER_CODEC.listOf().fieldOf("upgrades").forGetter(func -> func.upgrades),
                    NumberProviders.CODEC.fieldOf("points").forGetter(func -> func.pointsRange)
            )).apply(instance, SetLivingUpgrade::new)
    );

    private final List<Holder<LivingUpgrade>> upgrades;
    private final NumberProvider pointsRange;

    private SetLivingUpgrade(List<LootItemCondition> conditions, List<Holder<LivingUpgrade>> upgrades, NumberProvider pointsRange) {
        super(conditions);
        this.upgrades = upgrades;
        this.pointsRange = pointsRange;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return BMLootFunctions.SET_LIVING_UPGRADE.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        // Check if item can have upgrades (living armor)
        if (stack.has(BMDataComponents.UPGRADES) || stack.has(BMDataComponents.REQUIRED_SET)) {
            if (upgrades.isEmpty()) {
                LOGGER.warn("No upgrades specified for SetLivingUpgrade loot function");
                return stack;
            }

            // Pick a random upgrade using the context's random
            Random random = new Random(context.getRandom().nextLong());
            Holder<LivingUpgrade> upgrade = upgrades.get(random.nextInt(upgrades.size()));

            float points = pointsRange.getFloat(context);

            // Get or create living stats and add experience
            LivingStats stats = stack.getOrDefault(BMDataComponents.UPGRADES, LivingStats.EMPTY);
            Object2FloatOpenHashMap<Holder<LivingUpgrade>> newUpgrades = stats.upgrades().clone();
            newUpgrades.addTo(upgrade, points);
            stack.set(BMDataComponents.UPGRADES, new LivingStats(newUpgrades));
        } else {
            LOGGER.warn("Couldn't set living upgrade on loot item {}", stack);
        }
        return stack;
    }

    /**
     * Creates a builder using pre-resolved Holders.
     */
    public static LootItemConditionalFunction.Builder<?> withHolders(NumberProvider points, List<Holder<LivingUpgrade>> holders) {
        return simpleBuilder(conditions -> new SetLivingUpgrade(conditions, holders, points));
    }

    public static LootItemConditionalFunction.Builder<?> withRange(float minPoints, float maxPoints, List<Holder<LivingUpgrade>> holders) {
        return withHolders(UniformGenerator.between(minPoints, maxPoints), holders);
    }

    /**
     * Creates a builder for datagen use by resolving ResourceKeys through the HolderLookup.Provider.
     * This allows SetLivingUpgrade to be used in datagen loot tables.
     *
     * @param registries The HolderLookup.Provider from datagen context
     * @param points The number provider for upgrade points
     * @param upgradeKeys The ResourceKeys for the upgrades to add
     * @return A builder that can be used with LootItem.apply()
     */
    @SafeVarargs
    public static LootItemConditionalFunction.Builder<?> withKeys(
            HolderLookup.Provider registries,
            NumberProvider points,
            ResourceKey<LivingUpgrade>... upgradeKeys) {
        HolderGetter<LivingUpgrade> lookup = registries.lookupOrThrow(BMRegistries.Keys.LIVING_UPGRADES);
        List<Holder<LivingUpgrade>> holders = Arrays.stream(upgradeKeys)
                .map(lookup::getOrThrow)
                .collect(Collectors.toList());
        return withHolders(points, holders);
    }

    /**
     * Creates a builder for datagen use with a uniform point range.
     *
     * @param registries The HolderLookup.Provider from datagen context
     * @param minPoints Minimum points for the upgrade
     * @param maxPoints Maximum points for the upgrade
     * @param upgradeKeys The ResourceKeys for the upgrades to add
     * @return A builder that can be used with LootItem.apply()
     */
    @SafeVarargs
    public static LootItemConditionalFunction.Builder<?> withKeysRange(
            HolderLookup.Provider registries,
            float minPoints,
            float maxPoints,
            ResourceKey<LivingUpgrade>... upgradeKeys) {
        return withKeys(registries, UniformGenerator.between(minPoints, maxPoints), upgradeKeys);
    }
}
