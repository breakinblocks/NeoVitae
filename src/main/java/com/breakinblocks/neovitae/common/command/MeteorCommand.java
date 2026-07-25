package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.entity.projectile.EntityMeteor;
import com.breakinblocks.neovitae.common.recipe.meteor.MeteorRecipeHelper;

public class MeteorCommand {

    private static final SimpleCommandExceptionType ERROR_NOT_CATALYST = new SimpleCommandExceptionType(
            Component.translatable("commands.neovitae.meteor.not_catalyst"));

    public static LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext buildContext) {
        return Commands.literal("meteor")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .then(Commands.argument("catalyst", ItemArgument.item(buildContext))
                                .executes(context -> summon(context, false))
                                .then(Commands.argument("detonatePos", BlockPosArgument.blockPos())
                                        .executes(context -> summon(context, true)))));
    }

    private static int summon(CommandContext<CommandSourceStack> context, boolean hasDetonation) throws CommandSyntaxException {
        ServerLevel level = context.getSource().getLevel();
        BlockPos startPos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        ItemStack catalyst = ItemArgument.getItem(context, "catalyst").createItemStack(1);

        if (MeteorRecipeHelper.findRecipe(level, catalyst) == null) {
            throw ERROR_NOT_CATALYST.create();
        }

        EntityMeteor meteor = new EntityMeteor(level,
                startPos.getX() + 0.5, startPos.getY() + 0.5, startPos.getZ() + 0.5);
        meteor.setDeltaMovement(0, -0.1, 0);
        meteor.setContainedStack(catalyst);

        if (hasDetonation) {
            meteor.setTargetY(BlockPosArgument.getBlockPos(context, "detonatePos").getY());
        }

        level.addFreshEntity(meteor);

        context.getSource().sendSuccess(() -> Component.translatable("commands.neovitae.meteor.success",
                catalyst.getHoverName(), startPos.getX(), startPos.getY(), startPos.getZ()), true);
        return 1;
    }
}
