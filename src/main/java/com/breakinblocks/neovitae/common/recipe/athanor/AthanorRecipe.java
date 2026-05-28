package com.breakinblocks.neovitae.common.recipe.athanor;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;
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

    private static final StreamCodec<RegistryFriendlyByteBuf, Pair<ItemStackTemplate, Double>> CHANCE_PAIR_STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC, Pair::getFirst,
            ByteBufCodecs.DOUBLE, Pair::getSecond,
            Pair::new
    );

    private static final Codec<Map<SpiritusType, Double>> SPIRITUS_COST_CODEC =
            Codec.unboundedMap(SpiritusType.CODEC, Codec.DOUBLE);

    public static final MapCodec<AthanorRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC.fieldOf("tool").forGetter(AthanorRecipe::getTool),
            Ingredient.CODEC.listOf().fieldOf("inputs").forGetter(AthanorRecipe::getInputs),
            ItemStackTemplate.CODEC.listOf().fieldOf("guaranteed_outputs").forGetter(AthanorRecipe::getGuaranteedOutput),
            Codec.pair(ItemStackTemplate.CODEC.fieldOf("item").codec(), Codec.DOUBLE.fieldOf("chance").codec()).listOf().fieldOf("chance_outputs").forGetter(AthanorRecipe::getChanceOutput),
            SizedFluidIngredient.CODEC.optionalFieldOf("input_fluid").forGetter(AthanorRecipe::getInputFluid),
            FluidStackTemplate.CODEC.optionalFieldOf("output_fluid").forGetter(AthanorRecipe::getOutputFluidTemplate),
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
            List<ItemStackTemplate> guaranteed = ItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
            List<Pair<ItemStackTemplate, Double>> chanced = CHANCE_PAIR_STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buf);
            Optional<SizedFluidIngredient> inFluid = SizedFluidIngredient.STREAM_CODEC.apply(ByteBufCodecs::optional).decode(buf);
            Optional<FluidStackTemplate> outFluid = FluidStackTemplate.STREAM_CODEC.apply(ByteBufCodecs::optional).decode(buf);
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
            ItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.guaranteedOutput);
            CHANCE_PAIR_STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buf, recipe.chanceOutput);
            SizedFluidIngredient.STREAM_CODEC.apply(ByteBufCodecs::optional).encode(buf, recipe.inputFluid);
            FluidStackTemplate.STREAM_CODEC.apply(ByteBufCodecs::optional).encode(buf, recipe.outputFluid);
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
    private final List<ItemStackTemplate> guaranteedOutput;
    private final List<Pair<ItemStackTemplate, Double>> chanceOutput;
    private final Optional<SizedFluidIngredient> inputFluid;
    private final Optional<FluidStackTemplate> outputFluid;
    private final Map<SpiritusType, Double> spiritusCosts;
    private final boolean spiritusBoost;
    private volatile List<Pair<ItemStackTemplate, Double>> allListed;

    public AthanorRecipe(Ingredient tool, List<Ingredient> inputs, List<ItemStackTemplate> guaranteedOutput, List<Pair<ItemStackTemplate, Double>> chanceOutput, Optional<SizedFluidIngredient> inputFluid, Optional<FluidStackTemplate> outputStack, Map<SpiritusType, Double> spiritusCosts, boolean spiritusBoost) {
        this.tool = tool;
        this.inputs = List.copyOf(inputs);
        this.guaranteedOutput = guaranteedOutput;
        this.chanceOutput = chanceOutput;
        this.inputFluid = inputFluid;
        this.outputFluid = outputStack;
        this.spiritusCosts = Map.copyOf(spiritusCosts);
        this.spiritusBoost = spiritusBoost;
    }

    public AthanorRecipe(Ingredient tool, List<Ingredient> inputs, List<ItemStackTemplate> guaranteedOutput, List<Pair<ItemStackTemplate, Double>> chanceOutput, Optional<SizedFluidIngredient> inputFluid, Optional<FluidStackTemplate> outputStack, Map<SpiritusType, Double> spiritusCosts) {
        this(tool, inputs, guaranteedOutput, chanceOutput, inputFluid, outputStack, spiritusCosts, false);
    }

    public Ingredient getTool() {
        return tool;
    }

    public List<Ingredient> getInputs() {
        return inputs;
    }

    public List<ItemStackTemplate> getGuaranteedOutput() {
        return guaranteedOutput;
    }

    public List<Pair<ItemStackTemplate, Double>> getChanceOutput() {
        return chanceOutput;
    }

    public Optional<SizedFluidIngredient> getInputFluid() {
        return inputFluid;
    }

    public Optional<FluidStack> getOutputFluid() {
        return outputFluid.map(FluidStackTemplate::create);
    }

    public Optional<FluidStackTemplate> getOutputFluidTemplate() {
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

    public record AthanorResult(List<ItemStack> items, FluidStack fluid) {}

    @Override
    public ItemStack assemble(AthanorRecipeInput input) {
        return ItemStack.EMPTY;
    }

    public AthanorResult assembleOutputs(AthanorRecipeInput input) {
        return assembleOutputs(input, null, null);
    }

    public AthanorResult assembleOutputs(AthanorRecipeInput input, Level level, BlockPos pos) {
        List<ItemStack> outputs = new ArrayList<>(guaranteedOutput.size() + chanceOutput.size() + 1);
        for (ItemStackTemplate guaranteed : guaranteedOutput) {
            outputs.add(guaranteed.create());
        }
        ItemStack toolStack = input.getItem(0);
        double toolBonusChance = toolStack.getOrDefault(NVDataComponents.ARC_CHANCE, 1D);
        for (Pair<ItemStackTemplate, Double> entry : chanceOutput) {
            int produced = rollBonusCount(entry.getSecond() * toolBonusChance);
            for (int i = 0; i < produced; i++) {
                outputs.add(entry.getFirst().create());
            }
        }
        if (spiritusBoost && level != null && pos != null && !guaranteedOutput.isEmpty()) {
            double raw = WorldSpiritusHandler.getCurrentSpiritus(level, pos, SpiritusType.RAW);
            int produced = rollBonusCount(spiritusBoostChance(raw) * toolBonusChance);
            for (int i = 0; i < produced; i++) {
                outputs.add(guaranteedOutput.get(0).create().copyWithCount(1));
                if (Math.random() < SPIRITUS_BOOST_CONSUME_CHANCE) {
                    WorldSpiritusHandler.drainSpiritusFromChunk(level, pos, SpiritusType.RAW, 1.0);
                }
            }
        }
        return new AthanorResult(outputs, outputFluid.map(FluidStackTemplate::create).orElse(FluidStack.EMPTY));
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

    public List<Pair<ItemStackTemplate, Double>> getAllListedOutputs() {
        List<Pair<ItemStackTemplate, Double>> cached = allListed;
        if (cached != null) return cached;
        List<Pair<ItemStackTemplate, Double>> outputs = new ArrayList<>();
        guaranteedOutput.forEach(stack -> outputs.add(Pair.of(stack, 1D)));
        outputs.addAll(chanceOutput);
        if (spiritusBoost && !guaranteedOutput.isEmpty()) {
            ItemStack singleStack = guaranteedOutput.get(0).create().copyWithCount(1);
            ItemStackTemplate singleBonus = ItemStackTemplate.fromNonEmptyStack(singleStack);
            outputs.add(Pair.of(singleBonus, SPIRITUS_BOOST_MAX_CHANCE));
        }
        cached = List.copyOf(outputs);
        allListed = cached;
        return cached;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.createFromOptionals(inputs.stream().map(Optional::of).toList());
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<? extends Recipe<AthanorRecipeInput>> getSerializer() {
        return NVRecipes.ATHANOR_SERIALIZER.get();
    }

    @Override
    public RecipeType<? extends Recipe<AthanorRecipeInput>> getType() {
        return NVRecipes.ATHANOR_TYPE.get();
    }
}
