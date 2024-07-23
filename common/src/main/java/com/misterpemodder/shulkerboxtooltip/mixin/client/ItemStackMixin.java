package com.misterpemodder.shulkerboxtooltip.mixin.client;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewTooltipData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(ItemStack.class)
public class ItemStackMixin {
  @Inject(at = @At("HEAD"), method = "getTooltipImage()Ljava/util/Optional;", cancellable = true)
  private void onGetTooltipImage(CallbackInfoReturnable<Optional<TooltipComponent>> ci) {
    PreviewContext context = PreviewContext.of((ItemStack) (Object) this,
        ShulkerBoxTooltipClient.client == null ? null : ShulkerBoxTooltipClient.client.player);

    if (ShulkerBoxTooltipApi.isPreviewAvailable(context))
      ci.setReturnValue(Optional.of(
          new PreviewTooltipData(ShulkerBoxTooltipApi.getPreviewProviderForStack(context.stack()), context)));
  }

  @Inject(at = @At("RETURN"), method = "getTooltipLines"
      + "(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;")
  private void onGetTooltipLines(Player player, TooltipFlag context, CallbackInfoReturnable<List<Component>> ci) {
    ShulkerBoxTooltipClient.modifyStackTooltip((ItemStack) (Object) this, ci.getReturnValue());
  }

  @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getTagType(Ljava/lang/String;)B"), method =
      "getTooltipLines"
          + "(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;", slice = @Slice(from = @At(value = "CONSTANT", ordinal = 0, args = {
      "stringValue=Lore"})))
  private byte removeLore(CompoundTag tag, String key) {
    //noinspection ConstantConditions
    Item item = ((ItemStack) (Object) this).getItem();

    if (ShulkerBoxTooltip.config != null && ShulkerBoxTooltip.config.tooltip.hideShulkerBoxLore
        && item instanceof BlockItem blockitem && blockitem.getBlock() instanceof ShulkerBoxBlock)
      return 0;
    return tag.getTagType(key);
  }
}
