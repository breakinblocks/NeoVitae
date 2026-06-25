// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2022-2025 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.breakinblocks.neovitae.util.Utils;
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

        ResourceHandler<ItemResource> inputHandler = ctx.level().getCapability(Capabilities.Item.BLOCK, inputPos, null);
        ResourceHandler<ItemResource> outputHandler = ctx.level().getCapability(Capabilities.Item.BLOCK, outputPos, null);
        if (inputHandler == null || outputHandler == null) return;

        SpiritusState will = RitualHelper.querySpiritus(ctx.level(), masterPos, Math.min(MIN_STEADFAST, MIN_CORROSIVE));

        boolean tryHellfireForge = will.hasInvictus();
        boolean tryAlchemy = will.hasRuina();

        List<ItemStack> inputItems = new ArrayList<>();
        for (int i = 0; i < Math.min(9, inputHandler.size()); i++) {
            ItemStack stack = Utils.stackAt(inputHandler, i);
            inputItems.add(stack.copy());
        }

        // --- STEADFAST: Try Soul Forge recipes first ---
        if (tryHellfireForge) {
            ItemStack result = tryHellfireForgeRecipe(ctx, inputHandler, inputItems);
            if (!result.isEmpty()) {
                ItemStack insertResult = Utils.insertItemStacked(outputHandler, result.copy(), true);
                if (insertResult.isEmpty()) {
                    for (int i = 0; i < Math.min(4, inputHandler.size()); i++) {
                        if (!inputItems.get(i).isEmpty()) {
                            Utils.extractItem(inputHandler, i, 1, false);
                        }
                    }
                    Utils.insertItemStacked(outputHandler, result, false);
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
                ItemStack insertResult = Utils.insertItemStacked(outputHandler, result.copy(), true);
                if (insertResult.isEmpty()) {
                    for (int i = 0; i < Math.min(6, inputHandler.size()); i++) {
                        if (!inputItems.get(i).isEmpty()) {
                            Utils.extractItem(inputHandler, i, 1, false);
                        }
                    }
                    Utils.insertItemStacked(outputHandler, result, false);
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

        CraftingInput craftingInput = CraftingInput.of(3, 3, inputItems);

        Optional<CraftingRecipe> recipeOpt = ctx.serverLevel().recipeAccess()
                .getRecipeFor(RecipeType.CRAFTING, craftingInput, ctx.serverLevel())
                .map(holder -> holder.value());

        if (recipeOpt.isEmpty()) return;

        CraftingRecipe recipe = recipeOpt.get();
        ItemStack result = recipe.assemble(craftingInput);

        if (result.isEmpty()) return;

        ItemStack insertResult = Utils.insertItemStacked(outputHandler, result.copy(), true);
        if (!insertResult.isEmpty()) return;

        for (int i = 0; i < Math.min(9, inputHandler.size()); i++) {
            if (!inputItems.get(i).isEmpty()) {
                Utils.extractItem(inputHandler, i, 1, false);
            }
        }

        Utils.insertItemStacked(outputHandler, result, false);

        ctx.syphon(getRefreshCost());
        RitualHelper.chanceStream(ctx.level(), 8, () ->
                StreamPresets.arcaneBolt(inputPos, outputPos).build()
                        .sendToNearby(ctx.serverLevel(), masterPos, 128));
    }

    private ItemStack tryHellfireForgeRecipe(RitualContext ctx, ResourceHandler<ItemResource> inputHandler, List<ItemStack> inputItems) {
        List<ItemStack> forgeItems = new ArrayList<>();
        for (int i = 0; i < Math.min(4, inputItems.size()); i++) {
            if (!inputItems.get(i).isEmpty()) {
                forgeItems.add(inputItems.get(i).copy());
            }
        }

        if (forgeItems.isEmpty()) return ItemStack.EMPTY;

        // Use empty gem stack (ritual doesn't have a gem slot)
        ForgeInput forgeInput = new ForgeInput(forgeItems, ItemStack.EMPTY, -1);

        Optional<ForgeRecipe> recipeOpt = ctx.serverLevel().recipeAccess()
                .getRecipeFor(NVRecipes.HELLFIRE_FORGE_TYPE.get(), forgeInput, ctx.serverLevel())
                .map(holder -> holder.value());

        if (recipeOpt.isPresent()) {
            ForgeRecipe recipe = recipeOpt.get();
            return recipe.assemble(forgeInput);
        }

        return ItemStack.EMPTY;
    }

    // Tries to find and assemble a Tabula Vitae recipe from the input items.
    // Tabula Vitae recipes use up to 6 ingredients.
    private ItemStack tryTabulaVitaeRecipe(RitualContext ctx, ResourceHandler<ItemResource> inputHandler, List<ItemStack> inputItems) {
        List<ItemStack> alchemyItems = new ArrayList<>();
        for (int i = 0; i < Math.min(TabulaVitaeRecipe.MAX_INPUTS, inputItems.size()); i++) {
            if (!inputItems.get(i).isEmpty()) {
                alchemyItems.add(inputItems.get(i).copy());
            }
        }

        if (alchemyItems.isEmpty()) return ItemStack.EMPTY;

        // Use orb tier 0 since the ritual has no orb
        TabulaVitaeInput alchemyInput = new TabulaVitaeInput(alchemyItems, 0);

        Optional<TabulaVitaeRecipe> recipeOpt = ctx.serverLevel().recipeAccess()
                .getRecipeFor(NVRecipes.TABULA_VITAE_TYPE.get(), alchemyInput, ctx.serverLevel())
                .map(holder -> holder.value());

        if (recipeOpt.isPresent()) {
            TabulaVitaeRecipe recipe = recipeOpt.get();
            return recipe.assemble(alchemyInput);
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
