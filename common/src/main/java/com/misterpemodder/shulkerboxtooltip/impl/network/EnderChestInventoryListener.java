package com.misterpemodder.shulkerboxtooltip.impl.network;

import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CEnderChestUpdate;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.jetbrains.annotations.NotNull;

public final class EnderChestInventoryListener implements ContainerListener {

  private final ServerPlayer player;

  private EnderChestInventoryListener(ServerPlayer player) {
    this.player = player;
  }

  @Override
  public void containerChanged(@NotNull Container inv) {
    if (!ShulkerBoxTooltipApi.hasModAvailable(this.player)) {
      detachFrom(this.player);
      return;
    }
    S2CMessages.ENDER_CHEST_UPDATE.sendTo(this.player, S2CEnderChestUpdate.create((PlayerEnderChestContainer) inv));
  }

  /**
   * Attempts to attach an ender chest inventory listener to the given player
   * if they don't already have one.
   *
   * @param player The player
   */
  public static void attachTo(ServerPlayer player) {
    var inventory = player == null ? null : player.getEnderChestInventory();
    var listeners = inventory == null ? null : inventory.listeners;

    // Search for existing listener
    if (listeners != null) {
      for (ContainerListener listener : listeners)
        if (listener instanceof EnderChestInventoryListener)
          return;
    }
    if (inventory != null)
      inventory.addListener(new EnderChestInventoryListener(player));
  }

  /**
   * Attempts to detach an ender chest inventory listener to the given player if they have one.
   *
   * @param player The player
   */
  public static void detachFrom(ServerPlayer player) {
    var inventory = player == null ? null : player.getEnderChestInventory();
    var listeners = inventory == null ? null : inventory.listeners;

    if (listeners == null)
      return;

    // Search for existing listener and remove it if found
    for (ContainerListener listener : listeners) {
      if (listener instanceof EnderChestInventoryListener) {
        inventory.removeListener(listener);
        return;
      }
    }
  }
}
