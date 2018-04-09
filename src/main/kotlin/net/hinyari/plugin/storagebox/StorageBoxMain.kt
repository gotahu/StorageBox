package net.hinyari.plugin.storagebox

import net.hinyari.plugin.storagebox.event.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import java.awt.Shape

class StorageBoxMain : JavaPlugin() {

    private val namespacedKey = NamespacedKey(this, this.description.name)
    lateinit var inventoryUtil : SBRegisterInventory
    lateinit var config: Config

    val log = net.hinyari.plugin.storagebox.Logger(this.logger)

    override fun onEnable() {
        // 順番大事
        config = Config(this)
        inventoryUtil = SBRegisterInventory()

        init()
        getCommand("storagebox").executor = Command(this)
        
        registerListeners(
                Place(this),
                Pickup(this),
                Consume(this),
                Interact(this),
                Break(),
                Inventories(this))
    }
    
    private fun registerListeners(vararg listeners : Listener) {
        val pm = server.pluginManager
        listeners.forEach { listener -> pm.registerEvents(listener, this)
        log.info("${listener.javaClass.name} is registered.")}
    }

    private fun init() {
        // 完成品
        registerChest = ItemStack(Material.CHEST)
        val meta = registerChest.itemMeta
        meta.displayName = "§6§lStorageBox : §r§6未登録"
        meta.lore = listOf("§f右クリックでアイテムを登録")
        registerChest.itemMeta = meta
        
        reloadRecipe()
    }
    
    lateinit var registerChest : ItemStack

    fun reloadRecipe() {
        if (Config.values.enableCraftingStorageBox) {
            val rawRecipe = Config.values.recipe
            
            // レシピがconfigに定義されていたら
            if (rawRecipe.isNotEmpty()) {
                val recipeList = server.getRecipesFor(registerChest)
                if (recipeList.isNotEmpty()) {
                    val iterator = server.recipeIterator()
                    while (iterator.hasNext()) {
                        val r = iterator.next()
                        if (r is ShapedRecipe && r == recipeList[0]) {
                            iterator.remove()
                        }
                    }
                }

                // レシピ作成部
                val recipe1 = ShapedRecipe(namespacedKey, registerChest).shape("123", "456", "789")

                for (i in 1 until 10) {
                    recipe1.setIngredient(i.toString().toCharArray()[0], rawRecipe[i - 1].data)
                    println(i.toString().toCharArray()[0] + ", ${rawRecipe[i - 1].data}")
                }

                server.addRecipe(recipe1)

                log.info("レシピを追加しました！")
            } else {
                log.info("レシピが追加されていません。/sb recipeで設定をして下さい。")
            }
        } else {
            log.info("レシピは無効化されました。")
        }
    }


    private var debugMode: Boolean = false

    fun debug(string: String) {
        if (debugMode) {
            Bukkit.broadcast(string, "sb.debug")
        }
    }

    fun toggleDebugMode(): Boolean {
        debugMode = !debugMode
        return debugMode
    }
}
