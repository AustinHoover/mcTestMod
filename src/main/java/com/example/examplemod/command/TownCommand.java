package com.example.examplemod.command;

import com.example.town.Town;
import com.example.town.TownDataManager;
import com.example.town.WorldTownData;
import com.example.town.task.Task;
import com.example.town.task.Task.TaskType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3L;

/**
 * Command for managing towns
 */
public class TownCommand {
    
    /**
     * Register the town command
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("town")
            .requires(source -> source.hasPermission(2)) // Requires OP level 2
            .then(Commands.literal("spawn")
                .executes(TownCommand::spawnTownAtPlayer)
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(TownCommand::spawnTownAtPlayerWithName)))
            .then(Commands.literal("list")
                .executes(TownCommand::listTowns))
            .then(Commands.literal("info")
                .executes(TownCommand::showTownInfo))
            .then(Commands.literal("ticks")
                .executes(TownCommand::showTickInfo))
            .then(Commands.literal("give")
                .then(Commands.literal("wood")
                    .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(TownCommand::addWoodToNearestTown)))
                .then(Commands.literal("food")
                    .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(TownCommand::addFoodToNearestTown))))
            .then(Commands.literal("queue")
                .then(Commands.literal("woodcut")
                    .executes(TownCommand::queueWoodcutTask)
                )
                .then(Commands.literal("clear")
                    .executes(TownCommand::clearTasks)
                )
            )
                    
            );
    }
    
    /**
     * Spawn a town at the player's current position
     */
    private static int spawnTownAtPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Vector3L centerPos = new Vector3L(
            player.getBlockX(),
            player.getBlockY(),
            player.getBlockZ()
        );
        
        Town town = new Town();
        town.setCenterPos(centerPos);
        
        WorldTownData worldData = WorldTownData.getInstance();
        worldData.addTown(town);
        
        // Force save the data
        TownDataManager.saveData();
        
        context.getSource().sendSuccess(() -> Component.literal(
            String.format("Town created at position (%d, %d, %d) with UUID: %s", 
                centerPos.x(), centerPos.y(), centerPos.z(), town.getUUID())
        ), true);
        
        return 1;
    }
    
    /**
     * Spawn a town at the player's current position with a custom name
     */
    private static int spawnTownAtPlayerWithName(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String name = StringArgumentType.getString(context, "name");
        
        Vector3L centerPos = new Vector3L(
            player.getBlockX(),
            player.getBlockY(),
            player.getBlockZ()
        );
        
        Town town = new Town();
        town.setCenterPos(centerPos);
        // Note: The current Town class doesn't have a name field, but we could add one later
        
        WorldTownData worldData = WorldTownData.getInstance();
        worldData.addTown(town);
        
        // Force save the data
        TownDataManager.saveData();
        
        context.getSource().sendSuccess(() -> Component.literal(
            String.format("Town '%s' created at position (%d, %d, %d) with UUID: %s", 
                name, centerPos.x(), centerPos.y(), centerPos.z(), town.getUUID())
        ), true);
        
        return 1;
    }
    
    /**
     * List all towns in the world
     */
    private static int listTowns(CommandContext<CommandSourceStack> context) {
        WorldTownData worldData = WorldTownData.getInstance();
        int townCount = worldData.getTownCount();
        
        if (townCount == 0) {
            context.getSource().sendSuccess(() -> Component.literal("No towns exist in this world."), false);
            return 0;
        }
        
        context.getSource().sendSuccess(() -> Component.literal(
            String.format("Found %d town(s) in this world:", townCount)
        ), false);
        
        for (Town town : worldData.getTowns()) {
            Vector3L pos = town.getCenterPos();
            context.getSource().sendSuccess(() -> Component.literal(
                String.format("- Town %s at (%d, %d, %d) with stockpile [Wood: %d, Food: %d] (ticks: %d, citizens: %d)", 
                    town.getUUID(), pos.x(), pos.y(), pos.z(), 
                    town.getStockpile().getWood(), town.getStockpile().getFood(), town.getTickCount(), town.getCitizenCount())
            ), false);
        }
        
        return townCount;
    }
    
    /**
     * Show information about the town at the player's current position
     */
    private static int showTownInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
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
        
        if (nearestTown == null) {
            context.getSource().sendSuccess(() -> Component.literal("No towns found in this world."), false);
            return 0;
        }
        
        final Town finalNearestTown = nearestTown;
        final double finalNearestDistance = nearestDistance;
        Vector3L townPos = finalNearestTown.getCenterPos();
        context.getSource().sendSuccess(() -> Component.literal(
            String.format("Nearest town: %s at (%d, %d, %d) with stockpile [Wood: %d, Food: %d], tick count %d, citizens: %d (distance: %.1f blocks), tasks: %d", 
                finalNearestTown.getUUID(), townPos.x(), townPos.y(), townPos.z(), 
                finalNearestTown.getStockpile().getWood(), finalNearestTown.getStockpile().getFood(), 
                finalNearestTown.getTickCount(), finalNearestTown.getCitizenCount(), finalNearestDistance, finalNearestTown.getTasks().size())
        ), false);
        
        return 1;
    }
    
    /**
     * Show tick information for the server and towns
     */
    private static int showTickInfo(CommandContext<CommandSourceStack> context) {
        WorldTownData worldData = WorldTownData.getInstance();
        int townCount = worldData.getTownCount();
        
        context.getSource().sendSuccess(() -> Component.literal(
            String.format("Server tick counter: %d", com.example.town.TownTickManager.getTickCounter())
        ), false);
        
        context.getSource().sendSuccess(() -> Component.literal(
            String.format("Total towns: %d", townCount)
        ), false);
        
        if (townCount > 0) {
            long totalTicks = 0;
            for (Town town : worldData.getTowns()) {
                totalTicks += town.getTickCount();
            }
            
            final long finalTotalTicks = totalTicks;
            final int finalTownCount = townCount;
            
            context.getSource().sendSuccess(() -> Component.literal(
                String.format("Total town ticks: %d", finalTotalTicks)
            ), false);
            
            context.getSource().sendSuccess(() -> Component.literal(
                String.format("Average ticks per town: %.1f", (double) finalTotalTicks / finalTownCount)
            ), false);
        }
        
        return 1;
    }
    
    /**
     * Add wood to the nearest town
     */
    private static int addWoodToNearestTown(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        
        Town nearestTown = findNearestTown(player);
        if (nearestTown == null) {
            context.getSource().sendFailure(Component.literal("No towns found in this world."));
            return 0;
        }
        
        nearestTown.getStockpile().addWood(amount);
        TownDataManager.saveData();
        
        context.getSource().sendSuccess(() -> Component.literal(
            String.format("Added %d wood to town %s. New total: %d wood", 
                amount, nearestTown.getUUID(), nearestTown.getStockpile().getWood())
        ), true);
        
        return 1;
    }
    
    /**
     * Add food to the nearest town
     */
    private static int addFoodToNearestTown(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        
        Town nearestTown = findNearestTown(player);
        if (nearestTown == null) {
            context.getSource().sendFailure(Component.literal("No towns found in this world."));
            return 0;
        }
        
        nearestTown.getStockpile().addFood(amount);
        TownDataManager.saveData();
        
        context.getSource().sendSuccess(() -> Component.literal(
            String.format("Added %d food to town %s. New total: %d food", 
                amount, nearestTown.getUUID(), nearestTown.getStockpile().getFood())
        ), true);
        
        return 1;
    }

    private static int queueWoodcutTask(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Town nearestTown = findNearestTown(player);
        if (nearestTown == null) {
            context.getSource().sendFailure(Component.literal("No towns found in this world."));
            return 0;
        }
        nearestTown.getTasks().add(new Task(TaskType.WOODCUT));
        return 1;
    }

    private static int clearTasks(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Town nearestTown = findNearestTown(player);
        if (nearestTown == null) {
            context.getSource().sendFailure(Component.literal("No towns found in this world."));
            return 0;
        }
        nearestTown.getTasks().clear();
        return 1;
    }
    
    /**
     * Helper method to find the nearest town to a player
     */
    private static Town findNearestTown(ServerPlayer player) {
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
