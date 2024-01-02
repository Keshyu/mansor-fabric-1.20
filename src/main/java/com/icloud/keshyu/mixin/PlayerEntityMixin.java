package com.icloud.keshyu.mixin;

import com.icloud.keshyu.MansorMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow
    protected HungerManager hungerManager;
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "increaseTravelMotionStats", at = @At("HEAD"))
    public void increaseTravelMotionStats(double dx, double dy, double dz, CallbackInfo ci) {
        // Just living is tiring :/
        this.addExhaustion(0.01f * 0.2f);

        if (this.getVehicle() instanceof BoatEntity) {
            int i = Math.round((float) Math.sqrt(dx * dx + dz * dz) * 100.0f);
            if (i > 0) {
                // Ok, but rowing is *reeeealy* hard
                this.addExhaustion(0.2f * (float)i * 0.01f);
            }
        }

        if (this.hasVehicle()) { return; }

        if (this.isClimbing()) {
            int i = (int) Math.round(dy * 100.0);
            // Uhm, climbing is quite hard actually
            if (i > 0) {
                this.addExhaustion(0.2f * (float)i * 0.01f);
            }
        } else if (this.isOnGround()) {
            int i = Math.round((float) Math.sqrt(dx * dx + dz * dz) * 100.0f);
            if (i > 0) {
                if (this.isInSneakingPose()) {
                    // Sneaking is as hard as running ):[ (- my duck legs)
                    this.addExhaustion(0.1f * (float)i * 0.01f);
                } else if (!this.isSprinting()) {
                    // Walking is a teeny bit tiring
                    this.addExhaustion(0.02f * (float)i * 0.01f);
                }
            }
        }

        MansorMod.LOGGER.info("Tire: {}; Food: {}",
                hungerManager.getExhaustion(),
                hungerManager.getSaturationLevel());
    }

    @Shadow
    public void addExhaustion(float exhaustion) {}
}
