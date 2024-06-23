package com.misterpemodder.shulkerboxtooltip.neoforge;

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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, modid = ShulkerBoxTooltip.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ShulkerBoxTooltipClientImpl extends ShulkerBoxTooltipClient {
  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      ShulkerBoxTooltipClient.init();

      // Register the config screen
      ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
          () -> (client, parent) -> new ConfigScreen<>(parent, ShulkerBoxTooltip.configTree,
              ShulkerBoxTooltip.savedConfig, ConfigurationHandler::saveToFile));

      // ItemStack -> PreviewTooltipComponent
      NeoForge.EVENT_BUS.addListener(ShulkerBoxTooltipClientImpl::onGatherTooltipComponents);
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
      var data = new PreviewTooltipComponent(ShulkerBoxTooltipApi.getPreviewProviderForStack(context.stack()), context);

      elements.add(1, Either.right(data));
    }

    // Add the tooltip hints at the end of the tooltip
    ShulkerBoxTooltipClient.modifyStackTooltip(context.stack(),
        toAdd -> toAdd.stream().map(Either::<FormattedText, TooltipComponent>left).forEach(elements::add));
  }
}
