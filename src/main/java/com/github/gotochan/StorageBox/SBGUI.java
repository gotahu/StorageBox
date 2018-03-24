package com.github.gotochan.StorageBox;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
public class SBGUI
{

    private StorageBox plugin;
    private SBUtil util;

    private HashMap<UUID, Inventory> guiInventory = new HashMap<>();

    private HashMap<UUID, HashMap<Material, Inventory>> editorMap = new HashMap<>();

    private final ItemStack whitePane = getMetadataItem(Material.STAINED_GLASS_PANE, (byte) 0);
    private final ItemStack air = new ItemStack(Material.AIR);
    private final ItemStack okButton = getMetadataItem(Material.WOOL, (byte) 13);
    private final ItemStack cancelButton = getMetadataItem(Material.WOOL, (byte) 14);

    public SBGUI(StorageBox plugin)
    {
        this.plugin = plugin;
        this.util = plugin.getUtil();
        cosmeticButton();
    }

    public Inventory getSubmitGUI(UUID uuid)
    {
        if (guiInventory.get(uuid) == null)
        {
            Inventory gui = Bukkit.createInventory(null, 45, "[SB] 登録 - Submit");
            int[] black = {11, 12, 13, 20, 22, 29, 30, 31};
            for (int i = 0; i < gui.getSize(); i++)
            {
                gui.setItem(i, whitePane);
            }
            //11
            for (int o : black)
            {
                gui.setItem(o, getMetadataItem(Material.STAINED_GLASS_PANE, (byte) 15));
            }
            gui.setItem(21, air);
            gui.setItem(15, okButton);
            gui.setItem(33, cancelButton);
            guiInventory.put(uuid, gui);
            return gui;
        } else
            return guiInventory.get(uuid);
    }

    public Inventory getEditorGUI(ItemStack item, UUID uuid)
    {
        if (!util.isStorageBox(item))
        {
            return null;
        }

        int value = util.getValue(item);
        Material material = util.getMaterial(item);

        if (editorMap.get(uuid) != null)
        {
            if (editorMap.get(uuid).get(material) != null)
            {
                return editorMap.get(uuid).get(material);
            }
        }

        Inventory inventory = Bukkit.createInventory(null, InventoryType.FURNACE, "[SB] 編集 - Edit");
        inventory.setItem(1, new ItemStack(material, value));
        return inventory;
    }

    private ItemStack getMetadataItem(Material material, byte meta)
    {
        return new ItemStack(material, 1, (short) 0, meta);
    }

    private void cosmeticButton()
    {
        ItemMeta okMeta = okButton.getItemMeta();
        ItemMeta cancelMeta = cancelButton.getItemMeta();
        okMeta.setDisplayName("§aOK");
        cancelMeta.setDisplayName("§cCancel");
        okButton.setItemMeta(okMeta);
        cancelButton.setItemMeta(cancelMeta);
    }

}
