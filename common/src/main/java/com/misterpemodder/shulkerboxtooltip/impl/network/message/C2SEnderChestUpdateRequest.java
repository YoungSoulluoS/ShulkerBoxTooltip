package com.misterpemodder.shulkerboxtooltip.impl.network.message;

import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * Request an update to contents of the client's ender chest to the server.
 */
public record C2SEnderChestUpdateRequest() {
  public static class Type implements MessageType<C2SEnderChestUpdateRequest> {
    @Override
    public void encode(C2SEnderChestUpdateRequest message, FriendlyByteBuf buf) {
    }

    @Override
    public C2SEnderChestUpdateRequest decode(FriendlyByteBuf buf) {
      return new C2SEnderChestUpdateRequest();
    }

    @Override
    public void onReceive(C2SEnderChestUpdateRequest message, MessageContext<C2SEnderChestUpdateRequest> context) {
      var player = (ServerPlayer) context.getPlayer();
      S2CMessages.ENDER_CHEST_UPDATE.sendTo(player, S2CEnderChestUpdate.create(player.getEnderChestInventory()));
    }
  }
}
