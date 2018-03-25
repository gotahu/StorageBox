package net.hinyari.plugin.storagebox

import net.hinyari.plugin.storagebox.event.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class StorageBoxMain : JavaPlugin() {
    
    private val namespacedKey = NamespacedKey(this, this.description.name)
    
    override fun onEnable() {
        instance = this
        init()
        getCommand("storagebox").executor = SBCommandExecutor()
        
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

    val PREFIX_ERROR = "§c§l[ StorageBox ]§r "
    val PREFIX = "§a§l[ StorageBox ]§r "
    val noPerm = "${PREFIX_ERROR}このコマンドを実行する権限がありません！"
    
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
        lateinit var logger: Logger
        
        
    }
    
    
}
