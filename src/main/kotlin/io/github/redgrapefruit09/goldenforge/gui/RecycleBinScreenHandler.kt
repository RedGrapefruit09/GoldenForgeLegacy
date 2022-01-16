package io.github.redgrapefruit09.goldenforge.gui

import com.redgrapefruit.redmenu.redmenu.standard.MenuScreenHandler
import io.github.redgrapefruit09.goldenforge.block.RecycleBinBlock
import io.github.redgrapefruit09.goldenforge.registry.MenuRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerListener

/**
 * A screen handler for the recycle bin
 */
class RecycleBinScreenHandler(syncId: Int, playerInventory: PlayerInventory, inventory: Inventory) : MenuScreenHandler(
    syncId,
    playerInventory,
    inventory,
    RecycleBinBlock.INVENTORY_SIZE,
    MenuRegistry.RECYCLE_BIN_TYPE) {

    constructor(syncId: Int, playerInventory: PlayerInventory) : this(syncId,
        playerInventory,
        SimpleInventory(RecycleBinBlock.INVENTORY_SIZE))

    override fun onSlotInit(inventory: Inventory, playerInventory: PlayerInventory) {
        // Add player slots and 9 custom slots
        var index = 0
        for (y in 0..2) {
            for (x in 0..2) {
                addGridSlot(inventory, index, x, y)
                ++index
            }
        }

        addPlayerInventorySlots(playerInventory)
        addPlayerHotbarSlots(playerInventory)
    }

    override fun onListenerInit() {
        addListener(Listener())
    }
}

// This listener empties the slots in the recycle bin, emulating the effect that the items disappear
private class Listener : ScreenHandlerListener {
    override fun onSlotUpdate(handler: ScreenHandler, slotId: Int, stack: ItemStack) {
        // Only empty slots in the bin
        if (slotId > RecycleBinBlock.INVENTORY_SIZE) return

        handler.getSlot(slotId).stack = ItemStack(Items.AIR)
    }

    override fun onPropertyUpdate(handler: ScreenHandler, property: Int, value: Int) = Unit
}
