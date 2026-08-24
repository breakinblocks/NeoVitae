package com.breakinblocks.neovitae.compat.emi.recipe;

import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.compat.emi.NVEmiCategories;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ImperfectRitualEmiRecipe extends BasicEmiRecipe {

    private static final int WIDTH = 160;
    private static final int HEIGHT = 80;
    private static final int DARK_GRAY = 0x404040;
    private static final int GRAY = 0x808080;
    private static final int CONSUMED_RED = 0xCC4444;

    private final ResourceLocation ritualId;
    private final int activationCost;
    private final Component description;
    private final boolean consumesBlock;

    public ImperfectRitualEmiRecipe(ResourceLocation ritualId, List<ItemStack> catalystBlocks, int activationCost,
                                    Component description, boolean consumesBlock) {
        super(NVEmiCategories.IMPERFECT_RITUAL, NVEmiCategories.synthetic(ritualId), WIDTH, HEIGHT);
        this.ritualId = ritualId;
        this.activationCost = activationCost;
        this.description = description;
        this.consumesBlock = consumesBlock;

        List<EmiIngredient> stacks = new ArrayList<>(catalystBlocks.size());
        for (ItemStack stack : catalystBlocks) {
            stacks.add(EmiStack.of(stack));
        }
        this.inputs = stacks.isEmpty() ? List.of() : List.of(EmiIngredient.of(stacks));
        this.catalysts = List.of(EmiStack.of(NVBlocks.IMPERFECT_RITUAL_STONE.block().get()));
        this.outputs = List.of();
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        var font = Minecraft.getInstance().font;

        Component ritualName = Component.translatable("ritual.neovitae.imperfect." + ritualId.getPath());
        widgets.addText(ritualName, (WIDTH - font.width(ritualName)) / 2, 2, DARK_GRAY, false);

        String costText = activationCost + " EV";
        if (consumesBlock) {
            costText += " (Consumed)";
        }
        Component cost = Component.literal(costText);
        widgets.addText(cost, (WIDTH - font.width(cost)) / 2, 53, consumesBlock ? CONSUMED_RED : GRAY, false);

        int descWidth = font.width(description);
        widgets.addText(description, descWidth <= WIDTH - 4 ? (WIDTH - descWidth) / 2 : 2, 65, DARK_GRAY, false);

        if (!inputs.isEmpty()) {
            widgets.addSlot(inputs.get(0), 71, 15);
        }
        widgets.addSlot(catalysts.get(0), 71, 33);
    }
}
