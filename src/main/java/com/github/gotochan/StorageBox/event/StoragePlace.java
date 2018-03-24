package com.github.gotochan.StorageBox.event;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.github.gotochan.StorageBox.SBUtil;
import com.github.gotochan.StorageBox.StorageBox;

public class StoragePlace implements Listener
{

    private StorageBox plugin;
    private SBUtil util;

    private HashMap<UUID, Integer> amount = new HashMap<>();

    public StoragePlace(StorageBox plugin)
    {
        this.plugin = plugin;
        this.util = plugin.getUtil();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlaceWithStorageBox(BlockPlaceEvent event)
    {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (!player.hasPermission("sb.place"))
        {
            player.sendMessage(plugin.error + "権限がありません。");
            event.setCancelled(true);
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemStack item_off = player.getInventory().getItemInOffHand();
        if (util.isStorageBox(item))
        {
            if (util.getValue(item) <= 0)
            {
                event.setBuild(false);
                item.setAmount(1);
                return;
            }
            if (player.getGameMode().equals(GameMode.CREATIVE))
            {
                item.setAmount(1);
                player.getInventory().setItemInMainHand(util.createStorageBox(item, util.getValue(item)));
                return;
            }
            item.setAmount(1);
            player.getInventory().setItemInMainHand(util.createStorageBox(item, (util.getValue(item) - 1)));
        }

        if (util.isStorageBox(item_off))
        {
            event.setCancelled(true);
            if (amount.get(uuid) == null)
            {
                amount.put(uuid, 1);
            } else
            {
                amount.put(uuid, (amount.get(uuid) + 1));
            }
            if (amount.get(uuid) == 1 || amount.get(uuid) % 4 == 0)
            {
                player.sendMessage(plugin.error + "ブロックはメインハンドに入れて使用してください!");
            }
            player.getWorld().playEffect(event.getBlock().getLocation().add(0, 1, 0), Effect.SMOKE, 4);
        }
    }

}
