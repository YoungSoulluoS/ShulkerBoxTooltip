package com.misterpemodder.shulkerboxtooltip.impl.network.fabric;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.RegistrationChangeType;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.C2SMessages;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ClientNetworkingImpl extends ClientNetworking {
  private final static Map<ResourceLocation, RegistrationChangeListener> REGISTRATION_CHANGE_LISTENERS =
      new HashMap<>();

  /**
   * Implements {@link ClientNetworking#init()}.
   */
  public static void init() {
    if (ShulkerBoxTooltip.config.preview.serverIntegration) {
      C2SMessages.init();
      ClientPlayConnectionEvents.INIT.register((handler, client) -> S2CMessages.registerAll());
      ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> C2SMessages.onDisconnectFromServer());
    }
    ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ClientNetworking.onJoinServer(client));
  }

  /**
   * Implements {@link ClientNetworking#registerS2CReceiver(ResourceLocation, PacketReceiver)} ()}.
   */
  public static void registerS2CReceiver(ResourceLocation channelId, PacketReceiver receiver) {
    ClientPlayNetworking.registerReceiver(channelId, (client, handler, buf, responseSender) -> receiver.handle(buf));
  }

  /**
   * Implements {@link ClientNetworking#unregisterS2CReceiver(ResourceLocation)} ()}.
   */
  public static void unregisterS2CReceiver(ResourceLocation channelId) {
    ClientPlayNetworking.unregisterReceiver(channelId);
  }

  /**
   * Implements {@link ClientNetworking#addRegistrationChangeListener(ResourceLocation, RegistrationChangeListener)} ()}.
   */
  public static void addRegistrationChangeListener(ResourceLocation channelId, RegistrationChangeListener listener) {
    REGISTRATION_CHANGE_LISTENERS.put(channelId, listener);
  }

  private static void dispatchRegistrationChangeEvent(ResourceLocation channelId, RegistrationChangeType type) {
    RegistrationChangeListener listener = REGISTRATION_CHANGE_LISTENERS.get(channelId);
    if (listener != null)
      listener.onRegistrationChange(type);
  }

  static {
    C2SPlayChannelEvents.REGISTER.register((handler, sender, server, channels) -> channels.forEach(
        c -> ClientNetworkingImpl.dispatchRegistrationChangeEvent(c, RegistrationChangeType.REGISTER)));
    C2SPlayChannelEvents.UNREGISTER.register((handler, sender, server, channels) -> channels.forEach(
        c -> ClientNetworkingImpl.dispatchRegistrationChangeEvent(c, RegistrationChangeType.UNREGISTER)));
  }
}
