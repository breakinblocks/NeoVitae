package com.breakinblocks.neovitae.common.event;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class CommonEventHandler {

    private static final Map<UUID, Double> bounceMap = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInteract(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();

        if (player instanceof FakePlayer)
            return;

        ItemStack held = event.getItemStack();
        if (held.isEmpty()) {
            return;
        }

        if (!(held.getItem() instanceof com.breakinblocks.neovitae.common.item.IBindable)) {
            return;
        }

        Binding binding = held.get(NVDataComponents.BINDING);
        GameProfile profile = event.getEntity().getGameProfile();

        if (binding == null || binding.isEmpty()) {
            Binding newBinding = new Binding(profile.getId(), profile.getName());
            if (NeoForge.EVENT_BUS.post(new ItemBindEvent(event.getEntity(), held)).isCanceled()) {
                return;
            }
            held.set(NVDataComponents.BINDING, newBinding);
        } else if (binding.uuid().equals(profile.getId()) && !Objects.equals(binding.name(), profile.getName())) {
            binding = new Binding(profile.getId(), profile.getName());
            held.set(NVDataComponents.BINDING, binding);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!FMLLoader.isProduction() && event.getEntity() instanceof ServerPlayer serverPlayer) {
            var server = serverPlayer.getServer();
            if (server != null && !server.getPlayerList().isOp(serverPlayer.getGameProfile())) {
                server.getPlayerList().op(serverPlayer.getGameProfile());
                NeoVitae.LOGGER.info("Auto-opped {} in dev environment", serverPlayer.getName().getString());
            }
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.hasEffect(NVMobEffects.HEAVY_HEART)) {
            int amp = entity.getEffect(NVMobEffects.HEAVY_HEART).getAmplifier() + 1;
            event.setDamageMultiplier(event.getDamageMultiplier() + amp);
            event.setDistance(event.getDistance() + amp);
        }

        if (entity.hasEffect(NVMobEffects.BOUNCE)) {
            if (entity instanceof Player player) {
                event.setDamageMultiplier(0);
                if (!player.isShiftKeyDown() && event.getDistance() > 1.5) {
                    if (player.level().isClientSide) {
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1, -1, 1));
                        bounceMap.put(player.getUUID(), player.getDeltaMovement().y());
                    } else {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTickPost(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (bounceMap.containsKey(player.getUUID())) {
            double motionY = bounceMap.remove(player.getUUID());
            player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1).add(0, motionY, 0));
        }
    }

    @SubscribeEvent
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        handleEffectRemoval(event.getEntity(), event.getEffectInstance());
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        handleEffectRemoval(event.getEntity(), event.getEffectInstance());
    }

    private static void handleEffectRemoval(LivingEntity entity, MobEffectInstance instance) {
        if (instance == null) return;

        if (instance.is(NVMobEffects.FLIGHT) && entity instanceof Player player) {
            player.getAbilities().flying = player.isCreative();
            player.getAbilities().setFlyingSpeed(0.05F);
            player.onUpdateAbilities();
        }

        if (instance.is(NVMobEffects.SUSPENDED)) {
            entity.setNoGravity(false);
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity living = event.getEntity();
        if (!event.getSource().is(DamageTypes.MAGIC) && living.hasEffect(NVMobEffects.OBSIDIAN_CLOAK)) {
            MobEffectInstance instance = living.getEffect(NVMobEffects.OBSIDIAN_CLOAK);
            float modifier = (float) (1 - 0.2 * (1 + instance.getAmplifier()));
            event.setAmount(event.getAmount() * Math.max(0, modifier));
        }
    }
}
