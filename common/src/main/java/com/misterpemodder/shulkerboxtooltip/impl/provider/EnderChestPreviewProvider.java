package com.misterpemodder.shulkerboxtooltip.impl.provider;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.provider.BlockEntityPreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.EnderChestSyncType;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.C2SEnderChestUpdateRequest;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.C2SMessages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnderChestPreviewProvider implements PreviewProvider {
  @Override
  public List<ItemStack> getInventory(PreviewContext context) {
    Player owner = context.owner();

    if (owner == null)
      return Collections.emptyList();

    PlayerEnderChestContainer inventory = owner.getEnderChestInventory();
    int size = inventory.getContainerSize();
    List<ItemStack> items = NonNullList.withSize(size, ItemStack.EMPTY);

    for (int i = 0; i < size; ++i)
      items.set(i, inventory.getItem(i).copy());
    return items;
  }

  @Override
  public int getInventoryMaxSize(PreviewContext context) {
    Player owner = context.owner();

    return owner == null ? 0 : owner.getEnderChestInventory().getContainerSize();
  }

  @Override
  public boolean shouldDisplay(PreviewContext context) {
    Player owner = context.owner();

    if (owner == null)
      return false;
    return ShulkerBoxTooltip.config.preview.serverIntegration && ShulkerBoxTooltip.config.server.clientIntegration
        && ShulkerBoxTooltip.config.server.enderChestSyncType != EnderChestSyncType.NONE
        && !owner.getEnderChestInventory().isEmpty();
  }

  @Override
  @Environment(EnvType.CLIENT)
  public ColorKey getWindowColorKey(PreviewContext context) {
    return ColorKey.ENDER_CHEST;
  }

  @Override
  public void onInventoryAccessStart(PreviewContext context) {
    if (ShulkerBoxTooltip.config.server.enderChestSyncType == EnderChestSyncType.PASSIVE
        // this method may be called when not in a world, so we need to check if we can send packets
        && Minecraft.getInstance().getConnection() != null)
      C2SMessages.ENDER_CHEST_UPDATE_REQUEST.sendToServer(new C2SEnderChestUpdateRequest());
  }

  @Override
  public boolean showTooltipHints(PreviewContext context) {
    return ShulkerBoxTooltip.config.preview.serverIntegration && ShulkerBoxTooltip.config.server.clientIntegration
        && ShulkerBoxTooltip.config.server.enderChestSyncType != EnderChestSyncType.NONE;
  }

  @Override
  public List<Component> addTooltip(PreviewContext context) {
    if (ShulkerBoxTooltipApi.getCurrentPreviewType(this.isFullPreviewAvailable(context)) == PreviewType.FULL)
      return Collections.emptyList();
    return BlockEntityPreviewProvider.getItemCountTooltip(new ArrayList<>(), this.getInventory(context));
  }
}
