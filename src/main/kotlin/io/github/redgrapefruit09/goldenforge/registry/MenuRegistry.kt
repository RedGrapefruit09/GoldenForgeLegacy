package io.github.redgrapefruit09.goldenforge.registry

import io.github.redgrapefruit09.goldenforge.client.gui.renderer.FragmentCleanerGuiRenderer
import io.github.redgrapefruit09.goldenforge.client.gui.renderer.FragmentReinforcerGuiRenderer
import io.github.redgrapefruit09.goldenforge.client.gui.renderer.MetalFurnaceGuiRenderer
import io.github.redgrapefruit09.goldenforge.client.gui.renderer.RecycleBinScreen
import io.github.redgrapefruit09.goldenforge.gui.FragmentCleanerGui
import io.github.redgrapefruit09.goldenforge.gui.FragmentReinforcerGui
import io.github.redgrapefruit09.goldenforge.gui.MetalFurnaceGui
import io.github.redgrapefruit09.goldenforge.gui.RecycleBinScreenHandler
import io.github.redgrapefruit09.goldenforge.util.IClientRegistry
import io.github.redgrapefruit09.goldenforge.util.IRegistry
import io.github.redgrapefruit09.goldenforge.util.id
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.screen.ScreenHandlerType

/**
 * Registry for menu resources - screens and screen handlers
 */
object MenuRegistry : IRegistry, IClientRegistry {
    lateinit var RECYCLE_BIN_TYPE: ScreenHandlerType<RecycleBinScreenHandler>
    lateinit var FRAGMENT_CLEANER_TYPE: ScreenHandlerType<FragmentCleanerGui>
    lateinit var FRAGMENT_REINFORCER_TYPE: ScreenHandlerType<FragmentReinforcerGui>
    lateinit var METAL_FURNACE_TYPE: ScreenHandlerType<MetalFurnaceGui>

    override fun initialize() {
        RECYCLE_BIN_TYPE = ScreenHandlerRegistry.registerSimple("recycle_bin".id, ::RecycleBinScreenHandler)
        FRAGMENT_CLEANER_TYPE =
            ScreenHandlerRegistry.registerSimple("fragment_cleaner".id, ::FragmentCleanerGui)
        FRAGMENT_REINFORCER_TYPE =
            ScreenHandlerRegistry.registerSimple("fragment_reinforcer".id, ::FragmentReinforcerGui)
        METAL_FURNACE_TYPE =
            ScreenHandlerRegistry.registerSimple("metal_furnace".id, ::MetalFurnaceGui)
    }

    override fun initializeClient() {
        ScreenRegistry.register(RECYCLE_BIN_TYPE, ::RecycleBinScreen)
        ScreenRegistry.register(FRAGMENT_CLEANER_TYPE, ::FragmentCleanerGuiRenderer)
        ScreenRegistry.register(FRAGMENT_REINFORCER_TYPE, ::FragmentReinforcerGuiRenderer)
        ScreenRegistry.register(METAL_FURNACE_TYPE, ::MetalFurnaceGuiRenderer)
    }
}
