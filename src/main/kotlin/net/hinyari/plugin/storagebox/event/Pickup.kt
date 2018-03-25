package net.hinyari.plugin.storagebox.event

import net.hinyari.plugin.storagebox.SBUtil
import net.hinyari.plugin.storagebox.StorageBoxMain
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent

class Pickup : Listener {

    private val plugin = StorageBoxMain.instance

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onPlayerPickupItemEvent(event: EntityPickupItemEvent) {
        
        if (event.entityType != EntityType.PLAYER) {
            return
        }

        val player: Player = event.entity as Player

        if (!player.hasPermission("sb.pickup")) {
            return
        }

        val pickedItem = event.item.itemStack

        for (item in player.inventory.contents) {
            // アイテムが存在しない、メタを有しない、ストレージボックスではない
            if (item == null || !item.hasItemMeta() || (item.hasItemMeta() && (!SBUtil.isStorageBox(item)))) {
                continue
            }
            
            val sbmaterial = SBUtil.getTypeOfStorageBox(item)

            // ストレージボックスの内容と合っているか
            if (sbmaterial == pickedItem.type &&
                    item.durability == pickedItem.durability &&
                    item.data.data == pickedItem.data.data) {


                val afteramount: Int =
                        // StorageBoxを拾った
                        if (SBUtil.isStorageBox(pickedItem)) {
                            // 所有者が同じ
                            if (SBUtil.getOwnerOfStorageBox(item) == SBUtil.getOwnerOfStorageBox(pickedItem)) {
                                SBUtil.getAmountOfStorageBox(item) + SBUtil.getAmountOfStorageBox(pickedItem)
                            } else {
                                player.sendMessage("${plugin.PREFIX_ERROR}所有者の異なるStorageBoxをまとめる事は出来ません。")
                                player.sendMessage("${plugin.PREFIX}所有者を変更するには [/sb owner <新しい所有者名>] を実行します。")
                                return
                            }

                        } else { // そうでなければ普通に追加するだけ
                            SBUtil.getAmountOfStorageBox(item) + pickedItem.amount
                        }

                // イベントをキャンセルする
                event.isCancelled = true

                // StorageBoxを編集する
                SBUtil.createStorageBox(item, afteramount, player)
                // キャンセルして音が鳴らないので鳴らす
                player.playSound(player.location, Sound.ENTITY_ITEM_PICKUP, 0.2f, 2.0f)

                // アイテムのエンティティを消去
                event.item.remove()

            }


        }

    }

}
