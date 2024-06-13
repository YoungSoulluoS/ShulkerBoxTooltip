package com.misterpemodder.shulkerboxtooltip.impl.config.gui.entry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class ConfigEntry extends ContainerObjectSelectionList.Entry<ConfigEntry> {

  @Nullable
  public List<FormattedCharSequence> getTooltip() {
    return null;
  }

  public void refresh() {
  }
}
