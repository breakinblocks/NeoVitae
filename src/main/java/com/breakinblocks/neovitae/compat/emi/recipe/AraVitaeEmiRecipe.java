package com.breakinblocks.neovitae.compat.emi.recipe;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.recipe.AraVitaeRecipe;
import com.breakinblocks.neovitae.compat.emi.NVEmiCategories;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.text.DecimalFormat;
import java.util.List;

public class AraVitaeEmiRecipe extends BasicEmiRecipe {

    private static final ResourceLocation TEXTURE = NeoVitae.rl("textures/gui/jei/altar.png");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
    private static final int GRAY = 0x808080;
    private static final int TRANSFER_GREEN = 0x64B464;

    private final AraVitaeRecipe recipe;

    public AraVitaeEmiRecipe(AraVitaeRecipe recipe, ResourceLocation id) {
        super(NVEmiCategories.ARA_VITAE, id, 155, 65);
        this.recipe = recipe;
        this.inputs = List.of(EmiIngredient.of(recipe.getInput()));
        this.outputs = List.of(EmiStack.of(recipe.getResult()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 155, 65, 3, 4);

        widgets.addSlot(inputs.get(0), 31, 0).drawBack(false);
        widgets.addSlot(outputs.get(0), 125, 30).drawBack(false).recipeContext(this);

        var font = Minecraft.getInstance().font;
        Component tierText = Component.translatable("hud.neovitae.altar.tier", recipe.getMinTier());
        Component evText = Component.literal(recipe.getTotalBlood() + " EV");
        widgets.addText(tierText, 90 - font.width(tierText) / 2, 0, GRAY, false);
        widgets.addText(evText, 90 - font.width(evText) / 2, 10, GRAY, false);

        if (recipe.shouldCopyInputComponents()) {
            Component transfer = Component.translatable("jei.neovitae.recipe.componentTransfer");
            widgets.addText(transfer, 90 - font.width(transfer) / 2, 63, TRANSFER_GREEN, false);
        }

        widgets.addTooltipText(List.of(
                Component.translatable("jei.neovitae.recipe.consumptionrate", DECIMAL_FORMAT.format(recipe.getCraftSpeed())),
                Component.translatable("jei.neovitae.recipe.drainrate", DECIMAL_FORMAT.format(recipe.getDrainSpeed()))
        ), 85, 30, 20, 15);
    }
}
