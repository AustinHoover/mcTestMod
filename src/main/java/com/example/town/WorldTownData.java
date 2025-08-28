package com.example.town;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores data about the towns in the overworld dimensions
 */
public class WorldTownData {
    
    private static WorldTownData instance;
    
    /**
     * The towns in the world
     */
    private List<Town> towns;
    
    private WorldTownData() {
        this.towns = new ArrayList<>();
    }
    
    /**
     * Get the singleton instance of WorldTownData
     */
    public static WorldTownData getInstance() {
        if (instance == null) {
            instance = new WorldTownData();
        }
        return instance;
    }
    
    /**
     * Get the list of towns
     */
    public List<Town> getTowns() {
        return towns;
    }
    
    /**
     * Set the list of towns
     */
    public void setTowns(List<Town> towns) {
        this.towns = towns;
    }
    
    /**
     * Add a town to the list
     */
    public void addTown(Town town) {
        this.towns.add(town);
    }
    
    /**
     * Remove a town by UUID
     */
    public boolean removeTown(String uuid) {
        return this.towns.removeIf(town -> town.getUUID().equals(uuid));
    }
    
    /**
     * Get a town by UUID
     */
    public Town getTown(String uuid) {
        return this.towns.stream()
                .filter(town -> town.getUUID().equals(uuid))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Clear all towns
     */
    public void clearTowns() {
        this.towns.clear();
    }
    
    /**
     * Get the number of towns
     */
    public int getTownCount() {
        return this.towns.size();
    }
}
