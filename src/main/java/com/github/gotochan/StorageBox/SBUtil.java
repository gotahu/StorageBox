package com.github.gotochan.StorageBox;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SBUtil {

	private StorageBox plugin;

	public SBUtil(StorageBox plugin) {
		this.plugin = plugin;
	}

	public ItemStack createStorageBox(ItemStack item, int value)
	{
		ItemStack stack = item;
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(getLore(item.getType(), value));
		meta.setDisplayName("§6§l" + item.getType() + " : " + value);
		stack.setItemMeta(meta);
		return stack;
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
	 * @param itemStack 判定したいアイテム
	 * @return
	 */
	public boolean isStorageBox(ItemStack itemStack) {
		if (itemStack !=null)
			if (itemStack.hasItemMeta())
				if(itemStack.getItemMeta().hasLore())
					if(itemStack.getItemMeta().getLore().get(0).contains("StorageBox"))
						return true;
		return false;
	}

	/**
	 * 内容個数を返します。必ずnullチェックと、 {@link #isStorageBox(ItemStack)} を実行してください。
	 * @return
	 */
	public Integer getValue(ItemStack item) {
		if (!isStorageBox(item))
			return null;
		int value = 0;
		List<String> lore = item.getItemMeta().getLore();
		value = Integer.valueOf(lore.get(2));
		return value;
	}

	/**
	 * ストレージボックスという証明(lore)を消します。
	 * 実体を消すものではありません。
	 * @param item
	 */
	public void removeStorageBox(ItemStack item) {
		if (!isStorageBox(item)) {
			plugin.getLogger().warning("ストレージボックスでは無いものが指定されました。");
			return;
		}
		ItemMeta meta = item.getItemMeta();
		meta.setLore(null);
		item.setItemMeta(meta);
		item.setAmount(0);
	}

}
