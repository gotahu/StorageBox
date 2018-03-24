package net.hinyari.plugin.storagebox.event

import net.hinyari.plugin.storagebox.SBUtil
import net.hinyari.plugin.storagebox.StorageBoxMain
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class Interact : Listener {
    
    // StorageBoxの使用に対応するアイテム
    private var materialArray = arrayOf(Material.ENDER_PEARL, Material.MINECART, Material.WATER_BUCKET, Material.LAVA_BUCKET)
    private val plugin =  StorageBoxMain.instance 
    
    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        
        // StorageBoxじゃなかったら論外
        if (!SBUtil.isStorageBox(event.item)) {
            return
        }
        
        val player = event.player
        
        val interactedItem = event.item
        val isMainHand = player.inventory.itemInMainHand == interactedItem
        
        // 持っている物がStorageBoxであった
        //if (SBUtil.isStorageBox(event.item)) {

            //val item = event.item
            val material = interactedItem.type
            //val player = event.player
        
        // スニーク中
        if (player.isSneaking) {
            
            if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) { // 左クリ
                val amount = SBUtil.getAmountOfStorageBox(interactedItem)
                
                if (amount <= 0) {
                    return
                }
                
                println("isMainHand -> $isMainHand")

                // メインハンドにStorageBoxを所持している場合
                when {
                    isMainHand -> 
                        player.inventory.itemInMainHand = SBUtil.createStorageBox(interactedItem, amount - 1, player)
                    
                    player.inventory.itemInOffHand == event.item -> 
                        player.inventory.itemInOffHand = SBUtil.createStorageBox(interactedItem, amount - 1, player)
                    else -> return
                }

                SBUtil.giveItemToPlayer(player, interactedItem, 1)
                player.playSound(player.location, Sound.ENTITY_ITEM_PICKUP, 0.2f, 0.5f)
                
            } else if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) { // 右クリ
                // StorageBoxにまとめる
                if (SBUtil.stackToStorageBox(player, interactedItem) /* ここでまとめる 兼 まとめるアイテムがあったかチェック */) {
                    player.playSound(player.location, Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 1.5f)
                }
            }
        }
            
            // 対応しているアイテムである
            if (materialArray.contains(material)) {
                
                // 個数がない
                if (SBUtil.getAmountOfStorageBox(interactedItem) <= 0) {
                    event.isCancelled = true
                    player.sendMessage("${plugin.PREFIX_ERROR}アイテムを補充して下さい！")
                    interactedItem.amount = 1
                    return
                }
                
                
                //val isMainHand = player.inventory.itemInMainHand == item
                
                // ここで取得しないと消える
                val amount = SBUtil.getAmountOfStorageBox(interactedItem)
                
                
                // 時間をつけて実行する
                object : BukkitRunnable() {
                    override fun run() {
                        
                        val minusOne = amount -1
                        println("minusOne = $minusOne")
                        
                        val afterItem = SBUtil.createStorageBox(interactedItem, minusOne, player)
                        
                        // メインハンドにStorageBoxがある場合
                        when {
                            isMainHand -> {
                                println(amount)
                                println(amount - 1)

                                player.inventory.itemInMainHand = ItemStack(Material.AIR)
                                player.inventory.itemInMainHand = afterItem
                            }
                            player.inventory.itemInOffHand == event.item -> {
                                player.inventory.itemInOffHand = ItemStack(Material.AIR)
                                player.inventory.itemInOffHand = afterItem
                            }
                            else -> return
                        }
                        
                        interactedItem.amount = 1
                        
                        println(afterItem)
                        
                    }
                }.runTaskLater(plugin, 1L) // 1tick後
                
            }
            
        //}
        
        
    }
    
}
