package com.breakinblocks.neovitae.common.item;

import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.network.NVPayloads;
import com.breakinblocks.neovitae.common.network.RitualCodePayload;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.IMasterRitualStone;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualLayouts;
import com.breakinblocks.neovitae.ritual.RitualRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemRitualDesigner extends Item {

    public ItemRitualDesigner(Item.Properties props) {
        super(props.stacksTo(1));
    }

    private static boolean hasCorner1(ItemStack stack) {
        return stack.has(NVDataComponents.RITUAL_CORNER1.get());
    }

    private static boolean hasCorner2(ItemStack stack) {
        return stack.has(NVDataComponents.RITUAL_CORNER2.get());
    }

    private static BlockPos getCorner1(ItemStack stack) {
        return stack.get(NVDataComponents.RITUAL_CORNER1.get());
    }

    private static BlockPos getCorner2(ItemStack stack) {
        return stack.get(NVDataComponents.RITUAL_CORNER2.get());
    }

    private static void setCorner1(ItemStack stack, BlockPos pos) {
        stack.set(NVDataComponents.RITUAL_CORNER1.get(), pos);
    }

    private static void setCorner2(ItemStack stack, BlockPos pos) {
        stack.set(NVDataComponents.RITUAL_CORNER2.get(), pos);
    }

    private static void clearCorners(ItemStack stack) {
        stack.remove(NVDataComponents.RITUAL_CORNER1.get());
        stack.remove(NVDataComponents.RITUAL_CORNER2.get());
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.hasPermissions(2)) return InteractionResult.PASS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());
        if (!(be instanceof IMasterRitualStone)) return InteractionResult.PASS;
        return useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.hasPermissions(2)) {
            return InteractionResultHolder.fail(stack);
        }

        if (player.isShiftKeyDown()) {
            clearCorners(stack);
            player.displayClientMessage(
                    Component.literal("Positions cleared!").withStyle(ChatFormatting.YELLOW),
                    true);
            if (!level.isClientSide) {
                level.playSound(null, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH,
                        SoundSource.PLAYERS, 0.5F, 1.0F);
            }
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        if (!player.hasPermissions(2)) {
            player.displayClientMessage(
                    Component.literal("Ritual Designer requires operator permissions")
                            .withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }

        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        BlockEntity blockEntity = level.getBlockEntity(clickedPos);

        if (player.isShiftKeyDown()) {
            if (!hasCorner1(stack)) {
                setCorner1(stack, clickedPos);
                stack.remove(NVDataComponents.RITUAL_CORNER2.get());
                player.displayClientMessage(
                        Component.literal("Corner 1 set to: ").withStyle(ChatFormatting.GREEN)
                                .append(Component.literal(clickedPos.toShortString()).withStyle(ChatFormatting.WHITE)),
                        true);
                level.playSound(null, clickedPos, SoundEvents.EXPERIENCE_ORB_PICKUP,
                        SoundSource.BLOCKS, 0.5F, 1.0F);
            } else if (!hasCorner2(stack)) {
                setCorner2(stack, clickedPos);
                player.displayClientMessage(
                        Component.literal("Corner 2 set to: ").withStyle(ChatFormatting.GREEN)
                                .append(Component.literal(clickedPos.toShortString()).withStyle(ChatFormatting.WHITE)),
                        true);
                level.playSound(null, clickedPos, SoundEvents.EXPERIENCE_ORB_PICKUP,
                        SoundSource.BLOCKS, 0.5F, 1.2F);
            } else {
                setCorner1(stack, clickedPos);
                stack.remove(NVDataComponents.RITUAL_CORNER2.get());
                player.displayClientMessage(
                        Component.literal("Reset! Corner 1 set to: ").withStyle(ChatFormatting.YELLOW)
                                .append(Component.literal(clickedPos.toShortString()).withStyle(ChatFormatting.WHITE)),
                        true);
                level.playSound(null, clickedPos, SoundEvents.EXPERIENCE_ORB_PICKUP,
                        SoundSource.BLOCKS, 0.5F, 0.8F);
            }
            return InteractionResult.SUCCESS;
        }

        if (blockEntity instanceof IMasterRitualStone) {
            if (level.isClientSide) return InteractionResult.SUCCESS;

            if (!hasCorner1(stack) || !hasCorner2(stack)) {
                player.displayClientMessage(
                        Component.literal("Please set both corners first!")
                                .withStyle(ChatFormatting.RED), true);
                player.displayClientMessage(
                        Component.literal("Shift + Right-click opposite corners of the ritual area")
                                .withStyle(ChatFormatting.GRAY), false);
                return InteractionResult.FAIL;
            }

            String code = generateRitualCode(level, getCorner1(stack), getCorner2(stack), clickedPos, player);
            if (code != null && player instanceof ServerPlayer serverPlayer) {
                NVPayloads.sendToPlayer(serverPlayer, new RitualCodePayload(code));
                player.displayClientMessage(
                        Component.literal("Ritual code copied to clipboard!")
                                .withStyle(ChatFormatting.GREEN), true);
                player.sendSystemMessage(Component.literal("=== RITUAL CODE START ===").withStyle(ChatFormatting.GOLD));
                for (String line : code.split("\n")) {
                    player.sendSystemMessage(Component.literal(line).withStyle(ChatFormatting.WHITE));
                }
                player.sendSystemMessage(Component.literal("=== RITUAL CODE END ===").withStyle(ChatFormatting.GOLD));
                level.playSound(null, clickedPos, SoundEvents.ENCHANTMENT_TABLE_USE,
                        SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private static String generateRitualCode(Level level, BlockPos pos1, BlockPos pos2, BlockPos masterPos, Player player) {
        Map<Block, EnumRuneType> runeTypes = ritualStoneMapping();

        int minX = Math.min(pos1.getX(), pos2.getX());
        int minY = Math.min(pos1.getY(), pos2.getY());
        int minZ = Math.min(pos1.getZ(), pos2.getZ());
        int maxX = Math.max(pos1.getX(), pos2.getX());
        int maxY = Math.max(pos1.getY(), pos2.getY());
        int maxZ = Math.max(pos1.getZ(), pos2.getZ());

        int sizeX = maxX - minX + 1;
        int sizeY = maxY - minY + 1;
        int sizeZ = maxZ - minZ + 1;
        player.displayClientMessage(
                Component.literal("Scanning area: " + sizeX + "x" + sizeY + "x" + sizeZ
                        + " (" + (sizeX * sizeY * sizeZ) + " blocks)")
                        .withStyle(ChatFormatting.GRAY), false);

        List<RuneData> runes = new ArrayList<>();
        Map<String, Integer> nonRuneBlockCounts = new HashMap<>();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (pos.equals(masterPos)) continue;

                    Block block = level.getBlockState(pos).getBlock();
                    if (block == Blocks.AIR || block == Blocks.CAVE_AIR || block == Blocks.VOID_AIR) continue;

                    EnumRuneType runeType = runeTypes.get(block);
                    if (runeType != null) {
                        runes.add(new RuneData(x - masterPos.getX(), y - masterPos.getY(), z - masterPos.getZ(), runeType));
                    } else {
                        String blockId = BuiltInRegistries.BLOCK.getKey(block).toString();
                        nonRuneBlockCounts.merge(blockId, 1, Integer::sum);
                    }
                }
            }
        }

        if (runes.isEmpty()) {
            if (!nonRuneBlockCounts.isEmpty()) {
                player.displayClientMessage(
                        Component.literal("No runes found. Top non-rune blocks in the volume:")
                                .withStyle(ChatFormatting.YELLOW), false);
                nonRuneBlockCounts.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                        .limit(5)
                        .forEach(e -> player.displayClientMessage(
                                Component.literal("  - " + e.getKey() + " (" + e.getValue() + "x)")
                                        .withStyle(ChatFormatting.GRAY), false));
            }
            player.displayClientMessage(
                    Component.literal("No rune blocks found in the selected area!")
                            .withStyle(ChatFormatting.RED), true);
            return null;
        }

        String conflict = checkRitualConflict(runes, level);
        if (conflict != null) {
            player.displayClientMessage(
                    Component.literal("CONFLICT: This pattern matches existing ritual: ")
                            .withStyle(ChatFormatting.RED)
                            .append(Component.literal(conflict).withStyle(ChatFormatting.YELLOW)),
                    true);
            player.displayClientMessage(
                    Component.literal("Modify the rune layout to make it unique.")
                            .withStyle(ChatFormatting.GRAY), false);
            return null;
        }

        player.displayClientMessage(
                Component.literal("Found " + runes.size() + " rune blocks")
                        .withStyle(ChatFormatting.AQUA), false);
        return formatCode(runes);
    }

    private static Map<Block, EnumRuneType> ritualStoneMapping() {
        Map<Block, EnumRuneType> map = new LinkedHashMap<>();
        map.put(NVBlocks.BLANK_RITUAL_STONE.block().get(), EnumRuneType.BLANK);
        map.put(NVBlocks.WATER_RITUAL_STONE.block().get(), EnumRuneType.WATER);
        map.put(NVBlocks.FIRE_RITUAL_STONE.block().get(), EnumRuneType.FIRE);
        map.put(NVBlocks.EARTH_RITUAL_STONE.block().get(), EnumRuneType.EARTH);
        map.put(NVBlocks.AIR_RITUAL_STONE.block().get(), EnumRuneType.AIR);
        map.put(NVBlocks.TENEBRAE_RITUAL_STONE.block().get(), EnumRuneType.TENEBRAE);
        map.put(NVBlocks.DEUS_RITUAL_STONE.block().get(), EnumRuneType.DEUS);
        return map;
    }

    private static String checkRitualConflict(List<RuneData> runes, Level level) {
        Set<String> scanned = runes.stream().map(RuneData::signature).collect(Collectors.toSet());
        for (Ritual existing : RitualRegistry.getAllRituals()) {
            List<RitualComponent> components = RitualLayouts.get(level, existing);
            if (components.size() != runes.size()) continue;

            Set<String> existingSet = components.stream()
                    .map(c -> c.offset().getX() + "," + c.offset().getY() + "," + c.offset().getZ()
                            + "," + c.runeType().name())
                    .collect(Collectors.toSet());

            if (existingSet.equals(scanned)) {
                ResourceLocation id = RitualRegistry.getId(existing);
                return id == null ? existing.getName() : id.toString();
            }
        }
        return null;
    }

    private static String formatCode(List<RuneData> runes) {
        StringBuilder code = new StringBuilder();
        code.append("@Override\n");
        code.append("public void gatherComponents(Consumer<RitualComponent> components) {\n");

        Map<Integer, List<RuneData>> runesByLayer = runes.stream()
                .collect(Collectors.groupingBy(r -> r.y));
        List<Integer> sortedLayers = new ArrayList<>(runesByLayer.keySet());
        Collections.sort(sortedLayers);

        if (sortedLayers.size() > 1 && layersAreIdentical(runesByLayer, sortedLayers)) {
            int minLayer = sortedLayers.get(0);
            int maxLayer = sortedLayers.get(sortedLayers.size() - 1);
            code.append("    for (int layer = ").append(minLayer)
                    .append("; layer <= ").append(maxLayer).append("; layer++) {\n");
            for (RuneData rune : runesByLayer.get(minLayer)) {
                code.append("        addRune(components, ")
                        .append(rune.x).append(", layer, ").append(rune.z)
                        .append(", EnumRuneType.").append(rune.type.name()).append(");\n");
            }
            code.append("    }\n");
        } else {
            runes.sort(Comparator.<RuneData>comparingInt(r -> r.y)
                    .thenComparingInt(r -> r.x)
                    .thenComparingInt(r -> r.z));
            for (RuneData rune : runes) {
                code.append("    addRune(components, ")
                        .append(rune.x).append(", ").append(rune.y).append(", ").append(rune.z)
                        .append(", EnumRuneType.").append(rune.type.name()).append(");\n");
            }
        }
        code.append("}\n");
        return code.toString();
    }

    private static boolean layersAreIdentical(Map<Integer, List<RuneData>> runesByLayer, List<Integer> sortedLayers) {
        Set<String> template = runesByLayer.get(sortedLayers.get(0)).stream()
                .map(r -> r.x + "," + r.z + "," + r.type.name())
                .collect(Collectors.toSet());
        for (int i = 1; i < sortedLayers.size(); i++) {
            Set<String> current = runesByLayer.get(sortedLayers.get(i)).stream()
                    .map(r -> r.x + "," + r.z + "," + r.type.name())
                    .collect(Collectors.toSet());
            if (!template.equals(current)) return false;
        }
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Dev Tool - Requires OP").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        tooltip.add(Component.literal("Shift + Right-click block: set corner").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("  (set opposite corners of the ritual volume)").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.literal("Right-click Master Ritual Stone: generate code").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Shift + Right-click air: clear corners").withStyle(ChatFormatting.GRAY));

        if (hasCorner1(stack)) {
            tooltip.add(Component.literal("Corner 1: " + getCorner1(stack).toShortString()).withStyle(ChatFormatting.GREEN));
        }
        if (hasCorner2(stack)) {
            tooltip.add(Component.literal("Corner 2: " + getCorner2(stack).toShortString()).withStyle(ChatFormatting.GREEN));
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }

    private record RuneData(int x, int y, int z, EnumRuneType type) {
        String signature() {
            return x + "," + y + "," + z + "," + type.name();
        }
    }
}
