/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import dustmod.blocks.TileEntityDust;
import dustmod.runes.EntityRune;
import dustmod.runes.PoweredEvent;

/**
 *
 * @author billythegoat101
 */
public class REFog extends PoweredEvent {
	public REFog() {
		super();
	}
	
	@Override
	public void onInit(EntityRune e) {
		super.onInit(e);
		ItemStack[] req = new ItemStack[] { new ItemStack(Items.water_bucket, 1), new ItemStack(Blocks.red_mushroom, 1) };
		req = this.sacrifice(e, req);
		
		if (!checkSacrifice(req) || !takeXP(e, 6)) {
			e.fizzle();
			return;
		}
	}
	
	@Override
	public void onTick(EntityRune entityRune) {
		super.onTick(entityRune);
		
		if (entityRune.ticksExisted % 5 == 0) {
			int amt = 40;
			int radius = 10;
			int cycle = 60;
			float maxAlpha = 1.5F;
			float minAlpha = 0.4F;
			float alpha = 0;
			float diff = maxAlpha - minAlpha;
			int flip = (entityRune.ticksExisted % (cycle * 2) > cycle) ? 1 : -1;
			int stage = entityRune.ticksExisted % cycle - cycle / 2;
			stage *= flip;
			float percent = ((float) stage / (float) (cycle));
			alpha = percent * diff + minAlpha + diff / 2F;
//            for(int i = 0; i < amt; i++){
//                int rx = (int)(Math.random()*radius*2)-radius;
//                int ry = (int)(Math.random()*radius+3)-3;
//                int rz = (int)(Math.random()*radius*2)-radius;
//                int x = e.getX() + rx;
//                int y = e.getY() + ry;
//                int z = e.getZ() + rz;
//
//                Block block = Block.blocksList[e.worldObj.getBlockId(x, y, z)];
//                int light = e.worldObj.getSavedLightValue(EnumSkyBlock.Block, x, y, z);
//                if (light < 7 && (block == null || !block.isOpaqueCube()))
//                {
//                    mod_DustMod.spawnNewFogFX(e.worldObj, x, y, z, 0.85F + (float)Math.random() * 0.2F);
//                }
//            }
//            System.out.println("Alpha" + alpha + " \tPercent" + percent + " \tStage" + stage);

			if (entityRune.ticksExisted % cycle == 0 && !entityRune.worldObj.isRemote) {
				List ents = this.getEntities(entityRune, radius);
				EntityPlayer player = entityRune.worldObj.getClosestPlayerToEntity(entityRune, radius);
				
				for (Object o : ents) {
					Entity i = (Entity) o;
					int x = MathHelper.floor_double(i.posX);
					int y = MathHelper.floor_double(i.posY);
					int z = MathHelper.floor_double(i.posZ);
					int light = entityRune.worldObj.getSavedLightValue(EnumSkyBlock.Block, x, y, z);
					if (light >= 7) {
						// System.out.println("Err light");
						continue;
					}
					
					if (i instanceof EntityCreature) {
						EntityCreature ec = (EntityCreature) i;
						ec.setRevengeTarget(ec);
						ec.setAttackTarget(ec);
						ec.setAttackTarget(ec);
						ec.setPathToEntity(null);
						// DustModBouncer.setHasAttacked(ec, true);
						ec.attackTime = 30;
						
						if (player != null) {
							// DustModBouncer.setCantSee(ec, player);
						}

//                                ec.worldObj.setEntityState(ec, (byte)2);
////                                ec.setBeenAttacked();
//                                ec.velocityChanged = true;
//                                System.out.println("SETTING");
//                                mod_DustMod.updateActionState(ec);
//                                ec.attackEntityFrom(DamageSource.causeMobDamage((EntityLiving)target),0);
//                            }
//                            System.out.println("Retarget " + ec.getAITarget());
//                        }
//                        }
					}
					
					if (i instanceof EntityLiving && Math.random() < 0.8D) {
						EntityLiving el = (EntityLiving) o;
						el.setPositionAndRotation(el.posX, el.posY, el.posZ, (float) Math.random() * 360F, el.rotationPitch);
					}
				}
			}
		}
	}
	
	@Override
	public void onRightClick(EntityRune e, TileEntityDust ted, EntityPlayer p) {
		super.onRightClick(e, ted, p);
	}
	
	@Override
	public void onUnload(EntityRune e) {
		super.onUnload(e);
	}
	
	@Override
	public int getStartFuel() {
		return dayLength;
	}
	
	@Override
	public int getMaxFuel() {
		return dayLength * 7;
	}
	
	@Override
	public int getStableFuelAmount(EntityRune e) {
		return dayLength;
	}
	
	@Override
	public boolean isPaused(EntityRune e) {
		return false;
	}
}
