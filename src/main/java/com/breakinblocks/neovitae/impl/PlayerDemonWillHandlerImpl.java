package com.breakinblocks.neovitae.impl;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.api.will.IPlayerDemonWillHandler;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.will.PlayerDemonWillHandler;

public class PlayerDemonWillHandlerImpl implements IPlayerDemonWillHandler {

    public static final PlayerDemonWillHandlerImpl INSTANCE = new PlayerDemonWillHandlerImpl();

    private PlayerDemonWillHandlerImpl() {}

    @Override
    public double getTotalDemonWill(EnumWillType type, Player player) {
        return PlayerDemonWillHandler.getTotalDemonWill(type, player);
    }

    @Override
    public EnumWillType getLargestWillType(Player player) {
        return PlayerDemonWillHandler.getLargestWillType(player);
    }

    @Override
    public boolean isDemonWillFull(EnumWillType type, Player player) {
        return PlayerDemonWillHandler.isDemonWillFull(type, player);
    }

    @Override
    public double consumeDemonWill(EnumWillType type, Player player, double amount) {
        return PlayerDemonWillHandler.consumeDemonWill(type, player, amount);
    }

    @Override
    public ItemStack addDemonWill(Player player, ItemStack willStack) {
        return PlayerDemonWillHandler.addDemonWill(player, willStack);
    }

    @Override
    public double addDemonWill(EnumWillType type, Player player, double amount) {
        return PlayerDemonWillHandler.addDemonWill(type, player, amount);
    }

    @Override
    public double addDemonWill(EnumWillType type, Player player, double amount, ItemStack ignored) {
        return PlayerDemonWillHandler.addDemonWill(type, player, amount, ignored);
    }
}
