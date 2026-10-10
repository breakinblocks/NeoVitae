package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.IMasterRitualStone;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualHelper;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

public class RitualDeusArmourEvolve extends RitualSentientArmourEvolve {

    public static final String NAME = "deus_armour_evolve";
    public static final int DEUS_MAX_UPGRADE_POINTS = MAX_UPGRADE_POINTS + POINTS_PER_EVOLUTION;

    private static final List<EquipmentSlot> ARMOR_SLOTS =
            List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

    public RitualDeusArmourEvolve() {
        super(NAME, 2, 500000);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) {
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }

        AABB checkArea = new AABB(ctx.masterPos()).inflate(1, 2, 1);
        List<Player> players = ctx.level().getEntitiesOfClass(Player.class, checkArea);

        for (Player player : players) {
            ItemStack chestpiece = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chestpiece.isEmpty() || !chestpiece.is(NVTags.Items.SENTIENT_SET)) {
                continue;
            }

            int currentMaxPoints = chestpiece.getOrDefault(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS.get(),
                    NeoVitae.SERVER_CONFIG.DEFAULT_UPGRADE_POINTS.get());
            if (currentMaxPoints < MAX_UPGRADE_POINTS) {
                player.sendOverlayMessage(Component.translatable("chat." + NeoVitae.MODID + ".deus_armour_evolve.unready", MAX_UPGRADE_POINTS));
                masterRitualStone.stopRitual(BreakType.DEACTIVATE);
                return;
            }

            boolean evolved = currentMaxPoints < DEUS_MAX_UPGRADE_POINTS;
            boolean mended = applyBloodMending(player);
            if (!evolved && !mended) {
                player.sendOverlayMessage(Component.translatable("chat." + NeoVitae.MODID + ".deus_armour_evolve.maxed"));
                masterRitualStone.stopRitual(BreakType.DEACTIVATE);
                return;
            }

            if (evolved) {
                chestpiece.set(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS.get(), DEUS_MAX_UPGRADE_POINTS);
                player.sendOverlayMessage(Component.translatable("chat." + NeoVitae.MODID + ".deus_armour_evolve.evolved", DEUS_MAX_UPGRADE_POINTS));
            } else {
                player.sendOverlayMessage(Component.translatable("chat." + NeoVitae.MODID + ".deus_armour_evolve.mended"));
            }

            StreamPresets.soulSiphon(player, ctx.masterPos()).build()
                    .sendToNearby(ctx.serverLevel(), ctx.masterPos(), 128);

            ctx.syphon(getRefreshCost());
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }
    }

    private static boolean applyBloodMending(Player player) {
        boolean changed = false;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack piece = player.getItemBySlot(slot);
            if (piece.isEmpty() || !piece.is(NVTags.Items.SENTIENT_SET)) continue;
            if (piece.is(NVTags.Items.BLOOD_MENDING_BLACKLIST)) continue;
            if (piece.has(NVDataComponents.BLOOD_MENDING.get())) continue;
            piece.set(NVDataComponents.BLOOD_MENDING.get(), true);
            changed = true;
        }
        return changed;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addDeusCrown(components, super::gatherComponents);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualDeusArmourEvolve();
    }
}
