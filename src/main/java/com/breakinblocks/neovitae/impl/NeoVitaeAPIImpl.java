package com.breakinblocks.neovitae.impl;

import com.breakinblocks.neovitae.api.INeoVitaeAPI;
import com.breakinblocks.neovitae.api.altar.rune.IAltarRuneRegistry;
import com.breakinblocks.neovitae.api.incense.ITranquilityHandler;
import com.breakinblocks.neovitae.api.incense.TranquilityHandler;
import com.breakinblocks.neovitae.api.living.ILivingArmorManager;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.breakinblocks.neovitae.api.will.DemonWillHandler;
import com.breakinblocks.neovitae.api.will.IDemonWillHandler;
import com.breakinblocks.neovitae.api.will.IPlayerDemonWillHandler;
import com.breakinblocks.neovitae.util.helper.AnimaHelper;

import javax.annotation.Nullable;
import java.util.UUID;

public class NeoVitaeAPIImpl implements INeoVitaeAPI {

    public static final NeoVitaeAPIImpl INSTANCE = new NeoVitaeAPIImpl();

    private static final String API_VERSION = "1.1.0";

    private NeoVitaeAPIImpl() {}


    @Override
    @Nullable
    public IAnima getAnima(UUID uuid) {
        return AnimaHelper.getAnima(uuid);
    }

    @Override
    public ILivingArmorManager getLivingArmorManager() {
        return LivingArmorManagerImpl.INSTANCE;
    }

    @Override
    public IAltarRuneRegistry getRuneRegistry() {
        return AltarRuneRegistryImpl.INSTANCE;
    }

    @Override
    public ITranquilityHandler getTranquilityHandler() {
        return TranquilityHandler.INSTANCE;
    }

    @Override
    public IDemonWillHandler getDemonWillHandler() {
        return DemonWillHandler.INSTANCE;
    }

    @Override
    public IPlayerDemonWillHandler getPlayerWillHandler() {
        return PlayerDemonWillHandlerImpl.INSTANCE;
    }

    @Override
    public String getApiVersion() {
        return API_VERSION;
    }
}
