package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import com.breakinblocks.neovitae.common.item.BloodOrbItem;
import com.breakinblocks.neovitae.common.item.OrbFluidHandler;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

public class SetOrbFillCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("setorbfill")
                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                        .executes(SetOrbFillCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("This command must be run by a player"));
            return 0;
        }

        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof BloodOrbItem)) {
            source.sendFailure(Component.translatable("command.neovitae.setorbfill.not_orb"));
            return 0;
        }

        int amount = IntegerArgumentType.getInteger(context, "amount");
        int capacity = OrbFluidHandler.getOrbFluidCapacity(held);
        int clamped = Math.min(amount, capacity);

        if (clamped <= 0) {
            held.remove(NVDataComponents.ORB_FLUID.get());
        } else {
            FluidStack fluid = new FluidStack(NVFluids.ESSENTIA_VITAE_SOURCE.get(), clamped);
            held.set(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.copyOf(fluid));
        }

        source.sendSuccess(() -> Component.translatable("command.neovitae.setorbfill.success", clamped, capacity), true);
        return 1;
    }
}
