package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class ModPreviewRenderer extends BasePreviewRenderer {
  private static final ResourceLocation DEFAULT_TEXTURE_LIGHT = new ResourceLocation("shulkerboxtooltip",
      "textures/gui/shulker_box_tooltip.png");
  public static final ModPreviewRenderer INSTANCE = new ModPreviewRenderer();

  ModPreviewRenderer() {
    super(18, 18, 8, 8);
  }

  @Override
  public int getWidth() {
    return 12 + Math.min(this.getMaxRowSize(), this.getInvSize()) * 18;
  }

  @Override
  public int getHeight() {
    return 14 + (int) Math.ceil(this.getInvSize() / (double) this.getMaxRowSize()) * 18;
  }

  /**
   * Sets the color of the preview window.
   */
  private void setColor() {
    ColorKey key;

    if (this.config.useColors()) {
      key = this.provider.getWindowColorKey(this.previewContext);
    } else {
      key = ColorKey.DEFAULT;
    }
    float[] color = key.rgbComponents();
    RenderSystem.setShaderColor(color[0], color[1], color[2], 1.0f);
  }

  /**
   * Sets the texture to be used.
   */
  private void setTexture() {
    ResourceLocation texture = this.textureOverride;

    if (texture == null) {
      texture = DEFAULT_TEXTURE_LIGHT;
    }
    RenderSystem.setShaderTexture(0, texture);
  }

  private void drawBackground(int x, int y, PoseStack poseStack) {
    int invSize = this.getInvSize();
    int xOffset = 7;
    int yOffset = 7;
    int rowTexYPos = 7;
    int rowSize = Math.min(this.getMaxRowSize(), invSize);
    int rowWidth = rowSize * 18;

    this.setColor();
    this.setTexture();

    // top side
    for (int size = rowSize; size > 0; size -= 9) {
      int s = Math.min(size, 9);

      GuiComponent.blit(poseStack, x + xOffset, y, 0, 7, 0, s * 18, 7, 256, 256);
      xOffset += s * 18;
    }

    while (invSize > 0) {
      xOffset = 7;
      // left side
      GuiComponent.blit(poseStack, x, y + yOffset, 0, 0, rowTexYPos, 7, 18, 256, 256);
      for (int rSize = rowSize; rSize > 0; rSize -= 9) {
        int s = Math.min(rSize, 9);

        // center
        GuiComponent.blit(poseStack, x + xOffset, y + yOffset, 0, 7, rowTexYPos, s * 18, 18, 256, 256);
        xOffset += s * 18;
      }
      // right side
      GuiComponent.blit(poseStack, x + xOffset, y + yOffset, 0, 169, rowTexYPos, 7, 18, 256, 256);
      yOffset += 18;
      invSize -= rowSize;
      rowTexYPos = rowTexYPos >= 43 ? 7 : rowTexYPos + 18;
    }

    xOffset = 7;
    for (int size = rowSize; size > 0; size -= 9) {
      int s = Math.min(size, 9);

      // bottom side
      GuiComponent.blit(poseStack, x + xOffset, y + yOffset, 0, 7, 61, s * 18, 7, 256, 256);
      xOffset += s * 18;
    }

    // top-left corner
    GuiComponent.blit(poseStack, x, y, 0, 0, 0, 7, 7, 256, 256);
    // top-right corner
    GuiComponent.blit(poseStack, x + rowWidth + 7, y, 0, 169, 0, 7, 7, 256, 256);
    // bottom-right corner
    GuiComponent.blit(poseStack, x + rowWidth + 7, y + yOffset, 0, 169, 61, 7, 7, 256, 256);
    // bottom-left corner
    GuiComponent.blit(poseStack, x, y + yOffset, 0, 0, 61, 7, 7, 256, 256);

    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
  }

  @Override
  public void draw(int x, int y, PoseStack poseStack, Font font, ItemRenderer itemRenderer,
      TextureManager textureManager, Screen screen, int mouseX, int mouseY) {
    if (this.items.isEmpty() || this.previewType == PreviewType.NO_PREVIEW)
      return;
    RenderSystem.enableDepthTest();
    this.drawBackground(x, y, poseStack);
    this.drawItems(x, y, poseStack, font, itemRenderer);
    this.drawSlotHighlight(x, y, poseStack, mouseX, mouseY);
    this.drawInnerTooltip(x, y, poseStack, screen, mouseX, mouseY);
  }
}
