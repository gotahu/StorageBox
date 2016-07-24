package com.github.gotochan.StorageBox;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class StorageBox extends JavaPlugin implements Listener {

	private SBCommandExecutor executor;
	public SBUtil util;

	public String prefix = "§a§l[ StorageBox ]§r ";
	public String error  = "§c§l[ StorageBox ]§r ";

	@Override
	public void onEnable()
	{
		util = new SBUtil(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new StoragePlace(this), this);
		pm.registerEvents(new StoragePickup(this), this);
		executor = new SBCommandExecutor(this);
		pm.registerEvents(new StorageOtherUse(this), this);
		getCommand("storagebox").setExecutor(executor);
	}


}
