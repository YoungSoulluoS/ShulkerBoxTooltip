package com.misterpemodder.shulkerboxtooltip.api.provider;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.*;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
 * as created by {@link ContainerHelper#saveAllItems(CompoundTag, NonNullList)} )}.
 * </p>
 *
 * @since 1.3.0
 */
public class BlockEntityPreviewProvider implements PreviewProvider {
  /**
   * The maximum preview inventory size of the item (maybe lower than the actual inventory size).
   */
  protected final int maxInvSize;
  /**
   * If true, previews will not be shown when the {@code LootTable} tag inside {@code BlockEntityData} is present.
   */
  protected final boolean canUseLootTables;
  /**
   * The maximum number of item stacks to be displayed in a row.
   */
  protected final int maxRowSize;

  /**
   * Creates a BlockEntityPreviewProvider instance.
   *
   * @param maxInvSize       The maximum preview inventory size of the item
   *                         (may be lower than the actual inventory size).
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
   *                         (may be lower than the actual inventory size).
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
    CompoundTag blockEntityTag = context.stack().getTagElement("BlockEntityTag");

    if (blockEntityTag == null || (this.canUseLootTables && blockEntityTag.contains("LootTable", 8)))
      return false;
    return getItemCount(this.getInventory(context)) > 0;
  }

  @Override
  public boolean showTooltipHints(PreviewContext context) {
    return context.stack().getTagElement("BlockEntityTag") != null;
  }

  @Override
  public List<ItemStack> getInventory(PreviewContext context) {
    int invMaxSize = this.getInventoryMaxSize(context);
    List<ItemStack> inv = NonNullList.withSize(invMaxSize, ItemStack.EMPTY);
    CompoundTag blockEntityTag = context.stack().getTagElement("BlockEntityTag");

    if (blockEntityTag != null && blockEntityTag.contains("Items", 9)) {
      ListTag itemList = blockEntityTag.getList("Items", 10);

      if (itemList != null) {
        for (int i = 0, len = itemList.size(); i < len; ++i) {
          CompoundTag itemTag = itemList.getCompound(i);
          ItemStack s = ItemStack.of(itemTag);

          if (!itemTag.contains("Slot", 99))
            continue;
          int slot = itemTag.getInt("Slot");

          if (slot >= 0 && slot < invMaxSize)
            inv.set(slot, s);
        }
      }
    }
    return inv;
  }

  @Override
  public int getInventoryMaxSize(PreviewContext context) {
    return this.maxInvSize;
  }

  @Override
  public List<Component> addTooltip(PreviewContext context) {
    ItemStack stack = context.stack();
    CompoundTag compound = stack.getTag();
    Style style = Style.EMPTY.withColor(ChatFormatting.GRAY);

    if (this.canUseLootTables && compound != null && compound.contains("BlockEntityTag", 10)) {
      CompoundTag blockEntityTag = compound.getCompound("BlockEntityTag");

      if (blockEntityTag != null && blockEntityTag.contains("LootTable", 8)) {
        return switch (ShulkerBoxTooltip.config.tooltip.lootTableInfoType) {
          case HIDE -> Collections.emptyList();
          case SIMPLE -> Collections.singletonList(
              new TranslatableComponent("shulkerboxtooltip.hint.lootTable").setStyle(style));
          default -> Arrays.asList(
              new TranslatableComponent("shulkerboxtooltip.hint.lootTable.advanced").append(new TextComponent(": ")),
              new TextComponent(" " + blockEntityTag.getString("LootTable")).setStyle(style));
        };
      }
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
      text = new TranslatableComponent("container.shulkerbox.contains", itemCount);
    else
      text = new TranslatableComponent("container.shulkerbox.empty");
    tooltip.add(text.setStyle(style));
    return tooltip;
  }

  @Override
  public int getMaxRowSize(PreviewContext context) {
    return this.maxRowSize;
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
