package net.hinyari.plugin.storagebox

import net.hinyari.plugin.storagebox.event.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandExecutor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.material.MaterialData
import org.bukkit.plugin.java.JavaPlugin

class StorageBoxMain : JavaPlugin() {

    private val namespacedKey = NamespacedKey(this, this.description.name)
    lateinit var inventoryUtil : SBRegisterInventory
    lateinit var config: Config

    val log = net.hinyari.plugin.storagebox.Logger(this.logger)

    override fun onEnable() {
        // 順番大事
        instance = this
        config = Config()
        inventoryUtil = SBRegisterInventory()

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
        reloadRecipe()
    }
    
    lateinit var registerChest : ItemStack

    fun reloadRecipe() {
        if (Config.values.enableCraftingStorageBox) {
            val rawRecipe = Config.values.recipe
            if (rawRecipe.isNotEmpty()) {
                // 完成品
                registerChest = ItemStack(Material.CHEST)
                val meta = registerChest.itemMeta
                meta.displayName = "§6§lStorageBox : §r§6未登録"
                meta.lore = listOf("§f右クリックでアイテムを登録")
                registerChest.itemMeta = meta

                val recipeList = server.getRecipesFor(registerChest)
                if (recipeList.isNotEmpty()) {
                    server.clearRecipes()
                }

                // レシピ作成部
                val recipe1 = ShapedRecipe(namespacedKey, registerChest).shape("123", "456", "789")

                for (i in 1 until 10) {
                    recipe1.setIngredient(i.toString().toCharArray()[0], rawRecipe[i - 1].data)
                    println(i.toString().toCharArray()[0] + ", ${rawRecipe[i - 1].data}")
                }

                server.addRecipe(recipe1)

                log.info("レシピを追加しました！")
            }
        }
    }


    var debugMode: Boolean = false

    fun debug(string: String) {
        if (debugMode) {
            Bukkit.broadcast(string, "sb.debug")
        }
    }

    fun toggleDebugMode(): Boolean {
        debugMode = !debugMode
        return debugMode
    }

    companion object {
        lateinit var instance: StorageBoxMain
    }


}
