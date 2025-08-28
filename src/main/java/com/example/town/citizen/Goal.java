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
    Vector3L position;

    /**
     * The type of the goal
     */
    GoalType type;

    /**
     * Any associated task
     */
    Task task;
    
}
