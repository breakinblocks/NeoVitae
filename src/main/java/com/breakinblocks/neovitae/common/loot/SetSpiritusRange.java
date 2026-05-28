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
import com.breakinblocks.neovitae.spiritus.ISpiritus;

import java.util.List;

/**
 * Loot function that sets the spiritus amount on spiritus items.
 */
public class SetSpiritusRange extends LootItemConditionalFunction {
    private static final Logger LOGGER = LoggerFactory.getLogger(SetSpiritusRange.class);

    public static final MapCodec<SetSpiritusRange> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance).and(
                    NumberProviders.CODEC.fieldOf("spiritus").forGetter(func -> func.spiritusRange)
            ).apply(instance, SetSpiritusRange::new)
    );

    private final NumberProvider spiritusRange;

    private SetSpiritusRange(List<LootItemCondition> conditions, NumberProvider spiritusRange) {
        super(conditions);
        this.spiritusRange = spiritusRange;
    }

    @Override
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return NVLootFunctions.SET_SPIRITUS_RANGE.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        if (stack.getItem() instanceof ISpiritus spiritus) {
            float amount = spiritusRange.getFloat(context);
            return spiritus.createSpiritus(amount);
        } else {
            LOGGER.warn("Couldn't set spiritus of loot item {}", stack);
        }
        return stack;
    }

    public static LootItemConditionalFunction.Builder<?> withRange(NumberProvider range) {
        return simpleBuilder(conditions -> new SetSpiritusRange(conditions, range));
    }

    public static LootItemConditionalFunction.Builder<?> withRange(float min, float max) {
        return withRange(UniformGenerator.between(min, max));
    }

    public static LootItemConditionalFunction.Builder<?> builder(NumberProvider range) {
        return withRange(range);
    }
}
