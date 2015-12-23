package dustmod.runes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import dustmod.DustMod;
import dustmod.blocks.TileEntityDust;
import dustmod.blocks.TileEntityRut;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.server.management.UserListOps;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

/**
 * 
 * @author billythegoat101
 */
public abstract class RuneEvent {
	
	protected ArrayList<ArrayList<Sacrifice>> waitingSacrifices;
	private int sacID = 0;
	public String name;
	public boolean secret = false;
	public String permission = "ALL";
	
	// Set by the coder, unalterable by user configs.
	// Setting it to false disallows all use, but if true it will not affect anything
	// This is for the case where something breaks, it gives the coder time to fix it later
	public boolean permaAllowed = true;
	
	public RuneEvent() {
		waitingSacrifices = new ArrayList<ArrayList<Sacrifice>>();
	}
	
	public RuneEvent addSacr(Sacrifice sacrifice) {
		if (sacID >= waitingSacrifices.size()) {
			waitingSacrifices.add(new ArrayList<Sacrifice>());
		}
		
		waitingSacrifices.get(sacID).add(sacrifice);
		return this;
	}
	
	public RuneEvent addSacrificeList(Sacrifice... sacrifices) {
		List<Sacrifice> sac = Arrays.asList(sacrifices);
		
		if (sacID >= waitingSacrifices.size()) {
			waitingSacrifices.add(new ArrayList<Sacrifice>());
		}
		
		ArrayList<Sacrifice> current = waitingSacrifices.get(sacID);
		current.addAll(sac);
		sacID++;
		return this;
	}
	
	public RuneEvent shiftSacr() {
		sacID++;
		return this;
	}
	
	public final void init(EntityRune entityRune) {
		onInit(entityRune);
	}
	
	protected void initGraphics(EntityRune entityRune) {
		
	}
	
	protected void onInit(EntityRune entityRune) {
	}
	
	public final void tick(EntityRune entityRune) {
		if (entityRune.sacrificeWaiting > 0) {
			entityRune.sacrificeWaiting--;
			
			for (ArrayList<Sacrifice> arr : waitingSacrifices) {
				Sacrifice[] sacr = sacrifice(entityRune, arr);
				boolean cont = true;
				
				for (Sacrifice s : sacr) {
					if (!s.isComplete) {
						cont = false;
						break;
					}
				}
				
				if (cont) {
					handle(entityRune, sacr);
					entityRune.sacrificeWaiting = -1;
				}
			}
		} else if (entityRune.sacrificeWaiting == 0) {
			//			System.out.println("Waiting sacrifice = 0 death");
			entityRune.fizzle();
			return;
		} else {
			onTick(entityRune);
		}
	}
	
	protected void onTick(EntityRune entityRune) {
	}
	
	public void onRightClick(EntityRune entityRune, TileEntityDust ted, EntityPlayer p) {
	}
	
	public final void unload(EntityRune entityRune) {
		onUnload(entityRune);
	}
	
	protected void onUnload(EntityRune entityRune) {
		if (entityRune.rutPoints != null) {
			for (Integer[] rutPoint : entityRune.rutPoints) {
				TileEntityRut tileEntityRut = (TileEntityRut) entityRune.worldObj.getTileEntity(rutPoint[0], rutPoint[1], rutPoint[2]);
				if (tileEntityRut != null) {
					tileEntityRut.isBeingUsed = false;
				}
			}
		}
	}
	
	/**
	 * Will the server send this rune's info to the player
	 * 
	 * @param player
	 *            the player
	 * @return true if can see, false otherwise
	 */
	public boolean canPlayerKnowRune(UUID playerId) {
		boolean isOP = false;
		
		if (playerId != null) {
			try {
				MinecraftServer server = MinecraftServer.getServer();
				ServerConfigurationManager manager = server.getConfigurationManager();
				UserListOps ops = manager.func_152603_m();
				// TODO optimize this?
				if (ops != null) {
					GameProfile[] profiles = manager.func_152600_g();
					for (GameProfile profile : profiles) {
						if (profile.getId() != null && profile.getId().equals(playerId)) {
							isOP = (ops.func_152700_a(profile.getName()) != null);
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return permaAllowed && (this.permission.equals("ALL") || (this.permission.equals("OPS") && isOP));
	}
	
	/**
	 * Take experience levels from the closest nearby player.
	 * 
	 * @param e
	 *            EntityDust instance
	 * @param levels
	 *            number of levels to take
	 * @return True if levels can be (and have been) taken, false if player doesn't have enough
	 */
	public boolean takeXP(EntityRune entityRune, int levels) {
		EntityPlayer player = entityRune.worldObj.getClosestPlayerToEntity(entityRune, 12D);
		if (player == null) {
			return false;
		}
		if (player.capabilities.isCreativeMode) {
			return true;
		}
		
		if (player.experienceLevel >= levels) {
			player.addExperienceLevel(-levels);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Take hunger bars from the closest nearby player
	 * 
	 * @param entityRune
	 *            EntityDust instance
	 * @param halves
	 *            Number of bars to take (in halves)
	 * @return True if bars can be (and have been) taken, false if player doesn't have enough
	 */
	public boolean takeHunger(EntityRune entityRune, int halves) {
		EntityPlayer player = entityRune.worldObj.getClosestPlayerToEntity(entityRune, 12D);
		if (player == null) {
			return false;
		}
		if (player.capabilities.isCreativeMode) {
			return true;
		}
		
		if (player.getFoodStats().getFoodLevel() >= halves) {
			player.getFoodStats().addStats(-halves, 0);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Takes hearts away from the nearest player (in terms of half-hearts)
	 * 
	 * @param entityRune
	 *            EntityDust instance
	 * @param halves
	 *            Number of halves of hearts to take.
	 * @param kill
	 *            Whether to kill the player if they don't have enough hearts
	 * @return True if the closest player has enough hearts and they have been taken.
	 */
	public boolean takeLife(EntityRune entityRune, int halves, boolean kill) {
		EntityPlayer player = entityRune.worldObj.getClosestPlayerToEntity(entityRune, 12D);
		if (player == null) {
			return false;
		}
		if (player.capabilities.isCreativeMode) {
			return true;
		}
		
		if (player.getHealth() >= halves) {
			player.attackEntityFrom(DamageSource.magic, halves);
			return true;
		} else if (kill) {
			player.attackEntityFrom(DamageSource.magic, halves);
		}
		
		return false;
	}
	
	/**
	 * Loop through a list of ItemStacks and see if they are all empty. Useful to see if the sacrifice has been fulfilled.
	 * 
	 * @param req
	 *            The array of ItemStacks
	 * @return True if all items in the array have stackSizes <= 0, false if any are >0
	 */
	public boolean checkSacrifice(ItemStack[] req) {
		for (ItemStack itemStack : req) {
			if (itemStack.stackSize > 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public List<Entity> getEntities(World world, double x, double y, double z) {
		return getEntities(world, x, y, z, 1D);
	}
	
	/**
	 * Get all entities within 1 block of the given Entity's position
	 * 
	 * @param entity
	 *            The entity to look around
	 * @return A list of all entities within 1 block of the given entity's position (including the entity itself)
	 */
	public List<Entity> getEntities(Entity entity) {
		return getEntities(entity.worldObj, entity.posX, entity.posY - entity.yOffset, entity.posZ, 1D);
	}
	
	/**
	 * Get all entities within a radius of the given Entity's position
	 * 
	 * @param entity
	 *            The entity to look around
	 * @param radius
	 *            The radius to look around
	 * @return A list containing all entities within the radius of the given Entity's position (including the entity itself)
	 */
	public List<Entity> getEntities(Entity entity, double radius) {
		return getEntities(entity.worldObj, entity.posX, entity.posY - entity.yOffset, entity.posZ, radius);
	}
	
	/**
	 * Get all entities within a given radius of the coordinates
	 * 
	 * @param world
	 *            The current world to check in
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @param z
	 *            Z coordinate
	 * @param radius
	 *            The radius around the location to check
	 * @return A list containing all entities within the radius of the coordinates
	 */
	@SuppressWarnings("unchecked")
	public List<Entity> getEntities(World world, double x, double y, double z, double radius) {
		List<Entity> l = world.getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D).expand(radius, radius, radius));
		return l;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Entity> List<? extends T> getEntities(World world, Class<T> entType, double x, double y, double z, double radius) {
		List<? extends T> l = world.getEntitiesWithinAABB(entType, AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D).expand(radius, radius, radius));
		return l;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Entity> List<? extends T> getEntities(World world, Class<T> entType, double x, double y, double z, double radius, IEntitySelector selector) {
		List<? extends T> l = world.selectEntitiesWithinAABB(entType, AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D).expand(radius, radius, radius), selector);
		return l;
	}
	
	@SuppressWarnings("rawtypes")
	public List getEntitiesExcluding(World world, Entity e, double x, double y, double z, double radius) {
		List l = world.getEntitiesWithinAABBExcludingEntity(e, AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D).expand(radius, radius, radius));
		// System.out.println("Retrieving entities " +
		// world.worldProvider.worldType + " [" + x + "," + y + "," + z + "] " +
		// l.size());
		return l;
	}
	
	/**
	 * Get a list of all dropped items within a 1 block radius of the EntityDust
	 * 
	 * @param e
	 *            The EntityDust instance
	 * @return a List<EntityItem> of all nearby dropped items
	 */
	public final List<EntityItem> getItems(EntityRune entityRune) {
		return getItems(entityRune, 1D);
	}
	
	/**
	 * Get a list of all dropped items within a given radius of the EntityDust
	 * 
	 * @param entityRune
	 *            The EntityDust instance
	 * @param radius
	 *            The radius in which to check for dropped items
	 * @return A List<EntityItem> of all nearby dropped items.
	 */
	@SuppressWarnings("rawtypes")
	public final List<EntityItem> getItems(EntityRune entityRune, double radius) {
		ArrayList<EntityItem> itemstacks = new ArrayList<EntityItem>();
		List<Entity> entities = getEntities(entityRune.worldObj, entityRune.posX, entityRune.posY - entityRune.yOffset, entityRune.posZ, radius);
		
		for (Entity entity : entities) {
			if (entity instanceof EntityItem) {
				EntityItem entityItem = (EntityItem) entity;
				itemstacks.add(entityItem);
			}
		}
		
		return itemstacks;
	}
	
	/**
	 * Checks around the rune for the listed items and destroys them. Returns true if all listed items are found
	 * 
	 * @param entityRune
	 *            EntityDust instance
	 * @param itemStacks
	 *            ItemStacks to look for
	 * @return True if all items found and consumed
	 */
	public final boolean takeItems(EntityRune entityRune, ItemStack... itemStacks) {
		List<EntityItem> sacrifice = getItems(entityRune);
		
		for (EntityItem entityItem : sacrifice) {
			for (ItemStack item : itemStacks) {
				ItemStack itemStack = entityItem.getEntityItem();
				if (itemStack.getItem() == DustMod.negateSacrifice) {
					return true;
				}
				
				if (itemStack.getItem() == item.getItem() && (item.getItemDamage() == -1 || entityItem.getEntityItem().getItemDamage() == item.getItemDamage())) {
					if (itemStack.stackSize <= item.stackSize && item.stackSize > 0) {
						item.stackSize -= itemStack.stackSize;
						entityItem.setDead();
					} else {
						itemStack.stackSize -= item.stackSize;
						entityItem.setEntityItemStack(itemStack);
						break;
					}
				}
			}
		}
		
		for (ItemStack itemStack : itemStacks) {
			if (itemStack.stackSize > 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public void registerFollower(EntityRune entityRune, Object o) {
	}
	
	public ItemStack[] sacrifice(EntityRune entityRune, ItemStack[] req) {
		List<EntityItem> sacrifice = getItems(entityRune);
		boolean negate = false;
		
		for (EntityItem entityItem : sacrifice) {
			ItemStack itemStackSacrifice = entityItem.getEntityItem();
			
			if (itemStackSacrifice.getItem() == DustMod.negateSacrifice) {
				negate = true;
				break;
			}
			
			for (ItemStack itemStackRequirement : req) {
				if (  itemStackRequirement.getItem() == itemStackSacrifice.getItem()
				  && (itemStackRequirement.getItemDamage() == -1 || itemStackRequirement.getItemDamage() == itemStackSacrifice.getItemDamage())
				  && (itemStackRequirement.getItemDamage() == -1 || itemStackRequirement.hasTagCompound() == itemStackSacrifice.hasTagCompound()) ) {
					boolean match = false;
					
					if (itemStackRequirement.hasTagCompound() && itemStackRequirement.getItemDamage() != -1) {
						
						match = itemStackRequirement.getTagCompound().equals(itemStackSacrifice.getTagCompound());
						NBTTagCompound cTag = itemStackRequirement.getTagCompound();
						NBTTagCompound isTag = itemStackSacrifice.getTagCompound();
						if (!cTag.equals(isTag)) {
							match = false;
						}
						/*
						if(cTag.getTags().size() == isTag.getTags().size()){
							DustMod.log("check4");
							match = true;
							Object[] cCol = cTag.getTags().toArray();
							Object[] isCol = isTag.getTags().toArray();
							for(int t = 0; t < cCol.length; t++){
								if(!cCol[t].equals(isCol[t])){
									match = false;
									DustMod.log("swabby", cCol[t], isCol[t]);
									break;
								}
							}
						}else{
							DustMod.log("wat",cTag.getTags().size(),isTag.getTags().size());
						}*/
					} else {
						match = true;
					}
					while (match && itemStackRequirement.stackSize > 0 && itemStackSacrifice.stackSize > 0) {
						itemStackSacrifice.stackSize--;
						itemStackRequirement.stackSize--;
					}
				}
			}
			
			if (itemStackSacrifice.stackSize <= 0) {
				entityItem.setDead();
			} else {
				double rx, rz;
				double r = 0.1;
				double min = 0.065;
				rx = Math.random() * r * 2 - r;
				rx += (rx < 0) ? -min : min;
				rz = Math.random() * r * 2 - r;
				rz += (rz < 0) ? -min : min;
				entityItem.addVelocity(rx, Math.random() * 0.5, rz);
			}
			entityItem.setEntityItemStack(itemStackSacrifice);
		}
		
		if (negate) {
			for (ItemStack itemStackRequirement : req) {
				itemStackRequirement.stackSize = 0;
			}
		}
		
		return req;
	}
	
	public Sacrifice[] sacrifice(EntityRune entityRune, ArrayList<Sacrifice> reqA) {
		List<Entity> sacrifice = this.getEntities(entityRune, 3D);
		Sacrifice[] req = new Sacrifice[reqA.size()];
		
		for (int i = 0; i < reqA.size(); i++) {
			Sacrifice is = reqA.get(i);
			req[i] = is.clone();
		}
		
		boolean negate = false;
		
		for (Entity entity : sacrifice) {
			EntityItem entityItem = null;
			
			if (entity instanceof EntityItem) {
				entityItem = (EntityItem) entity;
			}
			
			if (entityItem != null && entityItem.getEntityItem().getItem() == DustMod.negateSacrifice) {
				negate = true;
				break;
			}
			
			for (Sacrifice sacrificeRequired : req) {
				if (sacrificeRequired.isComplete) {
					continue;
				}
				
				if (!(entity instanceof EntityPlayer || entity instanceof EntityRune) && sacrificeRequired.matchObject(entity)) {
					if (entityItem != null) {
						sacrificeRequired.itemType.stackSize -= entityItem.getEntityItem().stackSize;
						
						if (sacrificeRequired.itemType.stackSize == 0) {
							sacrificeRequired.isComplete = true;
						}
					} else {
						sacrificeRequired.isComplete = true;
					}
					
					System.out.println("Sacrifice matched to " + entityItem + " " + sacrificeRequired.isComplete);
					sacrificeRequired.entity = entity;
					break;
				}
			}
		}
		
		if (negate)
			for (Sacrifice sacrificeRequired : req) {
				sacrificeRequired.isComplete = true;
			}
		
		return req;
	}
	
	public void handle(EntityRune entityRune, Sacrifice[] sacrifices) {
		for (Sacrifice sacrifice : sacrifices) {
			sacrifice.handleObject(entityRune, sacrifice.entity);
		}
	}
	
	protected final void findRuts(EntityRune entityRune) {
		World world = entityRune.worldObj;
		
		ArrayList<Integer[]> ruts = new ArrayList<Integer[]>();
		
		for (Integer[] dustPoint : entityRune.dustPoints) {
			ruts.add(new Integer[] { dustPoint[0], dustPoint[1] - 1, dustPoint[2] });
			checkNeighbors(world, ruts, dustPoint[0], dustPoint[1] - 1, dustPoint[2]);
		}
		
		entityRune.rutPoints = ruts;
	}
	
	protected final void findRutsWithDistance(EntityRune entityRune, int distance) {
		World world = entityRune.worldObj;
		
		ArrayList<Integer[]> ruts = new ArrayList<Integer[]>();
		
		for (Integer[] dustPoint : entityRune.dustPoints) {
			ruts.add(new Integer[] { dustPoint[0], dustPoint[1] - 1, dustPoint[2] });
			checkNeighborsWithDistance(world, ruts, dustPoint[0], dustPoint[1] - 1, dustPoint[2], distance - 1);
		}
		
		entityRune.rutPoints = ruts;
	}
	
	protected final void findRuts(EntityRune entityRune, Block fluid) {
		World w = entityRune.worldObj;
		
		ArrayList<Integer[]> ruts = new ArrayList<Integer[]>();
		
		for (Integer[] dustPoint : entityRune.dustPoints) {
			ruts.add(new Integer[] { dustPoint[0], dustPoint[1] - 1, dustPoint[2] });
			checkNeighbors(w, ruts, dustPoint[0], dustPoint[1] - 1, dustPoint[2], fluid);
		}
		
		entityRune.rutPoints = ruts;
	}
	
	protected final boolean findRutArea(EntityRune entityRune) {
		List<Integer[]> horiz = new ArrayList<Integer[]>();
		List<Integer[]> length = new ArrayList<Integer[]>();
		List<Integer[]> vert = new ArrayList<Integer[]>();
		List<Integer[]> farea = new ArrayList<Integer[]>();
		List<Integer[]> ruts = entityRune.rutPoints;
		
		if (ruts == null) {
			findRuts(entityRune);
			ruts = entityRune.rutPoints;
			
			if (ruts == null) {
				return false;
			}
		}
		
		int rutSize = ruts.size();
		
		for (int i = 0; i < rutSize; i++) {
			Integer[] p = ruts.get(i);
			int ix = p[0];
			int iy = p[1];
			int iz = p[2];
			
			for (int j = 0; j < rutSize; j++) {
				if (i != j) {
					Integer[] t = ruts.get(j);
					int jx = t[0];
					int jy = t[1];
					int jz = t[2];
					
					if (iy == jy && ix == jx && jz > iz) {
						int dist = jz - iz;
						
						for (int l = 1; l < dist; l++) {
							Integer[] a = new Integer[] { jx, jy, iz + l };
							length.add(a);
						}
					}
					
					if (iy == jy && iz == jz && jx > ix) {
						int dist = jx - ix;
						
						for (int h = 1; h < dist; h++) {
							Integer[] a = new Integer[] { ix + h, jy, jz };
							horiz.add(a);
						}
					}
					
					if (ix == jx && iz == jz && jy > iy) {
						int dist = jy - iy;
						
						for (int v = 1; v < dist; v++) {
							Integer[] a = new Integer[] { jx, iy + v, jz };
							vert.add(a);
						}
					}
				}
			}
		}
		
		// check for confirmation
		for (Integer[] h : horiz) {
			int hx = h[0];
			int hy = h[1];
			int hz = h[2];
			boolean get = false;
			
			for (Integer[] v : vert) {
				int vx = v[0];
				int vy = v[1];
				int vz = v[2];
				
				if (hx == vx && hy == vy && hz == vz) {
					farea.add(new Integer[] { vx, vy, vz });
					get = true;
				}
			}
			
			if (!get) {
				next: for (Integer[] l : length) {
					int lx = l[0];
					int ly = l[1];
					int lz = l[2];
					
					if (hx == lx && hy == ly && hz == lz) {
						farea.add(new Integer[] { lx, ly, lz });
						// farea.add(new Integer[]{lx,ly+3,lz});
						// farea.add(new Integer[]{hx,hy+1,hz});
						get = true;
						break next;
					}
				}
			}
		}
		
		for (Integer[] v : vert) {
			int vx = v[0];
			int vy = v[1];
			int vz = v[2];
			boolean get = false;
			
			for (Integer[] h : horiz) {
				int hx = h[0];
				int hy = h[1];
				int hz = h[2];
				
				if (vx == hx && vy == hy && vz == hz) {
					farea.add(new Integer[] { hx, hy, hz });
					get = true;
				}
			}
			
			if (!get)
				for (Integer[] l : length) {
					int lx = l[0];
					int ly = l[1];
					int lz = l[2];
					
					if (vx == lx && vy == ly && vz == lz) {
						farea.add(new Integer[] { lx, ly, lz });
						get = true;
					}
				}
		}
		
		for (Integer[] l : length) {
			int lx = l[0];
			int ly = l[1];
			int lz = l[2];
			boolean get = false;
			
			for (Integer[] v : vert) {
				int vx = v[0];
				int vy = v[1];
				int vz = v[2];
				
				if (vx == lx && vy == ly && vz == lz) {
					farea.add(new Integer[] { vx, vy, vz });
					get = true;
				}
			}
			
			if (!get)
				for (Integer[] h : horiz) {
					int hx = h[0];
					int hy = h[1];
					int hz = h[2];
					
					if (lx == hx && ly == hy && lz == hz) {
						farea.add(new Integer[] { hx, hy, hz });
						get = true;
					}
				}
		}
		
		// farea = ruts;
		// cleanup because I'm not smart enough to do it right the first time
		List<Integer> remove = new ArrayList<Integer>();
		next:
		
		for (int i = 0; i < farea.size(); i++) {
			Integer[] ii = farea.get(i);
			int ix = ii[0];
			int iy = ii[1];
			int iz = ii[2];
			
			for (Integer r : remove) {
				if (r == i) {
					continue next;
				}
			}
			
			for (int j = 0; j < farea.size(); j++) {
				if (i != j) {
					Integer[] ji = farea.get(j);
					int jx = ji[0];
					int jy = ji[1];
					int jz = ji[2];
					
					if (ix == jx && iy == jy && iz == jz) {
						remove.add(j);
					}
				}
			}
			
			remRut:
			
			for (Integer[] rut : ruts) {
				int rx = rut[0];
				int ry = rut[1];
				int rz = rut[2];
				
				if (rx == ix && ry == iy && rz == iz) {
					remove.add(i);
					break remRut;
				}
			}
		}
		
		List<Integer[]> temp = new ArrayList<Integer[]>();
		next:
		
		for (int i = 0; i < farea.size(); i++) {
			for (Integer r : remove) {
				if (r.equals(i)) {
					continue next;
				}
			}
			
			temp.add(farea.get(i));
		}
		
		// System.out.println("Area : " + farea.size() + " " + temp.size() + " " + remove.size());
		farea = temp;
		// if(farea.size() > 10000) return false;
		entityRune.rutAreaPoints = farea;
		return true;
	}
	
	protected final boolean findRutArea(EntityRune entityRune, Block fluid) {
		List<Integer[]> horiz = new ArrayList<Integer[]>();
		List<Integer[]> length = new ArrayList<Integer[]>();
		List<Integer[]> vert = new ArrayList<Integer[]>();
		List<Integer[]> farea = new ArrayList<Integer[]>();
		List<Integer[]> ruts = entityRune.rutPoints;
		
		if (ruts == null) {
			findRuts(entityRune, fluid);
			ruts = entityRune.rutPoints;
			
			if (ruts == null) {
				return false;
			}
		}
		
		int rutSize = ruts.size();
		
		for (int i = 0; i < rutSize; i++) {
			Integer[] p = ruts.get(i);
			int ix = p[0];
			int iy = p[1];
			int iz = p[2];
			
			for (int j = 0; j < rutSize; j++) {
				if (i != j) {
					Integer[] t = ruts.get(j);
					int jx = t[0];
					int jy = t[1];
					int jz = t[2];
					
					if (iy == jy && ix == jx && jz > iz) {
						int dist = jz - iz;
						
						for (int l = 1; l < dist; l++) {
							Integer[] a = new Integer[] { jx, jy, iz + l };
							length.add(a);
						}
					} else if (iy == jy && iz == jz && jx > ix) {
						int dist = jx - ix;
						
						for (int h = 1; h < dist; h++) {
							Integer[] a = new Integer[] { ix + h, jy, jz };
							horiz.add(a);
						}
					} else if (ix == jx && iz == jz && jy > iy) {
						int dist = jy - iy;
						
						for (int v = 1; v < dist; v++) {
							Integer[] a = new Integer[] { jx, iy + v, jz };
							vert.add(a);
						}
					}
				}
			}
		}
		
		// check for confirmation
		for (Integer[] h : horiz) {
			int hx = h[0];
			int hy = h[1];
			int hz = h[2];
			boolean get = false;
			
			for (Integer[] v : vert) {
				int vx = v[0];
				int vy = v[1];
				int vz = v[2];
				
				if (hx == vx && hy == vy && hz == vz) {
					farea.add(v);
					get = true;
				}
			}
			
			if (!get)
				for (Integer[] l : length) {
					int lx = l[0];
					int ly = l[1];
					int lz = l[2];
					
					if (hx == lx && hy == ly && hz == lz) {
						farea.add(l);
						// farea.add(new Integer[]{lx,ly+3,lz});
						// farea.add(new Integer[]{hx,hy+1,hz});
						get = true;
					}
				}
		}
		
		for (Integer[] v : vert) {
			int vx = v[0];
			int vy = v[1];
			int vz = v[2];
			boolean get = false;
			
			for (Integer[] h : horiz) {
				farea.add(h);
				get = true;
			}
			
			if (!get)
				for (Integer[] l : length) {
					int lx = l[0];
					int ly = l[1];
					int lz = l[2];
					
					if (vx == lx && vy == ly && vz == lz) {
						farea.add(l);
						get = true;
					}
				}
		}
		
		for (Integer[] l : length) {
			int lx = l[0];
			int ly = l[1];
			int lz = l[2];
			boolean get = false;
			
			for (Integer[] v : vert) {
				int vx = v[0];
				int vy = v[1];
				int vz = v[2];
				
				if (vx == lx && vy == ly && vz == lz) {
					farea.add(v);
					get = true;
				}
			}
			
			if (!get)
				for (Integer[] h : horiz) {
					int hx = h[0];
					int hy = h[1];
					int hz = h[2];
					
					if (lx == hx && ly == hy && lz == hz) {
						farea.add(h);
						get = true;
					}
				}
		}
		
		// farea = ruts;
		// cleanup because I'm not smart enough to do it right the first time
		List<Integer> remove = new ArrayList<Integer>();
		next:
		
		for (int i = 0; i < farea.size(); i++) {
			Integer[] ii = farea.get(i);
			int ix = ii[0];
			int iy = ii[1];
			int iz = ii[2];
			
			for (Integer r : remove) {
				if (r == i) {
					continue next;
				}
			}
			
			for (int j = 0; j < farea.size(); j++) {
				if (i != j) {
					Integer[] ji = farea.get(j);
					int jx = ji[0];
					int jy = ji[1];
					int jz = ji[2];
					
					if (ix == jx && iy == jy && iz == jz) {
						remove.add(j);
					}
				}
			}
			
			remRut:
			
			for (Integer[] rut : ruts) {
				int rx = rut[0];
				int ry = rut[1];
				int rz = rut[2];
				
				if (rx == ix && ry == iy && rz == iz) {
					remove.add(i);
					break remRut;
				}
			}
		}
		
		List<Integer[]> temp = new ArrayList<Integer[]>();
		next:
		
		for (int i = 0; i < farea.size(); i++) {
			for (Integer r : remove) {
				if (r.equals(i)) {
					continue next;
				}
			}
			
			temp.add(farea.get(i));
		}
		
		// System.out.println("Area : " + farea.size() + " " + temp.size() + " " + remove.size());
		farea = temp;
		// if(farea.size() > 10000) return false;
		entityRune.rutAreaPoints = farea;
		return true;
	}
	
	protected final boolean findRutAreaFlat(EntityRune entityRune, Block fluid) {
		List<Integer[]> horiz = new ArrayList<Integer[]>();
		List<Integer[]> length = new ArrayList<Integer[]>();
		// List<Integer[]> vert = new ArrayList<Integer[]>();
		List<Integer[]> farea = new ArrayList<Integer[]>();
		List<Integer[]> ruts = entityRune.rutPoints;
		
		if (ruts == null) {
			findRuts(entityRune, fluid);
			ruts = entityRune.rutPoints;
			
			if (ruts == null) {
				return false;
			}
		}
		
		int rutSize = ruts.size();
		
		for (int i = 0; i < rutSize; i++) {
			Integer[] p = ruts.get(i);
			int ix = p[0];
			int iz = p[2];
			
			for (int j = 0; j < rutSize; j++) {
				if (i != j) {
					Integer[] t = ruts.get(j);
					int jx = t[0];
					int jz = t[2];
					
					if (ix == jx && jz > iz) {
						int dist = jz - iz;
						
						for (int l = 1; l < dist; l++) {
							Integer[] a = new Integer[] { jx, iz + l };
							length.add(a);
						}
					} else if (iz == jz && jx > ix) {
						int dist = jx - ix;
						
						for (int h = 1; h < dist; h++) {
							Integer[] a = new Integer[] { ix + h, jz };
							horiz.add(a);
						}
					}
				}
			}
		}
		
		// check for confirmation
		for (Integer[] h : horiz) {
			int hx = h[0];
			int hz = h[1];
			
			for (Integer[] l : length) {
				int lx = l[0];
				int lz = l[1];
				
				if (hx == lx && hz == lz) {
					farea.add(l);
				}
			}
		}
		
		// cleanup because I'm not smart enough to do it right the first time
		List<Integer> remove = new ArrayList<Integer>();
		next:
		
		for (int i = 0; i < farea.size(); i++) {
			Integer[] ii = farea.get(i);
			int ix = ii[0];
			int iz = ii[1];
			
			for (Integer r : remove) {
				if (r == i) {
					continue next;
				}
			}
			
			for (int j = 0; j < farea.size(); j++) {
				if (i != j) {
					Integer[] ji = farea.get(j);
					int jx = ji[0];
					int jz = ji[1];
					
					if (ix == jx && iz == jz) {
						remove.add(j);
					}
				}
			}
			
			remRut:
			
			for (Integer[] rut : ruts) {
				int rx = rut[0];
				int rz = rut[2];
				
				if (rx == ix && rz == iz) {
					remove.add(i);
					break remRut;
				}
			}
		}
		
		List<Integer[]> temp = new ArrayList<Integer[]>();
		next:
		
		for (int i = 0; i < farea.size(); i++) {
			for (Integer r : remove) {
				if (r.equals(i)) {
					continue next;
				}
			}
			
			temp.add(farea.get(i));
		}
		
		// System.out.println("Area : " + farea.size() + " " + temp.size() + " " + remove.size());
		farea = temp;
		// if(farea.size() > 10000) return false;
		entityRune.rutAreaPoints = farea;
		return true;
	}
	
	private final void checkNeighbors(World w, ArrayList<Integer[]> ruts, int x, int y, int z) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if ((i == 0 || i == 2) && i == j && (k == 0 || k == 2) && j != 0) {
						continue;
					}
					
					if ((i == 0 || i == 2) && (j == 0 || j == 2) && i != j && (k == 0 || k == 2) && j != 0) {
						continue;
					}
					
					Block block = w.getBlock(x + i, y + j, z + k);
					
					if (block == DustMod.rutBlock) {
						TileEntityRut ter = (TileEntityRut) w.getTileEntity(x + i, y + j, z + k);
						
						if (!ter.isBeingUsed) {
							ter.isBeingUsed = true;
							ruts.add(new Integer[] { x + i, y + j, z + k });
							checkNeighbors(w, ruts, x + i, y + j, z + k);
						}
					}
				}
			}
		}
	}
	
	private final void checkNeighborsWithDistance(World w, ArrayList<Integer[]> ruts, int x, int y, int z, int distance) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if ((i == 0 || i == 2) && i == j && (k == 0 || k == 2) && j != 0) {
						continue;
					}
					
					if ((i == 0 || i == 2) && (j == 0 || j == 2) && i != j && (k == 0 || k == 2) && j != 0) {
						continue;
					}
					
					Block block = w.getBlock(x + i, y + j, z + k);
					
					if (block == DustMod.rutBlock) {
						TileEntityRut ter = (TileEntityRut) w.getTileEntity(x + i, y + j, z + k);
						
						if (!ter.isBeingUsed && distance > 0) {
							ter.isBeingUsed = true;
							ruts.add(new Integer[] { x + i, y + j, z + k });
							checkNeighborsWithDistance(w, ruts, x + i, y + j, z + k, distance - 1);
						}
					}
				}
			}
		}
	}
	
	private final void checkNeighbors(World w, ArrayList<Integer[]> ruts, int x, int y, int z, Block fluid) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if ((i == 0 || i == 2) && i == j && (k == 0 || k == 2)) {
						continue;
					}
					
					if ((i == 0 || i == 2) && (j == 0 || j == 2) && i != j && (k == 0 || k == 2)) {
						continue;
					}
					
					Block block = w.getBlock(x + i, y + j, z + k);
					
					if (block == DustMod.rutBlock) {
						TileEntityRut ter = (TileEntityRut) w.getTileEntity(x + i, y + j, z + k);
						
						if (!ter.isBeingUsed && ter.fluidBlock == fluid) {
							ter.isBeingUsed = true;
							ruts.add(new Integer[] { x + i, y + j, z + k });
							checkNeighbors(w, ruts, x + i, y + j, z + k);
						}
					}
				}
			}
		}
	}
	
	public RuneEvent setSecret(boolean secret) {
		this.secret = secret;
		return this;
	}
	
	public RuneEvent setPermission(String allowed) {
		this.permission = allowed;
		return this;
	}
	
	public RuneEvent setPermaAllowed(boolean allowed) {
		this.permaAllowed = allowed;
		return this;
	}
	
	@Override
	public String toString() {
		return "DustEvent:" + this.name;
	}
}
