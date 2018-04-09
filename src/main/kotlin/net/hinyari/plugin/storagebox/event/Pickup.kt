package net.hinyari.plugin.storagebox.event

import net.hinyari.plugin.storagebox.util.SBUtil
import net.hinyari.plugin.storagebox.StorageBoxMain
import net.hinyari.plugin.storagebox.extensions.*
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent

class Pickup constructor(val plugin: StorageBoxMain) : Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerPickupItemEvent(event: EntityPickupItemEvent) {

        if (event.entityType != EntityType.PLAYER) {
            return
        }

        val player: Player = event.entity as Player
        val pickedItem = event.item.itemStack

        // 拾えないようにする
        if (!player.hasPermission("sb.pickup") && pickedItem.isStorageBox()) {
            event.isCancelled = true
            return
        }

        for (item in player.inventory.contents) {
            // アイテムが存在しない、またはストレージボックスではない
            if (item == null || item.isNotStorageBox()) {
                continue
            }

            // ストレージボックスの内容と合っているか
            if (item.type == pickedItem.type && // Material
                    item.data.data == pickedItem.data.data &&  // DyeColor等
                    item.durability == pickedItem.durability && // 耐久値  
                    item.enchantments == pickedItem.enchantments) { // エンチャント

                event.isCancelled = true


                // アイテムのエンティティを消去
                event.item.remove()

                // 拾った後の数
                val afterAmount: Int = if (pickedItem.isStorageBox())
                    SBUtil.getAmountOfStorageBox(item) + SBUtil.getAmountOfStorageBox(pickedItem)
                else SBUtil.getAmountOfStorageBox(item) + pickedItem.amount
                
                // StorageBoxを編集する
                item.toStorageBox(afterAmount)

                // キャンセルして音が鳴らないので鳴らす
                player.playSound(player.location, Sound.ENTITY_ITEM_PICKUP, 0.2f, 2.0f)
            }
        }
    }
}
