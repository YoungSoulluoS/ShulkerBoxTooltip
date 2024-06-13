package com.misterpemodder.shulkerboxtooltip.impl.config.gui;

import com.misterpemodder.shulkerboxtooltip.impl.config.gui.entry.ConfigEntry;
import com.misterpemodder.shulkerboxtooltip.impl.config.gui.entry.ValueConfigEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

@Environment(EnvType.CLIENT)
public final class ConfigEntryList extends ContainerObjectSelectionList<ConfigEntry> {
  private final ConfigCategoryTab<?> tab;

  public ConfigEntryList(ConfigCategoryTab<?> tab, Minecraft minecraft, int width, int contentHeight, int headerHeight,
      int itemSpacing, Iterable<ConfigEntry> entries) {
    super(minecraft, width, contentHeight, headerHeight, itemSpacing);
    this.tab = tab;
    entries.forEach(this::addEntry);
  }

  @Override
  public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
    super.renderWidget(guiGraphics, mouseX, mouseY, delta);
    var entry = this.getHovered();
    if (entry != null) {
      if (entry instanceof ValueConfigEntry<?, ?, ?> valueEntry) {
        if (valueEntry.resetButton.isHovered()) {
          this.tab.getScreen().setTooltipForNextRenderPass(ValueConfigEntry.RESET_BUTTON_TOOLTIP);
          return;
        } else if (valueEntry.undoButton.isHovered()) {
          this.tab.getScreen().setTooltipForNextRenderPass(ValueConfigEntry.UNDO_BUTTON_TOOLTIP);
          return;
        }
      }
      if (entry.getTooltip() != null) {
        this.tab.getScreen().setTooltipForNextRenderPass(entry.getTooltip());
      }
    }
  }

  @Override
  public int getRowWidth() {
    return this.width - 80;
  }

  public void refreshEntries() {
    this.children().forEach(ConfigEntry::refresh);
  }

  @Override
  protected void renderListSeparators(GuiGraphics guiGraphics) {
    // don't render separators
  }
}