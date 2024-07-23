package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.renderer.GuiGraphics;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.GuiGraphicsAccess;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.GuiGraphicsExtensions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Screen.class)
@SuppressWarnings("SpellCheckingInspection")
public class ScreenMixin implements GuiGraphicsAccess {
  @Unique
  private final GuiGraphics guiGraphics = new GuiGraphics((Screen) (Object) this);

  @Redirect(at = @At(value = "INVOKE", target =
      "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;"
          + "positionTooltip(Lnet/minecraft/client/gui/screens/Screen;IIII)Lorg/joml/Vector2ic;", ordinal = 0), method =
      "renderTooltipInternal"
          + "(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V")
  private Vector2ic updateTooltipLeftAndBottomPos(ClientTooltipPositioner positioner, Screen screen, int startX,
      int startY, int width, int height) {
    Vector2ic tooltipPos = positioner.positionTooltip(screen, startX, startY, width, height);
    GuiGraphicsExtensions posAccess = this.guiGraphics;
    posAccess.setTooltipTopYPosition(tooltipPos.y() - 3);
    posAccess.setTooltipBottomYPosition(posAccess.getTooltipTopYPosition() + height + 6);
    return tooltipPos;
  }

  @Redirect(at = @At(value = "INVOKE", target =
      "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;"
          + "renderImage(Lnet/minecraft/client/gui/Font;IILcom/mojang/blaze3d/vertex/PoseStack;"
          + "Lnet/minecraft/client/renderer/entity/ItemRenderer;)V"), method = "renderTooltipInternal"
      + "(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V")
  private void drawPosAwareComponent(ClientTooltipComponent component, Font font, int x, int y, PoseStack poseStack,
      ItemRenderer itemRenderer) {
    this.guiGraphics.update(poseStack, itemRenderer);
    this.guiGraphics.drawItems(component, font, x, y);
  }

  @Override
  public GuiGraphics getGuiGraphics() {
    return this.guiGraphics;
  }
}
