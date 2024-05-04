package com.github.L_Ender.lionfishapi.client.model.AdvancedAnimations;

import com.mojang.math.Vector3f;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record AdvancedKeyframe(float timestamp, Vector3f target, AdvancedAnimationChannel.Interpolation interpolation) {
}
