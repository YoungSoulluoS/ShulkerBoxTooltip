package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
  @Inject(at = @At("HEAD"), method = "keyPress(JIIII)V")
  private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
    // update the pressed preview keys when key event was received on the game window
    if (window == Minecraft.getInstance().getWindow().getWindow())
      ShulkerBoxTooltipClient.updatePreviewKeys();
  }
}
