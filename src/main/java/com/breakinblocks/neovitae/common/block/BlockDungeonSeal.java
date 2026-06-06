// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2021-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2021-2025 StellaNera <https://github.com/stellanera98>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.blockentity.DungeonControllerBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.DungeonSealBlockEntity;
import com.breakinblocks.neovitae.common.entity.mob.DaemoniumDolorisEntity;
import com.breakinblocks.neovitae.common.item.dungeon.ItemDungeonKey;
import com.breakinblocks.neovitae.common.menu.DungeonSealMenu;
import com.breakinblocks.neovitae.structures.DungeonSynthesizer;

import java.util.List;
import java.util.Map;

public class BlockDungeonSeal extends Block implements EntityBlock {

    public static final BooleanProperty SPECIAL = BooleanProperty.create("special");

    public BlockDungeonSeal() {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.STONE)
                .strength(-1.0F, 3600000.0F)
                .noLootTable()
                .lightLevel(state -> state.getValue(SPECIAL) ? 11 : 7));
        this.registerDefaultState(this.stateDefinition.any().setValue(SPECIAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SPECIAL);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DungeonSealBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof DungeonSealBlockEntity seal)) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown() && level instanceof ServerLevel serverLevel) {
            ItemDungeonKey heldKey = player.getMainHandItem().getItem() instanceof ItemDungeonKey k ? k : null;
            sendDungeonDebugInfo(player, seal, heldKey, serverLevel);
            return InteractionResult.CONSUME;
        }

        openSealMenu(player, seal, pos);
        return InteractionResult.CONSUME;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                               BlockPos pos, Player player, InteractionHand hand,
                                               BlockHitResult hitResult) {
        // Pass through to useWithoutItem for everything except sneak+debug
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private void openSealMenu(Player player, DungeonSealBlockEntity seal, BlockPos pos) {
        if (player instanceof ServerPlayer serverPlayer) {
            int mask = DungeonSealMenu.computeValidKeyMask(seal);

            boolean hasAnyKey = false;
            for (int i = 0; i < DungeonSealMenu.KEY_TYPES.size(); i++) {
                if ((mask & (1 << i)) == 0) continue;
                Item keyItem = DungeonSealMenu.KEY_TYPES.get(i).get();
                for (int s = 0; s < player.getInventory().getContainerSize(); s++) {
                    if (player.getInventory().getItem(s).getItem() == keyItem) {
                        hasAnyKey = true;
                        break;
                    }
                }
                if (hasAnyKey) break;
            }

            if (!hasAnyKey && !player.getAbilities().instabuild) {
                player.displayClientMessage(
                        Component.translatable("chat.neovitae.dungeon.seal.noKeys")
                                .withStyle(ChatFormatting.RED), true);
                return;
            }

            serverPlayer.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new DungeonSealMenu(id, inv, seal, mask),
                    Component.translatable("container.neovitae.dungeon_seal")
            ), buf -> {
                buf.writeBlockPos(pos);
                buf.writeInt(mask);
            });
        }
    }

    private void sendDungeonDebugInfo(Player player, DungeonSealBlockEntity seal,
                                       @Nullable ItemDungeonKey key, ServerLevel level) {
        BlockEntity controllerBE = level.getBlockEntity(seal.getControllerPos());
        if (!(controllerBE instanceof DungeonControllerBlockEntity controller)) {
            player.sendSystemMessage(Component.literal("[Debug] No controller found at " + seal.getControllerPos())
                    .withStyle(ChatFormatting.RED));
            return;
        }

        DungeonSynthesizer synth = controller.getDungeonSynthesizer();
        if (synth == null) {
            player.sendSystemMessage(Component.literal("[Debug] Synthesizer is null")
                    .withStyle(ChatFormatting.RED));
            return;
        }

        player.sendSystemMessage(Component.literal("=== Dungeon Debug ===")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

        // Progression depth
        int rooms = synth.getActivatedDoors();
        int depth = synth.getDescriptorList().size();
        player.sendSystemMessage(Component.literal("Rooms opened: " + rooms + " | Depth (descriptors): " + depth)
                .withStyle(ChatFormatting.AQUA));

        // Active seals
        player.sendSystemMessage(Component.literal("Active seals: " + synth.getActiveSealCount())
                .withStyle(ChatFormatting.AQUA));

        // Mine thresholds
        boolean mineEntrancesMet = rooms >= 5 && depth >= 2;
        boolean mineKeyMet = rooms >= 10 && depth >= 3;
        player.sendSystemMessage(Component.literal("Mine Entrances threshold (5 rooms, 2 depth): "
                + (mineEntrancesMet ? "MET" : "NOT MET (" + rooms + "/5 rooms, " + depth + "/2 depth)"))
                .withStyle(mineEntrancesMet ? ChatFormatting.GREEN : ChatFormatting.RED));
        player.sendSystemMessage(Component.literal("Mine Key threshold (10 rooms, 3 depth): "
                + (mineKeyMet ? "MET" : "NOT MET (" + rooms + "/10 rooms, " + depth + "/3 depth)"))
                .withStyle(mineKeyMet ? ChatFormatting.GREEN : ChatFormatting.RED));

        // Special room buffer
        List<ResourceLocation> buffer = synth.getSpecialRoomBuffer();
        if (buffer.isEmpty()) {
            player.sendSystemMessage(Component.literal("Special room buffer: (empty)")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            player.sendSystemMessage(Component.literal("Special room buffer:")
                    .withStyle(ChatFormatting.YELLOW));
            for (ResourceLocation rl : buffer) {
                player.sendSystemMessage(Component.literal("  - " + rl)
                        .withStyle(ChatFormatting.YELLOW));
            }
        }

        // Placements since last special
        Map<ResourceLocation, Integer> placements = synth.getPlacementsSinceLastSpecial();
        if (!placements.isEmpty()) {
            player.sendSystemMessage(Component.literal("Placements since last special:")
                    .withStyle(ChatFormatting.GRAY));
            for (Map.Entry<ResourceLocation, Integer> entry : placements.entrySet()) {
                player.sendSystemMessage(Component.literal("  - " + entry.getKey() + ": " + entry.getValue())
                        .withStyle(ChatFormatting.GRAY));
            }
        }

        // Seal info
        player.sendSystemMessage(Component.literal("This seal's pools: " + seal.getPotentialRoomTypes())
                .withStyle(ChatFormatting.LIGHT_PURPLE));

        // Which keys work on this seal
        StringBuilder keyInfo = new StringBuilder("Valid keys: ");
        boolean anyValid = false;
        for (int i = 0; i < DungeonSealMenu.KEY_TYPES.size(); i++) {
            ItemDungeonKey kt = DungeonSealMenu.KEY_TYPES.get(i).get();
            if (kt.canOpenDoor(seal.getPotentialRoomTypes())) {
                if (anyValid) keyInfo.append(", ");
                keyInfo.append(kt.getKeyType());
                anyValid = true;
            }
        }
        if (!anyValid) keyInfo.append("(none)");
        player.sendSystemMessage(Component.literal(keyInfo.toString())
                .withStyle(ChatFormatting.LIGHT_PURPLE));

        // Rift status
        player.sendSystemMessage(Component.literal("Rift opened: " + controller.isRiftOpened()
                + (controller.getPortalPos() != null ? " | Portal pos: " + controller.getPortalPos() : " | No portal pos set"))
                .withStyle(controller.isRiftOpened() ? ChatFormatting.GREEN : ChatFormatting.GRAY));

        // Search for The Foreman
        List<DaemoniumDolorisEntity> foremans = level.getEntitiesOfClass(
                DaemoniumDolorisEntity.class,
                new AABB(seal.getControllerPos()).inflate(500),
                DaemoniumDolorisEntity::isForeman);

        if (foremans.isEmpty()) {
            player.sendSystemMessage(Component.literal("The Foreman: not found in dungeon")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            for (DaemoniumDolorisEntity foreman : foremans) {
                BlockPos fPos = foreman.blockPosition();
                player.sendSystemMessage(Component.literal("The Foreman: ALIVE at " + fPos.getX() + ", " + fPos.getY() + ", " + fPos.getZ()
                        + " | HP: " + String.format("%.0f/%.0f", foreman.getHealth(), foreman.getMaxHealth()))
                        .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            }
        }

        // Total available doors
        int totalDoors = synth.getAvailableDoorMasterMap().values().stream()
                .flatMap(m -> m.values().stream())
                .mapToInt(List::size)
                .sum();
        player.sendSystemMessage(Component.literal("Available door slots: " + totalDoors)
                .withStyle(ChatFormatting.AQUA));

        // Find all remaining seals in the dungeon
        List<DungeonSealBlockEntity> otherSeals = new java.util.ArrayList<>();
        for (var desc : synth.getDescriptorList()) {
            AABB box = desc.getAABB(BlockPos.ZERO);
            BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
            BlockPos max = BlockPos.containing(box.maxX - 1, box.maxY - 1, box.maxZ - 1);
            for (int cx = min.getX() >> 4; cx <= max.getX() >> 4; cx++) {
                for (int cz = min.getZ() >> 4; cz <= max.getZ() >> 4; cz++) {
                    var chunk = level.getChunkSource().getChunkNow(cx, cz);
                    if (chunk == null) continue;
                    for (BlockPos bePos : chunk.getBlockEntitiesPos()) {
                        if (box.contains(bePos.getX() + 0.5, bePos.getY() + 0.5, bePos.getZ() + 0.5)
                                && chunk.getBlockEntity(bePos) instanceof DungeonSealBlockEntity other
                                && !bePos.equals(seal.getBlockPos())) {
                            otherSeals.add(other);
                        }
                    }
                }
            }
        }

        if (otherSeals.isEmpty()) {
            player.sendSystemMessage(Component.literal("Remaining seals: none (this is the last one)")
                    .withStyle(ChatFormatting.GRAY));
        } else {
            player.sendSystemMessage(Component.literal("Remaining seals (" + (otherSeals.size() + 1) + " total including this one):")
                    .withStyle(ChatFormatting.DARK_AQUA));
            for (DungeonSealBlockEntity other : otherSeals) {
                BlockPos sp = other.getBlockPos();
                double dist = Math.sqrt(seal.getBlockPos().distSqr(sp));
                List<ResourceLocation> pools = other.getPotentialRoomTypes();
                String poolNames = pools.stream()
                        .map(rl -> rl.getPath().substring(rl.getPath().lastIndexOf('/') + 1))
                        .collect(java.util.stream.Collectors.joining(", "));
                player.sendSystemMessage(Component.literal("  - " + sp.getX() + ", " + sp.getY() + ", " + sp.getZ()
                        + " (" + String.format("%.0f", dist) + "m) [" + poolNames + "]")
                        .withStyle(ChatFormatting.DARK_AQUA));
            }
        }
    }
}
