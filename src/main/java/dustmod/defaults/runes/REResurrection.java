/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import dustmod.DustMod;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REResurrection extends RuneEvent {
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		entityRune.setRenderStar(true);
		entityRune.setRenderBeam(true);
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		entityRune.setRenderStar(true);
		entityRune.setRenderBeam(true);
		ItemStack[] sac = new ItemStack[] { new ItemStack(Blocks.soul_sand, 4) };
		sac = this.sacrifice(entityRune, sac);
		
		if (!checkSacrifice(sac)) {
			entityRune.fizzle();
			return;
		}
		
		//get sacrifice
		ArrayList<EntityItem> entityItems = new ArrayList<EntityItem>();
		List<Entity> entities = getEntitiesExcluding(entityRune, 1.0D);
		
		for (Entity entity : entities) {
			if (entity instanceof EntityItem) {
				entityItems.add((EntityItem) entity);
			}
		}
		
		if (entityItems.size() == 0) {
			entityRune.kill();
			return;
		}
		
		int entClass = -1;
		
		for (EntityItem entityItem : entityItems) {
			if (entClass != -1) {
				break;
			}
			
			Item item = entityItem.getEntityItem().getItem();
			int metadata = entityItem.getEntityItem().getItemDamage();
			int amount;
			int amt = amount = 2;
			
			for (EntityItem ent : entityItems) {
				if (ent.getEntityItem().getItem() == item && ent.getEntityItem().getItemDamage() == metadata) {
					amount -= ent.getEntityItem().stackSize;
				}
			}
			
			if (amount <= 0 && DustMod.getEntityIDFromDrop(new ItemStack(item, 0, metadata), 0) != -1) {
				for (EntityItem ent : entityItems) {
					if (ent.getEntityItem().getItem() == item && ent.getEntityItem().getItemDamage() == metadata) {
						while (amt > 0 && ent.getEntityItem().stackSize > 0) {
							amt--;
							ItemStack itemStack = ent.getEntityItem();
							itemStack.stackSize--;
							
							if (itemStack.stackSize <= 0) {
								ent.setDead();
							} else {
								ent.setEntityItemStack(itemStack);
							}
						}
					}
				}
				
				entClass = DustMod.getEntityIDFromDrop(new ItemStack(item, 0, metadata), 0);
				
				if (entClass != -1) {
					break;
				}
			}
		}
		
		if (entClass == -1) {
			entityRune.fizzle();
			return;
		}
		
		entityRune.data[0] = (byte) entClass;
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		entityRune.setStarScale(entityRune.getStarScale() + 0.001F);
		
		if (entityRune.ticksExisted > 120) {
			Entity entity = null;
			entity = EntityList.createEntityByID(entityRune.data[0], entityRune.worldObj);
			
			if (entity != null) {
				entity.setPosition(entityRune.posX, entityRune.posY - entityRune.yOffset, entityRune.posZ);
				entityRune.worldObj.spawnEntityInWorld(entity);
			}
			
			entityRune.fade();
		}
	}
}