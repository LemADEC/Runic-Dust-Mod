/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
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
public class REFortuneEnch extends RuneEvent {
	public REFortuneEnch() {
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
		Item item = Items.diamond_pickaxe;
		
		for (EntityItem i : sacrifice) {
			ItemStack is = i.getEntityItem();
			
			if (is.getItem() == Items.diamond_pickaxe || is.getItem() == Items.diamond_sword) {
				item = is.getItem();
				break;
			}
		}
		
		//        int gold = ((item == Item.pickaxeDiamond.itemID) ? Item.pickaxeGold.itemID:Item.swordGold.itemID);
		ItemStack[] req = this.sacrifice(entityRune, new ItemStack[] { new ItemStack(item, 1, 0), new ItemStack(Blocks.diamond_ore, 1, 0), new ItemStack(Blocks.redstone_ore, 1, 0),
				new ItemStack(Blocks.lapis_ore, 1, 0) });
		
		if (!checkSacrifice(req) || !takeXP(entityRune, 15)) {
			entityRune.fizzle();
			return;
		}
		
		entityRune.setRenderStar(true);
		entityRune.setRenderBeam(true);
		entityRune.setColorStarOuter(0, 0, 255);
		entityRune.setColorBeam(0, 0, 255);
		entityRune.data[0] = Item.getIdFromItem(item);
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		entityRune.setStarScale(entityRune.getStarScale() + 0.001F);
		
		if (entityRune.ticksExisted > 20) {
			Entity entityItem = null;
			ItemStack create = new ItemStack(Item.getItemById(entityRune.data[0]), 1, 0);
			
			if (create.getItem() == Items.diamond_sword) {
				create.addEnchantment(Enchantment.looting, 4);
			}
			
			if (create.getItem() == Items.diamond_pickaxe) {
				create.addEnchantment(Enchantment.fortune, 4);
			}
			
			entityItem = new EntityItem(entityRune.worldObj, entityRune.posX, entityRune.posY - entityRune.yOffset, entityRune.posZ, create);
			
			entityItem.setPosition(entityRune.posX, entityRune.posY, entityRune.posZ);
			entityRune.worldObj.spawnEntityInWorld(entityItem);
			
			entityRune.fade();
		}
	}
}
