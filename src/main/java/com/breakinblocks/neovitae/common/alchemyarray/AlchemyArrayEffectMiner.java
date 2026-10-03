package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.ritual.RitualHelper;
import com.breakinblocks.neovitae.util.Utils;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AlchemyArrayEffectMiner extends AlchemyArrayEffect {

    private static final int IDLE_POLL_TICKS = 5;
    private static final int FIRST_FAIL_DELAY = 10;
    private static final int MAX_FAIL_DELAY = 100;
    private static final Direction[] OUTPUT_SIDES = {Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    private ItemStack tool = ItemStack.EMPTY;
    private final List<ItemStack> pending = new ArrayList<>();
    private double progress;
    private int cooldown;
    private int failDelay;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int activeCounter) {
        if (!(tile.getLevel() instanceof ServerLevel level)) return false;

        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        BlockPos arrayPos = tile.getBlockPos();
        if (!pending.isEmpty()) {
            flushPending(level, arrayPos);
            tile.setChanged();
            if (!pending.isEmpty()) {
                backOff();
                return false;
            }
        }

        BlockPos target = arrayPos.below();
        BlockState state = level.getBlockState(target);
        ItemStack activeTool = activeTool();
        if (!canMine(level, target, state, activeTool)) {
            progress = 0;
            cooldown = IDLE_POLL_TICKS;
            return false;
        }

        if (!hasRoomFor(level, arrayPos, new ItemStack(state.getBlock().asItem()))) {
            backOff();
            return false;
        }
        failDelay = 0;

        progress += miningRate(level, target, state, activeTool);
        if (progress < 1.0) return false;
        progress = 0;

        UUID owner = ownerOf(tile);
        if (!BlockProtectionHelper.canBreakBlock(level, target, owner)) {
            cooldown = IDLE_POLL_TICKS;
            return false;
        }

        FakePlayer fakePlayer = owner != null
                ? RitualHelper.createRitualFakePlayer(level, owner, "NeoVitae Miner Array", arrayPos)
                : FakePlayerFactory.getMinecraft(level);
        List<ItemStack> drops = RitualHelper.getBlockDrops(level, state, target, activeTool, fakePlayer);
        level.destroyBlock(target, false);

        if (!tool.isEmpty() && tool.isDamageableItem()) {
            tool.hurtAndBreak(1, level, fakePlayer, item -> {});
        }

        for (ItemStack drop : drops) {
            if (!drop.isEmpty()) pending.add(drop.copy());
        }
        flushPending(level, arrayPos);
        tile.setChanged();
        return false;
    }

    private ItemStack activeTool() {
        return tool.isEmpty() ? new ItemStack(Items.IRON_PICKAXE) : tool;
    }

    private boolean canMine(ServerLevel level, BlockPos target, BlockState state, ItemStack activeTool) {
        if (state.isAir() || !state.getFluidState().isEmpty()) return false;
        if (level.getBlockEntity(target) != null) return false;
        float hardness = state.getDestroySpeed(level, target);
        if (hardness < 0) return false;
        if (state.requiresCorrectToolForDrops() && !activeTool.isCorrectToolForDrops(state)) return false;
        return tool.isEmpty() || !tool.isDamageableItem() || tool.getDamageValue() < tool.getMaxDamage() - 1;
    }

    private double miningRate(ServerLevel level, BlockPos target, BlockState state, ItemStack activeTool) {
        float hardness = state.getDestroySpeed(level, target);
        if (hardness <= 0) return 1.0;
        float speed = activeTool.getDestroySpeed(state);
        if (speed > 1.0f) {
            Holder<Enchantment> efficiency = level.registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.EFFICIENCY);
            int levelOfEfficiency = EnchantmentHelper.getItemEnchantmentLevel(efficiency, activeTool);
            if (levelOfEfficiency > 0) {
                speed += levelOfEfficiency * levelOfEfficiency + 1;
            }
        }
        boolean harvestable = !state.requiresCorrectToolForDrops() || activeTool.isCorrectToolForDrops(state);
        return speed / hardness / (harvestable ? 30.0 : 100.0);
    }

    private void backOff() {
        failDelay = failDelay == 0 ? FIRST_FAIL_DELAY : Math.min(MAX_FAIL_DELAY, failDelay * 2);
        cooldown = failDelay;
    }

    private UUID ownerOf(AlchemyArrayBlockEntity tile) {
        Binding binding = tile.getOwnerBinding();
        return binding.isEmpty() ? null : binding.uuid();
    }

    private List<ResourceHandler<ItemResource>> outputs(Level level, BlockPos arrayPos) {
        List<ResourceHandler<ItemResource>> handlers = new ArrayList<>();
        for (Direction dir : OUTPUT_SIDES) {
            ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, arrayPos.relative(dir), dir.getOpposite());
            if (handler != null) handlers.add(handler);
        }
        return handlers;
    }

    private boolean hasRoomFor(Level level, BlockPos arrayPos, ItemStack probe) {
        for (ResourceHandler<ItemResource> handler : outputs(level, arrayPos)) {
            if (probe.isEmpty()) {
                for (int i = 0; i < handler.size(); i++) {
                    if (handler.getResource(i).isEmpty()) return true;
                }
            } else if (Utils.insertItemStacked(handler, probe.copy(), true).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void flushPending(Level level, BlockPos arrayPos) {
        List<ResourceHandler<ItemResource>> handlers = outputs(level, arrayPos);
        List<ItemStack> leftover = new ArrayList<>();
        for (ItemStack stack : pending) {
            ItemStack remaining = stack;
            for (ResourceHandler<ItemResource> handler : handlers) {
                remaining = Utils.insertItemStacked(handler, remaining, false);
                if (remaining.isEmpty()) break;
            }
            if (!remaining.isEmpty()) leftover.add(remaining);
        }
        pending.clear();
        pending.addAll(leftover);
    }

    public ItemStack swapTool(ItemStack newTool) {
        ItemStack old = tool;
        tool = newTool;
        progress = 0;
        return old;
    }

    public Component toolName() {
        return activeTool().getHoverName();
    }

    @Override
    public boolean onUse(AlchemyArrayBlockEntity tile, Player player) {
        if (tool.isEmpty()) return false;
        ItemStack removed = swapTool(ItemStack.EMPTY);
        if (!player.getInventory().add(removed)) {
            player.drop(removed, false);
        }
        player.sendOverlayMessage(Component.translatable("chat.neovitae.miner_array.removed", removed.getHoverName()));
        tile.setChanged();
        return true;
    }

    @Override
    public void onRemoved(AlchemyArrayBlockEntity tile) {
        Level level = tile.getLevel();
        if (level == null) return;
        BlockPos pos = tile.getBlockPos();
        if (!tool.isEmpty()) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), tool);
            tool = ItemStack.EMPTY;
        }
        for (ItemStack stack : pending) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
        }
        pending.clear();
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        progress = tag.getDoubleOr("progress", 0);
        cooldown = tag.getIntOr("cooldown", 0);
        failDelay = tag.getIntOr("failDelay", 0);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.putDouble("progress", progress);
        tag.putInt("cooldown", cooldown);
        tag.putInt("failDelay", failDelay);
    }

    @Override
    public List<ItemStack> saveItems() {
        if (tool.isEmpty() && pending.isEmpty()) return List.of();
        List<ItemStack> items = new ArrayList<>();
        items.add(tool);
        items.addAll(pending);
        return items;
    }

    @Override
    public void loadItems(List<ItemStack> items) {
        pending.clear();
        tool = items.isEmpty() ? ItemStack.EMPTY : items.getFirst();
        for (int i = 1; i < items.size(); i++) {
            if (!items.get(i).isEmpty()) pending.add(items.get(i));
        }
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectMiner();
    }
}
