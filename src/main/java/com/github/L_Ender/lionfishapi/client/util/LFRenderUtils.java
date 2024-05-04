package com.github.L_Ender.lionfishapi.client.util;

import com.github.L_Ender.lionfishapi.client.model.tools.AdvancedModelBox;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class LFRenderUtils {

    public static void matrixStackFromModel(PoseStack matrixStack, AdvancedModelBox AdvancedModelBox) {
        AdvancedModelBox parent = AdvancedModelBox.getParent();
        if (parent != null) matrixStackFromModel(matrixStack, parent);
        AdvancedModelBox.translateAndRotate(matrixStack);
    }

    public static Vec3 matrixStackFromModel(Entity entity, float entityYaw, AdvancedModelBox modelRenderer) {
        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(entity.getX(), entity.getY(), entity.getZ());
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((-entityYaw + 180)));
        matrixStack.scale(-1, -1, 1);
        matrixStack.translate(0, -1.5f, 0);
        LFRenderUtils.matrixStackFromModel(matrixStack, modelRenderer);
        PoseStack.Pose matrixEntry = matrixStack.last();
        Matrix4f matrix4f = matrixEntry.pose();
        Vector4f vec = new Vector4f(0, 0, 0, 1);
        vec.transform(matrix4f);
        return new Vec3(vec.x(), vec.y(), vec.z());
    }

}
