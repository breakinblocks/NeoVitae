package com.breakinblocks.neovitae.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.soul.LexVitaeItem;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

@EventBusSubscriber(modid = NeoVitae.MODID)
public final class LexVitaeAoeHandler {

    private static final ThreadLocal<Boolean> AOE_BREAKING = ThreadLocal.withInitial(() -> false);

    public static boolean isAoeBreaking() {
        return AOE_BREAKING.get();
    }

    private LexVitaeAoeHandler() {}

    @SubscribeEvent
    public static void onBlockBreak(BreakBlockEvent event) {
        if (AOE_BREAKING.get()) return;
        Player player = event.getPlayer();
        if (player == null) return;
        if (!player.isWithinBlockInteractionRange(event.getPos(), 1.0)) return;
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof LexVitaeItem)) return;
        if (!LexVitaeItem.isActive(stack)) return;
        if (LexVitaeItem.isBeaming(player)) return;

        if (LexVitaeItem.attackedRecently(player)) {
            event.setCanceled(true);
            return;
        }

        int radius = stack.getOrDefault(NVDataComponents.LEX_RADIUS.get(), 0);
        if (radius == 0) return;

        Level level = (Level) event.getLevel();
        BlockPos center = event.getPos();
        if (event.getState().getDestroySpeed(level, center) <= 0) return;

        int half = radius == 1 ? 1 : 2;
        Direction.Axis depth = dominantAxis(player.getLookAngle());

        AOE_BREAKING.set(true);
        try {
            for (int u = -half; u <= half; u++) {
                for (int v = -half; v <= half; v++) {
                    if (u == 0 && v == 0) continue;
                    BlockPos pos = switch (depth) {
                        case X -> center.offset(0, u, v);
                        case Y -> center.offset(u, 0, v);
                        case Z -> center.offset(u, v, 0);
                    };
                    BlockState state = level.getBlockState(pos);
                    if (state.isAir()) continue;
                    if (state.getDestroySpeed(level, pos) < 0) continue;
                    if (!stack.isCorrectToolForDrops(state)) continue;
                    if (BlockProtectionHelper.tryBreakBlock(level, pos, player)) {
                        stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                        if (stack.isEmpty()) return;
                    }
                }
            }
        } finally {
            AOE_BREAKING.set(false);
        }
    }

    private static Direction.Axis dominantAxis(Vec3 look) {
        double ax = Math.abs(look.x);
        double ay = Math.abs(look.y);
        double az = Math.abs(look.z);
        if (ay >= ax && ay >= az) return Direction.Axis.Y;
        if (ax >= az) return Direction.Axis.X;
        return Direction.Axis.Z;
    }
}
