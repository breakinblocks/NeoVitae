package com.breakinblocks.neovitae.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.menu.RoutingNodeMenu;
import com.breakinblocks.neovitae.common.network.RoutingNodePayload;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen for Input/Output Routing Nodes.
 * Matches 1.20.1 layout with single filter slot and direction buttons.
 */
public class RoutingNodeScreen extends AbstractContainerScreen<RoutingNodeMenu> {
    // Use 1.20.1 texture filename (no underscores)
    private static final ResourceLocation BACKGROUND = NeoVitae.rl("textures/gui/routingnode.png");

    // Direction labels for buttons - order matches 1.20.1 (U, -, D, N, S, -)
    private static final String[] DIRECTION_LABELS = {"U", "D", "N", "S", "E", "W"};
    private static final String[] DIRECTION_NAMES = {"Up", "Down", "North", "South", "East", "West"};
    // Direction mapping: index 0=Down, 1=Up, 2=North, 3=South, 4=West, 5=East (3DDataValue order)
    private static final Direction[] DIRECTIONS = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    // Map from our button order to Direction.get3DDataValue order
    private static final int[] SLOT_TO_DIRECTION = {1, 0, 2, 3, 5, 4}; // U->UP(1), D->DOWN(0), N->NORTH(2), S->SOUTH(3), E->EAST(5), W->WEST(4)

    // Button positions from 1.20.1
    // Button 0 (U): (129, 11)
    // Button 1 (-): (109, 31)  - priority decrement
    // Button 2 (D): (129, 31)
    // Button 3 (N): (149, 31)
    // Button 4 (S): (129, 51)
    // Button 5 (E): (149, 51)
    // Button 6 (W): (109, 51) - inferred

    private Button[] directionButtons = new Button[6];
    private Button priorityUpButton;
    private Button priorityDownButton;

    public RoutingNodeScreen(RoutingNodeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 187;  // Match 1.20.1 (was 166)
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        // Direction buttons matching 1.20.1 positions
        // Up at (129, 11)
        directionButtons[0] = createDirectionButton(129, 11, 0, "U");
        // Down at (129, 31)
        directionButtons[1] = createDirectionButton(129, 31, 1, "D");
        // North at (149, 31)
        directionButtons[2] = createDirectionButton(149, 31, 2, "N");
        // South at (129, 51)
        directionButtons[3] = createDirectionButton(129, 51, 3, "S");
        // East at (149, 51)
        directionButtons[4] = createDirectionButton(149, 51, 4, "E");
        // West at (109, 51)
        directionButtons[5] = createDirectionButton(109, 51, 5, "W");

        for (Button btn : directionButtons) {
            this.addRenderableWidget(btn);
        }

        // Priority buttons matching 1.20.1 positions (61, 50) and (89, 50)
        priorityDownButton = Button.builder(Component.literal("-"), btn -> {
            menu.decrementPriority();
            PacketDistributor.sendToServer(new RoutingNodePayload(menu.tile.getBlockPos(), RoutingNodePayload.ACTION_DECREMENT_PRIORITY, 0));
        }).bounds(leftPos + 61, topPos + 50, 16, 16).build();
        this.addRenderableWidget(priorityDownButton);

        priorityUpButton = Button.builder(Component.literal("+"), btn -> {
            menu.incrementPriority();
            PacketDistributor.sendToServer(new RoutingNodePayload(menu.tile.getBlockPos(), RoutingNodePayload.ACTION_INCREMENT_PRIORITY, 0));
        }).bounds(leftPos + 89, topPos + 50, 16, 16).build();
        this.addRenderableWidget(priorityUpButton);
    }

    private Button createDirectionButton(int x, int y, int dirIndex, String label) {
        return Button.builder(Component.literal(label), btn -> {
            menu.selectSlot(dirIndex);
            PacketDistributor.sendToServer(new RoutingNodePayload(menu.tile.getBlockPos(), RoutingNodePayload.ACTION_SELECT_SLOT, dirIndex));
        }).bounds(leftPos + x, topPos + y, 16, 16).build();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        // Highlight selected direction button
        int currentSlot = menu.getCurrentSlot();
        for (int i = 0; i < 6; i++) {
            directionButtons[i].active = (i != currentSlot);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Draw title
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        // Draw inventory label
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);

        // Draw current direction name
        int currentSlot = menu.getCurrentSlot();
        if (currentSlot >= 0 && currentSlot < 6) {
            String dirName = DIRECTION_NAMES[currentSlot];
            guiGraphics.drawString(font, dirName, 8, 20, 0x404040, false);

            // Draw priority value
            int priority = menu.getCurrentPriority();
            String priorityStr = String.valueOf(priority);
            int textWidth = font.width(priorityStr);
            guiGraphics.drawString(font, priorityStr, 79 - textWidth / 2, 54, 0x404040, false);

            // Draw neighbor info for current direction
            if (menu.tile != null) {
                Direction dir = Direction.from3DDataValue(currentSlot);
                String neighborName = menu.tile.getNeighborName(dir);
                boolean hasInv = menu.tile.hasInventoryNeighbor(dir);

                // Truncate long names
                if (neighborName.length() > 18) {
                    neighborName = neighborName.substring(0, 16) + "...";
                }

                // Draw neighbor label
                int color = hasInv ? 0x40A040 : 0x808080; // Green if inventory, gray if not
                guiGraphics.drawString(font, "Neighbor:", 8, 70, 0x404040, false);
                guiGraphics.drawString(font, neighborName, 8, 80, color, false);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        // Add tooltips for direction buttons
        for (int i = 0; i < 6; i++) {
            if (directionButtons[i].isHovered()) {
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.literal(DIRECTION_NAMES[i] + " Face"));

                // Show neighbor info in tooltip
                if (menu.tile != null) {
                    Direction dir = Direction.from3DDataValue(i);
                    String neighborName = menu.tile.getNeighborName(dir);
                    boolean hasInv = menu.tile.hasInventoryNeighbor(dir);

                    if (hasInv) {
                        tooltip.add(Component.literal("Inventory: " + neighborName).withStyle(ChatFormatting.GREEN));
                    } else {
                        tooltip.add(Component.literal("Block: " + neighborName).withStyle(ChatFormatting.GRAY));
                    }

                    // Show priority for this direction
                    int priority = menu.getPriority(dir);
                    tooltip.add(Component.literal("Priority: " + priority).withStyle(ChatFormatting.YELLOW));

                    // Show swap hint if not currently selected
                    int currentSlot = menu.getCurrentSlot();
                    if (i != currentSlot) {
                        tooltip.add(Component.literal("Right-click to swap priority").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                    }
                }

                guiGraphics.renderTooltip(font, tooltip, java.util.Optional.empty(), mouseX, mouseY);
                return; // Only show one tooltip at a time
            }
        }

        // Priority button tooltips
        if (priorityUpButton.isHovered()) {
            guiGraphics.renderTooltip(font, Component.literal("Increase Priority"), mouseX, mouseY);
        }
        if (priorityDownButton.isHovered()) {
            guiGraphics.renderTooltip(font, Component.literal("Decrease Priority"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle right-click on direction buttons for priority swap
        if (button == 1) { // Right click
            for (int i = 0; i < 6; i++) {
                if (directionButtons[i].isHovered() && i != menu.getCurrentSlot()) {
                    // Swap priority with this direction
                    if (menu.tile != null) {
                        menu.tile.swapPriorityWith(i);
                        PacketDistributor.sendToServer(new RoutingNodePayload(
                                menu.tile.getBlockPos(),
                                RoutingNodePayload.ACTION_SWAP_PRIORITY,
                                i
                        ));
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
