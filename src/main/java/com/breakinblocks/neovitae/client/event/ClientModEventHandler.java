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
import com.breakinblocks.neovitae.client.render.entity.DaemoniumGlaciarisRenderer;
import com.breakinblocks.neovitae.client.render.entity.DaemoniumIgnisRenderer;
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
import com.breakinblocks.neovitae.client.render.entity.layer.LivingElytraLayer;
import com.breakinblocks.neovitae.client.screen.AthanorScreen;
import com.breakinblocks.neovitae.client.screen.FilterScreen;
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
            NVItems.WILL_ITEMS.getEntries().forEach(item -> {
                ItemProperties.register(item.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.DEFAULT).ordinal());
            });
            ItemProperties.register(NVItems.SACRIFICIAL_DAGGER.get(), NeoVitae.INCENSE_PROPERTY, ((stack, level, entity, seed) -> stack.getOrDefault(NVDataComponents.INCENSE, false) ? 1 : 0));

            ItemProperties.register(NVItems.SENTIENT_SWORD.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.DEFAULT).ordinal());
            ItemProperties.register(NVItems.SENTIENT_AXE.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.DEFAULT).ordinal());
            ItemProperties.register(NVItems.SENTIENT_PICKAXE.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.DEFAULT).ordinal());
            ItemProperties.register(NVItems.SENTIENT_SHOVEL.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.DEFAULT).ordinal());
            ItemProperties.register(NVItems.SENTIENT_SCYTHE.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.DEFAULT).ordinal());

            ItemProperties.register(NVItems.SENTIENT_SWORD.get(), NeoVitae.rl("active"), (stack, level, entity, seed) -> {
                if (!(entity instanceof net.minecraft.world.entity.player.Player player)) return 0;
                double will = com.breakinblocks.neovitae.will.PlayerSpiritusHandler.getTotalSpiritus(
                        stack.getOrDefault(NVDataComponents.SPIRITUS_TYPE, SpiritusType.DEFAULT), player);
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
        event.registerEntityRenderer(NVEntities.SPIRITUS_SNARE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(NVEntities.METEOR.get(), EntityMeteorRenderer::new);
        event.registerEntityRenderer(NVEntities.POTION_FLASK.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(NVEntities.SHAPED_CHARGE.get(), EntityShapedChargeRenderer::new);
        event.registerEntityRenderer(NVEntities.THROWING_DAGGER.get(), EntityThrowingDaggerRenderer::new);
        event.registerEntityRenderer(NVEntities.THROWING_DAGGER_SYRINGE.get(), EntityThrowingDaggerRenderer::new);
        event.registerEntityRenderer(NVEntities.DAEMONIUM_IGNIS.get(), DaemoniumIgnisRenderer::new);
        event.registerEntityRenderer(NVEntities.DAEMONIUM_GLACIARIS.get(), DaemoniumGlaciarisRenderer::new);
        event.registerEntityRenderer(NVEntities.BLOOD_SHIELD.get(), com.breakinblocks.neovitae.client.render.entity.shield.BloodShieldRenderer::new);
    }

    @SubscribeEvent
    public static void registerRenderLayer(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model model : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(model);
            if (renderer != null) {
                renderer.addLayer(new LivingElytraLayer<>(renderer, event.getEntityModels()));
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
        event.register(NVMenus.FILTER.get(), FilterScreen::new);
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
            if (layer == 1) return 0x99990011;
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