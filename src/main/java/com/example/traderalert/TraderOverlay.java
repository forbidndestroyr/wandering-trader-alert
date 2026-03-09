package com.example.traderalert;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class TraderOverlay {
    
    public static void renderOverlay(GuiGraphics graphics, Minecraft client) {
        if (!ModConfig.isOverlayMode()) return;
        
        Font font = client.font;
        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        
        // Calculate trader info with fallbacks
        int ticksUntilSpawn = TraderSpawnTracker.getTicksUntilSpawn(client);
        double spawnChance = TraderSpawnTracker.getSpawnChance(client);
        
        // Ensure we have valid values
        if (ticksUntilSpawn < 0) ticksUntilSpawn = 24000; // Default to 1 day
        if (spawnChance < 0) spawnChance = 0.075; // Default to base chance
        
        // Format strings
        String timeString = "Next spawn: " + TraderSpawnTracker.formatTicks(ticksUntilSpawn);
        String chanceString = String.format("Spawn chance: %.1f%%", spawnChance * 100);
        
        // Debug: Always show some text to verify overlay is working
        if (client.level == null) {
            timeString = "Next spawn: No world";
            chanceString = "Spawn chance: 0.0%";
        }
        
        // Calculate positions (top-right corner with padding)
        int padding = 10;
        int lineHeight = font.lineHeight + 2;
        
        int timeWidth = font.width(timeString);
        int chanceWidth = font.width(chanceString);
        int maxWidth = Math.max(timeWidth, chanceWidth);
        
        int x = screenWidth - maxWidth - padding;
        int y = padding;

        // render traderCount 

        String traderCountStr = "Traders Spawned: " + TraderSpawnTracker.getCount();

        
        
        // Draw background
        int bgColor = 0x80000000; // Semi-transparent black
        graphics.fill(x - 4, y - 2, x + maxWidth + 4, y + lineHeight * 2 + 2, bgColor);
        
        // Draw text
        graphics.drawString(font, timeString, x, y, 0xFFFFFFFF, true);
        graphics.drawString(font, chanceString, x, y + lineHeight, 0xFFFFFFFF, true);
        graphics.drawString(font, traderCountStr, x, y + lineHeight * 2, 0xFFFFFFFF, true);
    }
    
    public static void renderSpawnNotification(GuiGraphics graphics, Minecraft client) {
        if (!TraderSpawnTracker.shouldShowSpawnNotification()) return;
        
        Font font = client.font;
        String message = "Wandering Trader Spawned";
        
        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        
        int textWidth = font.width(message);
        int x = (screenWidth - textWidth) / 2;
        int y = screenHeight / 2 - 60; // Above center
        
        // Draw background
        int bgColor = 0x80000000; // Semi-transparent black
        graphics.fill(x - 10, y - 5, x + textWidth + 10, y + font.lineHeight + 5, bgColor);
        
        // Draw text with yellow color
        graphics.drawString(font, message, x, y, 0xFFFFFF00, true);
    }
}
