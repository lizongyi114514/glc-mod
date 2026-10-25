package com.greenlantern.corps.client;

import com.greenlantern.corps.entity.LanternScoutEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LanternScoutModel extends GeoModel<LanternScoutEntity> {
    @Override
    public ResourceLocation getModelResource(LanternScoutEntity animatable) {
        return new ResourceLocation("glc", "geo/entity/hal_corps.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LanternScoutEntity animatable) {
        return new ResourceLocation("glc", "textures/entity/hal_corps_" + animatable.variant() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(LanternScoutEntity animatable) {
        return new ResourceLocation("glc", "animations/entity/hal_corps.animation.json");
    }
}
