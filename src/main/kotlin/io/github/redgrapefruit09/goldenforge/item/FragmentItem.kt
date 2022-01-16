package io.github.redgrapefruit09.goldenforge.item

import io.github.redgrapefruit09.goldenforge.util.itemSettings
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.world.World

/**
 * A metal's fragment
 */
class FragmentItem : Item(itemSettings) {
    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext,
    ) {
        tooltip += LiteralText("State: Dirty")
    }
}

/**
 * A clean fragment of a metal
 */
class CleanFragmentItem : Item(itemSettings) {
    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext,
    ) {
        tooltip += LiteralText("State: Clean")
    }
}

/**
 * A clean fragment of a metal with extra reinforcements applied
 */
class ReinforcedFragmentItem : Item(itemSettings) {
    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext,
    ) {
        tooltip += LiteralText("State: Reinforced")
    }
}
