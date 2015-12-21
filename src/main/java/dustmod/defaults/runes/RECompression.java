/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import dustmod.DustMod;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RECompression extends RuneEvent {
	public RECompression() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		entityRune.setRenderBeam(true);
		entityRune.setRenderStar(true);
		entityRune.setColorStarOuter(0, 0, 255);
		entityRune.setColorBeam(0, 0, 255);
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		if (!this.takeItems(entityRune, new ItemStack(Blocks.iron_block, 1, -1))) {
			entityRune.fizzle();
			return;
		}
		// Can't use negator
		if (this.takeItems(entityRune, new ItemStack(DustMod.getNegator(), 1, -1))) {
			entityRune.fizzle();
			return;
		}
		
		int diamondAmount = 0;
		ItemStack[] req = new ItemStack[] { new ItemStack(Items.coal, 0, 0) };
		
		while (req[0].stackSize == 0) {
			req[0].stackSize = 32;
			req = sacrifice(entityRune, req);
			
			if (req[0].stackSize <= 0) {
				diamondAmount++;
			}
		}
		
		entityRune.data[0] = diamondAmount;
		entityRune.setRenderBeam(true);
		entityRune.setRenderStar(true);
		entityRune.setColorStarOuter(0, 0, 255);
		entityRune.setColorBeam(0, 0, 255);
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		entityRune.setStarScale(entityRune.getStarScale() + 0.001F);
		
		if (entityRune.ticksExisted > 20) {
			int dAmt = entityRune.data[0];
			int stacks = (dAmt) / 64;
			int leftover = dAmt % 64;
			System.out.println("Dropping " + dAmt + " diamonds in " + stacks + " stacks and " + leftover + " items.");
			
			for (int i = 0; i < stacks; i++) {
				Entity entity = null;
				ItemStack create = new ItemStack(Items.diamond, 64, 0);
				entity = new EntityItem(entityRune.worldObj, entityRune.posX, entityRune.posY - EntityRune.yOffset, entityRune.posZ, create);
				
				entity.setPosition(entityRune.posX, entityRune.posY - EntityRune.yOffset, entityRune.posZ);
				entityRune.worldObj.spawnEntityInWorld(entity);
			}
			
			if (leftover > 0) {
				Entity entity = null;
				ItemStack create = new ItemStack(Items.diamond, leftover, 0);
				entity = new EntityItem(entityRune.worldObj, entityRune.posX, entityRune.posY - EntityRune.yOffset, entityRune.posZ, create);
				
				entity.setPosition(entityRune.posX, entityRune.posY - EntityRune.yOffset, entityRune.posZ);
				entityRune.worldObj.spawnEntityInWorld(entity);
			}
			
			entityRune.fade();
		}
	}
}
