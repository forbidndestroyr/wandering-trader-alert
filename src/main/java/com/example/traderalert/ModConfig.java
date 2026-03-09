package com.example.traderalert;

public class ModConfig {
    private static boolean overlayMode = true; // false = F3, true = overlay
    private static boolean showSpawnNotification = true;
    private static boolean soundEnabled = true;
    
    public static boolean isOverlayMode() {
        return overlayMode;
    }
    
    public static void toggleOverlayMode() {
        overlayMode = !overlayMode;
    }
    
    public static boolean isShowSpawnNotification() {
        return showSpawnNotification;
    }
    
    public static void toggleSpawnNotification() {
        showSpawnNotification = !showSpawnNotification;
    }
    
    public static boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    public static void toggleSound() {
        soundEnabled = !soundEnabled;
    }
}