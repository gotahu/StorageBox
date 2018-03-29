package net.hinyari.plugin.storagebox.event

import net.hinyari.plugin.storagebox.StorageBoxMain
import net.hinyari.plugin.storagebox.extensions.*
import net.hinyari.plugin.storagebox.util.SBUtil
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class Interact constructor(val plugin: StorageBoxMain) : Listener {

    // StorageBoxの使用に対応するアイテム
    private var materialArray = arrayOf(Material.ENDER_PEARL, Material.MINECART, Material.WATER_BUCKET,
            Material.LAVA_BUCKET)
    private val inventoryUtil = plugin.inventoryUtil

    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {

        val item = event.item
        val player = event.player
        
        if (item == null) return

        if (item.hasItemMeta() && item.itemMeta.displayName == "§6§lStorageBox : §r§6未登録" 
                && (event.action == Action.RIGHT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_AIR)) {

            player.openInventory(inventoryUtil.getRegisterInventoryByUUID(player.uniqueId))
            event.isCancelled = true
            return
        }

        // StorageBoxじゃない
        if (item.isNotStorageBox()) {
            return
        }
        
        val isMainHand = event.hand == EquipmentSlot.HAND

        // スニーク中
        if (player.isSneaking) {
            // アイテムを取り出す処理
            if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) { // 左クリ
                val amount = SBUtil.getAmountOfStorageBox(item)
                // 0個なら無視
                if (amount <= 0) return

                // メインハンドにStorageBoxを所持している場合
                if (isMainHand) player.itemInMainHand = item.toStorageBox(amount - 1)
                else player.itemInOffHand = item.toStorageBox(amount - 1)

                player.giveItem(item.reset(), 1)
                player.playSound(player.location, Sound.ENTITY_ITEM_PICKUP, 0.2f, 0.5f)

            } else if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) { // 右クリ
                // StorageBoxにまとめる
                if (SBUtil.stackToStorageBox(player, item) /* ここでまとめる 兼 まとめるアイテムがあったかチェック */) {
                    player.playSound(player.location, Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 1.5f)
                }
            }
            return
        }
    }
}
