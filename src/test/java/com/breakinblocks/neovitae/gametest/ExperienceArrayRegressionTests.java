package com.breakinblocks.neovitae.gametest;

import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectLiquifiedExperience;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.ExperienceTomeItem;
import com.breakinblocks.neovitae.common.item.NVItems;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.lang.reflect.Method;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class ExperienceArrayRegressionTests {
    private static void convert(boolean reverse, IItemHandler items, IFluidHandler fluids) {
        try {
            Method method = AlchemyArrayEffectLiquifiedExperience.class.getDeclaredMethod(
                    reverse ? "fillTomes" : "drainTomes", IItemHandler.class, IFluidHandler.class, Fluid.class, int.class);
            method.setAccessible(true);
            method.invoke(null, items, fluids, NVFluids.LIQUIFIED_EXPERIENCE_SOURCE.get(), 20);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static ItemStack tome(int xp) {
        ItemStack stack = new ItemStack(NVItems.EXPERIENCE_TOME.get());
        ExperienceTomeItem.addXpToTome(stack, xp);
        return stack;
    }

    private static class CopyingInventory extends ItemStackHandler {
        boolean changed;
        CopyingInventory() { super(1); }
        @Override public ItemStack getStackInSlot(int slot) { return super.getStackInSlot(slot).copy(); }
        @Override protected void onContentsChanged(int slot) { changed = true; }
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void copiedStacksPersistBothWays(GameTestHelper h) {
        CopyingInventory items = new CopyingInventory();
        items.setStackInSlot(0, tome(10));
        items.changed = false;
        FluidTank tank = new FluidTank(200);
        convert(false, items, tank);
        h.assertTrue(ExperienceTomeItem.getStoredXp(items.getStackInSlot(0)) == 0
                && tank.getFluidAmount() == 200 && items.changed, "Draining must save the tome and mark the inventory dirty");
        items.changed = false;
        convert(true, items, tank);
        h.assertTrue(ExperienceTomeItem.getStoredXp(items.getStackInSlot(0)) == 10
                && tank.isEmpty() && items.changed, "Filling must save the tome and mark the inventory dirty");
        h.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void rejectedReplacementPreservesBothSides(GameTestHelper h) {
        ItemStackHandler items = new ItemStackHandler(1) {
            @Override public boolean isItemValid(int slot, ItemStack stack) { return false; }
        };
        items.setStackInSlot(0, tome(10));
        FluidTank tank = new FluidTank(400);
        tank.setFluid(new FluidStack(NVFluids.LIQUIFIED_EXPERIENCE_SOURCE.get(), 200));
        convert(false, items, tank);
        convert(true, items, tank);
        h.assertTrue(ExperienceTomeItem.getStoredXp(items.getStackInSlot(0)) == 10
                && tank.getFluidAmount() == 200, "A rejected replacement must not consume or create XP");
        h.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void fractionalFluidIsPreserved(GameTestHelper h) {
        CopyingInventory items = new CopyingInventory();
        items.setStackInSlot(0, tome(0));
        FluidTank tank = new FluidTank(39);
        tank.setFluid(new FluidStack(NVFluids.LIQUIFIED_EXPERIENCE_SOURCE.get(), 39));
        convert(true, items, tank);
        h.assertTrue(ExperienceTomeItem.getStoredXp(items.getStackInSlot(0)) == 1 && tank.getFluidAmount() == 19,
                "39 mB must yield one XP and leave 19 mB");
        convert(false, items, tank);
        h.assertTrue(ExperienceTomeItem.getStoredXp(items.getStackInSlot(0)) == 0 && tank.getFluidAmount() == 39,
                "Round trip must conserve XP");
        h.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void fillingStopsAtTomeCapacity(GameTestHelper h) {
        CopyingInventory items = new CopyingInventory();
        items.setStackInSlot(0, tome(Integer.MAX_VALUE - 1));
        FluidTank tank = new FluidTank(200);
        tank.setFluid(new FluidStack(NVFluids.LIQUIFIED_EXPERIENCE_SOURCE.get(), 200));
        convert(true, items, tank);
        convert(true, items, tank);
        h.assertTrue(ExperienceTomeItem.getStoredXp(items.getStackInSlot(0)) == Integer.MAX_VALUE
                && tank.getFluidAmount() == 180, "A full tome must leave excess fluid in the tank");
        h.succeed();
    }
}
