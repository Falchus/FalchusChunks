package com.falchus.chunks.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.falchus.chunks.Main;
import com.falchus.chunks.tasks.GenerateTask;
import com.falchus.lib.minecraft.command.impl.SpigotCommandAdapter;

public class GenerateCommand extends SpigotCommandAdapter {
	
	public GenerateCommand() {
		super(Main.prefixPermission + "generate", Main.noPermissionMessage, Main.prefix + "§cUsage: /generate <radius> [teleport] [unloadable]");
	}

	@Override
	public void executeCommand(Object sender, String[] args) {
		if (!(sender instanceof Player player)) return;
		if (args.length < 1 || args.length > 3) {
			sendMessage(sender, getUsageMessage());
			return;
		}
		
		int radius;
		try {
			radius = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			player.sendMessage(Main.prefix + "Invalid radius!");
			return;
		}
		
		boolean teleport = args.length >= 2 && Boolean.parseBoolean(args[1]);
		boolean unloadable = args.length < 3 || Boolean.parseBoolean(args[2]);
		
		new GenerateTask(player, radius, teleport, unloadable).start();
	}
	
	@Override
	public List<String> tabComplete(Object sender, String[] args) {
		List<String> completions = new ArrayList<>();
		switch (args.length) {
			case 2:
			case 3:
				completions.addAll(List.of(
					"true",
					"false"
				));
				break;
				
			default:
				break;
		}
		return completions;
	}
}
