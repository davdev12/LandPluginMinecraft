package de.example.landsystem;

import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.Objects;

public class ChunkPosition {
    private final String world;
    private final int x;
    private final int z;

    public ChunkPosition(String world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public ChunkPosition(Chunk chunk) {
        this(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public ChunkPosition(Location location) {
        this(location.getWorld().getName(), location.getChunk().getX(), location.getChunk().getZ());
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkPosition)) return false;
        ChunkPosition that = (ChunkPosition) o;
        return x == that.x && z == that.z && world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, z);
    }

    @Override
    public String toString() {
        return "ChunkPosition{" +
                "world='" + world + '\'' +
                ", x=" + x +
                ", z=" + z +
                '}';
    }
}
