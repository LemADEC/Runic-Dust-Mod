/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import dustmod.DustMod;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REWall extends RuneEvent {
	public static final int ticksperblock = 7;
	
	@Override
	public void initGraphics(EntityRune e) {
		super.initGraphics(e);
		
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		entityRune.setIgnoreRune(true);
		ItemStack[] req = this.sacrifice(entityRune, new ItemStack[] { new ItemStack(Blocks.iron_ore, 1) });
		
		if (req[0].stackSize != 0 || !this.takeXP(entityRune, 3)) {
			entityRune.fizzle();
			return;
		}
		
		entityRune.data[0] = entityRune.runeRotation % 2;
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		if (entityRune.ticksExisted % ticksperblock == 0) {
			World world = entityRune.worldObj;
			int currentHeight = entityRune.ticksExisted / ticksperblock;
			int x = entityRune.getX();
			int y = entityRune.getY();
			int z = entityRune.getZ();
			boolean dir = entityRune.data[0] == 0;
			int width = 2;
			int height = 8;
			
			for (int t = -height; t <= height + 1; t++) {
				for (int w = -width; w <= width; w++) {
					if (y - t + currentHeight <= 0) {
						entityRune.fade();
						return;
					}
					
					int m = world.getBlockMetadata(x + (dir ? w : 0), y - t + currentHeight, z + (dir ? 0 : w));
					Block B = world.getBlock(x + (dir ? w : 0), y - t + currentHeight, z + (dir ? 0 : w));
					Block nB = world.getBlock(x + (dir ? w : 0), y - t + currentHeight + 1, z + (dir ? 0 : w));
					
					if (B == DustMod.dust) {
						B = Blocks.air;
					} else if (nB == DustMod.dust) {
						nB = Blocks.air;
					}
					
					if (B instanceof BlockContainer || nB instanceof BlockContainer) {
						entityRune.fade();
						return;
					}
					
					world.setBlock(x + (dir ? w : 0), y - t + currentHeight + 1, z + (dir ? 0 : w), B, m, 3);
					world.setBlockToAir(x + (dir ? w : 0), y - t + currentHeight, z + (dir ? 0 : w));
				}
			}
			
			if (currentHeight > 4) {
				entityRune.fade();
			}
		}
	}
}
