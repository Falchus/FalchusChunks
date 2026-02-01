package com.falchus.chunks.commands;

import org.bukkit.entity.Player;

import com.falchus.chunks.Main;
import com.falchus.chunks.tasks.GenerateTask;
import com.falchus.lib.minecraft.command.impl.SpigotCommandAdapter;

public class GenerateCommand extends SpigotCommandAdapter {
	
	public GenerateCommand() {
		super(Main.prefixPermission + "generate", Main.noPermissionMessage, Main.prefix + "§cUsage: /generate <radius> [unloadable]");
	}

	@Override
	public void executeCommand(Object sender, String[] args) {
		if (args.length != 1 && args.length != 2) {
			sendMessage(sender, getUsageMessage());
			return;
		}
		
		if (!(sender instanceof Player player)) return;
		
		int radius;
		try {
			radius = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			player.sendMessage(Main.prefix + "Invalid radius!");
			return;
		}
		
		boolean unloadable = true;
		if (args.length == 2) {
			unloadable = Boolean.parseBoolean(args[1]);
		}
		
		new GenerateTask(player, radius, unloadable).start();
	}
}
