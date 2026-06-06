// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2023 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.block.BlockRitualStone;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;

import java.util.List;

public class ItemInscriptionTool extends Item {
    private final EnumRuneType type;

    public ItemInscriptionTool(EnumRuneType type) {
        super(new Item.Properties().stacksTo(1));
        this.type = type;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof BlockRitualStone ritualStone
                && !ritualStone.isRuneType(world, pos, type)) {
            ritualStone.setRuneType(world, pos, type);
            if (!world.isClientSide) {
                world.playSound(null, pos, NVSounds.INSCRIPTION_TOOL_SCRIBE.get(), SoundSource.PLAYERS, 0.5f, 1.0f);
                int runeColor = type.colorCode.getColor() != null ? type.colorCode.getColor() : 0xAA0000;
                ((ServerLevel) world).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xAA0000), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, 0.15, 0.15, 0.15, 0);
                ((ServerLevel) world).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), runeColor), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, 0.15, 0.15, 0.15, 0.01);
                ((ServerLevel) world).sendParticles(new ColoredParticleOptions(NVParticles.RUNE_GLOW.get(), runeColor), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0.0, 0.0, 0.0, 0);
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.inscriber.desc").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, flag);
    }

    public EnumRuneType getRuneType() {
        return type;
    }
}
