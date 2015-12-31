/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import dustmod.DustMod;
import dustmod.runes.EntityRune;
import dustmod.runes.PoweredEvent;

/**
 *
 * @author billythegoat101
 */
public class REFireRain extends PoweredEvent {
	private List<EntityArrow> listArrows = new ArrayList(1000);
	private static Field fieldInGround = null;
	private final boolean oldStyle = false;
	
	static {
		// open access to EntityArrow.inGround
		Class<?> classEntityArrow = EntityArrow.class;
		try {
			fieldInGround = classEntityArrow.getDeclaredField("inGround");
		} catch (Exception exception1) {
			try {
				fieldInGround = classEntityArrow.getDeclaredField("field_70254_i");
			} catch (Exception exception2) {
				exception2.printStackTrace();
				String map = "";
				for(Field field : classEntityArrow.getDeclaredFields()) {
					if (!map.isEmpty()) {
						map += ", ";
					}
					map += field.getName();
				}
				DustMod.logger.error("Unable to find inGround field in " + classEntityArrow + " class. Available fields are: " + map);
			}
		}
		if (fieldInGround != null) {
			fieldInGround.setAccessible(true);
		}
	}
	
	public REFireRain() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		entityRune.setRenderBeam(true);
		entityRune.setColorStarOuter(255, 0, 0);
		entityRune.setColorBeam(255, 0, 0);
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		super.onInit(entityRune);
		entityRune.setRenderBeam(true);
		entityRune.setColorBeam(255, 0, 0);
		ItemStack[] requiredSacrifice = new ItemStack[] { new ItemStack(Items.blaze_rod, 2) };
		requiredSacrifice = sacrifice(entityRune, requiredSacrifice);
		
		if (!checkSacrifice(requiredSacrifice)) {
			entityRune.fizzle();
			return;
		}
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		super.onTick(entityRune);
		
		if (entityRune.ticksExisted % 2 == 0) {
			int radius = 64;
			float progression = Math.min(1.0F, entityRune.ticksExisted / 200F);
			int amount = Math.min((int)(5 + 10 * progression), Math.max(1, 150 - entityRune.ticksExisted / 5));
			
			Random rand = entityRune.worldObj.rand;
			for (int i = 0; i < amount; i++) {
				EntityArrow entityArrow;
				if (oldStyle) {
					entityArrow = new EntityArrow(entityRune.worldObj,
							entityRune.posX + (Math.random() * 2 - 1) * radius,
							entityRune.posY + 64D,
							entityRune.posZ + (Math.random() * 2 - 1) * radius);
					entityArrow.motionX = Math.random() * 0.05D;
					entityArrow.motionY = Math.random() * -2.0D;
					entityArrow.motionZ = Math.random() * 0.05D;
				} else {
					entityArrow = new EntityArrow(entityRune.worldObj,
							entityRune.posX + (rand.nextDouble() * 2 - 1),
							entityRune.posY + 0.5D + 0.5D * rand.nextDouble(),
							entityRune.posZ + (rand.nextDouble() * 2 - 1));
					double bearing = rand.nextDouble() * Math.PI * 2;
					entityArrow.motionX = (0.15D + 0.65D * progression) * Math.cos(bearing);
					entityArrow.motionY = 0.35D + 1.75D * rand.nextDouble();
					entityArrow.motionZ = (0.15D + 0.65D * progression) * Math.sin(bearing);
				}
				entityArrow.shootingEntity = entityRune.getSummoner();
				entityArrow.setFire(15);
				entityArrow.canBePickedUp = 0;
				listArrows.add(entityArrow);
				entityRune.worldObj.spawnEntityInWorld(entityArrow);
			}
		}
		
		if (entityRune.ticksExisted % 11 == 0) {
			for (Iterator<EntityArrow> iterator = listArrows.iterator(); iterator.hasNext();) {
				EntityArrow entityArrow = iterator.next();
				if (!entityArrow.isBurning()) {
					entityArrow.setDead();
					iterator.remove();
				} else {
					try {
						if (fieldInGround != null && fieldInGround.getBoolean(entityArrow)) {
							int x = (int) Math.round(entityArrow.posX);
							int y = (int) Math.round(entityArrow.posY);
							int z = (int) Math.round(entityArrow.posZ);
							
							if (!entityArrow.worldObj.getBlock(x, y, z).isReplaceable(entityArrow.worldObj, x, y, z)) {
								y++;
								if (!entityArrow.worldObj.getBlock(x, y, z).isReplaceable(entityArrow.worldObj, x, y, z)) {
									y = -1;
								}
							}
							if (y != -1 && Blocks.fire.canPlaceBlockAt(entityArrow.worldObj, x, y, z)) {
								if (entityRune.isPlayerAllowedToBreakBlock(x, y, z)) {
									entityArrow.setDead();
									iterator.remove();
									entityArrow.worldObj.setBlock(x, y, z, Blocks.fire, 0, 3);
								}
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DustMod.logger.error("Disabling reflexion...");
						fieldInGround = null;
					}
				}
			}
		}
	}
	
	@Override
	public int getStartFuel() {
		return dayLength / 2;
	}
	
	@Override
	public int getMaxFuel() {
		return dayLength * 3;
	}
	
	@Override
	public int getStableFuelAmount(EntityRune entityRune) {
		return dayLength / 2;
	}
	
	@Override
	public boolean isPaused(EntityRune entityRune) {
		return false;
	}
}
