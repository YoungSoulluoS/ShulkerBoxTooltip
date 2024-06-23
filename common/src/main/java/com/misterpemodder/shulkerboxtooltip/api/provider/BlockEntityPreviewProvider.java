package com.misterpemodder.shulkerboxtooltip.api.provider;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SeededContainerLoot;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * A PreviewProvider that works on items that carries block entity data.
 * </p>
 * <p>
 * Use/extend this when the target item(s) has the {@code Inventory} inside {@code BlockEntityData}
 * as created by {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList, HolderLookup.Provider)}.
 * </p>
 *
 * @since 1.3.0
 */
public class BlockEntityPreviewProvider implements PreviewProvider {
  /**
   * The maximum preview inventory size of the item (maybe lower than the actual inventory size).
   *
   * @deprecated Use {@link #getInventoryMaxSize(PreviewContext)} instead.
   */
  @Deprecated(since = "4.0.8", forRemoval = true)
  protected final int maxInvSize;
  /**
   * If true, previews will not be shown when the {@code LootTable} tag inside {@code BlockEntityData} is present.
   *
   * @deprecated Use {@link #canUseLootTables()} instead.
   */
  @Deprecated(since = "4.0.8", forRemoval = true)
  protected final boolean canUseLootTables;
  /**
   * The maximum number of item stacks to be displayed in a row.
   *
   * @deprecated Use {@link #getMaxRowSize(PreviewContext)} instead.
   */
  @Deprecated(since = "4.0.8", forRemoval = true)
  protected final int maxRowSize;

  /**
   * Creates a BlockEntityPreviewProvider instance.
   *
   * @param maxInvSize       The maximum preview inventory size of the item
   *                         (maybe lower than the actual inventory size).
   *                         If the inventory size isn't constant,
   *                         override {@link #getInventoryMaxSize(PreviewContext)}
   *                         and use {@code maxInvSize} as a default value.
   * @param canUseLootTables If true, previews will not be shown when the {@code LootTable}
   *                         tag inside {@code BlockEntityData} is present.
   * @since 1.3.0
   */
  public BlockEntityPreviewProvider(int maxInvSize, boolean canUseLootTables) {
    this.maxInvSize = maxInvSize;
    this.canUseLootTables = canUseLootTables;
    this.maxRowSize = 9;
  }

  /**
   * Creates a BlockEntityPreviewProvider instance.
   *
   * @param maxInvSize       The maximum preview inventory size of the item
   *                         (maybe lower than the actual inventory size).
   *                         If the inventory size isn't constant,
   *                         override {@link #getInventoryMaxSize(PreviewContext)}
   *                         and use {@code maxInvSize} as a default value.
   * @param canUseLootTables If true, previews will not be shown when the {@code LootTable}
   *                         tag inside {@code BlockEntityData} is present.
   * @param maxRowSize       The maximum number of item stacks to be displayed in a row.
   *                         If less or equal to zero, defaults to 9.
   * @since 2.0.0
   */
  public BlockEntityPreviewProvider(int maxInvSize, boolean canUseLootTables, int maxRowSize) {
    this.maxInvSize = maxInvSize;
    this.canUseLootTables = canUseLootTables;
    this.maxRowSize = maxRowSize <= 0 ? 9 : maxRowSize;
  }

  @Override
  public boolean shouldDisplay(PreviewContext context) {
    if (this.canUseLootTables() && context.stack().has(DataComponents.CONTAINER_LOOT))
      return false;
    return getItemCount(this.getInventory(context)) > 0;
  }

  @Override
  public boolean showTooltipHints(PreviewContext context) {
    return context.stack().has(DataComponents.CONTAINER);
  }

  @Override
  public List<ItemStack> getInventory(PreviewContext context) {
    var registries = context.registryLookup();
    var container = context.stack().get(DataComponents.CONTAINER);
    var invMaxSize = this.getInventoryMaxSize(context);
    var inv = NonNullList.withSize(invMaxSize, ItemStack.EMPTY);

    if (registries != null && container != null)
      container.copyInto(inv);

    return inv;
  }

  @Override
  public int getInventoryMaxSize(PreviewContext context) {
    return this.maxInvSize;
  }

  @Override
  public List<Component> addTooltip(PreviewContext context) {
    ItemStack stack = context.stack();
    SeededContainerLoot lootComponent = stack.get(DataComponents.CONTAINER_LOOT);
    Style style = Style.EMPTY.withColor(ChatFormatting.GRAY);

    if (this.canUseLootTables() && lootComponent != null) {
      return switch (ShulkerBoxTooltip.config.tooltip.lootTableInfoType) {
        case HIDE -> Collections.emptyList();
        case SIMPLE -> Collections.singletonList(
            Component.translatable("shulkerboxtooltip.hint.loot_table").setStyle(style));
        default -> Arrays.asList(
            Component.translatable("shulkerboxtooltip.hint.loot_table.advanced").append(Component.literal(": ")),
            Component.literal(" " + lootComponent.lootTable().location()).setStyle(style));
      };
    }
    if (ShulkerBoxTooltipApi.getCurrentPreviewType(this.isFullPreviewAvailable(context)) == PreviewType.FULL)
      return Collections.emptyList();
    return getItemListTooltip(new ArrayList<>(), this.getInventory(context), style);
  }

  /**
   * Adds the number of items to the passed tooltip, adds 'empty' if there is no items to count.
   *
   * @param tooltip The tooltip in which to add the item count.
   * @param items   The list of items to display, may be null or empty.
   * @return The passed tooltip, to allow chaining.
   * @since 2.0.0
   */
  public static List<Component> getItemCountTooltip(List<Component> tooltip, @Nullable List<ItemStack> items) {
    return getItemListTooltip(tooltip, items, Style.EMPTY.withColor(ChatFormatting.GRAY));
  }

  /**
   * Adds the number of items to the passed tooltip, adds 'empty' if there is no items to count.
   *
   * @param tooltip The tooltip in which to add the item count.
   * @param items   The list of items to display, may be null or empty.
   * @param style   The formatting style of the tooltip.
   * @return The passed tooltip, to allow chaining.
   * @since 2.0.0
   */
  public static List<Component> getItemListTooltip(List<Component> tooltip, @Nullable List<ItemStack> items,
      Style style) {
    int itemCount = getItemCount(items);
    MutableComponent text;

    if (itemCount > 0)
      text = Component.translatable("container.shulkerbox.contains", itemCount);
    else
      text = Component.translatable("container.shulkerbox.empty");
    tooltip.add(text.setStyle(style));
    return tooltip;
  }

  @Override
  public int getMaxRowSize(PreviewContext context) {
    return this.maxRowSize;
  }

  /**
   * If true, previews will not be shown when the {@code LootTable} tag inside {@code BlockEntityData} is present.
   *
   * @since 4.0.8
   */
  public boolean canUseLootTables() {
    return this.canUseLootTables;
  }

  private static int getItemCount(@Nullable List<ItemStack> items) {
    int itemCount = 0;

    if (items != null)
      for (ItemStack stack : items)
        if (stack.getItem() != Items.AIR)
          ++itemCount;
    return itemCount;
  }
}
