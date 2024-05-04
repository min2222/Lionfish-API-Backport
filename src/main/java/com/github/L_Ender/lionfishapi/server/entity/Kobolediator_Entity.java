package com.github.L_Ender.lionfishapi.server.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;


public class Kobolediator_Entity extends Internal_Animation_Monster {
    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState sleepAnimationState = new AnimationState();
    public AnimationState awakeAnimationState = new AnimationState();
    public AnimationState sword1AnimationState = new AnimationState();
    public AnimationState sword2AnimationState = new AnimationState();
    public AnimationState chargeprepareAnimationState = new AnimationState();
    public AnimationState chargeAnimationState = new AnimationState();
    public AnimationState chargeendAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();
    private int earthquake_cooldown = 0;
    public static final int EARTHQUAKE_COOLDOWN = 80;

    private int charge_cooldown = 0;
    public static final int CHARGE_COOLDOWN = 160;

    public Kobolediator_Entity(EntityType entity, Level world) {
        super(entity, world);
        this.xpReward = 35;
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }
    
    @Override
    public float getStepHeight() 
    {
    	return 1.25F;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D, 80));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(2, new InternalMoveGoal(this,false,1.0D));


        this.goalSelector.addGoal(1, new InternalAttackGoal(this,0,3,0,50,15,12){
            @Override
            public boolean canUse() {
                return super.canUse() && Kobolediator_Entity.this.getRandom().nextFloat() * 100.0F < 16f && Kobolediator_Entity.this.earthquake_cooldown <= 0;
            }
            @Override
            public void stop() {
                super.stop();
                Kobolediator_Entity.this.earthquake_cooldown = EARTHQUAKE_COOLDOWN;
            }
        });
        this.goalSelector.addGoal(1, new InternalAttackGoal(this,0,4,0,100,64,8));

        //chargePrepare
        this.goalSelector.addGoal(1, new InternalAttackGoal(this,0,5,6,40,30,15) {
            @Override
            public boolean canUse() {
                return super.canUse() && Kobolediator_Entity.this.getRandom().nextFloat() * 100.0F < 9f && Kobolediator_Entity.this.charge_cooldown <= 0;
            }
        });

        this.goalSelector.addGoal(1, new InternalStateGoal(this,6,6,7,30,0){
            @Override
            public void tick() {
                if(this.entity.isOnGround()){
                    Vec3 vector3d = entity.getDeltaMovement();
                    float f = entity.getYRot() * ((float)Math.PI / 180F);
                    Vec3 vector3d1 = new Vec3(-Mth.sin(f), entity.getDeltaMovement().y, Mth.cos(f)).scale(0.7D).add(vector3d.scale(0.5D));
                    entity.setDeltaMovement(vector3d1.x, entity.getDeltaMovement().y, vector3d1.z);
                }
            }
        });
        this.goalSelector.addGoal(0, new InternalAttackGoal(this,6,7,0,40,40,5) {

            @Override
            public void stop() {
                super.stop();
                Kobolediator_Entity.this.charge_cooldown = CHARGE_COOLDOWN;
            }
        });
        this.goalSelector.addGoal(0, new InternalStateGoal(this,7,7,0,40,40) {
            @Override
            public void stop() {
                super.stop();
                Kobolediator_Entity.this.charge_cooldown = CHARGE_COOLDOWN;
            }
        });
        this.goalSelector.addGoal(1, new InternalStateGoal(this,1,1,0,0,0){
            @Override
            public void tick() {
                entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);
            }
        });

        this.goalSelector.addGoal(0, new InternalAttackGoal(this,1,2,0,70,0,8));
    }

    public static AttributeSupplier.Builder kobolediator() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28F)
                .add(Attributes.ATTACK_DAMAGE, 14)
                .add(Attributes.MAX_HEALTH, 180)
                .add(Attributes.ARMOR, 10)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (this.isSleep() && !source.isBypassInvul()) {
            return false;
        }
        return super.hurt(source, damage);
    }

    protected int decreaseAirSupply(int air) {
        return air;
    }


    public boolean causeFallDamage(float p_148711_, float p_148712_, DamageSource p_148713_) {
        return false;
    }

    public AnimationState getAnimationState(String input) {
        if (input == "sleep") {
            return this.sleepAnimationState;
        } else if (input == "awake") {
            return this.awakeAnimationState;
        } else if (input == "sword1") {
            return this.sword1AnimationState;
        } else if (input == "sword2") {
            return this.sword2AnimationState;
        } else if (input == "idle") {
                return this.idleAnimationState;
        } else if (input == "charge_prepare") {
            return this.chargeprepareAnimationState;
        } else if (input == "charge") {
            return this.chargeAnimationState;
        } else if (input == "charge_end") {
            return this.chargeendAnimationState;
        } else if (input == "death") {
            return this.deathAnimationState;
        }else {
            return new AnimationState();
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public boolean isSleep() {
        return this.getAttackState() == 1 || this.getAttackState() == 2;
    }

    public void setSleep(boolean sleep) {
        this.setAttackState(sleep ? 1 : 0);
    }

    public boolean canBeSeenAsEnemy() {
        return !this.isSleep() && super.canBeSeenAsEnemy();
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_29678_, DifficultyInstance p_29679_, MobSpawnType p_29680_, @Nullable SpawnGroupData p_29681_, @Nullable CompoundTag p_29682_) {
        this.setSleep(true);
        return super.finalizeSpawn(p_29678_, p_29679_, p_29680_, p_29681_, p_29682_);
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_) {
        if (ATTACK_STATE.equals(p_21104_)) {
            if (this.level.isClientSide)
                switch (this.getAttackState()) {
                    case 0 -> this.stopAllAnimationStates();
                    case 1 -> {
                        this.stopAllAnimationStates();
                        this.sleepAnimationState.startIfStopped(this.tickCount);
                    }
                    case 2 -> {
                        this.stopAllAnimationStates();
                        this.awakeAnimationState.startIfStopped(this.tickCount);
                    }
                    case 3 -> {
                        this.stopAllAnimationStates();
                        this.sword1AnimationState.startIfStopped(this.tickCount);
                    }
                    case 4 -> {
                        this.stopAllAnimationStates();
                        this.sword2AnimationState.startIfStopped(this.tickCount);
                    }
                    case 5 -> {
                        this.stopAllAnimationStates();
                        this.chargeprepareAnimationState.startIfStopped(this.tickCount);
                    }
                    case 6 -> {
                        this.stopAllAnimationStates();
                        this.chargeAnimationState.startIfStopped(this.tickCount);
                    }
                    case 7 -> {
                        this.stopAllAnimationStates();
                        this.chargeendAnimationState.startIfStopped(this.tickCount);
                    }
                    case 8 -> {
                        this.stopAllAnimationStates();
                        this.deathAnimationState.startIfStopped(this.tickCount);
                    }
                }
        }

        super.onSyncedDataUpdated(p_21104_);
    }

    public void stopAllAnimationStates() {
        this.sleepAnimationState.stop();
        this.awakeAnimationState.stop();
        this.sword1AnimationState.stop();
        this.sword2AnimationState.stop();
        this.chargeprepareAnimationState.stop();
        this.chargeAnimationState.stop();
        this.chargeendAnimationState.stop();
        this.deathAnimationState.stop();
    }



    public void die(DamageSource p_21014_) {
        super.die(p_21014_);
        this.setAttackState(8);
    }

    public int deathtimer(){
        return 60;
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("is_Sleep", isSleep());
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setSleep(compound.getBoolean("is_Sleep"));
    }


    public void tick() {
        super.tick();
        if (this.level.isClientSide()) {
            this.animateWhen(this.idleAnimationState, !this.isMoving() && this.getAttackState() == 0, this.tickCount);
        }
        if (earthquake_cooldown > 0) earthquake_cooldown--;
        if (charge_cooldown > 0) charge_cooldown--;

    }
    
    public void animateWhen(AnimationState state, boolean p_252220_, int p_249486_) {
        if (p_252220_) {
        	state.startIfStopped(p_249486_);
        } else {
        	state.stop();
        }
     }
    
    public boolean isMoving() {
    	return this.animationSpeed > 1.0E-5F;
    }

    public void aiStep() {
        super.aiStep();

    }

    private void Makeparticle(float size,float vec, float math) {
        if (!this.level.isClientSide) {
            for (int i1 = 0; i1 < 80 + random.nextInt(12); i1++) {
                double DeltaMovementX = getRandom().nextGaussian() * 0.07D;
                double DeltaMovementY = getRandom().nextGaussian() * 0.07D;
                double DeltaMovementZ = getRandom().nextGaussian() * 0.07D;
                float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
                float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
                float angle = (0.01745329251F * this.yBodyRot) + i1;
                double extraX = size * Mth.sin((float) (Math.PI + angle));
                double extraY = 0.3F;
                double extraZ = size * Mth.cos(angle);
                double theta = (yBodyRot) * (Math.PI / 180);
                theta += Math.PI / 2;
                double vecX = Math.cos(theta);
                double vecZ = Math.sin(theta);
                int hitX = Mth.floor(getX() + vec * vecX + extraX);
                int hitY = Mth.floor(getY());
                int hitZ = Mth.floor(getZ() + vec * vecZ + extraZ);
                BlockPos hit = new BlockPos(hitX, hitY, hitZ);
                BlockState block = this.level.getBlockState(hit.below());
                if (block.getRenderShape() != RenderShape.INVISIBLE) {
                    ((ServerLevel) this.level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, block), getX() + vec * vecX + extraX + f * math, this.getY() + extraY, getZ() + vec * vecZ + extraZ + f1 * math,1, DeltaMovementX, DeltaMovementY, DeltaMovementZ,0);
                }
            }
        }
    }

    private void AreaAttack(float range, float height, float arc, float damage, int shieldbreakticks) {
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = this.yBodyRot % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ()) + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                if (!isAlliedTo(entityHit) && !(entityHit instanceof Kobolediator_Entity) && entityHit != this) {
                    entityHit.hurt(DamageSource.mobAttack(this), (float) (this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damage));
                    if (entityHit instanceof Player && entityHit.isBlocking() && shieldbreakticks > 0) {
                        disableShield(entityHit, shieldbreakticks);
                    }
                }
            }
        }
    }



    public boolean removeWhenFarAway(double p_21542_) {
        return false;
    }

    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    protected boolean canRide(Entity p_31508_) {
        return false;
    }

}





