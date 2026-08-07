package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.capability.NVCapabilities;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonAlternatorBlockEntity;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.SpikeTrapBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.InputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.MasterRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.OutputRoutingNodeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.routing.RoutingConduitBlockEntity;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.OrbFluidHandler;
import com.breakinblocks.neovitae.common.sideconfig.SideConfigItemHandler;
import com.breakinblocks.neovitae.client.render.blockentity.AlchemyArrayRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.AraVitaeRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.BloodTankRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.HellfireForgeRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.MasterRitualStoneRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.MimicRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.OrbFillingLinkRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.VasMaleficumRenderer;
import com.breakinblocks.neovitae.client.render.blockentity.VitaeLinkRenderer;

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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AthanorBlockEntity>> ATHANOR_TYPE =
            registerTile("athanor", AthanorBlockEntity::new, NVBlocks.ATHANOR_BLOCK.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BloodTankBlockEntity>> BLOOD_TANK_TYPE =
            registerTile("blood_tank", BloodTankBlockEntity::new, NVBlocks.BLOOD_TANK.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BloodBatteryBlockEntity>> BLOOD_BATTERY_TYPE =
            registerTile("blood_battery", BloodBatteryBlockEntity::new, NVBlocks.BLOOD_BATTERY.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlchemyArrayBlockEntity>> ALCHEMY_ARRAY_TYPE =
            registerTile("alchemy_array", AlchemyArrayBlockEntity::new, NVBlocks.ALCHEMY_ARRAY);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DemonLanternBlockEntity>> DEMON_LANTERN_TYPE =
            registerTile("demon_lantern", DemonLanternBlockEntity::new, NVBlocks.DEMON_LANTERN.block());

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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VasMaleficumBlockEntity>> VAS_MALEFICUM_TYPE =
            registerTile("vas_maleficum", VasMaleficumBlockEntity::new, NVBlocks.VAS_MALEFICUM.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpiraInfernalisBlockEntity>> SPIRA_INFERNALIS_TYPE =
            registerTile("spira_infernalis", SpiraInfernalisBlockEntity::new, NVBlocks.SPIRA_INFERNALIS.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrystallariumMaleficumBlockEntity>> CRYSTALLARIUM_MALEFICUM_TYPE =
            registerTile("crystallarium_maleficum", CrystallariumMaleficumBlockEntity::new, NVBlocks.CRYSTALLARIUM_MALEFICUM.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpiritusCrystalBlockEntity>> SPIRITUS_CRYSTAL_TYPE =
            registerTile("spiritus_crystal", SpiritusCrystalBlockEntity::new,
                    NVBlocks.RAW_SPIRITUS_CRYSTAL.block(),
                    NVBlocks.SPIRITUS_RUINA_CRYSTAL.block(),
                    NVBlocks.SPIRITUS_NIHILUM_CRYSTAL.block(),
                    NVBlocks.SPIRITUS_VINDICTA_CRYSTAL.block(),
                    NVBlocks.SPIRITUS_INVICTUS_CRYSTAL.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpectralBlockEntity>> SPECTRAL_BLOCK_TYPE =
            registerTile("spectral_block", SpectralBlockEntity::new, NVBlocks.SPECTRAL_BLOCK);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PhantomBridgeBlockEntity>> PHANTOM_BRIDGE_TYPE =
            registerTile("phantom_bridge", PhantomBridgeBlockEntity::new, NVBlocks.PHANTOM_BRIDGE_BLOCK);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RoutingConduitBlockEntity>> ROUTING_CONDUIT_TYPE =
            registerTile("routing_conduit", RoutingConduitBlockEntity::new, NVBlocks.ROUTING_CONDUIT.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InputRoutingNodeBlockEntity>> INPUT_ROUTING_NODE_TYPE =
            registerTile("input_routing_node", InputRoutingNodeBlockEntity::new, NVBlocks.INPUT_ROUTING_NODE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OutputRoutingNodeBlockEntity>> OUTPUT_ROUTING_NODE_TYPE =
            registerTile("output_routing_node", OutputRoutingNodeBlockEntity::new, NVBlocks.OUTPUT_ROUTING_NODE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MasterRoutingNodeBlockEntity>> MASTER_ROUTING_NODE_TYPE =
            registerTile("master_routing_node", MasterRoutingNodeBlockEntity::new, NVBlocks.MASTER_ROUTING_NODE.block());

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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpikeTrapBlockEntity>> SPIKE_TRAP_TYPE =
            registerTile("spike_trap", SpikeTrapBlockEntity::new,
                    DungeonBlocks.SPIKE_TRAP.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonAlternatorBlockEntity>> DUNGEON_ALTERNATOR_TYPE =
            registerTile("dungeon_alternator", DungeonAlternatorBlockEntity::new,
                    DungeonBlocks.ALTERNATOR.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonControllerBlockEntity>> DUNGEON_CONTROLLER_TYPE =
            registerTile("dungeon_controller", DungeonControllerBlockEntity::new, NVBlocks.DUNGEON_CONTROLLER.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpatialRiftBlockEntity>> SPATIAL_RIFT_TYPE =
            registerTile("spatial_rift", SpatialRiftBlockEntity::new, NVBlocks.SPATIAL_RIFT.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DungeonSealBlockEntity>> DUNGEON_SEAL_TYPE =
            registerTile("dungeon_seal", DungeonSealBlockEntity::new, NVBlocks.DUNGEON_SEAL.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BloodLightBlockEntity>> BLOOD_LIGHT =
            registerTile("blood_light", BloodLightBlockEntity::new, NVBlocks.BLOOD_LIGHT);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpiritCacheBlockEntity>> SPIRIT_CACHE_TYPE =
            registerTile("spirit_cache", SpiritCacheBlockEntity::new, NVBlocks.SPIRIT_CACHE.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VitaeLinkBlockEntity>> VITAE_LINK_TYPE =
            registerTile("vitae_link", VitaeLinkBlockEntity::new, NVBlocks.VITAE_LINK.block());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<OrbFillingLinkBlockEntity>> ORB_FILLING_LINK_TYPE =
            registerTile("orb_filling_link", OrbFillingLinkBlockEntity::new, NVBlocks.ORB_FILLING_LINK.block());

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
                TABULA_VITAE_TYPE.get(),
                (tile, side) -> {
                    TabulaVitaeBlockEntity master = tile.getMaster();
                    return master == null ? null : new SideConfigItemHandler(master.inv, master.getSideConfig(), side);
                }
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ATHANOR_TYPE.get(),
                AthanorBlockEntity::getItemHandler
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ATHANOR_TYPE.get(),
                AthanorBlockEntity::getFluidHandler
        );
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BLOOD_TANK_TYPE.get(),
                BloodTankBlockEntity::getFluidHandler
        );
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                BLOOD_BATTERY_TYPE.get(),
                BloodBatteryBlockEntity::getEnergyHandler
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                VAS_MALEFICUM_TYPE.get(),
                (tile, side) -> tile.getInventory()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                SPIRIT_CACHE_TYPE.get(),
                (tile, side) -> new InvWrapper(tile)
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                VITAE_LINK_TYPE.get(),
                VitaeLinkBlockEntity::getItemHandler
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ORB_FILLING_LINK_TYPE.get(),
                OrbFillingLinkBlockEntity::getItemHandler
        );

        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new OrbFluidHandler(stack),
                NVItems.ORB_WEAK.get(),
                NVItems.ORB_APPRENTICE.get(),
                NVItems.ORB_MAGICIAN.get(),
                NVItems.ORB_MASTER.get(),
                NVItems.ORB_ARCHMAGE.get(),
                NVItems.ORB_TRANSCENDENT.get());
    }

    private static void registerBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(HELLFIRE_FORGE_TYPE.get(), HellfireForgeRenderer::new);
        event.registerBlockEntityRenderer(VAS_MALEFICUM_TYPE.get(), VasMaleficumRenderer::new);
        event.registerBlockEntityRenderer(ARA_VITAE_TYPE.get(), AraVitaeRenderer::new);
        event.registerBlockEntityRenderer(BLOOD_TANK_TYPE.get(), BloodTankRenderer::new);
        event.registerBlockEntityRenderer(ALCHEMY_ARRAY_TYPE.get(), AlchemyArrayRenderer::new);
        event.registerBlockEntityRenderer(MIMIC_TYPE.get(), MimicRenderer::new);
        event.registerBlockEntityRenderer(MASTER_RITUAL_STONE_TYPE.get(), MasterRitualStoneRenderer::new);
        event.registerBlockEntityRenderer(VITAE_LINK_TYPE.get(), VitaeLinkRenderer::new);
        event.registerBlockEntityRenderer(ORB_FILLING_LINK_TYPE.get(), OrbFillingLinkRenderer::new);
    }

    public static void register(IEventBus modBus) {
        TILES.register(modBus);
        modBus.addListener(NVTiles::registerTileCapabilities);
        modBus.addListener(NVTiles::registerBlockEntityRenderer);
    }
}