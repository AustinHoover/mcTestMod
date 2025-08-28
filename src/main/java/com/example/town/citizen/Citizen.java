package com.example.town.citizen;

import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

/**
 * A citizen of a town
 */
public class Citizen {

    private static final double MOVE_TO_DISTANCE = 1.0;

    private static final double MOVE_TO_SPEED = 0.2;

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
    
    /**
     * Simulate the citizen's behavior based on their current goal
     * @param entity The entity representing the citizen in the world
     * @param level The server level for world access
     */
    public void simulate(Entity entity, ServerLevel level) {
        if (!this.hasGoal()) {
            return; // Citizen has no goal, nothing to simulate
        }

        Vec3 entityPos = entity.position();
        Villager villager = (Villager) entity;
        Goal goal = this.getGoal();
        
        // First check if the task associated with the goal is null
        if (goal.getTask() == null) {
            // Task is null, clear the goal
            this.setGoal(null);
            System.out.println("Citizen " + this.entityUUID + " had goal with null task, clearing goal");
            return;
        }
        
        // Check if the position of the task is defined
        if (goal.getPosition() == null) {
            // Task position is not defined, clear the goal
            this.setGoal(null);
            System.out.println("Citizen " + this.entityUUID + " had goal with undefined position, clearing goal");
            return;
        }
        
        // Check if the entity is within 1 unit of the task's position
        Vec3 taskPos = new Vec3(goal.getPosition().x(), goal.getPosition().y(), goal.getPosition().z());
        double distance = entityPos.distanceTo(taskPos);
        
        if (distance > MOVE_TO_DISTANCE) {
            // Entity is NOT within range, set the goal to be "MOVE_TO"
            if (goal.getType() != Goal.GoalType.MOVE_TO) {
                Goal newGoal = new Goal(Goal.GoalType.MOVE_TO, goal.getPosition(), goal.getTask());
                this.setGoal(newGoal);
                System.out.println("Citizen " + this.entityUUID + " is " + String.format("%.2f", distance) + " units from task, switching to MOVE_TO goal");
            }
        } else {
            // Entity is within range, ensure goal type matches the task type
            Goal.GoalType expectedGoalType = getExpectedGoalTypeForTask(goal.getTask());
            if (goal.getType() != expectedGoalType) {
                Goal newGoal = new Goal(expectedGoalType, goal.getPosition(), goal.getTask());
                this.setGoal(newGoal);
                System.out.println("Citizen " + this.entityUUID + " reached task position, switching to " + expectedGoalType + " goal");
            }
        }
        
        // Now process the current goal based on its type
        Goal.GoalType goalType = goal.getType();
        
        // Process the goal based on its type
        switch (goalType) {
            case MOVE_TO: {
                // For now, do nothing - just log that the citizen is moving
                System.out.println("Citizen " + this.entityUUID + " is moving to position " + goal.getPosition());
                if(goal.getPath() == null){
                    villager.getNavigation().moveTo(goal.getPosition().x,goal.getPosition().y,goal.getPosition().z, MOVE_TO_SPEED);
                    goal.setPath(villager.getNavigation().getPath());
                }
            } break;
                
            case WORK:
                // For now, do nothing - just log that the citizen is working
                if (goal.getTask() != null) {
                    System.out.println("Citizen " + this.entityUUID + " is working on task " + goal.getTask().getTaskId() + " at position " + goal.getPosition());
                } else {
                    System.out.println("Citizen " + this.entityUUID + " is working at position " + goal.getPosition());
                }
                break;
                
            case IDLE:
                // For now, do nothing - just log that the citizen is idle
                System.out.println("Citizen " + this.entityUUID + " is idle");
                break;
                
            default:
                System.out.println("Citizen " + this.entityUUID + " has unknown goal type: " + goalType);
                break;
        }
        
        // TODO: Implement actual goal behavior logic here
        // - MOVE_TO: Pathfinding to the target position
        // - WORK: Performing the actual work (chopping wood, farming, etc.)
        // - IDLE: Random movement or waiting behavior
    }
    
    /**
     * Determine the expected goal type for a given task
     * @param task The task to determine the goal type for
     * @return The appropriate goal type for the task
     */
    private Goal.GoalType getExpectedGoalTypeForTask(com.example.town.task.Task task) {
        if (task == null) {
            return Goal.GoalType.IDLE;
        }
        
        switch (task.getTaskType()) {
            case WOODCUT:
                return Goal.GoalType.WORK;
            // Add other task types as needed
            default:
                return Goal.GoalType.WORK;
        }
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
