package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.item.soul.HellforgedHelper;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.EnumRuneType;
import com.breakinblocks.neovitae.ritual.IMasterRitualStone;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.breakinblocks.neovitae.ritual.RitualComponent;
import com.breakinblocks.neovitae.ritual.RitualHelper;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;

import java.util.List;
import java.util.function.Consumer;

public class RitualHellforging extends Ritual {

    public static final String NAME = "hellforging";
    public static final String HELLFORGING_RANGE = "hellforgingRange";

    public RitualHellforging() {
        super(NAME, 2, 1000000, "ritual." + NeoVitae.MODID + "." + NAME);
        addBlockRange(HELLFORGING_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-2, 1, -2), 5, 2, 5));
        setMaximumVolumeAndDistanceOfRange(HELLFORGING_RANGE, 50, 5, 5);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        List<ItemEntity> items = RitualHelper.getEntitiesInRange(ctx, this, HELLFORGING_RANGE, ItemEntity.class);
        if (items.isEmpty()) return;

        ItemEntity gem = find(items, NVTags.Items.HELLFORGING_GEM);
        ItemEntity metal = find(items, NVTags.Items.HELLFORGING_METAL);
        if (gem == null || metal == null) return;

        for (ItemEntity entity : items) {
            ItemStack stack = entity.getItem();
            if (stack.getCount() != 1) continue;
            HellforgedHelper.Readiness readiness = HellforgedHelper.readiness(stack);
            if (readiness == HellforgedHelper.Readiness.NOT_HELLFORGEABLE
                    || readiness == HellforgedHelper.Readiness.ALREADY_HELLFORGED) {
                continue;
            }
            if (readiness == HellforgedHelper.Readiness.NEEDS_DEUS_EVOLUTION) {
                notifyNearby(ctx, Component.translatable("chat." + NeoVitae.MODID + ".hellforging.unready",
                        RitualDeusArmourEvolve.DEUS_MAX_UPGRADE_POINTS));
                masterRitualStone.stopRitual(BreakType.DEACTIVATE);
                return;
            }

            ItemStack forged = stack.copy();
            HellforgedHelper.apply(forged);
            entity.setItem(forged);
            consumeOne(gem);
            consumeOne(metal);

            BlockPos master = ctx.masterPos();
            ctx.serverLevel().playSound(null, master, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, 0.6F);
            ctx.serverLevel().sendParticles(ParticleTypes.LAVA, entity.getX(), entity.getY() + 0.25, entity.getZ(),
                    12, 0.2, 0.2, 0.2, 0.0);
            notifyNearby(ctx, Component.translatable("chat." + NeoVitae.MODID + ".hellforging.forged", forged.getHoverName()));

            ctx.syphon(getRefreshCost());
            return;
        }
    }

    private static ItemEntity find(List<ItemEntity> items, TagKey<Item> tag) {
        for (ItemEntity entity : items) {
            if (entity.isAlive() && entity.getItem().is(tag)) return entity;
        }
        return null;
    }

    private static void consumeOne(ItemEntity entity) {
        ItemStack reduced = entity.getItem().copy();
        reduced.shrink(1);
        if (reduced.isEmpty()) {
            entity.remove(RemovalReason.KILLED);
        } else {
            entity.setItem(reduced);
        }
    }

    private static void notifyNearby(RitualContext ctx, Component message) {
        AABB area = new AABB(ctx.masterPos()).inflate(8);
        for (Player player : ctx.level().getEntitiesOfClass(Player.class, area)) {
            player.displayClientMessage(message, true);
        }
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.DEUS);
        addParallelRunes(components, 2, 0, EnumRuneType.FIRE);
        addCornerRunes(components, 2, 0, EnumRuneType.DEUS);
        addParallelRunes(components, 3, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 3, 0, EnumRuneType.FIRE);
        addParallelRunes(components, 4, 0, EnumRuneType.DEUS);
        addCornerRunes(components, 4, 0, EnumRuneType.AIR);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualHellforging();
    }
}
