package com.misterpemodder.shulkerboxtooltip.api.config;

/**
 * Provides a view over the mod's configuration
 *
 * @since 3.3.0
 */
public enum ItemStackMergingStrategy {
  /**
   * Ignore component data when merging item stacks.
   *
   * @since 3.3.0
   */
  IGNORE,
  /**
   * Merge regardless of component data but use the NBT of the first item stack.
   *
   * @since 3.3.0
   */
  FIRST_ITEM,
  /**
   * Do not merge stacks that differ in component data.
   *
   * @since 3.3.0
   */
  SEPARATE;

  @Override
  public String toString() {
    return "shulkerboxtooltip.config.item_stack_merging_strategy." + this.name().toLowerCase();
  }
}
