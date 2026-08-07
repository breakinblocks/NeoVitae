package com.breakinblocks.neovitae.client;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookIcon;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookSpotlightPage;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.data.BookDataManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import com.breakinblocks.neovitae.NeoVitae;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class GuideQuickOpen {

    private static final ResourceLocation BOOK_ID = NeoVitae.rl("guide");

    private static Map<Item, ResourceLocation> itemToEntry;

    private GuideQuickOpen() {}

    public static void invalidate() {
        itemToEntry = null;
    }

    public static boolean openFor(ItemStack stack) {
        if (stack.isEmpty()) return false;

        Book book = BookDataManager.get().getBook(BOOK_ID);
        if (book == null) return false;

        ResourceLocation entryId = index(book).get(stack.getItem());
        if (entryId == null) return false;

        BookGuiManager.get().openEntry(BOOK_ID, entryId, 0);
        return true;
    }

    public static ItemStack hoveredStack() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof AbstractContainerScreen<?> containerScreen) {
            var slot = containerScreen.getSlotUnderMouse();
            if (slot != null && slot.hasItem()) {
                return slot.getItem();
            }
            return ItemStack.EMPTY;
        }
        return mc.player == null ? ItemStack.EMPTY : mc.player.getMainHandItem();
    }

    private static Map<Item, ResourceLocation> index(Book book) {
        if (itemToEntry != null) return itemToEntry;

        Map<Item, ResourceLocation> map = new HashMap<>();
        for (BookEntry entry : book.getEntries().values()) {
            for (BookPage page : entry.getPages()) {
                if (page instanceof BookSpotlightPage spotlight) {
                    spotlight.getItem().ifLeft(stack -> put(map, stack, entry.getId()));
                }
            }
        }

        RegistryAccess registries = registries();
        if (registries != null) {
            for (BookEntry entry : book.getEntries().values()) {
                put(map, iconStack(entry.getIcon(), registries), entry.getId());
            }
        }

        itemToEntry = map;
        return map;
    }

    private static RegistryAccess registries() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level == null ? null : mc.level.registryAccess();
    }

    private static ItemStack iconStack(BookIcon icon, RegistryAccess registries) {
        if (icon == null) return ItemStack.EMPTY;
        try {
            RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registries);
            icon.toNetwork(buf);
            if (buf.readBoolean()) return ItemStack.EMPTY;
            return ItemStack.STREAM_CODEC.decode(buf);
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    private static void put(Map<Item, ResourceLocation> map, ItemStack stack, ResourceLocation entryId) {
        if (stack == null || stack.isEmpty()) return;
        map.putIfAbsent(stack.getItem(), entryId);
    }
}
