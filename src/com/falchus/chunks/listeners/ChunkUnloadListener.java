package com.falchus.chunks.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.falchus.chunks.Main;
import com.falchus.chunks.tasks.GenerateTask.ChunkCoord;

public class ChunkUnloadListener implements Listener {

	private final Main plugin = Main.getInstance();
	
	public ChunkUnloadListener() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		Chunk chunk = event.getChunk();
		ChunkCoord coord = new ChunkCoord(chunk.getX(), chunk.getZ());
		if (plugin.getChunkManager().getChunks().contains(coord)) {
			event.setCancelled(true);
		}
	}
}
