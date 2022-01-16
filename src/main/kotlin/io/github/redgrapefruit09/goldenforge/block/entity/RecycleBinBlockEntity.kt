package io.github.redgrapefruit09.goldenforge.block.entity

import com.redgrapefruit.redmenu.redmenu.standard.MenuBlockEntity
import io.github.redgrapefruit09.goldenforge.block.RecycleBinBlock
import io.github.redgrapefruit09.goldenforge.gui.RecycleBinScreenHandler
import io.github.redgrapefruit09.goldenforge.registry.BlockRegistry
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

/**
 * A blockentity for the recycle bin
 */
class RecycleBinBlockEntity(pos: BlockPos, state: BlockState) :
    MenuBlockEntity(BlockRegistry.RECYCLE_BIN_BLOCK_ENTITY, pos, state, RecycleBinBlock.INVENTORY_SIZE) {

    override val items: DefaultedList<ItemStack> = inventory // mirror to inventory

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return RecycleBinScreenHandler(syncId, inv, this)
    }
}