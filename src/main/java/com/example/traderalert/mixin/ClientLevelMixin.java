package com.example.traderalert.mixin;

import com.example.traderalert.TraderSpawnTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ClientLevelMixin {
    
    @Inject(method = "setLevel", at = @At("TAIL"))
    private void onWorldJoin(ClientLevel level, CallbackInfo ci) {
        if (level != null) {
            // Give the world a moment to fully load before checking for traders
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // Wait 2 seconds for world to load
                    TraderSpawnTracker.onWorldJoin(Minecraft.getInstance());
                } catch (InterruptedException e) {
                    // Ignore
                }
            }).start();
        }
    }
}