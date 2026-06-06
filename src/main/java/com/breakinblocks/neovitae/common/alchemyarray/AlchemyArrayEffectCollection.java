// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.capabilities.Capabilities;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.particle.NVParticles;

import java.util.List;

public class AlchemyArrayEffectCollection extends AlchemyArrayEffect {

    private static final double RADIUS = 2.0;
    private static final double PULL_SPEED = 0.15;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide) return false;

        BlockPos pos = tile.getBlockPos();
        Vec3 center = Vec3.atCenterOf(pos);
        AABB area = new AABB(pos).inflate(RADIUS);

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
        if (items.isEmpty()) return false;

        BlockPos belowPos = pos.below();
        IItemHandler inventory = level.getCapability(Capabilities.ItemHandler.BLOCK, belowPos, null);

        for (ItemEntity itemEntity : items) {
            if (itemEntity.isRemoved()) continue;

            Vec3 dir = center.subtract(itemEntity.position());
            double dist = dir.length();

            if (dist < 0.5 && inventory != null) {
                ItemStack stack = itemEntity.getItem().copy();
                ItemStack remainder = insertIntoInventory(inventory, stack);
                if (remainder.isEmpty()) {
                    itemEntity.discard();
                } else {
                    itemEntity.setItem(remainder);
                }
            } else if (dist > 0.2) {
                Vec3 pull = dir.normalize().scale(Math.min(PULL_SPEED, dist * 0.2));
                itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add(pull));
            }
        }

        if (ticksActive % 20 == 0 && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0x22CC44),
                    center.x, center.y + 0.3, center.z, 4, RADIUS * 0.3, 0.1, RADIUS * 0.3, 0.01);
        }

        return false;
    }

    private ItemStack insertIntoInventory(IItemHandler handler, ItemStack stack) {
        ItemStack remaining = stack;
        for (int i = 0; i < handler.getSlots() && !remaining.isEmpty(); i++) {
            remaining = handler.insertItem(i, remaining, false);
        }
        return remaining;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {}

    @Override
    public void readFromNBT(CompoundTag tag) {}

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectCollection();
    }
}
