package com.example.town.citizen;

import org.joml.Vector3L;

import com.example.town.task.Task;

/**
 * A goal for a citizen
 */
public class Goal {

    /**
     * Type of goals
     */
    public static enum GoalType {
        MOVE_TO,
        WORK,
        IDLE,
    }

    /**
     * Position to perform the goal at
     */
    private Vector3L position;

    /**
     * The type of the goal
     */
    private GoalType type;

    /**
     * Any associated task
     */
    private Task task;
    
    /**
     * Default constructor
     */
    public Goal() {
        this.position = new Vector3L();
        this.type = GoalType.IDLE;
        this.task = null;
    }
    
    /**
     * Constructor with type
     */
    public Goal(GoalType type) {
        this.position = new Vector3L();
        this.type = type;
        this.task = null;
    }
    
    /**
     * Constructor with type and position
     */
    public Goal(GoalType type, Vector3L position) {
        this.position = new Vector3L();
        if (position != null) {
            this.position.set(position);
        }
        this.type = type;
        this.task = null;
    }
    
    /**
     * Constructor with type, position, and task
     */
    public Goal(GoalType type, Vector3L position, Task task) {
        this.position = new Vector3L();
        if (position != null) {
            this.position.set(position);
        }
        this.type = type;
        this.task = task;
    }
    
    // Getters and setters
    public Vector3L getPosition() {
        return position;
    }
    
    public void setPosition(Vector3L position) {
        if (position != null) {
            this.position.set(position);
        }
    }
    
    public GoalType getType() {
        return type;
    }
    
    public void setType(GoalType type) {
        this.type = type;
    }
    
    public Task getTask() {
        return task;
    }
    
    public void setTask(Task task) {
        this.task = task;
    }
    
    @Override
    public String toString() {
        return "Goal{type=" + type + ", position=" + position + ", task=" + task + "}";
    }
}
