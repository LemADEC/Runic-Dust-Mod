/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import dustmod.blocks.TileEntityDust;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REXPStore extends RuneEvent {
	public REXPStore() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune e) {
		super.initGraphics(e);
		
		e.setRenderStar(true);
		e.setColorStarInner(0, 255, 0);
		e.setColorStarOuter(0, 255, 0);
		
	}
	
	@Override
	public void onInit(EntityRune e) {
		ItemStack[] req = new ItemStack[] { new ItemStack(Items.iron_ingot, 16, -1) };
		sacrifice(e, req);
		
		if (req[0].stackSize > 0 || (!this.takeXP(e, 6))) {
			e.fizzle();
			return;
		}
		
		e.setRenderStar(true);
		e.setColorStarInner(0, 255, 0);
		e.setColorStarOuter(0, 255, 0);
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		List<Entity> entities = this.getEntitiesExcluding(entityRune, 1.0D);
		
		if (entityRune.bb != -1) {
			entityRune.data[1] = entityRune.bb;
			entityRune.bb = -1;
		}
		
		if (entityRune.ram > 0) {
			entityRune.setColorStarInner(255, 255, 0);
			entityRune.setColorStarOuter(255, 255, 0);
		} else {
			entityRune.setColorStarInner(0, 255, 0);
			entityRune.setColorStarOuter(0, 255, 0);
		}
		
		for (Entity i : entities) {
			if (i instanceof EntityPlayer && entityRune.ram <= 0) {
				EntityPlayer p = (EntityPlayer) i;
				
				if (p.getGameProfile().getId().equals(entityRune.getSummonerId())) {
					
					if (p.experience > 0) {
						p.addExperience(-1);
						entityRune.data[1]++;
					} else if (p.experienceLevel > 0) {
						p.addExperienceLevel(-1);
						entityRune.data[0]++;
					}
				}
			} else if (i instanceof EntityXPOrb) {
				entityRune.data[1] += ((EntityXPOrb) i).getXpValue();
				i.setDead();
			}
		}
		
		entities = this.getEntitiesExcluding(entityRune, 4D);
		
		for (Entity entity : entities) {
			if (entity instanceof EntityXPOrb) {
				entityRune.data[1] += ((EntityXPOrb) entity).getXpValue();
				entity.setDead();
			}
		}
		
		if (entityRune.ram > 0) {
			entityRune.ram--;
		}
	}
	
	@Override
	public void onRightClick(EntityRune entityRune, TileEntityDust tileEntityDust, EntityPlayer entityPlayer) {
		super.onRightClick(entityRune, tileEntityDust, entityPlayer);
		if (entityPlayer.getGameProfile().getId().equals(entityRune.getSummonerId())) {
			entityRune.ram = 100;
			drop(entityRune);
		}
	}
	
	@Override
	public void onUnload(EntityRune entityRune) {
		drop(entityRune);
		super.onUnload(entityRune);
	}
	
	public void drop(EntityRune e) {
		Entity i = e.worldObj.getClosestPlayerToEntity(e, 12D);
		
		if (i instanceof EntityPlayer) {
			EntityPlayer p = (EntityPlayer) i;
			p.addExperienceLevel(e.data[0]);
			e.data[0] = 0;
			p.addExperience(e.data[1]);
			e.data[1] = 0;
		}
	}
}
