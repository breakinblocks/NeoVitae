package com.breakinblocks.neovitae.impl;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.api.will.IPlayerSpiritusHandler;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.will.PlayerSpiritusHandler;

public class PlayerSpiritusHandlerImpl implements IPlayerSpiritusHandler {

    public static final PlayerSpiritusHandlerImpl INSTANCE = new PlayerSpiritusHandlerImpl();

    private PlayerSpiritusHandlerImpl() {}

    @Override
    public double getTotalSpiritus(SpiritusType type, Player player) {
        return PlayerSpiritusHandler.getTotalSpiritus(type, player);
    }

    @Override
    public SpiritusType getLargestSpiritusType(Player player) {
        return PlayerSpiritusHandler.getLargestSpiritusType(player);
    }

    @Override
    public boolean isSpiritusFull(SpiritusType type, Player player) {
        return PlayerSpiritusHandler.isSpiritusFull(type, player);
    }

    @Override
    public double consumeSpiritus(SpiritusType type, Player player, double amount) {
        return PlayerSpiritusHandler.consumeSpiritus(type, player, amount);
    }

    @Override
    public ItemStack addSpiritus(Player player, ItemStack spiritusStack) {
        return PlayerSpiritusHandler.addSpiritus(player, spiritusStack);
    }

    @Override
    public double addSpiritus(SpiritusType type, Player player, double amount) {
        return PlayerSpiritusHandler.addSpiritus(type, player, amount);
    }

    @Override
    public double addSpiritus(SpiritusType type, Player player, double amount, ItemStack ignored) {
        return PlayerSpiritusHandler.addSpiritus(type, player, amount, ignored);
    }
}
