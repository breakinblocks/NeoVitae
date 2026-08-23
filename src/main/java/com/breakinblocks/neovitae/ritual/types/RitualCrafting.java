// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.recipe.NVRecipes;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeInput;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeRecipe;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeInput;
import com.breakinblocks.neovitae.common.recipe.forge.ForgeRecipe;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.api.spiritus.SpiritusState;
import com.breakinblocks.neovitae.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Rhythm of the Beating Anvil - Automated crafting ritual with spiritus recipe modes.
 *
 * <p>Spiritus effects:
 * <ul>
 *   <li><b>Raw (Default)</b> - Standard vanilla crafting table recipes</li>
 *   <li><b>Steadfast</b> - Soul Forge recipe mode (tries soul forge recipes first, falls back to vanilla)</li>
 *   <li><b>Corrosive</b> - Tabula Vitae recipe mode (tries alchemy table recipes first, falls back to vanilla)</li>
 * </ul>
 *
 * <p>This is a Tenebrae tier ritual.
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

        SpiritusState will = RitualHelper.querySpiritus(ctx.level(), masterPos, Math.min(MIN_STEADFAST, MIN_CORROSIVE));

        boolean tryHellfireForge = will.hasInvictus();
        boolean tryAlchemy = will.hasRuina();

        List<ItemStack> inputItems = new ArrayList<>();
        for (int i = 0; i < Math.min(9, inputHandler.getSlots()); i++) {
            ItemStack stack = inputHandler.getStackInSlot(i);
            inputItems.add(stack.copy());
        }

        // --- STEADFAST: Try Soul Forge recipes first ---
        if (tryHellfireForge) {
            ItemStack result = tryHellfireForgeRecipe(ctx, inputHandler, inputItems);
            if (!result.isEmpty()) {
                // Check if output can accept the result
                ItemStack insertResult = ItemHandlerHelper.insertItemStacked(outputHandler, result.copy(), true);
                if (insertResult.isEmpty()) {
                    consumeInputs(ctx, inputHandler, outputHandler, outputPos, inputItems,
                            itemRemainders(inputItems, 4), 4);
                    ItemHandlerHelper.insertItemStacked(outputHandler, result, false);
                    will.use(SpiritusType.INVICTUS, WILL_PER_FORGE_CRAFT);
                    will.drain(ctx.level(), masterPos);
                    ctx.syphon(getRefreshCost());
                    RitualHelper.chanceStream(ctx.level(), 8, () ->
                            StreamPresets.arcaneBolt(inputPos, outputPos).build()
                                    .sendToNearby(ctx.serverLevel(), masterPos, 128));
                    return;
                }
            }
            // Fall through to vanilla crafting
        }

        // --- CORROSIVE: Try Tabula Vitae recipes first ---
        if (tryAlchemy) {
            ItemStack result = tryTabulaVitaeRecipe(ctx, inputHandler, inputItems);
            if (!result.isEmpty()) {
                // Check if output can accept the result
                ItemStack insertResult = ItemHandlerHelper.insertItemStacked(outputHandler, result.copy(), true);
                if (insertResult.isEmpty()) {
                    consumeInputs(ctx, inputHandler, outputHandler, outputPos, inputItems,
                            itemRemainders(inputItems, TabulaVitaeRecipe.MAX_INPUTS), TabulaVitaeRecipe.MAX_INPUTS);
                    ItemHandlerHelper.insertItemStacked(outputHandler, result, false);
                    will.use(SpiritusType.RUINA, WILL_PER_ALCHEMY_CRAFT);
                    will.drain(ctx.level(), masterPos);
                    ctx.syphon(getRefreshCost());
                    RitualHelper.chanceStream(ctx.level(), 8, () ->
                            StreamPresets.arcaneBolt(inputPos, outputPos).build()
                                    .sendToNearby(ctx.serverLevel(), masterPos, 128));
                    return;
                }
            }
            // Fall through to vanilla crafting
        }

        // --- DEFAULT: Vanilla crafting ---
        while (inputItems.size() < 9) {
            inputItems.add(ItemStack.EMPTY);
        }

        CraftingInput.Positioned positioned = CraftingInput.ofPositioned(3, 3, inputItems);
        CraftingInput craftingInput = positioned.input();

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

        consumeInputs(ctx, inputHandler, outputHandler, outputPos, inputItems,
                gridRemainders(recipe.getRemainingItems(craftingInput), positioned), 9);

        ItemHandlerHelper.insertItemStacked(outputHandler, result, false);

        ctx.syphon(getRefreshCost());
        RitualHelper.chanceStream(ctx.level(), 8, () ->
                StreamPresets.arcaneBolt(inputPos, outputPos).build()
                        .sendToNearby(ctx.serverLevel(), masterPos, 128));
    }

    private static List<ItemStack> itemRemainders(List<ItemStack> inputItems, int limit) {
        List<ItemStack> remainders = new ArrayList<>(Collections.nCopies(inputItems.size(), ItemStack.EMPTY));
        for (int i = 0; i < Math.min(limit, inputItems.size()); i++) {
            ItemStack stack = inputItems.get(i);
            if (!stack.isEmpty() && stack.hasCraftingRemainingItem()) {
                remainders.set(i, stack.getCraftingRemainingItem());
            }
        }
        return remainders;
    }

    private static List<ItemStack> gridRemainders(List<ItemStack> trimmed, CraftingInput.Positioned positioned) {
        List<ItemStack> remainders = new ArrayList<>(Collections.nCopies(9, ItemStack.EMPTY));
        CraftingInput input = positioned.input();
        for (int row = 0; row < input.height(); row++) {
            for (int col = 0; col < input.width(); col++) {
                int trimmedIndex = row * input.width() + col;
                if (trimmedIndex >= trimmed.size()) continue;
                remainders.set((row + positioned.top()) * 3 + col + positioned.left(), trimmed.get(trimmedIndex));
            }
        }
        return remainders;
    }

    private void consumeInputs(RitualContext ctx, IItemHandler inputHandler, IItemHandler outputHandler,
                               BlockPos outputPos, List<ItemStack> inputItems, List<ItemStack> remainders, int limit) {
        for (int i = 0; i < Math.min(limit, inputHandler.getSlots()); i++) {
            if (inputItems.get(i).isEmpty()) continue;
            inputHandler.extractItem(i, 1, false);

            ItemStack remainder = i < remainders.size() ? remainders.get(i) : ItemStack.EMPTY;
            if (remainder.isEmpty()) continue;

            ItemStack leftover = inputHandler.insertItem(i, remainder.copy(), false);
            if (!leftover.isEmpty()) leftover = Utils.insertStackIntoTile(leftover, outputHandler);
            if (!leftover.isEmpty()) Utils.spawnStackAtBlock(ctx.level(), outputPos, Direction.UP, leftover);
        }
    }

    /**
     * Tries to find and assemble a Soul Forge recipe from the input items.
     * Soul Forge recipes use up to 4 ingredients plus a gem.
     */
    private ItemStack tryHellfireForgeRecipe(RitualContext ctx, IItemHandler inputHandler, List<ItemStack> inputItems) {
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
                .getRecipeFor(NVRecipes.HELLFIRE_FORGE_TYPE.get(), forgeInput, ctx.level())
                .map(holder -> holder.value());

        if (recipeOpt.isPresent()) {
            ForgeRecipe recipe = recipeOpt.get();
            return recipe.assemble(forgeInput, ctx.level().registryAccess());
        }

        return ItemStack.EMPTY;
    }

    /**
     * Tries to find and assemble an Tabula Vitae recipe from the input items.
     * Tabula Vitae recipes use up to 6 ingredients.
     */
    private ItemStack tryTabulaVitaeRecipe(RitualContext ctx, IItemHandler inputHandler, List<ItemStack> inputItems) {
        List<ItemStack> alchemyItems = new ArrayList<>();
        for (int i = 0; i < Math.min(TabulaVitaeRecipe.MAX_INPUTS, inputItems.size()); i++) {
            if (!inputItems.get(i).isEmpty()) {
                alchemyItems.add(inputItems.get(i).copy());
            }
        }

        if (alchemyItems.isEmpty()) return ItemStack.EMPTY;

        // Use orb tier 0 since the ritual has no orb
        TabulaVitaeInput alchemyInput = new TabulaVitaeInput(alchemyItems, 0);

        Optional<TabulaVitaeRecipe> recipeOpt = ctx.level().getRecipeManager()
                .getRecipeFor(NVRecipes.TABULA_VITAE_TYPE.get(), alchemyInput, ctx.level())
                .map(holder -> holder.value());

        if (recipeOpt.isPresent()) {
            TabulaVitaeRecipe recipe = recipeOpt.get();
            return recipe.assemble(alchemyInput, ctx.level().registryAccess());
        }

        return ItemStack.EMPTY;
    }



    @Override
    public Component[] provideInformationOfRitualToPlayer(Player player) {
        return new Component[]{
                Component.translatable(getTranslationKey() + ".info"),
                Component.translatable(getTranslationKey() + ".spiritus.invictus"),
                Component.translatable(getTranslationKey() + ".spiritus.ruina")
        };
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.TENEBRAE);
        addParallelRunes(components, 2, 0, EnumRuneType.FIRE);
        addCornerRunes(components, 2, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 3, 0, EnumRuneType.TENEBRAE);
        addCornerRunes(components, 3, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 4, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 4, 0, EnumRuneType.TENEBRAE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualCrafting();
    }
}
