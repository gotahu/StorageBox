package net.hinyari.plugin.storagebox.event

import net.hinyari.plugin.storagebox.util.SBUtil
import net.hinyari.plugin.storagebox.StorageBoxMain
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class Consume : Listener {
    
    
    private val plugin = StorageBoxMain.instance
    
    @EventHandler
    fun onPlayerItemConsumeEvent(event: PlayerItemConsumeEvent) {
        
        val player = event.player
                
        if (!SBUtil.isStorageBox(event.item)) {
            return
        }
        
        if (!player.hasPermission("sb.consume")) {
            player.sendMessage("${SBUtil.errorPrefix}権限がありません。")
            event.isCancelled = true
            return
        }
        
        val consumedItem = event.item
        
        val isMainHand = player.inventory.itemInMainHand == event.item

        if (SBUtil.getOwnerOfStorageBox(consumedItem) != player) {
            player.sendMessage("${SBUtil.errorPrefix}所有者の異なるStorageBoxのアイテムを消費することは出来ません！")
            player.sendMessage("${SBUtil.prefix}所有者を変更するには [/sb owner <変更したい所有者名>] を実行して下さい。")
        }
        
        // オメーのStorageBoxの容量０以下だからぁ！！
        if (SBUtil.getAmountOfStorageBox(consumedItem) <= 0) {
            event.isCancelled = true
            player.sendMessage("${SBUtil.errorPrefix}アイテムを補充して下さい！")
            consumedItem.amount = 1
            
            return
        }
        
        // 時間差をつけて減らす
        // いったん消費させてから、新たにアイテムを追加する方式
        object : BukkitRunnable() {
            override fun run() {
                if (isMainHand) {
                    player.inventory.itemInMainHand = 
                            SBUtil.createStorageBox(consumedItem, SBUtil.getAmountOfStorageBox(consumedItem) - 1, player)
                    //player.inventory.itemInMainHand.amount = 1
                } else if (player.inventory.itemInOffHand == event.item){
                    player.inventory.itemInOffHand = 
                            SBUtil.createStorageBox(consumedItem, SBUtil.getAmountOfStorageBox(consumedItem) - 1, player)
                    
                    //player.inventory.itemInOffHand.amount = 1
                }
                
                if (consumedItem.type == Material.MILK_BUCKET) {
                    SBUtil.giveItemToPlayer(player, ItemStack(Material.BUCKET), 1)
                }
                
                consumedItem.amount = 1
                
            }
        }.runTaskLater(plugin, 1L)
        
        
        
        
    }
    
    
}
