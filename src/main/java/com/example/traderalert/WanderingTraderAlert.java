package com.example.traderalert;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.List;

public class WanderingTraderAlert implements ClientModInitializer {
    private static int tickCounter = 0;
    private static final int DING_INTERVAL = 40; // Ding every 2 seconds (40 ticks)
    private static boolean wasTraderNearby = false;
    private static Clip dingClip;
    private static WanderingTraderAlert instance;
    
    @Override
    public void onInitializeClient() {
        instance = this;
        // Load the sound file
        loadSound();
        
        // Register keybinds (would need Fabric API KeyBinding registry)
        // For now, keybinds are handled in tick event
    }
    
    private void loadSound() {
        try {
            // Load the ding sound from resources
            InputStream audioSrc = getClass().getResourceAsStream("/assets/traderalert/sounds/ding.wav");
            if (audioSrc == null) {
                System.err.println("[WanderingTraderAlert] Could not find ding.wav sound file");
                return;
            }
            
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
            
            dingClip = AudioSystem.getClip();
            dingClip.open(audioStream);
            
            // Set volume to a reasonable level
            FloatControl gainControl = (FloatControl) dingClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-10.0f); // Reduce volume by 10 decibels
            
            audioStream.close();
            bufferedIn.close();
            
            System.out.println("[WanderingTraderAlert] Sound loaded successfully");
        } catch (Exception e) {
            System.err.println("[WanderingTraderAlert] Error loading sound: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void onClientTick(Minecraft client) {
        // Only run when in game and world is loaded
        if (client.level == null || client.player == null) {
            wasTraderNearby = false;
            tickCounter = 0;
            return;
        }
        
        // Update trader spawn tracker
        TraderSpawnTracker.tick(client);
        
        // Handle key inputs
        KeyBinds.handleKeyInputs();
        
        // Check for wandering traders within render distance
        boolean traderNearby = isWanderingTraderNearby(client);
        
        if (traderNearby) {
            // If trader just appeared, reset counter to play sound immediately
            if (!wasTraderNearby) {
                tickCounter = DING_INTERVAL;
            }
            
            tickCounter++;
            
            // Play ding sound at intervals (if enabled)
            if (tickCounter >= DING_INTERVAL && ModConfig.isSoundEnabled()) {
                playDingSound(client);
                tickCounter = 0;
            }
        } else {
            tickCounter = 0;
        }
        
        wasTraderNearby = traderNearby;
    }
    
    private static boolean isWanderingTraderNearby(Minecraft client) {
        if (client.player == null || client.level == null) {
            return false;
        }
        
        // Get render distance in blocks
        int renderDistance = client.options.renderDistance().get() * 16;
        
        // Create a bounding box around the player based on render distance
        AABB searchBox = new AABB(
            client.player.getX() - renderDistance,
            client.player.getY() - renderDistance,
            client.player.getZ() - renderDistance,
            client.player.getX() + renderDistance,
            client.player.getY() + renderDistance,
            client.player.getZ() + renderDistance
        );
        
        // Find all entities and filter for wandering traders
        List<Entity> entities = client.level.getEntities(
            (Entity) null,
            searchBox,
            entity -> entity != null && entity.getType().toString().contains("wandering_trader")
        );
        
        // Check if any traders are within actual render distance
        for (Entity trader : entities) {
            double distance = client.player.distanceTo(trader);
            if (distance <= renderDistance) {
                return true;
            }
        }
        
        return false;
    }
    
    private static void playDingSound(Minecraft client) {
        if (dingClip != null) {
            // Stop the clip if it's still playing and rewind it
            if (dingClip.isRunning()) {
                dingClip.stop();
            }
            dingClip.setFramePosition(0);
            dingClip.start();
        }
    }
}