package net.hinyari.plugin.storagebox.event

import net.hinyari.plugin.storagebox.SBUtil
import net.hinyari.plugin.storagebox.StorageBoxMain
import org.bukkit.Effect
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class Place : Listener {
    
    private val plugin = StorageBoxMain.instance
    private var triedTimes = 0
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {

        val player = event.player

        val itemInMainHand = player.inventory.itemInMainHand
        val itemInOffHand = player.inventory.itemInOffHand

        val isMain = SBUtil.isStorageBox(itemInMainHand)

        val hasStorageBox = isMain || SBUtil.isStorageBox(itemInOffHand)

        // StorageBoxを持っていなかった場合
        if (!hasStorageBox) {
            return
        }

        // 設置権限を持っていなかった場合
        if (!player.hasPermission("sb.place")) {
            player.sendMessage(plugin.noPerm)
            event.isCancelled = true
            return
        }

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
                    player.sendMessage("${plugin.PREFIX_ERROR}アイテムを補充して下さい！")
                }
                spawnSmoke(event.block.location)
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
                    player.sendMessage("${plugin.PREFIX_ERROR}アイテムを補充して下さい！")
                }
                spawnSmoke(event.block.location)
                player.playSound(player.location, Sound.BLOCK_DISPENSER_FAIL, 0.4f, 1.0f)

                triedTimes++
                return
            }
            
            itemInOffHand.amount = 1
            player.inventory.itemInOffHand = SBUtil.createStorageBox(itemInOffHand,
                    SBUtil.getAmountOfStorageBox(itemInOffHand) - 1, player)
            
        }
        
        
    }
    
    private fun spawnSmoke(location: Location) {
        location.world.playEffect(location.add(0.0, 1.0, 0.0), Effect.SMOKE, 4)
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
