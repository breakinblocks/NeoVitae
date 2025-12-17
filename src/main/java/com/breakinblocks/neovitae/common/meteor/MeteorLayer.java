package com.breakinblocks.neovitae.common.meteor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a layer of a meteor with configurable block generation.
 * Layers are spherical shells that can contain weighted block types.
 */
public class MeteorLayer {

    public static final Codec<MeteorLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("radius").forGetter(MeteorLayer::getLayerRadius),
            Codec.INT.optionalFieldOf("additionalWeight", 0).forGetter(MeteorLayer::getAdditionalTotalWeight),
            Codec.INT.optionalFieldOf("minWeight", 0).forGetter(MeteorLayer::getMinWeight),
            Codec.STRING.fieldOf("fill").forGetter(l -> l.fillBlock.getEntry()),
            Codec.STRING.optionalFieldOf("shell", "").forGetter(l -> l.shellBlock != null ? l.shellBlock.getEntry() : ""),
            WeightedBlock.CODEC.listOf().optionalFieldOf("weighted", List.of()).forGetter(MeteorLayer::getSerializableWeightList)
    ).apply(instance, MeteorLayer::fromCodec));

    public static final StreamCodec<RegistryFriendlyByteBuf, MeteorLayer> STREAM_CODEC = StreamCodec.of(
            MeteorLayer::encode,
            MeteorLayer::decode
    );

    private final int layerRadius;
    private final int additionalTotalWeight;
    private int minWeight = 0;
    private int totalMaxWeight = 0;
    private final List<Pair<RandomBlockContainer, Integer>> weightList;
    private final RandomBlockContainer fillBlock;
    private RandomBlockContainer shellBlock;

    public MeteorLayer(int layerRadius, int additionalMaxWeight, List<Pair<RandomBlockContainer, Integer>> weightList, RandomBlockContainer fillBlock) {
        this.layerRadius = layerRadius;
        this.additionalTotalWeight = additionalMaxWeight;
        this.weightList = new ArrayList<>(weightList);
        this.fillBlock = fillBlock;
    }

    public MeteorLayer(int layerRadius, int additionalMaxWeight, Block fillBlock) {
        this(layerRadius, additionalMaxWeight, new ArrayList<>(), new StaticBlockContainer(fillBlock));
    }

    public MeteorLayer(int layerRadius, int additionalMaxWeight, Fluid fillFluid) {
        this(layerRadius, additionalMaxWeight, new ArrayList<>(), new FluidBlockContainer(fillFluid));
    }

    public MeteorLayer(int layerRadius, int additionalMaxWeight, TagKey<Block> fillTag) {
        this(layerRadius, additionalMaxWeight, fillTag, -1);
    }

    public MeteorLayer(int layerRadius, int additionalMaxWeight, TagKey<Block> fillTag, int staticIndex) {
        this(layerRadius, additionalMaxWeight, new ArrayList<>(), new RandomBlockTagContainer(fillTag, staticIndex));
    }

    private static MeteorLayer fromCodec(int radius, int additionalWeight, int minWeight, String fill, String shell, List<WeightedBlock> weighted) {
        RandomBlockContainer fillBlock = RandomBlockContainer.parseEntry(fill);
        List<Pair<RandomBlockContainer, Integer>> weightList = new ArrayList<>();
        for (WeightedBlock wb : weighted) {
            RandomBlockContainer container = RandomBlockContainer.parseEntry(wb.entry());
            if (container != null) {
                weightList.add(Pair.of(container, wb.weight()));
            }
        }
        MeteorLayer layer = new MeteorLayer(radius, additionalWeight, weightList, fillBlock);
        if (!shell.isEmpty()) {
            layer.addShellBlock(RandomBlockContainer.parseEntry(shell));
        }
        layer.setMinWeight(minWeight);
        return layer;
    }

    public MeteorLayer addShellBlock(RandomBlockContainer shellBlock) {
        this.shellBlock = shellBlock;
        return this;
    }

    public MeteorLayer addShellBlock(TagKey<Block> tag) {
        return addShellBlock(tag, -1);
    }

    public MeteorLayer addShellBlock(TagKey<Block> tag, int staticIndex) {
        return addShellBlock(new RandomBlockTagContainer(tag, staticIndex));
    }

    public MeteorLayer addShellBlock(Block block) {
        return addShellBlock(new StaticBlockContainer(block));
    }

    public MeteorLayer addShellBlock(Fluid fluid) {
        return addShellBlock(new FluidBlockContainer(fluid));
    }

    public MeteorLayer addWeightedTag(TagKey<Block> tag, int weight) {
        return addWeightedTag(tag, weight, -1);
    }

    public MeteorLayer addWeightedTag(TagKey<Block> tag, int weight, int staticIndex) {
        weightList.add(Pair.of(new RandomBlockTagContainer(tag, staticIndex), weight));
        return this;
    }

    public MeteorLayer addWeightedBlock(Block block, int weight) {
        weightList.add(Pair.of(new StaticBlockContainer(block), weight));
        return this;
    }

    public MeteorLayer addWeightedFluid(Fluid fluid, int weight) {
        weightList.add(Pair.of(new FluidBlockContainer(fluid), weight));
        return this;
    }

    public MeteorLayer setMinWeight(int weight) {
        this.minWeight = weight;
        return this;
    }

    public void buildLayer(Level level, BlockPos centerPos, int emptyRadius) {
        recalculateMaxWeight(level.random, level);

        int radius = layerRadius;
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                for (int k = -radius; k <= radius; k++) {
                    if (emptyRadius >= 0 && checkIfSphere(emptyRadius, i, j, k)) {
                        continue;
                    }

                    if (checkIfSphere(radius, i, j, k)) {
                        BlockPos pos = centerPos.offset(i, j, k);
                        BlockState currentState = level.getBlockState(pos);
                        BlockPlaceContext ctx = new BlockPlaceContext(level, null, InteractionHand.MAIN_HAND, ItemStack.EMPTY,
                                BlockHitResult.miss(new Vec3(0, 0, 0), Direction.UP, pos));
                        if (!currentState.canBeReplaced(ctx)) {
                            continue;
                        }
                        if (shellBlock != null && checkIfSphereShell(radius, i, j, k)) {
                            Block block = shellBlock.getRandomBlock(level.random, level);
                            if (block != null) {
                                level.setBlockAndUpdate(pos, block.defaultBlockState());
                            }
                        } else {
                            level.setBlockAndUpdate(pos, getRandomState(level.random, level));
                        }
                    }
                }
            }
        }
    }

    public void recalculateMaxWeight(RandomSource rand, Level level) {
        totalMaxWeight = additionalTotalWeight;

        var iterator = weightList.iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            Block newBlock = entry.getKey().getRandomBlock(rand, level);
            if (newBlock == null) {
                iterator.remove();
                continue;
            }
            totalMaxWeight += entry.getRight();
        }

        totalMaxWeight = Math.max(minWeight, totalMaxWeight);
    }

    public BlockState getRandomState(RandomSource rand, Level level) {
        Block block = fillBlock.getRandomBlock(rand, level);
        if (totalMaxWeight > 0) {
            int randNum = rand.nextInt(totalMaxWeight);
            for (var entry : weightList) {
                randNum -= entry.getValue();
                if (randNum < 0) {
                    Block newBlock = entry.getKey().getRandomBlock(rand, level);
                    if (newBlock != null) {
                        block = newBlock;
                    }
                    break;
                }
            }
        }

        if (block != null) {
            return block.defaultBlockState();
        } else {
            return Blocks.AIR.defaultBlockState();
        }
    }

    public boolean checkIfSphereShell(int xR, int xOff, int yOff, int zOff) {
        if (!checkIfSphere(xR, xOff, yOff, zOff)) {
            return false;
        }
        return !((checkIfSphere(xR, xOff + 1, yOff, zOff) && checkIfSphere(xR, xOff - 1, yOff, zOff))
                && (checkIfSphere(xR, xOff, yOff + 1, zOff) && checkIfSphere(xR, xOff, yOff - 1, zOff))
                && (checkIfSphere(xR, xOff, yOff, zOff + 1) && checkIfSphere(xR, xOff, yOff, zOff - 1)));
    }

    public boolean checkIfSphere(float R, float xOff, float yOff, float zOff) {
        float possOffset = 0.5f;
        return xOff * xOff + yOff * yOff + zOff * zOff <= ((R + possOffset) * (R + possOffset));
    }

    // Getters

    public int getLayerRadius() {
        return layerRadius;
    }

    public int getAdditionalTotalWeight() {
        return additionalTotalWeight;
    }

    public int getMinWeight() {
        return minWeight;
    }

    public RandomBlockContainer getFillBlock() {
        return fillBlock;
    }

    public RandomBlockContainer getShellBlock() {
        return shellBlock;
    }

    public List<Pair<RandomBlockContainer, Integer>> getWeightList() {
        return weightList;
    }

    private List<WeightedBlock> getSerializableWeightList() {
        List<WeightedBlock> result = new ArrayList<>();
        for (var entry : weightList) {
            result.add(new WeightedBlock(entry.getKey().getEntry(), entry.getValue()));
        }
        return result;
    }

    // Network serialization

    private static void encode(RegistryFriendlyByteBuf buffer, MeteorLayer layer) {
        buffer.writeInt(layer.layerRadius);
        buffer.writeInt(layer.additionalTotalWeight);
        buffer.writeInt(layer.minWeight);
        buffer.writeInt(layer.weightList.size());
        for (var entry : layer.weightList) {
            buffer.writeUtf(entry.getKey().getEntry());
            buffer.writeInt(entry.getValue());
        }
        buffer.writeUtf(layer.fillBlock.getEntry());
        buffer.writeUtf(layer.shellBlock != null ? layer.shellBlock.getEntry() : "");
    }

    private static MeteorLayer decode(RegistryFriendlyByteBuf buffer) {
        int layerRadius = buffer.readInt();
        int additionalWeight = buffer.readInt();
        int minWeight = buffer.readInt();
        int listSize = buffer.readInt();

        List<Pair<RandomBlockContainer, Integer>> weightList = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            String entry = buffer.readUtf();
            int weight = buffer.readInt();
            if (!entry.isEmpty()) {
                RandomBlockContainer container = RandomBlockContainer.parseEntry(entry);
                if (container != null) {
                    weightList.add(Pair.of(container, weight));
                }
            }
        }

        RandomBlockContainer fillBlock = RandomBlockContainer.parseEntry(buffer.readUtf());
        MeteorLayer layer = new MeteorLayer(layerRadius, additionalWeight, weightList, fillBlock);

        String shellEntry = buffer.readUtf();
        if (!shellEntry.isEmpty()) {
            layer.addShellBlock(RandomBlockContainer.parseEntry(shellEntry));
        }

        layer.setMinWeight(minWeight);
        return layer;
    }

    /**
     * Helper record for codec serialization of weighted blocks.
     */
    public record WeightedBlock(String entry, int weight) {
        public static final Codec<WeightedBlock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("entry").forGetter(WeightedBlock::entry),
                Codec.INT.fieldOf("weight").forGetter(WeightedBlock::weight)
        ).apply(instance, WeightedBlock::new));
    }
}
