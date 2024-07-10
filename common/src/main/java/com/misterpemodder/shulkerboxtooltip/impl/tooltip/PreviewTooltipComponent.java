package com.misterpemodder.shulkerboxtooltip.impl.tooltip;

import com.misterpemodder.shulkerboxtooltip.api.PreviewContext;
import com.misterpemodder.shulkerboxtooltip.api.provider.PreviewProvider;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record PreviewTooltipComponent(PreviewProvider provider, PreviewContext context) implements TooltipComponent {
}
