package com.breakinblocks.neovitae.compat.kubejs;

import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.TypeWrapperRegistry;
import com.breakinblocks.neovitae.common.crafting.OrbTierIngredient;

public class NVKubeJSPlugin implements KubeJSPlugin {

    @Override
    public void registerTypeWrappers(TypeWrapperRegistry registry) {
        registry.registerMapCodec(OrbTierIngredient.class, OrbTierIngredient.CODEC);
    }
}
