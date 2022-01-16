package io.github.redgrapefruit09.goldenforge.item

import io.github.redgrapefruit09.goldenforge.core.MetalSettings
import io.github.redgrapefruit09.goldenforge.core.MetalSettingsLoader
import io.github.redgrapefruit09.goldenforge.util.*
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.world.World

// Material increases temperature highly
class MetalMaterialItem(name: String) : Item(itemSettings.notStackable()) {
    private lateinit var resourceId: Identifier

    private val settings by invocation<MetalMaterialItem, MetalSettings?> {
        MetalSettingsLoader.pipeline.get(resourceId)
    }

    init {
        resourceId = name.id
    }

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, world, entity, slot, selected)
        // Only apply to players, lock till world load
        if (entity !is PlayerEntity || settings == null || !selected) return

        // Check if the stack NBT contains a flag notifying that the temperature has already been applied
        val nbt = stack.getOrCreateSubNbt("MaterialData")
        if (!nbt.contains("TemperatureApplied")) {
            val applied = settings!!.production.materialTemperature
            if (entity.temperature < applied) {
                entity.temperature = settings!!.production.materialTemperature
            }
            nbt.putBoolean("TemperatureApplied", true)
        }
    }
}
