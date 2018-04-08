package net.hinyari.plugin.storagebox.event

import net.hinyari.plugin.storagebox.Config
import net.hinyari.plugin.storagebox.StorageBoxMain
import net.hinyari.plugin.storagebox.extensions.*
import net.hinyari.plugin.storagebox.util.SBUtil
import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.EquipmentSlot

class Place constructor(val plugin: StorageBoxMain) : Listener {
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {

        val item = event.itemInHand
        val player = event.player
        if (item.isNotStorageBox()) return  // StorageBoxによるブロック設置ではない
        if (player.gameMode == GameMode.CREATIVE) return // プレイヤーのゲームモードがクリエイティブだった場合

        // StorageBox関係のアイテムだったら
        if (item.displayName.contains("StorageBox : ")) {
            event.isCancelled = true
            return
        }

        // 設置権限を持っていなかった場合
        if (!player.hasPermission("sb.place")) {
            player.sendMessage(SBUtil.noPermission)
            event.isCancelled = true
            return
        }

        // 無効化リストに入っていた場合
        if (Config.values.uncreatableMaterialList.contains(event.itemInHand.type)) {
            player.sendActionBar("&cそのアイテムを設置することは出来ません")
            event.isCancelled = true
            return
        }

        val block = event.block
        val location = block.location

        // 容量が無かった場合
        if (SBUtil.getAmountOfStorageBox(item) <= 0) {
            event.isCancelled = true
            
            // 警告を表示する
            player.sendActionBar("&cアイテムを補充して下さい!")
            location.spawnSmoke()
            player.playSound(player.location, Sound.BLOCK_DISPENSER_FAIL, 0.4f, 1.0f)
            
            return
        }
                
        // メインハンド
        if (event.hand == EquipmentSlot.HAND) {
            player.itemInMainHand.amount = 1
            player.itemInMainHand = item.toStorageBox(SBUtil.getAmountOfStorageBox(item) - 1)
        } else {
            player.itemInOffHand.amount = 1
            player.itemInOffHand = item.toStorageBox(SBUtil.getAmountOfStorageBox(item) - 1)
        }
    }
}
