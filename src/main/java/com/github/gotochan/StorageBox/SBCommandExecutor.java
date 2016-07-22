package com.github.gotochan.StorageBox;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SBCommandExecutor implements CommandExecutor{

	private StorageBox plugin;
	private SBUtil util;

	public SBCommandExecutor(StorageBox plugin) {
		this.plugin = plugin;
		this.util = plugin.util;
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(plugin.error + "ゲーム内プレイヤーから実行してください。");
		}
		Player player = (Player) sender;
		ItemStack item = player.getItemInHand();
		if (command.getName().equalsIgnoreCase("storagebox")) {
			if(args.length == 0)
				return false;
			if (item==null) {
				player.sendMessage(plugin.error + "StorageBoxを作成したいアイテムを持ってください。");
				return true;
			}
			//else {perm_error(player);}
			if(args[0].equalsIgnoreCase("create")) {
				if (player.hasPermission("sb.create")) {
					if (util.isStorageBox(item)){
						player.sendMessage(plugin.error + "既にStorageBoxは作成されています。");
						return true;
					} else {
						if (!item.getType().isBlock()) {
							player.sendMessage(plugin.error + "ブロックのみ登録することが出来ます。");
							return true;
						}
						item.setItemMeta(util.createStorageBox(item, item.getAmount()).getItemMeta());
						item.setAmount(1);
						player.sendMessage(plugin.prefix + "正常に " + item.getType().toString() + " のストレージボックスを作成しました。");
						return true;
					}
				} else {perm_error(player);}
			}
			else if(args[0].equalsIgnoreCase("remove")) {
				if (player.hasPermission("sb.remove")) {
					if (util.isStorageBox(item)) {
						player.getInventory().addItem(new ItemStack(item.getType(),util.getValue(item)));
						util.removeStorageBox(item);
						player.setItemInHand(null);
						return true;
					}else {
						player.sendMessage(plugin.error + "StorageBoxを手に持つ必要があります。");
						return true;
					}
				} else {perm_error(player);}
			}
			else if(args[0].equalsIgnoreCase("info")) {
				if (player.hasPermission("sb.info")) {
					if (util.isStorageBox(item)) {
						player.sendMessage(plugin.prefix + "Material: " + item.getType().toString() + ", Value: " +
								util.getValue(item));
						return true;
					}else {
						player.sendMessage(plugin.error + "StorageBoxを手に持つ必要があります。");
						return true;
					}
				}else {perm_error(player);}
			}
		}
		return false;
	}

	private boolean perm_error(Player player) {
		String message = plugin.error + "権限がありません。";
		player.sendMessage(message);
		return true;
	}
}
