package com.breakinblocks.neovitae.compat.emi.recipe;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.recipe.tabulavitae.TabulaVitaeRecipe;
import com.breakinblocks.neovitae.compat.emi.NVEmiCategories;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TabulaVitaeEmiRecipe extends BasicEmiRecipe {

    private static final ResourceLocation TEXTURE = NeoVitae.rl("textures/gui/jei/alchemytable.png");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
    private static final int LABEL_GRAY = 0x8b8b8b;

    private final TabulaVitaeRecipe recipe;

    public TabulaVitaeEmiRecipe(TabulaVitaeRecipe recipe, ResourceLocation id) {
        super(NVEmiCategories.TABULA_VITAE, id, 118, 50);
        this.recipe = recipe;

        List<EmiIngredient> ins = new ArrayList<>();
        for (Ingredient ingredient : recipe.getInput()) {
            ins.add(EmiIngredient.of(ingredient));
        }
        this.inputs = ins;
        this.catalysts = List.of(EmiIngredient.of(orbsForTier(recipe.getMinimumTier())));
        this.outputs = List.of(EmiStack.of(recipe.getOutput()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURE, 0, 0, 118, 50, 0, 0);

        for (int i = 0; i < inputs.size(); i++) {
            widgets.addSlot(inputs.get(i), (i % 3) * 18, (i / 3) * 18).drawBack(false);
        }
        widgets.addSlot(catalysts.get(0), 60, 0).drawBack(false).catalyst(true);
        widgets.addSlot(outputs.get(0), 91, 13).drawBack(false).recipeContext(this);

        widgets.addTooltipText(List.of(
                Component.translatable("jei.neovitae.recipe.requiredtier", DECIMAL_FORMAT.format(recipe.getMinimumTier())),
                Component.translatable("jei.neovitae.recipe.lpDrained", DECIMAL_FORMAT.format(recipe.getSyphon())),
                Component.translatable("jei.neovitae.recipe.ticksRequired", DECIMAL_FORMAT.format(recipe.getTicks()))
        ), 53, 14, 40, 35);

        widgets.addDrawable(0, 0, 118, 50, (graphics, mouseX, mouseY, delta) -> {
            var font = net.minecraft.client.Minecraft.getInstance().font;
            var pose = graphics.pose();
            pose.pushPose();
            pose.translate(67, 37, 0);
            pose.scale(0.5f, 0.5f, 1f);
            graphics.drawString(font, Component.translatable("jei.neovitae.recipe.lp"), 0, 0, LABEL_GRAY, false);
            pose.popPose();
            pose.pushPose();
            pose.translate(53, 43, 0);
            pose.scale(0.5f, 0.5f, 1f);
            graphics.drawString(font, Component.translatable("jei.neovitae.recipe.info"), 0, 0, LABEL_GRAY, false);
            pose.popPose();
        });
    }

    private static List<EmiIngredient> orbsForTier(int tier) {
        ItemLike[] orbs = {NVItems.ORB_WEAK.get(), NVItems.ORB_APPRENTICE.get(), NVItems.ORB_MAGICIAN.get(),
                NVItems.ORB_MASTER.get(), NVItems.ORB_ARCHMAGE.get(), NVItems.ORB_TRANSCENDENT.get()};
        List<EmiIngredient> out = new ArrayList<>();
        for (int i = 0; i < orbs.length; i++) {
            if (tier <= i) out.add(EmiStack.of(orbs[i]));
        }
        return out;
    }
}
