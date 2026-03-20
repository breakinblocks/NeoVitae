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
            Zombie zombie = EntityType.ZOMBIE.spawn(serverLevel,
                    imperfectRitualStone.getRitualPos().above(2),
                    MobSpawnType.TRIGGERED);

            if (zombie != null) {
                zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
                zombie.setPersistenceRequired();
            }
            return true;
        }

        return false;
    }
}
