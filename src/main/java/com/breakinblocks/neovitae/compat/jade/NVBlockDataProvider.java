package com.breakinblocks.neovitae.compat.jade;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffect;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectBounce;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectDay;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectElevator;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectMovement;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectNight;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectSpike;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectUndertow;
import com.breakinblocks.neovitae.common.alchemyarray.AlchemyArrayEffectUpdraft;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.blockentity.BloodLightBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.BloodTankBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.HellfireForgeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.IncenseAltarBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.OrbFillingLinkBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.SpiritAccumulatorBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.VasMaleficumBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.VitaeLinkBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.item.SpiritusCrystalItem;
import com.breakinblocks.neovitae.spiritus.SpiritusHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public enum NVBlockDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    static final Identifier UID = NeoVitae.rl("block_info");

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();

        if (be instanceof AlchemyArrayBlockEntity array) {
            AlchemyArrayEffect effect = array.arrayEffect;
            if (effect != null) {
                data.putString("array_effect", getEffectName(effect));

                if (effect instanceof AlchemyArrayEffectUpdraft) {
                    int feathers = array.getItem(0).is(Items.FEATHER) ? array.getItem(0).getCount() : 0;
                    int glowstone = array.getItem(1).is(Items.GLOWSTONE_DUST) ? array.getItem(1).getCount() : 0;
                    double base = 1.0;
                    data.putDouble("array_max_vel", base + 0.1 * glowstone + 0.05 * feathers);
                } else if (effect instanceof AlchemyArrayEffectUndertow undertow) {
                    int kelp = array.getItem(0).is(Items.KELP) ? array.getItem(0).getCount() : 0;
                    int redstone = array.getItem(1).is(Items.REDSTONE) ? array.getItem(1).getCount() : 0;
                    boolean dragDown = undertow.isDragDown();
                    data.putString("array_mode", dragDown ? "Downward" : "Upward");
                    data.putBoolean("array_mode_down", dragDown);
                    data.putDouble("array_accel", (dragDown ? 0.05 : 0.12) + 0.003 * redstone);
                    data.putDouble("array_max_vel", (dragDown ? 0.9 : 1.8) + 0.015 * kelp);
                }
            }
        }

        if (be instanceof AraVitaeTile altar) {
            data.putInt("altar_tier", altar.getTier());
            data.putInt("altar_ev", altar.getMainTank());
            data.putInt("altar_capacity", altar.getMainCapacity());
            data.putBoolean("altar_active", altar.isActive());
        }

        if (be instanceof MasterRitualStoneBlockEntity mrs) {
            if (mrs.getCurrentRitual() != null) {
                data.putString("ritual_name", mrs.getCurrentRitual().getTranslationKey());
                data.putBoolean("ritual_active", mrs.isActive());
            }
        }

        if (be instanceof BloodTankBlockEntity tank) {
            data.putInt("tank_amount", tank.getFluidContained().getAmount());
            data.putInt("tank_capacity", tank.getCapacity());
        }

        if (be instanceof BloodLightBlockEntity light) {
            data.putString("light_color", light.getColor().getName());
        }

        if (be instanceof HellfireForgeBlockEntity forge) {
            int progress = forge.getProgress();
            if (progress > 0) {
                data.putInt("forge_progress", (int) (forge.getProgressForGui() * 100));
            }
        }

        if (be instanceof IncenseAltarBlockEntity incense) {
            data.putDouble("tranquility", incense.getTranquility());
            data.putDouble("incense_bonus", incense.getIncenseAddition());
        }

        if (be instanceof VitaeLinkBlockEntity link) {
            data.putBoolean("link_linked", link.isLinked());
            data.putInt("link_tier", link.getCraftTier());
            data.putInt("link_max", Math.max(0, link.getMaxLinkTier()));
            data.putBoolean("link_crafting", link.isClientCrafting());
        }

        if (be instanceof OrbFillingLinkBlockEntity orbLink) {
            data.putBoolean("orb_link_linked", orbLink.isLinked());
            data.putInt("orb_link_network", orbLink.getNetworkPercent());
        }

        if (be instanceof SpiritAccumulatorBlockEntity accumulator) {
            data.putString("accumulator_type", accumulator.getAttunedType() == null ? "" : accumulator.getAttunedType().getSerializedName());
            data.putDouble("accumulator_stored", accumulator.getStored());
        }

        if (be instanceof VasMaleficumBlockEntity vas) {
            ItemStack stack = vas.inventory.getStackInSlot(0);
            boolean powered = accessor.getLevel().hasNeighborSignal(accessor.getPosition());
            if (stack.getItem() instanceof SpiritusCrystalItem crystal) {
                data.putString("vas_type", crystal.getSpiritusType().getSerializedName());
                data.putDouble("vas_stored", crystal.getSpiritus(stack));
                data.putString("vas_mode", powered ? "idle" : "seeding");
            } else if (SpiritusHelper.hasSpiritus(stack)) {
                SpiritusType type = SpiritusHelper.getCurrentType(stack);
                data.putString("vas_type", type.getSerializedName());
                data.putDouble("vas_stored", SpiritusHelper.getSpiritus(stack, type));
                data.putDouble("vas_max", SpiritusHelper.resolveMaxSpiritus(stack, type));
                if (SpiritusHelper.isRechargeable(stack)) {
                    data.putString("vas_mode", powered ? "filling" : "releasing");
                } else {
                    data.putString("vas_mode", powered ? "idle" : "releasing");
                }
            }
        }
    }

    private static String getEffectName(AlchemyArrayEffect effect) {
        if (effect instanceof AlchemyArrayEffectBounce) return "jade.neovitae.array_effect.bounce";
        if (effect instanceof AlchemyArrayEffectSpike) return "jade.neovitae.array_effect.spike";
        if (effect instanceof AlchemyArrayEffectUpdraft) return "jade.neovitae.array_effect.updraft";
        if (effect instanceof AlchemyArrayEffectUndertow) return "jade.neovitae.array_effect.undertow";
        if (effect instanceof AlchemyArrayEffectMovement) return "jade.neovitae.array_effect.movement";
        if (effect instanceof AlchemyArrayEffectDay) return "jade.neovitae.array_effect.day";
        if (effect instanceof AlchemyArrayEffectNight) return "jade.neovitae.array_effect.night";
        if (effect instanceof AlchemyArrayEffectElevator) return "jade.neovitae.array_effect.elevator";
        return "jade.neovitae.array_effect.generic";
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
