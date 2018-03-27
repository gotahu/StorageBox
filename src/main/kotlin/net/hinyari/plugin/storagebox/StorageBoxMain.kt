package net.hinyari.plugin.storagebox

import net.hinyari.plugin.storagebox.event.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandExecutor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger
import java.util.regex.Pattern

class StorageBoxMain : JavaPlugin() {
    
    private val namespacedKey = NamespacedKey(this, this.description.name)
    val inventoryUtil = SBRegisterInventory()
    
    override fun onEnable() {
        instance = this
        init()
        getCommand("storagebox").executor = Command()
        
        getCommand("heal").executor = CommandExecutor { sender, _, _, _ ->
            if (sender is Player) {
                val player: Player = sender
                
                if (player.hasPermission("sb.heal")) {
                    player.health = 20.0
                    player.sendMessage("&6You've been healed!")
                }
                
                return@CommandExecutor true
            }
            return@CommandExecutor true
        }
        
        val pm = Bukkit.getPluginManager()
        pm.registerEvents(Place(), this)
        pm.registerEvents(Pickup(), this)
        pm.registerEvents(Consume(), this)
        pm.registerEvents(Interact(), this)
        pm.registerEvents(Break(), this)
        pm.registerEvents(Inventories(), this)
        
        logger.info(ItemUtils.getItemOriginalName(ItemStack(Material.WOOD)))
        
        
        
    }

    private fun init() {
        val chestItemStack = ItemStack(Material.CHEST)
        val meta = chestItemStack.itemMeta
        
        
        meta.displayName = "&6&lStorageBox : &r&6未登録"
        meta.lore = listOf("&f右クリックでアイテムを登録")
        
        chestItemStack.itemMeta = meta
        
        val recipe1 = ShapedRecipe(namespacedKey, chestItemStack)
        recipe1.shape("***", "* *", "***")
        recipe1.setIngredient('*', Material.CHEST)

        server.addRecipe(recipe1)
        

    }

    
    
    var debugMode: Boolean = false
    
    fun debug(string: String)
    {
        if (debugMode) {
            Bukkit.broadcast(string, "sb.debug")
        }
    }
    
    fun toggleDebugMode() : Boolean {
        debugMode = !debugMode
        return debugMode
    }
    
    companion object {
        lateinit var instance: StorageBoxMain
    }
    
    
}
