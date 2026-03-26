package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.FakePlayer;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.util.AltarUtil;

import java.util.List;

public class ItemBloodProvider extends Item {
    protected final String tooltipBase;
    public final int evProvided;

    public ItemBloodProvider(String name, int evProvided) {
        super(new Item.Properties().stacksTo(64));
        this.tooltipBase = "tooltip.neovitae.blood_provider." + name + ".";
        this.evProvided = evProvided;
    }

    public ItemBloodProvider(String name) {
        this(name, 0);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player instanceof FakePlayer) {
            return super.use(level, player, hand);
        }

        BlockPos altarPos = AltarUtil.findAltar(level, player.blockPosition(), 2);
        if (altarPos != null) {
            BlockEntity be = level.getBlockEntity(altarPos);
            if (be instanceof AraVitaeTile altar) {
                double posX = player.getX();
                double posY = player.getY();
                double posZ = player.getZ();

                level.playSound(player, posX, posY, posZ, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS,
                        0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

                for (int l = 0; l < 8; ++l) {
                    level.addParticle(DustParticleOptions.REDSTONE,
                            posX + Math.random() - Math.random(),
                            posY + Math.random() - Math.random(),
                            posZ + Math.random() - Math.random(),
                            0, 0, 0);
                }

                if (!level.isClientSide) {
                    altar.addSacrificeEV(evProvided, false);

                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                }
            }
        }

        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(tooltipBase + "desc").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
