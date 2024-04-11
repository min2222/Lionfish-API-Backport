package com.github.L_Ender.lionfishapi;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = LionfishAPI.MODID, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {


    public void init() {
       // FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientLayerEvent::onAddLayers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupParticles);
    }

    public void setupParticles(RegisterParticleProvidersEvent registry) {
    }

    public void clientInit() {
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }

}