package com.falchus.chunks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.falchus.chunks.commands.GenerateCommand;
import com.falchus.chunks.listeners.*;
import com.falchus.chunks.manager.*;
import com.falchus.lib.minecraft.spigot.utils.Metrics;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Main extends JavaPlugin {

	static Main instance;

	ChunkUnloadListener chunkUnloadListener;
	ChunkManager chunkManager;
	
	public static final String server = "Falchus";
	public static final String serverLowerCase = server.toLowerCase();
	public static final String serverFull = server + "Chunks";
	public static final String colorcode = "§f§l";
	public static String prefix = "§8» " + colorcode + serverFull + "§r §8┃ §7";
	public static final String prefixPermission = serverLowerCase + ".";
	public static final String noPermissionMessage = prefix + "§cInsufficient permissions!";
	
	@Override
	public void onEnable() {
		instance = this;
		new Metrics(this, 29228);

		Bukkit.getScheduler().runTask(this, () -> {
			chunkUnloadListener = new ChunkUnloadListener();
			chunkManager = new ChunkManager();
			
			getCommand("generate").setExecutor(new GenerateCommand());
		});
	}
	
	public static Main getInstance() {
		return instance;
	}
}
