package io.github.redgrapefruit09.goldenforge.gui

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WSprite
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.redgrapefruit09.goldenforge.block.FragmentReinforcerBlock
import io.github.redgrapefruit09.goldenforge.registry.MenuRegistry
import io.github.redgrapefruit09.goldenforge.util.combinedId
import io.github.redgrapefruit09.goldenforge.util.id
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext

class FragmentReinforcerGui(
    syncId: Int, playerInventory: PlayerInventory, context: ScreenHandlerContext = ScreenHandlerContext.EMPTY,
) : SyncedGuiDescription(
    MenuRegistry.FRAGMENT_REINFORCER_TYPE,
    syncId, playerInventory,
    getBlockInventory(context, FragmentReinforcerBlock.INVENTORY_SIZE),
    null
) {
    init {
        val root = WGridPanel()
        setRootPanel(root)
        root.setSize(300, 200)
        root.insets = Insets.ROOT_PANEL

        // Top material row
        root.add(WItemSlot.of(blockInventory, 0, 5, 1), 0, 2)
        // Bottom material row
        root.add(WItemSlot.of(blockInventory, 5, 5, 1), 0, 4)
        // Top material images
        root.add(WSprite("minecraft:textures/item/iron_ingot.png".combinedId), 0, 1)
        root.add(WSprite("minecraft:textures/item/gold_ingot.png".combinedId), 1, 1)
        root.add(WSprite("minecraft:textures/item/diamond.png".combinedId), 2, 1)
        root.add(WSprite("minecraft:textures/item/netherite_ingot.png".combinedId), 3, 1)
        root.add(WSprite("minecraft:textures/item/emerald.png".combinedId), 4, 1)
        // Bottom material images
        root.add(WSprite("minecraft:textures/block/iron_block.png".combinedId), 0, 3)
        root.add(WSprite("minecraft:textures/block/gold_block.png".combinedId), 1, 3)
        root.add(WSprite("minecraft:textures/block/diamond_block.png".combinedId), 2, 3)
        root.add(WSprite("minecraft:textures/block/netherite_block.png".combinedId), 3, 3)
        root.add(WSprite("minecraft:textures/block/emerald_block.png".combinedId), 4, 3)
        // Input slot
        root.add(WItemSlot.of(blockInventory, 10), 7, 2)
        // Arrow image
        root.add(WSprite("textures/gui/arrow.png".id), 7, 3)
        // Output slot
        root.add(WItemSlot.of(blockInventory, 11), 7, 4)
        // Failed output slot
        root.add(WItemSlot.of(blockInventory, 12), 10, 3)
        // Player inventory
        root.add(createPlayerInventoryPanel(), 0, 6)

        root.validate(this)
    }
}
