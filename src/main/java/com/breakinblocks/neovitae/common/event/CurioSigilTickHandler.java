package com.breakinblocks.neovitae.common.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.sigil.SigilItem;
import com.breakinblocks.neovitae.compat.curios.CuriosCompat;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class CurioSigilTickHandler {

    @SubscribeEvent
    public static void onPlayerTickPost(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || !CuriosCompat.isCuriosLoaded()) {
            return;
        }
        for (ItemStack curioStack : CuriosCompat.getCuriosInventory(player)) {
            if (curioStack.getItem() instanceof SigilItem sigil) {
                sigil.serverTickActive(curioStack, player.level(), player, -1, false);
            }
        }
    }
}
