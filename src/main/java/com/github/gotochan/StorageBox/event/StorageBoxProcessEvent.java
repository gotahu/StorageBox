package com.github.gotochan.StorageBox.event;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.gotochan.StorageBox.SBGUI;
import com.github.gotochan.StorageBox.SBUtil;
import com.github.gotochan.StorageBox.StorageBox;

public class StorageBoxProcessEvent implements Listener
{

    private StorageBox plugin;
    private SBUtil util;
    private SBGUI gui;

    public StorageBoxProcessEvent(StorageBox plugin)
    {
        this.plugin = plugin;
        this.util = plugin.getUtil();
        this.gui = plugin.gui;
    }

    @EventHandler
    public void onPlayerCraftEvent(CraftItemEvent event)
    {
        if (event.getCurrentItem().hasItemMeta())
        {
            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("アイテムを登録してください"))
            {
                Player player = (Player) event.getWhoClicked();
                player.sendMessage(plugin.prefix + "右クリックで登録GUIを開くことが出来ます!");
            }
        }
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType().equals(Material.AIR))
            return;
        if (!item.hasItemMeta())
            return;
        if (!item.getItemMeta().hasDisplayName())
            return;
        if (item.getItemMeta().getDisplayName().contains("アイテムを登録してください"))
        {
            event.setCancelled(true);
            if (player.getInventory().getItemInMainHand().equals(item))
            {
                player.openInventory(gui.getSubmitGUI(player.getUniqueId()));
            } else
            {
                player.sendMessage(plugin.error + "メインハンドに入れてください。");
            }
        }

    }

}
