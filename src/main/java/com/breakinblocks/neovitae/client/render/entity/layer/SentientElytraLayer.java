package com.breakinblocks.neovitae.client.render.entity.layer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.sentient.SentientEffectComponents;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;

public class SentientElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends ElytraLayer<T, M> {

    public static final ResourceLocation TEXTURE = NeoVitae.rl("textures/entity/sentient_elytra.png");
    public SentientElytraLayer(RenderLayerParent renderer, EntityModelSet modelSet) {
        super(renderer, modelSet);
    }

    @Override
    public boolean shouldRender(ItemStack stack, T entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        if (SentientHelper.isNeverValid(stack)) {
            return false;
        }

        return SentientHelper.hasFullSet(player) && SentientHelper.has(stack, SentientEffectComponents.ELYTRA.get());
    }

    @Override
    public ResourceLocation getElytraTexture(ItemStack stack, T entity) {
        return TEXTURE;
    }
}
