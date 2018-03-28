package net.hinyari.plugin.storagebox.event

import net.hinyari.plugin.storagebox.StorageBoxMain
import net.hinyari.plugin.storagebox.util.SBUtil
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class Inventories : Listener {
    
    private val plugin = StorageBoxMain.instance
    
    @EventHandler
    fun onInventoryClickEvent(event: InventoryClickEvent) {  
        val clickedInventory = event.clickedInventory
        val player = event.whoClicked
        
        if (isStorageBoxInventory(clickedInventory)) {
            // とりあえずキャンセル
            event.isCancelled = true
            
            
            // レシピ登録用インベントリ
            if (isRecipeRegisterInventory(clickedInventory)) {
                val rrArray = arrayOf(1, 2, 3, 10, 11, 12, 19, 20, 21)
                val slot = event.slot
                
                // クリックしたのがスロットだった
                if (rrArray.contains(slot)) {
                    // 移動を有効化
                    event.isCancelled = false
                }
                
                // 権限あるかな～
                if (player.hasPermission("sb.modifyrecipe")) {
                    
                    
                    // 登録ボタン
                    if (slot == 15) {
                        var isAllEmpty = true
                        
                        // スロットを一つ一つ取得して、登録する
                        for (i in 0 until 9) {
                            if (clickedInventory.getItem(rrArray[i]) == null) {
                                plugin.config.setValue("crafting.recipes.$i", ItemStack(Material.AIR))
                                continue
                            } else {
                                isAllEmpty = false
                                plugin.config.setValue("crafting.recipes.$i", clickedInventory.getItem(rrArray[i]))
                            }
                        }
                        if (isAllEmpty) {
                            player.sendMessage("${SBUtil.errorPrefix}レシピの材料を置いて下さい!")
                            return
                        }
                        
                        player.sendMessage("${SBUtil.prefix}レシピを登録しました!")
                        plugin.config.reloadConfig()
                        plugin.reloadRecipe()
                        player.closeInventory()
                    }
                }
            }
        }
    }
    
    @EventHandler
    fun onInventoryMoveEvent(event: InventoryMoveItemEvent) {
        if (isStorageBoxInventory(event.source)) {
            event.isCancelled = true
        }
    }
    
    @EventHandler
    fun onInventoryDragEvent(event: InventoryDragEvent) {
        if (isStorageBoxInventory(event.inventory)) {
            event.isCancelled = true
        }
    }
    
    @EventHandler
    fun onInventoryCloseEvent(event: InventoryCloseEvent) {
    }
    
    private fun isStorageBoxInventory(inventory: Inventory?) : Boolean {
        if (inventory == null) {
            return false
        }
        return inventory.name.contains("Storagebox : ")
    }
    
    private fun isRecipeRegisterInventory(inventory: Inventory?) : Boolean {
        if (inventory == null) {
            return false
        }
        return inventory.name.contains("レシピ作成")
    }
        
}
