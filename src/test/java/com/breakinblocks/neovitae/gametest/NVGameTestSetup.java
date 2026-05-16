package com.breakinblocks.neovitae.gametest;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;

public class NVGameTestSetup {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NeoVitae.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<Block, TestEnergyBlock> TEST_ENERGY_BLOCK =
            BLOCKS.registerBlock("test_energy_block", TestEnergyBlock::new,
                    () -> BlockBehaviour.Properties.of().strength(1.0F));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TestEnergyBlock.TestEnergyBlockEntity>> TEST_ENERGY_BE_TYPE =
            BLOCK_ENTITIES.register("test_energy_block",
                    () -> new BlockEntityType<>(TestEnergyBlock.TestEnergyBlockEntity::new, TEST_ENERGY_BLOCK.get()));

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        modBus.addListener(NVGameTestSetup::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                TEST_ENERGY_BE_TYPE.get(),
                TestEnergyBlock.TestEnergyBlockEntity::getEnergyHandler
        );
    }
}
