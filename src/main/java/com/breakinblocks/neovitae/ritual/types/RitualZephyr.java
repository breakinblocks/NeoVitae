package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.ExperienceTomeItem;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.Utils;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual that collects items and XP orbs, pulling them toward the ritual owner.
 * Items within 2 blocks of the master stone are collected into a chest if available.
 */
public class RitualZephyr extends Ritual {

    public static final String ZEPHYR_RANGE = "zephyrRange";
    public static final String CHEST_RANGE = "chestRange";

    public RitualZephyr() {
        super("zephyr", 0, 1000, "ritual." + NeoVitae.MODID + ".zephyr");
        addBlockRange(ZEPHYR_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-20, -10, -20), 41, 21, 41));
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        setMaximumVolumeAndDistanceOfRange(ZEPHYR_RANGE, 50000, 30, 30);
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 5, 5);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        UUID owner = ctx.master().getOwner();
        if (owner == null) return;

        Player ownerPlayer = ctx.level().getPlayerByUUID(owner);
        if (ownerPlayer == null) return;

        AreaDescriptor range = RitualHelper.getEffectiveRange(ctx.master(), this, ZEPHYR_RANGE);
        AABB aabb = range.getAABB(ctx.masterPos());
        Vec3 target = ownerPlayer.position();
        Vec3 masterCenter = Vec3.atCenterOf(ctx.masterPos());

        BlockPos chestPos = RitualHelper.firstPositionInRange(ctx.master(), this, CHEST_RANGE, ctx.masterPos()).orElse(null);
        BlockEntity chestTile = chestPos != null ? ctx.level().getBlockEntity(chestPos) : null;
        boolean hasChest = chestTile != null && Utils.getNumberOfFreeSlots(chestTile, Direction.DOWN) >= 1;

        List<ItemEntity> items = ctx.level().getEntitiesOfClass(ItemEntity.class, aabb);
        int entitiesMoved = 0;

        for (ItemEntity item : items) {
            if (item.isRemoved()) continue;

            Vec3 itemPos = item.position();
            double distanceToMaster = itemPos.distanceTo(masterCenter);

            if (distanceToMaster <= 2.0 && hasChest) {
                ItemStack remainder = Utils.insertStackIntoTile(item.getItem().copy(), chestTile, Direction.DOWN);
                if (remainder.isEmpty()) {
                    item.discard();
                } else {
                    item.setItem(remainder);
                }
                entitiesMoved++;
                continue;
            }

            // Pull toward master stone when chest exists so items converge on it
            Vec3 pullTarget = hasChest ? masterCenter : target;
            double distance = itemPos.distanceTo(pullTarget);

            if (distance > 2.0) {
                Vec3 direction = pullTarget.subtract(itemPos).normalize().scale(0.4);
                item.setDeltaMovement(item.getDeltaMovement().add(direction));
                entitiesMoved++;
            }
        }

        IItemHandler inventory = ctx.level().getCapability(Capabilities.ItemHandler.BLOCK, chestPos, null);
        ItemStack experienceTome = findExperienceTome(inventory);

        List<ExperienceOrb> orbs = ctx.level().getEntitiesOfClass(ExperienceOrb.class, aabb);
        for (ExperienceOrb orb : orbs) {
            if (orb.isRemoved()) continue;

            if (!experienceTome.isEmpty()) {
                ExperienceTomeItem.addXpToTome(experienceTome, orb.getValue());
                orb.discard();
                entitiesMoved++;
                continue;
            }

            Vec3 orbPos = orb.position();
            double distance = orbPos.distanceTo(target);

            if (distance > 2.0) {
                Vec3 direction = target.subtract(orbPos).normalize().scale(0.5);
                orb.setDeltaMovement(orb.getDeltaMovement().add(direction));
                entitiesMoved++;
            }
        }

        if (entitiesMoved > 0) {
            int cost = Math.min(getRefreshCost() + (entitiesMoved / 10), ctx.currentEV());
            ctx.syphon(cost);
        }
    }

    private ItemStack findExperienceTome(IItemHandler inventory) {
        if (inventory == null || inventory.getSlots() == 0) {
            return ItemStack.EMPTY;
        }

        ItemStack firstSlot = inventory.getStackInSlot(0);
        if (firstSlot.is(NVItems.EXPERIENCE_TOME.get())) {
            return firstSlot;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public int getRefreshTime() {
        return 5;
    }

    @Override
    public int getRefreshCost() {
        return 2;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.AIR);
        addParallelRunes(components, 2, 0, EnumRuneType.AIR);
        addCornerRunes(components, 2, 0, EnumRuneType.WATER);
        addParallelRunes(components, 3, 0, EnumRuneType.AIR);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualZephyr();
    }
}
