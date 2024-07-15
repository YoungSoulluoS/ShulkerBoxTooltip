package com.misterpemodder.shulkerboxtooltip.impl.util;

import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration;
import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public abstract sealed class EnvironmentUtil permits ClientEnvironmentUtil, ServerEnvironmentUtil {

  private static EnvironmentUtil instance;

  private static final String PACKAGE_NAME = EnvironmentUtil.class.getPackageName();
  private static final String CLIENT_ENVIRONMENT_UTIL = PACKAGE_NAME + ".ClientEnvironmentUtil";
  private static final String SERVER_ENVIRONMENT_UTIL = PACKAGE_NAME + ".ServerEnvironmentUtil";

  public static EnvironmentUtil getInstance() {
    if (instance == null) {
      String className = isClient() ? CLIENT_ENVIRONMENT_UTIL : SERVER_ENVIRONMENT_UTIL;
      try {
        Class<?> clazz = Class.forName(className);
        instance = (EnvironmentUtil) clazz.getDeclaredConstructor().newInstance();
      } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
               InvocationTargetException e) {
        throw new IllegalStateException(e);
      }
    }
    return instance;
  }

  @NotNull
  public abstract Configuration makeConfiguration();

  @NotNull
  public abstract Class<? extends Configuration> getConfigurationClass();

  /**
   * @return Whether the current environment type (or Dist in forge terms) is the client.
   */
  @ExpectPlatform
  @Contract(value = "-> _", pure = true)
  public static boolean isClient() {
    //noinspection Contract
    throw new AssertionError("Missing implementation of EnvironmentUtil.isClient()");
  }
}
