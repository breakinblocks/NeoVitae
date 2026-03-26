package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.capability.NVCapabilities;
import com.breakinblocks.neovitae.client.render.blockentity.AlchemyArrayRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.AraVitaeRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.BloodTankRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.HellfireForgeRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.RoutingNodeRenderer;
import com.breakinblocks.neovitae.common.block.NVBlocks;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NVTiles {
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, NeoVitae.MODID);

    @SafeVarargs
    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerTile(
            String name,
            BlockEntityType.BlockEntitySupplier<T> factory,
            Supplier<? extends Block>... validBlocks) {
        return TILES.register(name, () -> new BlockEntityType<>(
                factory,
                Arrays.stream(validBlocks).map(Supplier::get).collect(Collectors.toSet()),
                null
        ));
    }

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HellfireForgeBlockEntity>> HELLFIRE_FORGE_TYPE =
            registerTile("hellfire_forge", HellfireForgeBlockEntity::new, NVBlocks.HELLFIRE_FORGE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AraVitaeTile>> ARA_VITAE_TYPE =
            registerTile("ara_vitae", AraVitaeTile::new, NVBlocks.ARA_VITAE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ARCBlockEntity>> ARC_TYPE =
            registerTile("arc", ARCBlockEntity::new, NVBlocks.ARC_BLOCK.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BloodTankBlockEntity>> BLOOD_TANK_TYPE =
            registerTile("blood_tank", BloodTankBlockEntity::new, NVBlocks.BLOOD_TANK.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemyArrayBlockEntity>> ALCHEMY_ARRAY_TYPE =
            registerTile("alchemy_array", AlchemyArrayBlockEntity::new, NVBlocks.ALCHEMY_ARRAY);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TabulaVitaeBlockEntity>> TABULA_VITAE_TYPE =
            registerTile("tabula_vitae", TabulaVitaeBlockEntity::new, NVBlocks.TABULA_VITAE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TeleposerBlockEntity>> TELEPOSER_TYPE =
            registerTile("teleposer", TeleposerBlockEntity::new, NVBlocks.TELEPOSER.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MasterRitualStoneBlockEntity>> MASTER_RITUAL_STONE_TYPE =
            registerTile("master_ritual_stone", MasterRitualStoneBlockEntity::new,
                    NVBlocks.MASTER_RITUAL_STONE.block(), NVBlocks.INVERTED_MASTER_RITUAL_STONE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ImperfectRitualStoneBlockEntity>> IMPERFECT_RITUAL_STONE_TYPE =
            registerTile("imperfect_ritual_stone", ImperfectRitualStoneBlockEntity::new, NVBlocks.IMPERFECT_RITUAL_STONE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IncenseAltarBlockEntity>> INCENSE_ALTAR_TYPE =
            registerTile("incense_altar", IncenseAltarBlockEntity::new, NVBlocks.INCENSE_ALTAR.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DemonCrucibleBlockEntity>> DEMON_CRUCIBLE_TYPE =
            registerTile("demon_crucible", DemonCrucibleBlockEntity::new, NVBlocks.DEMON_CRUCIBLE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DemonPylonBlockEntity>> DEMON_PYLON_TYPE =
            registerTile("demon_pylon", DemonPylonBlockEntity::new, NVBlocks.DEMON_PYLON.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DemonCrystallizerBlockEntity>> DEMON_CRYSTALLIZER_TYPE =
            registerTile("demon_crystallizer", DemonCrystallizerBlockEntity::new, NVBlocks.DEMON_CRYSTALLIZER.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DemonCrystalBlockEntity>> DEMON_CRYSTAL_TYPE =
            registerTile("demon_crystal", DemonCrystalBlockEntity::new,
                    NVBlocks.RAW_DEMON_CRYSTAL.block(),
                    NVBlocks.CORROSIVE_DEMON_CRYSTAL.block(),
                    NVBlocks.DESTRUCTIVE_DEMON_CRYSTAL.block(),
                    NVBlocks.VENGEFUL_DEMON_CRYSTAL.block(),
                    NVBlocks.STEADFAST_DEMON_CRYSTAL.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpectralBlockEntity>> SPECTRAL_BLOCK_TYPE =
            registerTile("spectral_block", SpectralBlockEntity::new, NVBlocks.SPECTRAL_BLOCK);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PhantomBridgeBlockEntity>> PHANTOM_BRIDGE_TYPE =
            registerTile("phantom_bridge", PhantomBridgeBlockEntity::new, NVBlocks.PHANTOM_BRIDGE_BLOCK);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.breakinblocks.neovitae.common.blockentity.routing.RoutingNodeBlockEntity>> ROUTING_NODE_TYPE =
            registerTile("routing_node", com.breakinblocks.neovitae.common.blockentity.routing.RoutingNodeBlockEntity::new, NVBlocks.ROUTING_NODE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.breakinblocks.neovitae.common.blockentity.routing.InputRoutingNodeBlockEntity>> INPUT_ROUTING_NODE_TYPE =
            registerTile("input_routing_node", com.breakinblocks.neovitae.common.blockentity.routing.InputRoutingNodeBlockEntity::new, NVBlocks.INPUT_ROUTING_NODE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeBlockEntity>> OUTPUT_ROUTING_NODE_TYPE =
            registerTile("output_routing_node", com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeBlockEntity::new, NVBlocks.OUTPUT_ROUTING_NODE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity>> MASTER_ROUTING_NODE_TYPE =
            registerTile("master_routing_node", com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity::new, NVBlocks.MASTER_ROUTING_NODE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShapedExplosiveBlockEntity>> SHAPED_EXPLOSIVE_TYPE =
            registerTile("shaped_explosive", ShapedExplosiveBlockEntity::new,
                    NVBlocks.SHAPED_CHARGE.block(),
                    NVBlocks.AUG_SHAPED_CHARGE.block(),
                    NVBlocks.SHAPED_CHARGE_DEEP.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DeforesterChargeBlockEntity>> DEFORESTER_CHARGE_TYPE =
            registerTile("deforester_charge", DeforesterChargeBlockEntity::new,
                    NVBlocks.DEFORESTER_CHARGE.block(), NVBlocks.DEFORESTER_CHARGE_2.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VeinMineChargeBlockEntity>> VEINMINE_CHARGE_TYPE =
            registerTile("veinmine_charge", VeinMineChargeBlockEntity::new,
                    NVBlocks.VEINMINE_CHARGE.block(), NVBlocks.VEINMINE_CHARGE_2.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FungalChargeBlockEntity>> FUNGAL_CHARGE_TYPE =
            registerTile("fungal_charge", FungalChargeBlockEntity::new,
                    NVBlocks.FUNGAL_CHARGE.block(), NVBlocks.FUNGAL_CHARGE_2.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MimicBlockEntity>> MIMIC_TYPE =
            registerTile("mimic", MimicBlockEntity::new,
                    NVBlocks.MIMIC.block(), NVBlocks.ETHEREAL_MIMIC.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InversionPillarBlockEntity>> INVERSION_PILLAR_TYPE =
            registerTile("inversion_pillar", InversionPillarBlockEntity::new, NVBlocks.INVERSION_PILLAR.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.breakinblocks.neovitae.common.block.dungeon.SpikeTrapBlockEntity>> SPIKE_TRAP_TYPE =
            registerTile("spike_trap", com.breakinblocks.neovitae.common.block.dungeon.SpikeTrapBlockEntity::new,
                    com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks.SPIKE_TRAP.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.breakinblocks.neovitae.common.block.dungeon.DungeonAlternatorBlockEntity>> DUNGEON_ALTERNATOR_TYPE =
            registerTile("dungeon_alternator", com.breakinblocks.neovitae.common.block.dungeon.DungeonAlternatorBlockEntity::new,
                    com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks.ALTERNATOR.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonControllerBlockEntity>> DUNGEON_CONTROLLER_TYPE =
            registerTile("dungeon_controller", DungeonControllerBlockEntity::new, NVBlocks.DUNGEON_CONTROLLER.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonSealBlockEntity>> DUNGEON_SEAL_TYPE =
            registerTile("dungeon_seal", DungeonSealBlockEntity::new, NVBlocks.DUNGEON_SEAL.block());

    private static void registerTileCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                HELLFIRE_FORGE_TYPE.get(),
                HellfireForgeBlockEntity::getInventory
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ARA_VITAE_TYPE.get(),
                (tile, side) -> tile.inv
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ARA_VITAE_TYPE.get(),
                (tile, side) -> tile
        );
        event.registerBlockEntity(
                NVCapabilities.ARA_VITAE,
                ARA_VITAE_TYPE.get(),
                (tile, side) -> tile
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ARC_TYPE.get(),
                ARCBlockEntity::getItemHandler
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ARC_TYPE.get(),
                ARCBlockEntity::getFluidHandler
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BLOOD_TANK_TYPE.get(),
                BloodTankBlockEntity::getFluidHandler
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                DEMON_CRUCIBLE_TYPE.get(),
                (tile, side) -> tile.getInventory()
        );
    }

    private static void registerBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(HELLFIRE_FORGE_TYPE.get(), HellfireForgeRenderer::new);
        event.registerBlockEntityRenderer(ARA_VITAE_TYPE.get(), AraVitaeRenderer::new);
        event.registerBlockEntityRenderer(BLOOD_TANK_TYPE.get(), BloodTankRenderer::new);
        event.registerBlockEntityRenderer(ALCHEMY_ARRAY_TYPE.get(), AlchemyArrayRenderer::new);

        event.registerBlockEntityRenderer(ROUTING_NODE_TYPE.get(), RoutingNodeRenderer::new);
        event.registerBlockEntityRenderer(INPUT_ROUTING_NODE_TYPE.get(), RoutingNodeRenderer::new);
        event.registerBlockEntityRenderer(OUTPUT_ROUTING_NODE_TYPE.get(), RoutingNodeRenderer::new);
        event.registerBlockEntityRenderer(MASTER_ROUTING_NODE_TYPE.get(), RoutingNodeRenderer::new);
    }

    public static void register(IEventBus modBus) {
        TILES.register(modBus);
        modBus.addListener(NVTiles::registerTileCapabilities);
        modBus.addListener(NVTiles::registerBlockEntityRenderer);
    }
}