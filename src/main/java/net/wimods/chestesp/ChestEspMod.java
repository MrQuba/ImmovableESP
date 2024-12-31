/*
 * Copyright (c) 2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wimods.chestesp;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.GlowItemFrameEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.wimods.chestesp.util.ChunkUtils;
import net.wimods.chestesp.util.RenderUtils;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChestEspMod
{
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("ChestESP");
	
	private final ConfigHolder<ChestEspConfig> configHolder;
	private final ChestEspGroupManager groups;
	private final KeyBinding toggleKey;
	
	private boolean enabled;
	private boolean ValueBetween(BlockPos b){
		final boolean y = b.getY() >= configHolder.get().min_height && b.getY() <= configHolder.get().max_height;
		if(!configHolder.get().enable_area)
			return y;
		final boolean x = b.getX() >= configHolder.get().area_start_x && b.getX() <= configHolder.get().area_end_x;
		final boolean z = b.getZ() >= configHolder.get().area_start_z && b.getZ() <= configHolder.get().area_end_z;
		return  y && x && z;
	}
	public ChestEspMod()
	{
		LOGGER.info("Starting ChestESP...");
		
		configHolder = AutoConfig.register(ChestEspConfig.class,
			GsonConfigSerializer::new);
		
		groups = new ChestEspGroupManager(configHolder);
		
		toggleKey = KeyBindingHelper
			.registerKeyBinding(new KeyBinding("key.chestesp.toggle",
				InputUtil.UNKNOWN_KEY.getCode(), "ChestESP"));
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			boolean enabled = configHolder.get().enable;
			while(toggleKey.wasPressed())
				setEnabled(!enabled);
		});
	}
	public void setEnabled(boolean enabled)
	{
		if(this.enabled == enabled)
			return;
		
		LOGGER.info("{} ChestESP.", enabled ? "Enabling" : "Disabling");
		
		this.enabled = enabled;
		
		if(enabled)
			ChestEspRenderer.prepareBuffers();
		else
		{
			groups.allGroups.forEach(ChestEspGroup::clear);
			ChestEspRenderer.closeBuffers();
		}
		
		if(configHolder.get().enable != enabled)
		{
			configHolder.get().enable = enabled;
			configHolder.save();
		}
	}
	
	public void onUpdate()
	{
		setEnabled(configHolder.get().enable);
		if(!isEnabled())
			return;

		groups.allGroups.forEach(ChestEspGroup::clear);
		if(configHolder.get().include_obsidian || configHolder.get().include_deepslate){
				ChunkUtils.getBlocksList(configHolder).forEach(block -> {
						assert(block != null);
						if (block.first.equals(Blocks.AIR) || block.first.equals(Blocks.CAVE_AIR) || block.first.equals(Blocks.VOID_AIR)) return;
						if(block.second.getY() >= configHolder.get().min_height && block.second.getY() <= configHolder.get().max_height){
							if (block.first.equals(Blocks.OBSIDIAN)) {
								groups.obsidian.add(block);
							}
							if (block.first.equals(Blocks.CRYING_OBSIDIAN)) {
								groups.obsidian.add(block);
							}
							if (block.first.equals(Blocks.REINFORCED_DEEPSLATE)) {
								groups.deepslate.add(block);
							}
						}
				});
		}
		ChunkUtils.getLoadedBlockEntities(configHolder).forEach(blockEntity -> {
			if(ValueBetween(blockEntity.getPos())){
				if(blockEntity instanceof TrappedChestBlockEntity)
					groups.trapChests.add(blockEntity);
				else if(blockEntity instanceof ChestBlockEntity)
					groups.basicChests.add(blockEntity);
				else if(blockEntity instanceof EnderChestBlockEntity)
					groups.enderChests.add(blockEntity);
				else if(blockEntity instanceof ShulkerBoxBlockEntity)
					groups.shulkerBoxes.add(blockEntity);
				else if(blockEntity instanceof BarrelBlockEntity)
					groups.barrels.add(blockEntity);
				else if(blockEntity instanceof HopperBlockEntity)
					groups.hoppers.add(blockEntity);
				else if(blockEntity instanceof DropperBlockEntity)
					groups.droppers.add(blockEntity);
				else if(blockEntity instanceof DispenserBlockEntity)
					groups.dispensers.add(blockEntity);
				else if(blockEntity instanceof SculkCatalystBlockEntity)
					groups.sculk.add(blockEntity);
				else if(blockEntity instanceof SculkSensorBlockEntity)
					groups.sculk.add(blockEntity);
				else if(blockEntity instanceof SculkShriekerBlockEntity)
					groups.sculk.add(blockEntity);
				else if(blockEntity instanceof MobSpawnerBlockEntity)
					groups.spawner.add(blockEntity);
				else if(blockEntity instanceof AbstractFurnaceBlockEntity)
					groups.furnaces.add(blockEntity);
			}
		});

        assert MC.world != null;
        for(Entity entity : MC.world.getEntities())
			if(ValueBetween(BlockPos.ofFloored(entity.getPos()))) {
				if (entity instanceof ChestMinecartEntity)
					groups.chestCarts.add(entity);
				else if (entity instanceof HopperMinecartEntity)
					groups.hopperCarts.add(entity);
				else if (entity instanceof ChestBoatEntity)
					groups.chestBoats.add(entity);
				else if (entity instanceof WardenEntity)
					groups.wardens.add(entity);
				else if (entity instanceof ItemFrameEntity)
					groups.frames.add(entity);
				else if (entity instanceof GlowItemFrameEntity)
					groups.frames.add(entity);
			}
	}
	
	public boolean shouldCancelViewBobbing()
	{
		return configHolder.get().style.hasLines();
	}
	
	public void onRender(MatrixStack matrixStack, float partialTicks)
	{
		// GL settings
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		
		matrixStack.push();
		RenderUtils.applyRegionalRenderOffset(matrixStack);
		
		groups.entityGroups.stream().filter(ChestEspGroup::isEnabled)
			.forEach(g -> g.updateBoxes(partialTicks));
		
		ChestEspRenderer espRenderer = new ChestEspRenderer(matrixStack);
		ChestEspStyle style = configHolder.get().style;
		
		if(style.hasBoxes())
		{
			RenderSystem.setShader(GameRenderer::getPositionProgram);
			groups.allGroups.stream().filter(ChestEspGroup::isEnabled)
				.forEach(espRenderer::renderBoxes);
		}
		
		if(style.hasLines())
		{
			RenderSystem.setShader(GameRenderer::getPositionProgram);
			groups.allGroups.stream().filter(ChestEspGroup::isEnabled)
				.forEach(espRenderer::renderLines);
		}
		
		matrixStack.pop();
		
		// GL resets
		RenderSystem.setShaderColor(1, 1, 1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	
	public static ChestEspMod getInstance()
	{
		return ChestEspModInitializer.getInstance();
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
}
