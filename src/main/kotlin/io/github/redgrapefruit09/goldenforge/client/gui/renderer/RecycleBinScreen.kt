package io.github.redgrapefruit09.goldenforge.client.gui.renderer

import com.redgrapefruit.redmenu.redmenu.standard.MenuScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class RecycleBinScreen(handler: ScreenHandler, inventory: PlayerInventory, title: Text) :
    MenuScreen(handler, inventory, title) {

    override val texture: Identifier = Identifier("minecraft", "textures/gui/container/dispenser.png")
}
