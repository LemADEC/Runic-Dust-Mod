/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import dustmod.blocks.TileEntityDust;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RECage extends RETrap {
	public RECage() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		entityRune.setRenderStar(true);
		
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		entityRune.setRenderStar(true);
		ItemStack[] req = new ItemStack[] { new ItemStack(Items.iron_ingot, 6), new ItemStack(Items.redstone, 2) };
		req = this.sacrifice(entityRune, req);
		
		if (!checkSacrifice(req)) {
			entityRune.fizzle();
			return;
		}
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		entityRune.setRenderStar(true);
		
		if (entityRune.ticksExisted < 80) {
			entityRune.setColorStarInner(140, 140, 140);
			entityRune.setColorStarOuter(140, 140, 140);
			return;
		}
		
		entityRune.setColorStarInner(0, 0, 255);
		entityRune.setColorStarOuter(0, 0, 255);
		List<Entity> entities = getEntitiesExcluding(entityRune, 2D);
		
		if (entities.size() > 0) {
			trigger(entityRune, entityRune.dustID);
			//            e.fade();
		}
	}
	
	@Override
	public void trigger(EntityRune entityRune, int dustLevel) {
		boolean found = false;
		List<Entity> trap = getEntitiesExcluding(entityRune, 2D);
		
		for (Entity k : trap) {
			if (k instanceof EntityLiving) {
				
				if (k instanceof EntityPlayer && entityRune.getSummonerId() != null) {
					EntityPlayer ep = (EntityPlayer) k;
					if (ep.getGameProfile().getId().equals(entityRune.getSummonerId())) {
						continue;
					}
				}
				found = true;
				EntityLiving el = (EntityLiving) k;
				int x = (int) Math.floor(el.posX);
				int y = (int) Math.floor(el.posY - el.yOffset);
				int z = (int) Math.floor(el.posZ);
				el.setPosition(x + 0.5D, (double) y + el.yOffset, z + 0.5D);
				World world = entityRune.worldObj;
				
				for (int ix = -1; ix <= 1; ix++)
					for (int iy = 0; iy <= 1; iy++)
						for (int iz = -1; iz <= 1; iz++) {
							if (ix == 0 && iz == 0) {
								continue;
							}
							
							world.setBlockToAir(x + ix, y + iy, z + iz);
							world.setBlock(x + ix, y + iy, z + iz, Blocks.iron_bars, 0, 3);
						}
				
				if (world.isAirBlock(x, y - 1, z)) {
					for (Integer[] p : entityRune.dustPoints) {
						TileEntityDust ted = (TileEntityDust) world.getTileEntity(p[0], p[1], p[2]);
						
						if (ted.getDusts()[3]) {
							Block block = world.getBlock(p[0], p[1] - 1, p[2]);
							world.setBlockToAir(p[0], p[1] - 1, p[2]);
							world.setBlock(x, y - 1, z, block, 0, 3);
						}
					}
				}
				
				break;
			}
		}
		
		if (found) {
			entityRune.fade();
		}
	}
}
