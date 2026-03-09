package com.example.traderalert;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.storage.LevelData;

public class WorldSpawnDataReader {
    
    /**
     * Attempts to read the actual wandering trader spawn data from the world
     * Only works in singleplayer where we have access to the integrated server
     */
    public static SpawnData getActualSpawnData(Minecraft client) {
        try {
            // Always try world time calculation for both singleplayer AND multiplayer
            if (client.level != null) {
                long worldTime = client.level.getDayTime();
                return estimateSpawnDataFromWorldTime(worldTime);
            }
        } catch (Exception e) {
            // Fall back to estimation if we can't access world data
        }
        
        return null; // Use fallback tracking
    }
    
    /**
     * Estimates spawn data based on world time
     * This is a heuristic approach when we can't access actual server data
     */
    private static SpawnData estimateSpawnDataFromWorldTime(long worldTime) {
        // VANILLA MECHANICS:
        // - First spawn: After 24000 ticks (1 day) minimum
        // - Subsequent spawns: Every 24000 ticks with chance checks
        // - Spawn delay: Always 24000 ticks minimum between attempts
        
        // Handle world time properly
        worldTime = Math.max(0, worldTime);
        
        // Debug output
        System.out.println("[TraderAlert] World time: " + worldTime + " ticks");
        
        // Calculate next spawn attempt based on vanilla mechanics
        long timeSinceWorldStart = worldTime;
        
        // If less than 1 day old, wait for first spawn
        if (timeSinceWorldStart < 24000) {
            int ticksUntilFirstSpawn = (int)(24000 - timeSinceWorldStart);
            System.out.println("[TraderAlert] Before first spawn eligibility: " + ticksUntilFirstSpawn + " ticks");
            return new SpawnData(ticksUntilFirstSpawn, 0.0);
        }
        
        // After first day, spawns happen every 24000 ticks (20 minutes)
        // Find next spawn attempt time
        long daysSinceStart = timeSinceWorldStart / 24000;
        long nextSpawnAttemptTime = (daysSinceStart + 1) * 24000;
        int ticksUntilNextAttempt = (int)(nextSpawnAttemptTime - timeSinceWorldStart);
        
        // Ensure we don't get negative or zero values
        if (ticksUntilNextAttempt <= 0) {
            ticksUntilNextAttempt = 24000; // Default to full cycle
        }
        
        // Calculate spawn chance based on days elapsed
        double baseChance = 0.075; // 7.5%
        double chanceIncrease = 0.025; // 2.5% per failed attempt
        double maxChance = 0.15; // 15% max
        
        // Conservative estimate: assume some spawns succeeded
        long spawnAttempts = Math.max(0, daysSinceStart - 1);
        double spawnChance = Math.min(maxChance, baseChance + (chanceIncrease * spawnAttempts / 2));
        
        System.out.println("[TraderAlert] Days since start: " + daysSinceStart + ", Next attempt in: " + ticksUntilNextAttempt + " ticks (" + (ticksUntilNextAttempt/20) + "s), Chance: " + String.format("%.1f%%", spawnChance * 100));
        
        return new SpawnData(ticksUntilNextAttempt, spawnChance);
    }
    
    public static class SpawnData {
        public final int ticksUntilSpawn;
        public final double spawnChance;
        
        public SpawnData(int ticksUntilSpawn, double spawnChance) {
            this.ticksUntilSpawn = ticksUntilSpawn;
            this.spawnChance = spawnChance;
        }
    }
}