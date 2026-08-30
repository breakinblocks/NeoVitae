// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.dataattachment.DeadPetStorage;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import net.minecraft.world.entity.EntitySpawnReason;

public class AlchemyArrayEffectLoyalFriends extends AlchemyArrayEffect {

    private static final int ACTIVATION_TICK = 100;
    private static final int COMPLETION_TICK = 200;
    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide()) return false;
        if (!(level instanceof ServerLevel serverLevel)) return false;

        if (ticksActive < ACTIVATION_TICK) return false;

        if (ticksActive == ACTIVATION_TICK) {
            tile.doDropIngredients(true);
        }

        if (ticksActive < COMPLETION_TICK) return false;

        BlockPos pos = tile.getBlockPos();
        Player nearest = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 8, false);
        if (nearest == null) return true;

        int lpCost = getEvCost();
        Anima anima = AnimaHelper.getAnima(nearest);
        if (anima == null || anima.getCurrentEV() < lpCost) return true;

        if (lpCost > 0) anima.syphon(AnimaTicket.create(lpCost));

        summonLivingPets(serverLevel, nearest, pos);
        reviveDeadPets(serverLevel, nearest, pos);

        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel, EntitySpawnReason.TRIGGERED);
        if (bolt != null) {
            bolt.snapTo(Vec3.atBottomCenterOf(pos));
            bolt.setVisualOnly(true);
            serverLevel.addFreshEntity(bolt);
        }

        return true;
    }

    private void summonLivingPets(ServerLevel level, Player owner, BlockPos pos) {
        AABB searchBox = new AABB(pos).inflate(256);
        List<TamableAnimal> pets = level.getEntitiesOfClass(TamableAnimal.class, searchBox,
                pet -> pet.isTame() && pet.isOwnedBy(owner));

        for (TamableAnimal pet : pets) {
            pet.teleportTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            pet.setOrderedToSit(false);
        }
    }

    private void reviveDeadPets(ServerLevel level, Player owner, BlockPos pos) {
        DeadPetStorage storage = owner.getData(NVDataAttachments.DEAD_PET_STORAGE);
        if (storage.pets().isEmpty()) return;

        for (CompoundTag petData : storage.pets()) {
            Entity loaded = EntityType.loadEntityRecursive(petData, level, EntitySpawnReason.TRIGGERED, e -> e);
            if (loaded instanceof TamableAnimal pet) {
                if (pet.getType().getTags().anyMatch(t -> t.equals(NVTags.Entities.LOYAL_FRIENDS_BLACKLIST))) continue;
                pet.snapTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, level.getRandom().nextFloat() * 360, 0);
                pet.setHealth(pet.getMaxHealth());
                pet.setOrderedToSit(false);
                level.addFreshEntity(pet);
                clearCarriedItems(pet);
            }
        }

        owner.setData(NVDataAttachments.DEAD_PET_STORAGE.get(), DeadPetStorage.EMPTY);
    }

    private void clearCarriedItems(TamableAnimal pet) {
        ResourceHandler<ItemResource> handler = pet.getCapability(Capabilities.Item.ENTITY);
        if (handler == null) return;
        try (Transaction tx = Transaction.openRoot()) {
            for (int slot = 0; slot < handler.size(); slot++) {
                ItemResource resource = handler.getResource(slot);
                int amount = handler.getAmountAsInt(slot);
                if (amount > 0) {
                    handler.extract(slot, resource, amount, tx);
                }
            }
            tx.commit();
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {}

    @Override
    public void readFromNBT(CompoundTag tag) {}

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectLoyalFriends();
    }
}
