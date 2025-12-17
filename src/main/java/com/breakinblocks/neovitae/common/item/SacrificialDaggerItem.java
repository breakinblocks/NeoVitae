package com.breakinblocks.neovitae.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import com.breakinblocks.neovitae.common.blockentity.BloodAltarTile;
import com.breakinblocks.neovitae.common.dataattachment.BMDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.effect.BMMobEffects;
import com.breakinblocks.neovitae.common.effect.SoulFrayEffect;
import com.breakinblocks.neovitae.common.event.SacrificialDaggerEvent;
import com.breakinblocks.neovitae.incense.IncenseHelper;
import com.breakinblocks.neovitae.util.AltarUtil;


public class SacrificialDaggerItem extends Item {
    public SacrificialDaggerItem() {
        super(new Properties().stacksTo(1).component(BMDataComponents.INCENSE, false));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player instanceof FakePlayer) {
            return super.use(level, player, hand);
        }

        boolean isCeremonial = player.getMainHandItem().getOrDefault(BMDataComponents.INCENSE, false);
        BlockPos altarPos = AltarUtil.findAltar(level, player.blockPosition(), 2);
        int healthSacrificed = 2;
        int lpAdded = AltarUtil.calculateSelfSacrificeLP(player, healthSacrificed);

        if (!player.getAbilities().instabuild) {
            // Check if player can perform ceremonial sacrifice (Soul Fray blocks it)
            if (isCeremonial && !SoulFrayEffect.canPerformCeremonialSacrifice(player)) {
                // Soul Fray is active - can only do normal sacrifice
                isCeremonial = false;
            }

            if (isCeremonial) {
                // Ceremonial sacrifice - sacrifice down to 10% health with incense bonus
                healthSacrificed = (int) (player.getHealth() - player.getMaxHealth() / 10F);
                double incenseBonus = player.getData(BMDataAttachments.INCENSE);
                lpAdded = AltarUtil.calculateSelfSacrificeLP(player, healthSacrificed, incenseBonus);
            }
            SacrificialDaggerEvent event = NeoForge.EVENT_BUS.post(new SacrificialDaggerEvent(player, true, true, healthSacrificed, lpAdded));
            if (event.isCanceled()) {
                return super.use(level, player, hand);
            }
            if (event.shouldDrainHealth) {
                player.invulnerableTime = 0;
                player.hurt(AltarUtil.sacrificeDamage(player), event.hpLost);
            }
            lpAdded = event.lpAdded;
            if (!event.shouldFillAltar) {
                return super.use(level, player, hand);
            }

            // After successful ceremonial sacrifice, apply Soul Fray and clear incense
            if (isCeremonial && !level.isClientSide()) {
                IncenseHelper.clearIncense(player);
                player.addEffect(new MobEffectInstance(BMMobEffects.SOUL_FRAY, SoulFrayEffect.DEFAULT_DURATION, 0));
            }
        } else if (player.isShiftKeyDown()) {
            lpAdded = Integer.MAX_VALUE;
        }

        double posX = player.getX();
        double posY = player.getY();
        double posZ = player.getZ();

        level.playSound(player, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat() * 0.8F));
        for (int i = 0; i < 8; i++) {
            level.addParticle(DustParticleOptions.REDSTONE, posX + level.random.nextDouble() - level.random.nextDouble(), posY + level.random.nextDouble() - level.random.nextDouble(), posZ + level.random.nextDouble() - level.random.nextDouble(), 0, 0, 0);
        }

        if (altarPos == null) {
            return super.use(level, player, hand);
        }
        BlockEntity be = level.getBlockEntity(altarPos);
        if (be instanceof BloodAltarTile altar) {
            altar.sacrificialDaggerCall(lpAdded, false);
        }

        return super.use(level, player, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            boolean state = stack.getOrDefault(BMDataComponents.INCENSE, false);
            boolean playerState = player.getData(BMDataAttachments.INCENSE) > 0;
            if (playerState && !state) {
                stack.set(BMDataComponents.INCENSE, true);
            } else if (!playerState && state) {
                stack.set(BMDataComponents.INCENSE, false);
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(BMDataComponents.INCENSE, false);
    }
}
