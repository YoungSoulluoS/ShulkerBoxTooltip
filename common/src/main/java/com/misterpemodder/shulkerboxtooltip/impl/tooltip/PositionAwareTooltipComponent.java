package com.misterpemodder.shulkerboxtooltip.impl.tooltip;

import com.misterpemodder.shulkerboxtooltip.impl.renderer.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;

public abstract class PositionAwareTooltipComponent implements ClientTooltipComponent {
  public abstract void drawItemsWithTooltipPosition(Font font, int x, int y, GuiGraphics context, int tooltipTopY,
      int tooltipBottomY, int mouseX, int mouseY);

  public abstract void drawItems(Font font, int x, int y, GuiGraphics context);

  /**
   * Fallback in case the 1.20-like API gets bypassed.
   */
  @Override
  public final void renderImage(Font font, int x, int y, PoseStack poseStack, ItemRenderer itemRenderer) {
    GuiGraphics guiGraphics = new GuiGraphics(null);
    guiGraphics.update(poseStack, itemRenderer);
    this.drawItems(font, x, y, guiGraphics);
  }
}
