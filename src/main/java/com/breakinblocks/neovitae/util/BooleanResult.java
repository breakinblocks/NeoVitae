// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.util;

public class BooleanResult<T> {
    private final boolean result;
    private final T value;

    private BooleanResult(boolean result, T value) {
        this.result = result;
        this.value = value;
    }

    public boolean isSuccess() {
        return result;
    }

    public T getValue() {
        return value;
    }

    public static <T> BooleanResult<T> newResult(boolean success, T value) {
        return new BooleanResult<>(success, value);
    }
}
