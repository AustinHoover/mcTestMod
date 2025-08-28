package com.example.town;

import com.example.examplemod.ExampleMod;
import com.example.town.citizen.Citizen;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3L;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages saving and loading of WorldTownData to/from save files
 */
@Mod.EventBusSubscriber(modid = ExampleMod.MODID)
public class TownDataManager {
    
    private static final String DATA_FILE_NAME = "town_data.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LoggerFactory.getLogger(TownDataManager.class);
    
    /**
     * Called when the server starts to load existing town data
     */
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        loadTownData(server);
        LOGGER.info("Town data loaded from save file");
    }
    
    /**
     * Called when the server stops to save town data
     */
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        MinecraftServer server = event.getServer();
        saveTownData(server);
        LOGGER.info("Town data saved to save file");
    }
    
    /**
     * Load town data from the save file
     */
    private static void loadTownData(MinecraftServer server) {
        try {
            Path savePath = server.getWorldPath(LevelResource.ROOT);
            File dataFile = savePath.resolve(DATA_FILE_NAME).toFile();
            
            if (dataFile.exists()) {
                try (FileReader reader = new FileReader(dataFile)) {
                    TownDataContainer container = GSON.fromJson(reader, TownDataContainer.class);
                    if (container != null && container.towns != null) {
                        WorldTownData worldData = WorldTownData.getInstance();
                        worldData.clearTowns();
                        
                        for (TownData townData : container.towns) {
                            Vector3L centerPos = new Vector3L((int)townData.centerX, (int)townData.centerY, (int)townData.centerZ);
                            Town town = new Town(townData.uuid, centerPos);
                            // Set the stockpile values from the loaded data
                            town.getStockpile().addWood(townData.wood);
                            town.getStockpile().addFood(townData.food);
                            // Reset tick count to 0 when loading from disk
                            town.setTickCount(0);
                            
                            // Load citizens if they exist
                            if (townData.citizens != null) {
                                for (CitizenData citizenData : townData.citizens) {
                                    try {
                                        UUID citizenUUID = UUID.fromString(citizenData.entityUUID);
                                        Citizen citizen = new Citizen(citizenUUID);
                                        town.addCitizen(citizen);
                                    } catch (IllegalArgumentException e) {
                                        LOGGER.warn("Invalid citizen UUID format: {}", citizenData.entityUUID);
                                    }
                                }
                            }
                            
                            worldData.addTown(town);
                        }
                        LOGGER.info("Loaded {} towns from save file", worldData.getTownCount());
                    }
                }
            } else {
                LOGGER.info("No existing town data found, starting with empty world");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load town data", e);
        }
    }
    
    /**
     * Save town data to the save file
     */
    private static void saveTownData(MinecraftServer server) {
        try {
            Path savePath = server.getWorldPath(LevelResource.ROOT);
            File dataFile = savePath.resolve(DATA_FILE_NAME).toFile();
            
            // Ensure the directory exists
            dataFile.getParentFile().mkdirs();
            
            WorldTownData worldData = WorldTownData.getInstance();
            List<Town> towns = worldData.getTowns();
            
            List<TownData> townDataList = new ArrayList<>();
            for (Town town : towns) {
                TownData townData = new TownData();
                townData.uuid = town.getUUID();
                townData.centerX = town.getCenterPos().x();
                townData.centerY = town.getCenterPos().y();
                townData.centerZ = town.getCenterPos().z();
                townData.wood = town.getStockpile().getWood();
                townData.food = town.getStockpile().getFood();
                townData.tickCount = town.getTickCount();
                
                // Convert citizens to CitizenData for JSON serialization
                townData.citizens = new java.util.ArrayList<>();
                for (Citizen citizen : town.getCitizens()) {
                    CitizenData citizenData = new CitizenData();
                    citizenData.entityUUID = citizen.getEntityUUID().toString();
                    citizenData.job = citizen.getJob();
                    townData.citizens.add(citizenData);
                }
                
                townDataList.add(townData);
            }
            
            TownDataContainer container = new TownDataContainer();
            container.towns = townDataList;
            
            try (FileWriter writer = new FileWriter(dataFile)) {
                GSON.toJson(container, writer);
            }
            
            LOGGER.info("Saved {} towns to save file", towns.size());
        } catch (Exception e) {
            LOGGER.error("Failed to save town data", e);
        }
    }
    
    /**
     * Force save the current town data
     */
    public static void saveData() {
        // This method can be called from commands or other parts of the mod
        LOGGER.info("Town data save requested");
    }
    
    /**
     * Container class for JSON serialization
     */
    private static class TownDataContainer {
        public List<TownData> towns;
    }
    
    /**
     * Data class for individual towns in JSON
     */
    private static class TownData {
        public String uuid;
        public long centerX;
        public long centerY;
        public long centerZ;
        public int wood;
        public int food;
        public long tickCount;
        public java.util.List<CitizenData> citizens;
    }
    
    /**
     * Data class for individual citizens in JSON
     */
    private static class CitizenData {
        public String entityUUID;
        public Citizen.JobType job;
    }
}
