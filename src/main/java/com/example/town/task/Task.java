package com.example.town.task;

import org.joml.Vector3L;

/**
 * A task queued for a town
 */
public class Task {
    
    public static enum TaskType {
        WOODCUT,
    }

    /**
     * The type of job to perform
     */
    TaskType taskType;

    /**
     * The position to perform the job at, if relevant
     */
    Vector3L position;

    /**
     * The amount related to the job, if relevant
     */
    int amount;

    public Task(TaskType taskType) {
        this.taskType = taskType;
        this.position = new Vector3L();
        this.amount = 0;
    }

    public Task(TaskType taskType, Vector3L position, int amount) {
        this.taskType = taskType;
        this.position = new Vector3L();
        if (position != null) {
            this.position.set(position);
        }
        this.amount = amount;
    }

}
