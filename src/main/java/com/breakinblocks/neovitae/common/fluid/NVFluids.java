package com.breakinblocks.neovitae.common.fluid;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import com.breakinblocks.neovitae.NeoVitae;
import java.util.function.Supplier;

public class NVFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, NeoVitae.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, NeoVitae.MODID);
    public static final DeferredRegister.Items BUCKETS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final DeferredRegister.Blocks SOURCE_BLOCKS = DeferredRegister.createBlocks(NeoVitae.MODID);

    private static FluidType createFluidType(String descriptionId) {
        return createFluidType(descriptionId, false);
    }

    private static FluidType createFluidType(String descriptionId, boolean canHydrate) {
        return new FluidType(FluidType.Properties.create()
                .descriptionId(descriptionId)
                .fallDistanceModifier(0F)
                .canExtinguish(false)
                .canConvertToSource(false)
                .supportsBoating(false)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                .canHydrate(canHydrate)
                .viscosity(1000)
        );
    }

    public static final DeferredHolder<FluidType, FluidType> ESSENTIA_VITAE_TYPE =
            FLUID_TYPES.register("essentia_vitae_type", () -> createFluidType("fluid.neovitae.essentia_vitae", true));

    public static final DeferredHolder<Fluid, FlowingFluid> ESSENTIA_VITAE_SOURCE = FLUIDS.register("essentia_vitae_source", () -> new BaseFlowingFluid.Source(essentiaVitaeProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> ESSENTIA_VITAE_FLOWING = FLUIDS.register("essentia_vitae_flowing", () -> new BaseFlowingFluid.Flowing(essentiaVitaeProperties()));
    public static final DeferredItem<BucketItem> ESSENTIA_VITAE_BUCKET = BUCKETS.registerItem("essentia_vitae_bucket",
            props -> new BucketItem(ESSENTIA_VITAE_SOURCE.get(), props.stacksTo(1).craftRemainder(Items.BUCKET)));
    public static final DeferredBlock<LiquidBlock> ESSENTIA_VITAE_BLOCK = SOURCE_BLOCKS.registerBlock("essentia_vitae_block",
            props -> new LiquidBlock(ESSENTIA_VITAE_SOURCE.get(), props),
            (Supplier<BlockBehaviour.Properties>) () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WATER));

    private static BaseFlowingFluid.Properties essentiaVitaeProperties() {
        return new BaseFlowingFluid.Properties(ESSENTIA_VITAE_TYPE, ESSENTIA_VITAE_SOURCE, ESSENTIA_VITAE_FLOWING)
                .bucket(ESSENTIA_VITAE_BUCKET)
                .block(ESSENTIA_VITAE_BLOCK)
                .tickRate(15)
                .slopeFindDistance(3);
    }

    public static final DeferredHolder<FluidType, FluidType> ANIMATED_SPIRITUS_TYPE =
            FLUID_TYPES.register("animated_spiritus_type", () -> createFluidType("fluid.neovitae.animated_spiritus"));

    public static final DeferredHolder<Fluid, FlowingFluid> ANIMATED_SPIRITUS_SOURCE = FLUIDS.register("animated_spiritus_source", () -> new BaseFlowingFluid.Source(animatedSpiritusProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> ANIMATED_SPIRITUS_FLOWING = FLUIDS.register("animated_spiritus_flowing", () -> new BaseFlowingFluid.Flowing(animatedSpiritusProperties()));
    public static final DeferredItem<BucketItem> ANIMATED_SPIRITUS_BUCKET = BUCKETS.registerItem("animated_spiritus_bucket",
            props -> new BucketItem(ANIMATED_SPIRITUS_SOURCE.get(), props.craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredBlock<LiquidBlock> ANIMATED_SPIRITUS_BLOCK = SOURCE_BLOCKS.registerBlock("animated_spiritus_block",
            props -> new LiquidBlock(ANIMATED_SPIRITUS_SOURCE.get(), props),
            (Supplier<BlockBehaviour.Properties>) () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WATER));

    private static BaseFlowingFluid.Properties animatedSpiritusProperties() {
        return new BaseFlowingFluid.Properties(ANIMATED_SPIRITUS_TYPE, ANIMATED_SPIRITUS_SOURCE, ANIMATED_SPIRITUS_FLOWING).bucket(ANIMATED_SPIRITUS_BUCKET).block(ANIMATED_SPIRITUS_BLOCK);
    }

    public static final DeferredHolder<FluidType, FluidType> LIQUIFIED_EXPERIENCE_TYPE =
            FLUID_TYPES.register("liquified_experience_type", () -> createFluidType("fluid.neovitae.liquified_experience"));

    public static final DeferredHolder<Fluid, FlowingFluid> LIQUIFIED_EXPERIENCE_SOURCE = FLUIDS.register("liquified_experience_source", () -> new BaseFlowingFluid.Source(liquifiedExperienceProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> LIQUIFIED_EXPERIENCE_FLOWING = FLUIDS.register("liquified_experience_flowing", () -> new BaseFlowingFluid.Flowing(liquifiedExperienceProperties()));
    public static final DeferredItem<BucketItem> LIQUIFIED_EXPERIENCE_BUCKET = BUCKETS.registerItem("liquified_experience_bucket",
            props -> new BucketItem(LIQUIFIED_EXPERIENCE_SOURCE.get(), props.stacksTo(1).craftRemainder(Items.BUCKET)));
    public static final DeferredBlock<LiquidBlock> LIQUIFIED_EXPERIENCE_BLOCK = SOURCE_BLOCKS.registerBlock("liquified_experience_block",
            props -> new LiquidBlock(LIQUIFIED_EXPERIENCE_SOURCE.get(), props),
            (Supplier<BlockBehaviour.Properties>) () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WATER));

    private static BaseFlowingFluid.Properties liquifiedExperienceProperties() {
        return new BaseFlowingFluid.Properties(LIQUIFIED_EXPERIENCE_TYPE, LIQUIFIED_EXPERIENCE_SOURCE, LIQUIFIED_EXPERIENCE_FLOWING)
                .bucket(LIQUIFIED_EXPERIENCE_BUCKET)
                .block(LIQUIFIED_EXPERIENCE_BLOCK)
                .tickRate(10);
    }

    private static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            public Identifier getStillTexture() {
                return Identifier.fromNamespaceAndPath(NeoVitae.MODID, "block/essentia_vitae_still");
            }

            public Identifier getFlowingTexture() {
                return Identifier.fromNamespaceAndPath(NeoVitae.MODID, "block/essentia_vitae_flowing");
            }
        }, ESSENTIA_VITAE_TYPE);

        event.registerFluidType(new IClientFluidTypeExtensions() {
            public Identifier getStillTexture() {
                return Identifier.fromNamespaceAndPath(NeoVitae.MODID, "block/animated_spiritus_still");
            }

            public Identifier getFlowingTexture() {
                return Identifier.fromNamespaceAndPath(NeoVitae.MODID, "block/animated_spiritus_flowing");
            }
        }, ANIMATED_SPIRITUS_TYPE);

        event.registerFluidType(new IClientFluidTypeExtensions() {
            public Identifier getStillTexture() {
                return Identifier.fromNamespaceAndPath(NeoVitae.MODID, "block/liquified_experience_still");
            }

            public Identifier getFlowingTexture() {
                return Identifier.fromNamespaceAndPath(NeoVitae.MODID, "block/liquified_experience_flowing");
            }
        }, LIQUIFIED_EXPERIENCE_TYPE);
    }

    public static void register(IEventBus modBus) {
        FLUID_TYPES.register(modBus);
        FLUIDS.register(modBus);
        BUCKETS.register(modBus);
        SOURCE_BLOCKS.register(modBus);
        modBus.addListener(NVFluids::registerClientExtensions);
    }
}
