/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import dustmod.runes.EntityRune;
import dustmod.runes.PoweredEvent;

/**
 *
 * @author billythegoat101
 */
public class REFireSprite extends PoweredEvent {
	public REFireSprite() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune e) {
		super.initGraphics(e);
		
		e.setRenderStar(true);
		e.setFollow(true);
		e.setColorStarInner(255, 0, 0);
		
	}
	
	@Override
	public void onInit(EntityRune e) {
		super.onInit(e);
		e.setRenderStar(true);
		e.setFollow(true);
		e.setColorStarInner(255, 0, 0);
		ItemStack[] sacrifice = new ItemStack[] { new ItemStack(Items.ghast_tear, 1), new ItemStack(Items.fire_charge, 2) };
		this.sacrifice(e, sacrifice);
		
		if (!checkSacrifice(sacrifice) || !takeXP(e, 22)) {
			e.fizzle();
			return;
		}
	}
	
	@Override
	public void onTick(EntityRune e) {
		super.onTick(e);
		e.setColorStarOuter(255, 0, 0);
		
		EntityPlayer player = e.getSummoner();
		
		if (player == null) {
			e.data[0] = 1;
			return;
		} else {
			e.data[0] = 0;
		}
		
		e.setFire(0);
		int rad = 3;
		List<Entity> kill = getEntities(e, rad);
		
		for (Entity k : kill) {
			if (k == player || k == e) {
				continue;
			}
			
			if (k instanceof EntityLiving) {
				k.setFire(2 + (int) (Math.random() * 5));
			}
		}
		
		if (e.ticksExisted % 100 == 0 && Math.random() < 0.5) {
			int ex = e.getX();
			int ey = e.getY();
			int ez = e.getZ();
			boolean ignited = false;
			
			for (int x = -rad; x <= rad && !ignited; x++) {
				for (int y = -rad; y <= rad && !ignited; y++) {
					for (int z = -rad; z <= rad && !ignited; z++) {
						if (!e.worldObj.isAirBlock(ex + x, ey + y - 1, ez + z) && e.worldObj.isAirBlock(ex + x, ey + y, ez + z) && Math.random() < 0.05D) {
							e.worldObj.setBlock(ex + x, ey + y, ez + z, Blocks.fire, 0, 3);
							ignited = true;
						}
					}
				}
			}
		}
	}
	
	@Override
	public int getStartFuel() {
		return dayLength * 3;
	}
	
	@Override
	public int getMaxFuel() {
		return dayLength * 7;
	}
	
	@Override
	public int getStableFuelAmount(EntityRune e) {
		return dayLength + dayLength / 2;
	}
	
	@Override
	public boolean isPaused(EntityRune e) {
		return e.data[0] == 1;
	}
}
