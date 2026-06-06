// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2020-2022 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.common.event;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public abstract class NeoVitaeCraftedEvent extends Event {
    protected ItemStack[] inputs;
    protected ItemStack output;

    public ItemStack[] getInputs() {
        return inputs.clone();
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public void setOutput(ItemStack outputStack) {
        this.output = outputStack.copy();
    }

    public static class Altar extends NeoVitaeCraftedEvent {
        public Altar(ItemStack output, ItemStack input) {
            this.inputs = new ItemStack[]{input};
            this.output = output;
        }
    }

    public static class Forge extends NeoVitaeCraftedEvent {
        public Forge(ItemStack output, ItemStack[] inputs) {
            this.inputs = inputs;
            this.output = output;
        }
    }
}
