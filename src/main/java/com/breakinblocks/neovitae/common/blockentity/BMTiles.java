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
import com.breakinblocks.neovitae.api.capability.BMCapabilities;
import com.breakinblocks.neovitae.client.render.blockentity.AlchemyArrayRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.BloodAltarRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.BloodTankRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.HellfireForgeRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.RoutingNodeRenderer;
import com.breakinblocks.neovitae.common.block.BMBlocks;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BMTiles {
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, NeoVitae.MODID);

    /**
     * Helper method to reduce boilerplate in block entity type registration.
     * @param name The registry name for the block entity type
     * @param factory The block entity constructor/factory
     * @param validBlocks Suppliers for blocks that can use this block entity type
     * @return A DeferredHolder for the registered block entity type
     */
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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HellfireForgeTile>> HELLFIRE_FORGE_TYPE =
            registerTile("hellfire_forge", HellfireForgeTile::new, BMBlocks.HELLFIRE_FORGE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BloodAltarTile>> BLOOD_ALTAR_TYPE =
            registerTile("blood_altar", BloodAltarTile::new, BMBlocks.BLOOD_ALTAR.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ARCTile>> ARC_TYPE =
            registerTile("arc", ARCTile::new, BMBlocks.ARC_BLOCK.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BloodTankTile>> BLOOD_TANK_TYPE =
            registerTile("blood_tank", BloodTankTile::new, BMBlocks.BLOOD_TANK.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemyArrayTile>> ALCHEMY_ARRAY_TYPE =
            registerTile("alchemy_array", AlchemyArrayTile::new, BMBlocks.ALCHEMY_ARRAY);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemyTableTile>> ALCHEMY_TABLE_TYPE =
            registerTile("alchemy_table", AlchemyTableTile::new, BMBlocks.ALCHEMY_TABLE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TeleposerTile>> TELEPOSER_TYPE =
            registerTile("teleposer", TeleposerTile::new, BMBlocks.TELEPOSER.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MasterRitualStoneTile>> MASTER_RITUAL_STONE_TYPE =
            registerTile("master_ritual_stone", MasterRitualStoneTile::new,
                    BMBlocks.MASTER_RITUAL_STONE.block(), BMBlocks.INVERTED_MASTER_RITUAL_STONE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileImperfectRitualStone>> IMPERFECT_RITUAL_STONE_TYPE =
            registerTile("imperfect_ritual_stone", TileImperfectRitualStone::new, BMBlocks.IMPERFECT_RITUAL_STONE.block());

    // Incense Altar - boosts self-sacrifice
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IncenseAltarTile>> INCENSE_ALTAR_TYPE =
            registerTile("incense_altar", IncenseAltarTile::new, BMBlocks.INCENSE_ALTAR.block());

    // Demon Will Aura System
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DemonCrucibleTile>> DEMON_CRUCIBLE_TYPE =
            registerTile("demon_crucible", DemonCrucibleTile::new, BMBlocks.DEMON_CRUCIBLE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DemonPylonTile>> DEMON_PYLON_TYPE =
            registerTile("demon_pylon", DemonPylonTile::new, BMBlocks.DEMON_PYLON.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DemonCrystallizerTile>> DEMON_CRYSTALLIZER_TYPE =
            registerTile("demon_crystallizer", DemonCrystallizerTile::new, BMBlocks.DEMON_CRYSTALLIZER.block());

    // Demon Crystal - growable crystals
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DemonCrystalTile>> DEMON_CRYSTAL_TYPE =
            registerTile("demon_crystal", DemonCrystalTile::new,
                    BMBlocks.RAW_DEMON_CRYSTAL.block(),
                    BMBlocks.CORROSIVE_DEMON_CRYSTAL.block(),
                    BMBlocks.DESTRUCTIVE_DEMON_CRYSTAL.block(),
                    BMBlocks.VENGEFUL_DEMON_CRYSTAL.block(),
                    BMBlocks.STEADFAST_DEMON_CRYSTAL.block());

    // Spectral Block - for fluid suppression
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpectralBlockTile>> SPECTRAL_BLOCK_TYPE =
            registerTile("spectral_block", SpectralBlockTile::new, BMBlocks.SPECTRAL_BLOCK);

    // Phantom Bridge Block - solid walkable temporary block
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PhantomBridgeTile>> PHANTOM_BRIDGE_TYPE =
            registerTile("phantom_bridge", PhantomBridgeTile::new, BMBlocks.PHANTOM_BRIDGE_BLOCK);

    // Routing Nodes
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.breakinblocks.neovitae.common.blockentity.routing.RoutingNodeTile>> ROUTING_NODE_TYPE =
            registerTile("routing_node", com.breakinblocks.neovitae.common.blockentity.routing.RoutingNodeTile::new, BMBlocks.ROUTING_NODE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.breakinblocks.neovitae.common.blockentity.routing.InputRoutingNodeTile>> INPUT_ROUTING_NODE_TYPE =
            registerTile("input_routing_node", com.breakinblocks.neovitae.common.blockentity.routing.InputRoutingNodeTile::new, BMBlocks.INPUT_ROUTING_NODE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeTile>> OUTPUT_ROUTING_NODE_TYPE =
            registerTile("output_routing_node", com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeTile::new, BMBlocks.OUTPUT_ROUTING_NODE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeTile>> MASTER_ROUTING_NODE_TYPE =
            registerTile("master_routing_node", com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeTile::new, BMBlocks.MASTER_ROUTING_NODE.block());

    // Explosive Charges
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShapedExplosiveTile>> SHAPED_EXPLOSIVE_TYPE =
            registerTile("shaped_explosive", ShapedExplosiveTile::new,
                    BMBlocks.SHAPED_CHARGE.block(),
                    BMBlocks.AUG_SHAPED_CHARGE.block(),
                    BMBlocks.SHAPED_CHARGE_DEEP.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DeforesterChargeTile>> DEFORESTER_CHARGE_TYPE =
            registerTile("deforester_charge", DeforesterChargeTile::new,
                    BMBlocks.DEFORESTER_CHARGE.block(), BMBlocks.DEFORESTER_CHARGE_2.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VeinMineChargeTile>> VEINMINE_CHARGE_TYPE =
            registerTile("veinmine_charge", VeinMineChargeTile::new,
                    BMBlocks.VEINMINE_CHARGE.block(), BMBlocks.VEINMINE_CHARGE_2.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FungalChargeTile>> FUNGAL_CHARGE_TYPE =
            registerTile("fungal_charge", FungalChargeTile::new,
                    BMBlocks.FUNGAL_CHARGE.block(), BMBlocks.FUNGAL_CHARGE_2.block());

    // Mimic Block (both regular and ethereal mimic use the same tile entity)
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MimicTile>> MIMIC_TYPE =
            registerTile("mimic", MimicTile::new,
                    BMBlocks.MIMIC.block(), BMBlocks.ETHEREAL_MIMIC.block());

    // Inversion Pillar - dungeon teleporter
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileInversionPillar>> INVERSION_PILLAR_TYPE =
            registerTile("inversion_pillar", TileInversionPillar::new, BMBlocks.INVERSION_PILLAR.block());

    // Dungeon functional blocks
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.breakinblocks.neovitae.common.block.dungeon.TileSpikeTrap>> SPIKE_TRAP_TYPE =
            registerTile("spike_trap", com.breakinblocks.neovitae.common.block.dungeon.TileSpikeTrap::new,
                    com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks.SPIKE_TRAP.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.breakinblocks.neovitae.common.block.dungeon.TileDungeonAlternator>> DUNGEON_ALTERNATOR_TYPE =
            registerTile("dungeon_alternator", com.breakinblocks.neovitae.common.block.dungeon.TileDungeonAlternator::new,
                    com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks.ALTERNATOR.block());

    // Dungeon Controller - manages procedural dungeon generation
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileDungeonController>> DUNGEON_CONTROLLER_TYPE =
            registerTile("dungeon_controller", TileDungeonController::new, BMBlocks.DUNGEON_CONTROLLER.block());

    // Dungeon Seal - sealed doors in procedural dungeons
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileDungeonSeal>> DUNGEON_SEAL_TYPE =
            registerTile("dungeon_seal", TileDungeonSeal::new, BMBlocks.DUNGEON_SEAL.block());

    private static void registerTileCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                HELLFIRE_FORGE_TYPE.get(),
                HellfireForgeTile::getInventory
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BLOOD_ALTAR_TYPE.get(),
                (tile, side) -> tile.inv
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BLOOD_ALTAR_TYPE.get(),
                (tile, side) -> tile
        );
        // Register Blood Altar API capability
        event.registerBlockEntity(
                BMCapabilities.BLOOD_ALTAR,
                BLOOD_ALTAR_TYPE.get(),
                (tile, side) -> tile
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ARC_TYPE.get(),
                ARCTile::getItemHandler
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ARC_TYPE.get(),
                ARCTile::getFluidHandler
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BLOOD_TANK_TYPE.get(),
                BloodTankTile::getFluidHandler
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                DEMON_CRUCIBLE_TYPE.get(),
                (tile, side) -> tile.getInventory()
        );
    }

    private static void registerBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(HELLFIRE_FORGE_TYPE.get(), HellfireForgeRenderer::new);
        event.registerBlockEntityRenderer(BLOOD_ALTAR_TYPE.get(), BloodAltarRenderer::new);
        event.registerBlockEntityRenderer(BLOOD_TANK_TYPE.get(), BloodTankRenderer::new);
        event.registerBlockEntityRenderer(ALCHEMY_ARRAY_TYPE.get(), AlchemyArrayRenderer::new);

        // Routing node renderers - draw beams between connected nodes
        event.registerBlockEntityRenderer(ROUTING_NODE_TYPE.get(), RoutingNodeRenderer::new);
        event.registerBlockEntityRenderer(INPUT_ROUTING_NODE_TYPE.get(), RoutingNodeRenderer::new);
        event.registerBlockEntityRenderer(OUTPUT_ROUTING_NODE_TYPE.get(), RoutingNodeRenderer::new);
        event.registerBlockEntityRenderer(MASTER_ROUTING_NODE_TYPE.get(), RoutingNodeRenderer::new);
    }

    public static void register(IEventBus modBus) {
        TILES.register(modBus);
        modBus.addListener(BMTiles::registerTileCapabilities);
        modBus.addListener(BMTiles::registerBlockEntityRenderer);
    }
}