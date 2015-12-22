/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import dustmod.runes.EntityRune;
import dustmod.runes.PoweredEvent;

/**
 *
 * @author billythegoat101
 */
public class REBait extends PoweredEvent {
	public REBait() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		entityRune.setRenderStar(true);
		entityRune.setStarScale(1.005F);
		entityRune.setColorStarOuter(255, 1, 1);
		
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		super.onInit(entityRune);
		int entClass = -1;
		List<Entity> entities = getEntities(entityRune);
		
		for (Entity entity : entities) {
			if (entity instanceof EntityItem) {
				EntityItem entityItem = (EntityItem) entity;
				ItemStack item = entityItem.getEntityItem();
				
				if (item.getItem() == Items.spawn_egg) {
					entClass = item.getItemDamage();
					item.stackSize--;
					
					if (item.stackSize <= 0) {
						entityItem.setDead();
					}
				}
				entityItem.setEntityItemStack(item);
			}
		}
		
		ItemStack[] req = new ItemStack[] { new ItemStack(Blocks.gold_block, 1) };
		req = this.sacrifice(entityRune, req);
		
		if (!checkSacrifice(req) || entClass == -1 || !takeXP(entityRune, 5)) {
			entityRune.fizzle();
			return;
		}
		
		entityRune.data[0] = entClass;
		entityRune.setStarScale(1.005F);
		entityRune.setColorStarOuter(255, 1, 1);
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		super.onTick(entityRune);
		List<Entity> bait = getEntities(entityRune, 16D);
		
		for (Entity k : bait) {
			if (k instanceof EntityCreature && EntityList.getEntityID(k) == entityRune.data[0]) {
				EntityCreature el = (EntityCreature)k;

//                System.out.println("Found entity " + mod_DustMod.isAIEnabled(el));
//                if (!DustModBouncer.isAIEnabled(el))
//                {
//                    el.posY += 1;
//                    el.setPathToEntity(null);//e.worldObj.getPathToEntity(el, e, 16F));
//                    el.setTarget(e);
//                    el.setPathToEntity(e.worldObj.getEntityPathToXYZ(el, e.getX(), e.getY(), e.getZ(), 10F, true, false, false, true));
//                    DustModBouncer.updateState(el);
//                    el.motionY += 0.015;
//                    EntityLookHelper elh = el.getLookHelper();//func_46008_aG();
//                    elh.setLookPositionWithEntity(e, 0, 1);//func_46141_a(e, 1, 1);
//                    el.setMoveForward(16F);
//                    el.velocityChanged = true;
//                    DustModBouncer.setEntityToAttack(el, e);
//                    el.setHomeArea(e.getX(), e.getY(), e.getZ(), 0);
//                    DustModBouncer.setEntityToAttack(el, e);
//                    
//
//                    if(Math.random() < 0.2){
//                    	DustMod.spawnParticles(el.worldObj, "smoke", el.posX, el.posY+el.height/2, el.posZ,
//                    			0, Math.random() * 0.05, 0, (int)(Math.random()*20), 0.75, el.height/2, 0.75);
//                    }
//                    
//                }
//                else
//                {

				EntityAITasks tasks = el.tasks;
				List taskList = tasks.taskEntries;
				boolean hasTaskAlready = false;
				for (Object object : taskList) {
					EntityAIBase task = (EntityAIBase) object;
					if (task.getClass() == EntityAIRuneFollowBaitRune.class) {
						hasTaskAlready = true;
						break;
					}
				}
				if (!hasTaskAlready) {
					tasks.addTask(-1, new EntityAIRuneFollowBaitRune(el, 0.22F));
				}
			}
		}
	}
	
	public static int getEntity(ItemStack is) {
		for (ItemStack i : entdrops.keySet()) {
			if (i.getItem() == is.getItem() && i.getItemDamage() == is.getItemDamage()) {
				return entdrops.get(i);
			}
		}
		
		return -1;
	}

    public static HashMap<ItemStack, Integer> entdrops = new HashMap<ItemStack, Integer>();
	
	static {
		entdrops.put(new ItemStack(Items.porkchop, 0, 0), 90);
		entdrops.put(new ItemStack(Items.beef, 0, 0), 92);
		entdrops.put(new ItemStack(Items.chicken, 0, 0), 93);
		entdrops.put(new ItemStack(Items.dye, 0, 0), 94);
		entdrops.put(new ItemStack(Items.leather, 0, 0), 95);
		entdrops.put(new ItemStack(Blocks.red_mushroom, 0, 0), 96);
		entdrops.put(new ItemStack(Blocks.pumpkin, 0, 0), 97);
		entdrops.put(new ItemStack(Items.cooked_porkchop, 0, 0), 57);
		entdrops.put(new ItemStack(Items.gunpowder, 0, 0), 50);
		entdrops.put(new ItemStack(Items.bone, 0, 0), 51);
		entdrops.put(new ItemStack(Items.string, 0, 0), 52);
		entdrops.put(new ItemStack(Items.rotten_flesh, 0, 0), 54);
		entdrops.put(new ItemStack(Items.slime_ball, 0, 0), 55);
		entdrops.put(new ItemStack(Items.ghast_tear, 0, 0), 56);
		entdrops.put(new ItemStack(Items.ender_pearl, 0, 0), 58);
		entdrops.put(new ItemStack(Items.spider_eye, 0, 0), 59);
		entdrops.put(new ItemStack(Blocks.stonebrick, 0, 0), 60);
		entdrops.put(new ItemStack(Items.blaze_rod, 0, 0), 61);
		entdrops.put(new ItemStack(Items.magma_cream, 0, 0), 62);
		//entdrops.put(new ItemStack(Items.ender_eye, 0, 0), 63);
	}
	
	@Override
	public int getStartFuel() {
		return dayLength * 7;
	}
	
	@Override
	public int getMaxFuel() {
		return dayLength * 7;
	}
	
	@Override
	public int getStableFuelAmount(EntityRune e) {
		return dayLength * 5;
	}
	
	@Override
	public boolean isPaused(EntityRune e) {
		return false;
	}
}
