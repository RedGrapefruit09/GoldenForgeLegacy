package io.github.redgrapefruit09.goldenforge.block

import com.redgrapefruit.redmenu.redmenu.standard.MenuBlock
import io.github.redgrapefruit09.goldenforge.block.entity.RecycleBinBlockEntity
import io.github.redgrapefruit09.goldenforge.util.voxelShape
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView

/**
 * A block for a generic recycle bin for any items.
 */
class RecycleBinBlock(settings: Settings) : MenuBlock(settings) {
    override fun castToInventory(blockEntity: BlockEntity): Inventory {
        return blockEntity as RecycleBinBlockEntity
    }

    override fun checkBlockEntity(blockEntity: BlockEntity): Boolean {
        return blockEntity is RecycleBinBlockEntity
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return RecycleBinBlockEntity(pos, state)
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext,
    ): VoxelShape = SHAPE

    companion object {
        const val INVENTORY_SIZE = 9

        // VoxelShape
        private val SHAPE = voxelShape {
            shape(12, 0, 4, 13, 12, 12)
            shape(3, 0, 4, 4, 12, 12)
            shape(3, 0, 3, 13, 12, 4)
            shape(3, 0, 12, 13, 12, 13)
            shape(4, 0, 4, 12, 1, 12)
            shape(2, 12, 2, 14, 13, 14)
            shape(4, 11, 4, 12, 12, 12)
            shape(3, 13, 3, 13, 14, 13)
        }
    }
}
