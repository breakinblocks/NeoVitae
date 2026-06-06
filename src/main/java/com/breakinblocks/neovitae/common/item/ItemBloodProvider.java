// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.FakePlayer;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.util.AltarUtil;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class ItemBloodProvider extends Item {
    protected final String tooltipBase;
    public final int evProvided;

    public ItemBloodProvider(Item.Properties props, String name, int evProvided) {
        super(props.stacksTo(64));
        this.tooltipBase = "tooltip.neovitae.blood_provider." + name + ".";
        this.evProvided = evProvided;
    }

    public ItemBloodProvider(Item.Properties props, String name) {
        this(props, name, 0);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
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
                        0.5F, 2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);

                for (int l = 0; l < 8; ++l) {
                    level.addParticle(DustParticleOptions.REDSTONE,
                            posX + Math.random() - Math.random(),
                            posY + Math.random() - Math.random(),
                            posZ + Math.random() - Math.random(),
                            0, 0, 0);
                }

                if (!level.isClientSide()) {
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
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable(tooltipBase + "desc").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));}
}
