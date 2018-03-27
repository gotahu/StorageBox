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

        if (SBUtil.isStorageBox(player.inventory.itemInMainHand) &&
                ItemUtils.isTool(player.inventory.itemInMainHand)) {
            // イベントをキャンセル
            event.isCancelled = true
            player.sendMessage("${SBUtil.errorPrefix}アイテムをStorageBoxから取り出して使用して下さい!")
            SBUtil.spawnSmoke(event.block.location)
        }
    }

}
