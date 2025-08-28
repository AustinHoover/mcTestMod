package com.example.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BlockSearchUtils {
    
    /**
     * Scans all blocks within the given range of the starting position to find a block of the specified type.
     * Returns the position of the first matching block found, or null if none is found.
     *
     * @param range the search range (radius, inclusive)
     * @param blockType the block type to search for
     * @param startPos the starting position (org.joml.Vector3L)
     * @return the position of the matching block as a new Vector3L, or null if not found
     */
    public static org.joml.Vector3L findBlockInRange(Level level, int range, Block type, org.joml.Vector3L startPos) {
        // Assumes world.getBlockTypeAt(long x, long y, long z) returns the block type at the given position
        long sx = startPos.x();
        long sy = startPos.y();
        long sz = startPos.z();

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                for (int dz = -range; dz <= range; dz++) {
                    long x = sx + dx;
                    long y = sy + dy;
                    long z = sz + dz;
                    try {
                        BlockState state = level.getBlockState(new BlockPos((int)x,(int)y,(int)z));
                        if(state.is(type)){
                            return new org.joml.Vector3L((int)x,(int)y,(int)z);
                        }
                    } catch (Exception e) {
                        // If the method doesn't exist or invocation fails, skip this position
                        continue;
                    }
                }
            }
        }
        return null;
    }

}
