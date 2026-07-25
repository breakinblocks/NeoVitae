package com.breakinblocks.neovitae.common.event;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import com.breakinblocks.neovitae.common.entity.mob.IDaemonium;
import com.breakinblocks.neovitae.common.item.soul.LexVitaeItem;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.core.BlockPos;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectVortex;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonVariant;
import com.breakinblocks.neovitae.common.blockentity.DungeonControllerBlockEntity;
import com.breakinblocks.neovitae.common.dataattachment.DeadPetStorage;
import com.breakinblocks.neovitae.common.dataattachment.DungeonExitData;
import com.breakinblocks.neovitae.common.dataattachment.NVDataAttachments;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.dimension.DungeonDimensionHelper;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.common.item.IBindable;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.block.dungeon.BlockPrismaticDemonite;
import com.breakinblocks.neovitae.common.material.MaterialRegistry;
import com.breakinblocks.neovitae.util.ChatUtil;
import com.breakinblocks.neovitae.spiritus.SpiritusHelper;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class CommonEventHandler {

    private static final Map<UUID, Double> bounceMap = new HashMap<>();
    private static final Map<UUID, Integer> dungeonGracePeriod = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInteract(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();

        if (player instanceof FakePlayer)
            return;

        ItemStack held = event.getItemStack();
        if (held.isEmpty()) {
            return;
        }

        if (!(held.getItem() instanceof IBindable)) {
            return;
        }

        Binding binding = held.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
        GameProfile profile = event.getEntity().getGameProfile();

        if (binding.isEmpty()) {
            Binding newBinding = new Binding(profile.getId(), profile.getName());
            if (NeoForge.EVENT_BUS.post(new ItemBindEvent(event.getEntity(), held)).isCanceled()) {
                return;
            }
            held.set(NVDataComponents.BINDING, newBinding);
            notifyBound(event, profile.getName());
            if (held.getItem() instanceof BloodOrbItem) {
                event.setCanceled(true);
            }
        } else if (binding.uuid().equals(profile.getId()) && !Objects.equals(binding.name(), profile.getName())) {
            binding = new Binding(profile.getId(), profile.getName());
            held.set(NVDataComponents.BINDING, binding);
        }
    }

    private static void notifyBound(PlayerInteractEvent.RightClickItem event, String name) {
        if (event.getLevel().isClientSide()) return;
        Player player = event.getEntity();
        player.displayClientMessage(
                Component.translatable("chat.neovitae.binding.bound", name).withStyle(ChatFormatting.DARK_RED), true);
        event.getLevel().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 0.7f, 0.6f);
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL, player.getX(), player.getY() + 1.0, player.getZ(),
                    14, 0.25, 0.4, 0.25, 0.02);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!FMLLoader.isProduction() && event.getEntity() instanceof ServerPlayer serverPlayer) {
            var server = serverPlayer.getServer();
            if (server != null && !server.getPlayerList().isOp(serverPlayer.getGameProfile())) {
                server.getPlayerList().op(serverPlayer.getGameProfile());
                NeoVitae.LOGGER.info("Auto-opped {} in dev environment", serverPlayer.getName().getString());
            }
        }

        if (event.getEntity() instanceof ServerPlayer player
                && MaterialRegistry.hasPendingRestartNotice()) {
            player.sendSystemMessage(Component.translatable("message.neovitae.materials.generated")
                    .withStyle(ChatFormatting.GOLD));
            player.sendSystemMessage(Component.translatable("message.neovitae.materials.restart_required")
                    .withStyle(ChatFormatting.YELLOW));
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.hasEffect(NVMobEffects.HEAVY_HEART)) {
            int amp = entity.getEffect(NVMobEffects.HEAVY_HEART).getAmplifier() + 1;
            event.setDamageMultiplier(event.getDamageMultiplier() + amp);
            event.setDistance(event.getDistance() + amp);
        }

        if (entity.hasEffect(NVMobEffects.BOUNCE)) {
            if (entity instanceof Player player) {
                event.setDamageMultiplier(0);
                if (!player.isShiftKeyDown() && event.getDistance() > 1.5) {
                    if (player.level().isClientSide) {
                        player.setDeltaMovement(player.getDeltaMovement().multiply(1, -1, 1));
                        bounceMap.put(player.getUUID(), player.getDeltaMovement().y());
                    } else {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTickPost(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (bounceMap.containsKey(player.getUUID())) {
            double motionY = bounceMap.remove(player.getUUID());
            player.setDeltaMovement(player.getDeltaMovement().multiply(1, 0, 1).add(0, motionY, 0));
        }

        if (player instanceof ServerPlayer serverPlayer
                && DungeonDimensionHelper.isDungeonDimension(player.level())) {
            int grace = dungeonGracePeriod.getOrDefault(player.getUUID(), 0);
            if (grace > 0) {
                dungeonGracePeriod.put(player.getUUID(), grace - 1);
                return;
            }
            if (player.getY() < player.level().getMinBuildHeight()) {
                NeoVitae.LOGGER.warn("Booting player {} below the dungeon floor at Y={}",
                        player.getName().getString(), player.getY());
                bootPlayerFromDungeon(serverPlayer);
            } else if (player.tickCount % 20 == 0) {
                DungeonExitData exitData = serverPlayer.getData(NVDataAttachments.DUNGEON_EXIT);
                BlockPos playerPos = player.blockPosition();
                BlockPos ctrlPos = exitData.controllerPos()
                        .orElseGet(() -> DungeonDimensionHelper.controllerPosForLocation(playerPos));
                if (player.level().getBlockEntity(ctrlPos) instanceof DungeonControllerBlockEntity controller) {
                    boolean inBounds = controller.getDungeonSynthesizer().isBlockNearDescriptor(playerPos, 5)
                            || controller.getDungeonSynthesizer().isBlockNearDescriptor(playerPos.above(), 5)
                            || controller.getDungeonSynthesizer().isBlockNearDescriptor(playerPos.above(2), 5);
                    if (inBounds) {
                        if (exitData.controllerPos().isEmpty()) {
                            serverPlayer.setData(NVDataAttachments.DUNGEON_EXIT.get(), exitData.withControllerPos(ctrlPos));
                        }
                    } else {
                        NeoVitae.LOGGER.warn("Ejecting player {} at {} from dungeon. Controller at {}. Descriptors: {}",
                                player.getName().getString(), playerPos, ctrlPos,
                                controller.getDungeonSynthesizer().getDescriptorList().size());
                        bootPlayerFromDungeon(serverPlayer);
                    }
                } else {
                    NeoVitae.LOGGER.warn("No controller BE found at {} for player {}. Ejecting.",
                            ctrlPos, player.getName().getString());
                    bootPlayerFromDungeon(serverPlayer);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEnderTeleport(EntityTeleportEvent.EnderEntity event) {
        if (AlchemyArrayEffectVortex.isTeleportSuppressed(event.getEntityLiving())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onDaemoniumTick(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof IDaemonium && event.getEntity() instanceof Mob mob && mob.hasRestriction()) {
            mob.restrictTo(BlockPos.ZERO, -1);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof Level level)) return;
        Block block = event.getState().getBlock();

        if (block instanceof BlockPrismaticDemonite) {
            if (level.isClientSide() || event.getPlayer().isCreative()) return;
            ItemStack drop = BlockPrismaticDemonite.getRandomRawOre(level);
            if (!drop.isEmpty()) {
                Block.popResource(level, event.getPos(), drop);
            }
            if (level.random.nextInt(100) + 1 <= BlockPrismaticDemonite.DEPLETE_THRESHOLD) {
                level.setBlock(event.getPos(),
                        DungeonBlocks.DUNGEON_STONE.get(DungeonVariant.RAW).block().get().defaultBlockState(),
                        Block.UPDATE_ALL);
            }
            event.setCanceled(true);
            return;
        }

        if (DungeonDimensionHelper.isDungeonDimension(level)
                && !event.getPlayer().isCreative()
                && block != DungeonBlocks.DUNGEON_ORE.block().get()
                && ((DungeonBlocks.isDungeonBlock(block) && !DungeonBlocks.isDungeonStone(block))
                    || block == NVBlocks.MASTER_RITUAL_STONE.block().get()
                    || block == NVBlocks.INVERTED_MASTER_RITUAL_STONE.block().get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player player && player.isCreative()) return;
        if (event.getLevel() instanceof Level level && DungeonDimensionHelper.isDungeonDimension(level)) {
            if (DungeonBlocks.isDungeonBlock(event.getPlacedBlock().getBlock())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEffectRemove(MobEffectEvent.Remove event) {
        handleEffectRemoval(event.getEntity(), event.getEffectInstance());
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        handleEffectRemoval(event.getEntity(), event.getEffectInstance());
    }

    public static void setDungeonGracePeriod(Player player, int ticks) {
        dungeonGracePeriod.put(player.getUUID(), ticks);
    }

    private static void bootPlayerFromDungeon(ServerPlayer serverPlayer) {
        DungeonExitData exitData = serverPlayer.getData(NVDataAttachments.DUNGEON_EXIT);
        if (exitData.isValid()) {
            DungeonDimensionHelper.teleportFromDungeon(serverPlayer, exitData.getExitPosOrNull(), exitData.getExitDimensionOrNull());
        } else {
            BlockPos spawnPos = serverPlayer.getRespawnPosition();
            if (spawnPos == null) {
                spawnPos = serverPlayer.server.overworld().getSharedSpawnPos();
            }
            DungeonDimensionHelper.teleportToOverworld(serverPlayer, spawnPos);
        }
    }

    private static void handleEffectRemoval(LivingEntity entity, MobEffectInstance instance) {
        if (instance == null) return;

        if (instance.is(NVMobEffects.FLIGHT) && entity instanceof Player player) {
            player.getAbilities().flying = player.isCreative();
            player.getAbilities().setFlyingSpeed(0.05F);
            player.onUpdateAbilities();
        }

        if (instance.is(NVMobEffects.SUSPENDED)) {
            entity.setNoGravity(false);
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity living = event.getEntity();
        if (!event.getSource().is(DamageTypes.MAGIC) && living.hasEffect(NVMobEffects.OBSIDIAN_CLOAK)) {
            MobEffectInstance instance = living.getEffect(NVMobEffects.OBSIDIAN_CLOAK);
            float modifier = (float) (1 - 0.2 * (1 + instance.getAmplifier()));
            event.setAmount(event.getAmount() * Math.max(0, modifier));
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.has(NVDataComponents.BLOOD_MENDING.get())) {
            event.getToolTip().add(Component.translatable("tooltip.neovitae.blood_mending")
                    .withStyle(ChatFormatting.DARK_RED));
        }
        if (SpiritusHelper.hasSpiritus(stack) && SpiritusHelper.isRechargeable(stack)) {
            SpiritusType type = SpiritusHelper.getCurrentType(stack);
            double amount = SpiritusHelper.getSpiritus(stack, type);
            double max = SpiritusHelper.resolveMaxSpiritus(stack);
            event.getToolTip().add(Component.translatable("tooltip.neovitae.spiritus_stored",
                    ChatUtil.DECIMAL_FORMAT.format(amount), ChatUtil.DECIMAL_FORMAT.format(max))
                    .withStyle(ChatFormatting.GRAY));
            event.getToolTip().add(Component.translatable("tooltip.neovitae.current_type." + type.getSerializedName())
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @SubscribeEvent
    public static void onPlayerTickBloodMending(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide || player.tickCount % 20 != 0) return;

        int repairCost = NeoVitae.SERVER_CONFIG.BLOOD_MENDING_REPAIR_COST.get();

        Binding orbBinding = findBoundOrb(player);
        if (orbBinding == null || orbBinding.isEmpty()) return;

        Anima anima = AnimaHelper.getAnima(orbBinding);
        if (anima == null || anima.getCurrentEV() < repairCost) return;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty() || !stack.has(NVDataComponents.BLOOD_MENDING.get())) continue;
            if (!stack.isDamaged()) continue;
            if (stack.is(NVTags.Items.BLOOD_MENDING_BLACKLIST)) continue;
            if (anima.getCurrentEV() < repairCost) break;

            stack.setDamageValue(stack.getDamageValue() - 1);
            anima.syphon(AnimaTicket.create(repairCost));
        }
    }

    @SubscribeEvent
    public static void onTamedPetDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof TamableAnimal pet)) return;
        if (!pet.isTame() || pet.getOwnerUUID() == null) return;

        Player owner = pet.level().getPlayerByUUID(pet.getOwnerUUID());
        if (owner == null) return;

        CompoundTag petData = new CompoundTag();
        pet.save(petData);
        petData.remove("Inventory");

        DeadPetStorage storage = owner.getData(NVDataAttachments.DEAD_PET_STORAGE);
        owner.setData(NVDataAttachments.DEAD_PET_STORAGE.get(), storage.addPet(petData));
    }

    private static Binding findBoundOrb(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof BloodOrbItem) {
                Binding binding = stack.getOrDefault(NVDataComponents.BINDING.get(), Binding.EMPTY);
                if (!binding.isEmpty()) return binding;
            }
        }
        return null;
    }

    @SubscribeEvent
    public static void onPlayerTickLexBeam(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!LexVitaeItem.isBeaming(player)) return;
        ItemStack held = player.getMainHandItem();
        if (held.getItem() instanceof LexVitaeItem && LexVitaeItem.isActive(held)) {
            LexVitaeItem.fireBeam(player.level(), player, held);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogoutLexBeam(PlayerEvent.PlayerLoggedOutEvent event) {
        LexVitaeItem.clearBeaming(event.getEntity().getUUID());
        BloodOrbItem.clearShieldActive(event.getEntity().getUUID());
    }
}
