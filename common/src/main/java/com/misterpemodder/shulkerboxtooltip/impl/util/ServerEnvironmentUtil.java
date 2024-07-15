package com.misterpemodder.shulkerboxtooltip.impl.util;

import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import org.jetbrains.annotations.NotNull;

public final class ServerEnvironmentUtil extends EnvironmentUtil {
  @NotNull
  @Override
  public Configuration makeConfiguration() {
    return new Configuration();
  }

  @Override
  @NotNull
  public Class<? extends Configuration> getConfigurationClass() {
    return Configuration.class;
  }
}
