package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.entity.projectile.AbstractEntityThrowingDagger;
import com.breakinblocks.neovitae.common.entity.projectile.EntityThrowingDagger;

import java.util.List;

/**
 * Throwing dagger item - right-click to throw a single dagger.
 */
public class ItemThrowingDagger extends Item {

    public ItemThrowingDagger() {
        super(new Item.Properties().stacksTo(16));
    }

    public ItemThrowingDagger(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.isCreative()) {
            stack.shrink(1);
        }
        player.getCooldowns().addCooldown(this, 50);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL,
                0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));

        if (!level.isClientSide) {
            ItemStack copyStack = stack.copy();
            copyStack.setCount(1);
            AbstractEntityThrowingDagger dagger = getDagger(copyStack, level, player);
            level.addFreshEntity(dagger);
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    /**
     * Creates the throwing dagger entity. Override in subclasses for different dagger types.
     */
    public AbstractEntityThrowingDagger getDagger(ItemStack stack, Level level, Player player) {
        EntityThrowingDagger dagger = new EntityThrowingDagger(level, player, stack);
        dagger.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3F, 0.5F);
        dagger.setDamage(10);
        return dagger;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.throwing_dagger.desc")
                .withStyle(ChatFormatting.ITALIC)
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
