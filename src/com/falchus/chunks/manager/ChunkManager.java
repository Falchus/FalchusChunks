package com.falchus.chunks.manager;

import java.util.HashSet;
import java.util.Set;

import com.falchus.chunks.tasks.GenerateTask.ChunkCoord;

import lombok.Getter;

@Getter
public class ChunkManager {
	
	private final Set<ChunkCoord> chunks = new HashSet<>();
}
