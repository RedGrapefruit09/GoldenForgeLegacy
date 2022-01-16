package io.github.redgrapefruit09.goldenforge.block.entity

import com.redgrapefruit.redmenu.redmenu.libgui.DefaultedSidedInventory
import io.github.redgrapefruit09.goldenforge.block.MetalFurnaceBlock
import io.github.redgrapefruit09.goldenforge.gui.MetalFurnaceGui
import io.github.redgrapefruit09.goldenforge.item.ReinforcedFragmentItem
import io.github.redgrapefruit09.goldenforge.item.SealedContainerItem
import io.github.redgrapefruit09.goldenforge.item.SealedContainerItemData
import io.github.redgrapefruit09.goldenforge.registry.BlockRegistry
import io.github.redgrapefruit09.goldenforge.registry.ItemRegistry
import io.github.redgrapefruit09.goldenforge.util.*
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
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
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

/**
 * The blockentity for the metal furnace
 */
class MetalFurnaceBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(BlockRegistry.METAL_FURNACE_BLOCK_ENTITY, pos, state), DefaultedSidedInventory, InventoryProvider, NamedScreenHandlerFactory {

    // Inventory
    private var items: DefaultedList<ItemStack> = DefaultedList.ofSize(MetalFurnaceBlock.INVENTORY_SIZE, Items.AIR.defaultStack)
    // Properties
    private var smeltingProgress: Int = 0

    override fun getItems(): DefaultedList<ItemStack> = items
    override fun markDirty() = Unit
    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory = this

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        Inventories.readNbt(nbt, items)
        smeltingProgress = nbt.getInt("Smelting Progress")
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        super.writeNbt(nbt)
        Inventories.writeNbt(nbt, items)
        nbt.putInt("Smelting Progress", smeltingProgress)
        return nbt
    }

    override fun createMenu(i: Int, playerInventory: PlayerInventory, playerEntity: PlayerEntity): ScreenHandler {
        return MetalFurnaceGui(i, playerInventory, ScreenHandlerContext.create(world, pos))
    }

    override fun getDisplayName(): Text {
        return TranslatableText(cachedState.block.translationKey)
    }

    companion object {
        // Indexes
        private const val I_INPUT = 0
        private const val I_FUEL = 4
        private const val I_FAILED_OUTPUT = 3
        private const val I_EMPTY_OUTPUT = 2
        private const val I_OUTPUT = 1

        // Other constants
        private const val SMELT_TIME = 120 // the length of smelting
        private const val FAILURE_CHANCE = 3 // chance of the smelting failing and producing dust
        private const val TRANSFERRED_AMOUNT = 3 // amount of litres transferred as a result of smelting

        fun tick(
            world: World,
            blockPos: BlockPos,
            blockState: BlockState,
            blockEntity: MetalFurnaceBlockEntity
        ) {
            blockEntity.apply {
                // Verify input
                val inputStack = items[I_INPUT]
                if (inputStack.item !is ReinforcedFragmentItem) return

                // Verify fuel
                val fuelStack = items[I_FUEL]
                if (!isFuel(fuelStack)) return

                // Verify failed output
                val failedOutputStack = items[I_FAILED_OUTPUT]
                if (failedOutputStack.item.translationKey != "item.$MOD_ID.dust" && failedOutputStack.item !is AirBlockItem) return

                // Verify empty output
                val emptyOutputStack = items[I_EMPTY_OUTPUT]
                // Check for item class
                if (emptyOutputStack.item !is SealedContainerItem) return
                // Check if there's enough capacity
                val data = SealedContainerItemData[emptyOutputStack]
                val config = (emptyOutputStack.item as? SealedContainerItem)?.settings ?: return
                if (config.capacity - data.usage < TRANSFERRED_AMOUNT) return

                // Verify main output
                val outputStack = items[I_OUTPUT]
                if (outputStack.item !is AirBlockItem) return

                // Update progress
                smeltingProgress++
                if (smeltingProgress >= SMELT_TIME) {
                    smeltingProgress = 0

                    // Handle failures
                    withChance(FAILURE_CHANCE) {
                        // Increment/create failed output slot
                        if (failedOutputStack.item.translationKey == "item.$MOD_ID.dust") {
                            failedOutputStack.increment()
                        } else {
                            items[I_FAILED_OUTPUT] = ItemRegistry.DUST.defaultStack
                        }
                        // Decrement fuel
                        fuelStack.decrement()

                        return@apply
                    }

                    // Put the empty output into the main output
                    items[I_OUTPUT] = items[I_EMPTY_OUTPUT]
                    // Decrement fuel
                    fuelStack.decrement()
                    // Transfer the amount
                    val output = items[I_OUTPUT]
                    SealedContainerItemData[output].use(output) {
                        transfer(inputStack.translationKey.translationToIdentifier(), TRANSFERRED_AMOUNT)
                    }
                    // Empty the secondary (empty) output
                    items[I_EMPTY_OUTPUT] = Items.AIR.defaultStack
                }
            }
        }
    }
}