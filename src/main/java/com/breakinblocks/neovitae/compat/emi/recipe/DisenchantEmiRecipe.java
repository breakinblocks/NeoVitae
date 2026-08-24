package com.breakinblocks.neovitae.compat.emi.recipe;

import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.compat.emi.NVEmiCategories;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class DisenchantEmiRecipe extends BasicEmiRecipe {

    private static final int ROW = 12;
    private static final int GREEN = 0x2E8B57;
    private static final int RAW_COLOR = 0xFFAA3333;
    private static final int ARROW_COLOR = 0xFF606060;
    private static final int DROPLET = 81;

    private final EmiIngredient books;

    public DisenchantEmiRecipe(List<ItemStack> enchantedBooks, ResourceLocation id) {
        super(NVEmiCategories.DISENCHANT, id, 120, 64);
        List<EmiStack> stacks = new ArrayList<>(enchantedBooks.size());
        for (ItemStack book : enchantedBooks) {
            stacks.add(EmiStack.of(book));
        }
        this.books = EmiIngredient.of(stacks);
        this.inputs = List.of(books, EmiStack.of(Items.BOOK),
                EmiStack.of(NVFluids.ESSENTIA_VITAE_SOURCE.get(), 100 * DROPLET));
        this.catalysts = List.of(EmiStack.of(NVItems.SANGUINE_REVERTER.get()));
        this.outputs = List.copyOf(stacks);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addText(Component.translatable("jei.neovitae.disenchant.any_item"), 2, 2, GREEN, false);

        widgets.addSlot(inputs.get(0), 1, ROW);
        widgets.addSlot(inputs.get(1), 19, ROW);
        widgets.addSlot(inputs.get(2), 37, ROW);
        widgets.addSlot(catalysts.get(0), 58, ROW).catalyst(true);
        widgets.addSlot(books, 99, ROW).recipeContext(this);

        widgets.addDrawable(0, 0, 120, 64, (graphics, mouseX, mouseY, delta) -> {
            int ay = ROW + 6;
            graphics.fill(79, ay, 95, ay + 2, ARROW_COLOR);
            graphics.fill(91, ay - 3, 95, ay + 5, ARROW_COLOR);
            graphics.fill(92, ay - 2, 95, ay + 4, ARROW_COLOR);
            graphics.fill(93, ay - 1, 95, ay + 3, ARROW_COLOR);
            graphics.fill(2, ROW + 33, 6, ROW + 37, RAW_COLOR);
        });

        int cy = ROW + 22;
        widgets.addText(Component.translatable("jei.neovitae.disenchant.per_enchant"), 2, cy, 0xAAAAAA, true);
        widgets.addText(Component.translatable("jei.neovitae.disenchant.spiritus"), 8, cy + 10, 0xFFFFFF, true);
        widgets.addText(Component.translatable("jei.neovitae.disenchant.ev"), 2, cy + 20, 0xFFFFFF, true);
    }
}
