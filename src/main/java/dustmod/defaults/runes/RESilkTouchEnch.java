/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RESilkTouchEnch extends RuneEvent {
	public RESilkTouchEnch() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		entityRune.setRenderStar(true);
		entityRune.setRenderBeam(true);
		entityRune.setColorStarOuter(0, 0, 255);
		entityRune.setColorBeam(0, 0, 255);
		
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		List<EntityItem> sacrifice = getItems(entityRune, 1.0D);
		Item item = null;
		
		for (EntityItem i : sacrifice) {
			ItemStack is = i.getEntityItem();
			
			if (is.getItem() == Items.diamond_pickaxe || is.getItem() == Items.diamond_shovel) {
				item = is.getItem();
				//                i.setDead();
				break;
			}
		}
		
		if (item == null) {
			entityRune.fizzle();
			return;
		}
		
		ItemStack[] req = this.sacrifice(entityRune, new ItemStack[] { new ItemStack(item, 1, 0), new ItemStack(Blocks.gold_block, 1, 0) });
		
		if (!checkSacrifice(req) || !takeXP(entityRune, 10)) {
			entityRune.fizzle();
			return;
		}
		
		entityRune.setRenderStar(true);
		entityRune.setRenderBeam(true);
		entityRune.setColorStarOuter(0, 0, 255);
		entityRune.setColorBeam(0, 0, 255);
		entityRune.data[1] = Item.getIdFromItem(item); //the sacrifice entity id will be set to data
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		entityRune.setStarScale(entityRune.getStarScale() + 0.001F);
		
		if (entityRune.ticksExisted > 20) {
			ItemStack create = new ItemStack(Item.getItemById(entityRune.data[1]), 1, 0);
			create.addEnchantment(Enchantment.silkTouch, 1);
			EntityItem entityItem = new EntityItem(entityRune.worldObj, entityRune.posX, entityRune.posY - entityRune.yOffset, entityRune.posZ, create);
			
			entityItem.setPosition(entityRune.posX, entityRune.posY, entityRune.posZ);
			entityRune.worldObj.spawnEntityInWorld(entityItem);
			
			entityRune.fade();
		}
	}
}
