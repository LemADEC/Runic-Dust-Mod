/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REBounce extends RuneEvent {
	public REBounce() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
	}
	
	@Override
	public void onInit(EntityRune e) {
		ItemStack[] req = new ItemStack[] { new ItemStack(Items.slime_ball, 4, -1) };
		sacrifice(e, req);
		
		if (req[0].stackSize > 0) {
			e.fizzle();
			return;
		}
		
		//        e.renderFlamesDust = true;
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		List<Entity> entities = this.getEntities(entityRune, 0.35D);
		for (Entity entity : entities) {
			if (entity instanceof EntityLivingBase) {
				EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
				double cons = 0;//0.0784000015258789;
				double yVel = entity.motionY + cons;
				
				// TODO isJumping
				if (!entityLivingBase.onGround /*&& !entityLivingBase.isJumping*/&& yVel < 0.7D) {
					entityLivingBase.setJumping(true);
					entity.addVelocity(0, 1.27D, 0);
					entity.velocityChanged = true;
				}
				if (!entityLivingBase.onGround) {
					entity.fallDistance = 0;
				} else {
					entityLivingBase.setJumping(false);
				}
			}
		}
		entities = this.getEntities(entityRune, 3D);
		
		for (Entity i : entities) {
			if (i instanceof EntityLivingBase) {
				EntityLivingBase el = (EntityLivingBase) i;
				if (!el.onGround) {
					i.fallDistance = 0;
				} else {
					el.setJumping(false);
				}
			}
		}
	}
}
