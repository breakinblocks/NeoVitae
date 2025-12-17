package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Rhythm of the Beating Anvil - Automated crafting ritual.
 * Uses items from adjacent inventories to craft recipes.
 * Filter the recipe by placing an item filter in a chest above.
 * This is a Dusk tier ritual.
 */
public class RitualCrafting extends Ritual {

    public static final String INPUT_RANGE = "inputRange";
    public static final String OUTPUT_RANGE = "outputRange";

    public RitualCrafting() {
        super("crafting", 1, 25000, "ritual." + NeoVitae.MODID + ".crafting");
        addBlockRange(INPUT_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        addBlockRange(OUTPUT_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, -1, 0), 1, 1, 1));
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) {
            masterRitualStone.stopRitual(BreakType.DEACTIVATE);
            return;
        }

        // Get input and output inventories
        List<BlockPos> inputPositions = RitualHelper.getRangePositions(ctx.master(), this, INPUT_RANGE, ctx.masterPos());
        if (inputPositions.isEmpty()) return;
        BlockPos inputPos = inputPositions.get(0);

        List<BlockPos> outputPositions = RitualHelper.getRangePositions(ctx.master(), this, OUTPUT_RANGE, ctx.masterPos());
        if (outputPositions.isEmpty()) return;
        BlockPos outputPos = outputPositions.get(0);

        IItemHandler inputHandler = ctx.level().getCapability(Capabilities.ItemHandler.BLOCK, inputPos, null);
        IItemHandler outputHandler = ctx.level().getCapability(Capabilities.ItemHandler.BLOCK, outputPos, null);

        if (inputHandler == null || outputHandler == null) return;

        // Try to find and execute a crafting recipe
        // Collect up to 9 items from input for a 3x3 crafting grid
        List<ItemStack> inputItems = new ArrayList<>();
        for (int i = 0; i < Math.min(9, inputHandler.getSlots()); i++) {
            ItemStack stack = inputHandler.getStackInSlot(i);
            inputItems.add(stack.copy());
        }

        // Pad to 9 slots
        while (inputItems.size() < 9) {
            inputItems.add(ItemStack.EMPTY);
        }

        // Create crafting input
        CraftingInput craftingInput = CraftingInput.of(3, 3, inputItems);

        // Find matching recipe
        Optional<CraftingRecipe> recipeOpt = ctx.level().getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, craftingInput, ctx.level())
            .map(holder -> holder.value());

        if (recipeOpt.isEmpty()) return;

        CraftingRecipe recipe = recipeOpt.get();
        ItemStack result = recipe.assemble(craftingInput, ctx.level().registryAccess());

        if (result.isEmpty()) return;

        // Check if output can accept the result
        ItemStack insertResult = ItemHandlerHelper.insertItemStacked(outputHandler, result.copy(), true);
        if (!insertResult.isEmpty()) return; // Output full

        // Consume ingredients
        for (int i = 0; i < Math.min(9, inputHandler.getSlots()); i++) {
            if (!inputItems.get(i).isEmpty()) {
                inputHandler.extractItem(i, 1, false);
            }
        }

        // Insert result
        ItemHandlerHelper.insertItemStacked(outputHandler, result, false);

        // Consume LP
        ctx.syphon(getRefreshCost());
    }

    @Override
    public int getRefreshTime() {
        return 40; // Every 2 seconds
    }

    @Override
    public int getRefreshCost() {
        return 100;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.DUSK);
        addParallelRunes(components, 2, 0, EnumRuneType.FIRE);
        addCornerRunes(components, 2, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 3, 0, EnumRuneType.DUSK);
        addCornerRunes(components, 3, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 4, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 4, 0, EnumRuneType.DUSK);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualCrafting();
    }
}
