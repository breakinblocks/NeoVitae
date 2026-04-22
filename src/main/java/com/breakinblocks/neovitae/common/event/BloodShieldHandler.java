package com.breakinblocks.neovitae.common.event;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class BloodShieldHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if (!(player.getOffhandItem().getItem() instanceof BloodOrbItem)) return;
        if (!player.isUsingItem() || player.getUsedItemHand() != InteractionHand.OFF_HAND) return;

        Vec3 sourcePos = null;
        if (event.getSource().getDirectEntity() != null) {
            sourcePos = event.getSource().getDirectEntity().position();
        } else if (event.getSource().getSourcePosition() != null) {
            sourcePos = event.getSource().getSourcePosition();
        } else if (event.getSource().getEntity() != null) {
            sourcePos = event.getSource().getEntity().position();
        }

        if (sourcePos == null) return;

        Vec3 toSource = sourcePos.subtract(player.position());
        Vec3 horizontalToSource = new Vec3(toSource.x, 0, toSource.z).normalize();
        float yawRad = (float) Math.toRadians(player.getYRot());
        Vec3 horizontalLook = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad));
        double dot = horizontalLook.dot(horizontalToSource);

        if (dot < 0.0) return;

        event.setCanceled(true);

        if (event.getSource().getDirectEntity() instanceof Projectile projectile) {
            projectile.discard();
        }
    }
}
