/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RELightning extends RETrap {
	
	@Override
	public void onInit(EntityRune entityRune) {
		super.onInit(entityRune);
		ItemStack[] sac = new ItemStack[] { new ItemStack(Items.iron_ingot, 3) };
		sac = this.sacrifice(entityRune, sac);
		
		if (sac[0].stackSize > 0) {
			entityRune.fizzle();
			return;
		}
	}
	
	public RELightning() {
		super();
	}
	
	@Override
	public void trigger(EntityRune entityRune, int level) {
		List<Entity> entities = getEntities(entityRune, 2D * level / 100);
		
		for (Entity entity : entities) {
			if (entity instanceof EntityLiving && entityRune.getDistanceToEntity(entity) < 2D * level / 100) {
				entityRune.worldObj.addWeatherEffect(new EntityLightningBolt(entityRune.worldObj, entityRune.posX, entityRune.posY - entityRune.yOffset, entityRune.posZ));
				entityRune.fade();
			}
		}
	}
}
