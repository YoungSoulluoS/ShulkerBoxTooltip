package com.misterpemodder.shulkerboxtooltip.forge;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltipClient;
import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.ShulkerBoxTooltipApi;
import com.misterpemodder.shulkerboxtooltip.impl.config.ConfigurationHandler;
import com.misterpemodder.shulkerboxtooltip.impl.config.gui.ConfigScreen;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewClientTooltipComponent;
import com.misterpemodder.shulkerboxtooltip.impl.tooltip.PreviewTooltipComponent;
import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ShulkerBoxTooltip.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ShulkerBoxTooltipClientImpl extends ShulkerBoxTooltipClient {
  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      ShulkerBoxTooltipClient.init();

      // Register the config screen
      ShulkerBoxTooltipImpl.INSTANCE.context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
          () -> new ConfigScreenHandler.ConfigScreenFactory(
              (client, parent) -> new ConfigScreen<>(parent, ShulkerBoxTooltip.configTree,
                  ShulkerBoxTooltip.savedConfig, ConfigurationHandler::saveToFile)));

      // ItemStack -> PreviewTooltipComponent
      MinecraftForge.EVENT_BUS.addListener(ShulkerBoxTooltipClientImpl::onGatherTooltipComponents);
    });
  }

  @SubscribeEvent
  public static void onRegisterTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
    // PreviewTooltipComponent -> PreviewClientTooltipComponent conversion
    event.register(PreviewTooltipComponent.class, PreviewClientTooltipComponent::new);
  }

  private static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
    var context = PreviewContext.builder(event.getItemStack()).withOwner(
        ShulkerBoxTooltipClient.client == null ? null : ShulkerBoxTooltipClient.client.player).build();
    var elements = event.getTooltipElements();

    // Add the preview window at the beginning of the tooltip
    if (ShulkerBoxTooltipApi.isPreviewAvailable(context)) {
      var data = new PreviewTooltipComponent(
          ShulkerBoxTooltipApi.getPreviewProviderForStackWithOverrides(context.stack()), context);

      elements.add(1, Either.right(data));
    }

    // Add the tooltip hints at the end of the tooltip
    ShulkerBoxTooltipClient.modifyStackTooltip(context.stack(),
        toAdd -> toAdd.stream().map(Either::<FormattedText, TooltipComponent>left).forEach(elements::add));
  }
}
