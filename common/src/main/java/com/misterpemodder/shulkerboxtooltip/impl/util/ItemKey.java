package com.misterpemodder.shulkerboxtooltip.impl.util;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

/**
 * Used as a key in maps
 */
public class ItemKey {
  private final Item item;
  private final int id;
  private final CompoundTag data;
  private final boolean ignoreData;

  public ItemKey(ItemStack stack, boolean ignoreData) {
    this.item = stack.getItem();
    this.id = Registry.ITEM.getId(this.item);
    this.data = stack.getTag();
    this.ignoreData = ignoreData;
  }

  @Override
  public int hashCode() {
    return 31 * id + (this.ignoreData || data == null ? 0 : data.hashCode());
  }

  @Override
  public boolean equals(Object other) {
    if (this == other)
      return true;
    if (!(other instanceof ItemKey key))
      return false;

    return key.item == this.item && key.id == this.id && (this.ignoreData || Objects.equals(key.data, this.data));
  }
}
