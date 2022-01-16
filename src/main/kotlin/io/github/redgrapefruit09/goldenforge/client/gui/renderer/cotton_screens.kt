package io.github.redgrapefruit09.goldenforge.client.gui.renderer

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import io.github.redgrapefruit09.goldenforge.gui.FragmentCleanerGui
import io.github.redgrapefruit09.goldenforge.gui.FragmentReinforcerGui
import io.github.redgrapefruit09.goldenforge.gui.MetalFurnaceGui
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

// file for CottonInventoryScreen impls since they're really short

class FragmentCleanerGuiRenderer(gui: FragmentCleanerGui, playerInventory: PlayerInventory, title: Text) :
    CottonInventoryScreen<FragmentCleanerGui>(gui, playerInventory.player, title)

class FragmentReinforcerGuiRenderer(gui: FragmentReinforcerGui, playerInventory: PlayerInventory, title: Text) :
    CottonInventoryScreen<FragmentReinforcerGui>(gui, playerInventory.player, title)

class MetalFurnaceGuiRenderer(gui: MetalFurnaceGui, playerInventory: PlayerInventory, title: Text) :
    CottonInventoryScreen<MetalFurnaceGui>(gui, playerInventory.player, title)
