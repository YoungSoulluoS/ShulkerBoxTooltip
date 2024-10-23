package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import static com.misterpemodder.shulkerboxtooltip.impl.util.ShulkerBoxTooltipUtil.id;

@Environment(EnvType.CLIENT)
public class ModPreviewRenderer extends BasePreviewRenderer {
  private static final ResourceLocation DEFAULT_TEXTURE_LIGHT = id("shulker_box_tooltip");
  public static final ModPreviewRenderer INSTANCE = new ModPreviewRenderer();

  ModPreviewRenderer() {
    super(18, 18, 8, 8);
  }

  @Override
  public int getWidth() {
    return 14 + Math.min(this.getMaxRowSize(), this.getInvSize()) * 18;
  }

  @Override
  public int getHeight() {
    return 14 + (int) Math.ceil(this.getInvSize() / (double) this.getMaxRowSize()) * 18;
  }

  /**
   * Sets the color of the preview window.
   */
  private int getColor() {
    ColorKey key;

    if (this.config.useColors()) {
      key = this.provider.getWindowColorKey(this.previewContext);
    } else {
      key = ColorKey.DEFAULT;
    }
    return 0xFF000000 | key.rgb();
  }

  private ResourceLocation getTexture() {
    if (this.textureOverride != null)
      return this.textureOverride;
    return DEFAULT_TEXTURE_LIGHT;
  }

  private void drawBackground(int x, int y, GuiGraphics graphics) {
    int invSize = this.getInvSize();
    int slotSize = 18;
    int rows = Math.min(this.getMaxRowSize(), invSize);
    int cols = (int) Math.ceil(invSize / (double) rows);

    graphics.blitSprite(RenderType::guiTexturedOverlay, this.getTexture(), x, y, 14 + rows * slotSize,
        14 + cols * slotSize, this.getColor());
  }

  @Override
  public void draw(int x, int y, GuiGraphics graphics, Font font, int mouseX, int mouseY) {
    if (this.items.isEmpty() || this.previewType == PreviewType.NO_PREVIEW)
      return;
    RenderSystem.enableDepthTest();
    this.drawBackground(x, y, graphics);
    this.drawSlotHighlight(x, y, graphics, mouseX, mouseY, () -> this.drawItems(x, y, graphics, font));
    this.drawInnerTooltip(x, y, graphics, font, mouseX, mouseY);
  }
}
