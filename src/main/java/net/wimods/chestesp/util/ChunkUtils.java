/*
 * Copyright (c) 2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.util;

import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.mrquba.data.BlockInfo;
import net.mrquba.data.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import net.wimods.chestesp.ChestEspConfig;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

import static java.lang.Math.abs;

public enum ChunkUtils
{
	;
	public static boolean update = true;
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	public static final BlockInfo block = new BlockInfo();

    public static Stream<BlockEntity> getLoadedBlockEntities()
	{
		return getLoadedChunks()
			.flatMap(chunk -> chunk.getBlockEntities().values().stream());
	}
//boolean isAir = bs.getBlock().equals(Blocks.AIR) || bs.getBlock().equals(Blocks.CAVE_AIR) || bs.getBlock().equals(Blocks.VOID_AIR);
public static List<Pair<Block, BlockPos>> getBlocksList(ConfigHolder<ChestEspConfig> configHolder) {
	int radius = Math.max(2, MC.options.getClampedViewDistance());
	assert MC.player != null;
	BlockPos center = MC.player.getBlockPos();
	BlockPos min = new BlockPos(center.getX() - radius, configHolder.get().min_height, center.getZ() - radius);
	BlockPos max = new BlockPos(center.getX() + radius, configHolder.get().max_height, center.getZ() + radius);

	int y = min.getY();
	int x = min.getX();
	int z = min.getZ();

	block.clear();
	block.add(Blocks.OBSIDIAN, new BlockPos(1, -60, 1));

	while (x <= max.getX() && z <= max.getZ() && y <= max.getY()) {
		if (MC.world == null) break;

		BlockPos currentPos = new BlockPos(x, y, z);
		BlockState bs = MC.world.getBlockState(currentPos);

		System.out.println("block: " + bs.getBlock() + " block pos:" + currentPos);

		if (bs.getBlock().equals(Blocks.OBSIDIAN) || bs.getBlock().equals(Blocks.CRYING_OBSIDIAN) || bs.getBlock().equals(Blocks.REINFORCED_DEEPSLATE)) {
			block.add(bs.getBlock(), currentPos);
		}

		y++;
		if (y > max.getY()) {
			y = min.getY();
			x++;
		}
		if (x > max.getX()) {
			x = min.getX();
			z++;
		}
	}
	return block.getList();
}


	public static BlockPos getBlockPosition(Block b, ConfigHolder<ChestEspConfig> configHolder) {
		int radius = Math.max(2, MC.options.getClampedViewDistance()) + 3;
		int diameter = radius * 2 + 1;
		Stream<WorldChunk> worldChunkStream = getLoadedChunks();
		BlockPos pos = new BlockPos(1, 1, 1);
		BlockPos foundPos = worldChunkStream.flatMap(chunk -> {
			for (int x = 0; x < radius; x++) {
				for (int y = configHolder.get().min_height; y < configHolder.get().max_height; y++) {
					for (int z = 0; z < radius; z++) {
						BlockPos blockPos = new BlockPos(x, y, z);
						if (chunk.getBlockState(blockPos).getBlock() == b) {
							return Stream.of(blockPos);
						}
					}
				}
			}
			return Stream.empty();
		}).findFirst().orElse(pos);

		return foundPos;
	}

	public static Stream<WorldChunk> getLoadedChunks()
	{
		int radius = Math.max(2, MC.options.getClampedViewDistance()) + 3;
		int diameter = radius * 2 + 1;
		
		ChunkPos center = MC.player.getChunkPos();
		ChunkPos min = new ChunkPos(center.x - radius, center.z - radius);
		ChunkPos max = new ChunkPos(center.x + radius, center.z + radius);
		
		Stream<WorldChunk> stream = Stream.<ChunkPos> iterate(min, pos -> {
			
			int x = pos.x;
			int z = pos.z;
			
			x++;
			
			if(x > max.x)
			{
				x = min.x;
				z++;
			}
			
			if(z > max.z)
				throw new IllegalStateException("Stream limit didn't work.");
			
			return new ChunkPos(x, z);
			
		}).limit(diameter * diameter)
			.filter(c -> MC.world.isChunkLoaded(c.x, c.z))
			.map(c -> MC.world.getChunk(c.x, c.z)).filter(Objects::nonNull);
		
		return stream;
	}
	
}
