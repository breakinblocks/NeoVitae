package com.breakinblocks.neovitae.spiritus;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
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
import com.breakinblocks.neovitae.common.item.soul.ISentientTool;
import com.breakinblocks.neovitae.common.item.soul.SpiritusEssenceItem;
import com.breakinblocks.neovitae.common.item.soul.SentientToolHelper;
import com.breakinblocks.neovitae.common.entity.mob.IDaemonium;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        if (weapon.getItem() instanceof ISentientTool sentient) {
            soulDrops = sentient.getRandomSpiritusDrop(killed, player, weapon, looting);
        }

        if (soulDrops != null && !soulDrops.isEmpty()) {
            SpiritusType weaponType = SentientToolHelper.getCurrentType(weapon);
            List<ItemStack> overflow = new ArrayList<>();
            for (ItemStack soulStack : soulDrops) {
                if (soulStack.isEmpty()) continue;
                if (soulStack.getItem() instanceof ISpiritus spirit) {
                    double amount = spirit.getSpiritus(weaponType, soulStack);
                    double added = PlayerSpiritusHandler.addSpiritus(weaponType, player, amount);
                    if (added < amount) {
                        double leftover = amount - added;
                        overflow.add(spirit.createSpiritus(leftover));
                    }
                } else {
                    overflow.add(soulStack);
                }
            }
            dropSouls(killed, overflow);
        }
    }

    @SubscribeEvent
    public static void onDaemoniumDeath(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();
        if (killed.level().isClientSide() || !(killed instanceof IDaemonium)) {
            return;
        }
        if (!(event.getSource().getEntity() instanceof Player killer)) {
            return;
        }
        Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(killed.getType());
        DaemoniumDrop drop = DAEMONIUM_DROPS.get(id.getPath());
        if (drop == null) {
            return;
        }
        RandomSource rng = killed.level().getRandom();
        if (drop.chance() < 1.0f && rng.nextFloat() >= drop.chance()) {
            return;
        }
        SpiritusType type = pickWeighted(drop.entries(), rng);
        double soulAmount = daemoniumSoulAmount(killed, killer, rng);
        dropSouls(killed, List.of(soulItem(type).createSpiritus(soulAmount)));
    }

    private static double daemoniumSoulAmount(LivingEntity killed, Player killer, RandomSource rng) {
        double amount = (SNARE_BASE_DROP + rng.nextDouble() * SNARE_RANDOM_DROP) * killed.getMaxHealth() / 20d;
        double bonusSpiritus = killer.getAttributeValue(NVAttributes.BONUS_SPIRITUS);
        if (bonusSpiritus > 0) {
            amount *= (1 + bonusSpiritus / 100);
        }
        return amount;
    }

    private static SpiritusType pickWeighted(List<SpiritusEntry> entries, RandomSource rng) {
        int total = 0;
        for (SpiritusEntry entry : entries) total += entry.weight();
        int r = rng.nextInt(total);
        for (SpiritusEntry entry : entries) {
            r -= entry.weight();
            if (r < 0) return entry.type();
        }
        return entries.get(0).type();
    }

    private static SpiritusEssenceItem soulItem(SpiritusType type) {
        return switch (type) {
            case RUINA -> NVItems.MONSTER_SOUL_RUINA.get();
            case NIHILUM -> NVItems.MONSTER_SOUL_NIHILUM.get();
            case VINDICTA -> NVItems.MONSTER_SOUL_VINDICTA.get();
            case INVICTUS -> NVItems.MONSTER_SOUL_INVICTUS.get();
            default -> NVItems.MONSTER_SOUL_RAW.get();
        };
    }

    private record SpiritusEntry(SpiritusType type, int weight) {}

    private record DaemoniumDrop(float chance, List<SpiritusEntry> entries) {}

    private static SpiritusEntry e(SpiritusType type, int weight) {
        return new SpiritusEntry(type, weight);
    }

    private static final Map<String, DaemoniumDrop> DAEMONIUM_DROPS = Map.ofEntries(
            Map.entry("daemonium_animaris", new DaemoniumDrop(0.4f, List.of(e(SpiritusType.INVICTUS, 70), e(SpiritusType.VINDICTA, 30)))),
            Map.entry("daemonium_corrodis", new DaemoniumDrop(0.6f, List.of(e(SpiritusType.RUINA, 80), e(SpiritusType.VINDICTA, 20)))),
            Map.entry("daemonium_cruoris", new DaemoniumDrop(0.4f, List.of(e(SpiritusType.RAW, 70), e(SpiritusType.NIHILUM, 30)))),
            Map.entry("daemonium_doloris", new DaemoniumDrop(0.6f, List.of(e(SpiritusType.VINDICTA, 70), e(SpiritusType.NIHILUM, 30)))),
            Map.entry("daemonium_fervidis", new DaemoniumDrop(0.6f, List.of(e(SpiritusType.NIHILUM, 80), e(SpiritusType.INVICTUS, 20)))),
            Map.entry("daemonium_glaciaris", new DaemoniumDrop(1.0f, List.of(e(SpiritusType.INVICTUS, 40), e(SpiritusType.RAW, 15), e(SpiritusType.RUINA, 15), e(SpiritusType.NIHILUM, 15), e(SpiritusType.VINDICTA, 15)))),
            Map.entry("daemonium_ignis", new DaemoniumDrop(0.6f, List.of(e(SpiritusType.NIHILUM, 50), e(SpiritusType.RUINA, 50)))),
            Map.entry("daemonium_pestis", new DaemoniumDrop(0.4f, List.of(e(SpiritusType.RUINA, 60), e(SpiritusType.RAW, 40)))),
            Map.entry("daemonium_rancoris", new DaemoniumDrop(0.4f, List.of(e(SpiritusType.VINDICTA, 60), e(SpiritusType.INVICTUS, 40)))),
            Map.entry("daemonium_voraxis", new DaemoniumDrop(0.5f, List.of(e(SpiritusType.NIHILUM, 70), e(SpiritusType.RUINA, 30))))
    );

    private static void handleSnareDrop(LivingEntity killed, @Nullable Player killer) {
        if (killed.level().getDifficulty() == Difficulty.PEACEFUL || !(killed instanceof Enemy)) {
            return;
        }

        double spiritusModifier = killed instanceof Slime ? 0.67 : 1;
        double soulAmount = spiritusModifier * (SNARE_BASE_DROP + killed.level().getRandom().nextDouble() * SNARE_RANDOM_DROP)
                * killed.getMaxHealth() / 20d;

        if (killer != null) {
            double bonusSpiritus = killer.getAttributeValue(NVAttributes.BONUS_SPIRITUS);
            if (bonusSpiritus > 0) {
                soulAmount *= (1 + bonusSpiritus / 100);
            }
        }

        SpiritusEssenceItem soulItem = NVItems.MONSTER_SOUL_RAW.get();
        ItemStack soulStack = soulItem.createSpiritus(soulAmount);

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

        if (!(pickedUp.getItem() instanceof ISpiritus spiritus)) {
            return;
        }

        ItemStack remaining = PlayerSpiritusHandler.addSpiritus(player, pickedUp.copy());

        if (remaining.isEmpty()) {
            event.getItemEntity().discard();
        } else {
            double originalSpiritus = spiritus.getSpiritus(spiritus.getType(pickedUp), pickedUp);
            double remainingSpiritusAmount = remaining.getItem() instanceof ISpiritus remainingSpiritus
                    ? remainingSpiritus.getSpiritus(spiritus.getType(remaining), remaining) : originalSpiritus;
            if (remainingSpiritusAmount < originalSpiritus) {
                event.getItemEntity().setItem(remaining);
            }
        }
    }
}
