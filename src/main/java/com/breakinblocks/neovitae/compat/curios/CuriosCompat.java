package com.breakinblocks.neovitae.compat.curios;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.living.LivingHelper;
import com.breakinblocks.neovitae.common.living.LivingUpgrade;
import com.breakinblocks.neovitae.common.registry.BMRegistries;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Curios API integration for Blood Magic.
 * Provides:
 * - Curio slots for sigils, tartaric gems, and training bracelet
 * - Living Armor socket slots that scale with the Curios Socket upgrade
 */
public class CuriosCompat {

    public static final String CURIOS_MODID = "curios";
    public static final String LIVING_ARMOUR_SOCKET_SLOT = "living_armour_socket";

    // ResourceKey for the curios_socket upgrade (matches datagen LivingUpgrades.CURIOS_SOCKET)
    public static final ResourceKey<LivingUpgrade> CURIOS_SOCKET_UPGRADE = ResourceKey.create(
            BMRegistries.Keys.LIVING_UPGRADES,
            NeoVitae.rl("curios_socket")
    );

    private static boolean curiosLoaded = false;

    /**
     * Checks if Curios is loaded.
     */
    public static boolean isCuriosLoaded() {
        return curiosLoaded;
    }

    /**
     * Initialize Curios compatibility.
     * Call this during mod construction.
     */
    public static void init(IEventBus modBus) {
        curiosLoaded = ModList.get().isLoaded(CURIOS_MODID);
        if (curiosLoaded) {
            NeoVitae.LOGGER.info("Curios detected - enabling compatibility");
            modBus.addListener(CuriosCompat::onInterModEnqueue);
        }
    }

    private static void onInterModEnqueue(InterModEnqueueEvent event) {
        // Curios 1.21+ uses data-driven slot registration via JSON
        // No IMC needed for slot registration
    }

    /**
     * Gets all items from a player's curios inventory.
     *
     * @param player The player to get curios from
     * @return A list of all equipped curios
     */
    public static NonNullList<ItemStack> getCuriosInventory(Player player) {
        if (!curiosLoaded) {
            return NonNullList.create();
        }

        Optional<ICuriosItemHandler> curioInv = CuriosApi.getCuriosInventory(player);
        if (curioInv.isEmpty()) {
            return NonNullList.create();
        }

        IItemHandlerModifiable itemHandler = curioInv.get().getEquippedCurios();
        NonNullList<ItemStack> inventory = NonNullList.create();
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                inventory.add(stack);
            }
        }
        return inventory;
    }

    /**
     * Finds a specific item in the player's curios slots.
     *
     * @param player The player to search
     * @param stack  The item to find (uses Item equality)
     * @return The SlotResult if found, or empty
     */
    public static Optional<SlotResult> findCurio(Player player, ItemStack stack) {
        if (!curiosLoaded) {
            return Optional.empty();
        }

        Optional<ICuriosItemHandler> curioInv = CuriosApi.getCuriosInventory(player);
        if (curioInv.isEmpty()) {
            return Optional.empty();
        }

        List<SlotResult> results = curioInv.get().findCurios(stack.getItem());
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Gets the level of the Curios Socket upgrade for a player.
     *
     * @param player The player to check
     * @return The upgrade level (0 if not present)
     */
    private static int getCuriosSocketLevel(Player player) {
        AtomicInteger level = new AtomicInteger(0);
        LivingHelper.runIterationOnPlayer(player, (upgrade, lvl) -> {
            if (upgrade.is(CURIOS_SOCKET_UPGRADE)) {
                level.set(Math.max(level.get(), lvl));
            }
        });
        return level.get();
    }

    /**
     * Recalculates the number of living armour socket slots based on the player's
     * Curios Socket upgrade level.
     *
     * @param player The player to update
     * @return The current upgrade level (0 if no upgrade or no living armor)
     */
    public static int recalculateCuriosSlots(Player player) {
        if (!curiosLoaded) {
            return 0;
        }

        Optional<ICuriosItemHandler> curioInv = CuriosApi.getCuriosInventory(player);
        if (curioInv.isEmpty()) {
            return 0;
        }

        Map<String, ICurioStacksHandler> curios = curioInv.get().getCurios();
        ICurioStacksHandler livingArmourSockets = curios.get(LIVING_ARMOUR_SOCKET_SLOT);
        if (livingArmourSockets == null) {
            return 0;
        }

        ResourceLocation modifierId = NeoVitae.rl("curios_socket_upgrade");

        if (LivingHelper.hasFullSet(player)) {
            int curiosLevel = getCuriosSocketLevel(player);

            // Remove existing modifier
            livingArmourSockets.removeModifier(modifierId);

            if (curiosLevel > 0) {
                // Add slots based on upgrade level (1 slot per level, up to 5)
                int bonusSlots = Math.min(curiosLevel, 5);
                livingArmourSockets.addTransientModifier(
                        new AttributeModifier(modifierId, bonusSlots, AttributeModifier.Operation.ADD_VALUE)
                );
            }
            return curiosLevel;
        } else {
            // No living armor - remove any bonus slots
            livingArmourSockets.removeModifier(modifierId);
            return 0;
        }
    }

    /**
     * Checks if the given item is in any of the player's curios slots.
     *
     * @param player The player to check
     * @param stack  The item to look for
     * @return true if the item is equipped as a curio
     */
    public static boolean isEquippedCurio(Player player, ItemStack stack) {
        return findCurio(player, stack).isPresent();
    }
}
