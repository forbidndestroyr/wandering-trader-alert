package com.example.traderalert;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class DebugCommand {
    
    public static void executeDebugCommand(Minecraft client) {
        if (client.player == null || client.level == null) {
            return;
        }
        
        // Get current world data
        long worldTime = client.level.getDayTime();
        long dayTime = worldTime % 24000;
        long totalDays = worldTime / 24000;
        
        // Get spawn calculations
        int ticksUntilSpawn = TraderSpawnTracker.getTicksUntilSpawn(client);
        double spawnChance = TraderSpawnTracker.getSpawnChance(client);
        
        // Check for existing traders
        Entity currentTrader = null;
        int traderCount = 0;
        for (Entity entity : client.level.entitiesForRendering()) {
            if (entity.getType() == EntityType.WANDERING_TRADER) {
                currentTrader = entity;
                traderCount++;
            }
        }
        
        // Get world spawn data calculation
        WorldSpawnDataReader.SpawnData worldData = WorldSpawnDataReader.getActualSpawnData(client);
        
        // Send debug info to player
        sendMessage(client, "§6=== WANDERING TRADER DEBUG ===");
        sendMessage(client, "§7World Time: §f" + worldTime + " ticks (" + (worldTime/20) + "s)");
        sendMessage(client, "§7Day Time: §f" + dayTime + " / 24000 (" + String.format("%.1f%%", (dayTime/24000.0)*100) + " through day)");
        sendMessage(client, "§7Total Days: §f" + totalDays);
        sendMessage(client, "");
        
        if (currentTrader != null) {
            sendMessage(client, "§aCurrent Trader: §fYES (" + traderCount + " found)");
            double distance = client.player.distanceTo(currentTrader);
            sendMessage(client, "§7Distance: §f" + String.format("%.1f", distance) + " blocks");
        } else {
            sendMessage(client, "§cCurrent Trader: §fNONE");
        }
        
        sendMessage(client, "");
        sendMessage(client, "§6Spawn Calculations:");
        sendMessage(client, "§7Next Spawn: §f" + TraderSpawnTracker.formatTicks(ticksUntilSpawn) + " (" + ticksUntilSpawn + " ticks)");
        sendMessage(client, "§7Spawn Chance: §f" + String.format("%.1f%%", spawnChance * 100));
        
        if (worldData != null) {
            sendMessage(client, "§7World Calc: §f" + TraderSpawnTracker.formatTicks(worldData.ticksUntilSpawn) + " @ " + String.format("%.1f%%", worldData.spawnChance * 100));
        }
        
        sendMessage(client, "");
        sendMessage(client, "§6Config:");
        sendMessage(client, "§7Overlay Mode: §f" + (ModConfig.isOverlayMode() ? "ON" : "OFF"));
        sendMessage(client, "§7Sound Alerts: §f" + (ModConfig.isSoundEnabled() ? "ON" : "OFF"));
        sendMessage(client, "§7Spawn Notifications: §f" + (ModConfig.isShowSpawnNotification() ? "ON" : "OFF"));
        
        // Vanilla comparison
        sendMessage(client, "");
        sendMessage(client, "§6Vanilla Expected:");
        if (totalDays < 1) {
            sendMessage(client, "§7Status: §fToo early (need 1+ days)");
        } else {
            long nextVanillaAttempt = (totalDays + 1) * 24000;
            int vanillaTicks = (int)(nextVanillaAttempt - worldTime);
            sendMessage(client, "§7Next Attempt: §f" + TraderSpawnTracker.formatTicks(vanillaTicks) + " (vanilla)");
        }
    }
    
    private static void sendMessage(Minecraft client, String message) {
        client.player.displayClientMessage(Component.literal(message), false);
    }
}