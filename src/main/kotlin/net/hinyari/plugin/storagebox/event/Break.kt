package net.hinyari.plugin.storagebox.event

import net.hinyari.plugin.storagebox.SBUtil
import net.hinyari.plugin.storagebox.StorageBoxMain
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class Break : Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val player = event.player

        if (SBUtil.isStorageBox(player.inventory.itemInMainHand)) {
            // イベントをキャンセル
            event.isCancelled = true
            player.sendMessage("${StorageBoxMain.instance.PREFIX_ERROR}StorageBoxからアイテムを出して下さい!")
            SBUtil.spawnSmoke(event.block.location)
        }
    }

}
