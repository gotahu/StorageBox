package net.hinyari.plugin.storagebox

import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.inventory.ItemStack
import java.util.*

class Config constructor(val plugin: StorageBoxMain) {
    
    private val logger = plugin.log
        
    private var config: FileConfiguration
    private set
    
    companion object {
        lateinit var values: ConfigValues
    }
    
    init {
        // もしconfig.ymlが生成されていなかったら保存する
        plugin.saveDefaultConfig()
        
        // configを取得する
        config = plugin.getConfig()
        
        values = ConfigValues()
    }

    private fun exists(path: String) : Boolean {
        return config.contains(path)
    }
    
    fun getString(path: String) : String {
        return if (exists(path) && config.isString(path)) {
            config.getString(path)
        } else {
            "NULL"
        }
    }
    
    fun getInt(path: String) : Int {
        return if (exists(path) && config.isInt(path)) {
            config.getInt(path)
        } else {
            -1
        }
    }
    
    fun getDouble(path: String) : Double {
        return if (exists(path) && config.isDouble(path)) {
            config.getDouble(path)
        } else {
            Double.NaN
        }
    }
    
    
    fun getStringList(path: String) : List<String> {
        return if (exists(path) && config.isList(path)) {
            val l = mutableListOf<String>()
            for (str in config.getList(path)) {
                l.add(str.toString())
            }
            
            // return
            l
        } else {
            listOf()
        }
    }
    
    
    
    fun getItemStack(path: String) : ItemStack {
        return if (exists(path) && config.isItemStack(path)) {
            config.getItemStack(path)
        } else {
            ItemStack(Material.AIR)
        }
    }
    
    
    fun getBoolean(path: String) : Boolean {
        return if (exists(path) && config.isBoolean(path)) {
            config.getBoolean(path)
        } else {
            false
        }
    }
    
    fun setValue(path: String, value: Any) {
        config.set(path, value)
        
        // configをリロードする
        reloadConfig()
    }

    /**
     * セーブしリロードする
     */
    fun reloadConfig() {
        // セーブする
        plugin.saveConfig()
        
        // config.ymlをデータから読む
        plugin.reloadConfig()
        
        config = plugin.getConfig()
        
        values = ConfigValues()
    }
    
    inner class ConfigValues internal constructor(){
        
        val uncreatableMaterialList = mutableListOf<Material>()
        val enableCraftingStorageBox: Boolean
        val recipe = mutableListOf<ItemStack>()
        
        
        init {
            if (getStringList("disableMaterial").isNotEmpty()) {
                for (str in getStringList("disableMaterial")) {
                    val material: Material? = Material.getMaterial(str.toUpperCase(Locale.ENGLISH))
                    if (material == null) {
                        logger.warn("disableMaterial内 解析できないMaterial ($str)")
                        logger.warn("in disableMaterial unanalyzeable Material ($str)")
                        continue
                    }

                    uncreatableMaterialList.add(material)
                }
            }
            
            enableCraftingStorageBox = getBoolean("crafting.enable")
            
            var isOnlyAir = true
            
            for (i in 0..8) {
                val itemStack = getItemStack("crafting.recipes.$i")
                recipe.add(itemStack)
                
                if (itemStack.type != Material.AIR) {
                    isOnlyAir = false
                }
            }
            
            if (isOnlyAir) {
                // 何もなかったことにするンゴ
                recipe.clear()
            }
        }
        
    }
    
    
    
}
