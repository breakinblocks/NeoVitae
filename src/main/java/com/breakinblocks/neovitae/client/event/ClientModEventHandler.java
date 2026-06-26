package com.breakinblocks.neovitae.client.event;

import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.model.sprite.Material;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.client.model.OrbFillProperty;
import com.breakinblocks.neovitae.client.model.OrbFillDecorator;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.client.particle.BloodBubbleParticle;
import com.breakinblocks.neovitae.client.particle.BloodDripParticle;
import com.breakinblocks.neovitae.client.particle.OvergrowthDripParticle;
import com.breakinblocks.neovitae.client.particle.BloodFlameParticle;
import com.breakinblocks.neovitae.client.particle.BloodGlowParticle;
import com.breakinblocks.neovitae.client.particle.RuneGlowParticle;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import com.breakinblocks.neovitae.common.entity.NVEntities;
import com.breakinblocks.neovitae.NeoVitae;
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
import com.breakinblocks.neovitae.client.render.entity.SlimeVitaeRenderer;
import com.breakinblocks.neovitae.client.render.entity.shield.BloodShieldRenderer;
import com.breakinblocks.neovitae.client.hud.ElementRegistry;
import com.breakinblocks.neovitae.client.hud.NVHudElements;
import com.breakinblocks.neovitae.common.item.AnointmentColor;
import com.breakinblocks.neovitae.common.item.MaterialItemColor;
import com.breakinblocks.neovitae.common.item.potion.FlaskColor;
import com.breakinblocks.neovitae.common.item.potion.TippedDaggerColor;
import com.breakinblocks.neovitae.client.screen.TabulaVitaeScreen;
import com.breakinblocks.neovitae.client.screen.MasterRoutingNodeScreen;
import com.breakinblocks.neovitae.client.screen.RoutingNodeScreen;
import com.breakinblocks.neovitae.client.screen.SigilHoldingScreen;
import com.breakinblocks.neovitae.client.screen.HellfireForgeScreen;
import com.breakinblocks.neovitae.client.screen.TrainerScreen;
import com.breakinblocks.neovitae.common.menu.NVMenus;
import com.breakinblocks.neovitae.client.screen.AthanorScreen;
import com.breakinblocks.neovitae.client.screen.DungeonSealScreen;
import com.breakinblocks.neovitae.client.screen.RitualConfiguratorScreen;
import com.breakinblocks.neovitae.client.screen.RitualDivinerScreen;
import com.breakinblocks.neovitae.client.screen.SpiritCacheScreen;
import com.breakinblocks.neovitae.client.screen.TeleposerScreen;
import com.breakinblocks.neovitae.common.item.NVItems;
import net.minecraft.client.renderer.entity.HuskRenderer;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.StrayRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import com.breakinblocks.neovitae.compat.modonomicon.NVModonomiconClientCompat;
import net.neoforged.fml.ModList;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public class ClientModEventHandler {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("modonomicon")) {
            event.enqueueWork(NVModonomiconClientCompat::registerPageRenderers);
        }
        event.enqueueWork(() -> {
            NVHudElements.register();
            ElementRegistry.readConfig();
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
        event.registerEntityRenderer(NVEntities.DAEMONIUM_ANIMARIS.get(), DaemoniumAnimarisRenderer::new);
        event.registerEntityRenderer(NVEntities.BLOOD_SHIELD.get(), BloodShieldRenderer::new);
        event.registerEntityRenderer(NVEntities.SLIME_VITAE.get(), SlimeVitaeRenderer::new);
        event.registerEntityRenderer(NVEntities.NECROMANCY_SUMMON.get(), ZombieRenderer::new);
        event.registerEntityRenderer(NVEntities.NECROMANCY_SUMMON_HUSK.get(), HuskRenderer::new);
        event.registerEntityRenderer(NVEntities.NECROMANCY_SUMMON_SKELETON.get(), SkeletonRenderer::new);
        event.registerEntityRenderer(NVEntities.NECROMANCY_SUMMON_STRAY.get(), StrayRenderer::new);
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
        event.register(NVMenus.RITUAL_DIVINER.get(), RitualDivinerScreen::new);
        event.register(NVMenus.RITUAL_CONFIGURATOR.get(), RitualConfiguratorScreen::new);
        event.register(NVMenus.SPIRIT_CACHE.get(), SpiritCacheScreen::new);
    }

    @SubscribeEvent
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(AnointmentColor.ID, AnointmentColor.MAP_CODEC);
        event.register(MaterialItemColor.ID, MaterialItemColor.MAP_CODEC);
        event.register(FlaskColor.ID, FlaskColor.MAP_CODEC);
        event.register(TippedDaggerColor.ID, TippedDaggerColor.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        event.register(NVItems.ORB_WEAK.get(), OrbFillDecorator.INSTANCE);
        event.register(NVItems.ORB_APPRENTICE.get(), OrbFillDecorator.INSTANCE);
        event.register(NVItems.ORB_MAGICIAN.get(), OrbFillDecorator.INSTANCE);
        event.register(NVItems.ORB_MASTER.get(), OrbFillDecorator.INSTANCE);
        event.register(NVItems.ORB_ARCHMAGE.get(), OrbFillDecorator.INSTANCE);
        event.register(NVItems.ORB_TRANSCENDENT.get(), OrbFillDecorator.INSTANCE);
    }

    @SubscribeEvent
    public static void registerRangeSelectItemModelProperties(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(OrbFillProperty.ID, OrbFillProperty.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(NVParticles.BLOOD_FLAME.get(), BloodFlameParticle.Provider::new);
        event.registerSpriteSet(NVParticles.BLOOD_GLOW.get(), BloodGlowParticle.Provider::new);
        event.registerSpriteSet(NVParticles.BLOOD_DRIP.get(), BloodDripParticle.Provider::new);
        event.registerSpriteSet(NVParticles.RUNE_GLOW.get(), RuneGlowParticle.Provider::new);
        event.registerSpriteSet(NVParticles.BLOOD_BUBBLE.get(), BloodBubbleParticle.Provider::new);
        event.registerSpriteSet(NVParticles.OVERGROWTH_DRIP.get(), OvergrowthDripParticle.Provider::new);
    }

    public static final KeyMapping.Category HUD_CATEGORY = KeyMapping.Category.register(NeoVitae.rl("neovitae"));
    public static final KeyMapping OPEN_HUD_EDIT = new KeyMapping(
            "key.neovitae.edit_hud", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, HUD_CATEGORY);

    public static final KeyMapping LEX_BEAM = new KeyMapping(
            "key.neovitae.lex_beam", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, HUD_CATEGORY);

    public static final KeyMapping BLOOD_SHIELD = new KeyMapping(
            "key.neovitae.blood_shield", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, HUD_CATEGORY);

    public static final KeyMapping LEX_MODE = new KeyMapping(
            "key.neovitae.lex_mode", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_PERIOD, HUD_CATEGORY);

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_HUD_EDIT);
        event.register(LEX_BEAM);
        event.register(BLOOD_SHIELD);
        event.register(LEX_MODE);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, NeoVitae.rl("hud_elements"),
                (guiGraphics, deltaTracker) -> ElementRegistry.render(guiGraphics, deltaTracker.getGameTimeDeltaPartialTick(false)));
    }

    @SubscribeEvent
    public static void registerFluidModels(RegisterFluidModelsEvent event) {
        event.register(new FluidModel.Unbaked(
                new Material(NeoVitae.rl("block/essentia_vitae_still")),
                new Material(NeoVitae.rl("block/essentia_vitae_flowing")),
                null, null), NVFluids.ESSENTIA_VITAE_SOURCE, NVFluids.ESSENTIA_VITAE_FLOWING);
        event.register(new FluidModel.Unbaked(
                new Material(NeoVitae.rl("block/animated_spiritus_still")),
                new Material(NeoVitae.rl("block/animated_spiritus_flowing")),
                null, null), NVFluids.ANIMATED_SPIRITUS_SOURCE, NVFluids.ANIMATED_SPIRITUS_FLOWING);
    }
}