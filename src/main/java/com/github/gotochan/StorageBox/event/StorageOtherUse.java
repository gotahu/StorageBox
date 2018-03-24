package com.github.gotochan.StorageBox.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.gotochan.StorageBox.SBUtil;
import com.github.gotochan.StorageBox.StorageBox;

public class StorageOtherUse implements Listener
{

    private StorageBox plugin;
    private SBUtil util;

    public StorageOtherUse(StorageBox plugin)
    {
        this.plugin = plugin;
        this.util = plugin.getUtil();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event)
    {
        final Player player = event.getPlayer();
        final ItemStack item = event.getItem();
        final ItemStack left = player.getInventory().getItemInOffHand();
        if (!player.hasPermission("sb.consume"))
        {
            player.sendMessage(plugin.error + "権限がありません。");
            event.setCancelled(true);
            return;
        }

        if (util.isStorageBox(item))
        {
            if (util.getValue(item) <= 0)
            {
                event.setCancelled(true);
                player.sendMessage(plugin.error + "アイテムを補充してください。");
                item.setAmount(1);
                return;
            }

            new BukkitRunnable()
            {

                public void run()
                {
                    if (item.equals(left))
                    {
                        player.getInventory().setItemInOffHand(util.createStorageBox(item, util.getValue(item) - 1));
                    } else
                    {
                        player.getInventory().setItemInMainHand(util.createStorageBox(item, util.getValue(item) - 1));
                    }
                    item.setAmount(1);
                }
            }.runTaskLater(plugin, 1L);
        }
    }


}
