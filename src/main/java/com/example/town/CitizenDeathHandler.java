package com.example.town;

import com.example.examplemod.ExampleMod;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles automatic removal of killed villagers from town citizen registries
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class CitizenDeathHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CitizenDeathHandler.class);
    
    /**
     * Called when any living entity dies
     */
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        
        // Only process villager deaths
        if (entity instanceof Villager) {
            Villager villager = (Villager) entity;
            java.util.UUID villagerUUID = villager.getUUID();
            
            // Check if this villager is a citizen of any town
            WorldTownData worldData = WorldTownData.getInstance();
            boolean wasRemoved = false;
            
            for (Town town : worldData.getTowns()) {
                if (town.isCitizen(villagerUUID)) {
                    town.removeCitizen(villagerUUID);
                    wasRemoved = true;
                    LOGGER.info("Removed killed villager {} from town {}", villagerUUID, town.getUUID());
                }
            }
            
            // If a villager was removed from any town, save the data
            if (wasRemoved) {
                TownDataManager.saveData();
                LOGGER.info("Villager {} was removed from town citizen registry due to death", villagerUUID);
            }
        }
    }
}
