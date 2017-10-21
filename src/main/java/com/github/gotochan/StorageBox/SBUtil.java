package com.github.gotochan.StorageBox;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SBUtil
{

    private StorageBox plugin;

    public SBUtil(StorageBox plugin)
    {
        this.plugin = plugin;
    }

    public ItemStack createStorageBox(ItemStack item, int value)
    {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(getLore(item.getType(), value));
        meta.setDisplayName("§6§l" + item.getType() + " : " + value);
        item.setItemMeta(meta);
        return item;
    }

    private List<String> getLore(Material material, int value)
    {
        List<String> list = new ArrayList<String>();
        list.add("§6§l[ StorageBox ]");
        list.add(material.toString());
        list.add(String.valueOf(value));

        return list;
    }

    /**
     * アイテムがストレージボックスであるかを判定します。
     * 基本的に初期で使用してください。
     *
     * @param itemStack 判定したいアイテム
     * @return
     */
    public boolean isStorageBox(ItemStack itemStack)
    {
        if (itemStack != null)
            if (itemStack.hasItemMeta())
                if (itemStack.getItemMeta().hasLore())
                    if (itemStack.getItemMeta().getLore().get(0).contains("StorageBox"))
                        return true;
        return false;
    }

    /**
     * 内容個数を返します。必ずnullチェックと、 {@link #isStorageBox(ItemStack)} を実行してください。
     *
     * @return
     */
    public Integer getValue(ItemStack item)
    {
        if (!isStorageBox(item))
            return null;
        int value = 0;
        List<String> lore = item.getItemMeta().getLore();
        value = Integer.valueOf(lore.get(2));
        return value;
    }

    public Material getMaterial(ItemStack item)
    {
        if (!isStorageBox(item))
            return null;
        Material material = null;
        List<String> lore = item.getItemMeta().getLore();
        material = Material.getMaterial(lore.get(1).toUpperCase());
        return material;
    }

    /**
     * ストレージボックスという証明(lore)を消します。
     * 実体を消すものではありません。
     *
     * @param item
     */
    public void removeStorageBox(ItemStack item)
    {
        if (!isStorageBox(item))
        {
            plugin.getLogger().warning("ストレージボックスでは無いものが指定されました。");
            return;
        }
        ItemMeta meta = item.getItemMeta();
        meta.setLore(null);
        item.setItemMeta(meta);
        item.setAmount(0);
    }

    /**
     * 指定したStringが数字であるかを判定
     *
     * @param num 判定したいString型
     * @return boolean
     */
    public boolean isNumber(String num)
    {
        try
        {
            Integer.parseInt(num);
            return true;
        } catch (NumberFormatException e)
        {
            return false;
        }
    }

    public void giveItem(Player player, ItemStack item, int value)
    {
        World world = player.getWorld();
        Location location = player.getLocation();
        int remainder = value % 64;
        if (remainder <= 0)
        {
            world.dropItemNaturally(location, item);
            return;
        }
        int multiply_value = value / 64;
        ItemStack clone = item.clone();
        clone.setAmount(1);
        while (multiply_value == 0)
        {
            for (int i = 0; i < 64; i++)
            {
                world.dropItemNaturally(location, clone);
            }
        }
        for (int b = 0; b < remainder; b++)
        {
            world.dropItemNaturally(location, clone);
        }
    }

    public boolean isSimilar(ItemStack item1, ItemStack item2)
    {
        if (item1 == null || item2 == null)
        {
            return false;
        }

        plugin.debug(item1.getType().toString() + " : " + item2.getType().toString()
         + ", " + item1.getDurability() + " : " + item2.getDurability()
         + ", " + item1.getData().getData() + " : " + item2.getData().getData());

        return
                item1.getType() == item2.getType() &&
                        item1.getDurability() == item2.getDurability() &&
                        item1.getData().getData() == item2.getData().getData();
    }
}
