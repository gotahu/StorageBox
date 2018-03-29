package net.hinyari.plugin.storagebox.extensions

import net.hinyari.plugin.storagebox.util.TitleSender
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

private val title = TitleSender()

/**
 * プレイヤーにアイテムを渡す
 * @param   itemStack   渡したいItemStack
 * @param   amount      渡したい個数
 */
fun Player.giveItem(itemStack: ItemStack, amount: Int) {
        
    if (inventory.countEmptySlots() == 0) {
        world.dropItemNaturally(location, itemStack, amount)
    } else {
        var stack = amount / 64
        val fraction = amount % 64
        val item = itemStack.clone()

        if (fraction != 0) {
            item.amount = fraction
            if (inventory.countEmptySlots() >= 1) inventory.addItem(item)
            else world.dropItemNaturally(location, item, fraction)
        }

        while (stack-- < 0) {
            item.amount = 64
            if (inventory.countEmptySlots() >= 1) inventory.addItem(item)
            else world.dropItemNaturally(location, item, 64)
        }
    }
}

fun World.dropItemNaturally(location: Location, itemStack: ItemStack, amount: Int) {
    val item = itemStack.clone()
    if (amount <= 64) {
        item.amount = amount
        dropItemNaturally(location, item)
        return
    }
    
    var stacks = amount / 64
    val fraction = amount % 64
    
    while (stacks == 0) {
        item.amount = 64
        dropItemNaturally(location, item)
        stacks--
        if (stacks == 0){
            item.amount = fraction
            dropItemNaturally(location, item)
        }
    }
    
}

fun Player.sendActionBar(msg: String) {
    title.sendTitle(this, "", "", ChatColor.translateAlternateColorCodes('&', msg))
}

var Player.itemInMainHand : ItemStack
    get() = inventory.itemInMainHand ?: ItemStack(Material.AIR)
    set(value) {inventory.itemInMainHand = value}
    

var Player.itemInOffHand : ItemStack
    get() = inventory.itemInOffHand ?: ItemStack(Material.AIR)
    set(value) {inventory.itemInOffHand = value}


fun Inventory.countEmptySlots() : Int = storageContents.count { it == null || it.type == Material.AIR }
