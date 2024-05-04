package com.github.L_Ender.lionfishapi.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.github.L_Ender.lionfishapi.server.event.StandOnFluidEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;

@Mixin(Entity.class)
public class EntityMixin{

    @ModifyVariable(method = "move", ordinal = 1, index = 3, name = "vec32", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 fluidCollision(Vec3 original) {
        if (!((Entity) (Object) this instanceof LivingEntity entity))
            return original;

        if (original.y > 0)
            return original;

        Level level = entity.getCommandSenderWorld();

        double[][] offsets = {
                {0.5, 0, 0.5}, {0.5, 0, 0}, {0.5, -1, 0}, {0.5, 0, -0.5},
                {0, 0, 0.5}, {0, 0, 0}, {0, -1, 0}, {0, 0, -0.5},
                {-0.5, 0, 0.5}, {-0.5, 0, 0}, {-0.5, -1, 0}, {-0.5, 0, -0.5}
        };

        double highestValue = original.y;
        FluidState highestFluid = null;

        for (double[] offset : offsets) {
            BlockPos sourcePos = entity.blockPosition();
            BlockPos pos = new BlockPos(sourcePos.getX() + offset[0], sourcePos.getY() + offset[1], sourcePos.getZ() + offset[2]);

            FluidState fluidState = level.getFluidState(pos);

            if (fluidState.isEmpty())
                continue;

            VoxelShape shape = Shapes.block().move(pos.getX(), pos.getY() + fluidState.getOwnHeight(), pos.getZ());

            if (Shapes.joinIsNotEmpty(shape, Shapes.create(entity.getBoundingBox().inflate(0.5)), BooleanOp.AND)) {
                double height = shape.max(Direction.Axis.Y) - entity.getY() - 1;

                if (highestValue < height) {
                    highestValue = height;
                    highestFluid = fluidState;
                }
            }
        }

        if (highestFluid == null)
            return original;

        StandOnFluidEvent event = new StandOnFluidEvent(entity, highestFluid);

        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            entity.fallDistance = 0F;
            entity.setOnGround(true);

            return new Vec3(original.x, highestValue, original.z);
        }

        return original;
    }
}

