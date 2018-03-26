import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import java.util.*

/**
 * @author Sebasju1234
 */
class ItemUtils {
    
    companion object {

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
    }

}
 
