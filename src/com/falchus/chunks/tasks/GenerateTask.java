package com.falchus.chunks.tasks;

import java.util.ArrayDeque;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.falchus.chunks.Main;

import lombok.AllArgsConstructor;

public class GenerateTask implements Runnable {
	
	private final Main plugin = Main.getInstance();
	
	private int taskId;
	
	private final Player player;
	private final boolean unloadable;
	
	private final Queue<ChunkCoord> queue = new ArrayDeque<>();
	private final Location originalLocation;
	private final GameMode originalGamemode;

	private final int total;
	private int processed = 0;

    public GenerateTask(Player player, int radius, boolean unloadable) {
        this.player = player;
        this.unloadable = unloadable;
        
        int chunkRadius = radius >> 4;
        int cx = player.getLocation().getBlockX() >> 4;
        int cz = player.getLocation().getBlockZ() >> 4;
        World world = player.getWorld();
        int viewDistance = Bukkit.getViewDistance();
        for (int x = -chunkRadius; x <= chunkRadius; x++) {
            for (int z = -chunkRadius; z <= chunkRadius; z++) {
            	int targetX = cx + x;
            	int targetZ = cz + z;
            	
            	ChunkCoord coord = new ChunkCoord(targetX, targetZ);
            	if (plugin.getChunkManager().getChunks().contains(coord)) continue;
            	
            	if (world.isChunkLoaded(targetX, targetZ)) continue;
            	
            	if (Math.abs(x) > viewDistance && Math.abs(z) > viewDistance) {
            		queue.add(coord);
            	}
            }
        }
        
        total = queue.size();
        
        originalLocation = player.getLocation().clone();
        originalGamemode = player.getGameMode();
    }
    
    public void start() {
    	player.setGameMode(GameMode.SPECTATOR);
    	player.sendMessage(Main.prefix + "Generating §a" + total + " §7chunks. Do not leave!");
    	
    	taskId = Bukkit.getScheduler()
    			.runTaskTimer(plugin, this, 0, 1)
    			.getTaskId();
    }
	
	@Override
	public void run() {
		if (queue.isEmpty()) {
			finish();
			return;
		}
		
    	ChunkCoord coord = queue.poll();
        
        int bx = (coord.x << 4) + 8;
        int bz = (coord.z << 4) + 8;
        int y = 100;
        
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (Bukkit.getName().equals("FalchusSpigot") ? "falchus" : "") + "spigot:tp " + player.getName() + " " + (bx + 0.5) + " " + y + " " + (bz + 0.5)); // async in FalchusSpigot

        if (!unloadable) {
            plugin.getChunkManager().getChunks().add(coord);
        }
        
		processed++;
		
		if (processed % 10 == 0) {
    		int percent = (processed * 100) / total;
    		player.sendMessage(Main.prefix + "Generated §a" + processed + "§7/§e" + total + " §7chunks (" + percent + "%).");
		}
	}
	
	private void finish() {
		Bukkit.getScheduler().cancelTask(taskId);
		
		player.teleport(originalLocation);
		player.setGameMode(originalGamemode);
		
		player.sendMessage(Main.prefix + "Generated §a" + processed + " §7chunks.");
	}
	
	@AllArgsConstructor
	public static class ChunkCoord {
		
		final int x;
		final int z;
	}
}
