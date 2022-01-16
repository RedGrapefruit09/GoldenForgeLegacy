package io.github.redgrapefruit09.goldenforge.block

import io.github.redgrapefruit09.goldenforge.core.MetalSettingsLoader
import io.github.redgrapefruit09.goldenforge.util.*
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

/**
 * A multi-stage ore which drops fragments in multiple stages and is destroyed with time.
 */
class OreBlock(settings: FabricBlockSettings, name: String) : Block(settings) {
    private lateinit var stage: IntProperty
    private lateinit var resourceId: Identifier

    private val metalSettings by lazy { MetalSettingsLoader.getResource(resourceId) }

    init {
        defaultState = stateManager.defaultState.with(stage, 0)
        resourceId = name.id
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        stage = stageProperty

        builder.add(stage)

        super.appendProperties(builder)
    }

    override fun afterBreak(
        world: World,
        player: PlayerEntity,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?,
        stack: ItemStack,
    ) {
        // Drop fragments
        metalSettings.production.oreDrops.forEach { drop -> // iterate all drops
            withChance(drop.chance) {
                // Get the referenced item
                val item = Registry.ITEM.get(drop.droppedItem.combinedId)
                // Get the drop count
                var count = drop.dropCount.pick()
                withChance(drop.doubledDropChance) { // drop counts can sometimes double
                    count *= 2
                }
                // Drop the item
                world.spawnEntity(ItemEntity(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), item.stack))
            }
        }

        // Increment the stage and replace the block
        val newStage = state.get(stageProperty) + 1

        if (newStage > 5) return

        world.setBlockState(pos, state.with(stageProperty, newStage))
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext,
    ): VoxelShape {
        val height = when (state.get(stage)) {
            0 -> 16
            1 -> 13
            2 -> 10
            3 -> 7
            4 -> 4
            5 -> 2
            else -> throw crash("Out-of-bounds stage value", ArithmeticException())
        }

        return createCuboidShape(0.0, 0.0, 0.0, 16.0, height.toDouble(), 16.0)
    }

    companion object {
        var stageProperty: IntProperty = IntProperty.of("stage", 0, 5)
    }
}
