/*
 * Copyright (c) 2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp;

import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import net.wimods.chestesp.util.BlockUtils;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

import static net.wimods.chestesp.util.ChunkUtils.getBlockPosition;

public final class ChestEspNormalBlockGroup extends ChestEspGroup
{
	public ChestEspNormalBlockGroup(ConfigHolder<ChestEspConfig> configHolder,
                                    ToIntFunction<ChestEspConfig> color, Predicate<ChestEspConfig> enabled)
	{
		super(configHolder, color, enabled);
	}

	public void add(Block b, BlockPos pos)
	{
		Box box = getBox(pos);
		if(box == null)
			return;

		boxes.add(box);
	}

	private Box getBox(BlockPos pos)
	{
		if(!BlockUtils.canBeClicked(pos))
			return null;
		return BlockUtils.getBoundingBox(pos);
	}


}
