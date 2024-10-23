package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.hook.GuiGraphicsExtensions;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewClientTooltipComponent;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin implements GuiGraphicsExtensions {
  @Unique
  private int mouseX = 0;
  @Unique
  private int mouseY = 0;

  @Redirect(at = @At(value = "INVOKE", target =
      "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;"
      + "renderImage(Lnet/minecraft/client/gui/Font;IIIILnet/minecraft/client/gui/GuiGraphics;)V"), method =
      "renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;II"
      + "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Lnet/minecraft/resources/ResourceLocation;)V")
  private void renderImageWithMouse(ClientTooltipComponent component, Font font, int x, int y, int totalWidth,
      int totalHeight, GuiGraphics graphics) {
    if (component instanceof PreviewClientTooltipComponent previewComponent) {
      previewComponent.renderImageWithMouse(font, x, y, totalWidth, totalHeight, graphics, this.getMouseX(),
          this.getMouseY());
    } else {
      component.renderImage(font, x, y, totalWidth, totalHeight, graphics);
    }
  }

  @Override
  @Intrinsic
  public void setMouseX(int mouseX) {
    this.mouseX = mouseX;
  }

  @Override
  @Intrinsic
  public int getMouseX() {
    return this.mouseX;
  }

  @Override
  @Intrinsic
  public void setMouseY(int mouseY) {
    this.mouseY = mouseY;
  }

  @Override
  @Intrinsic
  public int getMouseY() {
    return this.mouseY;
  }

}
