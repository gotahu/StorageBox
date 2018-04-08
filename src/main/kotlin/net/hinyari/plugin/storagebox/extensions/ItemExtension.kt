package net.hinyari.plugin.storagebox.extensions

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

fun ItemStack.hasLore(): Boolean {
    return hasItemMeta() && itemMeta.hasLore()
}

/**
 * アイテムがストレージボックスであるかを判定します。
 * 基本的に初期で使用してください。
 *
 * @return
 */
fun ItemStack.isStorageBox(): Boolean {
    if (type == Material.AIR) {
        return false
    }
    
    if (hasLore()) {
        return itemMeta.lore[0].contains("StorageBox")
    }

    return false
}

val Material.isTool: Boolean
    get() = Enchantment.DURABILITY.canEnchantItem(ItemStack(this))

val ItemStack.displayName: String
    get() = if (hasItemMeta() && itemMeta.hasDisplayName()) itemMeta.displayName else ""

fun ItemStack.reset() : ItemStack {    
    val i = ItemStack(type)
    i.addEnchantments(enchantments)
    i.durability = this.durability
    
    return i
}

/**
 * アイテムがストレージボックスでない場合trueを返します。
 */
fun ItemStack.isNotStorageBox(): Boolean = !isStorageBox()

fun ItemStack.toStorageBox(amount: Int): ItemStack {

    if (amount < 0) {
        throw IllegalArgumentException("amount must be more than 0")
    }
    
    if (type == Material.AIR) {
        println("toStorageBox() {type=$type}")
        return ItemStack(Material.AIR)
    }

    val meta = itemMeta

    if (hasLore()) {
        meta.lore = null
    }

    val lore = mutableListOf<String>()
    lore.add("§6§l[ StorageBox ]")
    lore.add(type.toString())
    lore.add(amount.toString())

    meta.lore = lore

    //print("meta's lore -> ${meta.lore}")
    meta.displayName = "§6§l$type : $amount"


    itemMeta = meta
    this.amount = 1

    return this
}

