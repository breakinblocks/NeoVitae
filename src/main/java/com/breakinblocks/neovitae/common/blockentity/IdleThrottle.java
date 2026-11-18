package com.breakinblocks.neovitae.common.blockentity;

public final class IdleThrottle {

    public static final int RECHECK_INTERVAL = 20;

    private boolean awake = true;
    private int restedTicks;

    public void wake() {
        awake = true;
    }

    public void rest() {
        awake = false;
        restedTicks = 0;
    }

    public boolean isResting() {
        return !awake;
    }

    public boolean shouldSkip() {
        if (awake) {
            return false;
        }
        if (++restedTicks >= RECHECK_INTERVAL) {
            restedTicks = 0;
            return false;
        }
        return true;
    }
}
