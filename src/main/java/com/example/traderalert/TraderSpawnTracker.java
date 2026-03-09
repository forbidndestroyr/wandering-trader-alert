package com.example.traderalert;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.WorldData;

public class TraderSpawnTracker {
    private static long lastTraderSpawnTime = -1;
    private static long worldStartTime = -1;
    private static boolean traderJustSpawned = false;
    private static long spawnNotificationTime = 0;
    private static Entity lastKnownTrader = null;
    private static int traderCount = 0;
    private static ClientLevel lastLevel = null;
    
    // Wandering trader spawn mechanics (vanilla values)
    private static final int MIN_SPAWN_DELAY = 24000; // 1 day (20 minutes)
    private static final int SPAWN_CHANCE_DELAY = 24000; // Check every day after min delay
    private static final double BASE_SPAWN_CHANCE = 0.075; // 7.5% base chance
    private static final double CHANCE_INCREMENT = 0.025; // 2.5% increase per failed attempt
    private static final double MAX_SPAWN_CHANCE = 0.075 + (0.025 * 3); // Cap at 15%
    
    public static void tick(Minecraft client) {
        if (client.level == null) {
            worldStartTime = -1;
            return;
        }

        if (lastLevel == null || lastLevel != client.level)
        {
            lastLevel = client.level;
            resetCount();
        }
        
        Level world = client.level;
        long currentTime = world.getDayTime();
        
        // Initialize world start time
        if (worldStartTime == -1) {
            worldStartTime = currentTime;
            lastTraderSpawnTime = currentTime;
        }
        
        // Check for new trader spawn
        checkForNewTrader(client);
        
        // Update spawn notification timer
        if (traderJustSpawned && System.currentTimeMillis() - spawnNotificationTime > 3000) {
            traderJustSpawned = false;
        }
    }
    
    private static void checkForNewTrader(Minecraft client) {
        if (client.level == null) return;
        
        // Find wandering trader
        Entity currentTrader = null;
        for (Entity entity : client.level.entitiesForRendering()) {
            if (entity.getType() == EntityType.WANDERING_TRADER) {
                currentTrader = entity;
                break;
            }
        }
        
        // Check if a new trader spawned
        if (currentTrader != null && lastKnownTrader != currentTrader) {
            lastKnownTrader = currentTrader;
            lastTraderSpawnTime = client.level.getDayTime();
            traderJustSpawned = true;
            spawnNotificationTime = System.currentTimeMillis();
            traderCount++;
            
            // IMPORTANT: Reset spawn tracking when trader actually spawns
            // This ensures spawn chance resets to base value
            worldStartTime = client.level.getDayTime(); // Reset reference point
            System.out.println("[TraderAlert] NEW TRADER SPAWNED! Resetting spawn chance tracking.");
            
        } else if (currentTrader == null) {
            lastKnownTrader = null;
        }
    }
    
    public static int getTicksUntilSpawn(Minecraft client) {
        if (client.level == null) {
            return MIN_SPAWN_DELAY;
        }
        
        // Try to get actual spawn data from world
        int actualTicksRemaining = getActualSpawnTimer(client);
        if (actualTicksRemaining >= 0) {
            return actualTicksRemaining;
        }
        
        // Fallback to our tracking
        if (lastTraderSpawnTime == -1) {
            lastTraderSpawnTime = client.level.getDayTime();
            return MIN_SPAWN_DELAY;
        }
        
        long currentTime = client.level.getDayTime();
        long timeSinceLastSpawn = currentTime - lastTraderSpawnTime;
        
        // If a trader exists, next spawn is after it despawns (typically 2-3 days)
        if (lastKnownTrader != null) {
            return MIN_SPAWN_DELAY * 2; // Approximate
        }
        
        // Calculate time until next spawn attempt
        if (timeSinceLastSpawn < MIN_SPAWN_DELAY) {
            return (int)(MIN_SPAWN_DELAY - timeSinceLastSpawn);
        }
        
        // After min delay, spawn attempts happen daily
        long daysSinceEligible = (timeSinceLastSpawn - MIN_SPAWN_DELAY) / SPAWN_CHANCE_DELAY;
        long ticksIntoCurrentDay = (timeSinceLastSpawn - MIN_SPAWN_DELAY) % SPAWN_CHANCE_DELAY;
        
        return (int)(SPAWN_CHANCE_DELAY - ticksIntoCurrentDay);
    }
    
    public static double getSpawnChance(Minecraft client) {
        if (client.level == null) {
            return BASE_SPAWN_CHANCE;
        }
        
        // Try to get actual spawn chance from world
        double actualChance = getActualSpawnChance(client);
        if (actualChance >= 0) {
            return actualChance;
        }
        
        // Fallback to our calculation
        if (lastTraderSpawnTime == -1) {
            lastTraderSpawnTime = client.level.getDayTime();
            return BASE_SPAWN_CHANCE;
        }
        
        // If trader exists, chance is 0
        if (lastKnownTrader != null) {
            return 0.0;
        }
        
        long currentTime = client.level.getDayTime();
        long timeSinceLastSpawn = currentTime - lastTraderSpawnTime;
        
        if (timeSinceLastSpawn < MIN_SPAWN_DELAY) {
            return 0.0; // Not eligible yet
        }
        
        // Calculate current chance based on failed attempts
        long failedAttempts = (timeSinceLastSpawn - MIN_SPAWN_DELAY) / SPAWN_CHANCE_DELAY;
        double chance = BASE_SPAWN_CHANCE + (CHANCE_INCREMENT * failedAttempts);
        
        return Math.min(chance, MAX_SPAWN_CHANCE);
    }
    
    public static boolean shouldShowSpawnNotification() {
        return traderJustSpawned && ModConfig.isShowSpawnNotification();
    }
    
    // Try to access the actual world spawn timer
    private static int getActualSpawnTimer(Minecraft client) {
        try {
            WorldSpawnDataReader.SpawnData data = WorldSpawnDataReader.getActualSpawnData(client);
            if (data != null) {
                return data.ticksUntilSpawn;
            }
        } catch (Exception e) {
            // Fall back on error
        }
        return -1; // Use fallback
    }
    
    // Try to access the actual world spawn chance
    private static double getActualSpawnChance(Minecraft client) {
        try {
            WorldSpawnDataReader.SpawnData data = WorldSpawnDataReader.getActualSpawnData(client);
            if (data != null) {
                return data.spawnChance;
            }
        } catch (Exception e) {
            // Fall back on error
        }
        return -1; // Use fallback
    }
    
    // Enhanced tracking that persists world spawn state
    public static void onWorldJoin(Minecraft client) {
        if (client.level == null) return;
        
        // Reset our tracking when joining a world
        lastTraderSpawnTime = -1;
        worldStartTime = -1;
        lastKnownTrader = null;
        traderJustSpawned = false;
        traderCount = 0;
        System.out.println("RESET COUNT!");
        
        // Try to detect existing traders to get a better baseline
        checkForExistingTraders(client);
    }
    
    private static void checkForExistingTraders(Minecraft client) {
        if (client.level == null) return;
        
        // Look for existing wandering traders
        for (Entity entity : client.level.entitiesForRendering()) {
            if (entity.getType() == EntityType.WANDERING_TRADER) {
                lastKnownTrader = entity;
                // If trader exists, assume recent spawn (conservative estimate)
                lastTraderSpawnTime = client.level.getDayTime() - 1000; // 1000 ticks ago
                break;
            }
        }
        
        // If no trader found, use current time as baseline
        if (lastKnownTrader == null) {
            lastTraderSpawnTime = client.level.getDayTime();
        }
    }
    
    public static String formatTicks(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        int days = hours / 24;
        
        if (days > 0) {
            return String.format("%dd %dh %dm", days, hours % 24, minutes % 60);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else {
            return String.format("%ds", seconds);
        }
    }

    public static int getCount()
    {
        return traderCount;
    }

    public static void resetCount()
    {
        traderCount = 0;
    }
}
