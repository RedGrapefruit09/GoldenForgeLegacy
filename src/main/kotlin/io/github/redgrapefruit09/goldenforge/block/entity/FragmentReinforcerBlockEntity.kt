package io.github.redgrapefruit09.goldenforge.block.entity

import com.redgrapefruit.redmenu.redmenu.libgui.DefaultedSidedInventory
import io.github.redgrapefruit09.goldenforge.block.FragmentReinforcerBlock
import io.github.redgrapefruit09.goldenforge.core.MetalSettings
import io.github.redgrapefruit09.goldenforge.core.MetalSettingsLoader
import io.github.redgrapefruit09.goldenforge.item.CleanFragmentItem
import io.github.redgrapefruit09.goldenforge.item.ReinforcedFragmentItem
import io.github.redgrapefruit09.goldenforge.registry.BlockRegistry
import io.github.redgrapefruit09.goldenforge.registry.ItemRegistry
import io.github.redgrapefruit09.goldenforge.gui.FragmentReinforcerGui
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
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class FragmentReinforcerBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(BlockRegistry.FRAGMENT_REINFORCER_BLOCK_ENTITY, pos, state), DefaultedSidedInventory, InventoryProvider,
    NamedScreenHandlerFactory {

    // Inventory
    private val items: DefaultedList<ItemStack> =
        DefaultedList.ofSize(FragmentReinforcerBlock.INVENTORY_SIZE, Items.AIR.defaultStack)

    // Properties
    private var processingTime: Int = 0

    override fun getItems(): DefaultedList<ItemStack> = items
    override fun markDirty() = Unit
    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory = this

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        Inventories.readNbt(nbt, items)
        processingTime = nbt.getInt("processingTime")
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        super.writeNbt(nbt)
        Inventories.writeNbt(nbt, items)
        nbt.putInt("processingTime", processingTime)
        return nbt
    }

    override fun createMenu(i: Int, playerInventory: PlayerInventory, playerEntity: PlayerEntity): ScreenHandler {
        return FragmentReinforcerGui(i, playerInventory, ScreenHandlerContext.create(world, pos))
    }

    override fun getDisplayName(): Text {
        return TranslatableText(cachedState.block.translationKey) // fetch from block's translation
    }

    companion object {
        private val ID_TO_INDEX_MAP = mapOf(
            "minecraft:iron_ingot" to 0,
            "minecraft:gold_ingot" to 1,
            "minecraft:diamond" to 2,
            "minecraft:netherite_ingot" to 3,
            "minecraft:emerald" to 4,
            "minecraft:iron_block" to 5,
            "minecraft:gold_block" to 6,
            "minecraft:diamond_block" to 7,
            "minecraft:netherite_block" to 8,
            "minecraft:emerald_block" to 9
        )

        private const val I_INPUT = 10
        private const val I_OUTPUT = 11
        private const val I_FAILED_OUTPUT = 12

        private const val PROCESS_LENGTH = 100 // length of processing
        private const val PROCESS_FAILURE_CHANCE = 15 // chance of failing

        private fun idForMaterial(material: String) = ID_TO_INDEX_MAP[material]

        fun tick(
            world: World,
            blockPos: BlockPos,
            blockState: BlockState,
            blockEntity: FragmentReinforcerBlockEntity,
        ) {
            blockEntity.apply {
                // Validate input slot: check for air and non-existent config
                val inputStack = items[I_INPUT]
                if (inputStack.item is AirBlockItem) return
                if (inputStack.item !is CleanFragmentItem) return
                val resourceName =
                    inputStack.translationKey.remove("item.$MOD_ID.").remove("clean_").remove("_fragment")
                val settings = MetalSettingsLoader.pipeline.get(resourceName.id) ?: return

                // Validate material slots
                val slots = mutableListOf<Int>()
                settings.production.reinforcementInputs.forEach { input ->
                    val slot = idForMaterial(input.id) ?: return@apply
                    slots += slot
                }

                // Validate material slot amounts
                slots.forEachIndexed { index, slot ->
                    val stack = items[slot]
                    val input = settings.production.reinforcementInputs[index]
                    if (stack.count < input.amount) return@apply
                }

                // Increment processing time, if done, convert
                ++processingTime
                if (processingTime > PROCESS_LENGTH) {
                    processingTime = 0
                    // Sometimes, the process might fail
                    withChance(PROCESS_FAILURE_CHANCE) {
                        val failedStack = items[I_FAILED_OUTPUT]
                        // Create or increment failure stack
                        if (failedStack.item.translationKey != "item.$MOD_ID.dust") {
                            items[I_FAILED_OUTPUT] = ItemRegistry.DUST.defaultStack
                        } else {
                            failedStack.increment(1)
                        }

                        decrementMaterials(blockEntity, settings)
                        return@apply
                    }

                    // Decrement input stack
                    inputStack.decrement(1)
                    if (inputStack.count <= 0) { // replace with air if empty
                        items[I_INPUT] = Items.AIR.defaultStack
                    }

                    // Increment or create output stack
                    val outputStack = items[I_OUTPUT]
                    if (outputStack.item !is ReinforcedFragmentItem) {
                        val reinforcedVariant = Registry.ITEM.get(settings.production.reinforcedFragment.combinedId)
                        items[I_OUTPUT] = reinforcedVariant.defaultStack
                    } else {
                        outputStack.increment(1)
                    }

                    decrementMaterials(blockEntity, settings)
                }
            }
        }

        // Decrement all materials required here
        private fun decrementMaterials(blockEntity: FragmentReinforcerBlockEntity, settings: MetalSettings) {
            blockEntity.apply {
                settings.production.reinforcementInputs.forEach { input ->
                    val slot = idForMaterial(input.id)!!
                    val stack = items[slot]
                    stack.decrement(input.amount)

                    if (stack.count <= 0) { // replace with air if empty
                        items[slot] = Items.AIR.defaultStack
                    }
                }
            }
        }
    }
}