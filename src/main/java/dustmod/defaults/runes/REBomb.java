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
		ItemStack[] sac = new ItemStack[] { new ItemStack(Items.gunpowder, 2) };
		sac = this.sacrifice(entityRune, sac);
		
		if (sac[0].stackSize > 0) {
			entityRune.fizzle();
			return;
		}
		
		int[] center = new int[4];
		int[] fuse = new int[4];
		int[][] dusts = entityRune.dusts;
		center[0] = dusts[3][1];
		center[1] = dusts[4][1];
		center[2] = dusts[3][2];
		center[3] = dusts[4][2];
		fuse[0] = dusts[0][4];
		fuse[1] = dusts[1][4];
		fuse[2] = dusts[1][3];
		fuse[3] = dusts[2][3];
		
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
		
		int c = center[0];
		int f = fuse[0];
		entityRune.data[0] = c;
		entityRune.data[1] = f;
		entityRune.setRenderStar(true);
	}
	
	@Override
	public void onTick(EntityRune e) {
		int f = getTime(e.data[1]);
		int c = e.data[0];
		e.setRenderStar(true);
		
		if (e.ticksExisted < f * 30) {
			e.setColorStarInner(140, 140, 140);
			e.setColorStarOuter(140, 140, 140);
			return;
		}
		
		e.setColorStarInner(0, 0, 255);
		e.setColorStarOuter(0, 0, 255);
		List<Entity> entities = getEntities(e);
		
		if (entities.size() > 0 || f > 1) {
			trigger(e, c);
			e.fade();
		}
	}
	
	public int getTime(int f) {
		switch (f) {
		case 100:
			return 1;
		case 200:
			return 2;
		case 300:
			return 3;
		case 400:
			return 4;
		default:
			return 1;
		}
	}
	
	public void trigger(EntityRune entityRune, int level) {
		entityRune.worldObj.createExplosion(entityRune, entityRune.posX, entityRune.posY - entityRune.yOffset, entityRune.posZ, (float) (level * level) / 10000 + 2F, true);
	}
}
