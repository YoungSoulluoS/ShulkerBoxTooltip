package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

@Environment(EnvType.CLIENT)
public class VanillaPreviewRenderer extends BasePreviewRenderer {
  public static final VanillaPreviewRenderer INSTANCE = new VanillaPreviewRenderer();

  VanillaPreviewRenderer() {
    super(18, 20, 2, 2);
  }

  @Override
  public int getWidth() {
    return this.getMaxRowSize() * 18;
  }

  public int getHeight() {
    return this.getRowCount() * 20 + 3;
  }

  private int getRowCount() {
    return (int) Math.ceil(((double) getInvSize()) / (double) this.getMaxRowSize());
  }

  @Override
  public void draw(int x, int y, GuiGraphics graphics, Font font, int mouseX, int mouseY) {
    // TODO: implement 1.21.2 bundle rendering
  }
}
