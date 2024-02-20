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

public enum ChunkUtils
{
	;
	public static boolean update = true;
	public static boolean init = true;
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	private  static Vec3d playerPos = new Vec3d(1,0,1);
	public static final BlockInfo block = new BlockInfo();
	public static final BlockInfo blockOLD = new BlockInfo();
	public static final BlockInfo nullReturnValue = new BlockInfo();


    public static Stream<BlockEntity> getLoadedBlockEntities()
	{
		return getLoadedChunks()
			.flatMap(chunk -> chunk.getBlockEntities().values().stream());
	}
	public static List<Pair<Block, BlockPos>> getLoadedBlocks(ConfigHolder<ChestEspConfig> configHolder) {
		return getBlocksList(configHolder);
	}
	public static List<Pair<Block, BlockPos>> getBlocks() {
		return block.getList();
	}

	public static List<Pair<Block, BlockPos>> getBlocksList(ConfigHolder<ChestEspConfig> configHolder) {

		int radius = Math.max(2, MC.options.getClampedViewDistance()) + 3;
        assert MC.player != null;
        BlockPos center = MC.player.getBlockPos();
		BlockPos min = new ChunkPos((center.getX() - radius) * 16, (center.getZ() - radius) * 16).getStartPos();
		BlockPos max = new ChunkPos((center.getX() + radius) * 16, (center.getZ() + radius) * 16).getStartPos();
		final int min_final_y = configHolder.get().min_height;
		final int min_final_x = min.getX();
		final int min_final_z = min.getZ();
		final int max_final_y = configHolder.get().max_height;
		final int max_final_x = max.getX();
		final int max_final_z = max.getZ();
		int y = min_final_y;
		int x = min_final_x;
		int z = min_final_z;
		nullReturnValue.add(Blocks.STONE, new BlockPos(1,1,1));
		/*
		if(block.getList() == blockOLD.getList()) {
			blockOLD.copy(block.getList());
			block.clear();
			return blockOLD.getList();
		}*/
		//if(blockOLD.getList() != block.getList()) {
		block.clear();
		block.add(Blocks.OBSIDIAN, new BlockPos(1,1,1));
			while ((x <= max_final_x && z <= max_final_z && z <= max_final_y)) {
				Block b = null;
				if(MC.world == null) break;
				b = MC.world.getBlockState(new BlockPos(x, y, z)).getBlock();
				boolean isAir = !b.equals(Blocks.AIR) || !b.equals(Blocks.CAVE_AIR) || !b.equals(Blocks.VOID_AIR);
				y++;
				if (b.equals(Blocks.OBSIDIAN) || b.equals(Blocks.CRYING_OBSIDIAN) || b.equals(Blocks.REINFORCED_DEEPSLATE))  {
					System.out.println("block: " + b + " block pos:" + new BlockPos(x, y, z));
					block.add(b, new BlockPos(x, y, z));
					continue;
				}
				if (y >= max_final_y ){
					y = min_final_y;
					x++;
				}

				if(isAir) continue;
				if (x >= max_final_x) {
					x = min_final_x;
					z++;
				}

				if (z >= max_final_z) {
					System.out.println("Limit did not work");
					break;
				}


			}
			blockOLD.copy(block.getList());
			return block.getList();
		//}
		//return nullReturnValue.getList();

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
