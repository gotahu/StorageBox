package net.hinyari.plugin.storagebox

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class SBUtil {
    
    companion object {

        fun createStorageBox(item: ItemStack, amount: Int, player: Player): ItemStack {
            
            //println("createStorageBox($item, $amount, $player")
            
            if (amount < 0) {
                throw IllegalArgumentException("amount must be more than 0")
            }
            
            val meta = item.itemMeta
            
            if (meta.hasLore()) {
                meta.lore = null
            }
            
            val lore = mutableListOf<String>()
            lore.add("§6§l[ StorageBox ]")
            lore.add(item.type.toString())
            lore.add(amount.toString())
            lore.add(player.uniqueId.toString())
            
            //println("generatedLore -> $lore")
            
            meta.lore = lore
            
            //print("meta's lore -> ${meta.lore}")
            meta.displayName = "§6§l${item.type} : $amount"
            
            
            item.itemMeta = meta
            item.amount = 1
            
            
            return item
        }

        /**
         * アイテムがストレージボックスであるかを判定します。
         * 基本的に初期で使用してください。
         *
         * @param itemStack 判定したいアイテム
         * @return
         */
        fun isStorageBox(itemStack: ItemStack?): Boolean {
            if (itemStack == null || itemStack.type == Material.AIR) {
                return false
            }
            
            if (itemStack.hasItemMeta() && itemStack.itemMeta.hasLore()) {
                    return itemStack.itemMeta.lore[0].contains("StorageBox")
            }
            
            return false
        }

        /**
         * StorageBoxに入っているアイテムの個数をリターンします。
         * 
         * @return amount of storagebox
         */
        fun getAmountOfStorageBox(itemStack: ItemStack): Int {
            if (!isStorageBox(itemStack)) {
                return -1
            }
            
            return itemStack.itemMeta.lore[2].toInt()
        }

        /**
         * StorageBoxを作ったユーザーを特定する
         * 
         * @return owner of storagebox
         */
        fun getOwnerOfStorageBox(itemStack: ItemStack): Player? {
            if (!isStorageBox(itemStack)) {
                return null
            }
                        
            val uuid = UUID.fromString(itemStack.itemMeta.lore[3])
            
            return Bukkit.getPlayer(uuid)
        }

        /**
         * 与えられた文字列が数列であるかを返します
         * 
         * @return isnumberic
         */
        fun isNumeric(string: String): Boolean {
            return string.matches("-?\\d+(\\.\\d+)?".toRegex())
        }

        /**
         * StorageBoxのMaterialを返します
         * @param itemStack StorageBox
         */
        fun getTypeOfStorageBox(itemStack: ItemStack): Material {
            if (!isStorageBox(itemStack)) {
                return Material.AIR
            }
            
            return Material.getMaterial(itemStack.itemMeta.lore[1])
        }
        
        

        /**
         * プレイヤーにアイテムを渡す
         * @param   player      渡したいプレイヤー
         * @param   itemStack   渡したいItemStack
         * @param   amount      渡したい個数
         */
        fun giveItemToPlayer(player: Player, itemStack: ItemStack, amount: Int) {
            
            val world = player.world
            val location = player.location
            
            var itemAmount = amount
            
            var stacks = amount / 64
            val fraction = amount % 64
            
            val inventory = player.inventory
            
            // １スタックに満たない、もしくは64個の場合
            if (stacks == 0 || amount == 64) {
                // 空きがなかった
                if (countEmptySlots(inventory) == 0) {
                    world.dropItemNaturally(location, ItemStack(itemStack.type))
                } else { // 空きがあった
                    inventory.addItem(ItemStack(itemStack.type, amount))
                }
                return
            } else { // 1スタック（６５個以上）
                                
                // インベントリに空きがない場合
                if (countEmptySlots(inventory) == 0) {
                    
                    while (stacks == 0) {
                        val cloned = itemStack.clone()
                        cloned.amount = 64
                        
                        world.dropItemNaturally(location, cloned)
                        
                        stacks--
                    }
                    
                    // 端数がある場合
                    if (fraction != 0) {
                        val cloned = itemStack.clone()
                        cloned.amount = fraction
                        
                        world.dropItemNaturally(location, cloned)
                    }
                    
                } else if (countEmptySlots(inventory) < stacks) { 
                    // インベントリに「十分な」空きがない場合
                    
                    var emptySlots = countEmptySlots(inventory)
                    
                    // 空のインベントリが埋まるまで
                    while (emptySlots == 0) {
                        val cloned = itemStack.clone()
                        cloned.amount = 64
                        
                        itemAmount -= 64
                        
                        inventory.addItem(cloned)
                        
                        emptySlots = countEmptySlots(inventory)
                    }
                    
                    stacks = itemAmount / 64
                    
                    while (stacks == 0) {
                        val cloned = itemStack.clone()
                        cloned.amount = 64

                        world.dropItemNaturally(location, cloned)

                        itemAmount -= 64
                        stacks--
                    }

                    // 端数がある場合
                    if (itemAmount != 0) {
                        val cloned = itemStack.clone()
                        cloned.amount = itemAmount

                        world.dropItemNaturally(location, cloned)
                    }
                } else { // 空きがあるならそのまま追加
                    inventory.addItem(ItemStack(itemStack.type, amount))
                }
            }
        }
                
        
        fun stackToStorageBox(player: Player, storagebox: ItemStack) : Boolean {
            
            if (!isStorageBox(storagebox)) {
                println("failed in stackToStorageBox because given item is not storagebox")
                return false
            }
            
            var hasStackableItem = false
            
            
            for (item in player.inventory.contents) {

                if (item == null || item.type == Material.AIR)
                    continue

                // 同じMaterialのアイテムが見つかった
                if (item.type == storagebox.type) {
                    // そのアイテムがストレージボックスだったらとりあえず保留？
                    if (SBUtil.isStorageBox(item)) {
                        continue
                    }
                    
                    hasStackableItem = true

                    object : BukkitRunnable() {
                        override fun run() {
                            
                            val isMainHand = storagebox == player.inventory.itemInMainHand
                            println("isMainHand -> $isMainHand")
                                                        
                            // メインハンドに所持している
                            when {
                                isMainHand -> player.inventory.itemInMainHand =
                                        SBUtil.createStorageBox(storagebox,
                                                SBUtil.getAmountOfStorageBox(storagebox) + item.amount,
                                                player)
                                player.inventory.itemInOffHand == storagebox -> player.inventory.itemInOffHand =
                                        SBUtil.createStorageBox(storagebox,
                                                SBUtil.getAmountOfStorageBox(storagebox) + item.amount,
                                                player)
                                else -> return
                            }
                            
                            // アイテムを消す
                            player.inventory.setItem(player.inventory.indexOf(item), null)
                        }
                        
                    }.runTaskLater(StorageBoxMain.instance, 1L)                    
                }
            }
            
            return hasStackableItem
        }
                
        private fun countEmptySlots(inventory: Inventory) : Int {
            var count = 0
            for (item in inventory.contents) {
                if (item == null) {
                    count++
                }
            }

            return count
        }


    }

    
    
    
    
}
