package com.misterpemodder.shulkerboxtooltip.impl.network.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.RegistrationChangeType;
import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.C2SMessages;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.HashMap;
import java.util.Map;

public final class ServerNetworkingImpl {
  private static final Map<ResourceLocation, ServerNetworking.RegistrationChangeListener>
      REGISTRATION_CHANGE_LISTENERS = new HashMap<>();

  /**
   * Implements {@link ServerNetworking#init()}.
   */
  public static void init() {
    if (!ShulkerBoxTooltip.config.server.clientIntegration)
      return;
    S2CMessages.init();
    C2SMessages.init();
    ServerPlayConnectionEvents.INIT.register((handler, server) -> C2SMessages.registerAllFor(handler.player));
    ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> ServerNetworking.removeClient(handler.player));
  }

  /**
   * Implements {@link ServerNetworking#registerC2SReceiver(ResourceLocation, ServerPlayer, ServerNetworking.PacketReceiver)}.
   */
  public static void registerC2SReceiver(ResourceLocation channelId, ServerPlayer player,
      ServerNetworking.PacketReceiver receiver) {
    ServerGamePacketListenerImpl connection = player.connection;

    if (connection == null) {
      ShulkerBoxTooltip.LOGGER.error("Cannot register packet receiver for " + channelId + ", player is not in game");
      return;
    }
    ServerPlayNetworking.registerReceiver(connection, channelId,
        (server, player1, handler1, buf, responseSender) -> receiver.handle(player1, buf));
  }

  /**
   * Implements {@link ServerNetworking#unregisterC2SReceiver(ResourceLocation, ServerPlayer)}.
   */
  public static void unregisterC2SReceiver(ResourceLocation channelId, ServerPlayer player) {
    ServerGamePacketListenerImpl connection = player.connection;

    if (connection != null) {
      ServerPlayNetworking.unregisterReceiver(connection, channelId);
    }
  }

  /**
   * Implements {@link ServerNetworking#addRegistrationChangeListener(ResourceLocation, ServerNetworking.RegistrationChangeListener)}.
   */
  public static void addRegistrationChangeListener(ResourceLocation channelId,
      ServerNetworking.RegistrationChangeListener listener) {
    REGISTRATION_CHANGE_LISTENERS.put(channelId, listener);
  }

  private static void dispatchRegistrationChangeEvent(ResourceLocation channelId, ServerPlayer sender,
      RegistrationChangeType type) {
    ServerNetworking.RegistrationChangeListener listener = REGISTRATION_CHANGE_LISTENERS.get(channelId);
    if (listener != null)
      listener.onRegistrationChange(sender, type);
  }

  static {
    S2CPlayChannelEvents.REGISTER.register((handler, sender, server, channels) -> channels.forEach(
        c -> ServerNetworkingImpl.dispatchRegistrationChangeEvent(c, handler.getPlayer(),
            RegistrationChangeType.REGISTER)));
    S2CPlayChannelEvents.UNREGISTER.register((handler, sender, server, channels) -> channels.forEach(
        c -> ServerNetworkingImpl.dispatchRegistrationChangeEvent(c, handler.getPlayer(),
            RegistrationChangeType.UNREGISTER)));
  }
}
