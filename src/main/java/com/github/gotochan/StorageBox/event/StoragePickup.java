package com.github.gotochan.StorageBox.event;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.github.gotochan.StorageBox.SBUtil;
import com.github.gotochan.StorageBox.StorageBox;

public class StoragePickup implements Listener
{

    private StorageBox plugin;
    private SBUtil util;

    public StoragePickup(StorageBox plugin)
    {
        this.plugin = plugin;
        this.util = plugin.getUtil();
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event)
    {
        Player player = event.getPlayer();
        ItemStack pItem = event.getItem().getItemStack();
        Material pMaterial = pItem.getType();
        if (!player.hasPermission("sb.pickup"))
            return;

        for (ItemStack item : player.getInventory().getContents())
        {
            if (item == null || !item.hasItemMeta() || (item.hasItemMeta() && (!util.isStorageBox(item))))
            {
                continue;
            }

            if (util.isSimilar(item, pItem))
            {
                if (util.isStorageBox(pItem))
                {
                    util.createStorageBox(item, util.getValue(item) + util.getValue(pItem)); // ストレージボックスを更新
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, (float) 0.2, (float) 2.0);
                    event.setCancelled(true);
                    event.getItem().remove(); // エンティティを削除
                    break;
                }
                util.createStorageBox(item, (util.getValue(item) + pItem.getAmount()));
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, (float) 0.2, (float) 2.0);
                event.setCancelled(true);
                event.getItem().remove(); // エンティティを削除
            }
        }
    }

}
