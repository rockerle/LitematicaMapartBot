package net.rockerle.mapbot.mapbot.client.pathfinding.astar;

import net.minecraft.util.math.BlockPos;

import java.util.LinkedList;

public record PathResult(LinkedList<BlockPos> path, long time) {}