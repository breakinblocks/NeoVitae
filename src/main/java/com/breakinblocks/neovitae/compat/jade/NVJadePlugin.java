package com.breakinblocks.neovitae.compat.jade;

import com.breakinblocks.neovitae.common.block.AlchemyArrayBlock;
import com.breakinblocks.neovitae.common.block.AraVitaeBlock;
import com.breakinblocks.neovitae.common.block.BlockIncenseAltar;
import com.breakinblocks.neovitae.common.block.BlockMasterRitualStone;
import com.breakinblocks.neovitae.common.block.BloodLightBlock;
import com.breakinblocks.neovitae.common.block.BloodTankBlock;
import com.breakinblocks.neovitae.common.block.HellfireForgeBlock;
import com.breakinblocks.neovitae.common.block.OrbFillingLinkBlock;
import com.breakinblocks.neovitae.common.block.SpiritAccumulatorBlock;
import com.breakinblocks.neovitae.common.block.VasMaleficumBlock;
import com.breakinblocks.neovitae.common.block.VitaeLinkBlock;
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
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class NVJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(NVBlockComponentProvider.INSTANCE, AlchemyArrayBlockEntity.class);
        registration.registerBlockDataProvider(NVBlockComponentProvider.INSTANCE, AraVitaeTile.class);
        registration.registerBlockDataProvider(NVBlockComponentProvider.INSTANCE, MasterRitualStoneBlockEntity.class);
        registration.registerBlockDataProvider(NVBlockComponentProvider.INSTANCE, BloodTankBlockEntity.class);
        registration.registerBlockDataProvider(NVBlockComponentProvider.INSTANCE, BloodLightBlockEntity.class);
        registration.registerBlockDataProvider(NVBlockComponentProvider.INSTANCE, HellfireForgeBlockEntity.class);
        registration.registerBlockDataProvider(NVBlockComponentProvider.INSTANCE, IncenseAltarBlockEntity.class);
        registration.registerBlockDataProvider(NVBlockComponentProvider.INSTANCE, VitaeLinkBlockEntity.class);
        registration.registerBlockDataProvider(NVBlockComponentProvider.INSTANCE, OrbFillingLinkBlockEntity.class);
        registration.registerBlockDataProvider(NVBlockComponentProvider.INSTANCE, SpiritAccumulatorBlockEntity.class);
        registration.registerBlockDataProvider(NVBlockComponentProvider.INSTANCE, VasMaleficumBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(NVBlockComponentProvider.INSTANCE, AlchemyArrayBlock.class);
        registration.registerBlockComponent(NVBlockComponentProvider.INSTANCE, AraVitaeBlock.class);
        registration.registerBlockComponent(NVBlockComponentProvider.INSTANCE, BlockMasterRitualStone.class);
        registration.registerBlockComponent(NVBlockComponentProvider.INSTANCE, BloodTankBlock.class);
        registration.registerBlockComponent(NVBlockComponentProvider.INSTANCE, BloodLightBlock.class);
        registration.registerBlockComponent(NVBlockComponentProvider.INSTANCE, HellfireForgeBlock.class);
        registration.registerBlockComponent(NVBlockComponentProvider.INSTANCE, BlockIncenseAltar.class);
        registration.registerBlockComponent(NVBlockComponentProvider.INSTANCE, VitaeLinkBlock.class);
        registration.registerBlockComponent(NVBlockComponentProvider.INSTANCE, OrbFillingLinkBlock.class);
        registration.registerBlockComponent(NVBlockComponentProvider.INSTANCE, SpiritAccumulatorBlock.class);
        registration.registerBlockComponent(NVBlockComponentProvider.INSTANCE, VasMaleficumBlock.class);
        registration.addRayTraceCallback((hitResult, accessor, original) -> {
            if (accessor instanceof snownee.jade.api.EntityAccessor entityAccessor
                    && entityAccessor.getEntity() instanceof com.breakinblocks.neovitae.common.entity.BloodShieldEntity) {
                return null;
            }
            return accessor;
        });
    }
}
