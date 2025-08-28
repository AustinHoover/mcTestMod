package com.example.town;

/**
 * Tracks the resources stored in the town
 */
public class TownStockpile {
    
    /**
     * The wood stored in the town
     */
    private int wood = 0;

    /**
     * The food stored in the town
     */
    private int food = 0;

    /**
     * Gets the amount of wood stored in the town.
     * @return the wood amount
     */
    public int getWood() {
        return wood;
    }

    /**
     * Adds the specified amount of wood to the stockpile.
     * @param amount the amount to add
     */
    public void addWood(int amount) {
        this.wood += amount;
    }

    /**
     * Consumes the specified amount of wood from the stockpile, if available.
     * @param amount the amount to consume
     * @return true if enough wood was available and consumed, false otherwise
     */
    public boolean consumeWood(int amount) {
        if (this.wood >= amount) {
            this.wood -= amount;
            return true;
        }
        return false;
    }

    /**
     * Gets the amount of food stored in the town.
     * @return the food amount
     */
    public int getFood() {
        return food;
    }

    /**
     * Adds the specified amount of food to the stockpile.
     * @param amount the amount to add
     */
    public void addFood(int amount) {
        this.food += amount;
    }

    /**
     * Consumes the specified amount of food from the stockpile, if available.
     * @param amount the amount to consume
     * @return true if enough food was available and consumed, false otherwise
     */
    public boolean consumeFood(int amount) {
        if (this.food >= amount) {
            this.food -= amount;
            return true;
        }
        return false;
    }

}
