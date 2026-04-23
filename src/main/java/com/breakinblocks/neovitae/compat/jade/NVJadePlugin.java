package com.breakinblocks.neovitae.compat.jade;

import com.breakinblocks.neovitae.common.block.AlchemyArrayBlock;
import com.breakinblocks.neovitae.common.block.AraVitaeBlock;
import com.breakinblocks.neovitae.common.block.BlockIncenseAltar;
import com.breakinblocks.neovitae.common.block.BlockMasterRitualStone;
import com.breakinblocks.neovitae.common.block.BloodLightBlock;
import com.breakinblocks.neovitae.common.block.BloodTankBlock;
import com.breakinblocks.neovitae.common.block.HellfireForgeBlock;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.AraVitaeTile;
import com.breakinblocks.neovitae.common.blockentity.BloodLightBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.BloodTankBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.HellfireForgeBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.IncenseAltarBlockEntity;
import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class NVJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(NVBlockDataProvider.INSTANCE, AlchemyArrayBlockEntity.class);
        registration.registerBlockDataProvider(NVBlockDataProvider.INSTANCE, AraVitaeTile.class);
        registration.registerBlockDataProvider(NVBlockDataProvider.INSTANCE, MasterRitualStoneBlockEntity.class);
        registration.registerBlockDataProvider(NVBlockDataProvider.INSTANCE, BloodTankBlockEntity.class);
        registration.registerBlockDataProvider(NVBlockDataProvider.INSTANCE, BloodLightBlockEntity.class);
        registration.registerBlockDataProvider(NVBlockDataProvider.INSTANCE, HellfireForgeBlockEntity.class);
        registration.registerBlockDataProvider(NVBlockDataProvider.INSTANCE, IncenseAltarBlockEntity.class);
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
        registration.addRayTraceCallback((hitResult, accessor, original) -> {
            if (accessor instanceof snownee.jade.api.EntityAccessor entityAccessor
                    && entityAccessor.getEntity() instanceof com.breakinblocks.neovitae.common.entity.BloodShieldEntity) {
                return null;
            }
            return accessor;
        });
    }
}
