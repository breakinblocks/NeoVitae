package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ritual that replenishes hunger for nearby players using food from an inventory.
 */
public class RitualFullStomach extends Ritual {

    public static final String HUNGER_RANGE = "hungerRange";
    public static final String CHEST_RANGE = "chestRange";

    public RitualFullStomach() {
        super("full_stomach", 0, 1000, "ritual." + NeoVitae.MODID + ".full_stomach");
        addBlockRange(HUNGER_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-10, -5, -10), 21, 11, 21));
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        setMaximumVolumeAndDistanceOfRange(HUNGER_RANGE, 5000, 20, 20);
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 5, 5);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        List<Player> players = RitualHelper.getEntitiesInRange(ctx, this, HUNGER_RANGE, Player.class);

        // Find chest inventory for food supply
        BlockPos chestPos = RitualHelper.getRangePositions(ctx.master(), this, CHEST_RANGE, ctx.masterPos()).getFirst();
        IItemHandler inventory = ctx.level().getCapability(Capabilities.ItemHandler.BLOCK, chestPos, null);

        int playersFed = 0;

        for (Player player : players) {
            int hunger = player.getFoodData().getFoodLevel();
            if (hunger >= 20) continue;

            // Try to find and use food from the chest inventory
            if (inventory != null) {
                boolean fed = feedFromInventory(player, inventory);
                if (fed) {
                    playersFed++;
                    continue;
                }
            }

            // Fallback: feed directly (original behavior) if no chest or no food found
            int toFeed = Math.min(20 - hunger, 4);
            player.getFoodData().eat(toFeed, 0.5f);
            playersFed++;
        }

        ctx.syphon(getRefreshCost() * playersFed);
    }

    /**
     * Attempts to find a food item in the inventory and feed it to the player.
     * @return true if the player was fed
     */
    private boolean feedFromInventory(Player player, IItemHandler inventory) {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            FoodProperties food = stack.getItem().getFoodProperties(stack, player);
            if (food == null) continue;

            // Extract 1 item from the inventory
            ItemStack extracted = inventory.extractItem(slot, 1, false);
            if (extracted.isEmpty()) continue;

            // Apply the food's nutrition and saturation to the player
            int nutrition = food.nutrition();
            float saturation = food.saturation();
            player.getFoodData().eat(nutrition, saturation);
            return true;
        }
        return false;
    }

    @Override
    public int getRefreshTime() {
        return 40;
    }

    @Override
    public int getRefreshCost() {
        return 100;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.WATER);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.WATER);
        addRune(components, 3, 0, 0, EnumRuneType.FIRE);
        addRune(components, -3, 0, 0, EnumRuneType.FIRE);
        addRune(components, 0, 0, 3, EnumRuneType.FIRE);
        addRune(components, 0, 0, -3, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualFullStomach();
    }
}
