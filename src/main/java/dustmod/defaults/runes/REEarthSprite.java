/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import dustmod.entities.EntityBlock;
import dustmod.runes.EntityRune;
import dustmod.runes.PoweredEvent;

/**
 *
 * @author billythegoat101
 */
public class REEarthSprite extends PoweredEvent {
	public REEarthSprite() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune e) {
		super.initGraphics(e);
		
		e.setFollow(true);
		e.setRenderBeam(true);
		e.setColorStarInner(255, 0, 0);
		e.setColorStarOuter(255, 0, 0);
		
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		super.onInit(entityRune);
		entityRune.setFollow(true);
		entityRune.setRenderBeam(true);
		entityRune.setColorStarInner(255, 0, 0);
		entityRune.setColorStarOuter(255, 0, 0);
		ItemStack[] req = new ItemStack[] { new ItemStack(Blocks.glass, 16, 0), new ItemStack(Items.ghast_tear, 1, 0) };
		req = this.sacrifice(entityRune, req);
		
		if (req[0].stackSize != 0 || req[1].stackSize != 0 || !takeXP(entityRune, 20)) {
			entityRune.fizzle();
			return;
		}
		
		for (int i = 0; i < 8; i++) {
			EntityBlock entityBlock = new EntityBlock(entityRune.worldObj, entityRune.getX(), entityRune.getY() + 2, entityRune.getZ(), Blocks.glass);
			entityBlock.setParent(entityRune);
			registerFollower(entityRune, entityBlock);
			entityBlock.updateDataWatcher();
			entityRune.worldObj.spawnEntityInWorld(entityBlock);
		}
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		super.onTick(entityRune);
		entityRune.setRenderStar(false);
		entityRune.setRenderBeam(false);
		entityRune.setFollow(true);
		EntityPlayer entityPlayer = entityRune.worldObj.func_152378_a(entityRune.getSummonerId());
		
		if (entityPlayer == null) {
			return;
		}
		
		if (entityRune.genericList == null) {
			entityRune.genericList = new ArrayList<EntityBlock>();
		}
		
		if (entityRune.genericList.size() > 0) {
			int ind = 0;
			float vel = entityPlayer.moveForward;
			boolean wasSneaking = entityRune.data[2] == 1;
			boolean wasProtect = entityRune.data[3] == 1;
			boolean protect = (vel == 0) && entityPlayer.isSneaking() && Math.abs(entityPlayer.motionY) < 0.08D && entityPlayer.onGround;
			entityRune.data[2] = (entityPlayer.isSneaking() ? 1 : 0);
			entityRune.data[3] = protect ? 1 : 0;
			int px = MathHelper.floor_double(entityPlayer.posX);
			int py = MathHelper.floor_double(entityPlayer.posY);
			int pz = MathHelper.floor_double(entityPlayer.posZ);
			
			if (protect) {
				if (entityPlayer.isSneaking() && !wasSneaking)
					entityPlayer.setPositionAndUpdate(px + 0.5D, (double) py + entityPlayer.yOffset, pz + 0.5D);
				entityPlayer.moveForward = 0;
			}
			if (!protect && wasProtect) {
				entityPlayer.setPositionAndUpdate(px + 0.5D, (double) py + entityPlayer.yOffset, pz + 0.5D);
			}
			
			for (Object o : entityRune.genericList) {
				EntityBlock entityBlock = (EntityBlock) o;
				int bx = 0, by = 0, bz = 0;
				
				if (ind % 2 == 0) {
					by = 1;
				}
				
				if (ind < 4) {
					bx = (ind < 2) ? 1 : -1;
				} else {
					bz = (ind < 6) ? 1 : -1;
				}
				
				if (protect /*&& worldObj.getBlockId(px+bx,py+by,pz+bz) == 0*/) {
					entityBlock.setPosition(px + bx, py + by, pz + bz);
					entityBlock.placeAndLinger(0.6D, px + bx, py + by + 1, pz + bz);
				} else {
					//                	eb.unplace();
					int period = 60;
					double dist = 3D;
					double ticks = (entityRune.ticksExisted + ind * 8) % period;
					double ticksOff = (entityRune.ticksExisted + ind * 30) % period;
					double sin = Math.sin((ticks / period) * Math.PI * 2);
					double sinY = Math.sin((ticksOff / period) * Math.PI * 2);
					double cos = Math.cos((ticks / period) * Math.PI * 2);
					double dx = cos * dist;
					double dz = sin * dist;
					double dy = sinY * 1D + 1.5D;
					entityBlock.goTo(2.8D, entityPlayer.posX + dx, entityPlayer.posY + dy + 0.5, entityPlayer.posZ + dz);
				}
				
				ind++;
			}
		}
		
		if (entityRune.ticksExisted > 24000 * 3) {
			entityRune.fade();
		}
	}
	
	@Override
	public void onUnload(EntityRune e) {
		super.onUnload(e);
		
		if (e.genericList == null)
			return;
		for (Object o : e.genericList) {
			EntityBlock eb = (EntityBlock) o;
			int y = (int) eb.posY;//e.worldObj.getHeightValue((int)eb.posX, (int)eb.posZ);
			
			for (int i = y; i >= 0; i--) {
				if (!e.worldObj.isAirBlock((int) eb.posX, i, (int) eb.posZ)) {
					y = i + 1;
					break;
				}
			}
			
			eb.setOriginal((int) eb.posX, y + 1, (int) eb.posZ);
			eb.returnToOrigin(0.2D);
		}
	}
	
	@Override
	public void registerFollower(EntityRune e, Object o) {
		if (o instanceof EntityBlock) {
			if (e.genericList == null) {
				e.genericList = new ArrayList<EntityBlock>();
			}
			
			e.genericList.add(o);
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
		return false;
	}
}
