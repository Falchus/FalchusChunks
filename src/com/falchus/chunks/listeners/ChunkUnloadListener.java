package com.falchus.chunks.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.falchus.chunks.Main;

public class ChunkUnloadListener implements Listener {

	private final Main plugin = Main.getInstance();
	
	public ChunkUnloadListener() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		Chunk chunk = event.getChunk();
		if (plugin.getChunkManager().getChunks().contains(chunk)) {
			event.setCancelled(true);
		}
	}
}
