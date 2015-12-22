/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import net.minecraft.enchantment.Enchantment;
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
	private static int DATA0_NONE    = 0;
	private static int DATA0_AXE     = 1;
	private static int DATA0_PICKAXE = 2;
	private static int DATA0_SHOVEL  = 3;
	private static int DATA0_SWORD   = 4;
	
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
		
		ItemStack[] req = new ItemStack[] { new ItemStack(Items.golden_axe, 1), new ItemStack(Blocks.coal_block, 1) };
		req = sacrifice(entityRune, req);
		
		entityRune.data[0] = DATA0_NONE;
		if (checkSacrifice(req)) {
			entityRune.data[0] = DATA0_AXE;
			
		} else {
			req = new ItemStack[] { new ItemStack(Items.golden_pickaxe, 1), new ItemStack(Blocks.tnt, 4) };
			req = sacrifice(entityRune, req);
			
			if (checkSacrifice(req)) {
				entityRune.data[0] = DATA0_PICKAXE;
				
			} else {
				req = new ItemStack[] { new ItemStack(Items.golden_shovel, 1), new ItemStack(Blocks.sandstone, 3) };
				req = sacrifice(entityRune, req);
				
				if (checkSacrifice(req)) {
					entityRune.data[0] = DATA0_SHOVEL;
					
				} else {
					req = new ItemStack[] { new ItemStack(Items.golden_sword, 1), new ItemStack(Blocks.glowstone, 1) };
					req = sacrifice(entityRune, req);
					
					if (checkSacrifice(req)) {
						entityRune.data[0] = DATA0_SWORD;
						
					} else {
						entityRune.fizzle();
						return;
					}
				}
			}
		}
		
		if (!takeXP(entityRune, 18)) {
			entityRune.fizzle();
			return;
		}
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		entityRune.setStarScale(entityRune.getStarScale() + 0.001F);
		
		if (entityRune.ticksExisted > 20) {
			Item item = null;
			
			if (entityRune.data[0] == DATA0_AXE) {
				item = DustMod.spiritAxe;
			} else if (entityRune.data[0] == DATA0_PICKAXE) {
				item = DustMod.spiritPickaxe;
			} else if (entityRune.data[0] == DATA0_SHOVEL) {
				item = DustMod.spiritShovel;
			} else if (entityRune.data[0] == DATA0_SWORD) {
				item = DustMod.spiritSword;
			}
			
			ItemStack itemStackTool = new ItemStack(item, 1, 0);
			
			if (entityRune.data[0] == DATA0_AXE) {
				itemStackTool.addEnchantment(Enchantment.smite, 5);
			} else if (entityRune.data[0] == DATA0_PICKAXE) {
				itemStackTool.addEnchantment(Enchantment.aquaAffinity, 1);
				itemStackTool.addEnchantment(Enchantment.baneOfArthropods, 5);
			} else if (entityRune.data[0] == DATA0_SHOVEL) {
				itemStackTool.addEnchantment(Enchantment.knockback, 1);
				itemStackTool.addEnchantment(Enchantment.looting, 3);
			} else if (entityRune.data[0] == DATA0_SWORD) {
				itemStackTool.addEnchantment(Enchantment.fireAspect, 2);
			}
			
			EntityItem entityItem = new EntityItem(entityRune.worldObj, entityRune.posX, entityRune.posY - entityRune.yOffset, entityRune.posZ, itemStackTool);
			entityRune.worldObj.spawnEntityInWorld(entityItem);
			
			entityRune.fade();
		}
	}
}