package dustmodtestpack.inscriptions;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import dustmod.DustMod;
import dustmod.entities.EntityBlock;
import dustmod.inscriptions.InscriptionEvent;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

public class ErfBendInscription extends InscriptionEvent {

	public ErfBendInscription(int[][] design, String idName,
			String properName, int id) {
		super(design, idName, properName, id);
		this.setAuthor("billythegoat101");
		this.setDescription("Description:\n" +
				"Launch your enemies sky-high with a pillar of earth! Or drop them deep into an abyss! \n-\n Shift+left and right click with an empty hand at an enemy to activate. \n" +
				"Design is still a WIP\n.\n.\n.\n(Earthbending is from the awesome show Avatar, please don't sue me.)");
		this.setNotes("Sacrifice:\n" +
				"TBD");
	}
	
	@Override
	public boolean callSacrifice(RuneEvent rune, EntityRune e, ItemStack item) {
		ItemStack[] req = rune.sacrifice(e, new ItemStack[] {new ItemStack(Blocks.iron_block, 8, -1)});

        if (req[0].stackSize != 0)
        {
            e.fizzle();
            return false;
        }
        
        item.setItemDamage(0);
        
		return true;
	}
	
	public void onUpdate(EntityLivingBase wearer, ItemStack item, boolean[] buttons){
		if (((EntityPlayer)wearer).getCurrentEquippedItem() == null && wearer.isSneaking() && wearer.onGround) {
			try{
				Entity target = getLaunchEnt((EntityPlayer)wearer);

				//Pillar & sinkhole
				if(!isAlreadyLaunching(item) && target != null && (buttons[0] || buttons[1])){
					int x = (int)target.posX-1;
					int y = (int)target.posY-1;
					int z = (int)target.posZ-1;
					
					World world = wearer.worldObj;
	
					int r = 1;
					int launch = 6;
					
					if((world.isAirBlock(x,y,z) && world.isAirBlock(x,y-1,z)) || target.isInWater()) return;
					if(buttons[0] && checkForEarth(target,r+1,launch) < 0.80) return;
					
					targetEntity(item,wearer,target,buttons[0] ? 0:1);
				}
				
				//Block mover
				else {
					if(!buttons[1] && this.mouseWasDownR(item)) blockClick(wearer,item);
					else if(buttons[2]) middleClickBlock(wearer,item);
				}
				
				
			}catch(Exception e){System.out.println("penis");e.printStackTrace();}
		}
		else if(((EntityPlayer)wearer).getCurrentEquippedItem() == null && buttons[0]) {
			fire(wearer,item);
		}
		
		if(isAlreadyLaunching(item)) {
				int cx,cy,cz;
				int tx,ty,tz;
				int entid;
				
				NBTTagCompound tag = item.getTagCompound();
				World world = wearer.worldObj;

				int button = tag.getInteger("but");
				cx = tag.getInteger("curx");
				cy = tag.getInteger("cury");
				cz = tag.getInteger("curz");
				tx = tag.getInteger("tarx");
				ty = tag.getInteger("tary");
				tz = tag.getInteger("tarz");
				entid = tag.getInteger("entid");

				int eRad = 3;
				Vec3 vec = Vec3.createVectorHelper(tx-cx, ty-cy, tz-cz);
				
				if(vec.lengthVector() > eRad){
				
					vec = vec.normalize();
					cx += Math.round(vec.xCoord);
					cy += Math.round(vec.yCoord);
					cz += Math.round(vec.zCoord);
//					world.setBlockWithNotify(cx, cy, cz, Block.brick.blockID);
					
					for(int i = 5; i >= -5; i--){
						Block block = world.getBlock(cx, cy+i, cz);
						int meta = world.getBlockMetadata(cx, cy+i, cz);
						
						if(block == Blocks.grass) block = Blocks.dirt;
						Block blockA = world.getBlock(cx, cy+i+1, cz);
						int metaA = world.getBlockMetadata(cx, cy+i+1, cz);
						if(isSolid(block,meta) && !isSolid(blockA,metaA)){
//								cy += i;
//							if(button == 0){
//								world.setBlock(cx,cy+i+1,cz,bid,meta);
//								world.setBlockWithNotify(cx,cy+i,cz,getFillerMaterial(bid,meta));
								DustMod.spawnParticles(wearer.worldObj, "tilecrack_" + Block.getIdFromBlock(block) + "_" + meta, cx, cy+i+1, cz, 0, 2, 0, 30, 0.5,0.1,0.5);
//							}else {
////								world.setBlock(cx,cy+i-1,cz,bid,meta);
////								world.setBlockWithNotify(cx,cy+i,cz,0);
//								DustMod.spawnParticles(wearer.worldObj, "tilecrack_" + bid + "_" + meta, cx, cy+i, cz, 0, 1, 0, 5, 0.5,0.1,0.5);
//							}
							break;
						}
					}
					
					tag.setInteger("curx",cx);
					tag.setInteger("cury",cy);
					tag.setInteger("curz",cz);

				} else {
					tag.setBoolean("isLaunching",false);
					damage((EntityPlayer)wearer, item, 10);
					int r = 1;
					int launch = 6;
					if(button == 0) launch(world,wearer,tx,ty,tz, r, launch, eRad);
					else sink(world,wearer,tx,ty,tz, r, launch, eRad);
				}
			}
		
		this.setMouseWasDownR(item, buttons[1]);
	}
	
	public void launch(World world, Entity wearer, int tx, int ty, int tz, int r, int launch, int eRad){
		List<Entity> ents = world.getEntitiesWithinAABBExcludingEntity(wearer, AxisAlignedBB.getBoundingBox(tx, ty, tz, tx + 1.0D, ty + 1.0D, tz + 1.0D).expand(eRad, eRad, eRad));
		for(Entity ent:ents){
			ent.setLocationAndAngles(ent.posX, ent.posY+launch, ent.posZ, ent.rotationYaw, ent.rotationPitch);
			ent.addVelocity(0, launch/4D, 0);
		}
		for(int i = -r; i <= r; i++){
			for(int j = -r; j <= r; j++){
				for(int k = 0; k > -launch; k--){
					Block block = world.getBlock(tx+i,ty+k,tz+j);
					int meta = world.getBlockMetadata(tx+i,ty+k,tz+j);
					Block toBlock = world.getBlock(tx+i,ty+k+launch,tz+j);
					int toMeta = world.getBlockMetadata(tx+i,ty+k+launch,tz+j);
					if(block.getMaterial() == Material.air && k != 0) block = Blocks.cobblestone;
					if(!isSolid(toBlock,toMeta)/* && Block.blocksList[bid].isOpaqueCube()*/){
						world.setBlock(tx+i, ty+k+launch, tz+j, block,meta,3);
						world.setBlock(tx+i, ty+k, tz+j, getFillerMaterial(block,meta),0,3);
					}
				}
			}
		}
	}
	
	public void sink(World world, Entity wearer, int tx, int ty, int tz, int r, int launch, int eRad){
		List<Entity> ents = world.getEntitiesWithinAABBExcludingEntity(wearer, AxisAlignedBB.getBoundingBox(tx, ty, tz, tx + 1.0D, ty + 1.0D, tz + 1.0D).expand(eRad, eRad, eRad));
		for(Entity ent:ents){
			ent.fallDistance += 8;
		}
		
		r++;
		for(int i = -r; i <= r; i++){
			for(int j = -r; j <= r; j++){
				for(int k = 0; k > -launch; k--){
					Block block = world.getBlock(tx+i,ty+k,tz+j);
					int meta = world.getBlockMetadata(tx+i,ty+k,tz+j);
					Block toBlock = world.getBlock(tx+i,ty+k+launch,tz+j);
					int toMeta = world.getBlockMetadata(tx+i,ty+k+launch,tz+j);
					if(block.getMaterial() == Material.air) continue;//bid = Block.cobblestone.blockID;
					if(!isSolid(toBlock,toMeta)/* && Block.blocksList[bid].isOpaqueCube()*/){
						world.setBlock(tx+i, ty+k-launch, tz+j, block,meta,3);
						world.setBlockToAir(tx+i, ty+k, tz+j);
					}
				}
			}
		}
	}
	
	public void targetEntity(ItemStack item, Entity player, Entity target, int button){
		NBTTagCompound tag = item.getTagCompound();
		int px = (int)player.posX-1;
		int py = (int)player.posY-1;
		int pz = (int)player.posZ-1;
		int x = (int)target.posX-1;
		int y = (int)target.posY-1;
		int z = (int)target.posZ-1;
		

		Vec3 vec = Vec3.createVectorHelper(x-px, y-py, z-pz);
		vec = vec.normalize();
		px += Math.round(vec.xCoord);
		py += Math.round(vec.yCoord);
		pz += Math.round(vec.zCoord);
		
		
		tag.setBoolean("isLaunching", true);
		tag.setInteger("curx", px);
		tag.setInteger("cury", py);
		tag.setInteger("curz", pz);
		tag.setInteger("tarx", x);
		tag.setInteger("tary", y);
		tag.setInteger("tarz", z);
		tag.setInteger("but",button);
		tag.setInteger("entid", target.getEntityId());
	}
	
	public boolean isAlreadyLaunching(ItemStack item){
		NBTTagCompound tag = item.getTagCompound();
		if(tag != null){
			if(!tag.hasKey("isLaunching")) {
				tag.setBoolean("isLaunching",false);
				return false;
			}
			return tag.getBoolean("isLaunching");
		}
		return false;
	}

	/**
	 * Checks for the percent of the blocks below the entity that are solid
	 * @param target	target entity
	 * @param r 	radius
	 * @param h		height
	 * @return		%isSolid
	 */
	public double checkForEarth(Entity target, int r, int h){
		int x = (int)target.posX-1;
		int y = (int)target.posY-1;
		int z = (int)target.posZ-1;
		
		double tot = 0;
		double amt = 0;
		
		World world = target.worldObj;
		for(int i = -r; i <= r; i++){
			for(int j = -r; j <= r; j++){
				for(int k = 0; k > -h; k--){
					if(y+k < 0){
						continue;
					}
					
					Block block = world.getBlock(x+i,y+k,z+j);
					int meta = world.getBlockMetadata(x+i,y+k,z+j);
					
					tot++;
					if(isSolid(block,meta)){
						amt++;
					}
				}
			}
		}
		if(tot == 0) return 0;
		return amt/tot;
	}
	
	public boolean isSolid(Block block, int meta){
		Material m = block.getMaterial();
		if(m == Material.vine || m == Material.ice || m == Material.wood || m == Material.web) return false;
		if(m == Material.snow) {
			if(block == Blocks.snow){
				return meta >= 8;
			}else return block.isOpaqueCube();
		}
		if(block instanceof BlockLiquid) return false; 
		return true;
	}
	
	public Block getCompressedMaterial(Block block, int meta){
//		final int sand=Block.sand.blockID, stone=Block.stone.blockID,dirt=Block.dirt.blockID,gravel=Block.gravel.blockID,netherbrick=Block.netherBrick.blockID;
//		final int sandstone, cobblestone, netherrack;
//		System.out.println("Get compressed " + blockID);
		
		if (block == Blocks.sand)
			return Blocks.sandstone;
		if (block == Blocks.stone || block == Blocks.grass || block == Blocks.dirt || block == Blocks.gravel || block == Blocks.double_stone_slab || block == Blocks.stone_slab || block == Blocks.mossy_cobblestone || block == Blocks.clay || block == Blocks.stonebrick)
			return Blocks.cobblestone;
		if (block == Blocks.netherrack || block == Blocks.nether_brick)
			return Blocks.netherrack;

		return block;
	}
	public Block getFillerMaterial(Block block, int meta){
		if (block == Blocks.sand || block == Blocks.sandstone)
			return Blocks.sand;
		if (block == Blocks.stone || block == Blocks.gravel || block == Blocks.double_stone_slab || block == Blocks.stone_slab || block == Blocks.mossy_cobblestone || block == Blocks.clay || block == Blocks.stonebrick)
			return Blocks.cobblestone;
		if (block == Blocks.netherrack || block == Blocks.nether_brick)
			return Blocks.netherrack;
		
		return Blocks.dirt;
	}
	public boolean isCompressedMaterial(Block block){
		Block[] blocks = new Block[]{Blocks.sandstone, 
				Blocks.cobblestone, 
				Blocks.mossy_cobblestone, 
				Blocks.netherrack};
		for(Block testBlock: blocks) 
			if(testBlock == block) return true;
		return false;
	}


	public static synchronized Entity getLaunchEnt(EntityPlayer ep) {
		try{
        float var9 = 1.0F;
        double var11 = 48; //reach
        Vec3 var6 = Vec3.createVectorHelper(ep.posX, ep.posY, ep.posZ);//ep.getPosition(0);
        Vec3 var7 = ep.getLook(0);
        Vec3 var8 = var6.addVector(var7.xCoord * var11, var7.yCoord * var11, var7.zCoord * var11);
		List var10 = ep.worldObj.getEntitiesWithinAABBExcludingEntity(ep, ep.boundingBox.addCoord(var7.xCoord * var11, var7.yCoord * var11, var7.zCoord * var11).expand((double)var9, (double)var9, (double)var9));
        Iterator var13 = var10.iterator();

        Entity pointedEntity = null;
        while (var13.hasNext())
        {
            Entity var14 = (Entity)var13.next();

            if (var14.canBeCollidedWith())
            {
                float var15 = var14.getCollisionBorderSize() + 1F;
                AxisAlignedBB var16 = var14.boundingBox.expand((double)var15, (double)var15, (double)var15);
                MovingObjectPosition var17 = var16.calculateIntercept(var6, var8);

                if (var16.isVecInside(var6))
                {
                    if (0.0D < var11 || var11 == 0.0D)
                    {
                        pointedEntity = var14;
                        var11 = 0.0D;
                    }
                }
                else if (var17 != null)
                {
                    double var18 = var6.distanceTo(var17.hitVec);

                    if (var18 < var11 || var11 == 0.0D)
                    {
                        pointedEntity = var14;
                        var11 = var18;
                    }
                }
            }
        }

        if(pointedEntity != null && ep.canEntityBeSeen(pointedEntity))
        	return pointedEntity;
		}catch(Exception e){}
        return null;
	}

	
	public synchronized void middleClickBlock(EntityLivingBase wearer, ItemStack item){
		MovingObjectPosition click = DustMod.getWornInscription().getMovingObjectPositionFromPlayer(wearer.worldObj, (EntityPlayer)wearer, false);
		if(click != null && click.typeOfHit == MovingObjectType.BLOCK){
			int tx = click.blockX;
			int ty = click.blockY;
			int tz = click.blockZ;
			World world = wearer.worldObj;
			
			if(isCloseToMovingBlock(item,tx,ty,tz)){

				int[] bPos = this.getMovingBlock(item);
				
				//ConvertToCobble
				for(int i = 1; i >= -1; i--){
					for(int j = 1; j >= -1; j--){
						for(int k = 1; k >= -1; k--){
							Block block = world.getBlock(bPos[0]+i, bPos[1]+j, bPos[2]+k);
							int meta = world.getBlockMetadata(bPos[0]+i, bPos[1]+j, bPos[2]+k);
							world.setBlock(bPos[0]+i, bPos[1]+j, bPos[2]+k,getCompressedMaterial(block,meta),0,3);
						}
					}
				}
			}
		}
	}
	
	public void fire(EntityLivingBase wearer, ItemStack item){

//		boolean hasTarget = false;
//		double tx,ty,tz;
		
		Entity target = getLaunchEnt((EntityPlayer)wearer);
//		if(target != null){
//			tx = target.posX;
//			ty = target.posY;
//			tz = target.posZ;
//			hasTarget = true;
//		}else {
//			MovingObjectPosition click = DustMod.getWornInscription().getMovingObjectPositionFromPlayer(wearer.worldObj, (EntityPlayer)wearer, true);
//			if(click != null){
//				tx = click.blockX + 0.5;
//				ty = click.blockY + 0.5;
//				tz = click.blockZ + 0.5;
//				hasTarget = true;
//			}
//		}
		World world = wearer.worldObj;
		double speed = 3F;
		int[] bPos = this.getMovingBlock(item);
		
		for(int i = 1; i >= -1; i--){
			for(int j = 1; j >= -1; j--){
				for(int k = 1; k >= -1; k--){
					int x = bPos[0]+i;
					int y = bPos[1]+j;
					int z = bPos[2]+k;
//					if(i != 0 || j != 0 || k != 0) {
//						world.setBlockWithNotify(x,y,z,0);
//						continue;
//					}
					
					Block block = world.getBlock(x,y,z);
					if (isCompressedMaterial(block)) {
						
						
						//EntityArrow arrow = new EntityArrow(world,x+0.5, y+0.5, z+0.5);
//						EntityFallingSand block = new EntityFallingSand(world,(double)x+0.5,(double)y+0.5,(double)z+0.5,bid);
						Vec3 vel;
						if(target == null){
							vel = wearer.getLookVec();
						} else {
							vel = Vec3.createVectorHelper(target.posX - (x+0.5), target.posY - (y+0.5) + 1.5, target.posZ - (z+0.5));
						}
						double randSpeed = speed*(Math.random()+0.6);
						vel = vel.normalize();
						vel.xCoord = vel.xCoord*speed;
						vel.yCoord = vel.yCoord*speed;
						vel.zCoord = vel.zCoord*speed;
//						arrow.setVelocity(vel.xCoord,vel.yCoord,vel.zCoord);
//						block.setVelocity(vel.xCoord/1,vel.yCoord/1,vel.zCoord/1);
//						block.noClip = true;
//						world.spawnEntityInWorld(block);
						world.setBlockToAir(x,y,z);
						

						EntityBlock eb = new EntityBlock(world,(double)x+0.5,(double)y+0.5,(double)z+0.5,block);
						eb.setThrowing(true);
//						eb.noClip = true;
						eb.motionX = vel.xCoord/1;
						eb.motionY = vel.yCoord/1;
						eb.motionZ = vel.zCoord/1;
			            eb.updateDataWatcher();
			            world.spawnEntityInWorld(eb);
//						world.spawnEntityInWorld(arrow);
					}
				}
			}
		}
	}
	
	public synchronized void blockClick(EntityLivingBase wearer, ItemStack item){
		MovingObjectPosition click = DustMod.getWornInscription().getMovingObjectPositionFromPlayer(wearer.worldObj, (EntityPlayer)wearer, false);
		if(click != null && click.typeOfHit == MovingObjectType.BLOCK){
			int tx = click.blockX;
			int ty = click.blockY;
			int tz = click.blockZ;
			World world = wearer.worldObj;
			
			if(isCloseToMovingBlock(item,tx,ty,tz)){
				moveBlock(world,item,tx,ty,tz,click.sideHit);
			}else {
				double tol = 6.5D;
				double dist = wearer.getDistanceSq(tx+0.5, ty, tz+0.5);
				if(dist < tol*tol){
					int h = 4;
					int ax=0,ay=0,az=0;
					int mx=0,my=0,mz=0;
					
					switch(click.sideHit){
					case 0: //y--
						ay = 1;
						my = -h;
						break;
					case 1: //y++
						ay = -1;
						my = h;
						break; 
					case 2: //z--
						az = 1;
						mz = -h;
						break;
					case 3://z++
						az = -1;
						mz = h;
						break;
					case 4://x--
						ax = 1;
						mx = -h;
						break;
					case 5://x++
						ax = -1;
						mx = h;
						break;
					}
					for(int i = 1+ax; i >= -1+ax; i--){
						for(int j = 1+ay; j >= -1+ay; j--){
							for(int k = 1+az; k >= -1+az; k--){
								Block block = world.getBlock(tx+i, ty+j, tz+k);
								int meta = world.getBlockMetadata(tx+i, ty+j, tz+k);
								
								if(j == -1+ay && block instanceof BlockFalling){
									block = Blocks.sandstone;
									meta = 0;
								}
								
								world.setBlock(tx+i+mx, ty+j+my, tz+k+mz,block,meta,3);
								world.setBlockToAir(tx+i, ty+j, tz+k);
							}
						}
					}
					setMovingBlock(item, tx+ax+mx,ty+ay+my,tz+az+mz);
				}
			}
		}
	}
	
	public void setMovingBlock(ItemStack item, int x, int y, int z){
		NBTTagCompound tag = item.getTagCompound();
		tag.setInteger("blockx",x);
		tag.setInteger("blocky",y);
		tag.setInteger("blockz",z);
	}
	
	public boolean isCloseToMovingBlock(ItemStack item, int x, int y, int z){
		int[] mov =  getMovingBlock(item);
		
		int dx,dy,dz;
		dx = Math.abs(mov[0] - x);
		dy = Math.abs(mov[1] - y);
		dz = Math.abs(mov[2] - z);
		int dif = dx+dy+dz;
		if(dif <= 3) return true;
		return false;
	}
	
	public int[] getMovingBlock(ItemStack item){
		int[] rtn = new int[3];
		
		NBTTagCompound tag = item.getTagCompound();
		rtn[0] = tag.getInteger("blockx");
		rtn[1] = tag.getInteger("blocky");
		rtn[2] = tag.getInteger("blockz");
		
		return rtn;
	}
	
	public void moveBlock(World world, ItemStack item, int clickx, int clicky, int clickz, int face){
		int[] bPos = getMovingBlock(item);
		int dx, dy, dz;
		
		dx = bPos[0] - clickx;
		dy = bPos[1] - clicky;
		dz = bPos[2] - clickz;
		boolean isEdge = (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) > 1);
		
		System.out.println("FACE " + face + " [" + dx + "," + dy + "," + dz + "]");
		
		int ax=0,ay=0,az=0;
		
		switch(face){
		case 0: //y--
			if(!isEdge) ay = dy;
			else {
				ax = -dx;
				az = -dz;
			}
			break;
		case 1: //y++
			if(!isEdge) ay = dy;
			else {
				ax = -dx;
				az = -dz;
			}
			break; 
		case 2: //z--
			if(!isEdge) az = dz;
			else {
				ax = -dx;
				ay = -dy;
			}
			break;
		case 3://z++
			if(!isEdge) az = dz;
			else {
				ax = -dx;
				ay = -dy;
			}
			break;
		case 4://x--
			if(!isEdge) ax = dx;
			else {
				az = -dz;
				ay = -dy;
			}
			break;
		case 5://x++
			if(!isEdge) ax = dx;
			else {
				az = -dz;
				ay = -dy;
			}
			break;
		}
		
		setMovingBlock(item,bPos[0] + ax, bPos[1] + ay, bPos[2] + az);

		//XXX WTH
		//int[][][][] blocks = new int[3][3][3][2];
		for(int i = 1; i >= -1; i--){
			for(int j = 1; j >= -1; j--){
				for(int k = 1; k >= -1; k--){
					Block block = world.getBlock(bPos[0]+i, bPos[1]+j, bPos[2]+k);
					int meta = world.getBlockMetadata(bPos[0]+i, bPos[1]+j, bPos[2]+k);
					//blocks[i+1][j+1][k+1][0] = bid;
					//blocks[i+1][j+1][k+1][1] = meta;
//					world.setBlock(tx+i, ty+j+h, tz+k,bid,meta);
					world.setBlockToAir(bPos[0]+i, bPos[1]+j, bPos[2]+k);
					world.setBlock(bPos[0]+i+ax, bPos[1]+j+ay, bPos[2]+k+az,block,meta,3);
				}
			}
		}/*
		for(int i = 1; i >= -1; i--){
			for(int j = 1; j >= -1; j--){
				for(int k = 1; k >= -1; k--){
					int bid = blocks[i+1][j+1][k+1][0];
					int meta = blocks[i+1][j+1][k+1][1];
					world.setBlock(bPos[0]+i+ax, bPos[1]+j+ay, bPos[2]+k+az,bid,meta,3);
				}
			}
		}*/
	}
	public boolean mouseWasDownR(ItemStack item){
		return item.getTagCompound().getBoolean("wasRDown");
	}
	public void setMouseWasDownR(ItemStack item, boolean val){
		item.getTagCompound().setBoolean("wasRDown",val);
	}
	
}
