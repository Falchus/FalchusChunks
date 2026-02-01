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
	private final World world;
	private final boolean unloadable;
	
	private final Queue<ChunkCoord> queue = new ArrayDeque<>();
	private final Location originalLocation;
	private final GameMode originalGamemode;

	private final int total;
	private int processed = 0;

    public GenerateTask(Player player, int radius, boolean unloadable) {
        this.player = player;
        world = player.getWorld();
        this.unloadable = unloadable;
        
        int chunkRadius = radius >> 4;
        int cx = player.getLocation().getBlockX() >> 4;
        int cz = player.getLocation().getBlockZ() >> 4;
        for (int x = -chunkRadius; x <= chunkRadius; x += 3) {
            for (int z = -chunkRadius; z <= chunkRadius; z += 3) {
                queue.add(new ChunkCoord(cx + x, cz + z));
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
		while (!queue.isEmpty()) {		
	    	ChunkCoord coord = queue.peek();
			
	    	if (!world.isChunkLoaded(coord.x, coord.z)) {
	    		queue.poll();
	    		
                if (!unloadable) {
                    plugin.getChunkManager().getChunks().add(coord);
                }
	            
		        int bx = (coord.x << 4) + 8;
		        int bz = (coord.z << 4) + 8;
		        int y = 100;
		        
		        String command = String.format((Bukkit.getName().equals("FalchusSpigot") ? "falchus" : "") + "spigot:tp %s %f %d %f", player.getName(), bx + 0.5, y, bz + 0.5);
		        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command); // async in FalchusSpigot
		
				processed++;
				
				if (processed % 10 == 0) {
		    		int percent = (processed * 100) / total;
		    		player.sendMessage(Main.prefix + "Generated §a" + processed + "§7/§e" + total + " §7chunks (" + percent + "%).");
				}
	    	} else {
	    		queue.poll();
	    		processed++;
	    	}
		}
		
		if (queue.isEmpty()) {
			finish();
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
