package com.breakinblocks.neovitae.api.soul;

import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A ticket representing an LP transaction in a player's Soul Network.
 * Soul tickets provide auditing information about what caused the transaction.
 *
 * <p>Use the static factory methods to create tickets for different contexts:</p>
 * <ul>
 *   <li>{@link #block(Level, BlockPos, int)} - For block-based operations (altars, ritual stones)</li>
 *   <li>{@link #item(ItemStack, int)} - For item-based operations (sigils, orbs)</li>
 *   <li>{@link #command(CommandSource, String, int)} - For command-based operations</li>
 *   <li>{@link #create(int)} - For simple operations without context</li>
 * </ul>
 */
public class SoulTicket {
    private static final Component EMPTY = Component.literal("");

    private final Component description;
    private final int amount;

    private SoulTicket(Component description, int amount) {
        this.description = description;
        this.amount = amount;
    }

    private SoulTicket(int amount) {
        this(EMPTY, amount);
    }

    /**
     * Creates a ticket for a block-based operation.
     *
     * @param level  The level containing the block
     * @param pos    The position of the block
     * @param amount The LP amount for this transaction
     * @return A new soul ticket
     */
    public static SoulTicket block(Level level, BlockPos pos, int amount) {
        return new SoulTicket(Component.literal("block|" + level.dimension().location() + "|" + pos.asLong()), amount);
    }

    /**
     * Creates a ticket for a block with zero amount (for ticket creation before knowing amount).
     */
    public static SoulTicket block(Level level, BlockPos pos) {
        return block(level, pos, 0);
    }

    /**
     * Creates a ticket for an item-based operation.
     *
     * @param stack  The item stack involved
     * @param amount The LP amount for this transaction
     * @return A new soul ticket
     */
    public static SoulTicket item(ItemStack stack, int amount) {
        return new SoulTicket(Component.literal("item|" + BuiltInRegistries.ITEM.getKey(stack.getItem())), amount);
    }

    /**
     * Creates a ticket for an item operation with location context.
     */
    public static SoulTicket item(ItemStack stack, Level level, BlockPos pos, int amount) {
        return new SoulTicket(Component.literal("item|" + BuiltInRegistries.ITEM.getKey(stack.getItem()) + "|" + level.dimension().location() + "|" + pos.asLong()), amount);
    }

    /**
     * Creates a ticket for an item operation with entity context.
     */
    public static SoulTicket item(ItemStack stack, Level level, Entity entity, int amount) {
        return new SoulTicket(Component.literal("item|" + BuiltInRegistries.ITEM.getKey(stack.getItem()) + "|" + level.dimension().location() + "|" + entity.getStringUUID()), amount);
    }

    /**
     * Creates a ticket for a command-based operation.
     */
    public static SoulTicket command(CommandSource sender, String command, int amount) {
        return new SoulTicket(Component.literal("command|" + command + "|" + sender.toString()), amount);
    }

    /**
     * Creates a simple ticket with just an amount.
     *
     * @param amount The LP amount for this transaction
     * @return A new soul ticket
     */
    public static SoulTicket create(int amount) {
        return new SoulTicket(amount);
    }

    /**
     * Gets the description of this ticket (for auditing/debugging).
     */
    public Component getDescription() {
        return description;
    }

    /**
     * Gets the LP amount for this transaction.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Returns true if this ticket represents a syphon (negative amount).
     */
    public boolean isSyphon() {
        return amount < 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o instanceof SoulTicket other)
            return other.getDescription().equals(description);

        return false;
    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }
}
