package net.hinyari.plugin.storagebox

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class SBCommandExecutor : CommandExecutor {

    private val plugin: StorageBoxMain = StorageBoxMain.instance
    

    override fun onCommand(sender: CommandSender, cmd: Command, label: String,
                           args: Array<out String>): Boolean {

        // プレイヤー以外は受け付けない
        if (sender !is Player) {
            sender.sendMessage("${plugin.PREFIX_ERROR}ゲーム内で実行して下さい。")
        }

        val player: Player = sender as Player

        if (cmd.name.equals("storagebox", true)) {

            if (args.isEmpty()) {
                plugin.logger.info("args isempty")
                return false // ヘルプを表示する // TODO usageの整備
            }

            val item = player.inventory.itemInMainHand
            
            if (item == null || item.type == Material.AIR) {
                player.sendMessage("${plugin.PREFIX_ERROR}アイテムを手に持った状態で実行して下さい！")
                return true
            }
            
            if (args[0].equals("create", true)) {
                if (player.hasPermission("sb.create")) {
                    
                    if (args.size == 1) {

                        return if (!SBUtil.isStorageBox(item)) { // ストレージボックスを持っていない場合（正常）

                            player.itemOnCursor.itemMeta = SBUtil.createStorageBox(
                                    item, item.amount, player
                            ).itemMeta

                            player.itemOnCursor.amount = 1
                            player.updateInventory()

                            player.sendMessage("${plugin.PREFIX}StorageBoxを作成しました！")

                            true
                        } else {
                            player.sendMessage("${plugin.PREFIX_ERROR}既にStorageBoxは作成されています！")

                            true
                        }
                        
                    } else if (args.size >= 2) {
                        
                        if (player.hasPermission("sb.create.infinity")) {
                            val material = Material.valueOf(args[1].toUpperCase())

                            // マテリアルがない
                            if (material == Material.AIR) {
                                player.sendMessage("${plugin.PREFIX_ERROR}そのようなアイテム名は存在しません！")
                                player.sendMessage("/$label create <Material> <数量>")
                                return true
                            }
                            
                            val amount = if (SBUtil.isNumeric(args[2])) args[2].toInt() else -1
                            
                            // 数がおかしい
                            if (amount < 0) {
                                player.sendMessage("${plugin.PREFIX_ERROR}0以上のs数字を入力して下さい！")
                                player.sendMessage("/$label create <Material> <数量>")
                                return true
                            }
                            
                            player.inventory.addItem(SBUtil.createStorageBox(ItemStack(material), amount, player))
                            player.sendMessage("${plugin.PREFIX}${material}のStorageBoxを作成しました。")
                            
                        } else {
                            // ヘルプでも表示させとく
                            return false
                        }
                    }

                } else { // 権限がない

                    player.sendMessage(plugin.noPerm)

                }
            } else if (args[0].equals("info", true)) {
                if (player.hasPermission("sb.info")) {

                    if (SBUtil.isStorageBox(item)) { // ストレージボックスを持っている場合
                        player.sendMessage("&6---- &rStorageBox Information &6----")
                        player.sendMessage("Material: ${item.type}")
                        player.sendMessage("Amount: ${SBUtil.getAmountOfStorageBox(item)}")
                        //player.sendMessage("Owner: ${SBUtil.getOwnerOfStorageBox(item)?.name}")
                    } else {
                        player.sendMessage("${plugin.PREFIX_ERROR}StorageBoxを手に持って実行して下さい！")
                    }

                } else {
                    player.sendMessage(plugin.noPerm)
                }
            } else if (args[0].equals("take", true)) {

                if (player.hasPermission("sb.take")) {
                    if (args.size < 2) { // sb takeだけだった
                        player.sendMessage("${plugin.PREFIX_ERROR}数量を指定して下さい。")
                        return true
                    }

                    // sb take 数列 であるかを判断
                    if (SBUtil.isNumeric(args[1])) {
                        val wantAmount = args[1].toInt()

                        // ストレージボックスを手に持っているか
                        if (SBUtil.isStorageBox(item)) {

                            // ゼロ以下
                            if (wantAmount <= 0) {
                                player.sendMessage("${plugin.PREFIX_ERROR}1以上の数字を入力して下さい！")
                                return true
                            }

                            // 求める数量にStorageBox内の数量が満たない
                            if (wantAmount > SBUtil.getAmountOfStorageBox(item)) {
                                player.sendMessage("${plugin.PREFIX_ERROR}指定した数量のアイテムがありません！")
                                return true
                            }

                            // 求める数量とStorageBox内の数量が同じ
                            if (wantAmount == SBUtil.getAmountOfStorageBox(item)) {
                                // StorageBoxを消す
                                player.inventory.remove(item)

                            } else { // 求める数量よりStorageBox内の数量の方が少ない
                                val aftertaken = SBUtil.getAmountOfStorageBox(item) - wantAmount

                                // StorageBoxの数量を変えたStorageBoxを付与する
                                player.inventory.itemInMainHand = 
                                        SBUtil.createStorageBox(item, aftertaken, player)
                                
                            }

                            // プレイヤーにアイテムを渡す
                            SBUtil.giveItemToPlayer(player, item, wantAmount)
                            player.sendMessage("${plugin.PREFIX}${wantAmount}個のアイテムをStorageBoxから取り出しました。")
                            
                        } else {
                            player.sendMessage("${plugin.PREFIX_ERROR}StorageBoxを手に持って実行して下さい！")
                        }
                    } else {
                        player.sendMessage("${plugin.PREFIX_ERROR}1以上の整数を入力して下さい！")
                    }
                } else {
                    player.sendMessage(plugin.noPerm)
                }

            }
        }

        return true
    }
}
