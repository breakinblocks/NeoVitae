package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.soul.SentientSpearItem;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;
import com.breakinblocks.neovitae.spiritus.PlayerSpiritusHandler;
import com.breakinblocks.neovitae.spiritus.SpiritusHelper;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameType;

public final class SentientSpearTests {
    private SentientSpearTests() {}

    private static final List<ResourceKey<Enchantment>> SPEAR_ENCHANTS = List.of(
            Enchantments.LUNGE,
            Enchantments.SHARPNESS,
            Enchantments.SMITE,
            Enchantments.BANE_OF_ARTHROPODS,
            Enchantments.LOOTING,
            Enchantments.FIRE_ASPECT,
            Enchantments.KNOCKBACK,
            Enchantments.UNBREAKING,
            Enchantments.MENDING);

    private static double mainHand(ItemStack stack, Holder<Attribute> attribute) {
        ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        return modifiers.modifiers().stream()
                .filter(e -> e.attribute().equals(attribute) && e.slot() == EquipmentSlotGroup.MAINHAND)
                .mapToDouble(e -> e.modifier().amount())
                .sum();
    }

    private static Player wielder(GameTestHelper helper, SpiritusType type, double spiritus) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(NVItems.SENTIENT_SPEAR.get()));
        if (spiritus > 0) {
            ItemStack gem = new ItemStack(NVItems.SPIRITUS_GEM_GRAND.get());
            SpiritusHelper.setSpiritus(gem, type, spiritus);
            player.getInventory().add(gem);
        }
        return player;
    }

    public static void register(NVTestRegistrar r) {
        r.add("spear/matches_netherite_spear_minus_half_damage", 20, helper -> {
            ItemStack spear = new ItemStack(NVItems.SENTIENT_SPEAR.get());
            ItemStack netherite = new ItemStack(Items.NETHERITE_SPEAR);
            helper.assertTrue(spear.is(ItemTags.SPEARS), "Sentient Spear should be in #minecraft:spears");
            helper.assertTrue(spear.has(DataComponents.PIERCING_WEAPON), "Sentient Spear should jab like a spear");
            helper.assertTrue(spear.get(DataComponents.KINETIC_WEAPON).equals(netherite.get(DataComponents.KINETIC_WEAPON)),
                    "Sentient Spear should charge exactly like a Netherite Spear");
            double damage = mainHand(spear, Attributes.ATTACK_DAMAGE);
            double netheriteDamage = mainHand(netherite, Attributes.ATTACK_DAMAGE);
            helper.assertTrue(Math.abs(damage - (netheriteDamage - 0.5)) < 1e-6,
                    "Sentient Spear should deal 0.5 less than a Netherite Spear, got " + damage + " vs " + netheriteDamage);
            double speed = mainHand(spear, Attributes.ATTACK_SPEED);
            double netheriteSpeed = mainHand(netherite, Attributes.ATTACK_SPEED);
            helper.assertTrue(Math.abs(speed - netheriteSpeed) < 1e-4,
                    "Sentient Spear should jab as fast as a Netherite Spear, got " + speed + " vs " + netheriteSpeed);
            helper.succeed();
        });

        r.add("spear/accepts_spear_enchantments", 20, helper -> {
            ItemStack spear = new ItemStack(NVItems.SENTIENT_SPEAR.get());
            var lookup = helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            for (ResourceKey<Enchantment> key : SPEAR_ENCHANTS) {
                helper.assertTrue(lookup.getOrThrow(key).value().isSupportedItem(spear),
                        "Sentient Spear should accept " + key.identifier());
            }
            helper.assertTrue(lookup.getOrThrow(Enchantments.LUNGE).value().isPrimaryItem(spear),
                    "Sentient Spear should roll Lunge at the enchanting table");
            helper.succeed();
        });

        r.add("spear/damage_scales_with_spiritus", 20, helper -> {
            Player player = wielder(helper, SpiritusType.RAW, 1000);
            ItemStack spear = player.getMainHandItem();
            ((SentientSpearItem) spear.getItem()).recalculatePowers(spear, helper.getLevel(), player);
            double damage = mainHand(spear, Attributes.ATTACK_DAMAGE);
            helper.assertTrue(Math.abs(damage - (SentientSpearItem.BASE_SPEAR_DAMAGE + 3.0)) < 1e-6,
                    "1000 Raw spiritus should add the sword's +3 damage, got " + damage);
            helper.assertTrue(spear.getOrDefault(NVDataComponents.SIGIL_ACTIVATED, false), "Spear should wake with spiritus");
            helper.succeed();
        });

        r.add("spear/hit_applies_ruina_and_drains_spiritus", 20, helper -> {
            Player player = wielder(helper, SpiritusType.RUINA, 1000);
            ItemStack spear = player.getMainHandItem();
            Zombie zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(2, 2, 2));
            double before = PlayerSpiritusHandler.getTotalSpiritus(SpiritusType.RUINA, player);
            spear.getItem().hurtEnemy(spear, zombie, player);
            helper.assertTrue(zombie.hasEffect(MobEffects.WITHER), "A Ruina-fed spear hit should inflict Wither");
            double after = PlayerSpiritusHandler.getTotalSpiritus(SpiritusType.RUINA, player);
            helper.assertTrue(after < before, "A spear hit should drain spiritus, " + before + " -> " + after);
            helper.succeed();
        });

        r.add("spear/charge_deals_half_less_than_netherite", 20, helper -> {
            Player player = wielder(helper, SpiritusType.RAW, 0);
            player.startUsingItem(InteractionHand.MAIN_HAND);
            helper.assertTrue(player.isUsingItem(), "Mock player should be charging the spear");
            Pig pig = helper.spawn(EntityType.PIG, new BlockPos(2, 2, 2));
            float startHealth = pig.getHealth();
            DamageSource charge = new DamageSource(helper.getLevel().registryAccess()
                    .lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(DamageTypes.SPEAR), player);
            pig.hurtServer(helper.getLevel(), charge, 5.0F);
            float dealt = startHealth - pig.getHealth();
            helper.assertTrue(Math.abs(dealt - 4.5F) < 1e-4, "An unfed charge should deal 0.5 less, dealt " + dealt);
            helper.succeed();
        });

        r.add("spear/kill_collects_spiritus", 20, helper -> {
            Player player = wielder(helper, SpiritusType.RAW, 1000);
            ItemStack spear = player.getMainHandItem();
            ((SentientSpearItem) spear.getItem()).recalculatePowers(spear, helper.getLevel(), player);
            Zombie zombie = helper.spawn(EntityType.ZOMBIE, new BlockPos(2, 2, 2));
            double before = PlayerSpiritusHandler.getTotalSpiritus(SpiritusType.RAW, player);
            zombie.hurtServer(helper.getLevel(), player.damageSources().playerAttack(player), 1000.0F);
            helper.assertTrue(zombie.isDeadOrDying(), "Zombie should die");
            double after = PlayerSpiritusHandler.getTotalSpiritus(SpiritusType.RAW, player);
            helper.assertTrue(after > before, "Killing with the spear should collect spiritus, " + before + " -> " + after);
            helper.succeed();
        });
    }
}
