package net.hinyari.plugin.storagebox.event

import net.hinyari.plugin.storagebox.Config
import net.hinyari.plugin.storagebox.util.SBUtil
import net.hinyari.plugin.storagebox.StorageBoxMain
import org.bukkit.*
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class Place : Listener {
    
    private val plugin = StorageBoxMain.instance
    private var triedTimes = 0
    //private val nms = NMS()
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {

        val player = event.player

        val itemInMainHand = player.inventory.itemInMainHand
        val itemInOffHand = player.inventory.itemInOffHand

        val isMain = SBUtil.isStorageBox(itemInMainHand)

        val hasStorageBox = isMain || SBUtil.isStorageBox(itemInOffHand)

        // 未登録のStorageBoxだったら
        if (event.itemInHand.hasItemMeta() && event.itemInHand.itemMeta.displayName == "§6§lStorageBox : §r§6未登録") {
            event.isCancelled = true
        }

        // StorageBoxを持っていなかった場合
        if (!hasStorageBox) {
            return
        }

        // 設置権限を持っていなかった場合
        if (!player.hasPermission("sb.place")) {
            player.sendMessage(SBUtil.noPermission)
            event.isCancelled = true
            return
        }
        
        // 無効化リストに入っていた場合
        if (Config.values.uncreatableMaterialList.contains(event.itemInHand.type)) {
            player.sendMessage("${SBUtil.errorPrefix}そのアイテムを設置することは出来ません!")
            return
        }
        
        val block = event.block
        val blocktype = block.type        

        // メインハンドに持っていた場合
        if (isMain) {

            // プレイヤーのゲームモードがクリエイティブだった場合
            if (player.gameMode == GameMode.CREATIVE) {
                itemInMainHand.amount = 1
                player.inventory.itemInMainHand = SBUtil.createStorageBox(itemInMainHand,
                        SBUtil.getAmountOfStorageBox(itemInMainHand), player)
                return
            }

            // 容量が無かった場合
            if (SBUtil.getAmountOfStorageBox(itemInMainHand) <= 0) {
                event.setBuild(false)
                itemInMainHand.amount = 1

                if (triedTimes == 0 || triedTimes % 4 == 0) {
                    player.sendMessage("${SBUtil.errorPrefix}アイテムを補充して下さい！")
                }
                SBUtil.spawnSmoke(event.block.location)
                player.playSound(player.location, Sound.BLOCK_DISPENSER_FAIL, 0.4f, 1.0f)
                
                triedTimes++
                return
            }

            itemInMainHand.amount = 1
            player.inventory.itemInMainHand = SBUtil.createStorageBox(itemInMainHand,
                    SBUtil.getAmountOfStorageBox(itemInMainHand) - 1, player)


        } else { // オフハンドに持っていた場合

            // プレイヤーのゲームモードがクリエイティブだった場合
            if (player.gameMode == GameMode.CREATIVE) {
                itemInOffHand.amount = 1
                player.inventory.itemInOffHand = SBUtil.createStorageBox(itemInOffHand,
                        SBUtil.getAmountOfStorageBox(itemInOffHand), player)
                return
            }

            // 容量が無かった場合
            if (SBUtil.getAmountOfStorageBox(itemInOffHand) <= 0) {
                event.setBuild(false)
                itemInOffHand.amount = 1

                if (triedTimes == 0 || triedTimes % 4 == 0) {
                    player.sendMessage("${SBUtil.errorPrefix}アイテムを補充して下さい！")
                }
                SBUtil.spawnSmoke(event.block.location)
                player.playSound(player.location, Sound.BLOCK_DISPENSER_FAIL, 0.4f, 1.0f)

                triedTimes++
                return
            }
            
            itemInOffHand.amount = 1
            player.inventory.itemInOffHand = SBUtil.createStorageBox(itemInOffHand,
                    SBUtil.getAmountOfStorageBox(itemInOffHand) - 1, player)
            
        }
        
        
    }
    
    /**
     * 音を再生します。
     * @param location 再生する位置
     * @param sound     再生する音
     * @param volume    再生する音の大きさ（MAX 1.0）
     * @param pitch     再生する音のピッチ（標準 1.0）
     */
    private fun playSound(location: Location, sound: Sound, volume: Float, pitch: Float)
    {
        location.world.playSound(location, sound, volume, pitch)
    }
    
}
