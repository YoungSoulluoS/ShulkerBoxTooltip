package com.misterpemodder.shulkerboxtooltip.impl.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.Objects;

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

  public boolean isUnbound() {
    return this.inner.equals(InputConstants.UNKNOWN);
  }

  public void set(InputConstants.Key key) {
    this.inner = key;
  }

  @Nullable
  public static Key defaultPreviewKey() {
    return new Key(InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_SHIFT));
  }

  @Nullable
  public static Key defaultFullPreviewKey() {
    return new Key(InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_ALT));
  }

  @Nullable
  public static Key defaultLockTooltipKey() {
    return new Key(InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_CONTROL));
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

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Key key = (Key) o;
    return Objects.equals(this.inner, key.inner);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(inner);
  }
}
