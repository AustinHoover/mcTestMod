package com.example.town;

import com.example.examplemod.ExampleMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages tick counting for towns
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class TownTickManager {

    private static final int TICK_INTERVAL = 20;
    private static final int LOG_INTERVAL = 2000;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TownTickManager.class);
    private static int tickCounter = 0;
    
    /**
     * Called every server tick to increment town tick counts
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        tickCounter++;
        
        // Increment tick count for all towns every 20 ticks (1 second)
        if (tickCounter % TICK_INTERVAL == 0) {
            WorldTownData worldData = WorldTownData.getInstance();
            int townCount = worldData.getTownCount();
            
            if (townCount > 0) {
                for (Town town : worldData.getTowns()) {
                    town.simulate(event);
                }
                
                // Log tick increment every 100 seconds (2000 ticks)
                if (tickCounter % LOG_INTERVAL == 0) {
                    LOGGER.debug("Incremented tick count for {} towns (tick: {})", townCount, tickCounter);
                }
            }
        }
    }
    
    /**
     * Get the current server tick counter
     */
    public static int getTickCounter() {
        return tickCounter;
    }
    
    /**
     * Reset the tick counter (useful for testing)
     */
    public static void resetTickCounter() {
        tickCounter = 0;
    }
}
