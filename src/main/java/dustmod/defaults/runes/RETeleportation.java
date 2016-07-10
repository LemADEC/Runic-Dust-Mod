/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import dustmod.DustMod;
import dustmod.blocks.TileEntityDust;
import dustmod.entities.EntityBlock;
import dustmod.runes.EntityRune;
import dustmod.runes.PoweredEvent;

/**
 *
 * @author billythegoat101
 */
public class RETeleportation extends PoweredEvent {
	//    public static ArrayList<EntityDust> warps = new ArrayList<EntityDust>();
	public RETeleportation() {
		super();
	}
	
	@Override
	public void initGraphics(EntityRune entityRune) {
		super.initGraphics(entityRune);
		
		entityRune.setRenderStar(true);
	}
	
	@Override
	public void onInit(EntityRune entityRune) {
		super.onInit(entityRune);
		World world = entityRune.worldObj;
		ItemStack[] req = this.sacrifice(entityRune, new ItemStack[] { new ItemStack(Items.ender_eye, 1) });
		
		if (req[0].stackSize != 0 || !takeXP(entityRune, 5)) {
			entityRune.fizzle();
			return;
		}
		
		Integer[] found = null;
		
		// checking ?
		for (Integer[] i : entityRune.dustPoints) {
			TileEntity tileEntity = world.getTileEntity(i[0], i[1], i[2]);
			
			if (tileEntity != null && tileEntity instanceof TileEntityDust) {
				TileEntityDust tileEntityDust = (TileEntityDust) tileEntity;
				int gamt = 10;
				int bamt = 4;
				
				for (int x = 0; x < 4; x++) {
					for (int y = 0; y < 4; y++) {
						if (tileEntityDust.getDust(x, y) == 2) {
							gamt--;
						}
						
						if (tileEntityDust.getDust(x, y) == 4) {
							bamt--;
						}
					}
				}
				
				if (gamt == 0 && bamt == 0) {
					found = i;
					Block block = world.getBlock(i[0], i[1] - 1, i[2]);
					entityRune.data[0] = Block.getIdFromBlock(block);
					DustMod.log("Warp ID set to " + entityRune.data[0] + " " + block.getUnlocalizedName());
				}
			} else {
				System.out.println("dewrp");
			}
		}
		
		for (int x = -1; x <= 1 && found != null; x++) {
			for (int z = -1; z <= 1; z++) {
				if (x == 0 || z == 0) {
					if (DustMod.isDust(world.getBlock(found[0] + x, found[1], found[2] + z))) {
						TileEntityDust ted = (TileEntityDust) world.getTileEntity(x + found[0], found[1], found[2] + z);
						int gamt = 4;
						int bamt = 4;
						
						for (int i = 0; i < 4; i++) {
							for (int j = 0; j < 4; j++) {
								if (ted.getDust(i, j) == 2) {
									gamt--;
								}
								
								if (ted.getDust(i, j) == 4) {
									bamt--;
								}
							}
						}
						
						if (gamt == 0 && bamt == 0) {
							entityRune.posX = (found[0] + x) + 0.5D;
							entityRune.posY = (found[1]    ) + 1.5D + entityRune.yOffset;
							entityRune.posZ = (found[2] + z) + 0.5D;
							
							if (x == -1) {
								entityRune.rotationYaw = 270;
							} else if (x == 1) {
								entityRune.rotationYaw = 90;
							} else if (z == -1) {
								entityRune.rotationYaw = 0;
							} else if (z == 1) {
								entityRune.rotationYaw = 180;
							}
						}
					}
				}
			}
		}
		
		entityRune.rotationYaw = ((entityRune.runeRotation + 1) % 4) * 90;
		
		int cx, cy, cz;
		cx = (int) entityRune.posX;
		cy = (int) entityRune.posY - 1;
		cz = (int) entityRune.posZ;
		
		if (cx < 0) {
			cx--;
		}
		if (cz < 0) {
			cz--;
		}
		switch (entityRune.runeRotation) {
		case 0:
			cx++;
			break;
		case 1:
			cz++;
			break;
		case 2:
			cx--;
			break;
		case 3:
			cz--;
			break;
		default:
			break;
		}
		//        e.worldObj.setBlockWithNotify(cx,cy,cz,Block.brick.blockID);
		entityRune.data[0] = Block.getIdFromBlock(entityRune.worldObj.getBlock(cx, cy, cz));
		
		//        System.out.println("Derp set " + e.data[0] + " " + Block.blocksList[e.data[0]].getBlockName() + " " + e.rot);
		entityRune.posY += 1.5D;
		entityRune.setRenderStar(true);
		entityRune.setStarScaleY(2.0F);
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		super.onTick(entityRune);
		
		int[] warp = VoidTeleManager.toWarp(entityRune);
		VoidTeleManager.addWarp(warp);
		
		if (entityRune.ram == 0) {
			VoidTeleManager.addWarp(warp);
			entityRune.ram = 1;
		} else if (entityRune.ram > 1) {
			entityRune.ram--;
			entityRune.setColorStarOuter(255, 0, 0);
			entityRune.setColorStarInner(255, 0, 0);
		} else {
			entityRune.setColorStarInner(255, 255, 255);
			entityRune.setColorStarOuter(255, 255, 255);
		}
		
		List<Entity> entities = this.getEntitiesExcluding(entityRune, 10D);
		
		if (entityRune.ram > 1 && VoidTeleManager.skipWarpTick > 0) {
			VoidTeleManager.skipWarpTick--;
		}
		
		if (entityRune.ram == 1) {
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
				double tol = 1.0D;
				
				if ( Math.abs(dx) > tol
				  || Math.abs(dz) > tol
				  || Math.abs(dy) > 3D ) {
					continue;
				}
				
				int index = VoidTeleManager.getVoidNetworkIndex(warp);
				
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
							entityRune.ram = 100;
							break stopWarp;
						}
						
						addFuel(entityRune, -1600);
						if (entity instanceof EntityLivingBase) {
							((EntityLivingBase) entity).setPositionAndUpdate(iwarp[0] + 0.5D, iwarp[1] + 0.6D, iwarp[2] + 0.5D);
							((EntityLivingBase) entity).attackEntityFrom(DamageSource.magic, 6);
						} else {
							entity.setPosition(iwarp[0] + 0.5D, iwarp[1] + 0.6D, iwarp[2] + 0.5D);
						}
						entity.setLocationAndAngles(iwarp[0] + 0.5D, iwarp[1] + 0.6D, iwarp[2] + 0.5D, iwarp[5], entity.rotationPitch);
						entity.setPositionAndRotation(iwarp[0] + 0.5D, iwarp[1] + 0.6D, iwarp[2] + 0.5D, iwarp[5], entity.rotationPitch);
						
						entityRune.ram = 100;
						EntityRune enWarp = VoidTeleManager.getWarpEntity(iwarp, entityRune.worldObj);
						
						if (enWarp != null) {
							enWarp.ram = 100;
						}
						
						if (entity instanceof EntityPlayer) {
							((EntityPlayer) entity).timeUntilPortal = 100;
						}
						
						VoidTeleManager.skipWarpTick = 10;
						break stopWarp;
					}
				}
			}
		}
	}
	
	@Override
	public void subtractFuel(EntityRune e) {
	}
	
	@Override
	public void onUnload(EntityRune e) {
		VoidTeleManager.removeWarp(VoidTeleManager.toWarp(e));
	}
	
	@Override
	public int getStartFuel() {
		return dayLength * 4;
	}
	
	@Override
	public int getMaxFuel() {
		return dayLength * 12;
	}
	
	@Override
	public int getStableFuelAmount(EntityRune e) {
		return dayLength * 2;
	}
	
	@Override
	public boolean isPaused(EntityRune e) {
		return false;
	}
	
	public int[] findBlock(EntityRune e) {
		int[] block = new int[2];
		return block;
	}
}
