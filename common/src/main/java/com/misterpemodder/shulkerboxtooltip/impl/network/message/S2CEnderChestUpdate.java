package com.misterpemodder.shulkerboxtooltip.impl.network.message;

import com.misterpemodder.shulkerboxtooltip.impl.network.context.MessageContext;
import com.misterpemodder.shulkerboxtooltip.impl.util.NbtType;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.PlayerEnderChestContainer;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Updates a client's ender chest contents.
 *
 * @param nbtInventory NBT-serialized ender chest inventory.
 */
public record S2CEnderChestUpdate(@Nullable ListTag nbtInventory) {
  public static S2CEnderChestUpdate create(PlayerEnderChestContainer inventory) {
    return new S2CEnderChestUpdate(inventory.createTag());
  }

  public static class Type implements MessageType<S2CEnderChestUpdate> {
    @Override
    public void encode(S2CEnderChestUpdate message, FriendlyByteBuf buf) {
      CompoundTag compound = new CompoundTag();

      compound.put("inv", Objects.requireNonNull(message.nbtInventory));
      buf.writeNbt(compound);
    }

    @Override
    public S2CEnderChestUpdate decode(FriendlyByteBuf buf) {
      CompoundTag compound = buf.readNbt();

      if (compound == null || !compound.contains("inv", NbtType.LIST))
        return new S2CEnderChestUpdate(null);
      return new S2CEnderChestUpdate(compound.getList("inv", NbtType.COMPOUND));
    }

    @Override
    public void onReceive(S2CEnderChestUpdate message, MessageContext<S2CEnderChestUpdate> context) {
      if (message.nbtInventory == null)
        return;

      Minecraft.getInstance().execute(() -> {
        if (Minecraft.getInstance().player != null)
          Minecraft.getInstance().player.getEnderChestInventory().fromTag(message.nbtInventory);
      });
    }
  }
}
