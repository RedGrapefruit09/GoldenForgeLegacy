package io.github.redgrapefruit09.goldenforge.item

import io.github.redgrapefruit09.goldenforge.core.MetalContainerSettings
import io.github.redgrapefruit09.goldenforge.core.MetalSettingsLoader
import io.github.redgrapefruit09.goldenforge.mixin.ItemStackAccessor
import io.github.redgrapefruit09.goldenforge.util.*
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.Entity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.Language
import net.minecraft.world.World

class SealedContainerItem(name: String) : Item(itemSettings.notStackable()) {
    private lateinit var resourceId: Identifier

    val settings by invocation<SealedContainerItem, MetalContainerSettings?> {
        val loaded = MetalSettingsLoader.pipeline.get(resourceId)
        MetalContainerSettings.get(loaded, resourceId.path)
    }

    init {
        resourceId = name.id
    }

    override fun inventoryTick(stack: ItemStack, world: World, entity: Entity, slot: Int, selected: Boolean) {
        super.inventoryTick(stack, world, entity, slot, selected)

        if (settings == null) return

        val data = SealedContainerItemData[stack]
        data.use(stack) {
            useTicks += BASE_USE_TICK_INCREASE + usage
        }

        if (data.useTicks >= settings!!.useMinutes * 12000) {
            (stack as ItemStackAccessor).setItem(Items.AIR)
        }
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, context)

        if (settings == null) return

        val data = SealedContainerItemData[stack]

        // Render inventory contents
        if (data.insides.isNotEmpty()) {
            tooltip += LiteralText("Contains:")
            var i = 1
            data.insides.forEach { (id, litres) ->
                tooltip += LiteralText("$i. $litres litres of ${Language.getInstance()[id.toTranslationKey("item")]}")
                ++i
            }
        } else {
            tooltip += LiteralText("Currently empty.")
        }

        tooltip.newLine()
        tooltip += LiteralText("Tier: ${settings!!.name}")
        tooltip += LiteralText("Temperature suppression: ${settings!!.withhold}")
        tooltip += LiteralText("Capacity: ${settings!!.capacity} litres")
        tooltip += LiteralText("Durability: ${data.useTicks}/${settings!!.useMinutes * 12000}")
    }

    companion object {
        const val BASE_USE_TICK_INCREASE = 1
    }
}

data class SealedContainerItemData(
    var insides: MutableMap<Identifier, Int> = mutableMapOf(),
    var useTicks: Int = 0
) : ItemData {
    override val nbtCategory: String = "SealedContainerData"

    override fun readNbt(nbt: NbtCompound) {
        useTicks = nbt.getInt("UseTicks")
        insides = readInsides(nbt)
    }

    private fun readInsides(nbt: NbtCompound): MutableMap<Identifier, Int> {
        // Read NbtLists
        val keysRaw = nbt.get("InsideKeys") as NbtList
        val valuesRaw = nbt.get("InsideValues") as NbtList

        // Checks
        if (keysRaw.size == 0 || valuesRaw.size == 0) return mutableMapOf()
        if (keysRaw.size != valuesRaw.size) throw RuntimeException("Bad NBT")

        // Read keys
        val keys = mutableListOf<Identifier>()
        keysRaw.forEach { key ->
            keys += Identifier.tryParse(key.asString()) ?: throw RuntimeException("Bad NBT")
        }

        // Read values
        val values = mutableListOf<Int>()
        valuesRaw.forEach { value ->
            values += value.asString().toInt()
        }

        // Construct pair list
        val pairs = mutableListOf<Pair<Identifier, Int>>()
        keys.forEachIndexed { index, key ->
            val value = values[index]
            pairs += Pair(key, value)
        }

        // Convert to map
        return pairs.toMap().toMutableMap()
    }

    override fun writeNbt(nbt: NbtCompound) {
        nbt.putInt("UseTicks", useTicks)
        writeInsides(nbt)
    }

    private fun writeInsides(nbt: NbtCompound) {
        val keys = insides.keys
        val values = insides.values

        val keysRaw = NbtList()
        val valuesRaw = NbtList()

        keys.forEach { id ->
            keysRaw += NbtString.of(id.toString())
        }

        values.forEach { value ->
            valuesRaw += NbtString.of(value.toString())
        }

        nbt.put("InsideKeys", keysRaw)
        nbt.put("InsideValues", valuesRaw)
    }

    override fun clearNbt(nbt: NbtCompound) {
        nbt.remove("InsideKeys")
        nbt.remove("InsideValues")
        nbt.remove("UseTicks")
    }

    override fun verifyNbt(nbt: NbtCompound): Boolean {
        return nbt.contains("UseTicks") && nbt.contains("InsideValues") && nbt.contains("UseTicks")
    }

    /**
     * Returns the amount of space currently used up
     */
    inline val usage: Int
        get() {
            var total = 0
            insides.forEach { (_, amount) -> total += amount }
            return total
        }

    /**
     * Transfers some amount of material into the container.
     */
    fun transfer(id: Identifier, amount: Int) {
        if (!insides.contains(id)) {
            insides[id] = amount
            return
        }

        insides[id] = insides[id]!! + amount
    }

    companion object {
        // Shortcut
        operator fun get(stack: ItemStack) = ItemData.get(::SealedContainerItemData, stack)
    }
}
