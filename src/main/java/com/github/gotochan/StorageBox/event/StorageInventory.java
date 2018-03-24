package com.github.gotochan.StorageBox.event;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.gotochan.StorageBox.SBGUI;
import com.github.gotochan.StorageBox.SBUtil;
import com.github.gotochan.StorageBox.StorageBox;

public class StorageInventory implements Listener
{

    private StorageBox plugin;
    private SBUtil util;
    private SBGUI gui;

    public StorageInventory(StorageBox plugin)
    {
        this.plugin = plugin;
        this.util = plugin.getUtil();
        this.gui = plugin.gui;
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event)
    {
        //プレイヤーでない
        if (!(event.getWhoClicked() instanceof Player))
            return;
        //インベントリをクリックしていない
        if (event.getInventory() == null)
            return;
        //アイテムが存在しない
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR))
            return;
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getInventory();
        String invname = inventory.getName();
        //StorageBox製である
        if (invname.contains("[SB]"))
        {
            //登録である
            if (invname.contains("Submit"))
            {
                if (event.getRawSlot() != 21)
                {
                    event.setCancelled(true);
                }
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
                {
                    ItemMeta meta = item.getItemMeta();
                    if (meta.getDisplayName().contains("OK"))
                    {
                        if (inventory.getItem(21) == null)
                        {
                            playSound(player, Sound.ENTITY_WITHER_SHOOT, (float) 0.5);
                        } else
                        {
                            ItemStack tmp = inventory.getItem(21);
                            if (util.isStorageBox(tmp))
                            {
                                playSound(player, Sound.BLOCK_TRIPWIRE_CLICK_ON, (float) 0.5);
                                return;
                            } else
                            {
                                inventory.remove(tmp);
                                player.closeInventory();
                                player.getInventory().setItemInMainHand(util.createStorageBox(tmp, tmp.getAmount()));
                                ItemStack tmp_1 = player.getInventory().getItemInMainHand();
                                tmp_1.setAmount(1);
                                player.getInventory().setItemInMainHand(tmp_1);
                            }
                        }
                    } else if (meta.getDisplayName().contains("Cancel"))
                    {
                        player.closeInventory();
                    }
                }
            } else if (invname.contains(""))
            {
            }
        }
        if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
        {
            if (event.getInventory().getName().contains("[SB]"))
            {
                event.setCancelled(true);
            }
        }

        if (event.getInventory().getName().contains("[SB]"))
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta()
                    .getDisplayName()
                    .contains("アイテムを登録してください"))
            {
                event.setCancelled(true);
            }
    }

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event)
    {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        if (event.getInventory() == null)
            return;
        Inventory inventory = event.getInventory();
        String invname = inventory.getName();
        if (invname.contains("[SB]"))
        {
            event.setCancelled(true);
        }
    }

    private void playSound(Player player, Sound sound, float pitch)
    {
        player.playSound(player.getLocation(), sound, (float) 0.15, pitch);
    }


}
