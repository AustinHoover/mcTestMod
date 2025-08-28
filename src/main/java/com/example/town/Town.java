package com.example.town;

import org.joml.Vector3L;
import com.example.town.citizen.Citizen;
import com.example.town.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
     * Set of citizens for this town
     */
    private java.util.Set<Citizen> citizens;

    /**
     * The tasks queued for this town
     */
    private List<Task> tasks = new ArrayList<Task>();
    
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
     * Get the set of citizens
     */
    public java.util.Set<Citizen> getCitizens() {
        return citizens;
    }
    
    /**
     * Set the citizens set
     */
    public void setCitizens(java.util.Set<Citizen> citizens) {
        this.citizens = citizens;
    }
    
    /**
     * Add a citizen to the town
     */
    public void addCitizen(Citizen citizen) {
        this.citizens.add(citizen);
    }
    
    /**
     * Remove a citizen from the town
     */
    public void removeCitizen(Citizen citizen) {
        this.citizens.remove(citizen);
    }
    
    /**
     * Remove a citizen by UUID from the town
     */
    public void removeCitizen(UUID citizenUUID) {
        this.citizens.removeIf(citizen -> citizen.getEntityUUID().equals(citizenUUID));
    }
    
    /**
     * Check if an entity is a citizen of this town
     */
    public boolean isCitizen(UUID citizenUUID) {
        return this.citizens.stream().anyMatch(citizen -> citizen.getEntityUUID().equals(citizenUUID));
    }
    
    /**
     * Get a citizen by UUID
     */
    public Citizen getCitizen(UUID citizenUUID) {
        return this.citizens.stream()
            .filter(citizen -> citizen.getEntityUUID().equals(citizenUUID))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get the number of citizens in this town
     */
    public int getCitizenCount() {
        return this.citizens.size();
    }
    
    /**
     * Get citizens with a specific job
     */
    public java.util.List<Citizen> getCitizensWithJob(com.example.town.citizen.Citizen.JobType jobType) {
        return this.citizens.stream()
            .filter(citizen -> citizen.hasJob(jobType))
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get the count of citizens with a specific job
     */
    public int getCitizenCountWithJob(com.example.town.citizen.Citizen.JobType jobType) {
        return (int) this.citizens.stream()
            .filter(citizen -> citizen.hasJob(jobType))
            .count();
    }

    /**
     * Get the list of tasks for this town.
     */
    public List<Task> getTasks() {
        return this.tasks;
    }
}
