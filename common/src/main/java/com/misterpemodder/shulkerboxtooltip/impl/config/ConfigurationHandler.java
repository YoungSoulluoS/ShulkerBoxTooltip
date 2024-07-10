package com.misterpemodder.shulkerboxtooltip.impl.config;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorRegistry;
import com.misterpemodder.shulkerboxtooltip.impl.PluginManager;
import com.misterpemodder.shulkerboxtooltip.impl.color.ColorRegistryImpl;
import com.misterpemodder.shulkerboxtooltip.impl.config.Configuration.*;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.AutoTooltip;
import com.misterpemodder.shulkerboxtooltip.impl.config.annotation.Validator;
import com.misterpemodder.shulkerboxtooltip.impl.util.Key;
import com.misterpemodder.shulkerboxtooltip.impl.util.NbtType;
import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData.ValidationException;
import me.shedaniel.autoconfig.ConfigManager;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.gui.ConfigScreenProvider;
import me.shedaniel.autoconfig.gui.DefaultGuiProviders;
import me.shedaniel.autoconfig.gui.DefaultGuiTransformers;
import me.shedaniel.autoconfig.gui.registry.ComposedGuiRegistryAccess;
import me.shedaniel.autoconfig.gui.registry.DefaultGuiRegistryAccess;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.KeyCodeEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import me.shedaniel.clothconfig2.impl.builders.KeyCodeBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ConfigurationHandler {
  /**
   * Creates of copy of the passed config object
   *
   * @param source The source object.
   * @return The newly created object.
   */
  public static Configuration copyOf(Configuration source) {
    Configuration c = new Configuration();

    c.preview = PreviewCategory.copyFrom(source.preview);
    c.tooltip = TooltipCategory.copyFrom(source.tooltip);
    c.server = ServerCategory.copyFrom(source.server);
    if (ShulkerBoxTooltip.isClient()) {
      c.colors = ColorsCategory.copyFrom(source.colors);
      c.controls = ControlsCategory.copyFrom(source.controls);
    }
    return c;
  }

  public static Configuration register() {
    Configuration configuration = AutoConfig.register(Configuration.class, ShulkerBoxTooltipConfigSerializer::new)
        .getConfig();

    AutoConfig.getConfigHolder(Configuration.class).registerSaveListener((holder, config) -> {
      onSave();
      return InteractionResult.PASS;
    });
    if (ShulkerBoxTooltip.isClient())
      ClientOnly.registerGui();
    return configuration;
  }

  private static Optional<Component[]> splitTooltipKey(String key) {
    String[] lines = Language.getInstance().getOrDefault(key).split("\n");
    Component[] tooltip = new Component[lines.length];

    for (int i = 0, l = lines.length; i < l; ++i)
      tooltip[i] = new TextComponent(lines[i]);
    return Optional.of(tooltip);
  }

  public static void validate(Configuration config) throws ValidationException {
    runValidators(PreviewCategory.class, config.preview, "preview");
    runValidators(TooltipCategory.class, config.tooltip, "tooltip");
    runValidators(ServerCategory.class, config.server, "server");
    if (ShulkerBoxTooltip.isClient())
      ClientOnly.validate(config);
  }

  private static <T> void runValidators(Class<T> categoryClass, T category, String categoryName)
      throws ValidationException {
    try {
      for (Field field : categoryClass.getDeclaredFields()) {
        Validator validator = field.getAnnotation(Validator.class);

        if (validator == null)
          continue;
        field.setAccessible(true);

        Optional<Component> errorMsg = getValidatorFunction(validator).apply(field.get(category));

        if (errorMsg.isPresent())
          throw new ValidationException(
              "ShulkerBoxTooltip config field " + categoryName + "." + field.getName() + " is invalid: "
                  + Language.getInstance().getOrDefault(errorMsg.get().getString()));
      }
    } catch (ReflectiveOperationException | RuntimeException e) {
      throw new ValidationException(e);
    }
  }

  private static Function<Object, Optional<Component>> getValidatorFunction(Validator validator) {
    try {
      var constructor = validator.value().getDeclaredConstructor();

      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("Couldn't create config validator", e);
    }
  }

  @Environment(EnvType.CLIENT)
  public static void reinitClientSideSyncedValues(Configuration config) {
    config.server.clientIntegration = false;
    config.server.enderChestSyncType = EnderChestSyncType.NONE;
  }

  public static void onSave() {
    if (ShulkerBoxTooltip.savedConfig == null)
      return;

    ServerCategory serverCategory =
        ShulkerBoxTooltip.config == null ? new ServerCategory() : ShulkerBoxTooltip.config.server;

    ShulkerBoxTooltip.config = ConfigurationHandler.copyOf(ShulkerBoxTooltip.savedConfig);
    ShulkerBoxTooltip.config.server = serverCategory;
  }

  public static void readFromPacketBuf(Configuration config, FriendlyByteBuf buf) {
    CompoundTag compound = buf.readNbt();

    if (compound != null && compound.contains("server", NbtType.COMPOUND)) {
      CompoundTag serverTag = compound.getCompound("server");

      if (serverTag.contains("clientIntegration", NbtType.BYTE))
        config.server.clientIntegration = serverTag.getBoolean("clientIntegration");
      if (serverTag.contains("enderChestSyncType", NbtType.STRING))
        config.server.enderChestSyncType = getEnumFromName(EnderChestSyncType.class, EnderChestSyncType.NONE,
            serverTag.getString("enderChestSyncType"));
    }
  }

  public static void writeToPacketBuf(Configuration config, FriendlyByteBuf buf) {
    CompoundTag compound = new CompoundTag();
    CompoundTag serverTag = new CompoundTag();

    serverTag.putBoolean("clientIntegration", config.server.clientIntegration);
    serverTag.putString("enderChestSyncType", config.server.enderChestSyncType.name());
    compound.put("server", serverTag);

    buf.writeNbt(compound);
  }

  @SuppressWarnings({"ConstantConditions", "SameParameterValue"})
  private static <E extends Enum<E>> E getEnumFromName(Class<E> clazz, E defaultValue, String name) {
    if (clazz != null && name != null) {
      try {
        E e = Enum.valueOf(clazz, name);
        return e == null ? defaultValue : e;
      } catch (IllegalArgumentException ignored) {
      }
    }
    return defaultValue;
  }

  @Environment(EnvType.CLIENT)
  @SuppressWarnings("rawtypes")
  public static final class ClientOnly {
    private static final GuiRegistry defaultGuiRegistry = DefaultGuiTransformers.apply(
        DefaultGuiProviders.apply(new GuiRegistry()));

    public static void validate(Configuration config) throws ValidationException {
      runValidators(ColorsCategory.class, config.colors, "colors");
      if (config.controls.previewKey == null)
        config.controls.previewKey = Key.defaultPreviewKey();
      if (config.controls.fullPreviewKey == null)
        config.controls.fullPreviewKey = Key.defaultFullPreviewKey();
      if (config.controls.lockTooltipKey == null)
        config.controls.lockTooltipKey = Key.defaultLockTooltipKey();
      runValidators(ControlsCategory.class, config.controls, "controls");
    }

    private static void registerGui() {
      GuiRegistry registry = AutoConfig.getGuiRegistry(Configuration.class);

      // Auto tooltip handling
      registry.registerAnnotationTransformer(
          (guis, i13n, field, config, defaults, guiRegistry) -> guis.stream().peek(gui -> {
            if (gui instanceof TooltipListEntry<?> entry)
              entry.setTooltipSupplier(() -> splitTooltipKey(i13n + ".tooltip"));
          }).collect(Collectors.toList()), AutoTooltip.class);

      // Validators
      registry.registerAnnotationTransformer(
          (guis, i13n, field, config, defaults, guiRegistry) -> guis.stream().peek(gui -> {
            var validator = getValidatorFunction(field.getAnnotation(Validator.class));

            ((AbstractConfigListEntry<?>) gui).setErrorSupplier(() -> validator.apply(gui.getValue()));
          }).collect(Collectors.toList()), Validator.class);

      // Keybinding UI
      registry.registerTypeProvider(ClientOnly::buildKeybindingEntry, Key.class);

      // Colors UI
      registry.registerTypeProvider(ClientOnly::buildColorsCategory, ColorRegistry.class);
    }

    private static List<AbstractConfigListEntry> buildKeybindingEntry(String i18n, Field field, Object config,
        Object defaults, GuiRegistryAccess guiRegistry) {
      if (field.isAnnotationPresent(ConfigEntry.Gui.Excluded.class))
        return Collections.emptyList();
      KeyCodeBuilder builder = ConfigEntryBuilder.create().startKeyCodeField(new TranslatableComponent(i18n),
          Utils.getUnsafely(field, config, new Key(InputConstants.UNKNOWN)).get()).setDefaultValue(
          () -> ((Key) Utils.getUnsafely(field, defaults)).get());

      KeyCodeEntry entry = setKeySaveConsumer(builder,
          newValue -> Utils.getUnsafely(field, config, new Key(InputConstants.UNKNOWN)).set(newValue)).build();

      entry.setAllowMouse(false);
      return Collections.singletonList(entry);
    }

    /**
     * Builds the 'Colors' category of the GUI config screen.
     */
    private static List<AbstractConfigListEntry> buildColorsCategory(String i18n, Field field, Object config,
        Object defaults, GuiRegistryAccess guiRegistry) {
      List<AbstractConfigListEntry> guis = new ArrayList<>();

      ColorRegistry.Category defaultCategory = ColorRegistryImpl.INSTANCE.defaultCategory();

      // add the uncategorized color keys at the top
      defaultCategory.keys().entrySet().stream().map(entry -> colorKeyEntry(defaultCategory, entry)).forEachOrdered(
          guis::add);

      for (var categoryEntry : ColorRegistryImpl.INSTANCE.categories().entrySet()) {
        var id = categoryEntry.getKey();
        var category = categoryEntry.getValue();

        if (category == defaultCategory)
          continue;

        guis.add(ConfigEntryBuilder.create()
            .startSubCategory(
                new TranslatableComponent("shulkerboxtooltip.colors." + id.getNamespace() + "." + id.getPath()),
                category.keys().entrySet().stream().map(entry -> colorKeyEntry(category, entry)).toList())
            .build());
      }
      return guis;
    }

    private static AbstractConfigListEntry colorKeyEntry(ColorRegistry.Category category,
        Map.Entry<String, ColorKey> entry) {
      var colorKey = entry.getValue();

      return ConfigEntryBuilder.create().startColorField(
          new TranslatableComponent(category.keyUnlocalizedName(colorKey)), colorKey.rgb()).setDefaultValue(
          colorKey.defaultRgb()).setSaveConsumer(colorKey::setRgb).build();
    }

    /**
     * A hack function that calls setSaveConsumer() or setKeySaveConsumer() on the key code builder
     * depending on which is implemented by cloth-config.
     */
    private static KeyCodeBuilder setKeySaveConsumer(KeyCodeBuilder builder, Consumer<InputConstants.Key> consumer) {
      try {
        Method method = builder.getClass().getMethod("setSaveConsumer", Consumer.class);
        method.setAccessible(true);
        method.invoke(builder, consumer);
        return builder;
      } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException ignored) {
      }
      try {
        Method method = builder.getClass().getMethod("setKeySaveConsumer", Consumer.class);
        method.setAccessible(true);
        method.invoke(builder, consumer);
        return builder;
      } catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException ignored) {
      }
      ShulkerBoxTooltip.LOGGER.warn("Could not save keybinding entries from config GUI");
      return builder;
    }

    public static Screen makeConfigScreen(Screen parent) {
      PluginManager.loadColors();

      // We set our gui registry after the default one to override the default transformations.
      var registryAccess = new ComposedGuiRegistryAccess(defaultGuiRegistry,
          AutoConfig.getGuiRegistry(Configuration.class), new DefaultGuiRegistryAccess());

      //noinspection UnstableApiUsage
      return new ConfigScreenProvider<>((ConfigManager<Configuration>) AutoConfig.getConfigHolder(Configuration.class),
          registryAccess, parent).get();
    }
  }
}