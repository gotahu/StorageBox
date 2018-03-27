import net.hinyari.plugin.storagebox.NMS
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * @author Sebasju1234
 */
class ItemUtils {
        
    companion object {

        private val nms = NMS()

        /**
         * @param item
         * @return
         */
        fun isTool(item: ItemStack): Boolean {
            return Enchantment.DURABILITY.canEnchantItem(item)
        }

        /**
         * @param item
         * @return
         */
        fun getDisplayName(item: ItemStack): String {
            return if (item.hasItemMeta()) {
                item.itemMeta.displayName
            } else {
                "Unknown display name"
            }
        }

        /**
         * @param material
         * @param name
         * @param desc
         * @return
         */
        fun createItem(material: Material, name: String, desc: Array<String>): ItemStack {
            val item = ItemStack(material)
            val meta = item.itemMeta

            meta.displayName = name
            meta.lore = Arrays.asList(*desc)

            item.itemMeta = meta
            return item
        }

        /**
         * @param material
         * @param name
         * @return
         */
        fun createItem(material: Material, name: String): ItemStack {
            val item = ItemStack(material)
            val meta = item.itemMeta

            meta.displayName = name

            item.itemMeta = meta
            return item
        }

        /**
         * @param material
         * @param name
         * @param desc
         * @param color
         * @return
         */
        fun createWoolItem(material: Material, name: String, desc: Array<String>, color: Byte): ItemStack {
            val item = ItemStack(material, 1, color.toShort())
            val meta = item.itemMeta

            meta.displayName = name
            meta.lore = Arrays.asList(*desc)

            item.itemMeta = meta
            return item
        }

        /**
         * @param material
         * @param name
         * @param color
         * @return
         */
        fun createWoolItem(material: Material, name: String, color: Byte): ItemStack {
            val item = ItemStack(material, 1, color.toShort())
            val meta = item.itemMeta

            meta.displayName = name

            item.itemMeta = meta
            return item
        }
        
        fun getItemOriginalName(itemStack: ItemStack) : String {
            val craftitemstack = nms.getBukkitNMSClass("inventory.CraftItemStack") ?: return "ERROR"
            val method = craftitemstack.getMethod("asNMSCopy", ItemStack(Material.AIR).javaClass)
            // asNMSCopyメソッドを実行
            val invoked = method.invoke(null, itemStack) // 戻り値は net.minecraft.server.<version>.ItemStack
            
            val nmsItemStack = nms.getNMSClass("ItemStack") ?: return "ERROR"
            val nameMethod = nmsItemStack.getMethod("getName")

            return nameMethod.invoke(invoked) as String
        }
    }

}
 
