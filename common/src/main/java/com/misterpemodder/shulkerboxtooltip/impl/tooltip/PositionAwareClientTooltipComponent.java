package com.misterpemodder.shulkerboxtooltip.impl.tooltip;

import com.misterpemodder.shulkerboxtooltip.impl.renderer.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;

public abstract class PositionAwareClientTooltipComponent implements ClientTooltipComponent {
  public abstract void renderImageWithTooltipPosition(Font font, int x, int y, GuiGraphics graphics, int tooltipTopY,
      int tooltipBottomY, int mouseX, int mouseY);

  public abstract void renderImage(Font font, int x, int y, GuiGraphics graphics);

  /**
   * Fallback in case the 1.20-like API gets bypassed.
   */
  @Override
  public final void renderImage(Font font, int x, int y, PoseStack poseStack, ItemRenderer itemRenderer, int z) {
    GuiGraphics drawContext = new GuiGraphics(null);
    drawContext.update(poseStack, itemRenderer);
    drawContext.setZ(z);
    this.renderImage(font, x, y, drawContext);
  }
}
