package com.greenlantern.corps.client;

import com.greenlantern.corps.item.RingItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RingModel extends GeoModel<RingItem> {
    @Override
    public ResourceLocation getModelResource(RingItem animatable) {
        return new ResourceLocation("glc", "geo/entity/willpower_ring.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RingItem animatable) {
        return new ResourceLocation("glc", "textures/entity/willpower_ring.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RingItem animatable) {
        return new ResourceLocation("glc", "animations/entity/willpower_ring.animation.json");
    }
}
