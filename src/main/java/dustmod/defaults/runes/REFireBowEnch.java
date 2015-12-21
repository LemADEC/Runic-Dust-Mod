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
	public void onInit(EntityRune e) {
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
		ItemStack[] req = this.sacrifice(e, new ItemStack[] { new ItemStack(Items.bow, 1, 0), new ItemStack(Blocks.gold_block, 1, 0), new ItemStack(Items.fire_charge, 9) });
		
		if (!checkSacrifice(req) || !takeXP(e, 30)) {
			e.fizzle();
			return;
		}
		
		e.setRenderStar(true);
		e.setRenderBeam(true);
		e.setColorStarOuter(0, 0, 255);
		e.setColorBeam(0, 0, 255);
	}
	
	@Override
	public void onTick(EntityRune e) {
		e.setStarScale(e.getStarScale() + 0.001F);
		
		if (e.ticksExisted > 20) {
			Entity en = null;
			ItemStack create = new ItemStack(Items.bow, 1, 0);
			create.addEnchantment(Enchantment.flame, 1);
			en = new EntityItem(e.worldObj, e.posX, e.posY - EntityRune.yOffset, e.posZ, create);
			
			if (en != null) {
				en.setPosition(e.posX, e.posY, e.posZ);
				e.worldObj.spawnEntityInWorld(en);
			}
			
			e.fade();
		}
	}
}
