package com.breakinblocks.neovitae.compat.jei;

import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import com.breakinblocks.neovitae.client.screen.RoutingNodeScreen;
import com.breakinblocks.neovitae.common.menu.RoutingNodeMenu;

import java.util.ArrayList;
import java.util.List;

public class RoutingNodeGhostHandler implements IGhostIngredientHandler<RoutingNodeScreen> {

    @Override
    public <I> List<Target<I>> getTargetsTyped(RoutingNodeScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
        List<Target<I>> targets = new ArrayList<>();
        Object dropped = ingredient.getIngredient();

        boolean items = dropped instanceof ItemStack && screen.isItemsTab();
        boolean fluids = dropped instanceof FluidStack && screen.isFluidsTab();
        if (!items && !fluids) return targets;

        RoutingNodeMenu menu = screen.getMenu();
        for (int i = 0; i < RoutingNodeMenu.GHOST_SLOT_COUNT; i++) {
            final int ghostSlot = i;
            Slot slot = menu.slots.get(i);
            Rect2i area = new Rect2i(screen.getGuiLeft() + slot.x, screen.getGuiTop() + slot.y, 16, 16);
            targets.add(new Target<>() {
                @Override
                public Rect2i getArea() {
                    return area;
                }

                @Override
                public void accept(I value) {
                    if (value instanceof ItemStack stack) {
                        screen.setItemGhostFromJei(ghostSlot, stack);
                    } else if (value instanceof FluidStack fluid) {
                        screen.setFluidGhostFromJei(ghostSlot, fluid);
                    }
                }
            });
        }
        return targets;
    }

    @Override
    public void onComplete() {
    }
}
