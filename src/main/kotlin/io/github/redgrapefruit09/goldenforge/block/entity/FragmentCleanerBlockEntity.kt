package io.github.redgrapefruit09.goldenforge.block.entity

import com.redgrapefruit.redmenu.redmenu.libgui.DefaultedSidedInventory
import io.github.redgrapefruit09.goldenforge.block.FragmentCleanerBlock
import io.github.redgrapefruit09.goldenforge.item.CleanFragmentItem
import io.github.redgrapefruit09.goldenforge.item.FragmentItem
import io.github.redgrapefruit09.goldenforge.registry.BlockRegistry
import io.github.redgrapefruit09.goldenforge.registry.ItemRegistry
import io.github.redgrapefruit09.goldenforge.gui.FragmentCleanerGui
import io.github.redgrapefruit09.goldenforge.util.MOD_ID
import io.github.redgrapefruit09.goldenforge.util.id
import io.github.redgrapefruit09.goldenforge.util.remove
import io.github.redgrapefruit09.goldenforge.util.withChance
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.AirBlockItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

/**
 * A blockentity for the fragment cleaner
 */
class FragmentCleanerBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(BlockRegistry.FRAGMENT_CLEANER_BLOCK_ENTITY, pos, state), DefaultedSidedInventory, InventoryProvider,
    NamedScreenHandlerFactory {

    // Inventory
    private val items: DefaultedList<ItemStack> =
        DefaultedList.ofSize(FragmentCleanerBlock.INVENTORY_SIZE, Items.AIR.defaultStack)

    // Properties
    var isFuelInserted: Boolean = false
    var conversionTime: Int = 0
    var currentSlot: Int = -1

    override fun getItems(): DefaultedList<ItemStack> = items
    override fun markDirty() = Unit
    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory = this

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)

        isFuelInserted = nbt.getBoolean("isFuelInserted")
        conversionTime = nbt.getInt("conversionTime")
        currentSlot = nbt.getInt("currentSlot")

        Inventories.readNbt(nbt, items)
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        nbt.putBoolean("isFuelInserted", isFuelInserted)
        nbt.putInt("conversionTime", conversionTime)
        nbt.putInt("currentSlot", currentSlot)

        Inventories.writeNbt(nbt, items)
        return super.writeNbt(nbt)
    }

    override fun createMenu(i: Int, playerInventory: PlayerInventory, playerEntity: PlayerEntity): ScreenHandler {
        return FragmentCleanerGui(i, playerInventory, ScreenHandlerContext.create(world, pos))
    }

    override fun getDisplayName(): Text {
        return TranslatableText(cachedState.block.translationKey) // fetch from the block's translation
    }

    private fun moveSlot() {
        var next = currentSlot + 1
        if (next > 2) next = 0 // if out-of-bounds, set back to zero
        currentSlot = next
    }

    companion object {
        const val PROCESS_LENGTH = 100 // length of processing in the cleaner. equivalent to 5 seconds
        const val DUST_DROP_CHANCE = 50 // chance of dropping dust in the slot

        fun tick(
            world: World,
            blockPos: BlockPos,
            blockState: BlockState,
            blockEntity: FragmentCleanerBlockEntity,
        ) {
            blockEntity.apply {
                // Init current slot
                if (currentSlot == -1) {
                    currentSlot = 0
                }
                // Move on if the operation is complete
                if (items[currentSlot].item is AirBlockItem) moveSlot()
                // Ignore if nothing to operate on still
                if (items[currentSlot].item is AirBlockItem) return

                val input = items[currentSlot]
                val output = items[currentSlot + 3]
                // Check if the input contains an inoperable item
                if (input.item !is FragmentItem || input.item is CleanFragmentItem) moveSlot()
                // Ignore if nothing to operate on still
                if (items[currentSlot].item is AirBlockItem) return

                // Check if fuel is inserted
                val fuel = items[6]
                isFuelInserted = AbstractFurnaceBlockEntity.canUseAsFuel(fuel) // this boi is reusable
                if (!isFuelInserted) return

                // Increment progress
                ++conversionTime
                // Convert and reset if progress has reached the mark
                if (conversionTime >= PROCESS_LENGTH) {
                    conversionTime = 0

                    // TODO: Fix this mess
                    val inputName = input.translationKey.remove("item.$MOD_ID.")
                    val outputName = "clean_$inputName"
                    val outputItem = Registry.ITEM.get(outputName.id)

                    // Decrement input slot
                    input.decrement(1)
                    if (input.count == 0) items[currentSlot] = Items.AIR.defaultStack // replace to air if necessary
                    // Increment/create output slot
                    if (output.item !is CleanFragmentItem) {
                        items[currentSlot + 3] = outputItem.defaultStack
                    } else {
                        output.increment(1)
                    }

                    // Increment/create dust slot
                    withChance(DUST_DROP_CHANCE) {
                        val dust = items[7]
                        if (dust.item.translationKey != "item.$MOD_ID.dust") {
                            items[7] = ItemRegistry.DUST.defaultStack
                        } else {
                            dust.increment(1)
                        }
                    }
                }
            }
        }
    }
}
