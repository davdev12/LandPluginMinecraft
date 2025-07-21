package de.example.landsystem;

import org.bukkit.Location;

public class RegionUtils {

    public static boolean regionsOverlap(Location pos1A, Location pos2A, Location pos1B, Location pos2B) {
        int ax1 = Math.min(pos1A.getBlockX(), pos2A.getBlockX());
        int ax2 = Math.max(pos1A.getBlockX(), pos2A.getBlockX());
        int az1 = Math.min(pos1A.getBlockZ(), pos2A.getBlockZ());
        int az2 = Math.max(pos1A.getBlockZ(), pos2A.getBlockZ());

        int bx1 = Math.min(pos1B.getBlockX(), pos2B.getBlockX());
        int bx2 = Math.max(pos1B.getBlockX(), pos2B.getBlockX());
        int bz1 = Math.min(pos1B.getBlockZ(), pos2B.getBlockZ());
        int bz2 = Math.max(pos1B.getBlockZ(), pos2B.getBlockZ());

        boolean overlapX = ax1 <= bx2 && ax2 >= bx1;
        boolean overlapZ = az1 <= bz2 && az2 >= bz1;

        return overlapX && overlapZ;
    }
}
