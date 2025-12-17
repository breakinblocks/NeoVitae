package com.breakinblocks.neovitae.common.item;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.ClientHandler;
import com.breakinblocks.neovitae.common.block.BMBlocks;
import com.breakinblocks.neovitae.common.block.BlockRitualStone;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneTile;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.ritual.*;

import java.util.*;

/**
 * The Ritual Diviner is used to build rituals by automatically placing ritual stones.
 * - Right-click on a Master Ritual Stone to begin building
 * - Sneak + right-click on MRS to show ritual hologram
 * - Sneak + right-click in air to cycle through rituals (backwards)
 * - Right-click in air to cycle facing direction
 * - Hold right-click in air to continuously cycle
 */
public class ItemRitualDiviner extends Item {

    public static final String TOOLTIP_BASE = "tooltip.neovitae.diviner.";

    private final int type;

    public ItemRitualDiviner(int type) {
        super(new Item.Properties()
                .stacksTo(1)
                .component(BMDataComponents.CURRENT_RITUAL.get(), "")
                .component(BMDataComponents.DIVINER_DIRECTION.get(), Direction.NORTH.get3DDataValue())
                .component(BMDataComponents.DIVINER_ACTIVATED.get(), false)
                .component(BMDataComponents.DIVINER_STORED_POS.get(), BlockPos.ZERO));
        this.type = type;
    }

    // ==================== State Management ====================

    public boolean isActivated(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(BMDataComponents.DIVINER_ACTIVATED.get()));
    }

    public void setActivated(ItemStack stack, boolean activated) {
        stack.set(BMDataComponents.DIVINER_ACTIVATED.get(), activated);
    }

    public BlockPos getStoredPos(ItemStack stack) {
        BlockPos pos = stack.get(BMDataComponents.DIVINER_STORED_POS.get());
        return pos != null ? pos : BlockPos.ZERO;
    }

    public void setStoredPos(ItemStack stack, BlockPos pos) {
        stack.set(BMDataComponents.DIVINER_STORED_POS.get(), pos);
    }

    public Direction getDirection(ItemStack stack) {
        Integer dir = stack.get(BMDataComponents.DIVINER_DIRECTION.get());
        if (dir == null || dir == 0) return Direction.NORTH;
        return Direction.from3DDataValue(dir);
    }

    public void setDirection(ItemStack stack, Direction direction) {
        stack.set(BMDataComponents.DIVINER_DIRECTION.get(), direction.get3DDataValue());
    }

    public String getCurrentRitualId(ItemStack stack) {
        String id = stack.get(BMDataComponents.CURRENT_RITUAL.get());
        return id != null ? id : "";
    }

    public void setCurrentRitual(ItemStack stack, String ritualId) {
        stack.set(BMDataComponents.CURRENT_RITUAL.get(), ritualId);
    }

    public Ritual getCurrentRitual(ItemStack stack) {
        String id = getCurrentRitualId(stack);
        if (id.isEmpty()) return null;
        return RitualRegistry.getRitual(ResourceLocation.parse(id));
    }

    // ==================== Interaction ====================

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        // Check if we're clicking on a Master Ritual Stone
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof MasterRitualStoneTile)) {
            return InteractionResult.PASS;
        }

        // Shift+click on MRS shows the ritual hologram (client-side only)
        if (player.isShiftKeyDown()) {
            if (level.isClientSide()) {
                trySetDisplayedRitual(stack, level, pos);
            }
            return InteractionResult.SUCCESS;
        }

        // Normal click - start building ritual
        if (addRuneToRitual(stack, level, pos, player)) {
            setStoredPos(stack, pos);
            setActivated(stack, true);

            if (level.isClientSide()) {
                spawnParticles(level, pos.relative(context.getClickedFace()), 15);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    /**
     * Right-click handling.
     *
     * Control scheme:
     * - Shift+Right-click (anywhere): cycle rituals forward
     * - Right-click in air: cycle direction (N/E/S/W)
     * - Left-click in air: cycle rituals backwards (handled via LeftClickEmpty event)
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        setActivated(stack, false);

        // Shift+Right-click: cycle ritual forward (works regardless of what you're looking at)
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                cycleRitual(stack, player, false);
            }
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        }

        // Non-shift right-click: only cycle direction when not looking at a block
        HitResult ray = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (ray != null && ray.getType() == HitResult.Type.BLOCK) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }

        // Right-click in air: cycle direction
        if (!level.isClientSide()) {
            cycleDirection(stack, player);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @OnlyIn(Dist.CLIENT)
    public void trySetDisplayedRitual(ItemStack itemStack, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof MasterRitualStoneTile masterRitualStone) {
            Ritual ritual = getCurrentRitual(itemStack);

            if (ritual != null) {
                Direction direction = getDirection(itemStack);
                ClientHandler.setRitualHolo(masterRitualStone, ritual, direction, true);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void undisplayHologram() {
        ClientHandler.setRitualHoloToNull();
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof Player player)) return;
        if (!isActivated(stack)) return;

        // Auto-build mode - place one rune every 4 ticks
        if (entity.tickCount % 4 == 0) {
            BlockPos pos = getStoredPos(stack);
            if (!addRuneToRitual(stack, level, pos, player)) {
                setActivated(stack, false);
            } else if (level.isClientSide()) {
                spawnParticles(level, pos, 30);
            }
        }
    }

    // ==================== Ritual Building ====================

    /**
     * Attempts to add a single rune to the ritual.
     *
     * @return true if a rune was placed or if more work remains
     */
    public boolean addRuneToRitual(ItemStack stack, Level level, BlockPos masterPos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(masterPos);
        if (!(blockEntity instanceof MasterRitualStoneTile)) return false;

        Ritual ritual = getCurrentRitual(stack);
        if (ritual == null) return false;

        Direction direction = getDirection(stack);
        List<RitualComponent> components = Lists.newArrayList();
        ritual.gatherComponents(components::add);

        for (RitualComponent component : components) {
            if (!canPlaceRitualStone(component.runeType(), stack)) {
                return false;
            }

            BlockPos offset = rotateOffset(component.offset(), direction);
            BlockPos runePos = masterPos.offset(offset);
            BlockState state = level.getBlockState(runePos);

            // Check if this position already has the correct rune
            if (state.getBlock() instanceof BlockRitualStone ritualStone) {
                if (ritualStone.isRuneType(level, runePos, component.runeType())) {
                    // Already has correct rune, clear hologram on client and continue
                    if (level.isClientSide()) {
                        undisplayHologram();
                    }
                    continue;
                } else {
                    // Wrong rune type - replace it (with protection check)
                    if (!ritualStone.setRuneType(level, runePos, component.runeType(), player)) {
                        notifyBlockedBuild(player, runePos);
                        return false;
                    }
                    return true;
                }
            }

            // Check if we can place a block here
            BlockPlaceContext ctx = new BlockPlaceContext(level, player, InteractionHand.MAIN_HAND,
                    ItemStack.EMPTY, BlockHitResult.miss(Vec3.ZERO, Direction.UP, runePos));

            if (state.canBeReplaced(ctx)) {
                if (!consumeRitualStone(stack, level, player)) {
                    return false;
                }

                // Place the ritual stone (with protection check)
                Block blankStone = BMBlocks.BLANK_RITUAL_STONE.block().get();
                if (blankStone instanceof BlockRitualStone ritualStone) {
                    if (!ritualStone.setRuneType(level, runePos, component.runeType(), player)) {
                        notifyBlockedBuild(player, runePos);
                        return false;
                    }
                }
                return true;
            } else {
                notifyBlockedBuild(player, runePos);
                return false;
            }
        }

        // All runes placed
        return false;
    }

    private BlockPos rotateOffset(BlockPos offset, Direction direction) {
        return switch (direction) {
            case NORTH -> offset;
            case EAST -> new BlockPos(-offset.getZ(), offset.getY(), offset.getX());
            case SOUTH -> new BlockPos(-offset.getX(), offset.getY(), -offset.getZ());
            case WEST -> new BlockPos(offset.getZ(), offset.getY(), -offset.getX());
            default -> offset;
        };
    }

    /**
     * Consumes a ritual stone from the player's inventory.
     */
    private boolean consumeRitualStone(ItemStack diviner, Level level, Player player) {
        if (player.isCreative()) return true;

        for (ItemStack invStack : player.getInventory().items) {
            if (invStack.isEmpty()) continue;
            if (invStack.getItem() instanceof BlockItem blockItem) {
                if (blockItem.getBlock() instanceof BlockRitualStone) {
                    invStack.shrink(1);
                    return true;
                }
            }
        }
        return false;
    }

    // ==================== Cycling ====================

    public void cycleDirection(ItemStack stack, Player player) {
        Direction current = getDirection(stack);
        Direction next = switch (current) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            default -> Direction.NORTH;
        };
        setDirection(stack, next);
        player.displayClientMessage(
                Component.translatable(TOOLTIP_BASE + "currentDirection", capitalize(next.getName())), true);

        // Force sync inventory to client so tooltip updates
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            serverPlayer.inventoryMenu.broadcastChanges();
        }
    }

    public void cycleRitual(ItemStack stack, Player player, boolean reverse) {
        String currentId = getCurrentRitualId(stack);

        // Get all rituals this diviner can build, sorted by registry ID for consistency
        List<Ritual> rituals = RitualRegistry.getAllRituals().stream()
                .filter(r -> canDivinerBuildRitual(stack, r))
                .sorted(Comparator.comparing(r -> {
                    ResourceLocation id = RitualRegistry.getId(r);
                    return id != null ? id.toString() : "";
                }))
                .toList();

        if (rituals.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("chat.neovitae.diviner.noRituals").withStyle(ChatFormatting.RED), true);
            return;
        }

        // Find current index
        int currentIndex = -1;
        for (int i = 0; i < rituals.size(); i++) {
            ResourceLocation id = RitualRegistry.getId(rituals.get(i));
            if (id != null && id.toString().equals(currentId)) {
                currentIndex = i;
                break;
            }
        }

        // Calculate next index
        int nextIndex;
        if (currentIndex == -1) {
            // No current ritual selected, start at first
            nextIndex = 0;
        } else if (reverse) {
            // Go backwards (previous)
            nextIndex = (currentIndex - 1 + rituals.size()) % rituals.size();
        } else {
            // Go forwards (next)
            nextIndex = (currentIndex + 1) % rituals.size();
        }

        Ritual nextRitual = rituals.get(nextIndex);
        ResourceLocation nextId = RitualRegistry.getId(nextRitual);

        if (nextId != null) {
            setCurrentRitual(stack, nextId.toString());
            notifyRitualChange(nextRitual, player);

            // Force sync inventory to client so tooltip updates
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                serverPlayer.inventoryMenu.broadcastChanges();
            }
        }
    }

    private boolean canDivinerBuildRitual(ItemStack stack, Ritual ritual) {
        List<RitualComponent> components = Lists.newArrayList();
        ritual.gatherComponents(components::add);
        for (RitualComponent component : components) {
            if (!canPlaceRitualStone(component.runeType(), stack)) {
                return false;
            }
        }
        return true;
    }

    private void notifyRitualChange(Ritual ritual, Player player) {
        player.displayClientMessage(Component.translatable(ritual.getTranslationKey()), true);
    }

    private void notifyBlockedBuild(Player player, BlockPos pos) {
        player.displayClientMessage(
                Component.translatable("chat.neovitae.diviner.blockedBuild", pos.getX(), pos.getY(), pos.getZ()), true);
    }

    // ==================== Rune Capability ====================

    public boolean canPlaceRitualStone(EnumRuneType rune, ItemStack stack) {
        return switch (rune) {
            case BLANK, AIR, EARTH, FIRE, WATER -> true;
            case DUSK -> type >= 1;
            case DAWN -> type >= 2;
        };
    }

    public int getDivinerType() {
        return type;
    }

    // ==================== Tooltip ====================

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Ritual ritual = getCurrentRitual(stack);
        if (ritual != null) {
            tooltip.add(Component.translatable(TOOLTIP_BASE + "currentRitual",
                    Component.translatable(ritual.getTranslationKey())).withStyle(ChatFormatting.GRAY));

            boolean sneaking = Screen.hasShiftDown();
            boolean extraInfo = sneaking && Screen.hasAltDown();

            if (extraInfo) {
                // Shift+Alt: Show demon will type info if available
                tooltip.add(Component.empty());
                // DemonWillType info would go here if translatable
            } else if (sneaking) {
                // Shift: Show direction and detailed rune counts
                tooltip.add(Component.translatable(TOOLTIP_BASE + "currentDirection",
                        capitalize(getDirection(stack).getName())).withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.empty());

                Map<EnumRuneType, Integer> runeCounts = countRunes(ritual);
                int total = 0;
                for (EnumRuneType runeType : EnumRuneType.values()) {
                    int count = runeCounts.getOrDefault(runeType, 0);
                    if (count > 0) {
                        tooltip.add(Component.translatable(TOOLTIP_BASE + runeType.translationKey, count)
                                .withStyle(runeType.colorCode));
                        total += count;
                    }
                }

                tooltip.add(Component.empty());
                tooltip.add(Component.translatable(TOOLTIP_BASE + "totalRune", total).withStyle(ChatFormatting.GRAY));
            } else {
                // Default: Show basic info and hint for extra info
                tooltip.add(Component.empty());
                // Show ritual description if available
                String infoKey = ritual.getTranslationKey() + ".info";
                tooltip.add(Component.translatable(infoKey).withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable(TOOLTIP_BASE + "extraInfo").withStyle(ChatFormatting.BLUE));
                tooltip.add(Component.translatable(TOOLTIP_BASE + "extraExtraInfo").withStyle(ChatFormatting.BLUE));
            }
        } else {
            tooltip.add(Component.translatable(TOOLTIP_BASE + "noRitual").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable(TOOLTIP_BASE + "cycleHint").withStyle(ChatFormatting.BLUE));
        }

        super.appendHoverText(stack, context, tooltip, flag);
    }

    private Map<EnumRuneType, Integer> countRunes(Ritual ritual) {
        Map<EnumRuneType, Integer> counts = new EnumMap<>(EnumRuneType.class);
        List<RitualComponent> components = Lists.newArrayList();
        ritual.gatherComponents(components::add);
        for (RitualComponent component : components) {
            counts.merge(component.runeType(), 1, Integer::sum);
        }
        return counts;
    }

    // ==================== Utilities ====================

    private static String capitalize(String str) {
        return StringUtils.capitalize(str.toLowerCase(Locale.ROOT));
    }

    public static void spawnParticles(Level level, BlockPos pos, int amount) {
        for (int i = 0; i < amount; i++) {
            double dx = level.random.nextGaussian() * 0.02;
            double dy = level.random.nextGaussian() * 0.02;
            double dz = level.random.nextGaussian() * 0.02;
            level.addParticle(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + level.random.nextFloat(),
                    pos.getY() + level.random.nextFloat(),
                    pos.getZ() + level.random.nextFloat(),
                    dx, dy, dz);
        }
    }
}
