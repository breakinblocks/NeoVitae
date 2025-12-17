package com.breakinblocks.neovitae.ritual.imperfect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.IImperfectRitualStone;
import com.breakinblocks.neovitae.ritual.ImperfectRitual;

/**
 * Imperfect ritual that summons a strong zombie.
 * Requires a coal block above the ritual stone.
 * The zombie has 1 armor (leather chestplate).
 * Costs 5000 LP.
 */
public class ImperfectRitualZombie extends ImperfectRitual {

    public ImperfectRitualZombie() {
        super("zombie",
                state -> state.is(Blocks.COAL_BLOCK),
                5000,
                true,
                "ritual." + NeoVitae.MODID + ".imperfect.zombie");
    }

    @Override
    public boolean onActivate(IImperfectRitualStone imperfectRitualStone, Player player) {
        Level level = imperfectRitualStone.getRitualWorld();
        if (level == null || level.isClientSide()) return false;

        if (level instanceof ServerLevel serverLevel) {
            // Spawn a zombie above the ritual stone
            Zombie zombie = EntityType.ZOMBIE.spawn(serverLevel,
                    imperfectRitualStone.getRitualPos().above(2),
                    MobSpawnType.TRIGGERED);

            if (zombie != null) {
                // Give it armor (leather chestplate = 1 armor point)
                zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
                // Make sure it doesn't despawn
                zombie.setPersistenceRequired();
            }
            return true;
        }

        return false;
    }
}
