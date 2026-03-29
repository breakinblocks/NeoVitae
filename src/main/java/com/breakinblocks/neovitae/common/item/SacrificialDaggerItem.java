package com.breakinblocks.neovitae.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import com.breakinblocks.neovitae.api.stream.StreamPresets;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.effect.SoulFrayEffect;
import com.breakinblocks.neovitae.common.event.SacrificialDaggerEvent;
import com.breakinblocks.neovitae.incense.IncenseHelper;
import com.breakinblocks.neovitae.util.AltarUtil;


public class SacrificialDaggerItem extends Item {
    // Cooldown tracking for stream visuals per player (server-side only).
    // Value is the game tick when the next stream can be sent.
    private static final Map<UUID, Long> streamCooldowns = new HashMap<>();

    public SacrificialDaggerItem() {
        super(new Properties().stacksTo(1).component(NVDataComponents.INCENSE, false));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        tooltip.add(net.minecraft.network.chat.Component.translatable("tooltip.neovitae.sacrificial_dagger.desc")
                .withStyle(net.minecraft.ChatFormatting.ITALIC, net.minecraft.ChatFormatting.DARK_RED));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player instanceof FakePlayer) {
            return super.use(level, player, hand);
        }

        boolean isCeremonial = player.getMainHandItem().getOrDefault(NVDataComponents.INCENSE, false);
        BlockPos altarPos = AltarUtil.findAltar(level, player.blockPosition(), 3);
        int healthSacrificed = 2;
        int evAdded = AltarUtil.calculateSelfSacrificeLP(player, healthSacrificed);

        if (!player.getAbilities().instabuild) {
            if (isCeremonial && !SoulFrayEffect.canPerformCeremonialSacrifice(player)) {
                isCeremonial = false;
            }

            if (isCeremonial) {
                healthSacrificed = (int) (player.getHealth() - player.getMaxHealth() / 10F);
                double incenseBonus = player.getData(NVDataAttachments.INCENSE);
                evAdded = AltarUtil.calculateSelfSacrificeLP(player, healthSacrificed, incenseBonus);
            }
            SacrificialDaggerEvent event = NeoForge.EVENT_BUS.post(new SacrificialDaggerEvent(player, true, true, healthSacrificed, evAdded));
            if (event.isCanceled()) {
                return super.use(level, player, hand);
            }
            if (event.shouldDrainHealth) {
                player.invulnerableTime = 0;
                player.hurt(AltarUtil.sacrificeDamage(player), event.hpLost);
            }
            evAdded = event.evAdded;

            // Blood drip particles at player's hand after self-sacrifice
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                double handY = player.getY() + player.getBbHeight() * 0.7;
                serverLevel.sendParticles(
                        new ColoredParticleOptions(NVParticles.BLOOD_DRIP.get(), 0x990011),
                        player.getX(), handY, player.getZ(),
                        3, 0.1, 0.05, 0.1, 0);
            }

            if (!event.shouldFillAltar) {
                return super.use(level, player, hand);
            }

            if (isCeremonial && !level.isClientSide()) {
                IncenseHelper.clearIncense(player);
                player.addEffect(new MobEffectInstance(NVMobEffects.SOUL_FRAY, SoulFrayEffect.DEFAULT_DURATION, 0));
            }
        } else if (player.isShiftKeyDown()) {
            evAdded = Integer.MAX_VALUE;
        }

        if (altarPos == null) {
            if (!level.isClientSide()) {
                player.displayClientMessage(Component.translatable("message.neovitae.too_far_from_altar"), true);
            }
            return super.use(level, player, hand);
        }
        BlockEntity be = level.getBlockEntity(altarPos);
        if (be instanceof AraVitaeTile altar) {
            altar.addSacrificeEV(evAdded, false);

            level.playSound(player, player.blockPosition(), SoundEvents.BEEHIVE_DRIP, SoundSource.PLAYERS, 0.6F, 0.8F + level.random.nextFloat() * 0.4F);

            // Send stream visual from player to altar (cooldown based on travel time)
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                long gameTime = level.getGameTime();
                UUID playerId = player.getUUID();
                long cooldownEnd = streamCooldowns.getOrDefault(playerId, 0L);

                if (gameTime >= cooldownEnd) {
                    double srcY = player.getY() + player.getBbHeight() * 0.75;
                    double dx = player.getX() - (altarPos.getX() + 0.5);
                    double dy = srcY - (altarPos.getY() + 0.5);
                    double dz = player.getZ() - (altarPos.getZ() + 0.5);
                    int travelTicks = Math.max(20, (int) (Math.sqrt(dx * dx + dy * dy + dz * dz) * 18));

                    StreamPresets.bloodTendril(player, altarPos)
                            .build()
                            .sendToNearby(serverLevel, altarPos, 128);
                    streamCooldowns.put(playerId, gameTime + travelTicks);
                }
            }
        }

        return super.use(level, player, hand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player) {
            boolean state = stack.getOrDefault(NVDataComponents.INCENSE, false);
            boolean playerState = player.getData(NVDataAttachments.INCENSE) > 0;
            if (playerState && !state) {
                stack.set(NVDataComponents.INCENSE, true);
            } else if (!playerState && state) {
                stack.set(NVDataComponents.INCENSE, false);
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.INCENSE, false);
    }
}
