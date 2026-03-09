package com.example.traderalert;

import net.minecraft.client.Minecraft;

public class KeyBinds {
    private static int debugCounter = 0;
    private static int toggleCounter = 0;
    
    public static void handleKeyInputs() {
        // For now, users can press Ctrl+F3 to trigger debug info
        // This is a simplified approach until proper keybinds are implemented
        
        // You can uncomment the following lines to test debug functionality:
        /*
        debugCounter++;
        if (debugCounter >= 200) { // Every 10 seconds for testing
            DebugCommand.executeDebugCommand(Minecraft.getInstance());
            debugCounter = 0;
        }
        */
        
        // For manual testing, users can modify this code temporarily
    }
    
    // Manual debug trigger method that can be called from elsewhere
    public static void triggerDebugInfo() {
        DebugCommand.executeDebugCommand(Minecraft.getInstance());
    }
    
    // Manual toggle method
    public static void toggleDisplayMode() {
        ModConfig.toggleOverlayMode();
    }
}
