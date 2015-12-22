/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import dustmod.blocks.TileEntityDust;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REHideout extends RuneEvent {
	public static final int thick = 2;
	
	public REHideout() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		entityRune.setRenderStar(true);
		entityRune.setColorStar(255, 255, 255);
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		super.onInit(entityRune);
		
		if (!takeHunger(entityRune, 4)) {
			entityRune.fizzle();
			return;
		}
		
		entityRune.setRenderStar(true);
		entityRune.setColorStar(255, 255, 255);
		int x = entityRune.getX();
		int y = entityRune.getY();
		int z = entityRune.getZ();
		World world = entityRune.worldObj;
		
		int r = 1;
		int h = 3;
		
		Block block = world.getBlock(x, y - h - thick - 1, z);
		
		if (world.isAirBlock(x, y - thick - 1, z)) {
			doCheck(entityRune);
			world.setBlock(x, y - h - thick - 1, z, Blocks.cobblestone, 0, 0);
			world.setBlock(x, y - h - thick, z, Blocks.torch, 0, 0);
			return;
		}
		
		switch (entityRune.dustID) {
		case 100:
			r = 1;
			h = 3;
			break;
		case 200:
			r = 2;
			h = 3;
			break;
		case 300:
			r = 2;
			h = 5;
			break;
		case 400:
			r = 4;
			h = 6;
			break;
		default:
			entityRune.fizzle();
			return;
		}
		for (int i = -r; i <= r; i++) {
			for (int k = -r; k <= r; k++) {
				for (int j = -thick; j >= -h - thick; j--) {
					if (j == -thick) {
						Block above = world.getBlock(x + i, y + j + 1, z + k);
						
						if (above != null && above instanceof BlockSand) {
							world.setBlock(x + i, y + j, z + k, Blocks.sandstone, 0, 3);
						}
					} else if (canBreakBlock(entityRune, x + i, y + j, z + k)) {
						world.setBlockToAir(x + i, y + j, z + k);
					}
				}
			}
		}
		
		if (block != null && !(block instanceof BlockLiquid)) {
			world.setBlock(x, y - h - thick - 1, z, Blocks.cobblestone, 0, 0);
			world.setBlock(x, y - h - thick, z, Blocks.torch, 0, 0);
		}
		
		doCheck(entityRune);
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		super.onTick(entityRune);
		
		if (entityRune.ticksExisted % 10 == 0) {
			yCheck(entityRune);
		}
		
		List<Entity> ents;
		
		if (entityRune.ram <= 0) {
			entityRune.setColorStar(255, 255, 255);
			ents = this.getEntities(entityRune, 0.2D);
			
			for (Entity ei : ents) {
				if (ei instanceof EntityPlayer) {
					EntityPlayer ep = (EntityPlayer) ei;
					entityRune.ram = 45;
					ep.setPositionAndUpdate(entityRune.getX() + 0.5D, entityRune.data[0] + 1 / +0.5D, entityRune.getZ() + 0.5D);
					ep.fallDistance = 0;
				}
			}
			
			ents = this.getEntities(entityRune.worldObj, entityRune.getX(), entityRune.data[0] + 2, entityRune.getZ(), 0.5D);
			
			for (Entity ei : ents) {
				if (ei instanceof EntityPlayer && ei.isSneaking()) {
					EntityPlayer ep = (EntityPlayer) ei;
					ep.setPositionAndUpdate(entityRune.getX() + 0.5D, entityRune.getY() /*+ 0 + ei.yOffset*/+ 0.5D, entityRune.getZ() + 0.5D);
					ep.fallDistance = 0;
					entityRune.ram = 45;
				}
			}
		} else {
			entityRune.setColorStar(255, 255, 0);
			entityRune.ram--;
		}
	}
	
	public boolean canBreakBlock(EntityRune entityRune, int x, int y, int z) {
		
		if (!entityRune.canAlterBlock(x, y, z))
			return false;
		
		Block b = entityRune.worldObj.getBlock(x, y, z);
		if (b.getMaterial() == Material.air)
			return false;
		
		if (b.getBlockHardness(entityRune.worldObj, x, y, z) >= Blocks.obsidian.getBlockHardness(entityRune.worldObj, x, y, z)) {
			return false;
		} else if (b == Blocks.bedrock) {
			return false;
		}
		return true;
	}
	
	private void yCheck(EntityRune entityRune) {
		int x = entityRune.getX();
		int y = entityRune.data[0];
		int z = entityRune.getZ();
		World w = entityRune.worldObj;
		Block b1 = w.getBlock(x, y, z);
		Block b2 = w.getBlock(x, y + 1, z);
		
		if (!b1.isOpaqueCube() || b1.getMaterial() == Material.air) {
			doCheck(entityRune);
		} else if (b2.isOpaqueCube()) {
			doCheck(entityRune);
		}
	}
	
	private void doCheck(EntityRune e) {
		int y;
		
		for (y = e.getY() - 1 - thick; y > 3 && y > e.getY() - 1 - thick - 64; y--) {
			Block block = e.worldObj.getBlock(e.getX(), y, e.getZ());
			
			if (block.isOpaqueCube()) {
				break;
			}
		}
		
		e.data[0] = y;
	}
	
	@Override
	public void onRightClick(EntityRune entityRune, TileEntityDust tileEntityDust, EntityPlayer entityPlayer) {
		super.onRightClick(entityRune, tileEntityDust, entityPlayer);
	}
	
	@Override
	public void onUnload(EntityRune entityRune) {
		super.onUnload(entityRune);
	}
}
