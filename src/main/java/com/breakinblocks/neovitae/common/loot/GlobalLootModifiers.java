package com.breakinblocks.neovitae.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.anointment.AnointmentRegistrar;
import com.breakinblocks.neovitae.common.datacomponent.AnointmentHolder;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.tag.BMTags;

import javax.annotation.Nonnull;

/**
 * Global loot modifiers for anointment effects (smelting, voiding).
 * These modifiers intercept block loot drops and modify them based on tool anointments.
 * <p>
 * Note: Silk Touch and Fortune anointments are handled via ItemStackMixin which makes
 * them behave like actual enchantments. This ensures proper compatibility with all blocks
 * and preserves block entity inventories (shulker boxes, etc.) when using Silk Touch.
 */
public class GlobalLootModifiers {

    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLM =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, NeoVitae.MODID);

    // SilkTouchModifier and FortuneModifier removed - now handled by ItemStackMixin
    // which makes anointments behave like actual enchantments

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<SmeltingModifier>> SMELTING =
            GLM.register("smelting", () -> SmeltingModifier.CODEC);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<VoidingModifier>> VOIDING =
            GLM.register("voiding", () -> VoidingModifier.CODEC);

    public static void register(IEventBus modEventBus) {
        GLM.register(modEventBus);
    }

    /**
     * Checks if the tool has a smelting anointment.
     */
    private static boolean hasSmeltingAnointment(ItemStack tool) {
        if (tool.isEmpty()) return false;

        AnointmentHolder holder = tool.get(BMDataComponents.ANOINTMENT_HOLDER.get());
        return holder != null && holder.getAnointmentLevel(AnointmentRegistrar.SMELTING) > 0;
    }

    /**
     * Checks if the tool has a voiding anointment.
     */
    private static boolean hasVoidingAnointment(ItemStack tool) {
        if (tool.isEmpty()) return false;

        AnointmentHolder holder = tool.get(BMDataComponents.ANOINTMENT_HOLDER.get());
        return holder != null && holder.getAnointmentLevel(AnointmentRegistrar.VOIDING) > 0;
    }

    /**
     * Creates a copy of an ItemStack with the specified count.
     */
    private static ItemStack copyStackWithSize(ItemStack stack, int size) {
        ItemStack copy = stack.copy();
        copy.setCount(size);
        return copy;
    }

    /**
     * Smelting loot modifier - auto-smelts block drops.
     */
    public static class SmeltingModifier extends LootModifier {
        public static final MapCodec<SmeltingModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
                codecStart(inst).apply(inst, SmeltingModifier::new));

        public SmeltingModifier(LootItemCondition[] conditions) {
            super(conditions);
        }

        @Nonnull
        @Override
        protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
            ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
            if (tool == null || tool.isEmpty()) return generatedLoot;

            // Skip explosive charges
            if (tool.is(BMTags.Items.CHARGES)) return generatedLoot;

            // Check for smelting anointment
            if (!hasSmeltingAnointment(tool)) return generatedLoot;

            // Smelt each item in the loot
            ObjectArrayList<ItemStack> smeltedLoot = new ObjectArrayList<>();
            for (ItemStack stack : generatedLoot) {
                smeltedLoot.add(smelt(stack, context));
            }
            return smeltedLoot;
        }

        private ItemStack smelt(ItemStack stack, LootContext context) {
            var recipeOptional = context.getLevel().getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), context.getLevel());

            if (recipeOptional.isEmpty()) {
                return stack;
            }

            ItemStack result = recipeOptional.get().value().getResultItem(context.getLevel().registryAccess());
            if (result.isEmpty()) {
                return stack;
            }

            return copyStackWithSize(result, stack.getCount() * result.getCount());
        }

        @Override
        public MapCodec<? extends IGlobalLootModifier> codec() {
            return CODEC;
        }
    }

    /**
     * Voiding loot modifier - destroys drops from mundane blocks.
     */
    public static class VoidingModifier extends LootModifier {
        public static final MapCodec<VoidingModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
                codecStart(inst).apply(inst, VoidingModifier::new));

        public VoidingModifier(LootItemCondition[] conditions) {
            super(conditions);
        }

        @Nonnull
        @Override
        protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
            ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);
            if (tool == null || tool.isEmpty()) return generatedLoot;

            // Skip explosive charges
            if (tool.is(BMTags.Items.CHARGES)) return generatedLoot;

            // Check for voiding anointment
            if (!hasVoidingAnointment(tool)) return generatedLoot;

            BlockState blockState = context.getParamOrNull(LootContextParams.BLOCK_STATE);
            if (blockState == null) return generatedLoot;

            // Only void mundane blocks
            if (!blockState.is(BMTags.Blocks.MUNDANE_BLOCK)) return generatedLoot;

            // Return empty loot
            return new ObjectArrayList<>();
        }

        @Override
        public MapCodec<? extends IGlobalLootModifier> codec() {
            return CODEC;
        }
    }
}
