package net.hinyari.plugin.storagebox.util

import net.hinyari.plugin.storagebox.StorageBoxMain
import org.bukkit.Bukkit
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class SBUtil {
    
    companion object {

        // 愉快な定数たち
        const val errorPrefix = "§c§l[ StorageBox ]§r "
        const val prefix = "§a§l[ StorageBox ]§r "
        const val noPermission = "${errorPrefix}このコマンドを実行する権限がありません！"

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

        fun spawnSmoke(location: Location) {
            location.world.playEffect(location.add(0.0, 1.0, 0.0), Effect.SMOKE, 4)
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
            
            println(countEmptySlots(player.inventory))
            
            val world = player.world
            val location = player.location
            
            val clonedItem = itemStack.clone()
            clonedItem.itemMeta = Bukkit.getItemFactory().getItemMeta(clonedItem.type)
            
            var itemAmount = amount
            
            var stacks = amount / 64
            val fraction = amount % 64
            
            val inventory = player.inventory
            
            // １スタックに満たない、もしくは64個の場合
            if (stacks == 0 || amount == 64) {
                // 空きがなかった
                if (countEmptySlots(inventory) == 0) {
                    world.dropItemNaturally(location, clonedItem)
                } else { // 空きがあった
                    clonedItem.amount = amount
                    inventory.addItem(clonedItem)
                }
                return
            } else { // 1スタック（６５個以上）
                                
                // インベントリに空きがない場合
                if (countEmptySlots(inventory) == 0) {
                    
                    while (stacks == 0) {
                        clonedItem.amount = 64
                        
                        world.dropItemNaturally(location, clonedItem)
                        
                        stacks--
                    }
                    
                    // 端数がある場合
                    if (fraction != 0) {
                        clonedItem.amount = fraction
                        
                        world.dropItemNaturally(location, clonedItem)
                    }
                    
                } else if (countEmptySlots(inventory) < stacks) { 
                    // インベントリに「十分な」空きがない場合
                    
                    var emptySlots = countEmptySlots(inventory)
                    
                    // 空のインベントリが埋まるまで
                    while (emptySlots == 0) {
                        clonedItem.amount = 64
                        
                        itemAmount -= 64
                        
                        inventory.addItem(clonedItem)
                        
                        emptySlots = countEmptySlots(inventory)
                    }
                    
                    stacks = itemAmount / 64
                    
                    while (stacks == 0) {
                        clonedItem.amount = 64

                        world.dropItemNaturally(location, clonedItem)

                        itemAmount -= 64
                        stacks--
                    }

                    // 端数がある場合
                    if (itemAmount != 0) {
                        clonedItem.amount = itemAmount

                        world.dropItemNaturally(location, clonedItem)
                    }
                } else { // 空きがあるならそのまま追加
                    clonedItem.amount = amount
                    inventory.addItem(clonedItem)
                }
            }
        }
        
        fun setOwnerOfStorageBox(storagebox: ItemStack, player: Player) : ItemStack? {
            if (!isStorageBox(storagebox)) {
                return null
            }
            
            storagebox.itemMeta.lore[3] = player.uniqueId.toString()
            return storagebox
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
                if (item.type == storagebox.type &&
                        item.data.data == storagebox.data.data &&
                        item.durability == storagebox.durability &&
                        item.enchantments == storagebox.enchantments) {
                    // そのアイテムがストレージボックスだったらとりあえず保留？
                    if (isStorageBox(item)) {
                        continue
                    }
                    
                    hasStackableItem = true

                    object : BukkitRunnable() {
                        override fun run() {
                            
                            val isMainHand = storagebox == player.inventory.itemInMainHand
                                                        
                            // メインハンドに所持している
                            when {
                                isMainHand -> player.inventory.itemInMainHand =
                                        createStorageBox(storagebox,
                                                getAmountOfStorageBox(
                                                        storagebox) + item.amount,
                                                player)
                                player.inventory.itemInOffHand == storagebox -> player.inventory.itemInOffHand =
                                        createStorageBox(storagebox,
                                                getAmountOfStorageBox(
                                                        storagebox) + item.amount,
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
            return 36 - inventory.contents.size
        }


    }

    
    
    
    
}
