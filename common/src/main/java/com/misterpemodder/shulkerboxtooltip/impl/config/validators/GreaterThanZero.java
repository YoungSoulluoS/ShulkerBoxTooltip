package com.misterpemodder.shulkerboxtooltip.impl.config.validators;

import com.misterpemodder.shulkerboxtooltip.impl.tree.ValueConfigNode;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public final class GreaterThanZero implements ValueConfigNode.ValueValidator<Object> {
  @Nullable
  @Override
  public Component validate(Object value) {
    Class<?> valueClass = value.getClass();
    if (valueClass.equals(Integer.class) && (Integer) value <= 0) {
      return Component.translatable("shulkerboxtooltip.config.validator.greater_than_zero");
    }
    return null;
  }
}
