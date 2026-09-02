package com.breakinblocks.neovitae.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import com.breakinblocks.neovitae.common.network.LexBeamPayload;
import com.breakinblocks.neovitae.common.network.LexCycleRadiusPayload;
import com.breakinblocks.neovitae.common.network.LexModeCyclePayload;
import com.breakinblocks.neovitae.common.network.NVPayloads;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.soul.LexVitaeItem;
import com.breakinblocks.neovitae.common.particle.NVParticles;

@EventBusSubscriber(modid = NeoVitae.MODID, value = Dist.CLIENT)
public final class LexVitaeBeamHandler {

    private static final double STEP = 1.1;
    private static final double WOBBLE_AMPLITUDE = 0.12;
    private static final double WOBBLE_FREQUENCY = 1.4;
    private static final double AOE_BRANCH_STEP = 1.6;

    private static boolean lastFiring = false;
    private static boolean lastHoldingActive = false;
    private static boolean beamArmed = true;

    public static int beamColor(SpiritusType type) {
        return switch (type) {
            case RAW -> 0x66E6E6;
            case RUINA -> 0x55EE55;
            case NIHILUM -> 0xFF8844;
            case VINDICTA -> 0xBB55EE;
            case INVICTUS -> 0xEEEEEE;
        };
    }

    private LexVitaeBeamHandler() {}

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!mc.player.isShiftKeyDown()) return;
        ItemStack held = mc.player.getMainHandItem();
        if (!(held.getItem() instanceof LexVitaeItem)) return;
        if (!LexVitaeItem.isActive(held)) return;
        double delta = event.getScrollDeltaY();
        if (delta == 0) return;
        NVPayloads.sendToServer(new LexCycleRadiusPayload(delta < 0 ? 1 : -1));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        if (!lastFiring) return;
        var input = event.getInput();
        input.forwardImpulse *= 5.0F;
        input.leftImpulse *= 5.0F;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Player player = mc.player;
        ItemStack held = player.getMainHandItem();
        boolean holdingActiveLex = held.getItem() instanceof LexVitaeItem && LexVitaeItem.isActive(held);

        boolean cycledMode = false;
        while (ClientModEventHandler.LEX_MODE.consumeClick()) {
            cycledMode = true;
        }
        if (cycledMode && holdingActiveLex) {
            NVPayloads.sendToServer(LexModeCyclePayload.INSTANCE);
        }

        boolean beamKeyDown = ClientModEventHandler.LEX_BEAM.isDown();
        if (!beamKeyDown) {
            beamArmed = true;
        } else if (holdingActiveLex && !lastHoldingActive) {
            beamArmed = false;
        }
        lastHoldingActive = holdingActiveLex;

        boolean firing = holdingActiveLex && beamKeyDown && beamArmed;
        if (firing != lastFiring) {
            NVPayloads.sendToServer(new LexBeamPayload(firing));
            lastFiring = firing;
        }
        if (!firing) return;

        Vec3 origin = player.getEyePosition();
        Vec3 dir = player.getLookAngle();
        Vec3 reach = origin.add(dir.scale(LexVitaeItem.BEAM_RANGE));
        Vec3 endpoint = reach;

        BlockHitResult bhr = mc.level.clip(new ClipContext(origin, reach,
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (bhr.getType() == HitResult.Type.BLOCK) {
            endpoint = bhr.getLocation();
        }

        Vec3 hand = handPosition(player);
        Vec3 path = endpoint.subtract(hand);
        double dist = path.length();
        if (dist < 0.5) return;

        Vec3 forward = path.scale(1.0 / dist);
        Vec3 perpA = forward.cross(new Vec3(0, 1, 0));
        if (perpA.lengthSqr() < 1e-6) perpA = new Vec3(1, 0, 0);
        else perpA = perpA.normalize();
        Vec3 perpB = forward.cross(perpA).normalize();

        float t = player.tickCount + mc.getTimer().getGameTimeDeltaPartialTick(false);
        SpiritusType type = held.getOrDefault(NVDataComponents.SPIRITUS_TYPE.get(), SpiritusType.RAW);
        int color = beamColor(type);
        var glow = new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), color, true);

        for (double d = 0.4; d <= dist; d += STEP) {
            double phase = d * WOBBLE_FREQUENCY + t * 0.3;
            double offA = Math.sin(phase) * WOBBLE_AMPLITUDE;
            double offB = Math.cos(phase * 1.3) * WOBBLE_AMPLITUDE;
            Vec3 p = hand.add(forward.scale(d))
                    .add(perpA.scale(offA))
                    .add(perpB.scale(offB));
            mc.level.addParticle(glow, p.x, p.y, p.z, 0, 0, 0);
        }

        int radius = held.getOrDefault(NVDataComponents.LEX_RADIUS.get(), 0);
        if (radius > 0) {
            int half = radius == 1 ? 1 : 2;
            float burstCycle = 6.0F;
            int markerStride = radius == 2 ? 3 : 2;

            for (int u = -half; u <= half; u++) {
                for (int v = -half; v <= half; v++) {
                    if (u == 0 && v == 0) continue;
                    if (radius == 2 && ((u + v) & 1) != 0) continue;

                    Vec3 aoeTarget = endpoint.add(perpA.scale(u)).add(perpB.scale(v));
                    if (((u + v + (int) (t / markerStride)) & 1) == 0) {
                        mc.level.addParticle(glow, aoeTarget.x, aoeTarget.y, aoeTarget.z, 0, 0, 0);
                    }

                    Vec3 branch = aoeTarget.subtract(endpoint);
                    double bdist = branch.length();
                    if (bdist < 0.1) continue;
                    Vec3 bdir = branch.scale(1.0 / bdist);
                    double phase = (t + (u * 1.7F + v * 2.3F)) % burstCycle;
                    if (phase < 0) phase += burstCycle;
                    double frac = phase / burstCycle;
                    if (frac > 1.0) continue;
                    Vec3 p = endpoint.add(bdir.scale(frac * bdist));
                    mc.level.addParticle(glow, p.x, p.y, p.z, 0, 0, 0);
                }
            }
        }
    }

    private static Vec3 handPosition(Player player) {
        float yaw = player.getYRot() * ((float) Math.PI / 180F);
        float pitch = player.getXRot() * ((float) Math.PI / 180F);
        double sideX = -Math.cos(yaw) * 0.4;
        double sideZ = -Math.sin(yaw) * 0.4;
        double forwardX = -Math.sin(yaw) * Math.cos(pitch) * 0.6;
        double forwardZ = Math.cos(yaw) * Math.cos(pitch) * 0.6;
        double forwardY = -Math.sin(pitch) * 0.6;
        return new Vec3(
                player.getX() + sideX + forwardX,
                player.getEyeY() - 0.1 + forwardY,
                player.getZ() + sideZ + forwardZ);
    }
}
