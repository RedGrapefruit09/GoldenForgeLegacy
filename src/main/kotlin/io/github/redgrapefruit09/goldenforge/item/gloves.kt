package io.github.redgrapefruit09.goldenforge.item

import io.github.redgrapefruit09.goldenforge.core.MetalGloveSettings
import io.github.redgrapefruit09.goldenforge.core.MetalSettingsLoader
import io.github.redgrapefruit09.goldenforge.mixin.ItemStackAccessor
import io.github.redgrapefruit09.goldenforge.util.*
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World

class GlovesItem(name: String) : Item(itemSettings) {

    private lateinit var resourceId: Identifier

    private val settings by invocation<GlovesItem, MetalGloveSettings?> { // init via lazy
        val global = MetalSettingsLoader.pipeline.get(resourceId)
        MetalGloveSettings.get(global, resourceId.path)
    }

    init {
        resourceId = name.id
    }

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, world, entity, slot, selected)

        // Only apply to players, lock till world load
        if (entity !is PlayerEntity) return
        if (settings == null) return

        val data = GlovesItemData[stack]

        // Increment ticks
        data.use(stack) { useTicks++ }

        if (data.useTicks >= settings!!.useMinutes * 12000) {
            (stack as ItemStackAccessor).setItem(Items.AIR)
        }

        // Decrease temperature for the player
        entity.temperature -= settings!!.withhold
        entity.temperature = MathHelper.clamp(entity.temperature, 20, 1000000) // clamp for safety
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)

        // Lock display before resource load / world init
        if (settings == null) return

        tooltip += LiteralText("Tier: ${settings!!.name}")
        tooltip += LiteralText("Temperature suppression: ${settings!!.withhold}")
        tooltip += LiteralText("Durability: ${GlovesItemData[stack].useTicks}/${settings!!.useMinutes * 12000}")
    }
}

data class GlovesItemData(
    var useTicks: Int = 0
) : ItemData {
    override val nbtCategory: String = "GlovesData"

    override fun readNbt(nbt: NbtCompound) {
        useTicks = nbt.getInt("UseTicks")
    }

    override fun writeNbt(nbt: NbtCompound) {
        nbt.putInt("UseTicks", useTicks)
    }

    override fun clearNbt(nbt: NbtCompound) {
        nbt.remove("UseTicks")
    }

    override fun verifyNbt(nbt: NbtCompound): Boolean {
        return nbt.contains("UseTicks")
    }

    companion object {
        // Shortcut
        operator fun get(stack: ItemStack): GlovesItemData = ItemData.get(::GlovesItemData, stack)
    }
}
