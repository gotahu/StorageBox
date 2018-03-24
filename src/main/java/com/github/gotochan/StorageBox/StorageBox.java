package com.github.gotochan.StorageBox;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.gotochan.StorageBox.event.StorageBoxProcessEvent;
import com.github.gotochan.StorageBox.event.StorageInventory;
import com.github.gotochan.StorageBox.event.StorageOtherUse;
import com.github.gotochan.StorageBox.event.StoragePickup;
import com.github.gotochan.StorageBox.event.StoragePlace;

public class StorageBox extends JavaPlugin implements Listener
{

    private SBCommandExecutor executor;
    private boolean isEnableDebug = false;
    private SBUtil util;
    public SBGUI gui;

    public String prefix = "§a§l[ StorageBox ]§r ";
    public String error = "§c§l[ StorageBox ]§r ";


    @Override
    public void onEnable()
    {
        init();
        util = new SBUtil(this);
        gui = new SBGUI(this);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new StoragePlace(this), this);
        pm.registerEvents(new StoragePickup(this), this);
        pm.registerEvents(new StorageInventory(this), this);
        executor = new SBCommandExecutor(this);
        pm.registerEvents(new StorageOtherUse(this), this);
        pm.registerEvents(new StorageBoxProcessEvent(this), this);
        getCommand("storagebox").setExecutor(executor);
    }

    public void debug(String str)
    {
        if (this.isEnableDebug)
        {
            Bukkit.broadcast("§e§l[SBDebug] §r" + str, "sb.debug");
        }
    }

    private void init()
    {
        List<String> lore = new ArrayList<>();
        lore.add("§6§o右クリックで登録GUIを開くことが出来ます!");

        ItemStack chest = new ItemStack(Material.CHEST);
        ItemMeta meta = chest.getItemMeta();
        meta.setDisplayName("§6§n-アイテムを登録してください-");
        meta.setLore(lore);
        chest.setItemMeta(meta);

        ShapedRecipe recipe1 = new ShapedRecipe(chest);
        recipe1.shape("***", "* *", "***");
        recipe1.setIngredient('*', Material.CHEST);

        getServer().addRecipe(recipe1);
    }

    public SBUtil getUtil()
    {
        return util;
    }

    public boolean isEnableDebug()
    {
        return isEnableDebug;
    }

    public void setEnableDebug(boolean enableDebug)
    {
        isEnableDebug = enableDebug;
    }
}
