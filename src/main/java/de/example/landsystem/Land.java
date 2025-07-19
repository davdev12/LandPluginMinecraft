package de.example.landsystem;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Land {

    private final String name;
    private UUID president;
    private final Set<UUID> citizens;

    // Die zwei Ecken, die das Land begrenzen
    private Location pos1;
    private Location pos2;

    // Level des Landes, z.B. für Upgrade-System
    private int level;

    // Optional: alte Felder, falls du noch borderSize & center brauchst
    private Location center;
    private int radius;
    private double borderSize;

    // Zum Speichern von Border-Block-Locations (z.B. für spezielle Blöcke an Rand)
    private final Set<String> borderBlockLocations = new HashSet<>();

    // Konstruktor mit pos1 und pos2
    public Land(String name, UUID president, Location pos1, Location pos2) {
        this.name = name;
        this.president = president;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.citizens = new HashSet<>();
        this.citizens.add(president);
        this.level = 1;  // Start-Level 1
    }

    // Optionaler Konstruktor (wenn du noch center + radius brauchst)
    public Land(String name, UUID president, Location center, int radius, double borderSize) {
        this.name = name;
        this.president = president;
        this.center = center;
        this.radius = radius;
        this.borderSize = borderSize;
        this.citizens = new HashSet<>();
        this.citizens.add(president);
        this.level = 1;
    }

    // --- Getter und Setter ---

    public String getName() {
        return name;
    }

    public UUID getPresident() {
        return president;
    }

    public void setPresident(UUID president) {
        this.president = president;
    }

    public Set<UUID> getCitizens() {
        return citizens;
    }

    public void addCitizen(UUID uuid) {
        citizens.add(uuid);
    }

    public void removeCitizen(UUID uuid) {
        citizens.remove(uuid);
    }

    public boolean isCitizen(UUID uuid) {
        return citizens.contains(uuid);
    }

    public int getCitizenCount() {
        return citizens.size();
    }

    public Location getPos1() {
        return pos1;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    // Border Block Locations (für spezielle Grenzblock-Logik)

    public void addBorderBlock(Location loc) {
        borderBlockLocations.add(serializeLocation(loc));
    }

    public boolean isBorderBlock(Location loc) {
        return borderBlockLocations.contains(serializeLocation(loc));
    }

    private String serializeLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    // Alte Felder weiter verfügbar

    public Location getCenter() {
        if (pos1 == null || pos2 == null) return null;
        double centerX = (pos1.getX() + pos2.getX()) / 2;
        double centerY = (pos1.getY() + pos2.getY()) / 2;
        double centerZ = (pos1.getZ() + pos2.getZ()) / 2;
        return new Location(pos1.getWorld(), centerX, centerY, centerZ);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public double getBorderSize() {
        return borderSize;
    }

    public void setBorderSize(double borderSize) {
        this.borderSize = borderSize;
    }
}
