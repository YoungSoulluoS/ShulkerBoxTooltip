package com.misterpemodder.shulkerboxtooltip.impl.config;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.EnderChestSyncType;
import com.misterpemodder.shulkerboxtooltip.impl.util.EnvironmentUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public final class ConfigurationHandler {

  private static ShulkerBoxTooltipConfigSerializer serializer;

  private ConfigurationHandler() {
  }

  public static Configuration register() {
    serializer = new ShulkerBoxTooltipConfigSerializer();
    var config = loadFromFile();
    saveToFile(config);
    return config;
  }

  public static Configuration loadFromFile() {
    try {
      var config = serializer.deserialize();
      var errorMsg = ShulkerBoxTooltip.configTree.validate(config);
      if (errorMsg != null) {
        ShulkerBoxTooltip.LOGGER.error("Failed to load configuration, using default values: " + errorMsg);
        return EnvironmentUtil.getInstance().makeConfiguration();
      }
      return config;
    } catch (SerializationException e) {
      ShulkerBoxTooltip.LOGGER.error("Failed to load configuration, using default values", e);
      return EnvironmentUtil.getInstance().makeConfiguration();
    }
  }

  public static void saveToFile(Configuration toSave) {

    if (ShulkerBoxTooltip.savedConfig != null) {
      ShulkerBoxTooltip.configTree.copy(toSave, ShulkerBoxTooltip.savedConfig);
    }
    if (ShulkerBoxTooltip.config != null) {
      // Put aside server-synced values
      var serverConfigNbt = new CompoundTag();
      ShulkerBoxTooltip.configTree.writeToNbt(ShulkerBoxTooltip.config, serverConfigNbt);

      ShulkerBoxTooltip.configTree.copy(toSave, ShulkerBoxTooltip.config);

      // Restore server-synced values to active config
      ShulkerBoxTooltip.configTree.readFromNbt(ShulkerBoxTooltip.config, serverConfigNbt);
    }

    try {
      serializer.serialize(toSave);
    } catch (SerializationException e) {
      ShulkerBoxTooltip.LOGGER.error("Failed to save configuration", e);
    }
  }

  @Environment(EnvType.CLIENT)
  public static void reinitClientSideSyncedValues(Configuration config) {
    config.server.clientIntegration = false;
    config.server.enderChestSyncType = EnderChestSyncType.NONE;
  }

  public static void readFromPacketBuf(Configuration config, FriendlyByteBuf buf) {
    CompoundTag compound = buf.readNbt();
    if (compound != null)
      ShulkerBoxTooltip.configTree.readFromNbt(config, compound);
  }

  public static void writeToPacketBuf(Configuration config, FriendlyByteBuf buf) {
    CompoundTag compound = new CompoundTag();
    ShulkerBoxTooltip.configTree.writeToNbt(config, compound);
    buf.writeNbt(compound);
  }
}