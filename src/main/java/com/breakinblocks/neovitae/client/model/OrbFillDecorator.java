package com.breakinblocks.neovitae.client.model;

import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.OrbFluidHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

public class OrbFillDecorator implements IItemDecorator {

    public static final OrbFillDecorator INSTANCE = new OrbFillDecorator();

    private static final int FILL_TINT = 0x80FF0000;

    @Override
    public boolean render(GuiGraphicsExtractor guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        int capacity = OrbFluidHandler.getOrbFluidCapacity(stack);
        if (capacity <= 0) return false;

        SimpleFluidContent content = stack.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
        int amount = content.isEmpty() ? 0 : content.getAmount();
        int level = fillLevel((float) amount / (float) capacity);
        if (level == 0) return false;

        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        Identifier texture = Identifier.fromNamespaceAndPath(itemId.getNamespace(),
                "textures/item/" + itemId.getPath() + "_fill_" + level + ".png");
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, xOffset, yOffset, 0.0F, 0.0F, 16, 16, 16, 16, FILL_TINT);
        return false;
    }

    private static int fillLevel(float ratio) {
        if (ratio < 0.01F) return 0;
        if (ratio < 0.2F) return 1;
        if (ratio < 0.4F) return 2;
        if (ratio < 0.6F) return 3;
        if (ratio < 0.8F) return 4;
        return 5;
    }
}
