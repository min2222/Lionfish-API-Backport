package com.github.L_Ender.lionfishapi.mixin.client;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.L_Ender.lionfishapi.client.event.EventGetFluidRenderType;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

@Mixin(ItemBlockRenderTypes.class)
public class ItemBlockRenderTypesMixin  {


    @Inject(at = @At("TAIL"), remap = true, cancellable = true,
            method = "Lnet/minecraft/client/renderer/ItemBlockRenderTypes;getRenderLayer(Lnet/minecraft/world/level/material/FluidState;)Lnet/minecraft/client/renderer/RenderType;")
    private static void lionfish_getFluidRenderLayer(FluidState fluidState, CallbackInfoReturnable<RenderType> cir) {
        EventGetFluidRenderType event = new EventGetFluidRenderType(fluidState, cir.getReturnValue());
        MinecraftForge.EVENT_BUS.post(event);
        if(event.getResult() == Event.Result.ALLOW){
            cir.setReturnValue(event.getRenderType());
        }
    }
}
