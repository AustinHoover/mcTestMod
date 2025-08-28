package com.example.town;

import org.joml.Vector3L;

/**
 * Macro data about a town
 */
public class Town {

    /**
     * The UUID of the town
     */
    private String uuid;

    /**
     * Center (block) position of the town
     */
    private Vector3L centerPos;

    /**
     * A generic stockpile value of the town
     */
    private TownStockpile stockpile;
    
    /**
     * Tick count for the town (incremented each server tick)
     */
    private long tickCount;
    
    /**
     * Set of citizen entity UUIDs for this town
     */
    private java.util.Set<java.util.UUID> citizens;
    
    public Town() {
        this.uuid = java.util.UUID.randomUUID().toString();
        this.centerPos = new Vector3L(0, 0, 0);
        this.stockpile = new TownStockpile();
        this.tickCount = 0;
        this.citizens = new java.util.HashSet<>();
    }
    
    public Town(String uuid, Vector3L centerPos) {
        this.uuid = uuid;
        this.centerPos = centerPos;
        this.stockpile = new TownStockpile();
        this.tickCount = 0;
        this.citizens = new java.util.HashSet<>();
    }
    
    public String getUUID() {
        return uuid;
    }
    
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }
    
    public Vector3L getCenterPos() {
        return centerPos;
    }
    
    public void setCenterPos(Vector3L centerPos) {
        this.centerPos = centerPos;
    }
    
    public TownStockpile getStockpile() {
        return stockpile;
    }
    
    public void setStockpile(TownStockpile stockpile) {
        this.stockpile = stockpile;
    }
    
    public long getTickCount() {
        return tickCount;
    }
    
    public void setTickCount(long tickCount) {
        this.tickCount = tickCount;
    }
    
    /**
     * Increment the tick count by 1
     */
    public void incrementTick() {
        this.tickCount++;
    }
    
    /**
     * Get the set of citizen UUIDs
     */
    public java.util.Set<java.util.UUID> getCitizens() {
        return citizens;
    }
    
    /**
     * Set the citizens set
     */
    public void setCitizens(java.util.Set<java.util.UUID> citizens) {
        this.citizens = citizens;
    }
    
    /**
     * Add a citizen to the town
     */
    public void addCitizen(java.util.UUID citizenUUID) {
        this.citizens.add(citizenUUID);
    }
    
    /**
     * Remove a citizen from the town
     */
    public void removeCitizen(java.util.UUID citizenUUID) {
        this.citizens.remove(citizenUUID);
    }
    
    /**
     * Check if an entity is a citizen of this town
     */
    public boolean isCitizen(java.util.UUID citizenUUID) {
        return this.citizens.contains(citizenUUID);
    }
    
    /**
     * Get the number of citizens in this town
     */
    public int getCitizenCount() {
        return this.citizens.size();
    }
}
