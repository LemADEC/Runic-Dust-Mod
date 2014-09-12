package dustmod.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustmod.DustMod;
import dustmod.items.ItemPouch;
import dustmod.runes.RuneManager;

/**
 * 
 * @author billythegoat101
 */
public class BlockDust extends BlockContainer {

	public static final int UNUSED_DUST = 0;
	public static final int ACTIVE_DUST = 1;
	public static final int DEAD_DUST = 2;
	public static final int ACTIVATING_DUST = 3;

	private IIcon topTexture;
	private IIcon sideTexture;

	public BlockDust() {
		super(Material.circuits);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
		this.setHardness(0.2F);
		this.setStepSound(Block.soundTypeGrass);
		this.disableStats();
	}

	@Override
	public IIcon getIcon(int side, int meta) {

		return (side == 1 ? topTexture : sideTexture);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	/**
	 * Returns which pass should this block be rendered on. 0 for solids and 1
	 * for alpha
	 */
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		int meta = world.getBlockMetadata(x, y, z);
		// if(world.isRemote) return;
		if (entity instanceof EntityItem && meta != DEAD_DUST) {
			EntityItem ei = (EntityItem) entity;
			ei.age = 0;
			EntityPlayer p = world.getClosestPlayerToEntity(ei, 0.6);

			if (p == null) {
				ei.delayBeforeCanPickup = 10;
				return;
			}

			//double dist = p.getDistanceToEntity(ei);

			// if (dist < 0.2 && ei.delayBeforeCanPickup > 5) {
			// System.out.println("Drop " + dist);
			// ei.delayBeforeCanPickup = 5;
			// } else {
			// // System.out.println("Grab " + dist);
			// }
		}

		if (entity instanceof EntityXPOrb && meta != DEAD_DUST) {
			EntityXPOrb orb = (EntityXPOrb) entity;
			orb.xpOrbAge = 0;
			EntityPlayer p = world.getClosestPlayerToEntity(orb, 3.0);

			if (p == null) {
				orb.setPosition(orb.prevPosX, orb.prevPosY, orb.prevPosZ);
				return;
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack item) {
		super.onBlockPlacedBy(world, x, y, z, entityLiving, item);
		// this.onBlockActivated(world, i, j, k, (EntityPlayer) entityliving, 0,
		// 0, 0, 0);

		// XXX WTH?
		if (entityLiving instanceof EntityPlayer) {
			ItemStack equipped = ((EntityPlayer) entityLiving).getCurrentEquippedItem();
			if (equipped != null && equipped.getItem() != DustMod.pouch) {
				equipped.stackSize++;
			}
		}
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {

		Block block = world.getBlock(x, y - 1, z);

		if (block == null) {
			return false;
		} else {
			return block == Blocks.glass || block == DustMod.rutBlock || block.isSideSolid(world, x, y - 1, z, ForgeDirection.UP);
		}
	}

	@Override
	public int getRenderType() {
		return DustMod.proxy.getBlockModel(this);
	}

	@Override
	public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
		int meta = blockAccess.getBlockMetadata(x, y, z);
		switch (meta) {
		case BlockDust.UNUSED_DUST:
			TileEntityDust ted = (TileEntityDust) blockAccess.getTileEntity(x, y, z);

			if (ted == null) {
				return 0xEFEFEF;
			}

			return ted.getRandomDustColor();

		case BlockDust.ACTIVE_DUST:// case 3:
		case BlockDust.ACTIVATING_DUST:
			return 0xDD0000;

		case BlockDust.DEAD_DUST:
			return 0xEFEFEF;

		default:
			DustMod.logger.warn("Unknown metadata value for {}: {}", this.getClass().getCanonicalName(), meta);
			return 0;
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (world.isRemote) {
			return;
		}

		int i1 = world.getBlockMetadata(x, y, z);

		Block blockBelow = world.getBlock(x, y - 1, z);

		if (blockBelow == null || !blockBelow.isSideSolid(world, x, y - 1, z, ForgeDirection.UP)) {
			world.setBlockToAir(x, y, z);
		} else if (world.isBlockIndirectlyGettingPowered(x, y, z) && i1 == 0) {
			updatePattern(world, x, y, z, null);
			world.notifyBlockChange(x, y, z, this);
		}

		TileEntityDust ted = (TileEntityDust) world.getTileEntity(x, y, z);
		if (ted != null)
			ted.onNeighborBlockChange();

		super.onNeighborBlockChange(world, x, y, z, block);
	}

	/**
	 * ejects contained items into the world, and notifies neighbours of an
	 * update, as appropriate
	 */
	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {

		// TODO Test this
		if (world.getBlockMetadata(x, y, z) > 0) {

			return world.setBlockToAir(x, y, z);

		} else {
			TileEntityDust ted = (TileEntityDust) world.getTileEntity(x, y, z);

			// if (world.isRemote) {
			// super.breakBlock(world, i, j, k, b, m);
			// return;
			// }

			if (ted == null || ted.isEmpty()) {
				DustMod.logger.warn("TED was empty!!");
				return true;
			}

			for (int dx = 0; dx < TileEntityDust.SIZE; dx++) {
				for (int dy = 0; dy < TileEntityDust.SIZE; dy++) {
					int dust = ted.getDust(dx, dy);

					if (dust > 0) {

						if (!player.capabilities.isCreativeMode)
							this.dropBlockAsItem(world, x, y, z, new ItemStack(DustMod.idust, 1, dust));

					}
				}
			}

			return world.setBlockToAir(x, y, z);
		}
	}

	@Override
	public int damageDropped(int i) {
		return i;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int face, float cx, float cy, float cz) {

		if (!world.canMineBlock(player, x, y, z))
			return false;

		ItemStack itemStack = player.getCurrentEquippedItem();

		if (itemStack != null && itemStack.getItem() == DustMod.chisel) {
			if (world.getBlock(x, y - 1, z) == DustMod.rutBlock) {
				return DustMod.rutBlock.onBlockActivated(world, x, y - 1, z, player, face, cx, cy, cz);
			}
		}

		if (world.getBlockMetadata(x, y, z) == ACTIVE_DUST) {
			TileEntityDust ted = (TileEntityDust) world.getTileEntity(x, y, z);
			ted.onRightClick(player);
			return true;
		} else if (world.getBlockMetadata(x, y, z) > 1) {
			return false;
		}

		if (player.isSneaking()) {
			if (itemStack != null && itemStack.getItem() != DustMod.tome) {
				onBlockClicked(world, x, y, z, player);
			}

			return false;
		}

		if (!world.isRemote && itemStack != null && itemStack.getItem() == DustMod.tome) {
			updatePattern(world, x, y, z, player);
			world.notifyBlockChange(x, y, z, this);
			return true;
		}

		if (itemStack == null || (itemStack.getItem() != DustMod.idust && itemStack.getItem() != DustMod.pouch)) {
			return false;
		}

		boolean isPouch = (itemStack.getItem() == DustMod.pouch);
		int dust = itemStack.getItemDamage();
		if (isPouch)
			dust = ItemPouch.getValue(itemStack);

		// Dust Migration
		if (dust < 5)
			dust *= 100;

		if (isPouch && ItemPouch.getDustAmount(itemStack) <= 0) {
			return false;
		}

		int rx = (int) Math.floor(cx * TileEntityDust.SIZE);
		int rz = (int) Math.floor(cz * TileEntityDust.SIZE);
		rx = Math.min(TileEntityDust.SIZE - 1, rx);
		rz = Math.min(TileEntityDust.SIZE - 1, rz);

		// DustMod.logger.debug("Result: {} {}", rx, rz);
		TileEntityDust ted = (TileEntityDust) world.getTileEntity(x, y, z);

		if (ted.getDust(rx, rz) <= 0) {
			if (ted.getDust(rx, rz) == -2) {
				setVariableDust(ted, rx, rz, player, dust);
			} else {
				ted.setDust(player, rx, rz, dust);

				if (!player.capabilities.isCreativeMode) {
					ItemPouch.subtractDust(itemStack, 1);

					if (!isPouch && itemStack.stackSize == 0) {
						player.destroyCurrentEquippedItem();
					}
				}
			}

			world.notifyBlockChange(x, y, z, this);
			world.playSoundEffect((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F, stepSound.getStepResourcePath(), (stepSound.getVolume() + 1.0F) / 6.0F, stepSound.getPitch() * 0.99F);
		}
		return true;
	}

	private void setVariableDust(TileEntityDust ted, int x, int z, EntityPlayer p, int dust) {
		if (ted.getDust(x, z) != -2) {
			return;
		}

		boolean found = false;

		if (!p.capabilities.isCreativeMode) {
			for (int sind = 0; sind < p.inventory.mainInventory.length; sind++) {
				ItemStack is = p.inventory.mainInventory[sind];

				if (is != null
						&& ((is.getItem() == DustMod.idust && is.getItemDamage() == dust) || (is.getItem() == DustMod.pouch && ItemPouch.getValue(is) == dust && ItemPouch.getDustAmount(is) > 0))) {
					
					ItemPouch.subtractDust(is, 1);

					if (ItemPouch.getDustAmount(is) == 0 && is.getItem() != DustMod.pouch) {
						p.inventory.mainInventory[sind] = null;
					}

					found = true;
					break;
				}
			}
		} else {
			found = true;
		}

		if (!found) {
			return;
		}

		ted.setDust(p, x, z, dust);

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i == 0 || j == 0) {
					int wx = ted.xCoord;
					int wz = ted.zCoord;
					int ix = x + i;
					int iz = z + j;

					if (ix < 0) {
						ix = TileEntityDust.SIZE - 1;
						wx--;
					} else if (ix >= TileEntityDust.SIZE) {
						ix = 0;
						wx++;
					}

					if (iz < 0) {
						iz = TileEntityDust.SIZE - 1;
						wz--;
					} else if (iz >= TileEntityDust.SIZE) {
						iz = 0;
						wz++;
					}

					TileEntity te = p.worldObj.getTileEntity(wx, ted.yCoord, wz);

					if (!(te instanceof TileEntityDust)) {
						continue;
					}

					TileEntityDust nted = (TileEntityDust) te;
					setVariableDust(nted, ix, iz, p, dust);
				}
			}
		}
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer p) {

		if (!world.canMineBlock(p, x, y, z))
			return;

		Vec3 look = p.getLookVec();
		double mx = look.xCoord;// Math.cos((p.rotationYaw+90)*Math.PI/180);
		double my = look.yCoord;// Math.sin(-p.rotationPitch*Math.PI/180);
		double mz = look.zCoord;// Math.sin((p.rotationYaw+90)*Math.PI/180);

		for (double test = 0; test < 4; test += 0.01) {
			double tx = p.posX + mx * test;
			double ty = p.posY + p.getEyeHeight() + my * test;
			double tz = p.posZ + mz * test;

			if (ty - (double) y <= 0.02) {
				double dx = Math.abs(tx - (double) x) - 0.02;
				double dz = Math.abs(tz - (double) z) - 0.02;
				int rx = (int) Math.floor(dx * TileEntityDust.SIZE);
				int rz = (int) Math.floor(dz * TileEntityDust.SIZE);

				if (rx >= TileEntityDust.SIZE) {
					rx = TileEntityDust.SIZE - 1;
				}

				if (rz >= TileEntityDust.SIZE) {
					rz = TileEntityDust.SIZE - 1;
				}

				if (rx < 0) {
					rx = 0;
				}

				if (rz < 0) {
					rz = 0;
				}

				TileEntityDust ted = (TileEntityDust) world.getTileEntity(x, y, z);

				if (ted.getDust(rx, rz) != 0 && world.getBlockMetadata(x, y, z) == 0) {
					if (ted.getDust(rx, rz) > 0 && !p.capabilities.isCreativeMode) {
						this.dropBlockAsItem(world, x, y, z, new ItemStack(DustMod.idust, 1, ted.getDust(rx, rz)));
					}

					world.playSoundEffect((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F, stepSound.getStepResourcePath(), (stepSound.getVolume() + 1.0F) / 6.0F, stepSound.getPitch() * 0.99F);
					world.notifyBlockChange(x, y, z, this);
					ted.setDust(p, rx, rz, 0);

					// System.out.println("drop click");
					if (ted.isEmpty() && world.getBlockMetadata(x, y, z) != 10) {
						// System.out.println("Destroying");
						world.setBlockToAir(x, y, z);
						this.onBlockDestroyedByPlayer(world, x, y, z, 0);
					}
				}
				break;
			}

			// world.setBlock((int)tx, (int)ty, (int)tz, Block.brick.blockID);
		}

		// super.onBlockClicked(world, i, j, k, p);
	}

	/*
	@Override
	public int idDropped(int i, Random random, int j) {
		return 0;// i == 0 ? mod_DustMod.ITEM_DustID+256:0;
	}*/

	public void updatePattern(World world, int i, int j, int k, EntityPlayer p) {
		List<Integer[]> n = new ArrayList<Integer[]>();
		addNeighbors(world, i, j, k, n);

		if (n.size() == 0) {
			return; // dudewat
		}

		for (Integer[] iter : n) {
			if (world.getBlock(iter[0], j, iter[2]) == this) {
				world.setBlockMetadataWithNotify(iter[0], j, iter[2], ACTIVATING_DUST, 2);
			}
		}

		int sx = n.get(0)[0];
		int sz = n.get(0)[2];
		int mx = n.get(0)[0];
		int mz = n.get(0)[2];

		for (Integer[] iter : n) {
			if (iter[0] < sx) {
				sx = iter[0];
			}

			if (iter[2] < sz) {
				sz = iter[2];
			}

			if (iter[0] > mx) {
				mx = iter[0];
			}

			if (iter[2] > mz) {
				mz = iter[2];
			}
		}

		int size = TileEntityDust.SIZE;
		int dx = mx - sx;
		int dz = mz - sz;
		int[][] map = new int[(mx - sx + 1) * size][(mz - sz + 1) * size];

		for (int x = 0; x <= dx; x++) {
			for (int z = 0; z <= dz; z++) {
				if (world.getBlock(x + sx, j, z + sz) == this) {
					TileEntityDust ted = (TileEntityDust) world.getTileEntity(x + sx, j, z + sz);

					for (int ix = 0; ix < size; ix++) {
						for (int iz = 0; iz < size; iz++) {
							map[ix + x * size][iz + z * size] = ted.getDust(ix, iz);
						}
					}
				}
			}
		}

		// System.out.println("ASNASO " + Arrays.deepToString(map));
		RuneManager.callShape(world, (double) sx + (double) dx / 2 + 0.5D, j + 1D, (double) sz + (double) dz / 2 + 0.5D, map, n, (p == null) ? null : p.getGameProfile().getId());
	}

	public void addNeighbors(World world, int i, int j, int k, List<Integer[]> list) {
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (world.getBlock(i + x, j, k + z) == this && world.getBlockMetadata(i + x, j, k + z) == 0) {
					boolean cont = true;
					// XXX
					stopcheck:

					for (Integer[] iter : list) {
						if (iter[0] == i + x && iter[2] == k + z) {
							cont = false;
							break stopcheck;
						}
					}

					if (cont) {
						list.add(new Integer[] { i + x, j, k + z });
						addNeighbors(world, i + x, j, k + z, list);
					}
				}
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityDust();
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z); 
		return block != null ? block.getPickBlock(target, world, x, y, z) : null;
	};

	/**
	 * Get the block's damage value (for use with pick block).
	 */
	public int getDamageValue(World world, int i, int j, int k) {
		return world.getBlockMetadata(i, j - 1, k);
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Returns the default ambient occlusion value based on block opacity
	 */
	public float getAmbientOcclusionLightValue(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		return 0;
	}

	/**
	 * Get a light value for the block at the specified coordinates, normal
	 * ranges are between 0 and 15
	 *
	 * @param world
	 *            The current world
	 * @param x
	 *            X Position
	 * @param y
	 *            Y position
	 * @param z
	 *            Z position
	 * @return The light value
	 */
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == ACTIVE_DUST || meta == ACTIVATING_DUST) {
			return 8;
		}
		return lightValue;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.topTexture = iconRegister.registerIcon(DustMod.spritePath + "dust_top");
		this.sideTexture = iconRegister.registerIcon(DustMod.spritePath + "dust_side");
	}

}
