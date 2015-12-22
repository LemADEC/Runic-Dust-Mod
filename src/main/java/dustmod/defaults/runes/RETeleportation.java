/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
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
	public void initGraphics(EntityRune e) {
		super.initGraphics(e);
		
		e.setRenderStar(true);
		
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
		
		Integer[] fnd = null;
		
		// System.out.println("Check");
		for (Integer[] i : entityRune.dustPoints) {
			TileEntity te = world.getTileEntity(i[0], i[1], i[2]);
			
			if (te != null && te instanceof TileEntityDust) {
				TileEntityDust ted = (TileEntityDust) te;
				int gamt = 10;
				int bamt = 4;
				
				// System.out.println("CHECKING");
				for (int x = 0; x < 4; x++) {
					for (int y = 0; y < 4; y++) {
						// System.out.print(ted.getDust(x, y) + ",");
						if (ted.getDust(x, y) == 2) {
							gamt--;
						}
						
						if (ted.getDust(x, y) == 4) {
							bamt--;
						}
					}
					
					// System.out.println();
				}
				
				if (gamt == 0 && bamt == 0) {
					fnd = i;
					Block block = world.getBlock(i[0], i[1] - 1, i[2]);
					entityRune.data[0] = Block.getIdFromBlock(block);
					DustMod.log("Warp ID set to " + entityRune.data[0] + " " + (block.getUnlocalizedName()));
				}
			} else {
				System.out.println("dewrp");
			}
		}
		
		for (int x = -1; x <= 1 && fnd != null; x++) {
			for (int z = -1; z <= 1; z++) {
				if (x == 0 || z == 0) {
					if (DustMod.isDust(world.getBlock(fnd[0] + x, fnd[1], fnd[2] + z))) {
						TileEntityDust ted = (TileEntityDust) world.getTileEntity(x + fnd[0], fnd[1], fnd[2] + z);
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
							entityRune.posX = (fnd[0] + x) + 0.5D;
							entityRune.posY = (fnd[1]    ) + 1.5D + entityRune.yOffset;
							entityRune.posZ = (fnd[2] + z) + 0.5D;
							
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
		cx = (int) (entityRune.posX);
		cy = (int) entityRune.posY - 1;
		cz = (int) (entityRune.posZ);
		
		if (cx < 0)
			cx--;
		if (cz < 0)
			cz--;
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
		//        System.out.println("ENTITY ID " + e.entityId);
		//        System.out.println("wtf ram:" + e.ram);
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
		
		List<Entity> ents = this.getEntities(entityRune, 10D);
		
		//        System.out.println("DURR " + e.worldObj.worldProvider.worldType + " " + ents.size());
		if (entityRune.ram > 1 && VoidTeleManager.skipWarpTick > 0) {
			VoidTeleManager.skipWarpTick--;
		}
		
		if (entityRune.ram == 1) {
			//            if (ents.size() > 1) {
			////                System.out.println("potato " + ents.size());
			//                mod_DustMod.skipWarpTick--;
			//            }
			for (Object o : ents.toArray()) {
				Entity i = (Entity) o;
				
				if (i instanceof EntityBlock) {
					if (((EntityBlock) i).hasParent()) {
						continue;
					}
				}
				
				double dx = i.posX - entityRune.getX();
				double dy = i.posY - entityRune.getY();
				double dz = i.posZ - entityRune.getZ();
				//                dx *= (dx < 0) ? -1 : 1;
				//                dz *= (dz < 0) ? -1 : 1;
				//                dy *= (dy < 0) ? -1 : 1;
				double tol = 1.0D;
				
				if (!(i instanceof EntityRune) && Math.abs(dx) < tol && Math.abs(dz) < tol && Math.abs(dy) < 3D/*i instanceof EntityLiving && e.getDistanceToEntity(i) <= 0.5F*/) {
					//                    System.out.println("Entity found " + e.getDistanceToEntity(i));
					//                    EntityLiving ei = (EntityLiving)i;
					int index = VoidTeleManager.getVoidNetworkIndex(warp);
					
					//                    for(Object o:(ArrayList<EntityDust>)(warps.clone())){
					//                        if(((EntityDust)o).isDead) warps.remove(o);
					//                    }
					if (i instanceof EntityPlayer && ((EntityPlayer) i).timeUntilPortal < 300) {
						entityRune.ram = 100;
					}
					
					stopWarp:
					
					for (int temp = index + 1; temp != index && index != -1; temp++) {
						if (temp >= VoidTeleManager.voidNetwork.size()) {
							temp = 0;
						}
						
						//                        System.out.println("Dicks :" + temp + " " + index);
						if (temp == index) {
							break stopWarp;
						}
						
						int[] iwarp = VoidTeleManager.voidNetwork.get(temp);
						
						//                        System.out.println("Found warp: " + warp[3] + ":" + warp[4] + " " + iwarp[3] + ":" + iwarp[4] + " dim:" + iwarp[6] + " ver:" + iwarp[7]);
						if ((Math.abs(warp[0] - iwarp[0]) < 0.5D && Math.abs(warp[1] - iwarp[1]) < 0.5D && Math.abs(warp[2] - iwarp[2]) < 0.5D) || iwarp[6] != i.worldObj.provider.dimensionId
								|| iwarp[7] != warp[7]) {
							//                            System.out.println("Skipping dead:" + ed.isDead);
							continue;
						}
						
						//if(ed != null && ed.data == e.data[0] && !ed.equals(e) && ed != e && ed.ram == 1){
						if (warp[3] == iwarp[3] && warp[4] == iwarp[4]) {
							//                            System.out.println("Found warp location " + Arrays.toString(iwarp) + " " + Arrays.toString(warp));
							if (VoidTeleManager.skipWarpTick > 0) {
								//                                mod_DustMod.skipWarpTick -- ;
								entityRune.ram = 100;
								//                                System.out.println("Skipping due to recent tele " + mod_DustMod.skipWarpTick);
								break stopWarp;
							}
							
							if (i instanceof EntityLiving) {
								addFuel(entityRune, -1600);
								((EntityLiving) i).setPositionAndRotation(iwarp[0] + 0.5D, iwarp[1] + 0.6D, iwarp[2] + 0.5D, entityRune.rotationYaw, i.rotationPitch);
								((EntityLiving) i).setPositionAndUpdate(iwarp[0] + 0.5D, iwarp[1] + 0.6D, iwarp[2] + 0.5D);
								((EntityLiving) i).attackEntityFrom(DamageSource.magic, 6);
							} else {
								addFuel(entityRune, -1600);
								i.setPosition(iwarp[0] + 0.5D, iwarp[1] + 0.6D, iwarp[2] + 0.5D);
							}
							
							i.posX = iwarp[0] + 0.5D;
							i.posY = iwarp[1] + 0.6D;
							i.posZ = iwarp[2] + 0.5D;
							i.rotationYaw = iwarp[5];
							i.setPositionAndRotation(iwarp[0] + 0.5D, iwarp[1] + 0.6D, iwarp[2] + 0.5D, iwarp[5], i.rotationPitch);
							//                            System.out.println("Sending to dimension " + i.worldObj.worldProvider.worldType);
							//                            System.out.println("new loc " + i.posX + " " + i.posY + " " + i.posZ);
							//                            System.out.println("DELTA " + dx + " " + dy + " " + dz);
							entityRune.ram = 100;
							EntityRune enWarp = VoidTeleManager.getWarpEntity(iwarp, entityRune.worldObj);
							
							if (enWarp != null) {
								enWarp.ram = 100;
							} else {
								//                                System.out.println("Bad ram");
							}
							
							if (i instanceof EntityPlayer) {
								((EntityPlayer) i).timeUntilPortal = 100;
							}
							
							VoidTeleManager.skipWarpTick = 10;
							break stopWarp;
						}
					}
				} else {
					//                    System.out.println("Derp? " + (!(i instanceof EntityDust)) + " " + e.getDistanceToEntity(i) + " " + dx + " " + dz + " " + dy);
				}
			}
		}
	}
	
	@Override
	public void subtractFuel(EntityRune e) {
	}
	
	@Override
	public void onUnload(EntityRune e) {
		//        System.out.println("KILL");
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
