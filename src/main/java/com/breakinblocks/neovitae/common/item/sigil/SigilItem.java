package com.breakinblocks.neovitae.common.item.sigil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import com.breakinblocks.neovitae.api.sigil.SigilEffect;
import com.breakinblocks.neovitae.api.sigil.SigilType;
import com.breakinblocks.neovitae.api.soul.SoulTicket;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.SoulNetwork;
import com.breakinblocks.neovitae.common.item.IActivatable;
import com.breakinblocks.neovitae.common.item.IBindable;
import com.breakinblocks.neovitae.registry.SigilTypeRegistry;
import com.breakinblocks.neovitae.util.helper.PlayerHelper;
import com.breakinblocks.neovitae.util.helper.SoulNetworkHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Unified sigil item that uses datapack-driven sigil types.
 * This single item class can represent any sigil by storing
 * a SigilType reference in its data components.
 */
public class SigilItem extends Item implements IBindable, IActivatable, ISigil {

    private final ResourceKey<SigilType> defaultSigilType;
    private final String tooltipBase;

    /**
     * Creates a sigil item with the specified default sigil type.
     *
     * @param sigilTypeKey The resource key for the default sigil type
     */
    public SigilItem(ResourceKey<SigilType> sigilTypeKey) {
        super(new Item.Properties().stacksTo(1));
        this.defaultSigilType = sigilTypeKey;
        this.tooltipBase = "tooltip.neovitae.sigil." + sigilTypeKey.location().getPath() + ".";
    }

    /**
     * Creates a sigil item with the specified default sigil type and custom properties.
     */
    public SigilItem(ResourceKey<SigilType> sigilTypeKey, Item.Properties properties) {
        super(properties.stacksTo(1));
        this.defaultSigilType = sigilTypeKey;
        this.tooltipBase = "tooltip.neovitae.sigil." + sigilTypeKey.location().getPath() + ".";
    }

    /**
     * Gets the sigil type for this stack, checking data components first,
     * then falling back to the default type.
     */
    @Nullable
    public SigilType getSigilType(ItemStack stack, Level level) {
        Holder<SigilType> holder = stack.get(BMDataComponents.SIGIL_TYPE.get());
        if (holder != null) {
            return holder.value();
        }

        // Fall back to default type from registry
        if (level != null) {
            var registry = level.registryAccess().registry(SigilTypeRegistry.SIGIL_TYPE_KEY);
            if (registry.isPresent()) {
                return registry.get().get(defaultSigilType);
            }
        }
        return null;
    }

    /**
     * Gets the sigil effect for this stack.
     */
    @Nullable
    public ISigilEffect getSigilEffect(ItemStack stack, Level level) {
        SigilType type = getSigilType(stack, level);
        return type != null ? type.effect().orElse(null) : null;
    }

    public boolean isUnusable(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getOrDefault(BMDataComponents.SIGIL_UNUSABLE.get(), false);
    }

    public ItemStack setUnusable(ItemStack stack, boolean unusable) {
        if (!stack.isEmpty()) {
            stack.set(BMDataComponents.SIGIL_UNUSABLE.get(), unusable);
        }
        return stack;
    }

    /**
     * Gets the LP cost for the specified use context.
     */
    public int getLpCost(ItemStack stack, Level level, SigilType.UseContext context) {
        SigilType type = getSigilType(stack, level);
        return type != null ? type.getCostForContext(context) : 0;
    }

    /**
     * Checks if this sigil is toggleable.
     */
    public boolean isToggleable(ItemStack stack, Level level) {
        SigilType type = getSigilType(stack, level);
        return type != null && type.isToggleable();
    }

    /**
     * Gets the drain interval for toggleable sigils.
     */
    public int getDrainInterval(ItemStack stack, Level level) {
        SigilType type = getSigilType(stack, level);
        return type != null ? type.drainInterval() : SigilType.DEFAULT_DRAIN_INTERVAL;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        // Add sigil-specific description
        tooltip.add(Component.translatable(tooltipBase + "desc")
                .withStyle(ChatFormatting.ITALIC)
                .withStyle(ChatFormatting.GRAY));

        // Add binding info
        Binding binding = getBinding(stack);
        if (binding != null) {
            tooltip.add(Component.translatable("tooltip.neovitae.currentOwner", binding.name())
                    .withStyle(ChatFormatting.GRAY));
        }

        // Add activation state for toggleable sigils
        Level level = context.level();
        if (level != null && isToggleable(stack, level)) {
            String stateKey = getActivated(stack) ? "tooltip.neovitae.activated" : "tooltip.neovitae.deactivated";
            tooltip.add(Component.translatable(stateKey).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = resolveStackForUse(player, hand);
        if (stack == null) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        Binding binding = getBinding(stack);
        if (binding == null) {
            return InteractionResultHolder.consume(player.getItemInHand(hand));
        }

        // Handle toggleable sigils - right-click toggles activation
        if (isToggleable(stack, level)) {
            if (!level.isClientSide && !isUnusable(stack)) {
                setActivatedState(stack, !getActivated(stack));
            }
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        // Execute the sigil effect for non-toggleable sigils
        if (!isUnusable(stack)) {
            ISigilEffect effect = getSigilEffect(stack, level);
            if (effect != null && effect.useOnAir(level, player, stack)) {
                // Consume LP on success
                if (!level.isClientSide && !player.isCreative()) {
                    int cost = getLpCost(stack, level, SigilType.UseContext.AIR);
                    if (cost > 0) {
                        SoulNetwork network = SoulNetworkHelper.getSoulNetwork(binding);
                        if (network != null) {
                            setUnusable(stack, !network.syphonAndDamage(player, SoulTicket.create(cost)).success());
                        }
                    }
                }
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }
        }

        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        // Handle Sigil of Holding
        if (stack.getItem() instanceof ISigil.Holding holding) {
            stack = holding.getHeldItem(stack, player);
        }

        Binding binding = getBinding(stack);
        if (binding == null) {
            return InteractionResult.CONSUME;
        }

        ISigilEffect effect = getSigilEffect(stack, level);
        if (effect != null) {
            Direction side = context.getClickedFace();
            Vec3 hitVec = context.getClickLocation();

            if (effect.useOnBlock(level, player, stack, blockPos, side, hitVec)) {
                // Consume LP on success
                if (!level.isClientSide && player != null && !player.isCreative()) {
                    int cost = getLpCost(stack, level, SigilType.UseContext.BLOCK);
                    if (cost > 0) {
                        SoulNetwork network = SoulNetworkHelper.getSoulNetwork(binding);
                        if (network != null) {
                            setUnusable(stack, !network.syphonAndDamage(player, SoulTicket.create(cost)).success());
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, net.minecraft.world.entity.LivingEntity target, InteractionHand hand) {
        ItemStack useStack = stack;
        if (stack.getItem() instanceof ISigil.Holding holding) {
            useStack = holding.getHeldItem(stack, player);
        }

        Binding binding = getBinding(useStack);
        if (binding == null) {
            return InteractionResult.CONSUME;
        }

        Level level = player.level();
        ISigilEffect effect = getSigilEffect(useStack, level);
        if (effect != null && effect.useOnEntity(level, player, useStack, target)) {
            // Consume LP on success
            if (!level.isClientSide && !player.isCreative()) {
                int cost = getLpCost(useStack, level, SigilType.UseContext.ENTITY);
                if (cost > 0) {
                    SoulNetwork network = SoulNetworkHelper.getSoulNetwork(binding);
                    if (network != null) {
                        setUnusable(useStack, !network.syphonAndDamage(player, SoulTicket.create(cost)).success());
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player player)) {
            return;
        }

        // Only tick if toggleable and activated
        if (!isToggleable(stack, level) || !getActivated(stack)) {
            return;
        }

        Binding binding = getBinding(stack);
        if (binding == null) {
            return;
        }

        // Drain LP at intervals
        int drainInterval = getDrainInterval(stack, level);
        if (entity.tickCount % drainInterval == 0) {
            int cost = getLpCost(stack, level, SigilType.UseContext.ACTIVE);
            if (cost > 0) {
                SoulNetwork network = SoulNetworkHelper.getSoulNetwork(binding);
                if (network != null) {
                    if (!network.syphonAndDamage(player, SoulTicket.create(cost)).success()) {
                        setActivatedState(stack, false);
                        return;
                    }
                }
            }
        }

        // Execute active tick
        ISigilEffect effect = getSigilEffect(stack, level);
        if (effect != null) {
            effect.activeTick(level, player, stack, itemSlot, isSelected);
        }
    }

    /**
     * Resolves the sigil stack for use, handling Sigil of Holding.
     * Returns null if the player is a fake player (automation not allowed).
     */
    @Nullable
    protected ItemStack resolveStackForUse(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof ISigil.Holding holding) {
            stack = holding.getHeldItem(stack, player);
        }

        if (PlayerHelper.isFakePlayer(player)) {
            return null;
        }

        return stack;
    }

    /**
     * Gets the default sigil type key for this item.
     */
    public ResourceKey<SigilType> getDefaultSigilType() {
        return defaultSigilType;
    }

    /**
     * Gets the tooltip base for this sigil.
     */
    public String getTooltipBase() {
        return tooltipBase;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        // Prevent the attack/swing animation when using sigils
        return true;
    }
}
