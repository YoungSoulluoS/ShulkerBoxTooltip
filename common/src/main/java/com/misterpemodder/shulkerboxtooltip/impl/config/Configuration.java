package com.misterpemodder.shulkerboxtooltip.impl.config;

import blue.endless.jankson.Comment;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorRegistry;
import com.misterpemodder.shulkerboxtooltip.api.config.ItemStackMergingStrategy;
import com.misterpemodder.shulkerboxtooltip.api.config.PreviewConfiguration;
import com.misterpemodder.shulkerboxtooltip.impl.color.ColorRegistryImpl;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.ConfigCategory;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.RequiresRestart;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.Synchronize;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.Validator;
import com.misterpemodder.shulkerboxtooltip.impl.config.validators.GreaterThanZero;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class Configuration implements PreviewConfiguration {
  @ConfigCategory
  public PreviewCategory preview;

  @ConfigCategory
  public TooltipCategory tooltip;

  @ConfigCategory
  @Environment(EnvType.CLIENT)
  public ColorsCategory colors;

  @ConfigCategory
  @Environment(EnvType.CLIENT)
  public ControlsCategory controls;

  @ConfigCategory
  public ServerCategory server;

  public Configuration() {
    this.preview = new PreviewCategory();
    this.tooltip = new TooltipCategory();
    if (ShulkerBoxTooltip.isClient()) {
      this.colors = new ColorsCategory();
      this.controls = new ControlsCategory();
    }
    this.server = new ServerCategory();
  }

  public static class PreviewCategory {
    @Comment("""
        Toggles the shulker box preview.
        (default value: true)""")
    public boolean enable = true;

    @Comment("""
        Swaps the preview modes.
        If true, pressing the preview key will show the full preview instead.
        (default value: false)""")
    public boolean swapModes = false;

    @Comment("""
        If on, the preview is always displayed, regardless of the preview key being pressed.
        (default value: false)""")
    public boolean alwaysOn = false;

    @Comment("""
        In compact mode, how should items with the same ID but different component data be compacted?
        IGNORE: Ignores component data
        FIRST_ITEM: Items are displayed as all having the same component as the first item
        SEPARATE: Separates items with different component data
        (default value: SEPARATE)""")
    public ItemStackMergingStrategy compactPreviewNbtBehavior = ItemStackMergingStrategy.SEPARATE;

    @Validator(GreaterThanZero.class)
    @Comment("""
        The max number of items in a row.
        May not affect modded containers.
        (default value: 9)""")
    public int defaultMaxRowSize = 9;

    @RequiresRestart
    @Comment("""
        If on, the client will try to send packets to servers to allow extra preview information such as ender chest previews.
        (default value: true)
        """)
    public boolean serverIntegration = true;

    @Comment("""
        The theme to use for preview windows.
        SHULKERBOXTOOLTIP: ShulkerBoxTooltip's default look and feel.
        VANILLA: Mimics the style of vanilla bundle previews.
        (default value: SHULKERBOXTOOLTIP)""")
    public Theme theme = Theme.SHULKERBOXTOOLTIP;

    @Comment("""
        The position of the preview window.
        INSIDE: Inside the item's tooltip.
        OUTSIDE: Outside the item's tooltip, moves depending on the screen borders.
        OUTSIDE_TOP: Always at the top of the item's tooltip.
        OUTSIDE_BOTTOM: Always at the bottom of the item's tooltip.
        (default value: INSIDE)""")
    public PreviewPosition position = PreviewPosition.INSIDE;

    @Comment("""
        If on, large item counts in compact previews will be shortened.
        (default value: true)""")
    public boolean shortItemCounts = true;
  }


  public enum Theme {
    SHULKERBOXTOOLTIP, VANILLA
  }


  public enum PreviewPosition {
    INSIDE, OUTSIDE, OUTSIDE_TOP, OUTSIDE_BOTTOM
  }


  public static class TooltipCategory {
    @Comment("""
        Controls whether the key hints in the container's tooltip should be displayed.
        (default value: true)""")
    public boolean showKeyHints = true;

    @Comment("""
        The tooltip to use.
        VANILLA: The vanilla tooltip (shows the first 5 items)
        MOD: The mod's tooltip
        NONE: No tooltip
        (default value: MOD)""")
    public ShulkerBoxTooltipType type = ShulkerBoxTooltipType.MOD;

    @Comment("""
        Shows info about the current loot table of the item if present.
        Visible only when Tooltip Type is set to Modded.
        HIDE: No loot table info, default.
        SIMPLE: Displays whether the stack uses a loot table.
        ADVANCED: Shows the loot table used by the item.
        (default value: HIDE)""")
    public LootTableInfoType lootTableInfoType = LootTableInfoType.HIDE;

    @Comment("""
        If on, the mod hides the custom text on shulker box tooltips.
        Use this option when a server-side preview data pack clashes with the mod.
        (default value: false)""")
    public boolean hideShulkerBoxLore = false;
  }


  public enum ShulkerBoxTooltipType {
    VANILLA, MOD, NONE
  }


  public enum LootTableInfoType {
    HIDE, SIMPLE, ADVANCED
  }


  @Environment(EnvType.CLIENT)
  public static class ColorsCategory {
    @Comment("""
        Controls whether the preview window should be colored.
        (default value: true)""")
    public boolean coloredPreview = true;

    public ColorRegistry colors = ColorRegistryImpl.INSTANCE;
  }


  @Environment(EnvType.CLIENT)
  public static class ControlsCategory {
    @Comment("""
        Press this key when hovering a container stack to open the preview window.
        (default value: key.keyboard.left.shift)""")
    public Key previewKey = Key.defaultPreviewKey();

    @Comment("""
        Press this key when hovering a container stack to open the full preview window.
        (default value: key.keyboard.left.alt)""")
    public Key fullPreviewKey = Key.defaultFullPreviewKey();

    @Comment("""
        Hold this key when previewing a stack to lock the tooltip.
        (default value: key.keyboard.left.control)""")
    public Key lockTooltipKey = Key.defaultLockTooltipKey();
  }


  public static class ServerCategory {
    @Synchronize
    @RequiresRestart
    @Comment("""
        If on, the server will be able to provide extra information about containers to the clients with the mod installed.
        Disabling this option will disable all the options below.
        (default value: true)
        """)
    public boolean clientIntegration = true;

    @Synchronize
    @RequiresRestart
    @Comment("""
        Changes the way the ender chest content preview is synchronized.
        NONE: No synchronization, prevents clients from seeing a preview of their ender chest.
        ACTIVE: Ender chest contents are synchronized when changed.
        PASSIVE: Ender chest contents are synchronized when the client opens a preview.
        (default value: ACTIVE)""")
    public EnderChestSyncType enderChestSyncType = EnderChestSyncType.ACTIVE;
  }


  public enum EnderChestSyncType {
    NONE, ACTIVE, PASSIVE
  }

  @Override
  public ItemStackMergingStrategy itemStackMergingStrategy() {
    return this.preview.compactPreviewNbtBehavior;
  }

  @Override
  public int defaultMaxRowSize() {
    return this.preview.defaultMaxRowSize;
  }

  @Override
  public boolean shortItemCounts() {
    return this.preview.shortItemCounts;
  }

  @Override
  public boolean useColors() {
    return this.colors.coloredPreview;
  }
}
