package de.example.landsystem;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SelectionManager {

    private final Map<UUID, Location> pos1 = new HashMap<>();
    private final Map<UUID, Location> pos2 = new HashMap<>();

    public void setPos1(UUID playerId, Location location) {
        pos1.put(playerId, location);
    }

    public void setPos2(UUID playerId, Location location) {
        pos2.put(playerId, location);
    }

    public Location getPos1(UUID playerId) {
        return pos1.get(playerId);
    }

    public Location getPos2(UUID playerId) {
        return pos2.get(playerId);
    }

    public boolean hasBothPositions(UUID playerId) {
        return pos1.containsKey(playerId) && pos2.containsKey(playerId);
    }

    public void clear(UUID playerId) {
        pos1.remove(playerId);
        pos2.remove(playerId);
    }
}
