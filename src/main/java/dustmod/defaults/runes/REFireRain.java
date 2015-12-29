/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import dustmod.runes.EntityRune;
import dustmod.runes.PoweredEvent;

/**
 *
 * @author billythegoat101
 */
public class REFireRain extends PoweredEvent {
	public REFireRain() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		entityRune.setRenderBeam(true);
		entityRune.setColorStarOuter(255, 0, 0);
		entityRune.setColorBeam(255, 0, 0);
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		super.onInit(entityRune);
		entityRune.setRenderBeam(true);
		entityRune.setColorBeam(255, 0, 0);
		ItemStack[] req = new ItemStack[] { new ItemStack(Items.blaze_rod, 2) };
		req = this.sacrifice(entityRune, req);
		
		if (!checkSacrifice(req)) {
			entityRune.fizzle();
			return;
		}
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		super.onTick(entityRune);
		
		int radius = 100;
		int amount = 20;
		
		for (int i = 0; i < amount && entityRune.ticksExisted % 5 == 0; i++) {
			EntityArrow entityArrow = new EntityArrow(entityRune.worldObj, entityRune.posX + (Math.random() * 2 - 1) * radius, 158, entityRune.posZ + (Math.random() * 2 - 1) * radius);
			entityArrow.motionX = 0;
			entityArrow.motionY = -2D;
			entityArrow.motionZ = 0;
			entityArrow.setFire(100);
			entityArrow.canBePickedUp = 0;
			entityRune.worldObj.spawnEntityInWorld(entityArrow);
		}
	}
	
	@Override
	public int getStartFuel() {
		return dayLength / 2;
	}
	
	@Override
	public int getMaxFuel() {
		return dayLength * 3;
	}
	
	@Override
	public int getStableFuelAmount(EntityRune entityRune) {
		return dayLength / 2;
	}
	
	@Override
	public boolean isPaused(EntityRune entityRune) {
		return false;
	}
}
