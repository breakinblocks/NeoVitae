package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.damagesource.NVDamageSources;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Ritual of Butchering - slaughters adult animals in range and harvests their drops,
 * leaving a configurable number of each species alive so a breeding stock remains.
 * The keep-per-species value is set on the Master Ritual Stone with the Ritual Configurator.
 */
public class RitualButchering extends Ritual {

    public static final String BUTCHER_RANGE = "butcherRange";
    public static final String CHEST_RANGE = "chestRange";

    public RitualButchering() {
        super("butchering", 0, 25000, "ritual." + NeoVitae.MODID + ".butchering");
        addBlockRange(BUTCHER_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, 0, -5), 11, 3, 11));
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));

        setMaximumVolumeAndDistanceOfRange(BUTCHER_RANGE, 1000, 10, 10);
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 5, 5);
    }

    @Override
    public boolean usesKeepCount() {
        return true;
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        int keep = Math.max(0, masterRitualStone.getKeepCount());
        int maxKills = ctx.maxOperations(getRefreshCost());
        if (maxKills <= 0) return;

        List<Animal> animals = RitualHelper.getEntitiesInRange(ctx, this, BUTCHER_RANGE,
                Animal.class, animal -> animal.isAlive() && !animal.isBaby());
        if (animals.isEmpty()) return;

        Map<EntityType<?>, Integer> counts = new HashMap<>();
        for (Animal animal : animals) {
            counts.merge(animal.getType(), 1, Integer::sum);
        }

        DamageSource source = ctx.level().damageSources().source(NVDamageSources.RITUAL);
        int kills = 0;
        for (Animal animal : animals) {
            if (kills >= maxKills) break;
            EntityType<?> type = animal.getType();
            int remaining = counts.getOrDefault(type, 0);
            if (remaining <= keep) continue;
            animal.hurt(source, Float.MAX_VALUE);
            counts.put(type, remaining - 1);
            kills++;
        }

        if (kills == 0) return;

        RitualHelper.ChestOutput chest = RitualHelper.resolveChestOutput(ctx, this, CHEST_RANGE);
        BlockEntity chestTile = chest.tile();
        ResourceHandler<ItemResource> outputHandler = chestTile != null ? Utils.getInventory(chestTile, Direction.DOWN) : null;
        if (outputHandler != null) {
            List<ItemEntity> drops = RitualHelper.getEntitiesInRange(ctx, this, BUTCHER_RANGE,
                    ItemEntity.class, ItemEntity::isAlive);
            for (ItemEntity itemEntity : drops) {
                ItemStack remainder = Utils.insertItemStacked(outputHandler, itemEntity.getItem(), false);
                if (remainder.isEmpty()) {
                    itemEntity.discard();
                } else {
                    itemEntity.setItem(remainder);
                }
            }
        }

        ctx.syphon(getRefreshCost() * kills);
    }

    @Override
    public Component[] provideInformationOfRitualToPlayer(Player player) {
        return new Component[]{ Component.translatable(getTranslationKey() + ".info") };
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 2, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 0, EnumRuneType.FIRE);
        addRune(components, 0, 0, 3, EnumRuneType.AIR);
        addRune(components, 0, 0, -3, EnumRuneType.AIR);
        addRune(components, 3, 0, 0, EnumRuneType.AIR);
        addRune(components, -3, 0, 0, EnumRuneType.AIR);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualButchering();
    }
}
