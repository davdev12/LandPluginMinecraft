package de.example.landsystem;

import java.util.HashSet;
import java.util.Set;

public class LandRegion {

    private final Set<ChunkPosition> chunks = new HashSet<>();

    public void addChunk(ChunkPosition chunk) {
        chunks.add(chunk);
    }

    public boolean contains(ChunkPosition chunk) {
        return chunks.contains(chunk);
    }

    public Set<ChunkPosition> getChunks() {
        return chunks;
    }
}