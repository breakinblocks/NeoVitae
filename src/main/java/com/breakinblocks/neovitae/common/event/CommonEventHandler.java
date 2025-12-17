package com.breakinblocks.neovitae.common.event;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;

import java.util.Objects;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class CommonEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInteract(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();

        if (player instanceof FakePlayer)
            return;

        ItemStack held = event.getItemStack();
        if (held.isEmpty()) {
            return;
        }

        // Only handle bindable items
        if (!(held.getItem() instanceof com.breakinblocks.neovitae.common.item.IBindable)) {
            return;
        }

        Binding binding = held.get(BMDataComponents.BINDING);
        GameProfile profile = event.getEntity().getGameProfile();

        // Bind if no binding exists or binding is empty
        if (binding == null || binding.isEmpty()) {
            Binding newBinding = new Binding(profile.getId(), profile.getName());
            if (NeoForge.EVENT_BUS.post(new ItemBindEvent(event.getEntity(), held)).isCanceled()) {
                return;
            }
            held.set(BMDataComponents.BINDING, newBinding);
        } else if (binding.uuid().equals(profile.getId()) && !Objects.equals(binding.name(), profile.getName())) {
            // Update name if UUID matches but name changed
            binding = new Binding(profile.getId(), profile.getName());
            held.set(BMDataComponents.BINDING, binding);
        }
    }

    // Auto-op players in dev environment
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

    // Note: Ritual diviner cycling is handled by ItemRitualDiviner.onEntitySwing()
    // with proper cooldown to prevent duplicate triggers
}
