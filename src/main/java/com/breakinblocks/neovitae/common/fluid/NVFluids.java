package com.breakinblocks.neovitae.common.fluid;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import com.breakinblocks.neovitae.NeoVitae;

public class NVFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, NeoVitae.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, NeoVitae.MODID);
    public static final DeferredRegister<Item> BUCKETS = DeferredRegister.createItems(NeoVitae.MODID);
    public static final DeferredRegister<Block> SOURCE_BLOCKS = DeferredRegister.createBlocks(NeoVitae.MODID);

    private static FluidType createFluidType(String descriptionId) {
        return new FluidType(FluidType.Properties.create()
                .descriptionId(descriptionId)
                .fallDistanceModifier(0F)
                .canExtinguish(false)
                .canConvertToSource(false)
                .supportsBoating(false)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
                .canHydrate(false)
                .viscosity(1000)
        );
    }

    public static final DeferredHolder<FluidType, FluidType> ESSENTIA_VITAE_TYPE =
            FLUID_TYPES.register("essentia_vitae_type", () -> createFluidType("fluid.neovitae.essentia_vitae"));

    public static final DeferredHolder<Fluid, FlowingFluid> ESSENTIA_VITAE_SOURCE = FLUIDS.register("essentia_vitae_source", () -> new BaseFlowingFluid.Source(essentiaVitaeProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> ESSENTIA_VITAE_FLOWING = FLUIDS.register("essentia_vitae_flowing", () -> new BaseFlowingFluid.Flowing(essentiaVitaeProperties()));
    public static final DeferredHolder<Item, BucketItem> ESSENTIA_VITAE_BUCKET = BUCKETS.register("essentia_vitae_bucket", () -> new BucketItem(ESSENTIA_VITAE_SOURCE.get(), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
    public static final DeferredHolder<Block, LiquidBlock> ESSENTIA_VITAE_BLOCK = SOURCE_BLOCKS.register("essentia_vitae_block", () -> new LiquidBlock(ESSENTIA_VITAE_SOURCE.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));

    private static BaseFlowingFluid.Properties essentiaVitaeProperties() {
        return new BaseFlowingFluid.Properties(ESSENTIA_VITAE_TYPE, ESSENTIA_VITAE_SOURCE, ESSENTIA_VITAE_FLOWING).bucket(ESSENTIA_VITAE_BUCKET).block(ESSENTIA_VITAE_BLOCK);
    }

    public static final DeferredHolder<FluidType, FluidType> DOUBT_TYPE =
            FLUID_TYPES.register("doubt_fluid_type", () -> createFluidType("fluid.neovitae.doubt_fluid"));

    public static final DeferredHolder<Fluid, FlowingFluid> DOUBT_FLUID_SOURCE = FLUIDS.register("doubt_fluid_source", () -> new BaseFlowingFluid.Source(doubtProperties()));
    public static final DeferredHolder<Fluid, FlowingFluid> DOUBT_FLUID_FLOWING = FLUIDS.register("doubt_fluid_flowing", () -> new BaseFlowingFluid.Flowing(doubtProperties()));
    public static final DeferredHolder<Item, BucketItem> DOUBT_BUCKET = BUCKETS.register("doubt_bucket", () -> new BucketItem(DOUBT_FLUID_SOURCE.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    public static final DeferredHolder<Block, LiquidBlock> DOUBT_BLOCK = SOURCE_BLOCKS.register("doubt_block", () -> new LiquidBlock(DOUBT_FLUID_SOURCE.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));

    private static BaseFlowingFluid.Properties doubtProperties() {
        return new BaseFlowingFluid.Properties(DOUBT_TYPE, DOUBT_FLUID_SOURCE, DOUBT_FLUID_FLOWING).bucket(DOUBT_BUCKET).block(DOUBT_BLOCK);
    }

    private static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "block/essentia_vitae_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "block/essentia_vitae_flowing");
            }
        }, ESSENTIA_VITAE_TYPE);

        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "block/liquid_doubt_still");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "block/liquid_doubt_flowing");
            }
        }, DOUBT_TYPE);
    }

    public static void register(IEventBus modBus) {
        FLUID_TYPES.register(modBus);
        FLUIDS.register(modBus);
        BUCKETS.register(modBus);
        SOURCE_BLOCKS.register(modBus);
        modBus.addListener(NVFluids::registerClientExtensions);
    }
}
