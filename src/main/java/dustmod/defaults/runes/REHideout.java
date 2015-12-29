/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
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
		entityRune.setColorStarInner(255, 200, 200);
		entityRune.setColorStarOuter(200, 255, 255);
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		super.onInit(entityRune);
		
		if (!takeHunger(entityRune, 4)) {
			entityRune.fizzle();
			return;
		}
		
		entityRune.setRenderStar(true);
		entityRune.setColorStarInner(255, 200, 00);
		entityRune.setColorStarOuter(200, 255, 255);
		int x = entityRune.getX();
		int y = entityRune.getY();
		int z = entityRune.getZ();
		World world = entityRune.worldObj;
		
		int radius = 1;
		int height = 3;
		boolean groundSafety = false;
		boolean wallSafety = false;
		boolean roofSafety = false;
		
		switch (entityRune.dustID) {
		case 100:
			radius = 1;
			height = 3;
			break;
			
		case 200:
			radius = 2;
			height = 3;
			groundSafety = true;
			break;
			
		case 300:
			radius = 2;
			height = 4;
			groundSafety = true;
			roofSafety = true;
			break;
			
		case 400:
			radius = 3;
			height = 5;
			groundSafety = true;
			roofSafety = true;
			wallSafety = true;
			break;
			
		default:
			entityRune.fizzle();
			return;
		}
		
		// Do not carve a cave if the hole is already there
		if ( world.isAirBlock(x, y - height - thick + 1, z)
		  && world.isAirBlock(x, y - height - thick + 2, z)
		  && world.getBlock(x, y - height - thick, z) instanceof BlockTorch) {
			if ( (!groundSafety)
			  || world.getBlock(x, y - height - thick - 1, z).isSideSolid(world, x, y, z, ForgeDirection.UP) ) {
				updateDestination(entityRune);
				return;
			}
		}
		
		// Check if player can carve the cave
		boolean canCarveCave = true;
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				for (int dy = -thick - 1; dy >= -height - thick; dy--) {
					if (!entityRune.canBreakBlockAnd_AirOrLiquidOrNotReinforced(x + dx, y + dy, z + dz)) {
						canCarveCave = false;
					}
				}
			}
		}
		if (!canCarveCave) {
			entityRune.fizzle();
			return;
		}
		
		// actually carve the cave
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				// carve inner room, do not drop items
				for (int dy = -thick - 1; dy >= -height - thick; dy--) {
					world.setBlockToAir(x + dx, y + dy, z + dz);
				}
				
				// add safeties
				if (roofSafety) {
					int yRoof = y - thick;
					Block roof = world.getBlock(x + dx, yRoof, z + dz);
					if (roof != null && entityRune.isPlayerAllowedToBreakBlock(x, y, z)) {
						if (roof instanceof BlockSand) {
							world.setBlock(x + dx, yRoof, z + dz, Blocks.sandstone, 0, 3);
						} else if (roof instanceof BlockGravel) {
							world.setBlock(x + dx, yRoof, z + dz, Blocks.cobblestone, 0, 3);
						} else if (roof instanceof BlockFalling || roof instanceof BlockLiquid) {
							world.setBlock(x + dx, yRoof, z + dz, Blocks.fence, 0, 3);
						}
					}
				}
				
				if (groundSafety) {
					int yGround = y - thick - height - 1;
					Block ground = world.getBlock(x + dx, yGround, z + dz);
					if (entityRune.isPlayerAllowedToBreakBlock(x + dx, yGround, z + dz)) {
						if (ground == null || ground.isAir(world, x + dx, yGround, z + dz)) {
							world.setBlock(x + dx, yGround, z + dz, Blocks.cobblestone, 0, 3);
						} else if (ground instanceof BlockLiquid) {
							world.setBlock(x + dx, yGround, z + dz, Blocks.fence, 0, 3);
						}
					}
				}
			}
		}
		if (wallSafety) {
			for (int dx = -radius - 1; dx <= radius + 1; dx++) {
				for (int dz = -radius - 1; dz <= radius + 1; dz++) {
					if (dx != -radius - 1 && dx != radius + 1 && dz != -radius - 1 && dz != radius + 1) {// only walls
						continue;
					}
					for (int dy = -thick; dy >= -height - thick; dy--) {
						Block wall = world.getBlock(x + dx, y + dy, z + dz);
						if (wall instanceof BlockLiquid) {
							if (entityRune.isPlayerAllowedToBreakBlock(x + dx, y + dy, z + dz)) {
								world.setBlock(x + dx, y + dy, z + dz, Blocks.fence, 0, 3);
							}
						}
					}
				}
			}
		}
		
		// add a torch on the center for light and reference
		Block block = world.getBlock(x, y - height - thick - 1, z);
		if ( block != null && !(block instanceof BlockLiquid) && entityRune.isPlayerAllowedToBreakBlock(x, y - height - thick - 1, z) && block.isAir(world, x, y - height - thick - 1, z)) {
			world.setBlock(x, y - height - thick - 1, z, Blocks.cobblestone, 0, 0);
		}
		if (Blocks.torch.canPlaceBlockAt(world, x, y - height - thick, z)) {
			world.setBlock(x, y - height - thick, z, Blocks.torch, 0, 0);
		}
		
		updateDestination(entityRune);
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		super.onTick(entityRune);
		
		if (entityRune.ram <= 0) {
			entityRune.setColorStarInner(255, 255, 150);
			entityRune.setColorStarOuter(150, 255, 255);
			if (entityRune.ticksExisted % 10 != 0) {
				return;
			}
			boolean isUpdated = false;
			
			// entering down
			List<Entity> entities = getEntitiesExcluding(entityRune, 0.2D);
			for (Entity entity : entities) {
				if (entity instanceof EntityPlayer) {
					entityRune.ram = 45;
					if (!isUpdated) {
						isUpdated = true;
						checkAndUpdateDestination(entityRune);
					}
					EntityPlayer entityPlayer = (EntityPlayer) entity;
					entityPlayer.setPositionAndUpdate(entityRune.getX() + 0.5D, entityRune.data[0] + 1.5D, entityRune.getZ() + 0.5D);
					entityPlayer.fallDistance = 0;
				}
			}
			
			// exiting up
			if (!isUpdated) {
				isUpdated = true;
				checkAndUpdateDestination(entityRune);
			}
			entities = getEntitiesExcluding(entityRune, entityRune.worldObj, entityRune.getX(), entityRune.data[0] + 2, entityRune.getZ(), 0.5D);
			for (Entity entity : entities) {
				if (entity instanceof EntityPlayer && entity.isSneaking()) {
					entityRune.ram = 45;
					EntityPlayer entityPlayer = (EntityPlayer) entity;
					entityPlayer.setPositionAndUpdate(entityRune.getX() + 0.5D, entityRune.getY() + 0.5D, entityRune.getZ() + 0.5D);
					entityPlayer.fallDistance = 0;
				}
			}
		} else {
			entityRune.setColorStarInner(255, 255,   0);
			entityRune.setColorStarOuter(150, 255, 150);
			entityRune.setColorStar(255, 255, 0);
			entityRune.ram--;
		}
	}
	
	private void checkAndUpdateDestination(EntityRune entityRune) {
		int x = entityRune.getX();
		int y = entityRune.data[0];
		int z = entityRune.getZ();
		World world = entityRune.worldObj;
		Block block1 = world.getBlock(x, y    , z);
		Block block2 = world.getBlock(x, y + 1, z);
		
		if (!block1.isOpaqueCube() || block1.getMaterial() == Material.air || block2.isOpaqueCube()) {
			updateDestination(entityRune);
		}
	}
	
	private void updateDestination(EntityRune entityRune) {
		int y;
		
		for (y = entityRune.getY() - 1 - thick; y > 3 && y > entityRune.getY() - 1 - thick - 64; y--) {
			Block block = entityRune.worldObj.getBlock(entityRune.getX(), y, entityRune.getZ());
			
			if (block.isOpaqueCube()) {
				break;
			}
		}
		
		entityRune.data[0] = y;
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
