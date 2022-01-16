package io.github.redgrapefruit09.goldenforge.block

import io.github.redgrapefruit09.goldenforge.block.entity.FragmentReinforcerBlockEntity
import io.github.redgrapefruit09.goldenforge.util.onClient
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

/**
 * The fragment reinforcer block. Allows adding more materials to increase fragment value.
 */
class FragmentReinforcerBlock(settings: Settings) : Block(settings), BlockEntityProvider {
    companion object {
        // 10 slots for 10 types of inputs
        // 1 input slot
        // 1 output slot
        // 1 failed output slot (for corruptions during the process)
        const val INVENTORY_SIZE = 13
    }

    private lateinit var facing: DirectionProperty

    init {
        defaultState = stateManager.defaultState.with(facing, Direction.NORTH)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)

        facing = Properties.HORIZONTAL_FACING
        builder.add(facing)
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState =
        state.with(facing, rotation.rotate(state.get(facing)))

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState =
        state.rotate(mirror.getRotation(state.get(facing)))

    override fun getPlacementState(context: ItemPlacementContext): BlockState =
        defaultState.with(facing, context.playerFacing.opposite)

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return FragmentReinforcerBlockEntity(pos, state)
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult,
    ): ActionResult {
        onClient {
            val factory = state.createScreenHandlerFactory(world, pos)
            player.openHandledScreen(factory)
        }

        return ActionResult.SUCCESS
    }

    override fun createScreenHandlerFactory(
        state: BlockState,
        world: World,
        pos: BlockPos,
    ): NamedScreenHandlerFactory? {
        // impl copied from BlockWithEntity
        val blockEntity = world.getBlockEntity(pos)
        return if (blockEntity is NamedScreenHandlerFactory) (blockEntity as NamedScreenHandlerFactory?)!! else null
    }

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>,
    ): BlockEntityTicker<T> {
        return BlockEntityTicker { world0, blockPos, blockState, blockEntity ->
            FragmentReinforcerBlockEntity.tick(world0,
                blockPos,
                blockState,
                blockEntity as FragmentReinforcerBlockEntity)
        }
    }
}