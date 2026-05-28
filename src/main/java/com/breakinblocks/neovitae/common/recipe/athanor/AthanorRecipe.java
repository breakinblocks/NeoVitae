package com.breakinblocks.neovitae.common.recipe.athanor;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.spiritus.WorldSpiritusHandler;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AthanorRecipe implements Recipe<AthanorRecipeInput> {

    public static final String RECIPE_TYPE_NAME = "athanor";
    public static final int MAX_INPUTS = 6;

    public static final int SPIRITUS_BOOST_MIN = 5;
    public static final int SPIRITUS_BOOST_MAX = 100;
    public static final double SPIRITUS_BOOST_MIN_CHANCE = 0.33;
    public static final double SPIRITUS_BOOST_MAX_CHANCE = 1.0;
    public static final double SPIRITUS_BOOST_CONSUME_CHANCE = 0.025;

    private static final StreamCodec<RegistryFriendlyByteBuf, Pair<ItemStack, Double>> CHANCE_PAIR_STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, Pair::getFirst,
            ByteBufCodecs.DOUBLE, Pair::getSecond,
            Pair::new
    );

    private static final Codec<Map<SpiritusType, Double>> SPIRITUS_COST_CODEC =
            Codec.unboundedMap(SpiritusType.CODEC, Codec.DOUBLE);

    public static final MapCodec<AthanorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("tool").forGetter(AthanorRecipe::getTool),
            Ingredient.CODEC.listOf().fieldOf("inputs").forGetter(AthanorRecipe::getInputs),
            ItemStack.CODEC.listOf().fieldOf("guaranteed_outputs").forGetter(AthanorRecipe::getGuaranteedOutput),
            Codec.pair(ItemStack.CODEC.fieldOf("item").codec(), Codec.DOUBLE.fieldOf("chance").codec()).listOf().fieldOf("chance_outputs").forGetter(AthanorRecipe::getChanceOutput),
            SizedFluidIngredient.NESTED_CODEC.optionalFieldOf("input_fluid").forGetter(AthanorRecipe::getInputFluid),
            FluidStack.CODEC.optionalFieldOf("output_fluid").forGetter(AthanorRecipe::getOutputFluid),
            SPIRITUS_COST_CODEC.optionalFieldOf("spiritus_costs", Map.of()).forGetter(AthanorRecipe::getSpiritusCosts),
            Codec.BOOL.optionalFieldOf("spiritus_boost", false).forGetter(AthanorRecipe::isSpiritusBoosted)
    ).apply(inst, AthanorRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AthanorRecipe> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public AthanorRecipe decode(RegistryFriendlyByteBuf buf) {
            Ingredient tool = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            int inputCount = buf.readVarInt();
            List<Ingredient> inputs = new ArrayList<>(inputCount);
            for (int i = 0; i < inputCount; i++) {
                inputs.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
            }
            List<ItemStack> guaranteed = ItemStack.LIST_STREAM_CODEC.decode(buf);
            List<Pair<ItemStack, Double>> chanced = CHANCE_PAIR_STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
            Optional<SizedFluidIngredient> inFluid = SizedFluidIngredient.STREAM_CODEC.apply(ByteBufCodecs::optional).decode(buf);
            Optional<FluidStack> outFluid = FluidStack.STREAM_CODEC.apply(ByteBufCodecs::optional).decode(buf);
            int costSize = buf.readVarInt();
            Map<SpiritusType, Double> costs = new EnumMap<>(SpiritusType.class);
            for (int i = 0; i < costSize; i++) {
                costs.put(SpiritusType.STREAM_CODEC.decode(buf), buf.readDouble());
            }
            boolean boost = buf.readBoolean();
            return new AthanorRecipe(tool, inputs, guaranteed, chanced, inFluid, outFluid, costs, boost);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, AthanorRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.tool);
            buf.writeVarInt(recipe.inputs.size());
            for (Ingredient input : recipe.inputs) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, input);
            }
            ItemStack.LIST_STREAM_CODEC.encode(buf, recipe.guaranteedOutput);
            CHANCE_PAIR_STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.chanceOutput);
            SizedFluidIngredient.STREAM_CODEC.apply(ByteBufCodecs::optional).encode(buf, recipe.inputFluid);
            FluidStack.STREAM_CODEC.apply(ByteBufCodecs::optional).encode(buf, recipe.outputFluid);
            buf.writeVarInt(recipe.spiritusCosts.size());
            recipe.spiritusCosts.forEach((type, amount) -> {
                SpiritusType.STREAM_CODEC.encode(buf, type);
                buf.writeDouble(amount);
            });
            buf.writeBoolean(recipe.spiritusBoost);
        }
    };

    private final Ingredient tool;
    private final List<Ingredient> inputs;
    private final List<ItemStack> guaranteedOutput;
    private final List<Pair<ItemStack, Double>> chanceOutput;
    private final Optional<SizedFluidIngredient> inputFluid;
    private final Optional<FluidStack> outputFluid;
    private final Map<SpiritusType, Double> spiritusCosts;
    private final boolean spiritusBoost;
    private final List<Pair<ItemStack, Double>> allListed;

    public AthanorRecipe(Ingredient tool, List<Ingredient> inputs, List<ItemStack> guaranteedOutput, List<Pair<ItemStack, Double>> chanceOutput, Optional<SizedFluidIngredient> inputFluid, Optional<FluidStack> outputStack, Map<SpiritusType, Double> spiritusCosts, boolean spiritusBoost) {
        this.tool = tool;
        this.inputs = List.copyOf(inputs);
        this.guaranteedOutput = guaranteedOutput;
        this.chanceOutput = chanceOutput;
        this.inputFluid = inputFluid;
        this.outputFluid = outputStack;
        this.spiritusCosts = Map.copyOf(spiritusCosts);
        this.spiritusBoost = spiritusBoost;

        List<Pair<ItemStack, Double>> outputs = new ArrayList<>();
        guaranteedOutput.forEach(stack -> outputs.add(Pair.of(stack, 1D)));
        outputs.addAll(chanceOutput);
        if (spiritusBoost && !guaranteedOutput.isEmpty()) {
            ItemStack singleBonus = guaranteedOutput.get(0).copyWithCount(1);
            outputs.add(Pair.of(singleBonus, SPIRITUS_BOOST_MAX_CHANCE));
        }
        allListed = List.copyOf(outputs);
    }

    public AthanorRecipe(Ingredient tool, List<Ingredient> inputs, List<ItemStack> guaranteedOutput, List<Pair<ItemStack, Double>> chanceOutput, Optional<SizedFluidIngredient> inputFluid, Optional<FluidStack> outputStack, Map<SpiritusType, Double> spiritusCosts) {
        this(tool, inputs, guaranteedOutput, chanceOutput, inputFluid, outputStack, spiritusCosts, false);
    }

    public Ingredient getTool() {
        return tool;
    }

    public List<Ingredient> getInputs() {
        return inputs;
    }

    public List<ItemStack> getGuaranteedOutput() {
        return guaranteedOutput;
    }

    public List<Pair<ItemStack, Double>> getChanceOutput() {
        return chanceOutput;
    }

    public Optional<SizedFluidIngredient> getInputFluid() {
        return inputFluid;
    }

    public Optional<FluidStack> getOutputFluid() {
        return outputFluid;
    }

    public Map<SpiritusType, Double> getSpiritusCosts() {
        return spiritusCosts;
    }

    public boolean hasSpiritusCosts() {
        return !spiritusCosts.isEmpty();
    }

    public boolean isSpiritusBoosted() {
        return spiritusBoost;
    }

    /**
     * Computes the spiritus boost chance for the given chunk raw spiritus value.
     * Returns 0 when the boost is inactive or below threshold.
     */
    public static double spiritusBoostChance(double chunkRawSpiritus) {
        if (chunkRawSpiritus < SPIRITUS_BOOST_MIN) return 0;
        double clamped = Math.min(chunkRawSpiritus, SPIRITUS_BOOST_MAX);
        double t = (clamped - SPIRITUS_BOOST_MIN) / (double) (SPIRITUS_BOOST_MAX - SPIRITUS_BOOST_MIN);
        return Mth.lerp(t, SPIRITUS_BOOST_MIN_CHANCE, SPIRITUS_BOOST_MAX_CHANCE);
    }

    @Override
    public boolean matches(AthanorRecipeInput recipeInput, Level level) {
        if (!tool.test(recipeInput.getItem(0))) {
            return false;
        }
        if (inputFluid.isPresent() && !inputFluid.get().test(recipeInput.getFluid())) {
            return false;
        }
        return matchInputs(recipeInput);
    }

    private boolean matchInputs(AthanorRecipeInput recipeInput) {
        boolean[] used = new boolean[MAX_INPUTS];
        for (Ingredient ingredient : inputs) {
            boolean found = false;
            for (int slot = 0; slot < MAX_INPUTS; slot++) {
                if (used[slot]) continue;
                if (ingredient.test(recipeInput.getItem(1 + slot))) {
                    used[slot] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    /** Per-craft result bundle. Recipe instances are singletons, so results must not be stored on them. */
    public record AthanorResult(List<ItemStack> items, FluidStack fluid) {}

    @Override
    public ItemStack assemble(AthanorRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    /**
     * Rolls chance outputs and returns a fresh result. Safe to call concurrently for different inputs
     * since no state is stored on the recipe.
     */
    public AthanorResult assembleOutputs(AthanorRecipeInput input) {
        return assembleOutputs(input, null, null);
    }

    /**
     * Rolls chance outputs (and any spiritus-boost bonus) and returns a fresh result.
     * When level + pos are provided and the recipe declares a spiritus boost, the chunk's
     * raw spiritus modulates a bonus +1 of the first guaranteed output, and a small probability
     * drains 1 raw spiritus from the chunk on the way out.
     */
    public AthanorResult assembleOutputs(AthanorRecipeInput input, Level level, BlockPos pos) {
        List<ItemStack> outputs = new ArrayList<>(guaranteedOutput.size() + chanceOutput.size() + 1);
        for (ItemStack guaranteed : guaranteedOutput) {
            outputs.add(guaranteed.copy());
        }
        ItemStack toolStack = input.getItem(0);
        double toolBonusChance = toolStack.getOrDefault(NVDataComponents.ARC_CHANCE, 1D);
        for (Pair<ItemStack, Double> entry : chanceOutput) {
            int produced = rollBonusCount(entry.getSecond() * toolBonusChance);
            for (int i = 0; i < produced; i++) {
                outputs.add(entry.getFirst().copy());
            }
        }
        if (spiritusBoost && level != null && pos != null && !guaranteedOutput.isEmpty()) {
            double raw = WorldSpiritusHandler.getCurrentSpiritus(level, pos, SpiritusType.RAW);
            int produced = rollBonusCount(spiritusBoostChance(raw) * toolBonusChance);
            for (int i = 0; i < produced; i++) {
                outputs.add(guaranteedOutput.get(0).copyWithCount(1));
                if (Math.random() < SPIRITUS_BOOST_CONSUME_CHANCE) {
                    WorldSpiritusHandler.drainSpiritusFromChunk(level, pos, SpiritusType.RAW, 1.0);
                }
            }
        }
        return new AthanorResult(outputs, outputFluid.orElse(FluidStack.EMPTY).copy());
    }

    private static int rollBonusCount(double effectiveChance) {
        if (effectiveChance <= 0) return 0;
        int guaranteed = (int) effectiveChance;
        double remainder = effectiveChance - guaranteed;
        if (remainder > 0 && Math.random() < remainder) {
            guaranteed++;
        }
        return guaranteed;
    }

    public List<Pair<ItemStack, Double>> getAllListedOutputs() {
        return allListed;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NVRecipes.ATHANOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return NVRecipes.ATHANOR_TYPE.get();
    }
}
