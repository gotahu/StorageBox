package net.hinyari.plugin.storagebox

import ItemUtils
import com.sun.org.apache.regexp.internal.RE
import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

class SBRegisterInventory {

    private val registerInventory = Bukkit.createInventory(null, 27, "StorageBox : 登録")
    val recipeRegister: Inventory
    val inventories = mutableMapOf<UUID, Inventory>()

    init {
        // レシピ登録インベントリセットアップ
        recipeRegister = fillInventory(Bukkit.createInventory(null, 27, "Storagebox : レシピ作成"),
                ItemUtils.createColorableItem(Material.STAINED_GLASS_PANE, "", arrayOf(""), DyeColor.GRAY.woolData))

        recipeRegister.setItem(15,
                ItemUtils.createColorableItem(Material.STAINED_CLAY, "§a§lOK", arrayOf(""), DyeColor.LIME.woolData))
        
        val recipes = Config.values.recipe
        val rrArray = arrayOf(1,2,3,10,11,12,19,20,21)
        for (i in 0 until 9) {
            if (recipes.isEmpty()) {
                recipeRegister.setItem(rrArray[i], ItemStack(Material.AIR))
            } else {
                recipeRegister.setItem(rrArray[i], recipes[i])
            }
        }
    }

    fun getRegisterInventoryByUUID(uuid: UUID): Inventory {
        return if (inventories[uuid] != null)
            inventories[uuid]!!
        else {
            inventories[uuid] = registerInventory
            registerInventory
        }
    }

    private fun fillInventory(inventory: Inventory, itemStack: ItemStack): Inventory {
        for (i in 0 until inventory.size) {
            inventory.setItem(i, itemStack)
        }
        return inventory
    }


}
