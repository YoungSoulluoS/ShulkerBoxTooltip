package com.misterpemodder.shulkerboxtooltip.impl.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DefaultedTranslatableComponent extends TranslatableComponent {
  private final String defaultString;
  private final FormattedText defaultStringRenderable;

  public DefaultedTranslatableComponent(String key, String defaultString) {
    super(key);
    this.defaultString = defaultString;
    this.defaultStringRenderable = FormattedText.of(defaultString);
  }

  public DefaultedTranslatableComponent(String key, String defaultString, Object... args) {
    super(key, args);
    this.defaultString = defaultString;
    this.defaultStringRenderable = FormattedText.of(defaultString);
  }

  public String getDefaultString() {
    return this.defaultString;
  }

  @Override
  @Environment(EnvType.CLIENT)
  public <T> Optional<T> visitSelf(FormattedText.StyledContentConsumer<T> consumer, Style style) {
    String key = this.getKey();

    if (Language.getInstance().getOrDefault(key).equals(key))
      return defaultStringRenderable.visit(consumer, style);
    return super.visitSelf(consumer, style);
  }

  @Override
  public <T> Optional<T> visitSelf(FormattedText.ContentConsumer<T> visitor) {
    String key = this.getKey();

    if (Language.getInstance().getOrDefault(key).equals(key))
      return defaultStringRenderable.visit(visitor);
    return super.visitSelf(visitor);
  }
}
