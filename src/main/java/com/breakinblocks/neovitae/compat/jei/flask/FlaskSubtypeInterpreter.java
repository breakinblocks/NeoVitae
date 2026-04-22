package com.breakinblocks.neovitae.compat.jei.flask;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;

import java.util.List;
import java.util.stream.Collectors;

public class FlaskSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

    public static final FlaskSubtypeInterpreter INSTANCE = new FlaskSubtypeInterpreter();

    @Override
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        List<EffectHolder> effects = ItemAlchemyFlask.getEffectHolders(ingredient);
        if (effects.isEmpty()) {
            return "";
        }
        return effects.stream()
                .map(e -> e.effect().unwrapKey()
                        .map(k -> k.identifier().toString() + ":" + e.amplifier())
                        .orElse("unknown"))
                .sorted()
                .collect(Collectors.joining(","));
    }

    // @Override (removed: not an override in 26.1)
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        Object data = getSubtypeData(ingredient, context);
        return data.toString();
    }
}
