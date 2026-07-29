package com.breakinblocks.neovitae.gametest;

import io.netty.buffer.Unpooled;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.recipe.AnointmentApplyRecipe;

@GameTestHolder("neovitae")
@PrefixGameTestTemplate(false)
public class AnointmentTests {

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void anointmentAppliesViaSmithing(GameTestHelper helper) {
        ItemStack anointment = new ItemStack(NVItems.SILK_TOUCH_ANOINTMENT.get());
        ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
        SmithingRecipeInput input = new SmithingRecipeInput(anointment, tool, ItemStack.EMPTY);

        AnointmentApplyRecipe recipe = new AnointmentApplyRecipe();
        if (!recipe.isTemplateIngredient(anointment)) {
            helper.fail("Anointment should be accepted in the smithing template slot");
            return;
        }
        if (!recipe.isBaseIngredient(tool)) {
            helper.fail("Pickaxe should be accepted in the smithing base slot");
            return;
        }
        if (!recipe.matches(input, helper.getLevel())) {
            helper.fail("Anointment smithing recipe should match anointment + pickaxe");
            return;
        }

        ItemStack result = recipe.assemble(input, helper.getLevel().registryAccess());
        if (result.isEmpty()) {
            helper.fail("Assembling the anointment smithing recipe produced nothing");
            return;
        }
        if (result.get(NVDataComponents.ANOINTMENT_HOLDER.get()) == null) {
            helper.fail("Result item has no anointment holder after smithing");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty_5x5x7", timeoutTicks = 20)
    public void anointmentRecipeStreamCodecEncodes(GameTestHelper helper) {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        AnointmentApplyRecipe recipe = new AnointmentApplyRecipe();
        try {
            AnointmentApplyRecipe.STREAM_CODEC.encode(buf, recipe);
        } catch (Exception e) {
            helper.fail("Recipe stream codec failed to encode a fresh instance (this kicks clients on login): " + e);
            return;
        }
        if (AnointmentApplyRecipe.STREAM_CODEC.decode(buf) == null) {
            helper.fail("Recipe stream codec decoded to null");
            return;
        }
        helper.succeed();
    }
}
