package de.example.landsystem;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LandParticleBorderTask extends BukkitRunnable {

    private final Main plugin;

    public LandParticleBorderTask(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        World world = Bukkit.getWorld("world"); // Optional: passe den Weltnamen an
        if (world == null) return;

        for (Land land : plugin.getLandManager().getAllLands().values()) {
            Location pos1 = land.getPos1();
            Location pos2 = land.getPos2();

            if (pos1 == null || pos2 == null) continue;

            int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
            int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
            int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
            int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

            int baseY = 0; // Jetzt beginnt es bei Y=0
            int height = world.getMaxHeight(); // Geht bis zur maximalen Bauhöhe

            // Kanten entlang X
            for (int x = minX; x <= maxX; x++) {
                spawnParticleColumn(world, x, baseY, minZ, height);
                spawnParticleColumn(world, x, baseY, maxZ, height);
            }

            // Kanten entlang Z (ohne Ecken doppelt zu machen)
            for (int z = minZ + 1; z < maxZ; z++) {
                spawnParticleColumn(world, minX, baseY, z, height);
                spawnParticleColumn(world, maxX, baseY, z, height);
            }
        }
    }

    private void spawnParticleColumn(World world, int x, int baseY, int z, int height) {
        for (int y = baseY; y < height; y += 1) { // alle 4 Blöcke ein Partikel für Performance
            Location loc = new Location(world, x + 0.5, y, z + 0.5);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.spawnParticle(Particle.DRAGON_BREATH, loc, 1, 0, 0, 0, 0);
            }
        }
    }
}
