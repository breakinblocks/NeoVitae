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
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.soul.SpiritusEssenceItem;
import com.breakinblocks.neovitae.common.item.soul.SentientToolHelper;
import com.breakinblocks.neovitae.common.item.soul.SentientAxeItem;
import com.breakinblocks.neovitae.common.item.soul.SentientPickaxeItem;
import com.breakinblocks.neovitae.common.item.soul.SentientScytheItem;
import com.breakinblocks.neovitae.common.item.soul.SentientShovelItem;
import com.breakinblocks.neovitae.common.item.soul.SentientSwordItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class SpiritusEventHandler {

    private static final double SNARE_BASE_DROP = 1.0;
    private static final double SNARE_RANDOM_DROP = 4.0;

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();

        if (killed.level().isClientSide()) {
            return;
        }

        if (killed.hasEffect(NVMobEffects.SPIRITUS_SNARE)) {
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
            soulDrops = sword.getRandomSpiritusDrop(killed, player, weapon, looting);
        } else if (weapon.getItem() instanceof SentientAxeItem axe) {
            soulDrops = axe.getRandomSpiritusDrop(killed, player, weapon, looting);
        } else if (weapon.getItem() instanceof SentientPickaxeItem pickaxe) {
            soulDrops = pickaxe.getRandomSpiritusDrop(killed, player, weapon, looting);
        } else if (weapon.getItem() instanceof SentientShovelItem shovel) {
            soulDrops = shovel.getRandomSpiritusDrop(killed, player, weapon, looting);
        } else if (weapon.getItem() instanceof SentientScytheItem scythe) {
            soulDrops = scythe.getRandomSpiritusDrop(killed, player, weapon, looting);
        }

        if (soulDrops != null && !soulDrops.isEmpty()) {
            SpiritusType weaponType = SentientToolHelper.getCurrentType(weapon);
            List<ItemStack> overflow = new ArrayList<>();
            for (ItemStack soulStack : soulDrops) {
                if (soulStack.isEmpty()) continue;
                if (soulStack.getItem() instanceof ISpiritus spirit) {
                    double amount = spirit.getWill(weaponType, soulStack);
                    double added = PlayerSpiritusHandler.addSpiritus(weaponType, player, amount);
                    if (added < amount) {
                        double leftover = amount - added;
                        overflow.add(spirit.createWill(leftover));
                    }
                } else {
                    overflow.add(soulStack);
                }
            }
            dropSouls(killed, overflow);
        }
    }

    private static void handleSnareDrop(LivingEntity killed, @Nullable Player killer) {
        if (killed.level().getDifficulty() == Difficulty.PEACEFUL || !(killed instanceof Enemy)) {
            return;
        }

        double willModifier = killed instanceof Slime ? 0.67 : 1;
        double soulAmount = willModifier * (SNARE_BASE_DROP + killed.level().random.nextDouble() * SNARE_RANDOM_DROP)
                * killed.getMaxHealth() / 20d;

        if (killer != null) {
            double bonusSpiritus = killer.getAttributeValue(NVAttributes.BONUS_SPIRITUS);
            if (bonusSpiritus > 0) {
                soulAmount *= (1 + bonusSpiritus / 100);
            }
        }

        SpiritusEssenceItem soulItem = NVItems.MONSTER_SOUL_RAW.get();
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

        if (!(pickedUp.getItem() instanceof ISpiritus will)) {
            return;
        }

        ItemStack remaining = PlayerSpiritusHandler.addSpiritus(player, pickedUp.copy());

        if (remaining.isEmpty()) {
            event.getItemEntity().discard();
        } else {
            double originalWill = will.getWill(will.getType(pickedUp), pickedUp);
            double remainingWillAmount = remaining.getItem() instanceof ISpiritus remainingSpiritus
                    ? remainingSpiritus.getWill(will.getType(remaining), remaining) : originalWill;
            if (remainingWillAmount < originalWill) {
                event.getItemEntity().setItem(remaining);
            }
        }
    }
}
