package io.github.redgrapefruit09.goldenforge.gui

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WSprite
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.redgrapefruit09.goldenforge.block.MetalFurnaceBlock
import io.github.redgrapefruit09.goldenforge.registry.MenuRegistry
import io.github.redgrapefruit09.goldenforge.util.id
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext

/**
 * The GUI of the metal furnace
 */
class MetalFurnaceGui(
    syncId: Int, playerInventory: PlayerInventory, context: ScreenHandlerContext = ScreenHandlerContext.EMPTY
) : SyncedGuiDescription(
    MenuRegistry.METAL_FURNACE_TYPE,
    syncId, playerInventory,
    getBlockInventory(context, MetalFurnaceBlock.INVENTORY_SIZE),
    null
) {
    init {
        val root = WGridPanel()
        setRootPanel(root)
        root.setSize(200, 200)
        root.insets = Insets.ROOT_PANEL

        root.add(WItemSlot.of(blockInventory, 0), 2, 1)
        root.add(WItemSlot.of(blockInventory, 4), 4, 2)
        root.add(WItemSlot.of(blockInventory, 1), 2, 3)
        root.add(WItemSlot.of(blockInventory, 2), 2, 4)
        root.add(WItemSlot.of(blockInventory, 3), 6, 2)
        root.add(WSprite("textures/gui/arrow.png".id), 2, 2)

        root.add(createPlayerInventoryPanel(), 0, 6)

        root.validate(this)
    }
}