package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.GuiGraphics;
import com.misterpemodder.shulkerboxtooltip.impl.renderer.GuiGraphicsAccess;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {


  @Shadow
  @Nullable
  protected Slot hoveredSlot;

  @Final
  @Shadow
  protected AbstractContainerMenu menu;

  @Unique
  @Nullable
  private Slot mouseLockSlot = null;
  @Unique
  private int mouseLockX = 0;
  @Unique
  private int mouseLockY = 0;

  @Inject(at = @At("HEAD"), method = "isHovering(Lnet/minecraft/world/inventory/Slot;DD)Z", cancellable = true)
  private void forceFocusSlot(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> cir) {
    if (this.mouseLockSlot != null) {
      // Handling the case where the hovered item stack get swapped for air while the tooltip is locked
      // When this happens, the lockTooltipPosition() hook will not be called (there is no tooltip for air),
      // so we need to perform cleanup logic here.
      //
      // We also need to check if the slot is still part of the menu,
      // as it may have been removed (this is the case when switching tabs in the creative inventory)

      if (this.mouseLockSlot.hasItem() && this.menu.slots.contains(this.mouseLockSlot))
        cir.setReturnValue(slot == this.mouseLockSlot && this.menu.getCarried().isEmpty());
      else
        // reset the lock if the stack is no longer present
        this.mouseLockSlot = null;
    }
  }

  /**
   * Makes the current mouse position available via extensions to the DrawContext.
   */
  @Inject(at = @At("HEAD"), method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V")
  private void captureMousePosition(PoseStack poseStack, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    GuiGraphics graphics = ((GuiGraphicsAccess) this).getGuiGraphics();
    graphics.setMouseY(mouseY);
    graphics.setMouseX(mouseX);
  }

  @Inject(at = @At("HEAD"), method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;II)V")
  private void enableLockKeyHints(CallbackInfo ci) {
    ShulkerBoxTooltipClient.setLockKeyHintsEnabled(true);
  }

  @Inject(at = @At("RETURN"), method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;II)V")
  private void disableLockKeyHints(CallbackInfo ci) {
    ShulkerBoxTooltipClient.setLockKeyHintsEnabled(false);
  }

  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;"
      + "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V"), method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;II)V")
  private void lockTooltipPosition(AbstractContainerScreen<?> instance, PoseStack poseStack, ItemStack itemStack, int x,
      int y) {
    Slot mouseLockSlot = this.mouseLockSlot;

    if (ShulkerBoxTooltipClient.isLockPreviewKeyPressed()) {
      if (mouseLockSlot == null) {
        // when locking is requested and no slot is currently locked.
        mouseLockSlot = this.hoveredSlot;
        this.mouseLockX = x;
        this.mouseLockY = y;
      }
    } else {
      mouseLockSlot = null;
    }

    if (mouseLockSlot != null) {
      ItemStack stack = mouseLockSlot.getItem();

      PreviewContext context = PreviewContext.of(stack,
          ShulkerBoxTooltipClient.client == null ? null : ShulkerBoxTooltipClient.client.player);

      // Check if the locked slot contains an item that is actively being previewed,
      // if not we reset the lock, so that pressing "Control" doesn't randomly lock slots for non-previewable items.
      if (ShulkerBoxTooltipApi.isPreviewAvailable(context)) {
        // override the tooltip that would be displayed with that of the locked slot item
        x = this.mouseLockX;
        y = this.mouseLockY;
      } else {
        mouseLockSlot = null;
      }
    }
    this.mouseLockSlot = mouseLockSlot;
    instance.renderTooltip(poseStack, itemStack, x, y);
  }
}
