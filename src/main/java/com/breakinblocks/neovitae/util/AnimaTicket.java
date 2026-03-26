package com.breakinblocks.neovitae.util;

import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * @deprecated Use {@link com.breakinblocks.neovitae.api.soul.AnimaTicket} instead
 */
@Deprecated
public class AnimaTicket {
    private static final Component EMPTY = Component.literal("");

    private final Component description;
    private final int amount;

    private AnimaTicket(Component description, int amount) {
        this.description = description;
        this.amount = amount;
    }

    private AnimaTicket(int amount) {
        this(EMPTY, amount);
    }

    public static AnimaTicket block(Level level, BlockPos pos, int amount) {
        return new AnimaTicket(Component.literal("block|" + level.dimension().location() + "|" + pos.asLong()), amount);
    }

    public static AnimaTicket block(Level level, BlockPos pos) {
        return block(level, pos, 0);
    }

    public static AnimaTicket item(ItemStack stack, int amount) {
        return new AnimaTicket(Component.literal("item|" + BuiltInRegistries.ITEM.getKey(stack.getItem())), amount);
    }

    public static AnimaTicket item(ItemStack stack, Level level, BlockPos pos, int amount) {
        return new AnimaTicket(Component.literal("item|" + BuiltInRegistries.ITEM.getKey(stack.getItem()) + "|" + level.dimension().location() + "|" + pos.asLong()), amount);
    }

    public static AnimaTicket item(ItemStack stack, Level level, Entity entity, int amount) {
        return new AnimaTicket(Component.literal("item|" + BuiltInRegistries.ITEM.getKey(stack.getItem()) + "|" + level.dimension().location() + "|" + entity.getStringUUID()), amount);
    }

    public static AnimaTicket command(CommandSource sender, String command, int amount) {
        return new AnimaTicket(Component.literal("command|" + command + "|" + sender.toString()), amount);
    }

    public static AnimaTicket create(int amount) {
        return new AnimaTicket(amount);
    }

    public Component getDescription() {
        return description;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isSyphon() {
        return amount < 0;
    }

    public com.breakinblocks.neovitae.api.soul.AnimaTicket toApiTicket() {
        return com.breakinblocks.neovitae.api.soul.AnimaTicket.create(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o instanceof AnimaTicket other)
            return other.getDescription().equals(description);

        return false;
    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }
}
