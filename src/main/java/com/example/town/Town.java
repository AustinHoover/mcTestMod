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
    private int stockpile;
    
    public Town() {
        this.uuid = java.util.UUID.randomUUID().toString();
        this.centerPos = new Vector3L(0, 0, 0);
        this.stockpile = 0;
    }
    
    public Town(String uuid, Vector3L centerPos, int stockpile) {
        this.uuid = uuid;
        this.centerPos = centerPos;
        this.stockpile = stockpile;
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
    
    public int getStockpile() {
        return stockpile;
    }
    
    public void setStockpile(int stockpile) {
        this.stockpile = stockpile;
    }
}
