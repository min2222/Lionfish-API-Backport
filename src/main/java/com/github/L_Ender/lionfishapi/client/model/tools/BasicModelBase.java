package com.github.L_Ender.lionfishapi.client.model.tools;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public abstract class BasicModelBase<T extends Entity> extends EntityModel<T> {
    public int texWidth = 64;
    public int texHeight = 32;
    public final List<BasicModelPart> boxList = Lists.newArrayList();

    protected BasicModelBase() {
        this(RenderType::entityCutoutNoCull);
    }

    @Override
    public void renderToBuffer(PoseStack p_103013_, VertexConsumer p_103014_, int p_103015_, int p_103016_, float p_103017_, float p_103018_, float p_103019_, float p_103020_) {
        this.parts().forEach((p_103030_) -> {
            p_103030_.render(p_103013_, p_103014_, p_103015_, p_103016_, p_103017_, p_103018_, p_103019_, p_103020_);
        });
    }

    protected BasicModelBase(Function<ResourceLocation, RenderType> p_102613_) {
        super(p_102613_);
    }

    public abstract Iterable<BasicModelPart> parts();

    public void accept(BasicModelPart modelRenderer) {
        boxList.add(modelRenderer);
    }

    public abstract void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_);

    public void prepareMobModel(T p_102614_, float p_102615_, float p_102616_, float p_102617_) {
    }
}
