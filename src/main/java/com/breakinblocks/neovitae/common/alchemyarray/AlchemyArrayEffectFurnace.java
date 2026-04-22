package com.breakinblocks.neovitae.common.alchemyarray;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.api.soul.AnimaTicket;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.Anima;
import com.breakinblocks.neovitae.common.datacomponent.Binding;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import java.util.*;

public class AlchemyArrayEffectFurnace extends AlchemyArrayEffect {

    private static final double RADIUS = 2.0;
    private static final int LP_COST_PER_STACK = 10;
    private static final int COOK_TIME = 200;

    private final Map<UUID, Integer> cookTimers = new HashMap<>();

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide()) return false;

        BlockPos pos = tile.getBlockPos();
        AABB area = new AABB(pos).inflate(RADIUS);

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);

        Set<UUID> presentItems = new HashSet<>();

        for (ItemEntity itemEntity : items) {
            if (itemEntity.isRemoved()) continue;

            ItemStack stack = itemEntity.getItem();
            Optional<RecipeHolder<SmeltingRecipe>> recipeOpt = level instanceof ServerLevel srv
                    ? srv.recipeAccess().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), srv)
                    : Optional.empty();

            if (recipeOpt.isPresent()) {
                UUID id = itemEntity.getUUID();
                presentItems.add(id);

                itemEntity.setUnlimitedLifetime();

                int timer = cookTimers.getOrDefault(id, 0) + 1;
                cookTimers.put(id, timer);

                if (timer >= COOK_TIME) {
                    Binding binding = tile.getOwnerBinding();
                    boolean hasLP = true;
                    if (!binding.isEmpty()) {
                        Anima network = AnimaHelper.getAnima(binding);
                        if (network != null) {
                            int cost = LP_COST_PER_STACK * stack.getCount();
                            if (network.syphon(AnimaTicket.create(cost)) < cost) {
                                hasLP = false;
                            }
                        }
                    }

                    if (hasLP) {
                        ItemStack result = recipeOpt.get().value().assemble(new SingleRecipeInput(stack)).copy();
                        result.setCount(result.getCount() * stack.getCount());

                        itemEntity.discard();
                        cookTimers.remove(id);

                        ItemEntity resultEntity = new ItemEntity(level,
                                itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result);
                        resultEntity.setDeltaMovement(0, 0.1, 0);
                        level.addFreshEntity(resultEntity);

                        if (level instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xFF6622),
                                    itemEntity.getX(), itemEntity.getY() + 0.3, itemEntity.getZ(),
                                    6, 0.2, 0.1, 0.2, 0.03);
                        }
                    }
                } else if (timer % 20 == 0 && level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), 0xFF8844),
                            itemEntity.getX(), itemEntity.getY() + 0.3, itemEntity.getZ(),
                            2, 0.1, 0.05, 0.1, 0.01);
                }
            }
        }

        cookTimers.keySet().removeIf(id -> !presentItems.contains(id));

        return false;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {}

    @Override
    public void readFromNBT(CompoundTag tag) {}

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectFurnace();
    }
}
