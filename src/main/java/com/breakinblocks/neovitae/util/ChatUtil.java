package com.breakinblocks.neovitae.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatUtil {
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,###.##");

    private static final Map<UUID, Long> lastMessageTime = new HashMap<>();
    private static final long SPAM_COOLDOWN = 500; // 500ms cooldown between messages

    /**
     * Sends a message to a player, preventing spam by limiting message frequency.
     * Displays as action bar message.
     */
    public static void sendNoSpam(Player player, Component message) {
        if (player == null || player.level().isClientSide) {
            return;
        }

        UUID uuid = player.getUUID();
        long currentTime = System.currentTimeMillis();
        Long lastTime = lastMessageTime.get(uuid);

        if (lastTime == null || currentTime - lastTime >= SPAM_COOLDOWN) {
            player.displayClientMessage(message, true);
            lastMessageTime.put(uuid, currentTime);
        }
    }

    /**
     * Sends a message to a player without spam prevention.
     */
    public static void send(Player player, Component message) {
        if (player != null && !player.level().isClientSide) {
            player.displayClientMessage(message, false);
        }
    }

    private static final char[] ones = new char[]{'I', 'X', 'C', 'M'};
    private static final char[] fives = new char[]{'V', 'L', 'D'};

    public static String toRoman(int in) {
        String input = new StringBuilder(Integer.toString(in)).reverse().toString();
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            output.append(romanDigit(input.charAt(i), ones[i], fives[i], ones[i+1]));
        }
        return output.toString();
    }

    private static String romanDigit(char in, char one, char five, char ten) {
        return switch (in) {
            case '0' -> "";
            case '1' -> "" + one;
            case '2' -> "" + one + one;
            case '3' -> "" + one + one + one;
            case '4' -> "" + one + five;
            case '5' -> "" + five;
            case '6' -> "" + five + one;
            case '7' -> "" + five + one + one;
            case '8' -> "" + five + one + one + one;
            case '9' -> "" + one + ten;
            default -> "[%s not found]".formatted(in);
        };
    }
}
