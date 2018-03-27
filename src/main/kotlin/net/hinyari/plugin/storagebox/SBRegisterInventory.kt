package net.hinyari.plugin.storagebox

import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import java.util.*

class SBRegisterInventory {
    
    private val registerInventory = Bukkit.createInventory(null, 27, "StorageBox : 登録画面")
    val inventories = mutableMapOf<UUID, Inventory>()
    
    init {
        // TODO ごにょごにょする
    }
    
    fun getRegisterInventoryByUUID(uuid: UUID) : Inventory {
        return if (inventories[uuid] != null)
            inventories[uuid]!!
        else {
            inventories[uuid] = registerInventory
            registerInventory
        }
    }
    
    
}
