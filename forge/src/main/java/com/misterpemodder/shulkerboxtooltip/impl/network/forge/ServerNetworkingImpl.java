package com.misterpemodder.shulkerboxtooltip.impl.network.forge;

import com.misterpemodder.shulkerboxtooltip.impl.network.ServerNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.C2SMessages;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ServerNetworkingImpl {
  @SubscribeEvent
  public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
    C2SMessages.registerAllFor((ServerPlayer) event.getPlayer());
  }

  @SubscribeEvent
  public static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
    ServerNetworking.removeClient((ServerPlayer) event.getPlayer());
  }

  /**
   * Implementation of {@link ServerNetworking#init()}.
   */
  public static void init() {
    C2SMessages.init();
    S2CMessages.init();
    MinecraftForge.EVENT_BUS.register(ServerNetworkingImpl.class);
  }

  /**
   * Implementation of {@link ServerNetworking#registerC2SReceiver(ResourceLocation, ServerPlayer, ServerNetworking.PacketReceiver)}.
   */
  public static void registerC2SReceiver(ResourceLocation channelId, ServerPlayer player,
      ServerNetworking.PacketReceiver receiver) {
    ChannelListener.get(channelId).c2sPacketReceiver = receiver;
  }

  /**
   * Implementation of {@link ServerNetworking#unregisterC2SReceiver(ResourceLocation, ServerPlayer)}.
   */
  public static void unregisterC2SReceiver(ResourceLocation channelId, ServerPlayer player) {
    ChannelListener.get(channelId).c2sPacketReceiver = null;
  }

  /**
   * Implementation of {@link ServerNetworking#addRegistrationChangeListener(ResourceLocation, ServerNetworking.RegistrationChangeListener)}.
   */
  public static void addRegistrationChangeListener(ResourceLocation channelId,
      ServerNetworking.RegistrationChangeListener listener) {
    ChannelListener.get(channelId).c2sRegChangeListener = listener;
  }
}
