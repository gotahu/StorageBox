package com.github.gotochan.StorageBox;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class StoragePickup implements Listener {

	StorageBox plugin;
	SBUtil util;

	public StoragePickup(StorageBox plugin) {
		this.plugin = plugin;
		this.util = plugin.util;
	}

	@EventHandler
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		ItemStack pItem = event.getItem().getItemStack();
		Material pMaterial = pItem.getType();
		if (!player.hasPermission("sb.pickup"))
			return;

		for (ItemStack item : player.getInventory().getContents())
		{
			if(item==null) {
				continue;
			}
			if(!item.hasItemMeta()) {
				continue;
			}
			if(item.hasItemMeta())
				if(!util.isStorageBox(item)) {
					continue;
				}

			if(item.getType().equals(pMaterial)) {
				if (util.isStorageBox(pItem)) {
					util.createStorageBox(item, util.getValue(item) + util.getValue(pItem));
					player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, (float) 0.2, (float) 2.0);
					event.setCancelled(true);
					event.getItem().remove();
					return;
				}
				util.createStorageBox(item, (util.getValue(item) + pItem.getAmount()));
				player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, (float) 0.2, (float) 2.0);
				event.setCancelled(true);
				event.getItem().remove();
			}
		}
	}

}
