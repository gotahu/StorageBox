package com.github.gotochan.StorageBox;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SBCommandExecutor implements CommandExecutor
{

    private StorageBox plugin;
    private SBUtil util;
    private SBGUI gui;

    public SBCommandExecutor(StorageBox plugin)
    {
        this.plugin = plugin;
        this.util = plugin.getUtil();
        this.gui = plugin.gui;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(plugin.error + "ゲーム内プレイヤーから実行してください。");
        }
        Player player = (Player) sender;
        ItemStack item = player.getItemInHand();
        if (command.getName().equalsIgnoreCase("storagebox"))
        {
            if (args.length == 0)
                return false;
            if (item.getType().equals(Material.AIR))
            {
                player.sendMessage(plugin.error + "StorageBoxを作成したいアイテムを持ってください。");
                return true;
            }
            //else {perm_error(player);}
            if (args[0].equalsIgnoreCase("create"))
            {
                if (player.hasPermission("sb.create"))
                {
                    if (util.isStorageBox(item))
                    {
                        player.sendMessage(plugin.error + "既にStorageBoxは作成されています。");
                        return true;
                    } else
                        item.setItemMeta(util.createStorageBox(item, item.getAmount()).getItemMeta());
                    item.setAmount(1);
                    player.sendMessage(plugin.prefix + "正常に " + item.getType().toString() + " のストレージボックスを作成しました。");
                    return true;
                } else
                {
                    perm_error(player);
                }
            } else if (args[0].equalsIgnoreCase("remove"))
            {
                if (player.hasPermission("sb.remove"))
                {
                    if (util.isStorageBox(item))
                    {
                        ItemStack remItem = new ItemStack(item);
                        remItem.setAmount(util.getValue(item));

                        player.getInventory().addItem(remItem);
                        util.removeStorageBox(item);
                        player.setItemInHand(null);
                        return true;
                    } else
                    {
                        player.sendMessage(plugin.error + "StorageBoxを手に持つ必要があります。");
                        return true;
                    }
                } else
                {
                    perm_error(player);
                }
            } else if (args[0].equalsIgnoreCase("info"))
            {
                if (player.hasPermission("sb.info"))
                {
                    if (util.isStorageBox(item))
                    {
                        player.sendMessage(plugin.prefix + "Material: " + item.getType().toString() + ", Value: " + util
                                .getValue(item));
                        return true;
                    } else
                    {
                        player.sendMessage(plugin.error + "StorageBoxを手に持つ必要があります。");
                        return true;
                    }
                } else
                {
                    perm_error(player);
                }
            }
            /*
            else if(args[0].equalsIgnoreCase("take")) {
				if (player.hasPermission("sb.take")) {
					if (args.length<2) { player.sendMessage(plugin.error + "数量を指定してください。");  return true;}
					if (!util.isNumber(args[1])) {player.sendMessage(plugin.error + "数字を入力する必要があります。"); return true; }
					int want = Integer.valueOf(args[1]);
					if (util.isStorageBox(item)) {
						if(util.getValue(item) < want) {
							player.sendMessage(plugin.error + "数量が大きすぎます!");
							return true;
						}
						player.getInventory().setItemInMainHand(util.createStorageBox(item, util.getValue(item)-want));
						util.giveItem(player, item, want);
						return true;
					}else {
						player.sendMessage(plugin.error + "StorageBoxを手に持つ必要があります。");
						return true;
					}
				}else {perm_error(player);}
			}
			 */

            else if (args[0].equalsIgnoreCase("edit"))
            {
                if (player.hasPermission("sb.edit"))
                {
                    if (util.isStorageBox(item))
                    {
                        player.openInventory(gui.getEditorGUI(item, player.getUniqueId()));
                        return true;
                    } else
                    {
                        player.sendMessage(plugin.error + "StorageBoxを手に持つ必要があります。");
                        return true;
                    }
                } else
                {
                    perm_error(player);
                }
            }
            else if (args[0].equalsIgnoreCase("debug"))
            {
                if (player.hasPermission("sb.debug"))
                {
                    plugin.setEnableDebug(!plugin.isEnableDebug());
                    player.sendMessage(plugin.prefix + "デバッグモードを" +
                            (plugin.isEnableDebug() ? "有効" : "無効") + "にしました");
                    return true;
                }
            }

        }
        return false;
    }

    private boolean perm_error(Player player)
    {
        String message = plugin.error + "権限がありません。";
        player.sendMessage(message);
        return true;
    }
}
