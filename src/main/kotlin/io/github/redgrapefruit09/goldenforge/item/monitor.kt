package io.github.redgrapefruit09.goldenforge.item

import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import io.github.redgrapefruit09.goldenforge.client.gui.MonitorGui
import io.github.redgrapefruit09.goldenforge.util.itemSettings
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

// Monitor
class MonitorItem : Item(itemSettings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (world.isClient()) {
            MinecraftClient.getInstance().setScreen(CottonClientScreen(MonitorGui()))
        }

        return super.use(world, user, hand)
    }
}
