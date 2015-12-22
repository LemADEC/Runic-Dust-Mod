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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import dustmod.DustMod;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RESpiritTool extends RuneEvent {
	
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
		entityRune.setRenderStar(true);
		entityRune.setRenderBeam(true);
		entityRune.setColorStarOuter(0, 0, 255);
		entityRune.setColorBeam(0, 0, 255);
		
		ItemStack[] req = new ItemStack[] { new ItemStack(Items.golden_pickaxe, 1) };
		req = this.sacrifice(entityRune, req);
		
		if (!checkSacrifice(req)) {
			//            System.out.println("check1");
			req = new ItemStack[] { new ItemStack(Items.golden_sword, 1), new ItemStack(Blocks.glowstone, 1) };
			req = this.sacrifice(entityRune, req);
			
			if (!checkSacrifice(req)) {
				//                System.out.println("check2");
				entityRune.fizzle();
				return;
			} else {
				//                System.out.println("check3");
				entityRune.data[0] = 2;
			}
		} else {
			req = new ItemStack[] { new ItemStack(Blocks.tnt, 4) };
			req = this.sacrifice(entityRune, req);
			
			if (!checkSacrifice(req)) {
				//                System.out.println("check4");
				entityRune.fizzle();
				return;
			}
			
			//            System.out.println("check5");
			entityRune.data[0] = 1;
		}
		
		if (!this.takeXP(entityRune, 18)) {
			entityRune.fizzle();
			return;
		}
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		entityRune.setStarScale(entityRune.getStarScale() + 0.001F);
		
		if (entityRune.ticksExisted > 20) {
			Item item = null;
			
			if (entityRune.data[0] == 1) {
				item = DustMod.spiritPickaxe;
			} else if (entityRune.data[0] == 2) {
				item = DustMod.spiritSword;
			}
			
			ItemStack create = new ItemStack(item, 1, 0);
			
			if (entityRune.data[0] == 2) {
				create.addEnchantment(Enchantment.knockback, 10);
				create.addEnchantment(Enchantment.smite, 5);
			}
			
			Entity en = new EntityItem(entityRune.worldObj, entityRune.posX, entityRune.posY - entityRune.yOffset, entityRune.posZ, create);
			
			en.setPosition(entityRune.posX, entityRune.posY - entityRune.yOffset, entityRune.posZ);
			entityRune.worldObj.spawnEntityInWorld(en);
			
			entityRune.fade();
		}
	}
}