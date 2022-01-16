package io.github.redgrapefruit09.goldenforge

import com.redgrapefruit.datapipe.kotlin.PipeResourceLoader
import io.github.redgrapefruit09.goldenforge.core.MetalSettingsLoader
import io.github.redgrapefruit09.goldenforge.registry.BlockRegistry
import io.github.redgrapefruit09.goldenforge.registry.ItemRegistry
import io.github.redgrapefruit09.goldenforge.registry.MenuRegistry
import net.fabricmc.api.ModInitializer

object GoldenForge : ModInitializer {
    override fun onInitialize() {
        // Register resource loaders
        PipeResourceLoader.registerServer(MetalSettingsLoader)

        // Run standard registries
        ItemRegistry.initialize()
        BlockRegistry.initialize()
        MenuRegistry.initialize()
    }
}
