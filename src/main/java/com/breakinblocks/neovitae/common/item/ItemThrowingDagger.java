package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import com.breakinblocks.neovitae.common.entity.projectile.AbstractEntityThrowingDagger;
import com.breakinblocks.neovitae.common.entity.projectile.EntityThrowingDagger;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class ItemThrowingDagger extends Item {

    public ItemThrowingDagger(Item.Properties props) {
        super(props.stacksTo(16));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.isCreative()) {
            stack.shrink(1);
        }
        player.getCooldowns().addCooldown(stack, 50);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL,
                0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!level.isClientSide()) {
            ItemStack copyStack = stack.copy();
            copyStack.setCount(1);
            AbstractEntityThrowingDagger dagger = getDagger(copyStack, level, player);
            level.addFreshEntity(dagger);
        }

        return InteractionResult.SUCCESS;
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.neovitae.throwing_dagger.desc")
                .withStyle(ChatFormatting.ITALIC)
                .withStyle(ChatFormatting.GRAY));}
}
