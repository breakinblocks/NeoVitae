// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.spiritus.SpiritusHelper;

import java.util.List;

/**
 * Loot function that sets the spiritus fraction (percentage of max) on spiritus gems.
 */
public class SetSpiritusFraction extends LootItemConditionalFunction {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetSpiritusFraction.class);

    public static final MapCodec<SetSpiritusFraction> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance).and(
                    NumberProviders.CODEC.fieldOf("fraction").forGetter(func -> func.fractionRange)
            ).apply(instance, SetSpiritusFraction::new)
    );

    private final NumberProvider fractionRange;

    private SetSpiritusFraction(List<LootItemCondition> conditions, NumberProvider fractionRange) {
        super(conditions);
        this.fractionRange = fractionRange;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return NVLootFunctions.SET_SPIRITUS_FRACTION.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (SpiritusHelper.isRechargeable(stack)) {
            double maxSpiritus = SpiritusHelper.resolveMaxSpiritus(stack, SpiritusType.RAW);
            float fraction = 1.0F - fractionRange.getFloat(context);
            SpiritusHelper.setSpiritus(stack, SpiritusType.RAW, maxSpiritus * fraction);
        } else {
            LOGGER.warn("Couldn't set spiritus fraction of loot item {}", stack);
        }
        return stack;
    }

    public static LootItemConditionalFunction.Builder<?> withRange(NumberProvider range) {
        return simpleBuilder(conditions -> new SetSpiritusFraction(conditions, range));
    }

    public static LootItemConditionalFunction.Builder<?> withRange(float min, float max) {
        return withRange(UniformGenerator.between(min, max));
    }
}
