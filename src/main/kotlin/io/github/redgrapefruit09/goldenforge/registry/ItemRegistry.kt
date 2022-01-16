package io.github.redgrapefruit09.goldenforge.registry

import io.github.redgrapefruit09.goldenforge.item.*
import io.github.redgrapefruit09.goldenforge.util.IRegistry
import io.github.redgrapefruit09.goldenforge.util.id
import io.github.redgrapefruit09.goldenforge.util.itemSettings
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry

/**
 * Registry for the mod's items
 */
object ItemRegistry : IRegistry {
    // Fragments
    // Initial
    val STONE_FRAGMENT = FragmentItem()
    val ANDESITE_FRAGMENT = FragmentItem()
    val BLACKSTONE_FRAGMENT = FragmentItem()
    val DIORITE_FRAGMENT = FragmentItem()
    val GRAVEL_FRAGMENT = FragmentItem()
    val STEEL_FRAGMENT = FragmentItem()

    // Clean
    val CLEAN_STONE_FRAGMENT = CleanFragmentItem()
    val CLEAN_ANDESITE_FRAGMENT = CleanFragmentItem()
    val CLEAN_BLACKSTONE_FRAGMENT = CleanFragmentItem()
    val CLEAN_DIORITE_FRAGMENT = CleanFragmentItem()
    val CLEAN_GRAVEL_FRAGMENT = CleanFragmentItem()
    val CLEAN_STEEL_FRAGMENT = CleanFragmentItem()

    // Reinforced
    val REINFORCED_STEEL_FRAGMENT = ReinforcedFragmentItem()

    // Containers
    val IRON_SEALED_CONTAINER = SealedContainerItem("iron")
    val GOLDEN_SEALED_CONTAINER = SealedContainerItem("golden")
    val DIAMOND_SEALED_CONTAINER = SealedContainerItem("diamond")
    val NETHERITE_SEALED_CONTAINER = SealedContainerItem("netherite")
    val STEEL_SEALED_CONTAINER = SealedContainerItem("steel")

    // Gloves
    val IRON_GLOVES = GlovesItem("iron")
    val GOLDEN_GLOVES = GlovesItem("golden")
    val DIAMOND_GLOVES = GlovesItem("diamond")
    val NETHERITE_GLOVES = GlovesItem("netherite")
    val STEEL_GLOVES = GlovesItem("steel")

    // Raw material
    val STEEL_MATERIAL = MetalMaterialItem("steel")

    // Misc
    val DUST = Item(itemSettings)
    val MONITOR = MonitorItem()

    override fun initialize() {
        register("stone_fragment", STONE_FRAGMENT)
        register("andesite_fragment", ANDESITE_FRAGMENT)
        register("blackstone_fragment", BLACKSTONE_FRAGMENT)
        register("diorite_fragment", DIORITE_FRAGMENT)
        register("gravel_fragment", GRAVEL_FRAGMENT)
        register("steel_fragment", STEEL_FRAGMENT)

        register("clean_stone_fragment", CLEAN_STONE_FRAGMENT)
        register("clean_andesite_fragment", CLEAN_ANDESITE_FRAGMENT)
        register("clean_blackstone_fragment", CLEAN_BLACKSTONE_FRAGMENT)
        register("clean_diorite_fragment", CLEAN_DIORITE_FRAGMENT)
        register("clean_gravel_fragment", CLEAN_GRAVEL_FRAGMENT)
        register("clean_steel_fragment", CLEAN_STEEL_FRAGMENT)

        register("reinforced_steel_fragment", REINFORCED_STEEL_FRAGMENT)

        register("iron_sealed_container", IRON_SEALED_CONTAINER)
        register("golden_sealed_container", GOLDEN_SEALED_CONTAINER)
        register("diamond_sealed_container", DIAMOND_SEALED_CONTAINER)
        register("netherite_sealed_container", NETHERITE_SEALED_CONTAINER)
        register("steel_sealed_container", STEEL_SEALED_CONTAINER)

        register("iron_gloves", IRON_GLOVES)
        register("golden_gloves", GOLDEN_GLOVES)
        register("diamond_gloves", DIAMOND_GLOVES)
        register("netherite_gloves", NETHERITE_GLOVES)
        register("steel_gloves", STEEL_GLOVES)

        register("steel_material", STEEL_MATERIAL)

        register("dust", DUST)
        register("temperature_monitor", MONITOR)
    }

    private fun register(name: String, item: Item) {
        Registry.register(Registry.ITEM, name.id, item)
    }
}
