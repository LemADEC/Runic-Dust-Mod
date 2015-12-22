/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RELillyBridge extends RuneEvent {
	public RELillyBridge() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune e) {
		super.initGraphics(e);
		
		e.setRenderStar(true);
		e.setColorStarOuter(0, 255, 0);
		
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		ItemStack[] req = this.sacrifice(entityRune, new ItemStack[] { new ItemStack(Blocks.leaves, 4, -1) });
		
		if (req[0].stackSize != 0) {
			entityRune.fizzle();
			return;
		}
		
		entityRune.rotationYaw = ((entityRune.runeRotation + 4) % 4) * 90;
		
		entityRune.setRenderStar(true);
		entityRune.setColorStarOuter(0, 255, 0);
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		int period = 20;
		
		if (entityRune.ticksExisted % period == 0) {
			World world = entityRune.worldObj;
			int dist = (entityRune.ticksExisted / period + 1) * 2;
			int y = entityRune.getY() - 1;
			int x = entityRune.getX();
			int z = entityRune.getZ();
			
			if (entityRune.rotationYaw == 90) {
				x -= dist;
			} else if (entityRune.rotationYaw == 270) {
				x += dist;
			} else if (entityRune.rotationYaw == 180) {
				z -= dist;
			} else if (entityRune.rotationYaw == 0) {
				z += dist;
			}
			
			for (int i = -1; i <= 1; i++) {
				if (world.getBlock(x, y + i - 1, z).getMaterial() == Material.water && world.isAirBlock(x, y + i, z)) {
					world.setBlock(x, y + i, z, Blocks.waterlily, 0, 3);
				}
			}
		}
		
		if (entityRune.ticksExisted > 16 * period) {
			entityRune.fade();
		}
	}
}
