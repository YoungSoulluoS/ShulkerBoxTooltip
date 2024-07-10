package com.misterpemodder.shulkerboxtooltip.api.renderer;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.ModPreviewRenderer;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.VanillaPreviewRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Renders a preview using a {@link PreviewProvider}.
 *
 * @since 1.3.0
 */
@Environment(EnvType.CLIENT)
public interface PreviewRenderer {
  /**
   * Returns the default renderer instance, corresponds the return value of either {@link #getModRendererInstance()},
   * or {@link #getVanillaRendererInstance()} depending on the mod's configuration.
   *
   * @return The instance of the default preview renderer.
   * @since 1.3.0
   */
  @Nonnull
  static PreviewRenderer getDefaultRendererInstance() {
    return ShulkerBoxTooltip.config.preview.theme == Configuration.Theme.VANILLA ?
        getVanillaRendererInstance() :
        getModRendererInstance();
  }

  /**
   * Returns the instance of ShulkerBoxTooltip's default preview renderer.
   *
   * @return The mod's default preview renderer.
   * @since 3.0.0
   */
  @Nonnull
  static PreviewRenderer getModRendererInstance() {
    return ModPreviewRenderer.INSTANCE;
  }

  /**
   * Returns an instance of ShulkerBoxTooltip's vanilla-style preview renderer.
   *
   * @return The mod's default preview renderer.
   * @since 3.0.0
   */
  @Nonnull
  static PreviewRenderer getVanillaRendererInstance() {
    return VanillaPreviewRenderer.INSTANCE;
  }

  /**
   * Gets the pixel height of the preview window.
   *
   * @return the height (in pixels) of the preview window.
   * @since 1.3.0
   */
  int getHeight();

  /**
   * Gets the pixel width of the preview window.
   *
   * @return the width (in pixels) of the preview window.
   * @since 1.3.0
   */
  int getWidth();

  /**
   * Sets the preview to use for the given context.
   *
   * @param context  The preview context.
   * @param provider The provider.
   * @since 2.0.0
   */
  void setPreview(PreviewContext context, PreviewProvider provider);

  /**
   * Sets the preview type.
   *
   * @param type The preview type.
   * @since 1.3.0
   */
  void setPreviewType(PreviewType type);

  /**
   * Renders the preview at the given coordinates.
   *
   * @param x              X position of the preview's upper-right corner.
   * @param y              Y position of the preview's upper-right corner.
   * @param z              The depth of the preview.
   * @param poseStack      The transformation matrices.
   * @param font           The text renderer.
   * @param itemRenderer   The item renderer.
   * @param textureManager The texture manager.
   * @since 3.0.0
   * @deprecated Will be removed in version 4.0.0.
   * Use {@link #draw(int, int, int, PoseStack, Font, ItemRenderer, TextureManager, Screen, int, int)} instead.
   */
  @Deprecated(forRemoval = true, since = "3.4.0")
  default void draw(int x, int y, int z, PoseStack poseStack, Font font, ItemRenderer itemRenderer,
      TextureManager textureManager) {
    this.draw(x, y, z, poseStack, font, itemRenderer, textureManager, null, 0, 0);
  }

  /**
   * Renders the preview at the given coordinates.
   * <p>
   * <b>Note:</b> Marked as experimental to keep API-compatibility between the 1.18.x, 1.19.x, 1.20.x branches.
   * Because of the ever-changing Minecraft rendering code, we cannot guarantee stability between versions.
   * Notably, the {@code z} parameter is removed in later versions.
   *
   * @param x              X position of the preview's upper-right corner.
   * @param y              Y position of the preview's upper-right corner.
   * @param z              The depth of the preview.
   * @param poseStack      The transformation matrices.
   * @param itemRenderer   The item renderer.
   * @param font           The text renderer.
   * @param textureManager The texture manager.
   * @param screen         The current screen this tooltip is a part of.
   * @param mouseX         The mouse X position.
   * @param mouseY         The mouse Y position.
   * @since 3.4.0 (1.18.x branch only)
   */
  @ApiStatus.Experimental
  default void draw(int x, int y, int z, PoseStack poseStack, Font font, ItemRenderer itemRenderer,
      TextureManager textureManager, @Nullable Screen screen, int mouseX, int mouseY) {
    this.draw(x, y, z, poseStack, font, itemRenderer, textureManager);
  }

}
