package com.breakinblocks.neovitae.client.event;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import com.breakinblocks.neovitae.common.entity.BMEntities;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.render.entity.BloodLightRenderer;
import com.breakinblocks.neovitae.client.render.entity.EntityMeteorRenderer;
import com.breakinblocks.neovitae.client.render.entity.EntityShapedChargeRenderer;
import com.breakinblocks.neovitae.client.render.entity.EntityThrowingDaggerRenderer;
import com.breakinblocks.neovitae.client.render.entity.NoopRenderer;
import com.breakinblocks.neovitae.client.hud.DemonWillGaugeOverlay;
import com.breakinblocks.neovitae.common.item.AnointmentColor;
import com.breakinblocks.neovitae.common.item.ItemAnointmentProvider;
import com.breakinblocks.neovitae.common.item.potion.FlaskColor;
import com.breakinblocks.neovitae.common.item.potion.TippedDaggerColor;
import com.breakinblocks.neovitae.client.screen.AlchemyTableScreen;
import com.breakinblocks.neovitae.client.screen.MasterRoutingNodeScreen;
import com.breakinblocks.neovitae.client.screen.RoutingNodeScreen;
import com.breakinblocks.neovitae.client.screen.SigilHoldingScreen;
import com.breakinblocks.neovitae.client.screen.SoulForgeScreen;
import com.breakinblocks.neovitae.client.screen.TrainerScreen;
import com.breakinblocks.neovitae.common.menu.BMMenus;
import com.breakinblocks.neovitae.client.render.entity.layer.LivingElytraLayer;
import com.breakinblocks.neovitae.client.screen.ARCScreen;
import com.breakinblocks.neovitae.client.screen.FilterScreen;
import com.breakinblocks.neovitae.client.screen.TeleposerScreen;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.item.BMItems;

@EventBusSubscriber(value = Dist.CLIENT, modid = NeoVitae.MODID)
public class ClientModEventHandler {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BMItems.WILL_ITEMS.getEntries().forEach(item -> {
                ItemProperties.register(item.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(BMDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT).ordinal());
            });
            ItemProperties.register(BMItems.SACRIFICIAL_DAGGER.get(), NeoVitae.INCENSE_PROPERTY, ((stack, level, entity, seed) -> stack.getOrDefault(BMDataComponents.INCENSE, false) ? 1 : 0));

            // Sentient tools - register type property for texture switching
            ItemProperties.register(BMItems.SENTIENT_SWORD.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(BMDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT).ordinal());
            ItemProperties.register(BMItems.SENTIENT_AXE.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(BMDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT).ordinal());
            ItemProperties.register(BMItems.SENTIENT_PICKAXE.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(BMDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT).ordinal());
            ItemProperties.register(BMItems.SENTIENT_SHOVEL.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(BMDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT).ordinal());
            ItemProperties.register(BMItems.SENTIENT_SCYTHE.get(), NeoVitae.TYPE_PROPERTY, (stack, level, player, seed) -> stack.getOrDefault(BMDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT).ordinal());

            // Sentient sword active state - shows different texture when will is available
            ItemProperties.register(BMItems.SENTIENT_SWORD.get(), NeoVitae.rl("active"), (stack, level, entity, seed) -> {
                if (!(entity instanceof net.minecraft.world.entity.player.Player player)) return 0;
                double will = com.breakinblocks.neovitae.will.PlayerDemonWillHandler.getTotalDemonWill(
                        stack.getOrDefault(BMDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT), player);
                return will > 0 ? 1 : 0;
            });
        });
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Blood light projectile - uses NoopRenderer since it renders via particles in tick()
        event.registerEntityRenderer(BMEntities.BLOOD_LIGHT.get(), NoopRenderer::new);
        // Soul snare - thrown item renderer
        event.registerEntityRenderer(BMEntities.SOUL_SNARE.get(), ThrownItemRenderer::new);
        // Meteor - custom billboard renderer
        event.registerEntityRenderer(BMEntities.METEOR.get(), EntityMeteorRenderer::new);
        // Potion flask - thrown item renderer
        event.registerEntityRenderer(BMEntities.POTION_FLASK.get(), ThrownItemRenderer::new);
        // Shaped charge - renders the block state
        event.registerEntityRenderer(BMEntities.SHAPED_CHARGE.get(), EntityShapedChargeRenderer::new);
        // Throwing daggers - custom renderer for proper rotation when stuck in ground
        event.registerEntityRenderer(BMEntities.THROWING_DAGGER.get(), EntityThrowingDaggerRenderer::new);
        event.registerEntityRenderer(BMEntities.THROWING_DAGGER_SYRINGE.get(), EntityThrowingDaggerRenderer::new);
    }

    @SubscribeEvent
    public static void registerRenderLayer(EntityRenderersEvent.AddLayers event) {
        // Add custom elytra layer to all player skin variants
        for (PlayerSkin.Model model : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(model);
            if (renderer != null) {
                renderer.addLayer(new LivingElytraLayer<>(renderer, event.getEntityModels()));
            }
        }
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(BMMenus.ARC.get(), ARCScreen::new);
        event.register(BMMenus.TRAINER.get(), TrainerScreen::new);
        event.register(BMMenus.TELEPOSER.get(), TeleposerScreen::new);
        event.register(BMMenus.ALCHEMY_TABLE.get(), AlchemyTableScreen::new);
        event.register(BMMenus.SOUL_FORGE.get(), SoulForgeScreen::new);
        event.register(BMMenus.SIGIL_HOLDING.get(), SigilHoldingScreen::new);
        event.register(BMMenus.ROUTING_NODE.get(), RoutingNodeScreen::new);
        event.register(BMMenus.MASTER_ROUTING_NODE.get(), MasterRoutingNodeScreen::new);
        event.register(BMMenus.FILTER.get(), FilterScreen::new);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        FlaskColor flaskColor = new FlaskColor();
        event.register(flaskColor,
                BMItems.ALCHEMY_FLASK.get(),
                BMItems.ALCHEMY_FLASK_THROWABLE.get(),
                BMItems.ALCHEMY_FLASK_LINGERING.get());

        TippedDaggerColor tippedDaggerColor = new TippedDaggerColor();
        event.register(tippedDaggerColor, BMItems.THROWING_DAGGER_TIPPED.get());

        // Anointment items - tints layer 0 (alchemic_liquid) based on anointment color
        AnointmentColor anointmentColor = new AnointmentColor();
        BMItems.BASIC_ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof ItemAnointmentProvider)
                .forEach(holder -> event.register(anointmentColor, holder.get()));
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, NeoVitae.rl("demon_will_gauge"), new DemonWillGaugeOverlay());
    }
}