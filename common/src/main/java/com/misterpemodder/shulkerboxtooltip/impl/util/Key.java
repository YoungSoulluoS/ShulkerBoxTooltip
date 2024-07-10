package com.misterpemodder.shulkerboxtooltip.impl.util;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public final class Key {
  public static final Key UNKNOWN_KEY = new Key(InputConstants.UNKNOWN);

  InputConstants.Key inner;

  public Key(InputConstants.Key key) {
    this.inner = key;
  }

  public InputConstants.Key get() {
    return this.inner;
  }

  public void set(InputConstants.Key key) {
    this.inner = key;
  }

  @Nullable
  public static Key defaultPreviewKey() {
    if (ShulkerBoxTooltip.isClient())
      return new Key(InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_SHIFT));
    return null;
  }

  @Nullable
  public static Key defaultFullPreviewKey() {
    if (ShulkerBoxTooltip.isClient())
      return new Key(InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_ALT));
    return null;
  }

  @Nullable
  public static Key defaultLockTooltipKey() {
    if (ShulkerBoxTooltip.isClient())
      return new Key(InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_CONTROL));
    return null;
  }

  public static Key fromTranslationKey(@Nullable String translationKey) {
    if (translationKey == null)
      return UNKNOWN_KEY;
    try {
      return new Key(InputConstants.getKey(translationKey));
    } catch (Exception e) {
      return UNKNOWN_KEY;
    }
  }
}