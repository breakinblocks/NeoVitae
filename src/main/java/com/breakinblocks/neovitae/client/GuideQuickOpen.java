package com.breakinblocks.neovitae.client;

import com.klikli_dev.modonomicon.book.Book;
import com.klikli_dev.modonomicon.book.BookIcon;
import com.klikli_dev.modonomicon.book.entries.BookEntry;
import com.klikli_dev.modonomicon.book.page.BookPage;
import com.klikli_dev.modonomicon.book.page.BookSpotlightPage;
import com.klikli_dev.modonomicon.client.gui.BookGuiManager;
import com.klikli_dev.modonomicon.data.BookDataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import com.breakinblocks.neovitae.NeoVitae;

import java.util.HashMap;
import java.util.Map;

public final class GuideQuickOpen {

    private static final Identifier BOOK_ID = NeoVitae.rl("guide");

    private static Map<Item, Identifier> itemToEntry;

    private GuideQuickOpen() {}

    public static void invalidate() {
        itemToEntry = null;
    }

    public static boolean openFor(ItemStack stack) {
        if (stack.isEmpty()) return false;

        Book book = BookDataManager.get().getBook(BOOK_ID);
        if (book == null) return false;

        Identifier entryId = index(book).get(stack.getItem());
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

    private static Map<Item, Identifier> index(Book book) {
        if (itemToEntry != null) return itemToEntry;

        Map<Item, Identifier> map = new HashMap<>();
        for (BookEntry entry : book.getEntries().values()) {
            for (BookPage page : entry.getPages()) {
                if (page instanceof BookSpotlightPage spotlight) {
                    put(map, spotlight.getCachedItemStack(), entry.getId());
                }
            }
        }
        for (BookEntry entry : book.getEntries().values()) {
            BookIcon icon = entry.getIcon();
            if (icon == null) continue;
            ItemStackTemplate template = icon.itemStackTemplate();
            if (template != null) {
                put(map, template.create(), entry.getId());
            }
        }

        itemToEntry = map;
        return map;
    }

    private static void put(Map<Item, Identifier> map, ItemStack stack, Identifier entryId) {
        if (stack == null || stack.isEmpty()) return;
        map.putIfAbsent(stack.getItem(), entryId);
    }
}
