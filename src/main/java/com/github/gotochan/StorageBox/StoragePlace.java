package com.github.gotochan.StorageBox;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class StoragePlace implements Listener {

	StorageBox plugin;
	SBUtil util;

	public StoragePlace(StorageBox plugin) {
		this.plugin = plugin;
		this.util = plugin.util;
	}

	@EventHandler
	public void onPlaceWithStorageBox(BlockPlaceEvent event) {
		final Player player = event.getPlayer();
		if (!player.hasPermission("sb.place") ) {
			player.sendMessage(plugin.error + "権限がありません。");
		}

		final ItemStack item = player.getInventory().getItemInMainHand();
		if (util.isStorageBox(item) ) {
			if (util.getValue(item) <= 0) {
				event.setBuild(false);
				item.setAmount(1);
				return;
			}
			item.setAmount(1);
			/*
			new BukkitRunnable() {

				public void run() {
			 */
			item.setAmount(1);
			player.getInventory().setItemInMainHand(util.createStorageBox(item, (util.getValue(item)-1)));
			/*
				}
			}.runTaskLater(plugin, 1L);
			 */
		}
	}

}
