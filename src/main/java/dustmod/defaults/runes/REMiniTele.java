package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import dustmod.entities.EntityBlock;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REMiniTele extends RuneEvent {
	public REMiniTele() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		entityRune.setRenderStar(true);
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		World world = entityRune.worldObj;
		
		if (!takeXP(entityRune, 5)) {
			entityRune.fizzle();
			return;
		}
		
		int cx, cy, cz;
		cx = (int) entityRune.posX - (entityRune.posX < 0 ? 1 : 0);
		cy = (int) entityRune.posY - 1;
		cz = (int) entityRune.posZ - (entityRune.posZ < 0 ? 1 : 0);
		entityRune.data[0] = Block.getIdFromBlock(world.getBlock(cx, cy, cz));
		
		entityRune.setRenderStar(true);
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		int[] warp = VoidTeleManager.toWarp(entityRune);
		
		List<Entity> entities = this.getEntitiesExcluding(entityRune, 10D);
		
		for (Entity entity : entities) {
			// skip sacrifices (?)
			if (entity instanceof EntityBlock) {
				if (((EntityBlock) entity).hasParent()) {
					continue;
				}
			}
			// skip other runes
			if (entity instanceof EntityRune) {
				continue;
			}
			
			// skip entities too far away
			double dx = entity.posX - entityRune.getX();
			double dy = entity.posY - entityRune.getY();
			double dz = entity.posZ - entityRune.getZ();
			double tol = 3.2D;
			
			if ( Math.abs(dx) > tol
			  || Math.abs(dz) > tol
			  || Math.abs(dy) > 3D ) {
				continue;
			}
			
			int index = 0;
			
			if (entity instanceof EntityPlayer && ((EntityPlayer) entity).timeUntilPortal < 300) {
				entityRune.ram = 100;
			}
			
			stopWarp:
			
			for (int temp = index + 1; temp != index && index != -1; temp++) {
				if (temp >= VoidTeleManager.voidNetwork.size()) {
					temp = 0;
				}
				
				if (temp == index) {
					break stopWarp;
				}
				
				int[] iwarp = VoidTeleManager.voidNetwork.get(temp);
				
				// skip this rune
				if ( Math.abs(warp[0] - iwarp[0]) < 0.5D
				  && Math.abs(warp[1] - iwarp[1]) < 0.5D
				  && Math.abs(warp[2] - iwarp[2]) < 0.5D ) {
					continue;
				}
				// skip runes in other dimension or from a different mod version
				if ( iwarp[6] != entity.worldObj.provider.dimensionId
				  || iwarp[7] != warp[7] ) {
					continue;
				}
				
				// is it the same block/metadata?
				if (warp[3] == iwarp[3] && warp[4] == iwarp[4]) {
					if (VoidTeleManager.skipWarpTick > 0) {
						break stopWarp;
					}
					
					if (entity instanceof EntityLivingBase) {
						((EntityLivingBase) entity).setPositionAndUpdate(iwarp[0] + 0.5D, iwarp[1] + 0.6D, iwarp[2] + 0.5D);
					}
					entity.setLocationAndAngles(iwarp[0] + 0.5D, iwarp[1] + 0.6D, iwarp[2] + 0.5D, iwarp[5], entity.rotationPitch);
					
					if (entity instanceof EntityPlayer) {
						((EntityPlayer) entity).timeUntilPortal = 200;
					}
					
					VoidTeleManager.skipWarpTick = 10;
					break stopWarp;
				}
			}
		}
		
		if (entityRune.ticksExisted > 100) {
			entityRune.setColorStarOuter(255, 0, 0);
			entityRune.setColorStarInner(255, 0, 0);
			entityRune.fade();
		}
	}
	
	@Override
	public void onUnload(EntityRune entityRune) {
	}
}
