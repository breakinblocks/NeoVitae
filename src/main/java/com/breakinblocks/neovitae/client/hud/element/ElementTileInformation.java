package com.breakinblocks.neovitae.client.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import com.breakinblocks.neovitae.client.hud.HUDElement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class ElementTileInformation<T extends BlockEntity> extends HUDElement {

    protected final Class<T> tileClass;
    private final List<Function<T, Component>> rows = new ArrayList<>();

    public ElementTileInformation(int width, int lines, Class<T> tileClass) {
        super(width, 11 * lines);
        this.tileClass = tileClass;
        gatherInformation(rows::add);
    }

    public abstract void gatherInformation(Consumer<Function<T, Component>> rows);

    @Nullable
    @SuppressWarnings("unchecked")
    protected T targetTile(Minecraft mc) {
        HitResult trace = mc.hitResult;
        if (trace == null || trace.getType() != HitResult.Type.BLOCK || mc.level == null) {
            return null;
        }
        BlockEntity be = mc.level.getBlockEntity(((BlockHitResult) trace).getBlockPos());
        if (be == null || !tileClass.isInstance(be)) {
            return null;
        }
        return (T) be;
    }

    @Override
    public boolean shouldRender(Minecraft minecraft) {
        return targetTile(minecraft) != null;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, float partialTicks, int drawX, int drawY) {
        Minecraft mc = Minecraft.getInstance();
        T tile = targetTile(mc);
        int y = drawY;
        for (Function<T, Component> row : rows) {
            Component text = tile != null ? row.apply(tile) : Component.literal("?");
            guiGraphics.drawString(mc.font, text, drawX, y, 0xFFFFFFFF, true);
            y += 11;
        }
    }
}
