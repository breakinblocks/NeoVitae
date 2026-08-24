package com.breakinblocks.neovitae.compat.emi.recipe;

import com.breakinblocks.neovitae.common.item.NVItems;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;

public final class NVEmiOrbs {

    private NVEmiOrbs() {
    }

    private static ItemLike[] orbs() {
        return new ItemLike[]{NVItems.ORB_WEAK.get(), NVItems.ORB_APPRENTICE.get(), NVItems.ORB_MAGICIAN.get(),
                NVItems.ORB_MASTER.get(), NVItems.ORB_ARCHMAGE.get(), NVItems.ORB_TRANSCENDENT.get()};
    }

    public static EmiIngredient atOrAbove(int tier, int firstTier) {
        ItemLike[] orbs = orbs();
        List<EmiIngredient> out = new ArrayList<>();
        for (int i = 0; i < orbs.length; i++) {
            if (tier <= i + firstTier) out.add(EmiStack.of(orbs[i]));
        }
        return out.isEmpty() ? EmiStack.EMPTY : EmiIngredient.of(out);
    }
}
