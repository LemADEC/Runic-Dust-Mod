/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REFireBowEnch extends RuneEvent {
	public REFireBowEnch() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune e) {
		super.initGraphics(e);
		
		e.setRenderStar(true);
		e.setRenderBeam(true);
		e.setColorStarOuter(0, 0, 255);
		e.setColorBeam(0, 0, 255);
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		//        List<EntityItem> sacrifice = getSacrifice(e);
		//        int item = Item.bow.itemID;
		//        for(EntityItem i:sacrifice){
		//            ItemStack is = i.item;
		//
		//            if(is.itemID == Item.pickaxeDiamond.itemID || is.itemID == Item.shovelDiamond.itemID) {
		//                item = is.itemID;
		//                break;
		//            }
		//        }
		//        int gold = ((item == Item.pickaxeDiamond.itemID) ? Item.pickaxeGold.itemID:Item.shovelGold.itemID);
		ItemStack[] req = this.sacrifice(entityRune, new ItemStack[] { new ItemStack(Items.bow, 1, 0), new ItemStack(Blocks.gold_block, 1, 0), new ItemStack(Items.fire_charge, 9) });
		
		if (!checkSacrifice(req) || !takeXP(entityRune, 30)) {
			entityRune.fizzle();
			return;
		}
		
		entityRune.setRenderStar(true);
		entityRune.setRenderBeam(true);
		entityRune.setColorStarOuter(0, 0, 255);
		entityRune.setColorBeam(0, 0, 255);
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		entityRune.setStarScale(entityRune.getStarScale() + 0.001F);
		
		if (entityRune.ticksExisted > 20) {
			ItemStack create = new ItemStack(Items.bow, 1, 0);
			create.addEnchantment(Enchantment.flame, 1);
			Entity entityItem = new EntityItem(entityRune.worldObj, entityRune.posX, entityRune.posY - entityRune.yOffset, entityRune.posZ, create);
			
			entityItem.setPosition(entityRune.posX, entityRune.posY, entityRune.posZ);
			entityRune.worldObj.spawnEntityInWorld(entityItem);
			
			entityRune.fade();
		}
	}
}
