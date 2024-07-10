package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.impl.renderer.GuiGraphics;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.GuiGraphicsAccess;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.GuiGraphicsExtensions;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Screen.class)
@SuppressWarnings("SpellCheckingInspection")
public class ScreenMixin implements GuiGraphicsAccess {
  @Unique
  private final GuiGraphics guiGraphics = new GuiGraphics((Screen) (Object) this);

  @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;"
      + "fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V", ordinal = 2), method = "renderTooltipInternal(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V", index = 2)
  private int updateTooltipLeftAndBottomPos(Matrix4f matrix, BufferBuilder builder, int x1, int y1, int x2, int y2,
      int zOffset, int color1, int color2) {
    GuiGraphicsExtensions posAccess = this.guiGraphics;
    posAccess.setTooltipTopYPosition(y1);
    posAccess.setTooltipBottomYPosition(y2);
    return x1;
  }

  @Redirect(at = @At(value = "INVOKE", target =
      "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;"
          + "renderImage(Lnet/minecraft/client/gui/Font;IILcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/entity/ItemRenderer;I)V"), method = "renderTooltipInternal(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/util/List;II)V")
  private void drawPosAwareComponent(ClientTooltipComponent component, Font font, int x, int y, PoseStack poseStack,
      ItemRenderer itemRenderer, int z) {
    this.guiGraphics.update(poseStack, itemRenderer);
    this.guiGraphics.setZ(z);
    this.guiGraphics.renderTooltip(component, font, x, y, z);
  }

  @Override
  public GuiGraphics getGuiGraphics() {
    return this.guiGraphics;
  }
}
