package com.example.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

import com.example.examplemod.ExampleMod;
import com.example.town.Town;
import com.example.town.WorldTownData;
import com.example.town.TownDataManager;
import org.joml.Vector3L;

/**
 * A wand item that can be used to interact with citizens and towns
 */
public class CitizenWand extends Item {
    
    public CitizenWand() {
        super(new Item.Properties().setId(ExampleMod.ITEMS.key("citizen_wand")));
    }
    
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (entity.getType().equals(EntityType.VILLAGER) && !player.level().isClientSide()) {
            Villager villager = (Villager) entity;
            java.util.UUID villagerUUID = villager.getUUID();
            
            // Find the nearest town
            Town nearestTown = findNearestTown(player);
            if (nearestTown == null) {
                player.displayClientMessage(Component.literal("§6[CitizenWand] §cNo towns found nearby!"), false);
                return InteractionResult.SUCCESS;
            }
            
            // Check if villager is already a citizen
            if (nearestTown.isCitizen(villagerUUID)) {
                player.displayClientMessage(Component.literal("§6[CitizenWand] §eThis villager is already a citizen of town " + nearestTown.getUUID()), false);
                return InteractionResult.SUCCESS;
            }
            
            // Add villager to the town
            nearestTown.addCitizen(villagerUUID);
            
            // Save the data
            TownDataManager.saveData();
            
            // Send success message
            player.displayClientMessage(Component.literal("§6[CitizenWand] §aAdded villager to town " + nearestTown.getUUID() + "! Town now has " + nearestTown.getCitizenCount() + " citizens."), false);
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            // Show usage instructions
            player.displayClientMessage(Component.literal("§6[CitizenWand] §fRight-click on a villager to add them to the nearest town!"), false);
            player.displayClientMessage(Component.literal("§6[CitizenWand] §fThe wand will automatically find the closest town."), false);
        }
        
        return InteractionResult.SUCCESS;
    }
    
    /**
     * Helper method to find the nearest town to a player
     */
    private Town findNearestTown(Player player) {
        Vector3L playerPos = new Vector3L(
            player.getBlockX(),
            player.getBlockY(),
            player.getBlockZ()
        );
        
        WorldTownData worldData = WorldTownData.getInstance();
        Town nearestTown = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (Town town : worldData.getTowns()) {
            Vector3L townPos = town.getCenterPos();
            double distance = Math.sqrt(
                Math.pow(townPos.x() - playerPos.x(), 2) +
                Math.pow(townPos.y() - playerPos.y(), 2) +
                Math.pow(townPos.z() - playerPos.z(), 2)
            );
            
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestTown = town;
            }
        }
        
        return nearestTown;
    }
}
