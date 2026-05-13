package com.breakinblocks.neovitae.impl;

import com.breakinblocks.neovitae.api.INeoVitaeAPI;
import com.breakinblocks.neovitae.api.altar.rune.IAltarRuneRegistry;
import com.breakinblocks.neovitae.api.incense.ITranquilityHandler;
import com.breakinblocks.neovitae.api.incense.TranquilityHandler;
import com.breakinblocks.neovitae.api.sentient.ISentientArmorManager;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.breakinblocks.neovitae.api.will.SpiritusHandler;
import com.breakinblocks.neovitae.api.will.ISpiritusHandler;
import com.breakinblocks.neovitae.api.will.IPlayerSpiritusHandler;
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
    public ISentientArmorManager getSentientArmorManager() {
        return SentientArmorManagerImpl.INSTANCE;
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
    public ISpiritusHandler getSpiritusHandler() {
        return SpiritusHandler.INSTANCE;
    }

    @Override
    public IPlayerSpiritusHandler getPlayerWillHandler() {
        return PlayerSpiritusHandlerImpl.INSTANCE;
    }

    @Override
    public String getApiVersion() {
        return API_VERSION;
    }
}
