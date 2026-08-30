package com.breakinblocks.neovitae.mixin;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import com.breakinblocks.neovitae.common.world.PoweredReceiverAccess;

@Mixin(Level.class)
public class LevelMixin implements PoweredReceiverAccess {

    @Unique
    private final LongSet neovitae$poweredReceivers = new LongOpenHashSet();

    @Override
    public LongSet neovitae$getPoweredReceivers() {
        return neovitae$poweredReceivers;
    }
}
