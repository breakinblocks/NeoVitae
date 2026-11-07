package com.breakinblocks.neovitae.common.event;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.ServerConfig;
import com.breakinblocks.neovitae.common.item.soul.HellforgedHelper;
import com.breakinblocks.neovitae.common.item.soul.ISentientTool;
import com.breakinblocks.neovitae.common.item.soul.LexVitaeItem;
import com.breakinblocks.neovitae.common.tag.NVTags;

@EventBusSubscriber(modid = NeoVitae.MODID)
public final class HellforgedEventHandler {

    private HellforgedEventHandler() {}

    @SubscribeEvent
    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (!HellforgedHelper.isHellforged(stack)) return;
        ServerConfig config = NeoVitae.SERVER_CONFIG;

        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (stack.is(NVTags.Items.SENTIENT_SET) && equippable != null) {
            EquipmentSlot equipmentSlot = equippable.slot();
            EquipmentSlotGroup group = EquipmentSlotGroup.bySlot(equipmentSlot);
            String slot = equipmentSlot.getName();
            double bonus = switch (equipmentSlot) {
                case HEAD -> config.HELLFORGED_ARMOR_HELMET.get();
                case CHEST -> config.HELLFORGED_ARMOR_CHESTPLATE.get();
                case LEGS -> config.HELLFORGED_ARMOR_LEGGINGS.get();
                default -> config.HELLFORGED_ARMOR_BOOTS.get();
            };
            add(event, Attributes.ARMOR, "hellforged_armor_" + slot, bonus, AttributeModifier.Operation.ADD_VALUE, group);
            add(event, Attributes.ARMOR_TOUGHNESS, "hellforged_toughness_" + slot,
                    config.HELLFORGED_ARMOR_TOUGHNESS.get(), AttributeModifier.Operation.ADD_VALUE, group);
            add(event, Attributes.KNOCKBACK_RESISTANCE, "hellforged_knockback_" + slot,
                    config.HELLFORGED_KNOCKBACK_RESISTANCE.get(), AttributeModifier.Operation.ADD_VALUE, group);
            return;
        }

        if (stack.getItem() instanceof ISentientTool) {
            if (stack.getItem() instanceof LexVitaeItem && !LexVitaeItem.isActive(stack)) return;
            if (HellforgedHelper.hasNetheriteFloor(stack)) {
                add(event, Attributes.ATTACK_DAMAGE, "hellforged_floor_damage", config.HELLFORGED_FLOOR_DAMAGE.get(),
                        AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND);
            }
            add(event, Attributes.ATTACK_DAMAGE, "hellforged_damage", config.HELLFORGED_DAMAGE_MULTIPLIER.get() - 1.0,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EquipmentSlotGroup.MAINHAND);
        }
    }

    private static void add(ItemAttributeModifierEvent event, Holder<Attribute> attribute, String id, double amount,
                            AttributeModifier.Operation operation, EquipmentSlotGroup group) {
        if (amount == 0) return;
        event.addModifier(attribute, new AttributeModifier(NeoVitae.rl(id), amount, operation), group);
    }
}
