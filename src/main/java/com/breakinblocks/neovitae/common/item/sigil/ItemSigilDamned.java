package com.breakinblocks.neovitae.common.item.sigil;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.attribute.NVAttributes;
import com.breakinblocks.neovitae.spiritus.PlayerSpiritusHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ItemSigilDamned extends ItemSigilBase {

    private static final ResourceLocation SPIRITUS_BONUS_ID = NeoVitae.rl("sigil_damned_spiritus");
    private static final ResourceLocation SACRIFICE_BONUS_ID = NeoVitae.rl("sigil_damned_sacrifice");
    private static final ResourceLocation SIPHON_BONUS_ID = NeoVitae.rl("sigil_damned_siphon");

    public ItemSigilDamned() {
        super("damned", 0);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.neovitae.sigil.damned.spiritus")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.neovitae.sigil.damned.sacrifice")
                .withStyle(ChatFormatting.DARK_RED));
        tooltip.add(Component.translatable("tooltip.neovitae.sigil.damned.siphon")
                .withStyle(ChatFormatting.RED));
    }

    public static boolean isCarried(Player player) {
        for (ItemStack stack : PlayerSpiritusHandler.getAllInventories(player)) {
            if (isBoundDamned(stack)) {
                return true;
            }
            if (stack.getItem() instanceof ItemSigilHolding) {
                for (ItemStack inner : ItemSigilHolding.getInternalInventory(stack)) {
                    if (isBoundDamned(inner)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isBoundDamned(ItemStack stack) {
        return stack.getItem() instanceof ItemSigilDamned sigil && sigil.getBinding(stack) != null;
    }

    public static void updateBonuses(Player player) {
        boolean carried = isCarried(player);
        applyBonus(player, NVAttributes.BONUS_SPIRITUS, SPIRITUS_BONUS_ID, 50, carried);
        applyBonus(player, NVAttributes.BONUS_SACRIFICE, SACRIFICE_BONUS_ID, 25, carried);
        applyBonus(player, NVAttributes.BLOOD_SIPHON, SIPHON_BONUS_ID, 25, carried);
    }

    private static void applyBonus(Player player, Holder<Attribute> attribute, ResourceLocation id, double amount, boolean carried) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        if (!carried) {
            instance.removeModifier(id);
        } else if (!instance.hasModifier(id)) {
            instance.addTransientModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    public static void onKill(Player player, LivingEntity target) {
        if (isCarried(player)) {
            float healAmount = target.getMaxHealth() * 0.1f;
            player.heal(Math.min(healAmount, 4.0f));
        }
    }
}
