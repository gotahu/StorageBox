package net.hinyari.plugin.storagebox

import net.hinyari.plugin.storagebox.extensions.*
import net.hinyari.plugin.storagebox.util.SBUtil
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Command constructor(val plugin: StorageBoxMain) : CommandExecutor {
    
    override fun onCommand(sender: CommandSender, cmd: Command, label: String,
                           args: Array<out String>): Boolean {


        if (!cmd.name.equalsIgnoreCase("storagebox")) {
            return true
        }

        // プレイヤー以外は受け付けない
        if (sender !is Player) {
            return sender.sendMessageWithErrorPrefix("ゲーム内で実行して下さい。")
        }

        val player: Player = sender


        if (args.isEmpty()) {
            plugin.logger.info("args isempty")
            return false // ヘルプを表示する // TODO usageの整備
        }

        val item = player.itemInMainHand

        when (args[0].toUpperCase()) {
            "CREATE" -> {
                if (!player.hasPermission("sb.create")) {
                    return player.sendMessageWithErrorPrefix(SBUtil.noPermission)
                }
                
                when (args.size) {
                    1 -> {
                        if (item.type == Material.AIR) {
                            return player.sendMessageWithErrorPrefix("手にアイテムを持って実行して下さい。")
                        }
                        
                        return if (item.isNotStorageBox()) {
                            player.itemInMainHand.itemMeta = item.toStorageBox(item.amount).itemMeta

                            player.itemInMainHand.amount = 1
                            player.updateInventory()

                            player.sendMessageWithPrefix("StorageBoxを作成しました!")
                        } else {
                            player.sendMessageWithErrorPrefix("StorageBoxは既に作成されています!")
                        }
                    }

                    3 -> {
                        if (!player.hasPermission("sb.create.infinity")) {
                            return player.sendMessageWithErrorPrefix(SBUtil.noPermission)
                        }

                        val material = Material.getMaterial(args[1].toUpperCase())

                        // Materialがみつからない
                        if (material == null || material == Material.AIR) {
                            player.sendMessageWithErrorPrefix("そのようなアイテム名は存在しません。")
                            return player.sendMessageWithPrefix("/$label create <Material> <数量>")
                        }

                        val amount = if (args[2].isNumeric()) args[2].toInt() else -1

                        // 数がおかしいンゴ
                        if (amount < 0) {
                            return player.sendMessageWithErrorPrefix("0以上の数字を入力してください。")
                        }

                        player.inventory.addItem(ItemStack(material).toStorageBox(amount))
                        player.sendMessageWithPrefix("${material}のStorageBoxを作成しました。")
                    }

                    else -> return false
                }
            }

            "INFO" -> {
                if (!player.hasPermission("sb.info")) {
                    return player.sendMessageWithErrorPrefix(SBUtil.noPermission)
                }
                
                if (item.isNotStorageBox()) {
                    return player.sendMessageWithErrorPrefix("StorageBoxを手に持って実行して下さい。")
                }

                player.sendMessage("§6---- §rStorageBox Information §6----")
                player.sendMessage("Material: ${item.type}")
                player.sendMessage("Amount: ${SBUtil.getAmountOfStorageBox(item)}")
            }

            "TAKE" -> {
                if (!player.hasPermission("sb.take")) {
                    return player.sendMessageWithErrorPrefix(SBUtil.noPermission)
                }

                if (item.isNotStorageBox()) {
                    return player.sendMessageWithErrorPrefix("StorageBoxを手に持って実行して下さい。")
                }

                when (args.size) {
                    1 -> {
                        return player.sendMessageWithErrorPrefix("数量を指定して下さい。")
                    }

                    2 -> {
                        // 数列でなければリターン
                        if (!args[1].isNumeric() || args[1].toInt() <= 0) {
                            return player.sendMessageWithErrorPrefix("1以上の整数を入力して下さい。")
                        }

                        val taken = SBUtil.getAmountOfStorageBox(item) - args[1].toInt()

                        player.giveItem(item.reset(), taken)
                        return player.sendMessageWithPrefix("$taken 個のアイテムを取り出しました。")
                    }
                }
            }

            "RECIPE" -> {
                if (!player.hasPermission("sb.modifyrecipe")) {
                    return player.sendMessageWithErrorPrefix(SBUtil.noPermission)
                }
                player.openInventory(plugin.inventoryUtil.recipeRegister)
            }
        }
        return true
    }
}
