package com.breakinblocks.neovitae.common.item.sigil;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.attribute.NVAttributes;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class ItemSigilDamned extends ItemSigilBase {

    private static final Identifier SPIRITUS_BONUS_ID = NeoVitae.rl("sigil_damned_spiritus");
    private static final Identifier SACRIFICE_BONUS_ID = NeoVitae.rl("sigil_damned_sacrifice");
    private static final Identifier SIPHON_BONUS_ID = NeoVitae.rl("sigil_damned_siphon");

    public ItemSigilDamned(Item.Properties props) {
        super(props, "damned", 0);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        Binding binding = getBinding(stack);
        if (binding == null) {
            return ItemAttributeModifiers.EMPTY;
        }
        return ItemAttributeModifiers.builder()
                .add(NVAttributes.BONUS_SPIRITUS,
                        new AttributeModifier(SPIRITUS_BONUS_ID, 50, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HAND)
                .add(NVAttributes.BONUS_SACRIFICE,
                        new AttributeModifier(SACRIFICE_BONUS_ID, 25, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HAND)
                .add(NVAttributes.BLOOD_SIPHON,
                        new AttributeModifier(SIPHON_BONUS_ID, 10, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HAND)
                .build();
    }
    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {tooltip.accept(Component.translatable("tooltip.neovitae.sigil.damned.spiritus")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.accept(Component.translatable("tooltip.neovitae.sigil.damned.sacrifice")
                .withStyle(ChatFormatting.DARK_RED));
        tooltip.accept(Component.translatable("tooltip.neovitae.sigil.damned.siphon")
                .withStyle(ChatFormatting.RED));
    }

    public static void onKill(Player player, LivingEntity target) {
        for (ItemStack stack : List.<ItemStack>of(player.getMainHandItem(), player.getOffhandItem())) {
            if (stack.getItem() instanceof ItemSigilDamned sigil) {
                Binding binding = sigil.getBinding(stack);
                if (binding != null) {
                    float healAmount = target.getMaxHealth() * 0.1f;
                    player.heal(Math.min(healAmount, 4.0f));
                    break;
                }
            }
        }
    }
}
