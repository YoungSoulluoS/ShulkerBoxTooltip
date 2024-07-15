package com.misterpemodder.shulkerboxtooltip.impl.util.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class EnvironmentUtilImpl {

  private EnvironmentUtilImpl() {
  }

  /**
   * Implementation of {@link com.misterpemodder.shulkerboxtooltip.impl.util.EnvironmentUtil#isClient()}.
   */
  public static boolean isClient() {
    return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
  }
}
