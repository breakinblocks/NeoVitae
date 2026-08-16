package com.breakinblocks.neovitae.api.spiritus;

import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

public interface ISpiritusStorage {

    @Nullable
    SpiritusType getStoredType();

    double getStored();

    double getCapacity();

    double extract(SpiritusType type, double amount, boolean simulate);

    double insert(SpiritusType type, double amount, boolean simulate);
}
