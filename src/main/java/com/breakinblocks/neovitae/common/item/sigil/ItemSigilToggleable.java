package com.breakinblocks.neovitae.common.item.sigil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.SoulNetwork;
import com.breakinblocks.neovitae.common.datamap.SigilStats;
import com.breakinblocks.neovitae.common.item.IActivatable;
import com.breakinblocks.neovitae.api.soul.SoulTicket;
import com.breakinblocks.neovitae.util.helper.SoulNetworkHelper;

/**
 * Base class for all toggleable sigils.
 */
public class ItemSigilToggleable extends ItemSigil implements IActivatable {

    public ItemSigilToggleable(Item.Properties property, int lpUsed) {
        super(property, lpUsed);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = resolveStackForUse(player, hand);
        if (stack == null) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        // Must be bound before use
        Binding binding = getBinding(stack);
        if (binding == null) {
            return InteractionResultHolder.consume(player.getItemInHand(hand));
        }

        if (!world.isClientSide && !isUnusable(stack)) {
            if (player.isShiftKeyDown()) {
                setActivatedState(stack, !getActivated(stack));
            }
            if (getActivated(stack)) {
                return super.use(world, player, hand);
            }
        }

        return super.use(world, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        // Handle Sigil of Holding
        if (stack.getItem() instanceof ISigil.Holding holding) {
            stack = holding.getHeldItem(stack, player);
        }

        Binding binding = getBinding(stack);
        // Make sure Sigils are bound before handling. Also ignores while toggling state
        if (binding == null || player.isShiftKeyDown()) {
            return InteractionResult.CONSUME;
        }

        return onSigilUse(stack, player, world, blockpos, context.getClickedFace(), context.getClickLocation())
                ? InteractionResult.SUCCESS
                : InteractionResult.FAIL;
    }

    /**
     * Called when the sigil is used on a block.
     *
     * @param itemStack The sigil ItemStack
     * @param player    The player using the sigil
     * @param world     The world
     * @param blockPos  The position of the block
     * @param side      The side of the block that was clicked
     * @param hitVec    The exact hit location
     * @return Whether the sigil effect was performed
     */
    public boolean onSigilUse(ItemStack itemStack, Player player, Level world, BlockPos blockPos, Direction side, Vec3 hitVec) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide && entityIn instanceof Player player && getActivated(stack)) {
            int drainInterval = getDrainInterval();
            if (entityIn.tickCount % drainInterval == 0) {
                Binding binding = getBinding(stack);
                if (binding != null) {
                    SoulNetwork network = SoulNetworkHelper.getSoulNetwork(binding);
                    if (network != null) {
                        if (!network.syphonAndDamage(player, SoulTicket.create(getLpUsed())).success()) {
                            setActivatedState(stack, false);
                        }
                    }
                }
            }

            onSigilUpdate(stack, worldIn, player, itemSlot, isSelected);
        }
    }

    /**
     * Gets the drain interval for this sigil, checking the datamap first.
     * Falls back to the default value (100 ticks / 5 seconds) if not in datamap.
     */
    public int getDrainInterval() {
        SigilStats stats = getSigilStats();
        if (stats != null) {
            return stats.drainInterval();
        }
        return SigilStats.DEFAULT_DRAIN_INTERVAL;
    }

    /**
     * Called every tick while the sigil is activated and in the player's inventory.
     *
     * @param stack      The sigil ItemStack
     * @param world      The world
     * @param player     The player holding the sigil
     * @param itemSlot   The slot the sigil is in
     * @param isSelected Whether the sigil is currently selected
     */
    public void onSigilUpdate(ItemStack stack, Level world, Player player, int itemSlot, boolean isSelected) {
    }
}
