package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PositionAwareTooltipComponent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;

import javax.annotation.Nullable;

/**
 * Polyfill for the DrawContext class of Minecraft 1.20.
 */
public final class GuiGraphics implements GuiGraphicsExtensions {
  @Nullable
  private final Screen screen;
  private PoseStack poseStack;
  private ItemRenderer itemRenderer;
  private int tooltipTopYPosition = 0;
  private int tooltipBottomYPosition = 0;
  private int mouseX = 0;
  private int mouseY = 0;

  public GuiGraphics(@Nullable Screen screen) {
    this.screen = screen;
  }

  public int getScaledWindowWidth() {
    return this.screen == null ? 0 : this.screen.width;
  }

  public int getScaledWindowHeight() {
    return this.screen == null ? 0 : this.screen.height;
  }

  public PoseStack getPoseStack() {
    return this.poseStack;
  }

  public ItemRenderer getItemRenderer() {
    return this.itemRenderer;
  }

  public void update(PoseStack poseStack, ItemRenderer itemRenderer) {
    this.poseStack = poseStack;
    this.itemRenderer = itemRenderer;
  }

  public void drawItems(ClientTooltipComponent component, Font font, int x, int y) {
    if (component instanceof PositionAwareTooltipComponent posAwareComponent) {
      //noinspection ConstantConditions
      posAwareComponent.drawItemsWithTooltipPosition(font, x, y, this, this.getTooltipTopYPosition(),
          this.getTooltipBottomYPosition(), this.getMouseX(), this.getMouseY());
    } else
      component.renderImage(font, x, y, this.poseStack, this.itemRenderer);
  }

  @Override
  public int getTooltipTopYPosition() {
    return this.tooltipTopYPosition;
  }

  @Override
  public void setTooltipTopYPosition(int tooltipTopYPosition) {
    this.tooltipTopYPosition = tooltipTopYPosition;
  }

  @Override
  public int getTooltipBottomYPosition() {
    return this.tooltipBottomYPosition;
  }

  @Override
  public void setTooltipBottomYPosition(int tooltipBottomYPosition) {
    this.tooltipBottomYPosition = tooltipBottomYPosition;
  }

  @Override
  public void setMouseX(int mouseX) {
    this.mouseX = mouseX;
  }

  @Override
  public int getMouseX() {
    return this.mouseX;
  }

  @Override
  public void setMouseY(int mouseY) {
    this.mouseY = mouseY;
  }

  @Override
  public int getMouseY() {
    return this.mouseY;
  }

  @Override
  @Nullable
  public Screen getScreen() {
    return this.screen;
  }
}
