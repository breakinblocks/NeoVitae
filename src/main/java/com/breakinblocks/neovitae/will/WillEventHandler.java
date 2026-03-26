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
import com.breakinblocks.neovitae.common.attribute.NVAttributes;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.soul.MonsterSoulItem;
import com.breakinblocks.neovitae.common.item.soul.SentientAxeItem;
import com.breakinblocks.neovitae.common.item.soul.SentientPickaxeItem;
import com.breakinblocks.neovitae.common.item.soul.SentientScytheItem;
import com.breakinblocks.neovitae.common.item.soul.SentientShovelItem;
import com.breakinblocks.neovitae.common.item.soul.SentientSwordItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class WillEventHandler {

    private static final double SNARE_BASE_DROP = 1.0;
    private static final double SNARE_RANDOM_DROP = 4.0;

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();

        if (killed.level().isClientSide()) {
            return;
        }

        if (killed.hasEffect(NVMobEffects.SOUL_SNARE)) {
            Player snareKiller = event.getSource().getEntity() instanceof Player p ? p : null;
            handleSnareDrop(killed, snareKiller);
            return;
        }

        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        ItemStack weapon = player.getMainHandItem();
        if (weapon.isEmpty()) {
            return;
        }

        Holder<Enchantment> lootingEnchant = killed.level().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING);
        int looting = weapon.getEnchantmentLevel(lootingEnchant);

        List<ItemStack> soulDrops = null;

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

        dropSouls(killed, soulDrops);
    }

    private static void handleSnareDrop(LivingEntity killed, @Nullable Player killer) {
        if (killed.level().getDifficulty() == Difficulty.PEACEFUL || !(killed instanceof Enemy)) {
            return;
        }

        double willModifier = killed instanceof Slime ? 0.67 : 1;
        double soulAmount = willModifier * (SNARE_BASE_DROP + killed.level().random.nextDouble() * SNARE_RANDOM_DROP)
                * killed.getMaxHealth() / 20d;

        if (killer != null) {
            double bonusDemonWill = killer.getAttributeValue(NVAttributes.BONUS_DEMON_WILL);
            if (bonusDemonWill > 0) {
                soulAmount *= (1 + bonusDemonWill / 100);
            }
        }

        MonsterSoulItem soulItem = NVItems.MONSTER_SOUL_RAW.get();
        ItemStack soulStack = soulItem.createWill(soulAmount);

        List<ItemStack> drops = new ArrayList<>();
        drops.add(soulStack);
        dropSouls(killed, drops);
    }

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

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        ItemStack pickedUp = event.getItemEntity().getItem();
        Player player = event.getPlayer();

        if (pickedUp.isEmpty() || player.level().isClientSide()) {
            return;
        }

        if (!(pickedUp.getItem() instanceof IDemonWill will)) {
            return;
        }

        ItemStack remaining = PlayerDemonWillHandler.addDemonWill(player, pickedUp.copy());

        if (remaining.isEmpty()) {
            event.getItemEntity().discard();
        } else if (remaining.getItem() instanceof IDemonWill remainingWill &&
                   remainingWill.getWill(will.getType(remaining), remaining) < will.getWill(will.getType(pickedUp), pickedUp)) {
            event.getItemEntity().setItem(remaining);
        }
    }
}
