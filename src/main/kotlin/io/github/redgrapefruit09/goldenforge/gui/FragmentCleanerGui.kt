package io.github.redgrapefruit09.goldenforge.gui

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WSprite
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.redgrapefruit09.goldenforge.block.FragmentCleanerBlock
import io.github.redgrapefruit09.goldenforge.registry.MenuRegistry
import io.github.redgrapefruit09.goldenforge.util.id
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext

class FragmentCleanerGui(
    syncId: Int, playerInventory: PlayerInventory, context: ScreenHandlerContext = ScreenHandlerContext.EMPTY,
) :
    SyncedGuiDescription(
        MenuRegistry.FRAGMENT_CLEANER_TYPE,
        syncId, playerInventory,
        getBlockInventory(context, FragmentCleanerBlock.INVENTORY_SIZE),
        null) {

    init {
        val root = WGridPanel()
        setRootPanel(root)
        root.setSize(300, 200)
        root.insets = Insets.ROOT_PANEL

        root.add(WItemSlot.of(blockInventory, 0, 3, 1), 4, 1) // input array
        root.add(WItemSlot.of(blockInventory, 3, 3, 1), 4, 5) // output array
        root.add(WItemSlot.of(blockInventory, 6), 2, 2) // fuel slot
        root.add(WItemSlot.of(blockInventory, 7), 2, 4) // trash slot
        root.add(WSprite("textures/gui/arrow.png".id), 5, 3) // arrow image

        root.add(createPlayerInventoryPanel(), 0, 6)

        root.validate(this)
    }
}