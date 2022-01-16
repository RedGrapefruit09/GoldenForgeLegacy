package io.github.redgrapefruit09.goldenforge.util

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList

// Fixes the SidedInventory bullshit

interface ImplementedSidedInventory : SidedInventory {
    val items: DefaultedList<ItemStack>

    /**
     * Returns the inventory size.
     */
    override fun size(): Int = items.size

    /**
     * Checks if the inventory is empty.<br></br>
     *
     * @return True if this inventory has only empty stacks, false otherwise.
     */
    override fun isEmpty(): Boolean {
        for (i in 0 until size()) {
            val stack = getStack(i)
            if (!stack.isEmpty) {
                return false
            }
        }
        return true
    }

    /**
     * Retrieves the item in the slot.
     */
    override fun getStack(slot: Int): ItemStack = items[slot]

    /**
     * Removes items from an inventory slot.
     *
     * @param slot  The slot to remove from.
     * @param count How many items to remove. If there are less items in the slot than what are requested,
     * takes all items in that slot.
     */
    override fun removeStack(slot: Int, count: Int): ItemStack {
        val result = Inventories.splitStack(items, slot, count)
        if (!result.isEmpty) {
            markDirty()
        }
        return result
    }

    /**
     * Removes all items from an inventory slot.
     *
     * @param slot The slot to remove from.
     */
    override fun removeStack(slot: Int): ItemStack = Inventories.removeStack(items, slot)

    /**
     * Replaces the current stack in an inventory slot with the provided stack.
     *
     * @param slot  The inventory slot of which to replace the [ItemStack].
     * @param stack The replacing [ItemStack]. If the stack is too big for
     * this inventory ([Inventory.getMaxCountPerStack]),
     * it gets resized to this inventory's maximum amount.
     */
    override fun setStack(slot: Int, stack: ItemStack) {
        items[slot] = stack
        if (stack.count > maxCountPerStack) {
            stack.count = maxCountPerStack
        }
    }

    /**
     * Clears the inventory.
     */
    override fun clear() = items.clear()

    /**
     * Marks the state as dirty.<br></br>
     * Must be called after changes in the inventory, so that the game can properly save.<br></br>
     * the inventory contents and notify neighboring blocks of inventory changes.
     */
    override fun markDirty() = Unit

    /**
     * Returns whether the player can use this inventory
     *
     * @return True/false
     */
    override fun canPlayerUse(player: PlayerEntity): Boolean = true
}
