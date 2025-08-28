package com.example.town;

import org.apache.commons.compress.compressors.lz77support.LZ77Compressor.Block;
import org.joml.Vector3L;
import com.example.town.citizen.Citizen;
import com.example.town.citizen.Goal;
import com.example.town.citizen.Goal.GoalType;
import com.example.town.task.Task;
import com.example.util.BlockSearchUtils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Macro data about a town
 */
public class Town {

    /**
     * Scan range for woodcutting task
     */
    private static final int WOODCUT_RANGE = 10;

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
    private Set<Citizen> citizens;

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
     * Increment the tick count by 1 and simulate town behavior
     */
    public void simulate(TickEvent.ServerTickEvent event) {
        this.tickCount++;

        ServerLevel overworld = event.getServer().overworld();
        
        // Check each citizen for task assignment
        for (Citizen citizen : this.citizens) {
            Entity entity = event.getServer().overworld().getEntity(citizen.getEntityUUID());
            if(entity == null){
                continue;
            }
            // If citizen doesn't have a goal, try to assign a task
            if (!citizen.hasGoal()) {
                // Look for unassigned tasks in the queue
                for (Task task : this.tasks) {
                    if (task.isUnassigned()) {
                        // Check if the task can be assigned before proceeding
                        if (canTaskBeAssigned(overworld, entity, task)) {
                            // Create a goal for this citizen based on the task
                            Goal goal = createGoalFromTask(task);
                            citizen.setGoal(goal);
                            
                            // Assign the task to this citizen
                            task.setOwner(citizen.getEntityUUID());
                            
                            // Log the assignment (optional)
                            System.out.println("Assigned task " + task.getTaskId() + " to citizen " + citizen.getEntityUUID());
                            break; // Only assign one task per citizen per tick
                        } else {
                            // Log that the task couldn't be assigned
                            System.out.println("Task " + task.getTaskId() + " cannot be assigned (validation failed)");
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Create a goal for a citizen based on a task
     */
    private Goal createGoalFromTask(Task task) {
        GoalType goalType;
        
        switch (task.getTaskType()) {
            case WOODCUT:
                goalType = GoalType.WORK;
                break;
            default:
                goalType = GoalType.WORK;
                break;
        }
        
        return new Goal(goalType, task.getPosition(), task);
    }
    
    /**
     * Get all unassigned tasks (tasks without an owner)
     */
    public List<Task> getUnassignedTasks() {
        return this.tasks.stream()
            .filter(Task::isUnassigned)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get the count of unassigned tasks
     */
    public int getUnassignedTaskCount() {
        return (int) this.tasks.stream()
            .filter(Task::isUnassigned)
            .count();
    }
    
    /**
     * Get tasks assigned to a specific citizen
     */
    public List<Task> getTasksAssignedTo(UUID citizenUUID) {
        return this.tasks.stream()
            .filter(task -> task.isAssignedTo(citizenUUID))
            .collect(java.util.stream.Collectors.toList());
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
    
    /**
     * Add a task to the town's task queue
     */
    public void addTask(Task task) {
        this.tasks.add(task);
    }
    
    /**
     * Remove a task from the town's task queue
     */
    public void removeTask(Task task) {
        this.tasks.remove(task);
    }
    
    /**
     * Remove a task by its ID from the town's task queue
     */
    public void removeTask(UUID taskId) {
        this.tasks.removeIf(task -> task.getTaskId().equals(taskId));
    }
    
    /**
     * Find a task by its ID
     */
    public Task getTask(UUID taskId) {
        return this.tasks.stream()
            .filter(task -> task.getTaskId().equals(taskId))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Check if a task with the given ID exists in this town
     */
    public boolean hasTask(UUID taskId) {
        return this.tasks.stream().anyMatch(task -> task.getTaskId().equals(taskId));
    }
    
    /**
     * Get the number of tasks in this town's queue
     */
    public int getTaskCount() {
        return this.tasks.size();
    }
    
    /**
     * Get tasks of a specific type
     */
    public List<Task> getTasksOfType(Task.TaskType taskType) {
        return this.tasks.stream()
            .filter(task -> task.getTaskType() == taskType)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get the count of tasks of a specific type
     */
    public int getTaskCountOfType(Task.TaskType taskType) {
        return (int) this.tasks.stream()
            .filter(task -> task.getTaskType() == taskType)
            .count();
    }

    private boolean canTaskBeAssigned(Level level, Entity entity, Task task) {
        if(!task.isUnassigned()){
            System.out.println("Task is already assigned");
            return false;
        }
        switch(task.getTaskType()){
            case WOODCUT: {
                Vector3L blockPos = BlockSearchUtils.findBlockInRange(level, WOODCUT_RANGE, Blocks.OAK_WOOD, this.getCenterPos());
                if(blockPos != null){
                    task.setPosition(blockPos);
                    return true;
                }
                System.out.println("Task is already assigned");
                return false;
            }
        }
        return true;
    }
}
