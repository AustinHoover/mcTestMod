package com.example.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import com.example.examplemod.ExampleMod;

import net.minecraft.network.chat.Component;

/**
 * A wand item that can be used to interact with citizens and towns
 */
public class CitizenWand extends Item {
    
    public CitizenWand() {
        super(new Item.Properties().setId(ExampleMod.ITEMS.key("citizen_wand")));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        if (!level.isClientSide()) {
            // Server-side logic - use chat component for compatibility
            player.displayClientMessage(Component.literal("§6[CitizenWand] §fTest text: You used the Citizen Wand!"), false);
            player.displayClientMessage(Component.literal("§6[CitizenWand] §fThis wand will be used for citizen management in the future."), false);
        }
        
        return InteractionResult.SUCCESS;
    }
}
