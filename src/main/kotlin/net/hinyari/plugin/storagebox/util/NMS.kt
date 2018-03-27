package net.hinyari.plugin.storagebox

import org.bukkit.Bukkit
import java.util.logging.Level


class NMS {

    private var version = ""
    private val logger = StorageBoxMain.instance.logger

    init {
        
        logger.info("Your server version is ${Bukkit.getVersion()}")
        
        version = Bukkit.getBukkitVersion().split('-')[0]
        
        // それっぽいログを出しておく
        logger.info("Identified version ($version)")

        version =
                when (version) {
                    "1.8", "1.8.1" -> "v1_8_R1"
                    "1.8.3" -> "v1_8_R2"
                    "1.8.4", "1.8.5", "1.8.6", "1.8.7" -> "v1_8_R3"
                    "1.9", "1.9.2" -> "v1_9_R1"
                    "1.9.4" -> "v1_9_R2"
                    "1.10", "1.10.2" -> "v1_10_R1"
                    "1.11", "1.11.1", "1.11.2" -> "v1_11_R1"
                    "1.12", "1.12.1", "1.12.2" -> "v1_12_R1"
                    else -> "unsupported"
                }
        
        logger.info("Craftbukkit version ($version)")
        
        if (version == "unsupported") {
            logger.info("------------------------------------")
            logger.log(Level.WARNING, "このプラグインはお使いのサーバーのバージョンに対応していません。")
            logger.log(Level.WARNING, "対応しているバージョンは1.9～1.12.xです。")
            logger.log(Level.WARNING, "プラグインを無効化します。")
            logger.log(Level.WARNING, "")
            // 無駄に英語をかましておく（ガバガバイングリッシュ）
            logger.log(Level.WARNING, "This plugin does not support your version ($version)")
            logger.log(Level.WARNING, "And versions supported by this plugin are from 1.9 to 1.12.x")
            logger.log(Level.WARNING, "Please upgrade your server version to use this!")
            logger.info("------------------------------------")
            
            // 無効化
            Bukkit.getPluginManager().disablePlugin(StorageBoxMain.instance)
        }
    }

    fun getNMSClass(name: String): Class<*>? {
        return try {
            Class.forName("net.minecraft.server.$version.$name")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            null
        }
    }
    
    fun getBukkitNMSClass(name: String): Class<*>? {
        return try {
            Class.forName("org.bukkit.craftbukkit.$version.$name")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}
