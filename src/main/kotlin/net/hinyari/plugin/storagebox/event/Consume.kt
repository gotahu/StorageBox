package net.hinyari.plugin.storagebox.event

import net.hinyari.plugin.storagebox.util.SBUtil
import net.hinyari.plugin.storagebox.StorageBoxMain
import net.hinyari.plugin.storagebox.extensions.*
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class Consume constructor(val plugin: StorageBoxMain)  : Listener {
        
    @EventHandler
    fun onPlayerItemConsumeEvent(event: PlayerItemConsumeEvent) {
        
        val player = event.player
        val consumedItem = event.item
                
        if (consumedItem.isNotStorageBox()) {
            return
        }
        
        if (!player.hasPermission("sb.consume")) {
            event.isCancelled = true
            player.sendMessageWithErrorPrefix(SBUtil.noPermission)
            return 
        }
        
        val isMainHand = player.itemInMainHand == event.item
        
        // オメーのStorageBoxの容量０以下だからぁ！！
        if (SBUtil.getAmountOfStorageBox(consumedItem) <= 0) {
            event.isCancelled = true
            player.sendMessageWithErrorPrefix("アイテムを補充して下さい！")
            consumedItem.amount = 1
            
            return
        }
        
        // 時間差をつけて減らす
        // いったん消費させてから、新たにアイテムを追加する方式
        object : BukkitRunnable() {
            override fun run() {
                if (isMainHand) {
                    player.itemInMainHand = consumedItem.toStorageBox(SBUtil.getAmountOfStorageBox(consumedItem) - 1)
                    //player.itemInMainHand.amount = 1
                } else if (player.itemInOffHand == event.item){
                    player.itemInOffHand = consumedItem.toStorageBox(SBUtil.getAmountOfStorageBox(consumedItem) - 1)
                    
                    //player.itemInOffHand.amount = 1
                }
                
                // TODO: 他にも消費アイテム調査
                if (consumedItem.type == Material.MILK_BUCKET) {
                    player.giveItem(ItemStack(Material.BUCKET), 1)
                }
                
                consumedItem.amount = 1
            }
        }.runTaskLater(plugin, 1L)
    }
}
