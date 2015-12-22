/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;
import dustmod.runes.Sacrifice;

/**
 *
 * @author billythegoat101
 */
public class REEggifier extends RuneEvent {
	public REEggifier() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		entityRune.setRenderStar(true);
		
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		super.onInit(entityRune);
		ItemStack[] req = new ItemStack[] { new ItemStack(Items.egg, 1) };
		req = this.sacrifice(entityRune, req);
		
		if (!checkSacrifice(req) || !takeXP(entityRune, 5)) {
			entityRune.fizzle();
			return;
		}
		
		entityRune.setRenderStar(true);
		entityRune.setColorStar(255, 2555, 255);
		entityRune.sacrificeWaiting = 600;
		
		for (Object o : EntityList.IDtoClassMapping.keySet()) {
			int i = (Integer) o;
			this.addSacrificeList(new Sacrifice(i));
		}
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		super.onTick(entityRune);
		entityRune.setStarScale(entityRune.getStarScale() + 0.001F);
		
		if (entityRune.ticksExisted > 40 && !EntityList.entityEggs.containsKey(entityRune.data[15])) {
			entityRune.fizzle();
			return;
		} else if (entityRune.ticksExisted > 120) {
			EntityItem entityItem = new EntityItem(entityRune.worldObj, entityRune.posX, entityRune.posY, entityRune.posZ, new ItemStack(Items.spawn_egg, 1, entityRune.data[15]));
			
			entityItem.setPosition(entityRune.posX, entityRune.posY - entityRune.yOffset, entityRune.posZ);
			entityRune.worldObj.spawnEntityInWorld(entityItem);
			
			entityRune.fade();
		}
	}
}
