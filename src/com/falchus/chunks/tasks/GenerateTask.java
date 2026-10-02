package com.falchus.chunks.tasks;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.falchus.chunks.Main;
import com.falchus.lib.enums.TaskPriority;
import com.falchus.lib.minecraft.spigot.task.SpigotTask;
import com.falchus.lib.minecraft.spigot.utils.WorldUtils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenerateTask implements Runnable {
	
	private static final Main plugin = Main.getInstance();
	
	private int taskId;
	
	final Player player;
	final World world;
	final boolean teleport;
	final boolean unloadable;
	
	final Queue<ChunkCoord> queue = new ArrayDeque<>();
	final Location originalLocation;
	final GameMode originalGamemode;

	final int total;
	int processed;

    public GenerateTask(Player player, int radius, boolean teleport, boolean unloadable) {
        this.player = player;
        world = player.getWorld();
        this.teleport = teleport;
        this.unloadable = unloadable;
        
        int chunkRadius = radius >> 4;
        int cx = player.getLocation().getBlockX() >> 4;
        int cz = player.getLocation().getBlockZ() >> 4;
        int viewDistance = Bukkit.getViewDistance();
        for (int x = -chunkRadius; x <= chunkRadius; x++) {
            for (int z = -chunkRadius; z <= chunkRadius; z++) {
            	int targetX = cx + x;
            	int targetZ = cz + z;
            	
            	ChunkCoord coord = new ChunkCoord(targetX, targetZ);
            	if (plugin.getChunkManager().getChunks().contains(coord)) continue;
            	
            	if (world.isChunkLoaded(targetX, targetZ)) continue;
            	
            	if (!teleport || (Math.abs(x) > viewDistance && Math.abs(z) > viewDistance)) {
            		queue.add(coord);
            	}
            }
        }
        
        total = queue.size();
        
        originalLocation = player.getLocation().clone();
        originalGamemode = player.getGameMode();
    }
    
    public void start() {
    	if (teleport) {
    		player.setGameMode(GameMode.SPECTATOR);
    	}
    	player.sendMessage(Main.prefix + "Generating §a" + total + " §7chunks." + (teleport ? " Do not leave!" : ""));
    	
    	taskId = SpigotTask.of(this)
    			.runTimer(100, TimeUnit.MILLISECONDS)
    			.getId();
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
        Location location = new Location(world, bx + 0.5, 100, bz + 0.5);
        
        if (teleport) {
        	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (Bukkit.getName().equals("FalchusSpigot") ? "falchus" : "") + "spigot:tp " + player.getName() + " " + location.getX() + " " + location.getY() + " " + location.getZ()); // async in FalchusSpigot
        } else {
        	WorldUtils.getChunkAtAsync(world, location, true, TaskPriority.NORMAL);
        }
        
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
		SpigotTask.end(taskId);
		
		if (teleport) {
			player.teleport(originalLocation);
			player.setGameMode(originalGamemode);
		}
		
		player.sendMessage(Main.prefix + "Generated §a" + processed + " §7chunks.");
	}
	
	public record ChunkCoord(int x, int z) {}
}
