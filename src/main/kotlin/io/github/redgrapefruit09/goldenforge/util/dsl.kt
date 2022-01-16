package io.github.redgrapefruit09.goldenforge.util

import net.minecraft.block.Block
import net.minecraft.util.function.BooleanBiFunction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

// a home for mini DSLs

inline fun voxelShape(build: VoxelShapeScope.() -> Unit): VoxelShape {
    val scope = VoxelShapeScope(mutableSetOf())
    scope.build()
    return scope.construct()
}

class VoxelShapeScope(private val shapes: MutableSet<VoxelShape>) {
    fun shape(minX: Int, minY: Int, minZ: Int, maxX: Int, maxY: Int, maxZ: Int) {
        shapes += Block.createCuboidShape(minX.toDouble(),
            minY.toDouble(),
            minZ.toDouble(),
            maxX.toDouble(),
            maxY.toDouble(),
            maxZ.toDouble())
    }

    fun construct(): VoxelShape {
        return shapes.reduce { v1, v2 -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR) }
    }
}
