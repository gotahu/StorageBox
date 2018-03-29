package net.hinyari.plugin.storagebox.util

import net.hinyari.plugin.storagebox.extensions.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class SBUtil {
    
    companion object {

        // 愉快な定数たち
        const val errorPrefix = "§c§l[ StorageBox ]§r "
        const val prefix = "§a§l[ StorageBox ]§r "
        const val noPermission = "権限がありません。"
        
        /**
         * StorageBoxに入っているアイテムの個数をリターンします。
         * 
         * @return amount of storagebox
         */
        fun getAmountOfStorageBox(itemStack: ItemStack): Int {
            if (itemStack.isNotStorageBox()) {
                return -1
            }
            return itemStack.itemMeta.lore[2].toInt()
        }
                
        fun stackToStorageBox(player: Player, storagebox: ItemStack) : Boolean {
            
            if (storagebox.isNotStorageBox()) {
                println("failed in stackToStorageBox because given item is not storagebox")
                return false
            }
            
            var hasStackableItem = false
            
            for (item in player.inventory.contents) {
                if (item == null || item.type == Material.AIR) {
                    continue
                }
                
                // 同じMaterialのアイテムが見つかった
                if (item.type == storagebox.type &&
                        item.data.data == storagebox.data.data &&
                        item.durability == storagebox.durability &&
                        item.enchantments == storagebox.enchantments) {
                    
                    // そのアイテムがストレージボックスだったらとりあえず保留？
                    if (item.isStorageBox()) {
                        continue
                    }
                    
                    hasStackableItem = true

                    object : BukkitRunnable() {
                        override fun run() {
                            
                            val isMainHand = storagebox == player.itemInMainHand
                                                        
                            // メインハンドに所持している
                            when {
                                isMainHand -> player.itemInMainHand.toStorageBox(getAmountOfStorageBox(storagebox) + item.amount)
                                player.itemInOffHand == storagebox -> player.itemInOffHand.toStorageBox(
                                        getAmountOfStorageBox(storagebox) + item.amount)
                                else -> return
                            }
                            
                            // アイテムを消す
                            player.inventory.remove(item)
                        }
                    }.runTaskLater(Bukkit.getPluginManager().getPlugin("StorageBox"), 1L)                    
                }
            }
            
            return hasStackableItem
        }
    }
}
