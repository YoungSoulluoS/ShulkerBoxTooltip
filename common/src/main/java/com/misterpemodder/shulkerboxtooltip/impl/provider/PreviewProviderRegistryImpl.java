package com.misterpemodder.shulkerboxtooltip.impl.provider;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProviderRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PreviewProviderRegistryImpl implements PreviewProviderRegistry {
  private boolean locked;
  private final BiMap<ResourceLocation, PreviewProvider> providerIds;
  private final Map<Item, PreviewProvider> providerItems;

  public static final PreviewProviderRegistryImpl INSTANCE = new PreviewProviderRegistryImpl();

  private PreviewProviderRegistryImpl() {
    this.locked = true;
    this.providerIds = HashBiMap.create();
    this.providerItems = new HashMap<>();
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  @Override
  public void register(ResourceLocation id, PreviewProvider provider, Iterable<Item> items) {
    if (this.locked)
      throw new IllegalStateException(
          "attempted to register PreviewProvider outside ShulkerBoxTooltipApi.registerProviders");
    if (this.providerIds.containsValue(provider))
      throw new IllegalStateException("attempted to register PreviewProvider twice");
    if (this.providerIds.containsKey(id))
      ShulkerBoxTooltip.LOGGER.warn("registering PreviewProvider with an existing id: " + id);
    int priority = provider.getPriority();

    this.providerIds.put(id, provider);
    for (Item item : items) {
      PreviewProvider previousProvider = this.providerItems.get(item);

      if (previousProvider == null) {
        this.providerItems.put(item, provider);
      } else {
        ResourceLocation previousId = this.getId(previousProvider);
        ResourceLocation itemId = Registry.ITEM.getKey(item);

        if (priority > previousProvider.getPriority()) {
          ShulkerBoxTooltip.LOGGER.info(
              "overriding preview provider " + previousId + " with " + id + " for item " + itemId);
          this.providerItems.put(item, provider);
        } else {
          ShulkerBoxTooltip.LOGGER.info(
              "overriding preview provider " + id + " with " + previousId + " for item " + itemId);
        }
      }
    }
  }

  @Override
  public void register(ResourceLocation id, PreviewProvider provider, Item... items) {
    this.register(id, provider, Arrays.asList(items));
  }

  @Override
  public PreviewProvider get(ResourceLocation id) {
    return this.providerIds.get(id);
  }

  @Override
  public PreviewProvider get(ItemStack stack) {
    return this.providerItems.get(stack.getItem());
  }

  @Override
  public PreviewProvider get(Item item) {
    return this.providerItems.get(item);
  }

  @Override
  public ResourceLocation getId(PreviewProvider provider) {
    return this.providerIds.inverse().get(provider);
  }

  @Override
  @Nonnull
  public Set<Item> getItems(PreviewProvider provider) {
    ImmutableSet.Builder<Item> builder = ImmutableSet.builder();

    for (Map.Entry<Item, PreviewProvider> entry : this.providerItems.entrySet())
      if (entry.getValue() == provider)
        builder.add(entry.getKey());
    return builder.build();
  }

  @Override
  @Nonnull
  public Set<PreviewProvider> getProviders() {
    return this.providerIds.values();
  }

  @Override
  @Nonnull
  public Set<ResourceLocation> getIds() {
    return this.providerIds.keySet();
  }
}
