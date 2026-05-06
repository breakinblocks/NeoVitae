package com.breakinblocks.neovitae.client.event;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.client.particle.BloodFlameParticle;
import com.breakinblocks.neovitae.client.particle.BloodGlowParticle;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import com.breakinblocks.neovitae.common.entity.NVEntities;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.render.entity.BloodLightRenderer;
import com.breakinblocks.neovitae.client.render.entity.DaemoniumCorrodisRenderer;
import com.breakinblocks.neovitae.client.render.entity.DaemoniumCruorisRenderer;
import com.breakinblocks.neovitae.client.render.entity.DaemoniumGlaciarisRenderer;
import com.breakinblocks.neovitae.client.render.entity.DaemoniumAnimarisRenderer;
import com.breakinblocks.neovitae.client.render.entity.DaemoniumDolorisRenderer;
import com.breakinblocks.neovitae.client.render.entity.DaemoniumPestisRenderer;
import com.breakinblocks.neovitae.client.render.entity.DaemoniumVoraxisRenderer;
import com.breakinblocks.neovitae.client.render.entity.DaemoniumFervidisRenderer;
import com.breakinblocks.neovitae.client.render.entity.DaemoniumIgnisRenderer;
import com.breakinblocks.neovitae.client.render.entity.DaemoniumRancorisRenderer;
import com.breakinblocks.neovitae.client.render.entity.EntityMeteorRenderer;
import com.breakinblocks.neovitae.client.render.entity.EntityShapedChargeRenderer;
import com.breakinblocks.neovitae.client.render.entity.EntityThrowingDaggerRenderer;
import com.breakinblocks.neovitae.client.render.entity.NoopRenderer;
import com.breakinblocks.neovitae.client.hud.SpiritusGaugeOverlay;
import com.breakinblocks.neovitae.common.item.AnointmentColor;
import com.breakinblocks.neovitae.common.item.ItemAnointmentProvider;
import com.breakinblocks.neovitae.common.item.MaterialItem;
import com.breakinblocks.neovitae.common.item.MaterialItemColor;
import com.breakinblocks.neovitae.common.material.MaterialRegistry;
import com.breakinblocks.neovitae.util.helper.ColorHelper;
import com.breakinblocks.neovitae.common.item.potion.FlaskColor;
import com.breakinblocks.neovitae.common.item.potion.TippedDaggerColor;
import com.breakinblocks.neovitae.client.screen.TabulaVitaeScreen;
import com.breakinblocks.neovitae.client.screen.MasterRoutingNodeScreen;
import com.breakinblocks.neovitae.client.screen.RoutingNodeScreen;
import com.breakinblocks.neovitae.client.screen.SigilHoldingScreen;
import com.breakinblocks.neovitae.client.screen.HellfireForgeScreen;
import com.breakinblocks.neovitae.client.screen.TrainerScreen;
import com.breakinblocks.neovitae.common.menu.NVMenus;
import com.breakinblocks.neovitae.client.render.entity.layer.SentientElytraLayer;
import com.breakinblocks.neovitae.client.screen.AthanorScreen;
import com.breakinblocks.neovitae.client.screen.DungeonSealScreen;
import com.breakinblocks.neovitae.client.screen.TeleposerScreen;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.OrbFluidHandler;
import com.breakinblocks.neovitae.util.helper.BloodLightHelper;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public class ClientModEventHandler {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (net.neoforged.fml.ModList.get().isLoaded("modonomicon")) {
            event.enqueueWork(com.breakinblocks.neovitae.compat.modonomicon.NVModonomiconClientCompat::registerPageRenderers);
        }

        event.enqueueWork(() -> {
            NVItems.SPIRITUS_ITEMS.getEntries().forEach(item -> {
                ItemProperties.register(item.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW).ordinal());
            });
            ItemProperties.register(NVItems.SACRIFICIAL_DAGGER.get(), NeoVitae.INCENSE_PROPERTY, ((stack, level, entity, seed) -> stack.getOrDefault(NVDataComponents.INCENSE, false) ? 1 : 0));
            ItemProperties.register(NVItems.SACRIFICIAL_DAGGER.get(), NeoVitae.rl("alternate"), ((stack, level, entity, seed) -> NeoVitae.SERVER_CONFIG.ALTERNATE_SACRIFICE_TOOL.get() ? 1 : 0));

            ItemProperties.register(NVItems.SENTIENT_SWORD.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW).ordinal());
            ItemProperties.register(NVItems.SENTIENT_AXE.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW).ordinal());
            ItemProperties.register(NVItems.SENTIENT_PICKAXE.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW).ordinal());
            ItemProperties.register(NVItems.SENTIENT_SHOVEL.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW).ordinal());
            ItemProperties.register(NVItems.SENTIENT_SCYTHE.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW).ordinal());

            ItemProperties.register(NVItems.SENTIENT_SWORD.get(), NeoVitae.rl("active"), (stack, level, entity, seed) -> {
                if (!(entity instanceof net.minecraft.world.entity.player.Player player)) return 0;
                double will = com.breakinblocks.neovitae.will.PlayerSpiritusHandler.getTotalSpiritus(
                        stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW), player);
                return will > 0 ? 1 : 0;
            });

            for (var orb : java.util.List.of(NVItems.ORB_WEAK, NVItems.ORB_APPRENTICE, NVItems.ORB_MAGICIAN,
                    NVItems.ORB_MASTER, NVItems.ORB_ARCHMAGE, NVItems.ORB_TRANSCENDENT)) {
                ItemProperties.register(orb.get(), NeoVitae.rl("fill_level"), (stack, lvl, entity, seed) -> {
                    int capacity = OrbFluidHandler.getOrbFluidCapacity(stack);
                    if (capacity <= 0) return 0;
                    SimpleFluidContent fluid = stack.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
                    return fluid.isEmpty() ? 0 : (float) fluid.getAmount() / capacity;
                });
            }
        });
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(NVEntities.BLOOD_LIGHT.get(), NoopRenderer::new);
        event.registerEntityRenderer(NVEntities.METEOR.get(), EntityMeteorRenderer::new);
        event.registerEntityRenderer(NVEntities.POTION_FLASK.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(NVEntities.SHAPED_CHARGE.get(), EntityShapedChargeRenderer::new);
        event.registerEntityRenderer(NVEntities.THROWING_DAGGER.get(), EntityThrowingDaggerRenderer::new);
        event.registerEntityRenderer(NVEntities.THROWING_DAGGER_SYRINGE.get(), EntityThrowingDaggerRenderer::new);
        event.registerEntityRenderer(NVEntities.DAEMONIUM_IGNIS.get(), DaemoniumIgnisRenderer::new);
        event.registerEntityRenderer(NVEntities.DAEMONIUM_CORRODIS.get(), DaemoniumCorrodisRenderer::new);
        event.registerEntityRenderer(NVEntities.DAEMONIUM_CRUORIS.get(), DaemoniumCruorisRenderer::new);
        event.registerEntityRenderer(NVEntities.DAEMONIUM_GLACIARIS.get(), DaemoniumGlaciarisRenderer::new);
        event.registerEntityRenderer(NVEntities.DAEMONIUM_RANCORIS.get(), DaemoniumRancorisRenderer::new);
        event.registerEntityRenderer(NVEntities.DAEMONIUM_FERVIDIS.get(), DaemoniumFervidisRenderer::new);
        event.registerEntityRenderer(NVEntities.DAEMONIUM_PESTIS.get(), DaemoniumPestisRenderer::new);
        event.registerEntityRenderer(NVEntities.DAEMONIUM_VORAXIS.get(), DaemoniumVoraxisRenderer::new);
        event.registerEntityRenderer(NVEntities.DAEMONIUM_DOLORIS.get(), DaemoniumDolorisRenderer::new);
        //noinspection unchecked,rawtypes - DaemoniumAnimarisEntity extends Vex; raw cast needed for VexRenderer generics
        event.registerEntityRenderer((net.minecraft.world.entity.EntityType) NVEntities.DAEMONIUM_ANIMARIS.get(), DaemoniumAnimarisRenderer::new);
        event.registerEntityRenderer(NVEntities.BLOOD_SHIELD.get(), com.breakinblocks.neovitae.client.render.entity.shield.BloodShieldRenderer::new);
        //noinspection unchecked,rawtypes - SlimeVitaeEntity extends Slime; raw cast needed for SlimeRenderer generics
        event.registerEntityRenderer((net.minecraft.world.entity.EntityType) NVEntities.SLIME_VITAE.get(), com.breakinblocks.neovitae.client.render.entity.SlimeVitaeRenderer::new);
        //noinspection unchecked,rawtypes - Summoned undead extend vanilla types; raw casts needed for renderer generics
        event.registerEntityRenderer((net.minecraft.world.entity.EntityType) NVEntities.NECROMANCY_SUMMON.get(), net.minecraft.client.renderer.entity.ZombieRenderer::new);
        event.registerEntityRenderer((net.minecraft.world.entity.EntityType) NVEntities.NECROMANCY_SUMMON_HUSK.get(), net.minecraft.client.renderer.entity.HuskRenderer::new);
        event.registerEntityRenderer((net.minecraft.world.entity.EntityType) NVEntities.NECROMANCY_SUMMON_SKELETON.get(), net.minecraft.client.renderer.entity.SkeletonRenderer::new);
        event.registerEntityRenderer((net.minecraft.world.entity.EntityType) NVEntities.NECROMANCY_SUMMON_STRAY.get(), net.minecraft.client.renderer.entity.StrayRenderer::new);
    }

    @SubscribeEvent
    public static void registerRenderLayer(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model model : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(model);
            if (renderer != null) {
                renderer.addLayer(new SentientElytraLayer<>(renderer, event.getEntityModels()));
            }
        }
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(NVMenus.ARC.get(), AthanorScreen::new);
        event.register(NVMenus.TRAINER.get(), TrainerScreen::new);
        event.register(NVMenus.TELEPOSER.get(), TeleposerScreen::new);
        event.register(NVMenus.TABULA_VITAE.get(), TabulaVitaeScreen::new);
        event.register(NVMenus.HELLFIRE_FORGE.get(), HellfireForgeScreen::new);
        event.register(NVMenus.SIGIL_HOLDING.get(), SigilHoldingScreen::new);
        event.register(NVMenus.ROUTING_NODE.get(), RoutingNodeScreen::new);
        event.register(NVMenus.MASTER_ROUTING_NODE.get(), MasterRoutingNodeScreen::new);
        event.register(NVMenus.DUNGEON_SEAL.get(), DungeonSealScreen::new);
        event.register(NVMenus.SPIRIT_CACHE.get(), com.breakinblocks.neovitae.client.screen.SpiritCacheScreen::new);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        FlaskColor flaskColor = new FlaskColor();
        event.register(flaskColor,
                NVItems.ALCHEMY_FLASK.get(),
                NVItems.ALCHEMY_FLASK_THROWABLE.get(),
                NVItems.ALCHEMY_FLASK_LINGERING.get());

        TippedDaggerColor tippedDaggerColor = new TippedDaggerColor();
        event.register(tippedDaggerColor, NVItems.THROWING_DAGGER_TIPPED.get());

        AnointmentColor anointmentColor = new AnointmentColor();
        NVItems.BASIC_ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof ItemAnointmentProvider)
                .forEach(holder -> event.register(anointmentColor, holder.get()));

        MaterialItemColor materialColor = new MaterialItemColor();
        MaterialRegistry.getAllItems().forEach(holder -> event.register(materialColor, holder.get()));

        event.register((stack, layer) -> {
            if (layer == 1) {
                return 0xFF000000 | ColorHelper.fromDye(BloodLightHelper.getColor(stack));
            }
            return 0xFFFFFFFF;
        }, NVItems.SIGIL_BLOOD_LIGHT.get());

        event.register((stack, layer) -> {
            net.minecraft.world.item.DyeColor color = stack.get(com.breakinblocks.neovitae.common.datacomponent.NVDataComponents.ALCHEMY_ARRAY_COLOR.get());
            if (color != null) {
                return 0xFF000000 | ColorHelper.fromDye(color);
            }
            return 0xFFFFFFFF;
        }, NVItems.ARCANE_SCRIBE_TOOL.get());

        event.register((stack, layer) -> {
            if (layer == 1) return 0x80FF0000;
            return 0xFFFFFFFF;
        }, NVItems.ORB_WEAK.get(), NVItems.ORB_APPRENTICE.get(), NVItems.ORB_MAGICIAN.get(),
                NVItems.ORB_MASTER.get(), NVItems.ORB_ARCHMAGE.get(), NVItems.ORB_TRANSCENDENT.get());
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(NVParticles.BLOOD_FLAME.get(), BloodFlameParticle.Provider::new);
        event.registerSpriteSet(NVParticles.BLOOD_GLOW.get(), BloodGlowParticle.Provider::new);
        event.registerSpriteSet(NVParticles.BLOOD_DRIP.get(), com.breakinblocks.neovitae.client.particle.BloodDripParticle.Provider::new);
        event.registerSpriteSet(NVParticles.RUNE_GLOW.get(), com.breakinblocks.neovitae.client.particle.RuneGlowParticle.Provider::new);
        event.registerSpriteSet(NVParticles.BLOOD_BUBBLE.get(), com.breakinblocks.neovitae.client.particle.BloodBubbleParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, NeoVitae.rl("spiritus_gauge"), new SpiritusGaugeOverlay());
    }

}