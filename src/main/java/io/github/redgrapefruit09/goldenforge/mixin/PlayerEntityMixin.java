package io.github.redgrapefruit09.goldenforge.mixin;

import io.github.redgrapefruit09.goldenforge.util.ModDamageSource;
import io.github.redgrapefruit09.goldenforge.util.PlayerEntityMixinAccess;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Temperature serialization
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityMixinAccess {
    @Shadow public abstract boolean damage(DamageSource source, float amount);

    private static final DamageSource TEMPERATURE = new ModDamageSource("high_temperature");

    private @Unique int temperature = 0;
    private @Unique int sinceLastTick = 0;

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void goldenforge$readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        temperature = nbt.getInt("Temperature");
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void goldenforge$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("Temperature", temperature);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void goldenforge$tick(CallbackInfo ci) {
        // Burn the player if the temperature is too high
        if (temperature > 40) {
            damage(TEMPERATURE, 2f);
        }

        ++sinceLastTick;

        if (sinceLastTick >= 20) {
            --temperature;
            sinceLastTick = 0;
        }

        temperature = MathHelper.clamp(temperature, 20, 100000);
    }

    @Override @Unique
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override @Unique
    public int getTemperature() {
        return this.temperature;
    }
}
