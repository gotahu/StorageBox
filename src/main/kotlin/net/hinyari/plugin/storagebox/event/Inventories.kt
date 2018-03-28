package net.hinyari.plugin.storagebox.event

import net.hinyari.plugin.storagebox.Config
import net.hinyari.plugin.storagebox.StorageBoxMain
import net.hinyari.plugin.storagebox.util.SBUtil
import org.bukkit.Material
import org.bukkit.entity.Player
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
        val player = event.whoClicked as Player
        
        if (isStorageBoxInventory(clickedInventory)) {
            // とりあえずキャンセル
            event.isCancelled = true
            val slot = event.slot
            
            
            // レシピ登録用インベントリ
            if (isRecipeRegisterInventory(clickedInventory)) {
                val rrArray = arrayOf(1, 2, 3, 10, 11, 12, 19, 20, 21)
                
                
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
            } else if (isRegisterStorageBoxInventory(clickedInventory)) {
                val itemOn11 = clickedInventory.getItem(11)
                
                // 登録ボタンだった
                if (slot == 15) {
                    if (itemOn11 == null || itemOn11.type == Material.AIR) {
                        player.sendMessage("${SBUtil.errorPrefix}アイテムを指定して下さい!")
                        return
                    }

                    val type = itemOn11.type
                    // 作成できないMaterialだった
                    if (Config.values.uncreatableMaterialList.contains(type)) {
                        player.sendMessage("${SBUtil.errorPrefix}そのアイテムの作成は許可されていません!")
                        return
                    }

                    if (SBUtil.isStorageBox(itemOn11)) {
                        player.sendMessage("${SBUtil.errorPrefix}StorageBoxの中にStorageBoxを含めることは出来ません!")
                        return
                    }

                    // エンチャントが合った場合（注意喚起）
                    if (itemOn11.enchantments.isNotEmpty()) {
                        player.sendMessage("${SBUtil.prefix}異なるエンチャントを含むアイテムを一緒にしまうことは出来ません。")
                    }

                    // 耐久値が違う場合（注意喚起）
                    if (type.maxDurability != itemOn11.durability) {
                        player.sendMessage("${SBUtil.prefix}耐久値の異なるアイテムを一緒にしまうことは出来ません。")
                    }
                    
                    clickedInventory.setItem(11, ItemStack(Material.AIR))

                    // インベントリを閉じる
                    player.closeInventory()
                    
                    // メインハンドにStorageBoxを持っていた場合
                    if (plugin.registerChest == player.inventory.itemInMainHand) {
                        player.inventory.itemInMainHand = ItemStack(Material.AIR)
                        player.inventory.itemInMainHand = SBUtil.createStorageBox(itemOn11, itemOn11.amount, player)
                    } else {
                        player.inventory.itemInOffHand = ItemStack(Material.AIR)
                        player.inventory.itemInOffHand = SBUtil.createStorageBox(itemOn11, itemOn11.amount, player)
                    }
                    
                    player.sendMessage("${SBUtil.prefix}StorageBoxを作成しました!")
                } else if (slot == 11) {
                    event.isCancelled = false
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
        if (inventory.location != null) {
            return false
        }
        
        return inventory.name.contains("StorageBox : ")
    }
    
    private fun isRecipeRegisterInventory(inventory: Inventory?) : Boolean {
        if (inventory == null) {
            return false
        }
        return inventory.name.contains("レシピ作成")
    }
    
    private fun isRegisterStorageBoxInventory(inventory: Inventory?) : Boolean {
        if (inventory == null) {
            return false
        }
        return inventory.name.contains("アイテム登録")
    }
        
}
