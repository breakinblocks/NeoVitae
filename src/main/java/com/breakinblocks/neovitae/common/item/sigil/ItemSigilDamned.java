package com.breakinblocks.neovitae.common.item.sigil;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.attribute.NVAttributes;
import com.breakinblocks.neovitae.spiritus.PlayerSpiritusHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class ItemSigilDamned extends ItemSigilBase {

    private static final Identifier SPIRITUS_BONUS_ID = NeoVitae.rl("sigil_damned_spiritus");
    private static final Identifier SACRIFICE_BONUS_ID = NeoVitae.rl("sigil_damned_sacrifice");
    private static final Identifier SIPHON_BONUS_ID = NeoVitae.rl("sigil_damned_siphon");

    public ItemSigilDamned(Item.Properties props) {
        super(props, "damned", 0);
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

    private static void applyBonus(Player player, Holder<Attribute> attribute, Identifier id, double amount, boolean carried) {
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
