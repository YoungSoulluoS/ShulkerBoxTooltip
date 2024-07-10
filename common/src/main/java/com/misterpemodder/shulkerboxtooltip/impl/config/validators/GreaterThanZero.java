package com.misterpemodder.shulkerboxtooltip.impl.config.validators;

import com.misterpemodder.shulkerboxtooltip.impl.util.DefaultedTranslatableComponent;
import net.minecraft.network.chat.Component;

import java.util.Optional;
import java.util.function.Function;

public final class GreaterThanZero implements Function<Object, Optional<Component>> {
  @Override
  public Optional<Component> apply(Object value) {
    Class<?> valueClass = value.getClass();
    if (valueClass.equals(Integer.class) && (Integer) value <= 0) {
      return Optional.of(new DefaultedTranslatableComponent("ShulkerBoxTooltip.config.validator.greater_than_zero",
          "Must be greater than zero"));
    }
    return Optional.empty();
  }
}
