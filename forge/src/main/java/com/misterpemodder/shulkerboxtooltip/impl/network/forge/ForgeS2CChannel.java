package com.misterpemodder.shulkerboxtooltip.impl.network.forge;

import com.misterpemodder.shulkerboxtooltip.impl.network.Payload;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.S2CChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ForgeS2CChannel<T> extends ForgeChannel<T> implements S2CChannel<T> {
  public ForgeS2CChannel(ResourceLocation id, MessageType<T> type) {
    super(id, type);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void register() {
    // NeoForge does not support dynamic channel registration
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void unregister() {
    // NeoForge does not support dynamic channel registration
  }

  @Override
  public void sendTo(ServerPlayer player, T message) {
    //    PacketDistributor.sendToPlayer(player, new Payload<>(this.id, message));
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  protected void onReceive(Payload<T> payload, Object /*IPayloadContext*/ context) {
    //    if (context.flow().isClientbound()) {
    //      this.type.onReceive(payload.value(), new S2CMessageContext<>(this));
    //    }
  }
}
