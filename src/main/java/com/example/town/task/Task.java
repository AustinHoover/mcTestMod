package com.example.town.task;

import org.joml.Vector3L;
import java.util.UUID;

/**
 * A task queued for a town
 */
public class Task {
    
    public static enum TaskType {
        WOODCUT,
    }

    /**
     * Unique identifier for this task
     */
    private UUID taskId;

    /**
     * The type of job to perform
     */
    private TaskType taskType;

    /**
     * The position to perform the job at, if relevant
     */
    private Vector3L position;

    /**
     * The amount related to the job, if relevant
     */
    private int amount;

    public Task(TaskType taskType) {
        this.taskId = UUID.randomUUID();
        this.taskType = taskType;
        this.position = new Vector3L();
        this.amount = 0;
    }

    public Task(TaskType taskType, Vector3L position, int amount) {
        this.taskId = UUID.randomUUID();
        this.taskType = taskType;
        this.position = new Vector3L();
        if (position != null) {
            this.position.set(position);
        }
        this.amount = amount;
    }

    /**
     * Constructor with existing task ID (for deserialization)
     */
    public Task(UUID taskId, TaskType taskType, Vector3L position, int amount) {
        this.taskId = taskId;
        this.taskType = taskType;
        this.position = new Vector3L();
        if (position != null) {
            this.position.set(position);
        }
        this.amount = amount;
    }

    // Getters and setters
    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public Vector3L getPosition() {
        return position;
    }

    public void setPosition(Vector3L position) {
        if (position != null) {
            this.position.set(position);
        }
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return taskId.equals(task.taskId);
    }

    @Override
    public int hashCode() {
        return taskId.hashCode();
    }

    @Override
    public String toString() {
        return "Task{taskId=" + taskId + ", taskType=" + taskType + ", position=" + position + ", amount=" + amount + "}";
    }
}
