package com.github.L_Ender.lionfishapi.client.event;

import com.github.L_Ender.lionfishapi.client.screen.OptifineWarningScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ClientSetUp {

    private static boolean firstTitleScreenShown = false;
    public static boolean optifinePresent = false;

    @SubscribeEvent
    public void showOptifineWarning(ScreenEvent.Init.Post event) {
        if (firstTitleScreenShown || !(event.getScreen() instanceof TitleScreen)) return;

        if (optifinePresent) {
            Minecraft.getInstance().setScreen(new OptifineWarningScreen(event.getScreen()));
        }

        firstTitleScreenShown = true;
    }
}
