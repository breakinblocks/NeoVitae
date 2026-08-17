package com.breakinblocks.neovitae.common.routing;

public enum FaceDirection {
    OFF,
    INPUT,
    OUTPUT,
    BOTH;

    public boolean pulls() {
        return this == INPUT || this == BOTH;
    }

    public boolean pushes() {
        return this == OUTPUT || this == BOTH;
    }

    public FaceDirection next() {
        return values()[(ordinal() + 1) % values().length];
    }

    public static FaceDirection byOrdinal(int ordinal) {
        FaceDirection[] values = values();
        return values[Math.max(0, Math.min(values.length - 1, ordinal))];
    }
}
