package net.rockerle.mapbot.mapbot.client.pathfinding.astar.util;

import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import net.minecraft.block.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.rockerle.mapbot.mapbot.client.MapbotClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockPosUtil {


    private static ClientWorld clientWorld = MapbotClient.mc.player.clientWorld;
    private static WorldSchematic schemWorld = SchematicWorldHandler.getSchematicWorld();
    private static BlockView bV = null;
    private static ClientPlayerEntity player = MapbotClient.mc.player;

    public static final ArrayList<Block> glassBlocks = new ArrayList(List.of(
            Blocks.GLASS,
            Blocks.BLACK_STAINED_GLASS,
            Blocks.BLUE_STAINED_GLASS,
            Blocks.BROWN_STAINED_GLASS,
            Blocks.CYAN_STAINED_GLASS,
            Blocks.GRAY_STAINED_GLASS,
            Blocks.GREEN_STAINED_GLASS,
            Blocks.LIME_STAINED_GLASS,
            Blocks.MAGENTA_STAINED_GLASS,
            Blocks.ORANGE_STAINED_GLASS,
            Blocks.PINK_STAINED_GLASS,
            Blocks.PURPLE_STAINED_GLASS,
            Blocks.RED_STAINED_GLASS,
            Blocks.WHITE_STAINED_GLASS,
            Blocks.YELLOW_STAINED_GLASS
    ));
    public static final ArrayList<Block> NonWalkable = new ArrayList<>(Arrays.asList(
            Blocks.CAVE_AIR,
            Blocks.AIR,
            Blocks.VOID_AIR,
            Blocks.WATER,
            Blocks.LAVA,
            Blocks.FIRE,
            Blocks.SOUL_FIRE,
            Blocks.BARRIER,
            Blocks.CACTUS,
            Blocks.SUGAR_CANE,

            Blocks.END_GATEWAY,
            Blocks.END_PORTAL,

            // unsure about the nether portal as it does not teleport the player instantly
            Blocks.NETHER_PORTAL
    ));

    private static boolean checkSpecialBlocks(Block block, BlockState state) {
        // if it's a trapdoor, it needs to be open
        if (block instanceof TrapdoorBlock) {
            return !state.get(((TrapdoorBlock) block).OPEN);
        }
        // to prevent sinking into the snow, only traverse snow if leather boots are in the players inventory
        if (block instanceof PowderSnowBlock) {
            return player.getInventory().contains(new ItemStack(Items.LEATHER_BOOTS));
        }

        if (block instanceof SignBlock) {
            return false;
        }

        if (glassBlocks.contains(block)) {
            return true;
        }
        return true;
    }

    public static double distance(BlockPos a, Vec3d b){
        double x = a.getX() - b.getX();
        double y = a.getY() - b.getY();
        double z = a.getZ() - b.getZ();

        return x*x + y*y + z*z;
    }
    public static double distance(Vec3d a, Vec3d b){
        double x = a.getX() - b.getX();
        double y = a.getY() - b.getY();
        double z = a.getZ() - b.getZ();

        return x*x + y*y + z*z;
    }
    public static int distance(BlockPos from, BlockPos to) {
        int x = from.getX() - to.getX();
        int y = from.getY() - to.getY();
        int z = from.getZ() - to.getZ();

        return x * x + y * y + z * z;
    }

    public static boolean isTraversable(BlockPos pos) {
        return isSameTraversable(pos) || isUpperTraversable(pos) || isLowerTraversable(pos);
    }

    public static boolean isWalkable(BlockState state, BlockPos pos) {
        // check if block is solid <- gets most blocks, but some walkable blocks are not solid
        // check custom list with not walkable blocks <- gets almost all blocks,
        // some blocks are dependent on their state if they are walkable
        // check special cases like snow and trapdoors <- handle the rest
        return state.isSolidBlock(bV, pos) ||
                (!NonWalkable.contains(state.getBlock()) && checkSpecialBlocks(state.getBlock(), state));
    }

    public static boolean isPosTraversable(BlockPos pos) {
        return (isWalkable(clientWorld.getBlockState(pos.down()), pos.down())
                && !isWalkable(clientWorld.getBlockState(pos), pos)
                && !isWalkable(clientWorld.getBlockState(pos.up()), pos.up()));
    }

    public static boolean isSameTraversable(BlockPos pos) {
        return isPosTraversable(pos);
    }

    public static boolean isUpperTraversable(BlockPos pos) {
        return isPosTraversable(pos.up());
    }

    public static boolean isLowerTraversable(BlockPos pos) {
        return isPosTraversable(pos.down());
    }
}