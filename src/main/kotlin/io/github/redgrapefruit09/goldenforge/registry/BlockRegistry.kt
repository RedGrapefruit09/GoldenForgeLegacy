package io.github.redgrapefruit09.goldenforge.registry

import io.github.redgrapefruit09.goldenforge.block.*
import io.github.redgrapefruit09.goldenforge.block.entity.FragmentCleanerBlockEntity
import io.github.redgrapefruit09.goldenforge.block.entity.FragmentReinforcerBlockEntity
import io.github.redgrapefruit09.goldenforge.block.entity.MetalFurnaceBlockEntity
import io.github.redgrapefruit09.goldenforge.block.entity.RecycleBinBlockEntity
import io.github.redgrapefruit09.goldenforge.util.IRegistry
import io.github.redgrapefruit09.goldenforge.util.id
import io.github.redgrapefruit09.goldenforge.util.itemSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.util.registry.Registry

/**
 * Registry for all the mod's blocks
 */
object BlockRegistry : IRegistry {
    // Ores
    val STEEL_ORE = OreBlock(FabricBlockSettings.of(Material.METAL).hardness(0.5f), "steel")

    // Machines
    val RECYCLE_BIN = RecycleBinBlock(FabricBlockSettings.of(Material.METAL).hardness(1.5f))
    val FRAGMENT_CLEANER = FragmentCleanerBlock(FabricBlockSettings.of(Material.METAL).hardness(1.8f))
    val FRAGMENT_REINFORCER = FragmentReinforcerBlock(FabricBlockSettings.of(Material.METAL).hardness(2.1f))
    val METAL_FURNACE = MetalFurnaceBlock(FabricBlockSettings.of(Material.METAL).hardness(2.3f))

    // Block entity types
    val RECYCLE_BIN_BLOCK_ENTITY: BlockEntityType<RecycleBinBlockEntity> =
        FabricBlockEntityTypeBuilder.create(::RecycleBinBlockEntity, RECYCLE_BIN).build()
    val FRAGMENT_CLEANER_BLOCK_ENTITY: BlockEntityType<FragmentCleanerBlockEntity> =
        FabricBlockEntityTypeBuilder.create(::FragmentCleanerBlockEntity, FRAGMENT_CLEANER).build()
    val FRAGMENT_REINFORCER_BLOCK_ENTITY: BlockEntityType<FragmentReinforcerBlockEntity> =
        FabricBlockEntityTypeBuilder.create(::FragmentReinforcerBlockEntity, FRAGMENT_REINFORCER).build()
    val METAL_FURNACE_BLOCK_ENTITY: BlockEntityType<MetalFurnaceBlockEntity> =
        FabricBlockEntityTypeBuilder.create(::MetalFurnaceBlockEntity, METAL_FURNACE).build()

    override fun initialize() {
        register("steel_ore", STEEL_ORE)

        register("recycle_bin", RECYCLE_BIN, RECYCLE_BIN_BLOCK_ENTITY)
        register("fragment_cleaner", FRAGMENT_CLEANER, FRAGMENT_CLEANER_BLOCK_ENTITY)
        register("fragment_reinforcer", FRAGMENT_REINFORCER, FRAGMENT_REINFORCER_BLOCK_ENTITY)
        register("metal_furnace", METAL_FURNACE, METAL_FURNACE_BLOCK_ENTITY)
    }

    /**
     * Registers a block with an item for it
     */
    private fun register(name: String, block: Block) {
        Registry.register(Registry.BLOCK, name.id, block)
        Registry.register(Registry.ITEM, name.id, BlockItem(block, itemSettings))
    }

    /**
     * Also registers a [BlockEntityType]
     */
    private fun register(name: String, block: Block, blockEntityType: BlockEntityType<*>) {
        register(name, block)
        Registry.register(Registry.BLOCK_ENTITY_TYPE, name.id, blockEntityType)
    }
}
