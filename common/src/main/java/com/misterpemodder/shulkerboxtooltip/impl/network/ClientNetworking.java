package com.misterpemodder.shulkerboxtooltip.impl.network;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.PluginManager;
import com.misterpemodder.shulkerboxtooltip.impl.config.ConfigurationHandler;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.C2SMessages;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * Client-side network handling.
 */
@Environment(EnvType.CLIENT)
public class ClientNetworking {
  /**
   * The server's network protocol version, null when not connected or the server is not compatible.
   */
  @Nullable
  public static ProtocolVersion serverProtocolVersion;

  /**
   * Corresponds to Fabric's <code>ClientPlayConnectionEvents.JOIN</code> and
   * Forge's <code>ClientPlayerNetworkEvent.LoggedInEvent</code> events.
   */
  public static void onJoinServer(Minecraft client) {
    client.execute(() -> {
      PluginManager.loadColors();
      PluginManager.loadProviders();
    });
    ShulkerBoxTooltip.config = ConfigurationHandler.copyOf(ShulkerBoxTooltip.savedConfig);

    // Re-init some config values before syncing
    serverProtocolVersion = null;
    if (!Minecraft.getInstance().hasSingleplayerServer())
      ConfigurationHandler.reinitClientSideSyncedValues(ShulkerBoxTooltip.config);
    C2SMessages.attemptHandshake();
  }

  /**
   * Creates a vanilla custom payload packet from the given channel identifier and raw data.
   *
   * @param channelId The channel identifier.
   * @param buf       The packet's data.
   * @return A custom vanilla packet.
   */
  public static Packet<?> createC2SPacket(ResourceLocation channelId, FriendlyByteBuf buf) {
    return new ServerboundCustomPayloadPacket(channelId, buf);
  }

  /**
   * Performs registration of messages and events.
   */
  @ExpectPlatform
  public static void init() {
    throw new AssertionError("Missing implementation of ClientNetworking.init()");
  }

  /**
   * Registers a function to handle messages from the server in the given channel.
   *
   * @param channelId The channel identifier.
   * @param receiver  The handling function.
   */
  @ExpectPlatform
  public static void registerS2CReceiver(ResourceLocation channelId, PacketReceiver receiver) {
    throw new AssertionError("Missing implementation of ClientNetworking.registerS2CReceiver()");
  }

  /**
   * Unregisters a channel.
   * <p>
   * Does nothing if already unregistered/not registered.
   *
   * @param channelId The channel identifier.
   */
  @ExpectPlatform
  public static void unregisterS2CReceiver(ResourceLocation channelId) {
    throw new AssertionError("Missing implementation of ClientNetworking.unregisterS2CReceiver()");
  }

  /**
   * Register a function to call each time the server registers/unregisters the given channel id.
   *
   * @param channelId The channel identifier.
   * @param listener  The listener to call on registration changes.
   */
  @ExpectPlatform
  public static void addRegistrationChangeListener(ResourceLocation channelId, RegistrationChangeListener listener) {
    throw new AssertionError("Missing implementation of ClientNetworking.addRegistrationChangeListener()");
  }

  /**
   * Server to client packet-handling function.
   */
  @FunctionalInterface
  public interface PacketReceiver {
    /**
     * Handles a server to client packet.
     *
     * @param buf The packet data.
     */
    void handle(FriendlyByteBuf buf);
  }


  /**
   * Server channel registration change listener.
   */
  @FunctionalInterface
  public interface RegistrationChangeListener {
    /**
     * Server channel registration change event.
     *
     * @param type Whether the channel was registered on unregistered.
     */
    void onRegistrationChange(RegistrationChangeType type);
  }
}
