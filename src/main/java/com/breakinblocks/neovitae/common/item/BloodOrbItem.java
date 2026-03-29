package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datamap.NVDataMaps;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.List;

public class BloodOrbItem extends Item implements IBindable {

    public BloodOrbItem() {
        super(new Item.Properties().stacksTo(1).component(NVDataComponents.BINDING, Binding.EMPTY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) {
            return InteractionResultHolder.fail(stack);
        }

        if (player instanceof FakePlayer)
            return InteractionResultHolder.consume(stack);


        Binding binding = stack.getOrDefault(NVDataComponents.BINDING, Binding.EMPTY);
        if (binding.isEmpty())
            return InteractionResultHolder.consume(stack);

        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS,
                    0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F
            );


            int capacity = getCapacity(stack);
            if (capacity == 0)
                return InteractionResultHolder.fail(stack);

            Anima ownerNetwork = AnimaHelper.getAnima(binding);
            ownerNetwork.add(AnimaTicket.create(200), capacity);
            ownerNetwork.hurtPlayer(player, 200);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public int getCapacity(ItemStack stack) {
        return stack.getItemHolder().getData(NVDataMaps.BLOOD_ORB_STATS).capacity();
    }

    public int getFillRate(ItemStack stack) {
        return stack.getItemHolder().getData(NVDataMaps.BLOOD_ORB_STATS).fillRate();
    }

    public int getOrbTier(ItemStack stack) {
        return stack.getItemHolder().getData(NVDataMaps.BLOOD_ORB_STATS).tier();
    }

    private static final String[] TIER_NAMES = {
            "Weak", "Apprentice", "Magician", "Master", "Archmage", "Transcendent"
    };

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        var stats = stack.getItemHolder().getData(NVDataMaps.BLOOD_ORB_STATS);
        if (stats != null) {
            int tier = stats.tier();
            String tierName = tier >= 0 && tier < TIER_NAMES.length ? TIER_NAMES[tier] : "Unknown";
            tooltip.add(Component.translatable("tooltip.neovitae.orb.tier", tier, tierName)
                    .withStyle(ChatFormatting.GRAY));
        }

        SimpleFluidContent fluid = stack.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
        int capacity = OrbFluidHandler.getOrbFluidCapacity(stack);
        if (capacity > 0) {
            int amount = fluid.isEmpty() ? 0 : fluid.getAmount();
            tooltip.add(Component.translatable("tooltip.neovitae.orb.fluid", amount, capacity)
                    .withStyle(ChatFormatting.DARK_RED));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
