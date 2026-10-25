package com.greenlantern.corps.client;

import com.greenlantern.corps.item.RingItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RingRenderer extends GeoItemRenderer<RingItem> {
    public RingRenderer() {
        super(new RingModel());
    }
}
