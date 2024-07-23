package com.misterpemodder.shulkerboxtooltip.impl;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public record PreviewContextImpl(ItemStack stack, Player owner, Configuration config) implements PreviewContext {
  public PreviewContextImpl(ItemStack stack, @Nullable Player owner, Configuration config) {
    this.stack = stack;
    this.owner = owner;
    this.config = config;
  }
}
