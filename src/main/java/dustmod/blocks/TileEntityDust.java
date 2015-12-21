/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.blocks;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import dustmod.DustMod;
import dustmod.dusts.DustManager;
import dustmod.runes.EntityRune;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 *
 * @author billythegoat101
 */
public class TileEntityDust extends TileEntity implements IInventory {
	public static final int SIZE = 4;
	public boolean active = false;
	private int[] pattern;
	private boolean[] dusts;
	private int toDestroy = -1;
	private int ticksExisted = 0;
	private EntityRune entityDust = null;
	private boolean isPowered = false;
	private boolean hasMadeFirstPoweredCheck = false;
	
	public int dustEntID;
	
	private boolean hasFlame = false;
	private final float[] flameColor = new float[3]; //flame rgb
	
	public TileEntityDust() {
		pattern = new int[SIZE * SIZE];
	}
	
	public void setEntityDust(EntityRune ed) {
		this.entityDust = ed;
		this.dustEntID = ed.getEntityId();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		tag.setInteger("ticks", ticksExisted);
		
		writeNetworkNBT(tag);
	}
	
	public void writeNetworkNBT(NBTTagCompound tag) {
		int[] intPattern = new int[pattern.length / 2];
		
		for (int i = 0; i < intPattern.length; i++) {
			intPattern[i] = (pattern[i * 2] & 0xFFFF) | (pattern[i * 2 + 1] << 16);
		}
		tag.setIntArray("pattern", intPattern);
		
		tag.setInteger("toDestroy", toDestroy);
		
		tag.setBoolean("flame", hasFlame);
		
		int r = (int) (flameColor[0] * 255);
		int g = (int) (flameColor[1] * 255);
		int b = (int) (flameColor[2] * 255);
		
		tag.setInteger("flameColor", r << 16 | g << 8 | b);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		if (tag.hasKey("ticks")) {
			ticksExisted = tag.getInteger("ticks");
		}
		
		readNetworkNBT(tag);
	}
	
	public void readNetworkNBT(NBTTagCompound tag) {
		int[] intPattern = tag.getIntArray("pattern");
		
		for (int i = 0; i < intPattern.length; i++) {
			short dustLow = (short) intPattern[i];
			short dustHigh = (short) (intPattern[i] >> 16);
			pattern[i * 2] = dustLow;
			pattern[i * 2 + 1] = dustHigh;
		}
		
		if (tag.hasKey("toDestroy")) {
			toDestroy = tag.getInteger("toDestroy");
		}
		
		if (tag.hasKey("flame")) {
			this.hasFlame = tag.getBoolean("flame");
			int rgb = tag.getInteger("flameColor");
			int r = rgb >> 16;
			int g = (rgb >> 8) & 0xFF;
			int b = rgb & 0xFF;
			
			flameColor[0] = r / 255.0F;
			flameColor[1] = g / 255.0F;
			flameColor[2] = b / 255.0F;
		}
	}
	
	public void setDust(EntityPlayer p, int i, int j, int dust) {
		if (p != null && !worldObj.canMineBlock(p, this.xCoord, this.yCoord, this.zCoord))
			return;
		int last = getDust(i, j);
		if (dust >= 1000)
			dust = 999;
		pattern[i + j * SIZE] = dust;
		dusts = null;
		
		if (dust != 0 && last != dust) {
			int[] color = DustManager.getFloorColorRGB(dust);
			java.awt.Color c = new java.awt.Color(color[0], color[1], color[2]);
			c = c.darker();
			float r = c.getRed() / 255F;
			float g = c.getGreen() / 255F;
			float b = c.getBlue() / 255F;
			if (r == 0)
				r -= 1;
			
			if (Math.random() < 0.75)
				for (int d = 0; d < Math.random() * 3; d++) {
					worldObj.spawnParticle("reddust", xCoord + i / 4D + Math.random() * 0.15, yCoord, zCoord + j / 4D + Math.random() * 0.15, r, g, b);
				}
		}
		worldObj.notifyBlockChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		this.markDirty();
	}
	
	public int[] getPattern() {
		return pattern;
	}
	
	public int getDust(int i, int j) {
		int rtn = pattern[i + j * SIZE];
		if (rtn >= 1000) {
			return 999;
		}
		return rtn;
	}
	
	@Override
	public void updateEntity() {
		if (isEmpty()) {
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			this.invalidate();
			return;
		}
		
		if (this.getBlockMetadata() == BlockDust.ACTIVE_DUST && Math.random() < 0.12) {
			worldObj.spawnParticle("reddust", xCoord + Math.random(), yCoord, zCoord + Math.random(), 0, 0, 0);
		}
		
		ticksExisted++;
		
		if (this.getBlockMetadata() == BlockDust.DEAD_DUST) {
			
			if (!worldObj.isRemote && toDestroy == -1 && ticksExisted % 100 == 0) {
				toDestroy = (int) Math.round(Math.random() * 200D + 100D);
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			} else if (toDestroy == 0) {
				
				if (worldObj.isRemote) {
					for (int i1 = 0; i1 < Math.random() * 2D + 2D; i1++) {
						worldObj.spawnParticle("smoke", xCoord + Math.random(), yCoord + Math.random() / 2D, zCoord + Math.random(), 0.07, 0.01D, 0.07D);
					}
				} else {
					List<Integer> d = new ArrayList<Integer>(SIZE * SIZE);
					
					for (int i = 0; i < SIZE; i++) {
						for (int j = 0; j < SIZE; j++) {
							if (getDust(i, j) != 0) {
								d.add(i + j * SIZE);
							}
						}
					}
					
					int ind = d.get(worldObj.rand.nextInt(d.size()));
					this.setDust(null, (int) Math.floor(ind % SIZE), ind / SIZE, 0);
					this.markDirty();
					
					toDestroy = (int) Math.round(Math.random() * 200D + 100D);
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			} else if (toDestroy > 0) {
				toDestroy--;
			}
		} else {
			toDestroy = -1;
		}
		
		if (!this.hasMadeFirstPoweredCheck) {
			this.isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
			this.hasMadeFirstPoweredCheck = true;
		}
	}
	
	public void onRightClick(EntityPlayer p) {
		if (this.entityDust != null) {
			entityDust.onRightClick(this, p);
		}
	}
	
	public void onNeighborBlockChange() {
		this.isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}
	
	public boolean isPowered() {
		return this.isPowered;
	}
	
	public int[][][] getRendArrays() {
		int[][][] rtn = new int[3][SIZE + 1][SIZE + 1];
		int[][] n = new int[SIZE + 2][SIZE + 2]; //neighbors
		
		for (int x = 0; x < SIZE; x++) {
			for (int z = 0; z < SIZE; z++) {
				n[x + 1][z + 1] = getDust(x, z);
				rtn[0][x][z] = getDust(x, z);
			}
		}
		
		if (DustMod.isDust(worldObj.getBlock(xCoord - 1, yCoord, zCoord))) {
			TileEntityDust ted = (TileEntityDust) worldObj.getTileEntity(xCoord - 1, yCoord, zCoord);
			
			for (int i = 0; i < SIZE; i++) {
				n[0][i + 1] = ted.getDust(SIZE - 1, i);
			}
		}
		
		if (DustMod.isDust(worldObj.getBlock(xCoord + 1, yCoord, zCoord))) {
			TileEntityDust ted = (TileEntityDust) worldObj.getTileEntity(xCoord + 1, yCoord, zCoord);
			
			for (int i = 0; i < SIZE; i++) {
				n[SIZE + 1][i + 1] = ted.getDust(0, i);
			}
		}
		
		if (DustMod.isDust(worldObj.getBlock(xCoord, yCoord, zCoord - 1))) {
			TileEntityDust ted = (TileEntityDust) worldObj.getTileEntity(xCoord, yCoord, zCoord - 1);
			
			for (int i = 0; i < SIZE; i++) {
				n[i + 1][0] = ted.getDust(i, SIZE - 1);
			}
		}
		
		if (DustMod.isDust(worldObj.getBlock(xCoord, yCoord, zCoord + 1))) {
			TileEntityDust ted = (TileEntityDust) worldObj.getTileEntity(xCoord, yCoord, zCoord + 1);
			
			for (int i = 0; i < SIZE; i++) {
				n[i + 1][SIZE + 1] = ted.getDust(i, 0);
			}
		}
		
		//        System.out.println("DERP " + Arrays.deepToString(n));
		//horiz
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE + 1; y++) {
				if (n[x + 1][y] == n[x + 1][y + 1]) {
					rtn[1][x][y] = n[x + 1][y];
				}
			}
		}
		
		//vert
		for (int x = 0; x < SIZE + 1; x++) {
			for (int y = 0; y < SIZE; y++) {
				if (n[x][y + 1] == n[x + 1][y + 1]) {
					rtn[2][x][y] = n[x][y + 1];
				}
			}
		}
		
		return rtn;
	}
	
	public boolean isEmpty() {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (getDust(i, j) != 0) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public int getAmount() {
		int amt = 0;
		
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (getDust(i, j) != 0) {
					amt++;
				}
			}
		}
		
		return amt;
	}
	
	public boolean[] getDusts() {
		if (dusts == null) {
			dusts = new boolean[1000];
			
			for (int i = 0; i < SIZE; i++) {
				for (int j = 0; j < SIZE; j++) {
					if (getDust(i, j) >= 0) {
						dusts[getDust(i, j)] = true;
					}
				}
			}
		}
		
		return dusts;
	}
	
	public int getRandomDustColor() {
		int s = 0;
		int[] dustIds = new int[1000];
		boolean[] bdusts = getDusts();
		
		for (int i = 1; i < 1000; i++) {
			if (bdusts[i]) {
				dustIds[s] = i;
				s++;
			}
		}
		
		if (s <= 0) {
			return 0;
		}
		
		int[] rgb = DustManager.getFloorColorRGB(dustIds[worldObj.rand.nextInt(s)]);
		return new Color(rgb[0], rgb[1], rgb[2]).getRGB();
	}
	
	public void empty() {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				setDust(null, i, j, 0);
			}
		}
	}
	
	public void copyTo(TileEntityDust ted) {
		ted.dusts = dusts.clone();
		
		ted.pattern = pattern.clone();
		
		ted.toDestroy = toDestroy;
		ted.ticksExisted = ticksExisted;
		int tx = ted.xCoord;
		int ty = ted.yCoord;
		int tz = ted.zCoord;
		ted.worldObj.setBlock(tx, ty, tz, worldObj.getBlock(xCoord, yCoord, zCoord), worldObj.getBlockMetadata(xCoord, yCoord, zCoord), 3);
	}
	
	@Override
	public int getSizeInventory() {
		return SIZE * SIZE;
	}
	
	@Override
	public ItemStack getStackInSlot(int loc) {
		int y = loc % SIZE;
		int x = (loc - SIZE) / SIZE;
		
		if (getDust(x, y) == 0) {
			return null;
		} else {
			return new ItemStack(DustMod.itemDust, 1, pattern[x + y * SIZE]);
		}
	}
	
	@Override
	public ItemStack decrStackSize(int loc, int amt) {
		int y = loc % SIZE;
		int x = (loc - SIZE) / SIZE;
		//        if(amt > 0){
		pattern[x + y * SIZE] = 0;
		return null;
		//        }else if(amt < 0){
		//            pattern[x][y] = 1;
		//            return new ItemStack(mod_DustMod.idust.itemID,1,pattern[x][y]);
		//        }else{
		//            if(pattern[x][y] == 0){
		//                return null;
		//            }else{
		//                return new ItemStack(mod_DustMod.idust.itemID,1,pattern[x][y]);
		//            }
		//        }
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int loc) {
		return null;
	}
	
	@Override
	public void setInventorySlotContents(int loc, ItemStack item) {
		int y = loc % SIZE;
		int x = (loc - SIZE) / SIZE;
		int size = item.stackSize;
		int meta = item.getItemDamage();
		
		if (item.getItem() == DustMod.itemDust && size > 0) {
			pattern[x + y * SIZE] = meta;
		}
	}
	
	@Override
	public String getInventoryName() {
		return "dusttileentity";
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return false;
	}
	
	@Override
	public void openInventory() {
	}
	
	@Override
	public void closeInventory() {
	}
	
	public void setRenderFlame(boolean val, int r, int g, int b) {
		this.hasFlame = val;
		this.flameColor[0] = r / 255.0F;
		this.flameColor[1] = g / 255.0F;
		this.flameColor[2] = b / 255.0F;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public boolean hasFlame() {
		return hasFlame;
	}
	
	public float[] getFlameColor() {
		return flameColor;
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeNetworkNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readNetworkNBT(pkt.func_148857_g());
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	
	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		return false;
	}
}
