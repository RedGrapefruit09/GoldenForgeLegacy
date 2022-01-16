package io.github.redgrapefruit09.goldenforge.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

// Allows resetting stack item
@Mixin(ItemStack.class)
public interface ItemStackAccessor {
    @Accessor("item") @Mutable
    void setItem(Item item);
}
