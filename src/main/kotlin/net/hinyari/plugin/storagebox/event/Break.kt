package net.hinyari.plugin.storagebox.event

import net.hinyari.plugin.storagebox.extensions.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class Break : Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val player = event.player

        if (player.itemInMainHand.isStorageBox() && player.itemInMainHand.type.isTool) {
            // イベントをキャンセル
            event.isCancelled = true
            player.sendMessageWithErrorPrefix("アイテムをStorageBoxから取り出して使用して下さい!")
            event.block.location.spawnSmoke()
        }
    }

}
