package com.misterpemodder.shulkerboxtooltip.impl.network.forge;

import com.misterpemodder.shulkerboxtooltip.impl.network.Payload;
import com.misterpemodder.shulkerboxtooltip.impl.network.channel.C2SChannel;
import com.misterpemodder.shulkerboxtooltip.impl.network.message.MessageType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ForgeC2SChannel<T> extends ForgeChannel<T> implements C2SChannel<T> {
  public ForgeC2SChannel(ResourceLocation id, MessageType<T> type) {
    super(id, type);
  }

  @Override
  public void registerFor(ServerPlayer player) {
    // Forge does not support dynamic channel registration
  }

  @Override
  public void unregisterFor(ServerPlayer player) {
    // Forge does not support dynamic channel registration
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void sendToServer(T message) {
    //    PacketDistributor.sendToServer(new Payload<>(this.id, message));
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public boolean canSendToServer() {
    return false;
    //    ICommonPacketListener listener = Minecraft.getInstance().getConnection();
    //    return listener != null && listener.hasChannel(this.getId());
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void onDisconnect() {
  }

  @Override
  protected void onReceive(Payload<T> payload, Object /*IPayloadContext*/ context) {
    //    if (context.flow().isServerbound()) {
    //      this.type.onReceive(payload.value(), new C2SMessageContext<>((ServerPlayer) context.player(), this));
    //    }
  }
}
