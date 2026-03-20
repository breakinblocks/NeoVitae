package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.alchemytable.AlchemyTableInput;
import com.breakinblocks.neovitae.common.recipe.alchemytable.AlchemyTableRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeInput;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.api.will.WillState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Rhythm of the Beating Anvil - Automated crafting ritual with demon will recipe modes.
 *
 * <p>Demon Will effects:
 * <ul>
 *   <li><b>Raw (Default)</b> - Standard vanilla crafting table recipes</li>
 *   <li><b>Steadfast</b> - Soul Forge recipe mode (tries soul forge recipes first, falls back to vanilla)</li>
 *   <li><b>Corrosive</b> - Alchemy Table recipe mode (tries alchemy table recipes first, falls back to vanilla)</li>
 * </ul>
 *
 * <p>This is a Dusk tier ritual.
 */
public class RitualCrafting extends Ritual {

    public static final String INPUT_RANGE = "inputRange";
    public static final String OUTPUT_RANGE = "outputRange";

    private static final double MIN_STEADFAST = 20.0;
    private static final double MIN_CORROSIVE = 20.0;

    private static final double WILL_PER_FORGE_CRAFT = 2.0;
    private static final double WILL_PER_ALCHEMY_CRAFT = 2.0;

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

        BlockPos masterPos = ctx.masterPos();

        List<BlockPos> inputPositions = RitualHelper.getRangePositions(ctx.master(), this, INPUT_RANGE, masterPos);
        if (inputPositions.isEmpty()) return;
        BlockPos inputPos = inputPositions.get(0);

        List<BlockPos> outputPositions = RitualHelper.getRangePositions(ctx.master(), this, OUTPUT_RANGE, masterPos);
        if (outputPositions.isEmpty()) return;
        BlockPos outputPos = outputPositions.get(0);

        IItemHandler inputHandler = ctx.level().getCapability(Capabilities.ItemHandler.BLOCK, inputPos, null);
        IItemHandler outputHandler = ctx.level().getCapability(Capabilities.ItemHandler.BLOCK, outputPos, null);

        if (inputHandler == null || outputHandler == null) return;

        WillState will = RitualHelper.queryWill(ctx.level(), masterPos, Math.min(MIN_STEADFAST, MIN_CORROSIVE));

        boolean trySoulForge = will.hasSteadfast();
        boolean tryAlchemy = will.hasCorrosive();

        List<ItemStack> inputItems = new ArrayList<>();
        for (int i = 0; i < Math.min(9, inputHandler.getSlots()); i++) {
            ItemStack stack = inputHandler.getStackInSlot(i);
            inputItems.add(stack.copy());
        }

        // --- STEADFAST: Try Soul Forge recipes first ---
        if (trySoulForge) {
            ItemStack result = trySoulForgeRecipe(ctx, inputHandler, inputItems);
            if (!result.isEmpty()) {
                // Check if output can accept the result
                ItemStack insertResult = ItemHandlerHelper.insertItemStacked(outputHandler, result.copy(), true);
                if (insertResult.isEmpty()) {
                    // Consume ingredients (up to 4 for soul forge)
                    int consumed = 0;
                    for (int i = 0; i < Math.min(4, inputHandler.getSlots()); i++) {
                        if (!inputItems.get(i).isEmpty()) {
                            inputHandler.extractItem(i, 1, false);
                            consumed++;
                        }
                    }
                    ItemHandlerHelper.insertItemStacked(outputHandler, result, false);
                    will.use(EnumWillType.STEADFAST, WILL_PER_FORGE_CRAFT);
                    will.drain(ctx.level(), masterPos);
                    ctx.syphon(getRefreshCost());
                    return;
                }
            }
            // Fall through to vanilla crafting
        }

        // --- CORROSIVE: Try Alchemy Table recipes first ---
        if (tryAlchemy) {
            ItemStack result = tryAlchemyTableRecipe(ctx, inputHandler, inputItems);
            if (!result.isEmpty()) {
                // Check if output can accept the result
                ItemStack insertResult = ItemHandlerHelper.insertItemStacked(outputHandler, result.copy(), true);
                if (insertResult.isEmpty()) {
                    // Consume ingredients (up to 6 for alchemy table)
                    for (int i = 0; i < Math.min(6, inputHandler.getSlots()); i++) {
                        if (!inputItems.get(i).isEmpty()) {
                            inputHandler.extractItem(i, 1, false);
                        }
                    }
                    ItemHandlerHelper.insertItemStacked(outputHandler, result, false);
                    will.use(EnumWillType.CORROSIVE, WILL_PER_ALCHEMY_CRAFT);
                    will.drain(ctx.level(), masterPos);
                    ctx.syphon(getRefreshCost());
                    return;
                }
            }
            // Fall through to vanilla crafting
        }

        // --- DEFAULT: Vanilla crafting ---
        while (inputItems.size() < 9) {
            inputItems.add(ItemStack.EMPTY);
        }

        CraftingInput craftingInput = CraftingInput.of(3, 3, inputItems);

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

        for (int i = 0; i < Math.min(9, inputHandler.getSlots()); i++) {
            if (!inputItems.get(i).isEmpty()) {
                inputHandler.extractItem(i, 1, false);
            }
        }

        ItemHandlerHelper.insertItemStacked(outputHandler, result, false);

        ctx.syphon(getRefreshCost());
    }

    /**
     * Tries to find and assemble a Soul Forge recipe from the input items.
     * Soul Forge recipes use up to 4 ingredients plus a gem.
     */
    private ItemStack trySoulForgeRecipe(RitualContext ctx, IItemHandler inputHandler, List<ItemStack> inputItems) {
        // Build input stacks (up to 4 items)
        List<ItemStack> forgeItems = new ArrayList<>();
        for (int i = 0; i < Math.min(4, inputItems.size()); i++) {
            if (!inputItems.get(i).isEmpty()) {
                forgeItems.add(inputItems.get(i).copy());
            }
        }

        if (forgeItems.isEmpty()) return ItemStack.EMPTY;

        // Use empty gem stack (ritual doesn't have a gem slot)
        ForgeInput forgeInput = new ForgeInput(forgeItems, ItemStack.EMPTY, -1);

        Optional<ForgeRecipe> recipeOpt = ctx.level().getRecipeManager()
                .getRecipeFor(NVRecipes.SOUL_FORGE_TYPE.get(), forgeInput, ctx.level())
                .map(holder -> holder.value());

        if (recipeOpt.isPresent()) {
            ForgeRecipe recipe = recipeOpt.get();
            return recipe.assemble(forgeInput, ctx.level().registryAccess());
        }

        return ItemStack.EMPTY;
    }

    /**
     * Tries to find and assemble an Alchemy Table recipe from the input items.
     * Alchemy Table recipes use up to 6 ingredients.
     */
    private ItemStack tryAlchemyTableRecipe(RitualContext ctx, IItemHandler inputHandler, List<ItemStack> inputItems) {
        List<ItemStack> alchemyItems = new ArrayList<>();
        for (int i = 0; i < Math.min(AlchemyTableRecipe.MAX_INPUTS, inputItems.size()); i++) {
            if (!inputItems.get(i).isEmpty()) {
                alchemyItems.add(inputItems.get(i).copy());
            }
        }

        if (alchemyItems.isEmpty()) return ItemStack.EMPTY;

        // Use orb tier 0 since the ritual has no orb
        AlchemyTableInput alchemyInput = new AlchemyTableInput(alchemyItems, 0);

        Optional<AlchemyTableRecipe> recipeOpt = ctx.level().getRecipeManager()
                .getRecipeFor(NVRecipes.ALCHEMY_TABLE_TYPE.get(), alchemyInput, ctx.level())
                .map(holder -> holder.value());

        if (recipeOpt.isPresent()) {
            AlchemyTableRecipe recipe = recipeOpt.get();
            return recipe.assemble(alchemyInput, ctx.level().registryAccess());
        }

        return ItemStack.EMPTY;
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
    public Component[] provideInformationOfRitualToPlayer(Player player) {
        return new Component[]{
                Component.translatable(getTranslationKey() + ".info"),
                Component.translatable(getTranslationKey() + ".will.steadfast"),
                Component.translatable(getTranslationKey() + ".will.corrosive")
        };
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
