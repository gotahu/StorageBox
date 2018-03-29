package net.hinyari.plugin.storagebox.extensions

import net.hinyari.plugin.storagebox.util.SBUtil
import org.bukkit.ChatColor
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.command.CommandSender

fun String.isNumeric() : Boolean {
    return matches("-?\\d+(\\.\\d+)?".toRegex())
}

fun Location.spawnSmoke() {
    world.playEffect(this, Effect.SMOKE, BlockFace.UP)
}

fun String.equalsIgnoreCase(string: String) : Boolean = this.equals(string, true)

fun CommandSender.sendMessageWithPrefix(msg: String) : Boolean {
    sendMessage(SBUtil.prefix + ChatColor.translateAlternateColorCodes('&', msg))
    return true
}

fun CommandSender.sendMessageWithErrorPrefix(msg: String) : Boolean {
    sendMessage(SBUtil.errorPrefix + ChatColor.translateAlternateColorCodes('&', msg))
    return true
}
