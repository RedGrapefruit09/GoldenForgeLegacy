package io.github.redgrapefruit09.goldenforge.client

import io.github.redgrapefruit09.goldenforge.registry.MenuRegistry
import net.fabricmc.api.ClientModInitializer

object GoldenForgeClient : ClientModInitializer {
    override fun onInitializeClient() {
        MenuRegistry.initializeClient()
    }
}
