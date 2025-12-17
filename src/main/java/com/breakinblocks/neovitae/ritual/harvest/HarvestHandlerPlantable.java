package com.breakinblocks.neovitae.ritual.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.minecraft.core.registries.BuiltInRegistries;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Harvest handler for standard plantable crops such as Wheat, Potatoes, and Netherwart.
 * Register a new crop for this handler with {@link HarvestRegistry#registerStandardCrop(Block, int)}
 */
public class HarvestHandlerPlantable implements IHarvestHandler {

    private static final ItemStack mockHoe = new ItemStack(Items.DIAMOND_HOE, 1);

    public HarvestHandlerPlantable() {
        // Register standard vanilla crops
        HarvestRegistry.registerStandardCrop(Blocks.CARROTS, 7);
        HarvestRegistry.registerStandardCrop(Blocks.WHEAT, 7);
        HarvestRegistry.registerStandardCrop(Blocks.POTATOES, 7);
        HarvestRegistry.registerStandardCrop(Blocks.BEETROOTS, 3);

        // Third-party mod crops
        addThirdPartyCrop("actuallyadditions", "flax_block", 7);
        addThirdPartyCrop("actuallyadditions", "canola_block", 7);
        addThirdPartyCrop("actuallyadditions", "rice_block", 7);

        addThirdPartyCrop("extrautils2", "redorchid", 6);
        addThirdPartyCrop("extrautils2", "enderlily", 7);

        addThirdPartyCrop("roots", "moonglow", 7);
        addThirdPartyCrop("roots", "terra_moss", 7);
        addThirdPartyCrop("roots", "pereskia", 7);
        addThirdPartyCrop("roots", "wildroot", 7);
        addThirdPartyCrop("roots", "aubergine", 7);
        addThirdPartyCrop("roots", "spirit_herb", 7);

        addMysticalCrops();
    }

    @Override
    public boolean harvest(Level level, BlockPos pos, BlockState state, List<ItemStack> drops, @Nullable UUID ownerUUID) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        boolean foundSeed = false;
        LootParams.Builder lootBuilder = new LootParams.Builder(serverLevel);
        Vec3 blockCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        List<ItemStack> blockDrops = state.getDrops(lootBuilder
                .withParameter(LootContextParams.ORIGIN, blockCenter)
                .withParameter(LootContextParams.TOOL, mockHoe));

        for (ItemStack stack : blockDrops) {
            if (stack.isEmpty()) continue;

            // Find and consume one seed for replanting
            if (stack.getItem() instanceof BlockItem blockItem
                    && blockItem.getBlock() == state.getBlock()) {
                stack.shrink(1);
                foundSeed = true;
                break;
            }
        }

        if (foundSeed) {
            // Replace block with protection check
            BlockState newState = state.getBlock().defaultBlockState();
            if (!BlockProtectionHelper.tryReplaceBlock(level, pos, newState, ownerUUID)) {
                return false;
            }
            level.levelEvent(2001, pos, Block.getId(state));

            for (ItemStack stack : blockDrops) {
                if (!stack.isEmpty()) {
                    drops.add(stack);
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean test(Level level, BlockPos pos, BlockState state) {
        return HarvestRegistry.getStandardCrops().containsKey(state.getBlock())
                && state.getBlock() instanceof CropBlock crop
                && crop.isMaxAge(state);
    }

    private static void addThirdPartyCrop(String modid, String regName, int matureMeta) {
        if (!ModList.get().isLoaded(modid)) return;

        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(modid, regName));
        if (block != null && block != Blocks.AIR) {
            HarvestRegistry.registerStandardCrop(block, matureMeta);
        }
    }

    private static void addMysticalCrops() {
        if (!ModList.get().isLoaded("mysticalagriculture")) return;

        try {
            // Use reflection to access Mystical Agriculture API
            Class<?> apiClass = Class.forName("com.blakebr0.mysticalagriculture.api.MysticalAgricultureAPI");
            Class<?> registryClass = Class.forName("com.blakebr0.mysticalagriculture.api.registry.ICropRegistry");
            Class<?> cropClass = Class.forName("com.blakebr0.mysticalagriculture.api.crop.Crop");

            Object cropRegistry = apiClass.getMethod("getCropRegistry").invoke(null);
            Iterable<?> crops = (Iterable<?>) registryClass.getMethod("getCrops").invoke(cropRegistry);

            for (Object crop : crops) {
                CropBlock block = (CropBlock) cropClass.getMethod("getCropBlock").invoke(crop);
                if (block != null) {
                    HarvestRegistry.registerStandardCrop(block, block.getMaxAge());
                }
            }
        } catch (Exception e) {
            NeoVitae.LOGGER.error("Failed to integrate with Mystical Agriculture: " + e.getMessage());
        }
    }
}
