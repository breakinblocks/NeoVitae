package com.breakinblocks.neovitae.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.FakePlayer;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.attribute.NVAttributes;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.damagesource.NVDamageSources;
import com.breakinblocks.neovitae.common.datamap.EntitySacrificeHelper;

public class DaggerOfSacrificeItem extends Item {

    public DaggerOfSacrificeItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.translatable("tooltip.neovitae.dagger_of_sacrifice.desc")
                .withStyle(net.minecraft.ChatFormatting.ITALIC, net.minecraft.ChatFormatting.DARK_RED));
    }

    /**
     * Called BEFORE Player.attack() processes. If sacrifice conditions are met,
     * silence the entity so the melee hit doesn't play a hurt sound.
     */
    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (player.level().isClientSide()) return false;
        if (!(entity instanceof LivingEntity target)) return false;
        if (player instanceof FakePlayer) return false;
        if (target instanceof Player) return false;
        if (target.getHealth() < 0.5F) return false;

        int sacrificeValue = getSacrificeValue(target);
        if (sacrificeValue <= 0) return false;

        BlockPos altarPos = findAltar(target.level(), target.blockPosition());
        if (altarPos == null) return false;

        // Sacrifice conditions met - silence before the melee damage processes
        target.setSilent(true);
        return false; // let the attack proceed (hurtEnemy will handle the sacrifice)
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof Player player)) {
            return false;
        }

        if (player instanceof FakePlayer) {
            return false;
        }

        if (player.level().isClientSide()) {
            return false;
        }

        if (target instanceof Player) {
            return false;
        }

        if (target.getHealth() < 0.5F) {
            return false;
        }

        int sacrificeValue = getSacrificeValue(target);
        if (sacrificeValue <= 0) {
            return false;
        }

        int ev = (int) (sacrificeValue * target.getHealth());

        if (target.isBaby()) {
            ev = (int) (ev * 0.5F);
        }

        double bonusSacrifice = player.getAttributeValue(NVAttributes.BONUS_SACRIFICE);
        if (bonusSacrifice > 0) {
            ev = (int) (ev * (1 + bonusSacrifice / 100));
        }

        BlockPos altarPos = findAltar(target.level(), target.blockPosition());
        if (altarPos == null) {
            target.setSilent(false); // restore if we silenced in onLeftClickEntity
            player.displayClientMessage(Component.translatable("message.neovitae.too_far_from_altar"), true);
            return false;
        }

        BlockEntity be = target.level().getBlockEntity(altarPos);
        if (!(be instanceof AraVitaeTile altar)) {
            target.setSilent(false);
            return false;
        }

        altar.addSacrificeEV(ev, true);

        // Send blood tendril from the sacrificed entity to the altar
        if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            StreamPresets.bloodTendril(target, altarPos)
                    .build()
                    .sendToNearby(serverLevel, altarPos, 128);
        }

        // Kill the entity (still silent from onLeftClickEntity, suppresses death sound too)
        Level level = target.level();
        target.hurt(target.level().damageSources().source(NVDamageSources.SACRIFICE, player), Float.MAX_VALUE);

        level.playSound(null, target.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.BLOCKS, 0.3F, 1.0F);
        level.playSound(null, target.blockPosition(), SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, 0.6F, 0.8F + level.random.nextFloat() * 0.4F);

        return true;
    }

    private int getSacrificeValue(LivingEntity entity) {
        return EntitySacrificeHelper.getEvPerDamage(entity);
    }

    private BlockPos findAltar(Level level, BlockPos pos) {
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 1; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos testPos = pos.offset(x, y, z);
                    BlockEntity be = level.getBlockEntity(testPos);
                    if (be instanceof AraVitaeTile) {
                        return testPos;
                    }
                }
            }
        }
        return null;
    }
}
