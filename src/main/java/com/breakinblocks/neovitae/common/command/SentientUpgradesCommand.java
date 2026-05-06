package com.breakinblocks.neovitae.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.util.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SentientStats;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeLimits;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.common.registry.NVRegistries;

import javax.swing.text.html.CSS;
import java.util.Optional;

public class SentientUpgradesCommand {
    private static final DynamicCommandExceptionType ERROR_NO_LIVING_HOLDER = new DynamicCommandExceptionType(playername -> Component.translatable("command.neovitae.upgrade.no_armour", playername));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(
                Commands.literal("living-upgrade")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .then(
                                Commands.argument("target", EntityArgument.player())
                                        .then(
                                                Commands.literal("upgrade")
                                                        .then(
                                                                Commands.literal("set")
                                                                        .then(
                                                                                Commands.argument("id", ResourceArgument.resource(buildContext, NVRegistries.Keys.SENTIENT_UPGRADES))
                                                                                        .then(
                                                                                                Commands.argument("exp", IntegerArgumentType.integer(0))
                                                                                                        .executes(
                                                                                                                context -> setUpgrade(context.getSource(), EntityArgument.getPlayer(context, "target"), ResourceArgument.getResource(context, "id", NVRegistries.Keys.SENTIENT_UPGRADES), IntegerArgumentType.getInteger(context, "exp"))
                                                                                                        )
                                                                                        )
                                                                        )
                                                        )
                                                        .then(
                                                                Commands.literal("get")
                                                                        .executes(
                                                                                context -> getUpgrades(context.getSource(), EntityArgument.getPlayer(context, "target"), Optional.empty())
                                                                        )
                                                                        .then(
                                                                                Commands.argument("id", ResourceArgument.resource(buildContext, NVRegistries.Keys.SENTIENT_UPGRADES))
                                                                                        .executes(
                                                                                                context -> getUpgrades(context.getSource(), EntityArgument.getPlayer(context, "target"), Optional.of(ResourceArgument.getResource(context, "id", NVRegistries.Keys.SENTIENT_UPGRADES)))
                                                                                        )
                                                                        )
                                                        )
                                        )
                                        .then(
                                                Commands.literal("limits")
                                                        .then(
                                                                Commands.literal("set")
                                                                        .then(
                                                                                Commands.argument("id", ResourceArgument.resource(buildContext, NVRegistries.Keys.SENTIENT_UPGRADES))
                                                                                        .then(
                                                                                                Commands.argument("exp", IntegerArgumentType.integer(0))
                                                                                                        .executes(
                                                                                                                context -> setLimit(context.getSource(), EntityArgument.getPlayer(context, "target"), ResourceArgument.getResource(context, "id", NVRegistries.Keys.SENTIENT_UPGRADES), IntegerArgumentType.getInteger(context, "exp"))
                                                                                                        )
                                                                                        )
                                                                        )
                                                        )
                                                        .then(
                                                                Commands.literal("get")
                                                                        .executes(
                                                                                context -> getLimit(context.getSource(), EntityArgument.getPlayer(context, "target"), Optional.empty())
                                                                        )
                                                                        .then(
                                                                                Commands.argument("id", ResourceArgument.resource(buildContext, NVRegistries.Keys.SENTIENT_UPGRADES))
                                                                                        .executes(
                                                                                                context -> getLimit(context.getSource(), EntityArgument.getPlayer(context, "target"), Optional.of(ResourceArgument.getResource(context, "id", NVRegistries.Keys.SENTIENT_UPGRADES)))
                                                                                        )
                                                                        )
                                                        )
                                                        .then(
                                                                Commands.literal("remove")
                                                                        .then(
                                                                                Commands.argument("id", ResourceArgument.resource(buildContext, NVRegistries.Keys.SENTIENT_UPGRADES))
                                                                                        .executes(context -> removeLimit(context.getSource(), EntityArgument.getPlayer(context, "target"), ResourceArgument.getResource(context, "id", NVRegistries.Keys.SENTIENT_UPGRADES)))
                                                                        )
                                                        )
                                                        .then(
                                                                Commands.literal("mode")
                                                                        .then(
                                                                                Commands.literal("allow")
                                                                                        .executes(context -> setMode(context.getSource(), EntityArgument.getPlayer(context, "target"), true))
                                                                        )
                                                                        .then(
                                                                                Commands.literal("deny")
                                                                                        .executes(context -> setMode(context.getSource(), EntityArgument.getPlayer(context, "target"), false))
                                                                        )
                                                        )
                                        )
                                        .then(
                                                Commands.literal("points")
                                                        .then(
                                                                Commands.literal("recalc")
                                                                        .executes(context -> recalcPoints(context.getSource(), EntityArgument.getPlayer(context, "target")))
                                                        )
                                                        .then(
                                                                Commands.literal("set-cap")
                                                                        .then(
                                                                                Commands.argument("cap", IntegerArgumentType.integer(0))
                                                                                        .executes(context -> setCap(context.getSource(), EntityArgument.getPlayer(context, "target"), IntegerArgumentType.getInteger(context, "cap")))
                                                                        )
                                                                        .then(
                                                                                Commands.literal("default")
                                                                                        .executes(context -> setCap(context.getSource(), EntityArgument.getPlayer(context, "target"), NeoVitae.SERVER_CONFIG.DEFAULT_UPGRADE_POINTS.get()))
                                                                        )
                                                                        .then(
                                                                                Commands.literal("evolved")
                                                                                        .executes(context -> setCap(context.getSource(), EntityArgument.getPlayer(context, "target"), NeoVitae.SERVER_CONFIG.EVOLUTION_UPGRADE_POINTS.get()))
                                                                        )
                                                        )
                                        )
                        )
        );
    }

    private static int setMode(CommandSourceStack source, ServerPlayer target, boolean mode) throws CommandSyntaxException {
        if (SentientHelper.isNeverValid(target)) {
            throw ERROR_NO_LIVING_HOLDER.create(target.getName());
        }
        ItemStack chest = SentientHelper.getChest(target);
        UpgradeLimits limits = chest.getOrDefault(NVDataComponents.LIMITS, UpgradeLimits.EMPTY);
        chest.set(NVDataComponents.LIMITS, new UpgradeLimits(mode, limits.limits().clone()));

        return Command.SINGLE_SUCCESS;
    }

    private static int removeLimit(CommandSourceStack source, ServerPlayer target, Holder<SentientUpgrade> id) throws CommandSyntaxException {
        if (SentientHelper.isNeverValid(target)) {
            throw ERROR_NO_LIVING_HOLDER.create(target.getName());
        }
        ItemStack chest = SentientHelper.getChest(target);
        UpgradeLimits limits = chest.getOrDefault(NVDataComponents.LIMITS, UpgradeLimits.EMPTY);
        Object2FloatOpenHashMap<Holder<SentientUpgrade>> limitMap = limits.limits().clone();
        limitMap.removeFloat(id);
        chest.set(NVDataComponents.LIMITS, new UpgradeLimits(limits.allowOthers(), limitMap));

        return Command.SINGLE_SUCCESS;
    }

    private static int setCap(CommandSourceStack source, ServerPlayer target, int amount) throws CommandSyntaxException {
        if (SentientHelper.isNeverValid(target)) {
            throw ERROR_NO_LIVING_HOLDER.create(target.getName());
        }
        ItemStack chest = SentientHelper.getChest(target);
        chest.set(NVDataComponents.CURRENT_MAX_UPGRADE_POINTS, amount);
        source.sendSuccess(() -> Component.translatable("commands.neovitae.cap.success", amount), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int recalcPoints(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException {
        if (SentientHelper.isNeverValid(target)) {
            throw ERROR_NO_LIVING_HOLDER.create(target.getName());
        }
        int calculated = SentientHelper.recalcPoints(target);
        source.sendSuccess(() -> Component.translatable("commands.neovitae.recalc.success", calculated), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int setUpgrade(CommandSourceStack source, ServerPlayer target, Holder<SentientUpgrade> id, int exp) throws CommandSyntaxException {
        if (SentientHelper.isNeverValid(target)) {
            throw ERROR_NO_LIVING_HOLDER.create(target.getName());
        }
        ItemStack chest = SentientHelper.getChest(target);
        Object2FloatOpenHashMap<Holder<SentientUpgrade>> map = new Object2FloatOpenHashMap<>();
        map.putAll(chest.getOrDefault(NVDataComponents.UPGRADES, SentientStats.EMPTY).upgrades());
        map.put(id, exp);
        chest.set(NVDataComponents.UPGRADES, new SentientStats(map));

        source.sendSuccess(() -> Component.translatable("commands.neovitae.upgrade.set", Component.translatable(SentientUpgrade.descriptionId(id.getKey())), exp, target.getName()), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int getUpgrades(CommandSourceStack source, Player target, Optional<Holder<SentientUpgrade>> filter) throws CommandSyntaxException {
        if (SentientHelper.isNeverValid(target)) { // if this check fails the applied upgrades can never take effect
            throw ERROR_NO_LIVING_HOLDER.create(target.getName());
        }

        ItemStack chestStack = SentientHelper.getChest(target);
        SentientStats stats = chestStack.getOrDefault(NVDataComponents.UPGRADES, SentientStats.EMPTY);
        MutableComponent result = Component.empty();
        if (filter.isEmpty()) {
            stats.object2FloatEntrySet().forEach(entry -> {
                result.append(Component.translatable(SentientUpgrade.descriptionId(entry.getKey().getKey())));
                result.append(Component.literal(": " + entry.getFloatValue() + "exp\n"));
            });
        } else {
            result.append(Component.translatable(SentientUpgrade.descriptionId(filter.get().getKey())));
            result.append(Component.literal(": " + stats.upgrades().getFloat(filter.get()) + "exp"));
        }

        source.sendSuccess(() -> Component.translatable("commands.neovitae.upgrade.get", target.getName()).append(result), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int setLimit(CommandSourceStack source, ServerPlayer target, Holder<SentientUpgrade> id, int exp) throws CommandSyntaxException {
        if (SentientHelper.isNeverValid(target)) {
            throw ERROR_NO_LIVING_HOLDER.create(target.getName());
        }
        ItemStack chest = SentientHelper.getChest(target);
        Object2FloatOpenHashMap<Holder<SentientUpgrade>> map = new Object2FloatOpenHashMap<>();
        UpgradeLimits limits = chest.getOrDefault(NVDataComponents.LIMITS, UpgradeLimits.EMPTY);
        map.putAll(limits.limits());
        map.put(id, exp);
        chest.set(NVDataComponents.LIMITS, new UpgradeLimits(limits.allowOthers(), map));

        source.sendSuccess(() -> Component.translatable("commands.neovitae.limit.set", Component.translatable(SentientUpgrade.descriptionId(id.getKey())), exp, target.getName()), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int getLimit(CommandSourceStack source, Player target, Optional<Holder<SentientUpgrade>> filter) throws CommandSyntaxException {
        if (SentientHelper.isNeverValid(target)) { // if this check fails the applied upgrades can never take effect
            throw ERROR_NO_LIVING_HOLDER.create(target.getName());
        }

        ItemStack chestStack = SentientHelper.getChest(target);
        UpgradeLimits stats = chestStack.getOrDefault(NVDataComponents.LIMITS, UpgradeLimits.EMPTY);
        MutableComponent result = Component.empty();
        Component mode = Component.translatable("commands.neovitae.limit.mode." + (stats.allowOthers() ? "allow" : "deny"));
        if (filter.isEmpty()) {
            stats.limits().object2FloatEntrySet().forEach(entry -> {
                result.append(Component.translatable(SentientUpgrade.descriptionId(entry.getKey().getKey())));
                result.append(Component.literal(": " + entry.getFloatValue() + "exp\n"));
            });
        } else {
            result.append(Component.translatable(SentientUpgrade.descriptionId(filter.get().getKey())));
            result.append(Component.literal(": " + stats.getLimit(filter.get()) + "exp"));
        }

        source.sendSuccess(() -> Component.translatable("commands.neovitae.limit.get", target.getName(), mode).append(result), true);
        return Command.SINGLE_SUCCESS;
    }
}
