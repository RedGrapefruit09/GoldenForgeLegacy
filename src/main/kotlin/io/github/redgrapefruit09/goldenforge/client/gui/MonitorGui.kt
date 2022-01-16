package io.github.redgrapefruit09.goldenforge.client.gui

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.redgrapefruit09.goldenforge.util.temperature
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient

@Environment(EnvType.CLIENT)
class MonitorGui : LightweightGuiDescription() {
    init {
        val root = WGridPanel()
        setRootPanel(root)
        root.setSize(200, 25)
        root.insets = Insets.ROOT_PANEL

        val label = WDynamicLabel {
            val temperature = MinecraftClient.getInstance().player?.temperature ?: "Could not get temperature"
            "Your temperature: $temperature degrees"
        }
        root.add(label, 0, 0)

        root.validate(this)
    }
}
