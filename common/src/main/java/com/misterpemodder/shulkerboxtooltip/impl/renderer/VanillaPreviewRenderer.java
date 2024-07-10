package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class VanillaPreviewRenderer extends BasePreviewRenderer {
  public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("textures/gui/container/bundle.png");
  public static final VanillaPreviewRenderer INSTANCE = new VanillaPreviewRenderer();

  VanillaPreviewRenderer() {
    super(18, 20, 2, 2);
  }

  @Override
  public int getWidth() {
    return this.getMaxRowSize() * 18;
  }

  @Override
  public int getHeight() {
    return this.getRowCount() * 20 + 3;
  }

  private int getColumnCount() {
    return Math.min(this.getMaxRowSize(), this.getInvSize());
  }

  private int getRowCount() {
    return (int) Math.ceil(((double) getInvSize()) / (double) this.getMaxRowSize());
  }

  @Override
  public void draw(int x, int y, int z, PoseStack poseStack, Font font, ItemRenderer itemRenderer,
      TextureManager textureManager, @Nullable Screen screen, int mouseX, int mouseY) {
    ++y;
    setTexture();
    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    RenderSystem.enableDepthTest();
    this.drawBackground(x, y, z, this.getColumnCount(), this.getRowCount(), poseStack);
    this.drawItems(x, y, z, poseStack, font, itemRenderer);
    if (screen != null)
      this.drawInnerTooltip(x, y, z, poseStack, screen, mouseX, mouseY);
  }

  private void drawBackground(int x, int y, int z, int columns, int rows, PoseStack poseStack) {
    for (int row = 0; row < rows; ++row) {
      for (int col = 0; col < columns; ++col) {
        this.drawSprite(poseStack, 1 + x + 18 * col, 1 + y + 20 * row, z, ClientBundleTooltip.Texture.SLOT);
      }
    }
    this.drawSprite(poseStack, x, y, z, ClientBundleTooltip.Texture.BORDER_CORNER_TOP);
    this.drawSprite(poseStack, x + columns * 18 + 1, y, z, ClientBundleTooltip.Texture.BORDER_CORNER_TOP);
    for (int col = 0; col < columns; ++col) {
      this.drawSprite(poseStack, x + 1 + col * 18, y, z, ClientBundleTooltip.Texture.BORDER_HORIZONTAL_TOP);
      this.drawSprite(poseStack, x + 1 + col * 18, y + rows * 20, z,
          ClientBundleTooltip.Texture.BORDER_HORIZONTAL_BOTTOM);
    }
    for (int row = 0; row < rows; ++row) {
      this.drawSprite(poseStack, x, y + row * 20 + 1, z, ClientBundleTooltip.Texture.BORDER_VERTICAL);
      this.drawSprite(poseStack, x + columns * 18 + 1, y + row * 20 + 1, z,
          ClientBundleTooltip.Texture.BORDER_VERTICAL);
    }
    this.drawSprite(poseStack, x, y + rows * 20, z, ClientBundleTooltip.Texture.BORDER_CORNER_BOTTOM);
    this.drawSprite(poseStack, x + columns * 18 + 1, y + rows * 20, z,
        ClientBundleTooltip.Texture.BORDER_CORNER_BOTTOM);
  }

  private void setTexture() {
    if (this.textureOverride == null)
      RenderSystem.setShaderTexture(0, DEFAULT_TEXTURE);
    else
      RenderSystem.setShaderTexture(0, this.textureOverride);
  }

  private void drawSprite(PoseStack poseStack, int x, int y, int z, ClientBundleTooltip.Texture texture) {
    GuiComponent.blit(poseStack, x, y, z, texture.x, texture.y, texture.w, texture.h, 128, 128);
  }
}
