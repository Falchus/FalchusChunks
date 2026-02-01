package com.falchus.chunks.manager;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Chunk;

import com.falchus.chunks.Main;

import lombok.Getter;

@Getter
public class ChunkManager {

	private final Main plugin = Main.getInstance();
	
	private final Set<Chunk> chunks = new HashSet<>();
}
