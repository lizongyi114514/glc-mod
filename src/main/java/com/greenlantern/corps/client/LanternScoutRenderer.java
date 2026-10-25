package com.greenlantern.corps.client;

import com.greenlantern.corps.entity.LanternScoutEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LanternScoutRenderer extends GeoEntityRenderer<LanternScoutEntity> {
    public LanternScoutRenderer(EntityRendererProvider.Context context) {
        super(context, new LanternScoutModel());
    }
}
