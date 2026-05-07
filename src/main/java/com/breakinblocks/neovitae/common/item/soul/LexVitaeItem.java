package com.breakinblocks.neovitae.common.item.soul;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.will.PlayerSpiritusHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static com.breakinblocks.neovitae.common.item.soul.SentientToolHelper.*;

public class LexVitaeItem extends Item implements ISentientTool {

    private static final float ACTIVE_SPEED = 9.0F;
    private static final int DURABILITY = 2031;

    private static final double[] DEFAULT_DAMAGE = {1, 2, 3, 4, 5, 6, 7};
    private static final double[] DESTRUCTIVE_DAMAGE = {2, 3, 4, 5, 6, 7, 8};
    private static final double[] VENGEFUL_DAMAGE = {0.5, 1, 1.5, 2, 2.5, 3, 3.5};
    private static final double[] STEADFAST_DAMAGE = {0.5, 1, 1.5, 2, 2.5, 3, 3.5};

    public LexVitaeItem(Item.Properties props) {
        super(props
                .stacksTo(1)
                .durability(DURABILITY)
                .fireResistant()
                .enchantable(20)
                .component(DataComponents.TOOL, buildTool())
                .component(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW)
                .component(NVDataComponents.LEX_ACTIVE, false)
                .component(NVDataComponents.LEX_RADIUS, 0));
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, java.util.function.Consumer<Item> onBroken) {
        if (entity instanceof Player player) {
            SpiritusType type = PlayerSpiritusHandler.getLargestSpiritusType(player);
            if (PlayerSpiritusHandler.getTotalSpiritus(type, player) >= 1.0
                    && PlayerSpiritusHandler.consumeSpiritus(type, player, 1.0) > 0) {
                return 0;
            }
        }
        return amount;
    }

    @Override
    public boolean canPerformAction(net.minecraft.world.item.ItemInstance stack, ItemAbility ability) {
        if (!(stack instanceof ItemStack itemStack) || !isActive(itemStack)) return false;
        return ability == ItemAbilities.SWORD_SWEEP
                || ability == ItemAbilities.AXE_STRIP
                || ability == ItemAbilities.AXE_SCRAPE
                || ability == ItemAbilities.AXE_WAX_OFF
                || ability == ItemAbilities.SHOVEL_FLATTEN
                || ability == ItemAbilities.HOE_TILL;
    }

    private static Tool buildTool() {
        HolderGetter<net.minecraft.world.level.block.Block> blocks =
                BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        return new Tool(List.of(
                Tool.Rule.deniesDrops(blocks.getOrThrow(BlockTags.INCORRECT_FOR_NETHERITE_TOOL)),
                Tool.Rule.minesAndDrops(blocks.getOrThrow(BlockTags.MINEABLE_WITH_PICKAXE), ACTIVE_SPEED),
                Tool.Rule.minesAndDrops(blocks.getOrThrow(BlockTags.MINEABLE_WITH_AXE), ACTIVE_SPEED),
                Tool.Rule.minesAndDrops(blocks.getOrThrow(BlockTags.MINEABLE_WITH_SHOVEL), ACTIVE_SPEED),
                Tool.Rule.minesAndDrops(blocks.getOrThrow(BlockTags.MINEABLE_WITH_HOE), ACTIVE_SPEED)
        ), 1.0F, 2, true);
    }

    public static final double BEAM_RANGE = 32.0;

    private static final Identifier ATTACK_DAMAGE_ID = NeoVitae.rl("lex_vitae_attack_damage");
    private static final Identifier ATTACK_SPEED_ID = NeoVitae.rl("lex_vitae_attack_speed");

    private static final ItemAttributeModifiers ACTIVE_MODIFIERS = ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(ATTACK_DAMAGE_ID, 8.0, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                    new AttributeModifier(ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND)
            .build();

    private static final Map<UUID, Long> BEAM_COOLDOWN = new HashMap<>();

    public static boolean isActive(ItemStack stack) {
        return stack.getOrDefault(NVDataComponents.LEX_ACTIVE, false);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return isActive(stack) ? ACTIVE_MODIFIERS : ItemAttributeModifiers.EMPTY;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return isActive(stack) ? ItemUseAnimation.BOW : ItemUseAnimation.NONE;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return isActive(stack) ? super.getDestroySpeed(stack, state) : 1.0F;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return isActive(stack) && super.isCorrectToolForDrops(stack, state);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!isActive(stack)) return false;
        if (state.getDestroySpeed(level, pos) != 0.0F) {
            stack.hurtAndBreak(1, miner, EquipmentSlot.MAINHAND);
        }
        if (miner instanceof Player player) {
            recalculatePowers(stack, level, player);
        }
        return true;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            boolean now = !isActive(stack);
            stack.set(NVDataComponents.LEX_ACTIVE, now);
            world.playSound(null, player.blockPosition(),
                    now ? SoundEvents.BEACON_ACTIVATE : SoundEvents.BEACON_DEACTIVATE,
                    SoundSource.PLAYERS, 0.5F, now ? 1.4F : 0.7F);
            if (!world.isClientSide() && now) {
                recalculatePowers(stack, world, player);
            }
            return InteractionResult.SUCCESS;
        }
        if (isActive(stack)) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
        return super.use(world, player, hand);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide()) return;
        if (!(entity instanceof Player player)) return;
        if (!isActive(stack)) {
            player.stopUsingItem();
            return;
        }

        Vec3 origin = player.getEyePosition();
        Vec3 dir = player.getLookAngle();
        Vec3 reach = origin.add(dir.scale(BEAM_RANGE));
        long now = level.getGameTime();
        long cooldownEnd = BEAM_COOLDOWN.getOrDefault(player.getUUID(), 0L);

        EntityHitResult ehr = ProjectileUtil.getEntityHitResult(player, origin, reach,
                new AABB(origin, reach).inflate(2.0),
                e -> !e.isSpectator() && e.isPickable() && e != player && e.isAlive(),
                0.0);
        if (ehr != null && ehr.getEntity() instanceof LivingEntity living && now >= cooldownEnd) {
            int attackCd = Math.max(2,
                    (int) Math.ceil(20.0 / Math.max(0.1, player.getAttributeValue(Attributes.ATTACK_SPEED))));
            BEAM_COOLDOWN.put(player.getUUID(), now + attackCd);
            player.attack(living);
            player.resetAttackStrengthTicker();
            return;
        }

        BlockHitResult bhr = level.clip(new ClipContext(origin, reach,
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (bhr.getType() != HitResult.Type.BLOCK) return;

        BlockPos pos = bhr.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return;

        float hardness = state.getDestroySpeed(level, pos);
        if (hardness < 0) return;

        int requiredTicks = Math.max(2, (int) (hardness * 4));
        if (now < cooldownEnd) return;
        BEAM_COOLDOWN.put(player.getUUID(), now + requiredTicks);

        if (player instanceof ServerPlayer sp) {
            sp.gameMode.destroyBlock(pos);
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player p) {
            BEAM_COOLDOWN.remove(p.getUUID());
        }
        return true;
    }

    private static final List<ItemAbility> ABILITIES = List.of(
            ItemAbilities.AXE_STRIP,
            ItemAbilities.AXE_SCRAPE,
            ItemAbilities.AXE_WAX_OFF,
            ItemAbilities.SHOVEL_FLATTEN,
            ItemAbilities.HOE_TILL
    );

    private static final double VANILLA_BLOCK_REACH = 4.5;

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (!isActive(stack)) return InteractionResult.PASS;

        Player ctxPlayer = context.getPlayer();
        if (ctxPlayer != null) {
            double distSq = ctxPlayer.getEyePosition().distanceToSqr(context.getClickLocation());
            if (distSq > VANILLA_BLOCK_REACH * VANILLA_BLOCK_REACH) return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        ItemAbility chosen = null;
        BlockState modified = null;
        for (ItemAbility ability : ABILITIES) {
            BlockState m = state.getToolModifiedState(context, ability, false);
            if (m != null) { chosen = ability; modified = m; break; }
        }
        if (modified == null) return InteractionResult.PASS;

        if (!level.isClientSide()) {
            level.setBlock(pos, modified, 11);
            level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (ctxPlayer != null) {
                stack.hurtAndBreak(1, ctxPlayer, context.getHand().asEquipmentSlot());
                applyAbilityAoe(context, chosen, stack, ctxPlayer);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private static void applyAbilityAoe(UseOnContext context, ItemAbility ability, ItemStack stack, Player player) {
        int radius = stack.getOrDefault(NVDataComponents.LEX_RADIUS, 0);
        if (radius == 0) return;
        int half = radius == 1 ? 1 : 2;
        Level level = context.getLevel();
        BlockPos center = context.getClickedPos();
        net.minecraft.core.Direction face = context.getClickedFace();
        net.minecraft.core.Direction.Axis depth = face.getAxis();

        for (int u = -half; u <= half; u++) {
            for (int v = -half; v <= half; v++) {
                if (u == 0 && v == 0) continue;
                BlockPos offset = switch (depth) {
                    case X -> center.offset(0, u, v);
                    case Y -> center.offset(u, 0, v);
                    case Z -> center.offset(u, v, 0);
                };
                if (!level.isLoaded(offset)) continue;
                BlockState s = level.getBlockState(offset);
                Vec3 hitLoc = Vec3.atCenterOf(offset).add(
                        face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5);
                BlockHitResult bhr = new BlockHitResult(hitLoc, face, offset, false);
                UseOnContext aoeCtx = new UseOnContext(level, player, context.getHand(), stack, bhr);
                BlockState m = s.getToolModifiedState(aoeCtx, ability, false);
                if (m == null) continue;
                level.setBlock(offset, m, 11);
                stack.hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
                if (stack.isEmpty()) return;
            }
        }
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!isActive(stack)) return;
        if (attacker instanceof Player player) {
            recalculatePowers(stack, player.level(), player);
            SpiritusType type = getCurrentType(stack);
            double will = PlayerSpiritusHandler.getTotalSpiritus(type, player);
            int bracket = getLevel(will);
            if (bracket >= 0) {
                applyEffectToEntity(type, bracket, target, player);
            }
        }
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!isActive(stack)) return true;
        recalculatePowers(stack, player.level(), player);
        return handleSpiritusDrain(stack, player);
    }

    @Override
    public double[] getDamageForSpiritusType(SpiritusType type) {
        return switch (type) {
            case NIHILUM -> DESTRUCTIVE_DAMAGE;
            case VINDICTA -> VENGEFUL_DAMAGE;
            case INVICTUS -> STEADFAST_DAMAGE;
            default -> DEFAULT_DAMAGE;
        };
    }

    @Override
    public String getTooltipKey() {
        return "lexVitae";
    }

    @Override
    public void recalculatePowers(ItemStack stack, Level world, Player player) {
        SpiritusType type = PlayerSpiritusHandler.getLargestSpiritusType(player);
        double soulsRemaining = PlayerSpiritusHandler.getTotalSpiritus(type, player);
        setCurrentType(stack, soulsRemaining > 0 ? type : SpiritusType.RAW);
        int level = getLevel(soulsRemaining);
        setDrainAmount(stack, level >= 0 ? SOUL_DRAIN_PER_SWING[level] : 0);
        setDamageBonus(stack, getExtraDamage(type, level));
        setStaticDrop(stack, level >= 0 ? STATIC_DROP[level] : 1);
        setSoulDrop(stack, level >= 0 ? SOUL_DROP[level] : 0);
        setDigSpeedBonus(stack, level >= 0 ? DEFAULT_DIG_SPEED_ADDED[level] : 0);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.neovitae.lexVitae.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable(isActive(stack)
                ? "tooltip.neovitae.lexVitae.active"
                : "tooltip.neovitae.lexVitae.dormant").withStyle(isActive(stack) ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY));
        int r = stack.getOrDefault(NVDataComponents.LEX_RADIUS, 0);
        int side = r == 0 ? 1 : (r == 1 ? 3 : 5);
        tooltip.accept(Component.translatable("tooltip.neovitae.lexVitae.radius", side, side).withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.neovitae.currentType." + getCurrentType(stack).name().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }
}
