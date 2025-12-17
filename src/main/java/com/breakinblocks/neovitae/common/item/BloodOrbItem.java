package com.breakinblocks.neovitae.common.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.FakePlayer;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.datacomponent.SoulNetwork;
import com.breakinblocks.neovitae.common.datamap.BMDataMaps;
import com.breakinblocks.neovitae.api.soul.SoulTicket;
import com.breakinblocks.neovitae.util.helper.SoulNetworkHelper;

public class BloodOrbItem extends Item implements IBindable {

    public BloodOrbItem() {
        super(new Item.Properties().stacksTo(1).component(BMDataComponents.BINDING, Binding.EMPTY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) {
            return InteractionResultHolder.fail(stack);
        }

        if (player instanceof FakePlayer)
            return InteractionResultHolder.consume(stack);


        Binding binding = stack.getOrDefault(BMDataComponents.BINDING, Binding.EMPTY);
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

            SoulNetwork ownerNetwork = SoulNetworkHelper.getSoulNetwork(binding);
            ownerNetwork.add(SoulTicket.create(200), capacity);
            ownerNetwork.hurtPlayer(player, 200);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public int getCapacity(ItemStack stack) {
        return stack.getItemHolder().getData(BMDataMaps.BLOOD_ORB_STATS).capacity();
    }

    public int getFillRate(ItemStack stack) {
        return stack.getItemHolder().getData(BMDataMaps.BLOOD_ORB_STATS).fillRate();
    }

    public int getOrbTier(ItemStack stack) {
        return stack.getItemHolder().getData(BMDataMaps.BLOOD_ORB_STATS).tier();
    }

}
