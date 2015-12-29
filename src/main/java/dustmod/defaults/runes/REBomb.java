/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REBomb extends RuneEvent {
	public REBomb() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		entityRune.setRenderStar(true);
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		ItemStack[] requiredSacrifice = new ItemStack[] { new ItemStack(Items.gunpowder, 2) };
		requiredSacrifice = sacrifice(entityRune, requiredSacrifice);
		
		if (requiredSacrifice[0].stackSize > 0) {
			entityRune.fizzle();
			return;
		}
		
		int[] center = new int[4];
		int[] fuse = new int[4];
		int[][] dusts = entityRune.dusts;
		center[0] = dusts[2][1];
		center[1] = dusts[3][1];
		center[2] = dusts[2][2];
		center[3] = dusts[3][2];
		fuse[0] = dusts[0][4];
		fuse[1] = dusts[1][4];
		fuse[2] = dusts[1][3];
		fuse[3] = dusts[0][5];
		
		for (int i = 0; i < 4; i++) {
			if (center[0] != center[i]) {
				entityRune.fizzle();
				return;
			}
			
			if (fuse[0] != fuse[i]) {
				entityRune.fizzle();
				return;
			}
		}
		
		int fuseDuration = 0;
		switch (fuse[0]) {
		case 100:
			fuseDuration = 1;
			break;
		case 200:
			fuseDuration = 0 + entityRune.worldObj.rand.nextInt(5);
			break;
		case 300:
			fuseDuration = 85 + entityRune.worldObj.rand.nextInt(10);
			break;
		case 400:
			fuseDuration = 230 + entityRune.worldObj.rand.nextInt(20);
			break;
		default:
			entityRune.fizzle();
			return;
		}
		
		entityRune.data[0] = center[0];
		entityRune.data[1] = fuseDuration;
		entityRune.setRenderStar(true);
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		int center = entityRune.data[0];
		int fuseDuration = entityRune.data[1];
		
		entityRune.setRenderStar(true);
		
		if (entityRune.ticksExisted < fuseDuration) {
			entityRune.setColorStarInner(140, 140, 140);
			entityRune.setColorStarOuter(140, 140, 140);
			return;
		}
		
		entityRune.setColorStarInner(0, 0, 255);
		entityRune.setColorStarOuter(0, 0, 255);
		List<Entity> entities = getEntitiesExcluding(entityRune, 1.0D);
		
		if (entities.size() > 0 || fuseDuration > 1) {
			trigger(entityRune, center);
			entityRune.fade();
		}
	}
	
	public void trigger(EntityRune entityRune, int level) {
		entityRune.worldObj.createExplosion(entityRune, entityRune.posX, entityRune.posY - entityRune.yOffset, entityRune.posZ, (float) (level * level) / 10000 + 2F, true);
	}
}
