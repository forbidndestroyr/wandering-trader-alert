package com.example.traderalert.mixin;

import com.example.traderalert.DebugCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatMixin {
    
    @Shadow protected EditBox input;
    
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void onKeyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        // Check if Enter key is pressed
        if (keyEvent.key() == 257 && this.input != null) { // GLFW_KEY_ENTER
            String message = this.input.getValue().trim();
            
            // Intercept our debug command
            if (message.equalsIgnoreCase("/traderdebug") || message.equalsIgnoreCase("/trader debug")) {
                // Execute debug command client-side
                DebugCommand.executeDebugCommand(Minecraft.getInstance());
                
                // Clear the input and close chat
                this.input.setValue("");
                Minecraft.getInstance().setScreen(null);
                
                // Cancel normal processing
                cir.setReturnValue(true);
            }
        }
    }
}
