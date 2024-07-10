package com.misterpemodder.shulkerboxtooltip;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApiStatus.Internal
@Environment(EnvType.CLIENT)
public class ShulkerBoxTooltipClient {
  private static ItemStack previousStack = null;
  public static Minecraft client;
  private static boolean wasPreviewAccessed = false;

  private static boolean previewKeyPressed = false;
  private static boolean fullPreviewKeyPressed = false;
  private static boolean lockPreviewKeyPressed = false;

  private static boolean lockKeyHintsEnabled = false;

  public static void init() {
    client = Minecraft.getInstance();
    ClientNetworking.init();
  }

  private static boolean isPreviewRequested() {
    return ShulkerBoxTooltip.config.preview.alwaysOn || ShulkerBoxTooltipClient.isPreviewKeyPressed();
  }

  private static List<Component> getTooltipHints(PreviewContext context, PreviewProvider provider) {
    if (!ShulkerBoxTooltip.config.preview.enable || !provider.shouldDisplay(context))
      return Collections.emptyList();

    boolean previewRequested = isPreviewRequested();
    List<Component> hints = new ArrayList<>();
    Component previewKeyHint = getPreviewKeyTooltipHint(context, provider, previewRequested);
    Component lockKeyHint = getLockKeyTooltipHint(context, provider, previewRequested);

    if (previewKeyHint != null)
      hints.add(previewKeyHint);
    if (lockKeyHint != null)
      hints.add(lockKeyHint);
    return hints;
  }

  @Nullable
  private static Component getPreviewKeyTooltipHint(PreviewContext context, PreviewProvider provider,
      boolean previewRequested) {
    if (previewRequested && ShulkerBoxTooltipClient.isFullPreviewKeyPressed())
      return null; // full preview is enabled, no need to display the preview key hint.

    // At this point, SHIFT may be pressed but not ALT.
    boolean fullPreviewAvailable = provider.isFullPreviewAvailable(context);

    if (!fullPreviewAvailable && previewRequested)
      return null;

    MutableComponent previewKeyHint = new TextComponent("");
    Component previewKeyText = ShulkerBoxTooltip.config.controls.previewKey.get().getDisplayName();

    if (previewRequested) {
      previewKeyHint.append(ShulkerBoxTooltip.config.controls.fullPreviewKey.get().getDisplayName());
      if (!ShulkerBoxTooltip.config.preview.alwaysOn) {
        previewKeyHint.append("+").append(previewKeyText);
      }
    } else {
      previewKeyHint.append(previewKeyText);
    }
    previewKeyHint.append(": ");
    previewKeyHint.withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));

    String contentHint;

    if (ShulkerBoxTooltipApi.getCurrentPreviewType(fullPreviewAvailable) == PreviewType.NO_PREVIEW)
      contentHint = ShulkerBoxTooltip.config.preview.swapModes ?
          provider.getFullTooltipHintLangKey(context) :
          provider.getTooltipHintLangKey(context);
    else
      contentHint = ShulkerBoxTooltip.config.preview.swapModes ?
          provider.getTooltipHintLangKey(context) :
          provider.getFullTooltipHintLangKey(context);
    return previewKeyHint.append(
        new TranslatableComponent(contentHint).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));
  }

  @Nullable
  private static Component getLockKeyTooltipHint(PreviewContext context, PreviewProvider provider,
      boolean previewRequested) {
    if (!previewRequested || ShulkerBoxTooltipClient.isLockPreviewKeyPressed() || !lockKeyHintsEnabled)
      return null;
    MutableComponent lockKeyHint = new TextComponent("");
    String lockKeyHintLangKey = provider.getLockKeyTooltipHintLangKey(context);

    lockKeyHint.append(ShulkerBoxTooltip.config.controls.lockTooltipKey.get().getDisplayName());
    lockKeyHint.append(": ");
    lockKeyHint.withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
    lockKeyHint.append(
        new TranslatableComponent(lockKeyHintLangKey).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE)));
    return lockKeyHint;
  }

  public static void modifyStackTooltip(ItemStack stack, List<Component> tooltip) {
    if (client == null)
      return;

    PreviewContext context = PreviewContext.of(stack, client.player);
    PreviewProvider provider = ShulkerBoxTooltipApi.getPreviewProviderForStack(stack);

    if (provider == null)
      return;
    if (previousStack == null || !ItemStack.isSame(stack, previousStack))
      wasPreviewAccessed = false;
    previousStack = stack;

    if (!wasPreviewAccessed)
      provider.onInventoryAccessStart(context);
    wasPreviewAccessed = true;

    if (provider.showTooltipHints(context)) {
      if (ShulkerBoxTooltip.config.tooltip.type == Configuration.ShulkerBoxTooltipType.MOD)
        tooltip.addAll(provider.addTooltip(context));
      if (ShulkerBoxTooltip.config.tooltip.showKeyHints) {
        tooltip.addAll(getTooltipHints(context, provider));
      }
    }
  }

  public static boolean isPreviewAvailable(PreviewContext context) {
    if (ShulkerBoxTooltip.config.preview.enable) {
      PreviewProvider provider = ShulkerBoxTooltipApi.getPreviewProviderForStack(context.stack());

      return provider != null && provider.shouldDisplay(context) && ShulkerBoxTooltipApi.getCurrentPreviewType(
          provider.isFullPreviewAvailable(context)) != PreviewType.NO_PREVIEW;
    }
    return false;
  }

  public static PreviewType getCurrentPreviewType(boolean hasFullPreviewMode) {
    boolean previewRequested = isPreviewRequested();

    if (previewRequested && !hasFullPreviewMode) {
      return PreviewType.COMPACT;
    }
    if (ShulkerBoxTooltip.config.preview.swapModes) {
      if (previewRequested)
        return isFullPreviewKeyPressed() ? PreviewType.COMPACT : PreviewType.FULL;
    } else {
      if (previewRequested)
        return isFullPreviewKeyPressed() ? PreviewType.FULL : PreviewType.COMPACT;
    }
    return PreviewType.NO_PREVIEW;
  }

  public static boolean isPreviewKeyPressed() {
    return previewKeyPressed;
  }

  public static boolean isFullPreviewKeyPressed() {
    return fullPreviewKeyPressed;
  }

  public static boolean isLockPreviewKeyPressed() {
    return lockPreviewKeyPressed;
  }

  public static void setLockKeyHintsEnabled(boolean value) {
    lockKeyHintsEnabled = value;
  }

  private static boolean isKeyPressed(@Nullable Key key) {
    if (key == null || key.equals(Key.UNKNOWN_KEY) || key.get().equals(InputConstants.UNKNOWN))
      return false;
    return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key.get().getValue());
  }

  public static void updatePreviewKeys() {
    Configuration config = ShulkerBoxTooltip.config;

    if (config == null) {
      previewKeyPressed = false;
      fullPreviewKeyPressed = false;
      lockPreviewKeyPressed = false;
    } else {
      previewKeyPressed = isKeyPressed(config.controls.previewKey);
      fullPreviewKeyPressed = isKeyPressed(config.controls.fullPreviewKey);
      lockPreviewKeyPressed = isKeyPressed(config.controls.lockTooltipKey);
    }
  }
}