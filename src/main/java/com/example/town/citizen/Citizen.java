package com.example.town.citizen;

import java.util.UUID;

/**
 * A citizen of a town
 */
public class Citizen {

    /**
     * The types of jobs a citizen can do
     */
    public static enum JobType {
        LOGGER,
        FARMER,
        BUILDER,
    }
    
    /**
     * The UUID of the entity that this citizen corresponds to
     */
    private UUID entityUUID;

    /**
     * The job assigned to this citizen
     */
    private JobType job = JobType.LOGGER;

    /**
     * The current goal of the citizen
     */
    private Goal goal;
    
    /**
     * Constructor with entity UUID
     */
    public Citizen(UUID entityUUID) {
        this.entityUUID = entityUUID;
        this.job = JobType.LOGGER;
    }
    
    // Getters and setters
    public UUID getEntityUUID() {
        return entityUUID;
    }
    
    public void setEntityUUID(UUID entityUUID) {
        this.entityUUID = entityUUID;
    }
    
    public JobType getJob() {
        return job;
    }
    
    public void setJob(JobType job) {
        this.job = job;
    }
    
    /**
     * Check if this citizen has a specific job
     */
    public boolean hasJob(JobType jobType) {
        return this.job == jobType;
    }
    
    /**
     * Get the current goal of the citizen
     */
    public Goal getGoal() {
        return goal;
    }
    
    /**
     * Set the current goal of the citizen
     */
    public void setGoal(Goal goal) {
        this.goal = goal;
    }
    
    /**
     * Check if the citizen has a goal
     */
    public boolean hasGoal() {
        return this.goal != null;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Citizen citizen = (Citizen) obj;
        return entityUUID.equals(citizen.entityUUID);
    }
    
    @Override
    public int hashCode() {
        return entityUUID.hashCode();
    }
    
    @Override
    public String toString() {
        return "Citizen{entityUUID=" + entityUUID + ", job=" + job + "'}";
    }
}
