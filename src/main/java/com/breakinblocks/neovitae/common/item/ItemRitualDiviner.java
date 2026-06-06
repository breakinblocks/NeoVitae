// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.item;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
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
import org.apache.commons.lang3.StringUtils;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.ClientHandler;
import com.breakinblocks.neovitae.client.helper.ClientLevelAccess;
import com.breakinblocks.neovitae.ritual.RitualLayouts;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.BlockRitualStone;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.menu.RitualDivinerMenu;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.util.helper.KeyboardHelper;

import java.util.*;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.component.TooltipDisplay;

/**
 * The Ritual Diviner is used to build rituals by automatically placing ritual stones.
 * - Right-click on a Master Ritual Stone to begin building
 * - Sneak + right-click on MRS to show ritual hologram
 * - Right-click in air to open the ritual selection screen
 * - Sneak + right-click in air to cycle facing direction
 */
public class ItemRitualDiviner extends Item {

    public static final String TOOLTIP_BASE = "tooltip.neovitae.diviner.";

    private final int type;

    public ItemRitualDiviner(Item.Properties props, int type) {
        super(props
                .stacksTo(1)
                .component(NVDataComponents.CURRENT_RITUAL.get(), "")
                .component(NVDataComponents.DIVINER_DIRECTION.get(), Direction.NORTH.get3DDataValue())
                .component(NVDataComponents.DIVINER_ACTIVATED.get(), false)
                .component(NVDataComponents.DIVINER_STORED_POS.get(), BlockPos.ZERO));
        this.type = type;
    }


    public boolean isActivated(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(NVDataComponents.DIVINER_ACTIVATED.get()));
    }

    public void setActivated(ItemStack stack, boolean activated) {
        stack.set(NVDataComponents.DIVINER_ACTIVATED.get(), activated);
    }

    public BlockPos getStoredPos(ItemStack stack) {
        BlockPos pos = stack.get(NVDataComponents.DIVINER_STORED_POS.get());
        return pos != null ? pos : BlockPos.ZERO;
    }

    public void setStoredPos(ItemStack stack, BlockPos pos) {
        stack.set(NVDataComponents.DIVINER_STORED_POS.get(), pos);
    }

    public Direction getDirection(ItemStack stack) {
        Integer dir = stack.get(NVDataComponents.DIVINER_DIRECTION.get());
        if (dir == null || dir == 0) return Direction.NORTH;
        return Direction.from3DDataValue(dir);
    }

    public void setDirection(ItemStack stack, Direction direction) {
        stack.set(NVDataComponents.DIVINER_DIRECTION.get(), direction.get3DDataValue());
    }

    public String getCurrentRitualId(ItemStack stack) {
        String id = stack.get(NVDataComponents.CURRENT_RITUAL.get());
        return id != null ? id : "";
    }

    public void setCurrentRitual(ItemStack stack, String ritualId) {
        stack.set(NVDataComponents.CURRENT_RITUAL.get(), ritualId);
    }

    public Ritual getCurrentRitual(ItemStack stack) {
        String id = getCurrentRitualId(stack);
        if (id.isEmpty()) return null;
        return RitualRegistry.getRitual(Identifier.parse(id));
    }


    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getPlayer().getItemInHand(context.getHand());
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof MasterRitualStoneBlockEntity)) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            if (level.isClientSide()) {
                trySetDisplayedRitual(stack, level, pos);
            }
            return InteractionResult.SUCCESS;
        }

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

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        setActivated(stack, false);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                cycleDirection(stack, player);
            }
            return InteractionResult.SUCCESS;
        }

        HitResult ray = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        if (ray != null && ray.getType() == HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            openRitualSelector(serverPlayer, stack, level, hand);
        }
        return InteractionResult.SUCCESS;
    }

    private void openRitualSelector(ServerPlayer player, ItemStack stack, Level level, InteractionHand hand) {
        List<Identifier> buildable = getBuildableRitualIds(stack, level);
        if (buildable.isEmpty()) {
            player.sendOverlayMessage(
                    Component.translatable("chat.neovitae.diviner.noRituals").withStyle(ChatFormatting.RED));
            return;
        }
        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new RitualDivinerMenu(id, inv, hand, buildable),
                Component.translatable("container.neovitae.ritual_diviner")
        ), buf -> {
            buf.writeEnum(hand);
            buf.writeCollection(buildable, (b, rid) -> b.writeUtf(rid.toString()));
        });
    }

    public List<Identifier> getBuildableRitualIds(ItemStack stack, Level level) {
        return RitualRegistry.getAllRituals().stream()
                .filter(r -> canDivinerBuildRitual(stack, r, level))
                .map(RitualRegistry::getId)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Identifier::toString))
                .toList();
    }

    public void trySetDisplayedRitual(ItemStack itemStack, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof MasterRitualStoneBlockEntity masterRitualStone) {
            Ritual ritual = getCurrentRitual(itemStack);

            if (ritual != null) {
                Direction direction = getDirection(itemStack);
                ClientHandler.setRitualHolo(masterRitualStone, ritual, direction, true);
            }
        }
    }

    public void undisplayHologram() {
        ClientHandler.setRitualHoloToNull();
    }

    // @Override (removed: not an override in 26.1)
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof Player player)) return;
        if (!isActivated(stack)) return;

        if (entity.tickCount % 4 == 0) {
            BlockPos pos = getStoredPos(stack);
            if (level.isClientSide()) {
                spawnParticles(level, pos, 30);
            } else if (!addRuneToRitual(stack, level, pos, player)) {
                setActivated(stack, false);
            }
        }
    }


    /**
     * Attempts to add a single rune to the ritual.
     *
     * @return true if a rune was placed or if more work remains
     */
    public boolean addRuneToRitual(ItemStack stack, Level level, BlockPos masterPos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(masterPos);
        if (!(blockEntity instanceof MasterRitualStoneBlockEntity)) return false;

        Ritual ritual = getCurrentRitual(stack);
        if (ritual == null) return false;

        Direction direction = getDirection(stack);
        List<RitualComponent> components = RitualLayouts.get(level, ritual);

        for (RitualComponent component : components) {
            if (!canPlaceRitualStone(component.runeType(), stack)) {
                return false;
            }

            BlockPos offset = rotateOffset(component.offset(), direction);
            BlockPos runePos = masterPos.offset(offset);
            BlockState state = level.getBlockState(runePos);

            if (state.getBlock() instanceof BlockRitualStone ritualStone) {
                if (ritualStone.isRuneType(level, runePos, component.runeType())) {
                    if (level.isClientSide()) {
                        undisplayHologram();
                    }
                    continue;
                } else {
                    if (!ritualStone.setRuneType(level, runePos, component.runeType(), player)) {
                        notifyBlockedBuild(player, runePos);
                        return false;
                    }
                    return true;
                }
            }

            BlockPlaceContext ctx = new BlockPlaceContext(level, player, InteractionHand.MAIN_HAND,
                    ItemStack.EMPTY, BlockHitResult.miss(Vec3.ZERO, Direction.UP, runePos));

            if (state.canBeReplaced(ctx)) {
                if (!consumeRitualStone(stack, level, player)) {
                    return false;
                }

                Block blankStone = NVBlocks.BLANK_RITUAL_STONE.block().get();
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

    private boolean consumeRitualStone(ItemStack diviner, Level level, Player player) {
        if (player.isCreative()) return true;

        var inv = player.getInventory();
        int size = inv.getContainerSize();
        for (int i = 0; i < size; i++) {
            ItemStack invStack = inv.getItem(i);
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


    public void cycleDirection(ItemStack stack, Player player) {
        Direction current = getDirection(stack);
        Direction next = switch (current) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            default -> Direction.NORTH;
        };
        setDirection(stack, next);
        player.sendOverlayMessage(
                Component.translatable(TOOLTIP_BASE + "currentDirection", capitalize(next.getName())));

        // Force sync inventory to client so tooltip updates
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.inventoryMenu.broadcastChanges();
        }
    }

    private boolean canDivinerBuildRitual(ItemStack stack, Ritual ritual, Level level) {
        List<RitualComponent> components = RitualLayouts.get(level, ritual);
        for (RitualComponent component : components) {
            if (!canPlaceRitualStone(component.runeType(), stack)) {
                return false;
            }
        }
        return true;
    }

    private void notifyBlockedBuild(Player player, BlockPos pos) {
        player.sendOverlayMessage(
                Component.translatable("chat.neovitae.diviner.blockedBuild", pos.getX(), pos.getY(), pos.getZ()));
    }


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
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        Ritual ritual = getCurrentRitual(stack);
        if (ritual != null) {
            tooltip.accept(Component.translatable(TOOLTIP_BASE + "currentRitual",
                    Component.translatable(ritual.getTranslationKey())).withStyle(ChatFormatting.GRAY));

            boolean sneaking = KeyboardHelper.isShiftDown();
            boolean extraInfo = KeyboardHelper.isAltDown();

            if (extraInfo) {
                tooltip.accept(Component.empty());
            } else if (sneaking) {
                tooltip.accept(Component.translatable(TOOLTIP_BASE + "currentDirection",
                        capitalize(getDirection(stack).getName())).withStyle(ChatFormatting.GRAY));
                tooltip.accept(Component.empty());

                Map<EnumRuneType, Integer> runeCounts = countRunes(ritual);
                int total = 0;
                for (EnumRuneType runeType : EnumRuneType.values()) {
                    int count = runeCounts.getOrDefault(runeType, 0);
                    if (count > 0) {
                        tooltip.accept(Component.translatable(TOOLTIP_BASE + runeType.translationKey, count)
                                .withStyle(runeType.colorCode));
                        total += count;
                    }
                }

                tooltip.accept(Component.empty());
                tooltip.accept(Component.translatable(TOOLTIP_BASE + "totalRune", total).withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.accept(Component.empty());
                String infoKey = ritual.getTranslationKey() + ".info";
                tooltip.accept(Component.translatable(infoKey).withStyle(ChatFormatting.GRAY));
                tooltip.accept(Component.empty());
                tooltip.accept(Component.translatable(TOOLTIP_BASE + "extraInfo").withStyle(ChatFormatting.BLUE));
                tooltip.accept(Component.translatable(TOOLTIP_BASE + "extraExtraInfo").withStyle(ChatFormatting.BLUE));
            }
        } else {
            tooltip.accept(Component.translatable(TOOLTIP_BASE + "noRitual").withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable(TOOLTIP_BASE + "cycleHint").withStyle(ChatFormatting.BLUE));
        }}

    private Map<EnumRuneType, Integer> countRunes(Ritual ritual) {
        Map<EnumRuneType, Integer> counts = new EnumMap<>(EnumRuneType.class);
        List<RitualComponent> components = RitualLayouts.get(ClientLevelAccess.currentLevel(), ritual);
        for (RitualComponent component : components) {
            counts.merge(component.runeType(), 1, Integer::sum);
        }
        return counts;
    }


    private static String capitalize(String str) {
        return StringUtils.capitalize(str.toLowerCase(Locale.ROOT));
    }

    public static void spawnParticles(Level level, BlockPos pos, int amount) {
        for (int i = 0; i < amount; i++) {
            double dx = level.getRandom().nextGaussian() * 0.02;
            double dy = level.getRandom().nextGaussian() * 0.02;
            double dz = level.getRandom().nextGaussian() * 0.02;
            level.addParticle(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + level.getRandom().nextFloat(),
                    pos.getY() + level.getRandom().nextFloat(),
                    pos.getZ() + level.getRandom().nextFloat(),
                    dx, dy, dz);
        }
    }
}
