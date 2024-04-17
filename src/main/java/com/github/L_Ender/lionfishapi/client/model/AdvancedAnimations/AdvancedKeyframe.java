package com.github.L_Ender.lionfishapi.client.model.AdvancedAnimations;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public record AdvancedKeyframe(float timestamp, Vector3f target, AdvancedAnimationChannel.Interpolation interpolation) {
}
