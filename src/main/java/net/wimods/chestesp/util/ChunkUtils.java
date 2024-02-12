/*
 * Copyright (c) 2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp.util;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;
import net.wimods.chestesp.ChestEspConfig;

public enum ChunkUtils
{
	;
	public static boolean update = true;
	public static boolean init = true;
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	private  static Vec3d playerPos = new Vec3d(1,0,1);
	public static Stream<Block> oldBlocks, newBlocks;

	public static Stream<BlockEntity> getLoadedBlockEntities()
	{
		return getLoadedChunks()
			.flatMap(chunk -> chunk.getBlockEntities().values().stream());
	}
	public static Stream<Block> getLoadedBlocks() {
		assert MC.player != null;
		if(init){
			init = false;
			playerPos = MC.player.getPos();
			newBlocks = getBlockStream();
			oldBlocks = newBlocks;
		}
		if (playerPos != null && playerPos.equals(MC.player.getPos())) update = false;
		else if(oldBlocks != null && newBlocks != null) update = false;
		else update = !Objects.equals(oldBlocks, newBlocks);
		if(!update) return oldBlocks;
		oldBlocks = newBlocks;
		playerPos = MC.player.getPos();
		newBlocks = getBlockStream();
		return newBlocks;
	}


	@NotNull
	public static Stream<Block> getBlockStream() {
		int radius = Math.max(2, MC.options.getClampedViewDistance()) + 3;
		int diameter = radius * 2 + 1;
		ChestEspConfig conf = new ChestEspConfig();
		BlockPos center = MC.player.getBlockPos();
		final int final_y = conf.min_height;
		BlockPos min = new ChunkPos((center.getX() - radius) * 16, (center.getZ() - radius) * 16).getStartPos();
		BlockPos max = new ChunkPos((center.getX() + radius) * 16, (center.getZ() + radius) * 16).getStartPos();
		Stream<WorldChunk> chunks = getLoadedChunks();

		Stream<Block> blockStream = Stream.<BlockPos> iterate(min, pos -> {
					int y = final_y;
					int x = pos.getX();
					int z = pos.getZ();
					x++;

					if(x > max.getX())
					{
						x = min.getX();
						z++;
					}
					if(z > max.getZ()){
						z = min.getZ();
						y++;
					}

					if(y > conf.max_height)
						throw new IllegalStateException("Stream limit didn't work.");

					return new BlockPos(x, z, y);

				}).limit(diameter * diameter)
				.filter(c -> MC.world.isChunkLoaded(c.getX() >> 4, c.getZ() >> 4))
				.map(c -> MC.world.getChunk(c.getX() >> 4, c.getZ() >> 4))
				.filter(chunk -> chunk != null)
				.flatMap(chunk -> {
					return BlockPos.stream(chunk.getPos().getStartX(), 0, chunk.getPos().getStartZ(),
							chunk.getPos().getEndX(), MC.world.getHeight(), chunk.getPos().getEndZ());
				})
				.map(MC.world::getBlockState)
				.map(BlockState::getBlock)
				.filter(Objects::nonNull);
		return blockStream;
	}

	public static BlockPos getBlockPosition(Block b) {
		int radius = Math.max(2, MC.options.getClampedViewDistance()) + 3;
		int diameter = radius * 2 + 1;
		ChestEspConfig conf = new ChestEspConfig();
		Stream<WorldChunk> worldChunkStream = getLoadedChunks();
		BlockPos pos = new BlockPos(1, 1, 1);
		BlockPos foundPos = worldChunkStream.flatMap(chunk -> {
			for (int x = 0; x < radius; x++) {
				for (int y = conf.min_height; y < conf.max_height; y++) {
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
