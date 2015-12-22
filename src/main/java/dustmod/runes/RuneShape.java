/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.runes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import dustmod.DustMod;
import dustmod.blocks.BlockDust;
import dustmod.blocks.TileEntityDust;
import dustmod.dusts.DustManager;
import dustmod.items.ItemPouch;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * 
 * @author billythegoat101
 */
public class RuneShape {
	public static final int n = -1;

	public final int width;
	public final int length;
	public final int height;
	public boolean solid = true;
	public boolean isPower = false;

	public String name;
	private String pName = "";
	private String notes = "";
	private String author = "";
	protected String desc = "";
	public final int[][][] data;
	public boolean isRemote = false;

	// Center X Offset
	public final int cx;
	// Center Z Offset
	public final int cz;
	
	// Edge X Offset
	public final int ox;
	// Edge Z Offset
	public final int oz;

	public ArrayList<ArrayList<int[][]>> blocks;

	public int[] dustAmt;

	public final int id;
	public int pageNumber;

	public ArrayList<Integer> allowedVariable;

	/**
	 * 
	 * The generic shape class that should be created to assign each rune its
	 * design. Note: The 'name' parameter is used to identify the rune in save
	 * files and once set should not be changed. If you want to set the name
	 * that is displayed, use DustShape.setRuneName() See picture:
	 * 
	 * 
	 * @param w
	 *            Rune size width (x)
	 * @param l
	 *            Rune size length (z)
	 * @param design
	 *            Design of the Rune
	 * @param name
	 *            Code name for the rune.
	 * @param solid
	 *            Is this rune a solid color. Mostly useful for runes who are
	 *            entirely made out of variable and should only be one dust
	 *            (like fire trap)
	 * @param cx
	 *            X offset for the center of the rune
	 * @param cy
	 *            Y offset for the center of the rune
	 * @param id
	 *            unique rune id
	 */
	public RuneShape(int[][][] design, String name, boolean solid, int cx, int cy, int id) {
		this(design, name, solid, cx, cy, RuneManager.getNextPageNumber(), id);
	}

	/**
	 * 
	 * The generic shape class that should be created to assign each rune its
	 * design. Note: The 'name' parameter is used to identify the rune in save
	 * files and once set should not be changed. If you want to set the name
	 * that is displayed, use DustShape.setRuneName() See picture:
	 * 
	 * 
	 * @param w
	 *            Rune size width (x)
	 * @param l
	 *            Rune size length (z)
	 * @param design
	 *            Design of the Rune
	 * @param name
	 *            Code name for the rune.
	 * @param solid
	 *            Is this rune a solid color. Mostly useful for runes who are
	 *            entirely made out of variable and should only be one dust
	 *            (like fire trap)
	 * @param cx
	 *            X offset for the center of the rune
	 * @param cy
	 *            Y offset for the center of the rune
	 * @param page
	 *            The page number used in the tome
	 * @param id
	 *            unique rune id
	 */
	public RuneShape(int[][][] design, String name, boolean solid, int cx, int cy, int page, int id) {

		this.width = design[0].length;
		this.length = design[0][0].length;
		this.height = 1;
		
		if (width > 32 || length > 32) {
			throw new IllegalArgumentException("Rune dimensions too big! " + name + " Max:32x32");
		}

		this.id = id;
		this.name = name;
		this.data = design;
		this.dustAmt = new int[1000];
		this.solid = solid;
		this.cz = cy;
		this.cx = cx;
		this.oz = (cy % 4 == 0) ? 0 : 4 - (cy % 4);
		this.ox = (cx % 4 == 0) ? 0 : 4 - (cx % 4);

		blocks = new ArrayList<ArrayList<int[][]>>();
		allowedVariable = new ArrayList<Integer>();
		int[] test = getBlockCoord(ox + width, oz + length);
		int bwidth = test[0] + 2;
		int bheight = test[1] + 2;

		for (int i = 0; i < bwidth; i++) {
			blocks.add(new ArrayList<int[][]>());

			for (int j = 0; j < bheight; j++) {
				blocks.get(i).add(new int[4][4]);
			}
		}
		this.pageNumber = page;

		updateData();
	}

	/**
	 * Set the text for the notes/sacrifices page in the Tome
	 * 
	 * @param n
	 *            Raw string to display
	 * @return This DustShape
	 */
	public RuneShape setNotes(String n) {
		this.notes = n;
		return this;
	}

	private static String ALLOWED_VARIABLE_DUSTS_HEADER = "\n\n§fAllowed variable dusts§7\n";
	/**
	 * Set the text for the description page in the Tome
	 * 
	 * @param rawString
	 *            Raw string to display
	 * @return This DustShape
	 */
	public RuneShape setDesc(String rawString) {
		this.desc = rawString;
		if (!allowedVariable.isEmpty()) {
			desc += ALLOWED_VARIABLE_DUSTS_HEADER;
			boolean isFirst = true;
			for (int i : allowedVariable) {
				desc += (isFirst ? "" : ", ") + DustManager.getName(i);
				isFirst = false;
			}
		}
		return this;
	}

	public RuneShape addAllowedVariable(ArrayList<Integer> values) {
		allowedVariable.addAll(values);
		if (desc.contains(ALLOWED_VARIABLE_DUSTS_HEADER)) {
			setDesc(desc.substring(0, desc.indexOf(ALLOWED_VARIABLE_DUSTS_HEADER)));
		}
		return this;
	}

	public boolean isDustAllowedAsVariable(int dustValue) {
		return allowedVariable.contains(dustValue) || allowedVariable.isEmpty();
	}

	private void updateData() {
		blocks = updateData(data, oz, ox);
	}

	private ArrayList<ArrayList<int[][]>> updateData(int[][][] tdata, int tox, int toy) {
		int w = tdata[0].length;
		int l = tdata[0][0].length;

		ArrayList<ArrayList<int[][]>> tblocks = new ArrayList<ArrayList<int[][]>>();
		int[] coords = getBlockCoord(tox + w, toy + l, tox, toy);
		int bwidth = coords[0] + 2;
		int bheight = coords[1] + 2;

		for (int i = 0; i < bwidth; i++) {
			tblocks.add(new ArrayList<int[][]>());

			for (int j = 0; j < bheight; j++) {
				tblocks.get(i).add(new int[4][4]);
			}
		}

		dustAmt = new int[1000];

		for (int y = 0; y < tdata.length; y++)
			for (int x = 0; x < tdata[0].length; x++) {
				for (int z = 0; z < tdata[0][0].length; z++) {
					int[] c = getBlockCoord(x, z, tox, toy);
					int to = tdata[y][x][z];

					if (to == -1) {
						to = -2;
					}

					tblocks.get(c[0]).get(c[1])[c[2]][c[3]] = to;

					if (to >= 0) {
						dustAmt[to]++;
					}
				}
			}

		return tblocks;
	}

	public int[] getBlockCoord(int x, int z) {
		return getBlockCoord(x, z, oz, ox);
	}

	public int[] getBlockCoord(int x, int z, int toX, int toY) {
		int i = (int) Math.floor((x + toX) / 4);
		int j = (int) Math.floor((z + toY) / 4);
		int nx = x + toX - i * 4;
		int nz = z + toY - j * 4;

		return new int[] { i, j, nx, nz };
	}

	public RuneShape setDataAt(int x, int y, int z, int b) {
		data[y][x][z] = b;
		updateData();
		return this;
	}

	public int getDataAt(int x, int y, int z) {
		return data[y][x][z];
	}

	public int[][][] getData() {
		return data;
	}

	/**
	 * 
	 * @param map
	 * @return -1: no match, 0 to 3: match against that direction
	 */
	public int dataMatches(int[][] map) {
		int w = map.length;
		int l = map[0].length;
		
		if ((w != width || l != length) && (w != length || l != width)) {
			return -1;
		}
		
		boolean checkAllRotations = (w == l) || true;
		int rotationOffset = 0;
		/*
		if (w != width) {
			map = rotateMatrixLeft(map);
			rotationOffset = 1;
			checkAllRotations = false;
		}/**/
		
		if (dataMatches(map, 0, 0, 0)) {
			return checkSolid(map) ? (rotationOffset + 2) : -1;
		}
		
		map = flipMatrixXY(map);
		
		if (dataMatches(map, 0, 0, 0)) {
			return checkSolid(map) ? (rotationOffset + 0) : -1;
		}
		
		if (checkAllRotations) {
			map = rotateMatrixLeft(map);
			
			if (dataMatches(map, 0, 0, 0)) {
				return checkSolid(map) ? (rotationOffset + 1) : -1;
			}
			
			map = flipMatrixXY(map);
			
			if (dataMatches(map, 0, 0, 0)) {
				return checkSolid(map) ? ((rotationOffset + 3) % 4) : -1;
			}
		}

		return -1;
	}
	
	public boolean checkSolid(int[][] d) {
		int w = d.length;
		int l = d[0].length;
		
		if (solid) {
			int compare = 0;

			for (int x = 0; x < w; x++) {
				for (int z = 0; z < l; z++) {
					int iter = d[x][z];

					if (compare == 0 && iter != 0) {
						compare = iter;
					} else if (compare != 0 && iter != 0 && compare != iter) {
						return false;
					}
				}
			}
		}
		
		return true;
	}

	protected boolean dataMatches(int[][] d, int ox, int oy, int oz) {

		int w = d.length;
		int l = d[0].length;
	
		for (int x = 0; x < w; x++) {
			for (int z = 0; z < l; z++) {
				if ((d[x][z] != data[oy][x + ox][z + oz] && (d[x][z] == 0 || data[oy][x + ox][z + oz] != -1))
						|| (data[oy][x + ox][z + oz] == -1 && !this.isDustAllowedAsVariable(d[x][z]))) {
					return false;
				}
			}
		}
	
		return true;
	}

	// Rotates the matrix counterclockwise
	public static int[][] rotateMatrixLeft(int[][] matrixIn) {
		int width = matrixIn.length;
		int height = matrixIn[0].length;
		int[][] matrixOut = new int[height][width];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				matrixOut[y][width - 1 - x] = matrixIn[x][y];
			}
		}

		return matrixOut;
	}
	
	// Rotates the matrix clockwise
	public static int[][] rotateMatrixRight(int[][] matrixIn) {
		int width = matrixIn.length;
		int height = matrixIn[0].length;
		int[][] matrixOut = new int[height][width];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				matrixOut[height - 1 - y][x] = matrixIn[x][y];
			}
		}

		return matrixOut;
	}

	public static int[][] flipMatrixX(int[][] matrixIn) {
		int width = matrixIn.length;
		int height = matrixIn[0].length;
		int[][] matrixOut = new int[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				matrixOut[width - 1 - x][y] = matrixIn[x][y];
			}
		}
		return matrixOut;
	}
	
	public static int[][] flipMatrixY(int[][] matrixIn) {
		int width = matrixIn.length;
		int height = matrixIn[0].length;
		int[][] matrixOut = new int[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				matrixOut[x][height - 1 - y] = matrixIn[x][y];
			}
		}
		return matrixOut;
	}
	
	public static int[][] flipMatrixXY(int[][] matrixIn) {
		int width = matrixIn.length;
		int height = matrixIn[0].length;
		int[][] matrixOut = new int[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				matrixOut[width - 1 - x][height - 1 - y] = matrixIn[x][y];
			}
		}
		return matrixOut;
	}
	
	public boolean drawOnWorldWhole(World world, int x, int y, int z, EntityPlayer player, int rotation) {
		
		if (world.isRemote)
			return false;
		
		int placementX = x;
		int placementZ = z;
		int newEdgeOffsetX;
		int newEdgeOffsetY;
		int newCenterOffsetX;
		int newCenterOffsetY;
		
		int[][][] rotatedData = new int[height][][];
		
		for (int i = 0; i < rotatedData.length; i++) {
			if (rotation == 0) {
				rotatedData[i] = data[i].clone();
			} else if (rotation == 1) {
				rotatedData[i] = rotateMatrixLeft(data[i]);
			} else if (rotation == 2) {
				rotatedData[i] = flipMatrixXY(data[i]);
			} else if (rotation == 3) {
				rotatedData[i] = rotateMatrixRight(data[i]);
			}
		}
		
		// DustMod.logger.info("rotation {} data {}", rotation, Arrays.deepToString(rotatedData));
		
		// Recalculate Center offset
		switch (rotation) {
		case 0:
		default:
			newCenterOffsetX = cx;
			newCenterOffsetY = cz;
			break;
			
		case 1:
			newCenterOffsetX = cz;
			newCenterOffsetY = width - cx - 4;
			break;
		
		case 2:
			newCenterOffsetX = width - cx - 4;
			newCenterOffsetY = length - cz - 4;
			break;
			
		case 3:
			newCenterOffsetX = length - cz - 4;
			newCenterOffsetY = cx;
			break;
		}
		
		newEdgeOffsetX = (newCenterOffsetX % 4 == 0) ? 0 : 4 - (newCenterOffsetX % 4);
		newEdgeOffsetY = (newCenterOffsetY % 4 == 0) ? 0 : 4 - (newCenterOffsetY % 4);
		
		int[] centerPos = getBlockCoord(newCenterOffsetX, newCenterOffsetY, newEdgeOffsetX, newEdgeOffsetY);
		
		placementX -= centerPos[0];
		placementZ -= centerPos[1];
		
		int[] pDustAmount = new int[1000];

		for (ItemStack is : player.inventory.mainInventory) {
			if (is != null) {
				if (is.getItem() == DustMod.itemDust) {
					pDustAmount[is.getItemDamage()] += is.stackSize;
				} else if (is.getItem() == DustMod.pouch) {
					int dustID = ItemPouch.getValue(is);
					int amt = ItemPouch.getDustAmount(is);
					pDustAmount[dustID] += amt;
				}
			}
		}

		ArrayList<ArrayList<int[][]>> blockData = updateData(rotatedData, newEdgeOffsetX, newEdgeOffsetY);
		int[] reduceDustAmount = new int[1000];

		for (int bx = 0; bx < blockData.size(); bx++) {
			for (int bz = 0; bz < blockData.get(0).size(); bz++) {
				int[][] block = blockData.get(bx).get(bz);

				boolean empty = true;
				for (int iter = 0; iter < block.length && empty; iter++) {
					for (int jter = 0; jter < block[0].length && empty; jter++) {
						if (block[iter][jter] != 0)
							empty = false;
					}
				}
				if (empty) {
					continue;
				}

				Block otherBlock = world.getBlock(placementX + bx, y, placementZ + bz);
				int meta = world.getBlockMetadata(placementX + bx, y, placementZ + bz);

				if (!otherBlock.getMaterial().isReplaceable() && !DustMod.isDust(otherBlock)) {
					continue;
				}

				if (world.isAirBlock(placementX + bx, y - 1, placementZ + bz)) {
					continue;
				}

				if (!DustMod.dust.canPlaceBlockAt(world, placementX + bx, y, placementZ + bz)) {
					continue;
				}

				if (otherBlock != DustMod.dust) {
					world.setBlock(placementX + bx, y, placementZ + bz, DustMod.dust, BlockDust.UNUSED_DUST, 2);
				} else if (meta == BlockDust.DEAD_DUST) {
					world.setBlock(placementX + bx, y, placementZ + bz, DustMod.dust, BlockDust.UNUSED_DUST, 2);
				} else if (meta != BlockDust.UNUSED_DUST) {
					continue;	// skip that block
				}
				
				TileEntityDust ted;
				TileEntity te = world.getTileEntity(placementX + bx, y, placementZ + bz);

				if (te != null && te instanceof TileEntityDust) {
					ted = (TileEntityDust) te;
				} else {
					DustMod.logger.info("CREATING TE 2");
					ted = new TileEntityDust();
					world.setTileEntity(placementX + bx, y, placementZ + bz, ted);
				}

				// ted.empty();

				for (int ix = 0; ix < 4; ix++) {
					for (int iz = 0; iz < 4; iz++) {
						int dust = block[ix][iz];
						if (ted.getDust(ix, iz) == 0 && dust != 0) {
							boolean canDraw = true;
							if (dust > 0 && !player.capabilities.isCreativeMode) {
								if (pDustAmount[dust] > 0) {
									reduceDustAmount[dust]++;
									pDustAmount[dust]--;
								} else {
									canDraw = false;
								}
							}
							if (canDraw) {
								ted.setDust(player, ix, iz, dust);
							}
						}
					}
				}
			}
		}

		for (int bx = 0; bx < blockData.size(); bx++) {
			for (int bz = 0; bz < blockData.get(0).size(); bz++) {
				if (DustMod.isDust(world.getBlock(placementX + bx, y, placementZ + bz))) {
					TileEntityDust ted = (TileEntityDust) world.getTileEntity(placementX + bx, y, placementZ + bz);

					if (ted.isEmpty()) {
						world.setBlockToAir(placementX + bx, y, placementZ + bz);
					} else {
						world.markBlockForUpdate(placementX + bx, y, placementZ + bz);
					}
				}
			}
		}

		if (!player.capabilities.isCreativeMode) {
			for (int id = 1; id < 1000; id++) {
				for (int sind = 0; sind < player.inventory.mainInventory.length; sind++) {
					ItemStack is = player.inventory.mainInventory[sind];

					if (is != null && reduceDustAmount[id] > 0) {
						if (is.getItem() == DustMod.itemDust && is.getItemDamage() == id) {
							while (reduceDustAmount[id] > 0 && is.stackSize > 0) {
								is.stackSize--;

								if (is.stackSize == 0) {
									player.inventory.mainInventory[sind] = null;
								}

								reduceDustAmount[id]--;
							}
						} else if (is.getItem() == DustMod.pouch) {
							int did = ItemPouch.getValue(is);
							if (did == id) {
								while (reduceDustAmount[id] > 0 && ItemPouch.getDustAmount(is) > 0) {
									ItemPouch.subtractDust(is, 1);

									reduceDustAmount[id]--;
								}
							}
						}

					}
				}
			}
		}
		InventoryPlayer inv = player.inventory;
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			inv.getStackInSlot(slot);
		}
		player.inventory.inventoryChanged = true;

		updateData();
		return true;
	}

	public boolean drawOnWorldPart(World world, int x, int y, int z, EntityPlayer player, int rotation, int itemUse) {
		if (world.isRemote)
			return false;
		
		int placementX = x;
		int placementZ = z;
		int newEdgeOffsetX;
		int newEdgeOffsetY;
		int newCenterOffsetX;
		int newCenterOffsetY;
		
		int[][][] rotatedData = new int[height][][];
		
		for (int i = 0; i < rotatedData.length; i++) {
			if (rotation == 0) {
				rotatedData[i] = data[i].clone();
			} else if (rotation == 1) {
				rotatedData[i] = rotateMatrixLeft(data[i]);
			} else if (rotation == 2) {
				rotatedData[i] = flipMatrixXY(data[i]);
			} else if (rotation == 3) {
				rotatedData[i] = rotateMatrixRight(data[i]);
			}
		}
		
		// DustMod.logger.info("rotation {} data {}", rotation, Arrays.deepToString(rotatedData));
		
		// Recalculate Center offset
		switch (rotation) {
		case 0:
		default:
			newCenterOffsetX = cx;
			newCenterOffsetY = cz;
			break;
			
		case 1:
			newCenterOffsetX = cz;
			newCenterOffsetY = width - cx - 4;
			break;
		
		case 2:
			newCenterOffsetX = width - cx - 4;
			newCenterOffsetY = length - cz - 4;
			break;
			
		case 3:
			newCenterOffsetX = length - cz - 4;
			newCenterOffsetY = cx;
			break;
		}
		
		newEdgeOffsetX = (newCenterOffsetX % 4 == 0) ? 0 : 4 - (newCenterOffsetX % 4);
		newEdgeOffsetY = (newCenterOffsetY % 4 == 0) ? 0 : 4 - (newCenterOffsetY % 4);
		
		int[] centerPos = getBlockCoord(newCenterOffsetX, newCenterOffsetY, newEdgeOffsetX, newEdgeOffsetY);
		
		placementX -= centerPos[0];
		placementZ -= centerPos[1];
		
		int[] pDustAmount = new int[1000];

		for (ItemStack is : player.inventory.mainInventory) {
			if (is != null) {
				if (is.getItem() == DustMod.itemDust) {
					pDustAmount[is.getItemDamage()] += is.stackSize;
				} else if (is.getItem() == DustMod.pouch) {
					int dustID = ItemPouch.getValue(is);
					int amt = ItemPouch.getDustAmount(is);
					pDustAmount[dustID] += amt;
				}
			}
		}

		ArrayList<ArrayList<int[][]>> blockData = updateData(rotatedData, newEdgeOffsetX, newEdgeOffsetY);
		int[] reduceDustAmount = new int[1000];

		int hasDrawn = 1;

		Random rand = new Random();
		for (int check = 0; check < this.width * this.height * 2 && hasDrawn > 0; check++) {
			int rx = rand.nextInt(blockData.size());
			int rz = rand.nextInt(blockData.get(0).size());
			int[][] block = blockData.get(rx).get(rz);

			boolean empty = true;
			for (int iter = 0; iter < block.length && empty; iter++) {
				for (int jter = 0; jter < block[0].length && empty; jter++) {
					if (block[iter][jter] != 0)
						empty = false;
				}
			}
			if (empty) {
				continue;
			}

			Block otherBlock = world.getBlock(placementX + rx, y, placementZ + rz);
			int meta = world.getBlockMetadata(placementX + rx, y, placementZ + rz);
			if (!otherBlock.getMaterial().isReplaceable() && !DustMod.isDust(otherBlock)) {
				continue;
			}

			if (world.getBlock(placementX + rx, y - 1, placementZ + rz).getMaterial() == Material.air) {
				continue;
			}

			if (!DustMod.dust.canPlaceBlockAt(world, placementX + rx, y, placementZ + rz)) {
				continue;
			}

			if (otherBlock != DustMod.dust) {
				world.setBlock(placementX + rx, y, placementZ + rz, DustMod.dust, 0, 2);
			} else if (meta == BlockDust.DEAD_DUST) {
				world.setBlockMetadataWithNotify(placementX + rx, y, placementZ + rz, BlockDust.UNUSED_DUST, 2);
			} else if (meta != BlockDust.UNUSED_DUST) {
				continue;
			}
			
			TileEntityDust ted;
			TileEntity te = world.getTileEntity(placementX + rx, y, placementZ + rz);

			if (te != null && te instanceof TileEntityDust) {
				ted = (TileEntityDust) te;
			} else {
				DustMod.logger.info("CREATING TE");
				ted = new TileEntityDust();
				world.setTileEntity(placementX + rx, y, placementZ + rz, ted);
			}

			// ted.empty();

			int ix = rand.nextInt(4);
			int iz = rand.nextInt(4);

			int check2 = 16;
			while ((ted.getDust(ix, iz) != 0 || block[ix][iz] == 0) && check2 > 0) {
				ix = rand.nextInt(4);
				iz = rand.nextInt(4);
				check2--;
				continue;
			}
			int dust = block[ix][iz];
			if (ted.getDust(ix, iz) == 0 && dust != 0) {
				boolean canDraw = true;
				if (dust > 0 && !player.capabilities.isCreativeMode) {
					if (pDustAmount[dust] > 0) {
						reduceDustAmount[dust]++;
						pDustAmount[dust]--;
					} else {
						canDraw = false;
					}
				}
				if (canDraw) {
					ted.setDust(player, ix, iz, dust);
				}
			}
		}

		for (int bx = 0; bx < blockData.size(); bx++) {
			for (int bz = 0; bz < blockData.get(0).size(); bz++) {
				if (DustMod.isDust(world.getBlock(placementX + bx, y, placementZ + bz))) {
					TileEntityDust ted = (TileEntityDust) world.getTileEntity(placementX + bx, y, placementZ + bz);

					if (ted.isEmpty()) {
						world.setBlockToAir(placementX + bx, y, placementZ + bz);
					} else {
						world.markBlockForUpdate(placementX + bx, y, placementZ + bz);
					}
				}
			}
		}

		if (!player.capabilities.isCreativeMode) {
			for (int id = 1; id < 1000; id++) {
				for (int sind = 0; sind < player.inventory.mainInventory.length; sind++) {
					ItemStack is = player.inventory.mainInventory[sind];

					if (is != null && reduceDustAmount[id] > 0) {
						if (is.getItem() == DustMod.itemDust && is.getItemDamage() == id) {
							while (reduceDustAmount[id] > 0 && is.stackSize > 0) {
								is.stackSize--;

								if (is.stackSize == 0) {
									player.inventory.mainInventory[sind] = null;
								}

								reduceDustAmount[id]--;
							}
						} else if (is.getItem() == DustMod.pouch) {
							int did = ItemPouch.getValue(is);
							if (did == id) {
								while (reduceDustAmount[id] > 0 && ItemPouch.getDustAmount(is) > 0) {
									ItemPouch.subtractDust(is, 1);

									reduceDustAmount[id]--;
								}
							}
						}

					}
				}
			}
		}
		InventoryPlayer inv = player.inventory;
		for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
			inv.getStackInSlot(slot);
		}
		player.inventory.inventoryChanged = true;

		updateData();
		return true;
	}

	public boolean isEmpty(int[][] block) {
		for (int[] i : block) {
			for (int j : i) {
				if (j != 0) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean hasEnough(int[] dust) {
		for (int i = 1; i < 1000; i++) {
			if (dust[i] < dustAmt[i]) {
				DustMod.logger.debug("Not enough dust: " + i);
				// System.out.println("[DustMod] Not enough dust:" + i);
				return false;
			}
		}

		return true;
	}

	/**
	 * Set the proper name for the rune.
	 * 
	 * @param n
	 *            The name
	 * @return This DustShape
	 */
	public RuneShape setRuneName(String n) {
		pName = n;
		return this;
	}

	/**
	 * Set the mod-author's name of this rune
	 * 
	 * @param name
	 *            Your screenname
	 * @return this DustShape
	 */
	public RuneShape setAuthor(String name) {
		this.author = name;
		return this;
	}

	public String getRuneName() {
		if (pName.isEmpty()) {
			return name + ".propername";
		}
		return pName;
	}

	public String getDescription() {
		if (desc.isEmpty()) {
			return name + ".desc";
		}
		return desc;
	}

	public String getAuthor() {
		if (author.isEmpty()) {
			return name + ".author";
		}
		return author;
	}

	public String getNotes() {
		if (notes.isEmpty()) {
			return name + ".notes";
		}
		return notes;
	}
}
