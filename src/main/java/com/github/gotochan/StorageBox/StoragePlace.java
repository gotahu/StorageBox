package com.github.gotochan.StorageBox;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class StoragePlace implements Listener {

	StorageBox plugin;
	SBUtil util;

	private HashMap<UUID, Integer> amount = new HashMap<UUID, Integer>();

	public StoragePlace(StorageBox plugin) {
		this.plugin = plugin;
		this.util = plugin.util;
	}

	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onPlaceWithStorageBox(BlockPlaceEvent event) {
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		if (!player.hasPermission("sb.place") ) {
			player.sendMessage(plugin.error + "権限がありません。");
		}

		/*final*/
		ItemStack item = player.getInventory().getItemInMainHand();
		ItemStack item_off = player.getInventory().getItemInOffHand();
		if (util.isStorageBox(item)) {
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

		if (util.isStorageBox(item_off)) {
			event.setCancelled(true);
			if (amount.get(uuid)==null) {
				amount.put(uuid, 1);
			} else {
				amount.put(uuid, (amount.get(uuid) + 1));
			}
			if (amount.get(uuid) == 1 || amount.get(uuid) % 4 == 0)
			{
				player.sendMessage(plugin.error + "メインハンドに入れて使用してください!");
			}
			player.getWorld().playEffect(event.getBlock().getLocation().add(0, 1, 0), Effect.SMOKE, 4);
			return;
		}
	}

}
