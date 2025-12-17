package com.breakinblocks.neovitae.will;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.effect.BMMobEffects;
import com.breakinblocks.neovitae.common.item.BMItems;
import com.breakinblocks.neovitae.common.item.soul.MonsterSoulItem;
import com.breakinblocks.neovitae.common.item.soul.SentientAxeItem;
import com.breakinblocks.neovitae.common.item.soul.SentientPickaxeItem;
import com.breakinblocks.neovitae.common.item.soul.SentientScytheItem;
import com.breakinblocks.neovitae.common.item.soul.SentientShovelItem;
import com.breakinblocks.neovitae.common.item.soul.SentientSwordItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Event handler for demon will system.
 * Handles soul drops when entities are killed with sentient weapons or soul snares.
 */
@EventBusSubscriber(modid = NeoVitae.MODID)
public class WillEventHandler {

    // Base soul drop for snared mobs
    private static final double SNARE_BASE_DROP = 1.0;
    private static final double SNARE_RANDOM_DROP = 4.0;

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();

        if (killed.level().isClientSide()) {
            return;
        }

        // Check for soul snare effect first (works with any damage source)
        if (killed.hasEffect(BMMobEffects.SOUL_SNARE)) {
            handleSnareDrop(killed);
            return; // Snare drops don't stack with sentient weapon drops
        }

        // Check if the source of damage was a player with a sentient weapon
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        ItemStack weapon = player.getMainHandItem();
        if (weapon.isEmpty()) {
            return;
        }

        // Get looting level for bonus drops
        Holder<Enchantment> lootingEnchant = killed.level().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING);
        int looting = weapon.getEnchantmentLevel(lootingEnchant);

        List<ItemStack> soulDrops = null;

        // Check what type of sentient weapon is being used
        if (weapon.getItem() instanceof SentientSwordItem sword) {
            soulDrops = sword.getRandomDemonWillDrop(killed, player, weapon, looting);
        } else if (weapon.getItem() instanceof SentientAxeItem axe) {
            soulDrops = axe.getRandomDemonWillDrop(killed, player, weapon, looting);
        } else if (weapon.getItem() instanceof SentientPickaxeItem pickaxe) {
            soulDrops = pickaxe.getRandomDemonWillDrop(killed, player, weapon, looting);
        } else if (weapon.getItem() instanceof SentientShovelItem shovel) {
            soulDrops = shovel.getRandomDemonWillDrop(killed, player, weapon, looting);
        } else if (weapon.getItem() instanceof SentientScytheItem scythe) {
            soulDrops = scythe.getRandomDemonWillDrop(killed, player, weapon, looting);
        }

        // Drop the souls
        dropSouls(killed, soulDrops);
    }

    /**
     * Handles soul drops from mobs killed while affected by Soul Snare.
     */
    private static void handleSnareDrop(LivingEntity killed) {
        // Only hostile mobs drop souls
        if (killed.level().getDifficulty() == Difficulty.PEACEFUL || !(killed instanceof Enemy)) {
            return;
        }

        double willModifier = killed instanceof Slime ? 0.67 : 1;
        double soulAmount = willModifier * (SNARE_BASE_DROP + killed.level().random.nextDouble() * SNARE_RANDOM_DROP)
                * killed.getMaxHealth() / 20d;

        // Snared mobs always drop raw/default will
        MonsterSoulItem soulItem = BMItems.MONSTER_SOUL_RAW.get();
        ItemStack soulStack = soulItem.createWill(soulAmount);

        List<ItemStack> drops = new ArrayList<>();
        drops.add(soulStack);
        dropSouls(killed, drops);
    }

    /**
     * Drops soul items at the killed entity's location.
     */
    private static void dropSouls(LivingEntity killed, List<ItemStack> soulDrops) {
        if (soulDrops != null && !soulDrops.isEmpty()) {
            for (ItemStack soulStack : soulDrops) {
                if (!soulStack.isEmpty()) {
                    ItemEntity itemEntity = new ItemEntity(killed.level(),
                            killed.getX(), killed.getY() + 0.5, killed.getZ(), soulStack);
                    itemEntity.setDefaultPickUpDelay();
                    killed.level().addFreshEntity(itemEntity);
                }
            }
        }
    }

    /**
     * Handles automatic absorption of monster souls into tartaric gems on pickup.
     * When a player picks up a monster soul, it automatically fills gems in their inventory.
     */
    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        ItemStack pickedUp = event.getItemEntity().getItem();
        Player player = event.getPlayer();

        if (pickedUp.isEmpty() || player.level().isClientSide()) {
            return;
        }

        // Only handle monster soul items
        if (!(pickedUp.getItem() instanceof IDemonWill will)) {
            return;
        }

        // Try to fill gems in player's inventory
        ItemStack remaining = PlayerDemonWillHandler.addDemonWill(player, pickedUp.copy());

        if (remaining.isEmpty()) {
            // Fully absorbed into gems - consume the item entity
            event.getItemEntity().discard();
            // The item entity is now gone, so pickup will naturally fail
        } else if (remaining.getItem() instanceof IDemonWill remainingWill &&
                   remainingWill.getWill(will.getType(remaining), remaining) < will.getWill(will.getType(pickedUp), pickedUp)) {
            // Partially absorbed - update the item entity with remaining will amount
            event.getItemEntity().setItem(remaining);
        }
        // If nothing was absorbed, let normal pickup proceed
    }
}
