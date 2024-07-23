package com.misterpemodder.shulkerboxtooltip.impl.network.forge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.network.ClientNetworking;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.C2SMessages;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.S2CMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public final class ClientNetworkingImpl extends ClientNetworking {
  @SubscribeEvent
  public static void onJoinServer(ClientPlayerNetworkEvent.LoggingIn event) {
    if (ShulkerBoxTooltip.config.preview.serverIntegration)
      S2CMessages.registerAll();
    ClientNetworking.onJoinServer(Minecraft.getInstance());
  }

  @SubscribeEvent
  public static void onLeaveServer(ClientPlayerNetworkEvent.LoggingOut event) {
    if (ShulkerBoxTooltip.config.preview.serverIntegration)
      C2SMessages.onDisconnectFromServer();
  }

  /**
   * Implements {@link ClientNetworking#init()}.
   */
  public static void init() {
    C2SMessages.init();
    S2CMessages.init();
    MinecraftForge.EVENT_BUS.register(ClientNetworkingImpl.class);
  }

  /**
   * Implements {@link ClientNetworking#registerS2CReceiver(ResourceLocation, PacketReceiver)}.
   */
  public static void registerS2CReceiver(ResourceLocation channelId, PacketReceiver receiver) {
    ChannelListener.get(channelId).s2cPacketReceiver = receiver;
  }

  /**
   * Implements {@link ClientNetworking#unregisterS2CReceiver(ResourceLocation)}.
   */
  public static void unregisterS2CReceiver(ResourceLocation channelId) {
    ChannelListener.get(channelId).s2cPacketReceiver = null;
  }

  /**
   * Implements {@link ClientNetworking#addRegistrationChangeListener(ResourceLocation, RegistrationChangeListener)}.
   */
  public static void addRegistrationChangeListener(ResourceLocation channelId, RegistrationChangeListener listener) {
    ChannelListener.get(channelId).s2cRegChangeListener = listener;
  }
}
