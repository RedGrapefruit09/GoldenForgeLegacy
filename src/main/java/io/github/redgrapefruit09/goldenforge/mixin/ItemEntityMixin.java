package io.github.redgrapefruit09.goldenforge.mixin;

import io.github.redgrapefruit09.goldenforge.item.MetalMaterialItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A small injection required for raw material to operate.
 *
 * This clears the stack's NBT of the flag and basically says that the temperature can be applied again.
 */
@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    // Inject into most constructors

    @Inject(method = "<init>(Lnet/minecraft/entity/ItemEntity;)V", at = @At("TAIL"))
    private void goldenforge$init1(ItemEntity itemEntity, CallbackInfo ci) {
        initCommon(itemEntity.getStack());
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    private void goldenforge$init3(World world, double d, double e, double f, ItemStack itemStack, CallbackInfo ci) {
        initCommon(itemStack);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;DDD)V", at = @At("TAIL"))
    private void goldenforge$init4(World world, double d, double e, double f, ItemStack itemStack, double g, double h, double i, CallbackInfo ci) {
        initCommon(itemStack);
    }

    @Unique private void initCommon(ItemStack stack) {
        // Check if the item is of raw material
        if (!(stack.getItem() instanceof MetalMaterialItem)) return;

        // Clear the stack's NBT of the flag
        final NbtCompound nbt = stack.getOrCreateSubNbt("MaterialData");
        nbt.remove("TemperatureApplied");
    }
}
