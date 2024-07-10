package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import net.minecraft.client.gui.screens.Screen;

public interface GuiGraphicsExtensions {
  void setTooltipTopYPosition(int topY);

  void setTooltipBottomYPosition(int bottomY);

  int getTooltipTopYPosition();

  int getTooltipBottomYPosition();

  void setMouseX(int mouseX);

  int getMouseX();

  void setMouseY(int mouseY);

  int getMouseY();

  Screen getScreen();

  void setZ(int z);

  int getZ();
}