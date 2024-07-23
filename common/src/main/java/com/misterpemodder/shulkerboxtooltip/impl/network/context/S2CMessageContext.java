package com.misterpemodder.shulkerboxtooltip.impl.network.context;

import com.misterpemodder.shulkerboxtooltip.impl.network.channel.Channel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

@Environment(EnvType.CLIENT)
public record S2CMessageContext<MSG>(Channel<MSG> channel) implements MessageContext<MSG> {
  @Override
  public void execute(Runnable task) {
    Minecraft.getInstance().execute(task);
  }

  @Override
  public LocalPlayer getPlayer() {
    return Minecraft.getInstance().player;
  }

  @Override
  public Channel<MSG> getChannel() {
    return this.channel;
  }

  @Override
  public Side getReceivingSide() {
    return Side.CLIENT;
  }
}
