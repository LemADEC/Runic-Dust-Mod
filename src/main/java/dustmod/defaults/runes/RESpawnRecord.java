/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import dustmod.DustMod;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RESpawnRecord extends RuneEvent {
	public RESpawnRecord() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		entityRune.setRenderStar(true);
		entityRune.setRenderBeam(true);
		entityRune.setColorStarOuter(0, 255, 0);
		entityRune.setColorBeam(0, 255, 0);
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		entityRune.setRenderStar(true);
		entityRune.setRenderBeam(true);
		entityRune.setColorStarOuter(0, 255, 0);
		entityRune.setColorBeam(0, 255, 0);
		ItemStack[] sacrifice = new ItemStack[] { new ItemStack(Items.diamond, 1) };
		this.sacrifice(entityRune, sacrifice);
		
		if (sacrifice[0].stackSize > 0) {
			entityRune.fizzle();
			return;
		}
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		entityRune.setStarScale(entityRune.getStarScale() + 0.001F);
		
		if (entityRune.ticksExisted > 120) {
			try {
				
				Field recordMapField = ItemRecord.class.getDeclaredField("field_150928_b");
				recordMapField.setAccessible(true);
				Map<String, ItemRecord> recordMap = (Map<String, ItemRecord>) recordMapField.get(null);
				Object[] recordList = recordMap.values().toArray();
				int recordNr = entityRune.worldObj.rand.nextInt(recordList.length);
				
				EntityItem en = new EntityItem(entityRune.worldObj, entityRune.posX, entityRune.posY - entityRune.yOffset - 1, entityRune.posZ, new ItemStack((Item) recordList[recordNr], 1, 0));
				entityRune.worldObj.spawnEntityInWorld(en);
				
			} catch (NoSuchFieldException e1) {
				DustMod.logger.catching(e1);
			} catch (SecurityException e1) {
				DustMod.logger.catching(e1);
			} catch (IllegalArgumentException e1) {
				DustMod.logger.catching(e1);
			} catch (IllegalAccessException e1) {
				DustMod.logger.catching(e1);
			}
			
			entityRune.fade();
			
		}
	}
}
