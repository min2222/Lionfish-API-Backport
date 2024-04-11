package com.github.L_Ender.lionfishapi.server.network;

import com.github.L_Ender.lionfishapi.server.animation.IAnimatedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class AnimationMessage {
    private int entityID;
    private int index;

    public AnimationMessage() {

    }

    public AnimationMessage(int entityID, int index) {
        this.entityID = entityID;
        this.index = index;
    }

    public static void write(final AnimationMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.entityID);
        buf.writeVarInt(message.index);
    }

    public static AnimationMessage read(final FriendlyByteBuf buf) {
        final AnimationMessage message = new AnimationMessage();
        message.entityID = buf.readVarInt();
        message.index = buf.readVarInt();
        return message;
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(AnimationMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                IAnimatedEntity entity = (IAnimatedEntity) Minecraft.getInstance().level.getEntity(message.entityID);
                if (entity != null) {
                    if (message.index == -1) {
                        entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
                    } else {
                        entity.setAnimation(entity.getAnimations()[message.index]);
                    }
                    entity.setAnimationTick(0);
                }
            });
            context.setPacketHandled(true);
        }
    }
}
