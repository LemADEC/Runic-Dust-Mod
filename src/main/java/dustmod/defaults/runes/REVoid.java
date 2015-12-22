/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REVoid extends RuneEvent {
	public REVoid() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		entityRune.setRenderStar(true);
		entityRune.setColorStarInner(255, 0, 255);
		entityRune.setColorStarOuter(255, 0, 255);
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		if (!this.takeXP(entityRune, 3)) {
			entityRune.fizzle();
			return;
		}
		
		entityRune.setRenderStar(true);
		entityRune.setColorStarInner(255, 0, 255);
		entityRune.setColorStarOuter(255, 0, 255);
		List<EntityItem> sacrifice = this.getItems(entityRune);
		
		if (sacrifice == null || sacrifice.isEmpty()) {
			entityRune.setStarScale(1.02F);
			entityRune.data[0] = 1;
		} else {
			for (EntityItem i : sacrifice) {
				VoidStorageManager.addItemToVoidInventory(entityRune, i.getEntityItem());
				i.setDead();
			}
			
			VoidStorageManager.updateVoidInventory();
			entityRune.data[0] = 0;
		}
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		if (entityRune.data[0] == 1) {
			if (entityRune.ticksExisted > 100) {
				entityRune.fade();
				ArrayList<ItemStack> itemStacks = VoidStorageManager.getVoidInventory(entityRune);
				if (itemStacks == null) {
					return;
				}
				for (ItemStack itemStack : itemStacks) {
					Entity entityItem = new EntityItem(entityRune.worldObj, entityRune.posX, entityRune.posY - entityRune.yOffset, entityRune.posZ, itemStack);
					
					entityItem.setPosition(entityRune.posX, entityRune.posY, entityRune.posZ);
					entityRune.worldObj.spawnEntityInWorld(entityItem);
				}
				
				VoidStorageManager.clearVoidInventory(entityRune);
				VoidStorageManager.updateVoidInventory();
			}
		} else {
			if (entityRune.ticksExisted > 35) {
				entityRune.ticksExisted += 3;
				entityRune.setStarScale(entityRune.getStarScale() - 0.001F);
			}
			
			if (entityRune.ticksExisted > 100) {
				entityRune.kill();
			}
		}
	}
}
