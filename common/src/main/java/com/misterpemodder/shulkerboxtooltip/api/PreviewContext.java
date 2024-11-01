package com.misterpemodder.shulkerboxtooltip.api;

import com.misterpemodder.shulkerboxtooltip.api.config.PreviewConfiguration;
import com.misterpemodder.shulkerboxtooltip.impl.PreviewContextImpl;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides information for item previews, such as the item stack and player that owns the stack (if present).
 *
 * @since 2.0.0
 */
public interface PreviewContext {
  /**
   * Creates a new {@link PreviewContext.Builder} instance.
   * <p>
   * This is the recommended way to create a {@link PreviewContext} instance as of 4.1.0.
   *
   * @param stack The stack to create the context with, may not be null.
   * @return a new {@link PreviewContext.Builder} instance.
   * @since 4.1.0
   */
  @Nonnull
  @Contract("_ -> new")
  static PreviewContext.Builder builder(ItemStack stack) {
    return new PreviewContextImpl.Builder(stack);
  }

  /**
   * Gets the item stack associated with this context.
   *
   * @return The item stack.
   * @since 3.1.0
   */
  @Nonnull
  ItemStack stack();

  /**
   * Gets the player associated with this context, or null if it does not exist.
   *
   * @return The owner of this item stack, may be null.
   * @since 3.1.0
   */
  @Nullable
  Player owner();

  /**
   * Provides access to the configuration in use for this context.
   *
   * @return the configuration.
   * @since 3.3.0
   */
  @Nonnull
  PreviewConfiguration config();

  /**
   * Returns the registry lookup for this context, if available.
   *
   * @return the registry lookup, or {@code null}.
   * @since 4.1.0
   */
  @Nullable
  HolderLookup.Provider registryLookup();

  /**
   * A builder for creating {@link PreviewContext} instances.
   *
   * @since 4.1.0
   */
  interface Builder {
    /**
     * Sets the owning player of the item stack.
     *
     * @param owner The owner of the item stack, may be null.
     * @return this builder instance for chaining.
     * @since 4.1.0
     */
    Builder withOwner(@Nullable Player owner);

    /**
     * Sets the registry lookup to use for deserialization of the item stack.
     * <p>
     * Defaults to the owner's registry if not set.
     *
     * @param registryLookup The registry lookup for the item stack, may be null.
     * @return this builder instance for chaining.
     * @since 4.1.0
     */
    Builder withRegistryLookup(@Nullable HolderLookup.Provider registryLookup);

    /**
     * Builds the {@link PreviewContext} instance.
     *
     * @return a new {@link PreviewContext} instance.
     * @since 4.1.0
     */
    @Nonnull
    PreviewContext build();
  }
}
