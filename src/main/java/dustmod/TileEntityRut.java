/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

/**
 *
 * @author billythegoat101
 */
public class TileEntityRut extends TileEntity
{
	public static final int RUT_COUNT = 3;
	
    public static float hardnessStandard = -1;
    public Block maskBlock;
    public int maskMeta;
    public Block prevFluid;
    public Block fluid;
    public int[] ruts;
    public boolean isBeingUsed = false;
    public boolean isDead = false;
    public int ticksExisted = 0;
    
    public int dustEntID;

    private boolean hasFlame = false;
    private int fr,fg,fb; //flame rgb
    
    public boolean[][][] neighborSolid = null;

    public boolean changed = true;

    public TileEntityRut()
    {
    	if(hardnessStandard == -1){
    		 hardnessStandard = Blocks.gravel.getBlockHardness(worldObj,xCoord,yCoord,zCoord);
    	}
        ruts = new int[RUT_COUNT * RUT_COUNT * RUT_COUNT];

        for (int i = 0; i < RUT_COUNT * RUT_COUNT * RUT_COUNT; i++)
        {
            ruts[i] = 0;
        }
    }

    public boolean hasChanged()
    {
        boolean rtn = changed;
        changed = false;
        return rtn;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (neighborSolid == null)
        {
            neighborSolid = new boolean[3][3][3];
            updateNeighbors();
        }

        if (isEmpty() || (maskBlock instanceof BlockFalling && BlockFalling.func_149831_e(worldObj, xCoord, yCoord - 1, zCoord)))
        {
            isDead = true;
            worldObj.setBlock(xCoord, yCoord, zCoord, maskBlock, maskMeta, 3);
            this.invalidate();
            return;
        }

        if (worldObj.getWorldTime() % 14 == 0 && prevFluid == fluid && fluidIsFluid())
        {
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, maskMeta,0);
            int i = xCoord, j = yCoord, k = zCoord;
            super.updateEntity();

            for (int ix = -1; ix <= 1; ix++)
            {
                for (int iy = -1; iy <= 0; iy++)
                {
                    for (int iz = -1; iz <= 1; iz++)
                    {
                        if ((ix == -1 || ix == 1) && ix == iy && (iz == -1 || iz == 1))
                        {
                            continue;
                        }

                        if ((ix == -1 || ix == 1) && (iy == -1 || iy == 1) && ix != iy && (iz == -1 || iz == 1))
                        {
                            continue;
                        }

                        if (iy == 0 && (ix == -1 || ix == 1) && (iz == -1 || iz == 1))
                        {
                            continue;
                        }

                        if (worldObj.getBlock(i + ix, j + iy, k + iz) == DustMod.rutBlock)
                        {
                            TileEntityRut ter = (TileEntityRut)worldObj.getTileEntity(i + ix, j + iy, k + iz);

                            if (ter.fluid == null)
                            {
                                ter.setFluid(this.fluid);
                            }
                            else if (ter.fluid == Blocks.water && this.fluid == Blocks.lava)
                            {
                                ter.setFluid(Blocks.cobblestone);
                                this.setFluid(Blocks.cobblestone);
                            }
                            else if (this.fluid == Blocks.water && ter.fluid == Blocks.lava)
                            {
                                ter.setFluid(Blocks.cobblestone);
                                this.setFluid(Blocks.cobblestone);
                            }
                        }
                    }
                }
            }
        }

        if (worldObj.getWorldTime() % 60 == 0 && fluid == Blocks.air)
        {
            for (int ix = -1; ix <= 1; ix++)
            {
                for (int iy = -1; iy <= 1; iy++)
                {
                    for (int iz = -1; iz <= 1; iz++)
                    {
                        if (ix == iy || ix == iz || iy == iz)
                        {
                            Block check = worldObj.getBlock(xCoord + ix, yCoord + iy, zCoord + iz);

                            if (fluid == Blocks.air)
                            {
                                if (check == Blocks.lava)
                                {
                                    setFluid(Blocks.lava);
//                                    mod_DustMod.notifyBlockChange(worldObj, xCoord, yCoord, zCoord, 0);
                                }
                                else if (check == Blocks.water)
                                {
                                    setFluid(Blocks.water);
//                                    mod_DustMod.notifyBlockChange(worldObj, xCoord, yCoord, zCoord, 0);
                                }
                            }
                        }
                    }
                }
            }
        }

        prevFluid = fluid;
    }

    public boolean updateNeighbors()
    {
    	boolean rtn = false;
        if (neighborSolid == null)
        {
        	rtn = true;
            neighborSolid = new boolean[3][3][3];
        }

        changed = true;

        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++)
                for (int k = -1; k <= 1; k++)
                {
                	boolean prev = neighborSolid[i + 1][j + 1][k + 1]; 
                    Block block = worldObj.getBlock(xCoord + i, yCoord + j, zCoord + k);
                    boolean next = (block != null && (block.isOpaqueCube() || block == DustMod.rutBlock));
                    if(prev != next) rtn = true;
                    neighborSolid[i + 1][j + 1][k + 1] = next;
                }
        return rtn;
    }

    public boolean isNeighborSolid(int ix, int iy, int iz)
    {
        if (neighborSolid == null)
        {
            updateNeighbors();
        }

        return neighborSolid[ix + 1][iy + 1][iz + 1];
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        writeNetworkNBT(tag);
    }
    
    public void writeNetworkNBT(NBTTagCompound tag)
    {
        UniqueIdentifier maskIdent = GameRegistry.findUniqueIdentifierFor(maskBlock);
        UniqueIdentifier fluidIdent = GameRegistry.findUniqueIdentifierFor(fluid);

        tag.setString("maskBlock", maskIdent.modId + ":" + maskIdent.name);
        tag.setInteger("maskMeta", maskMeta);
        tag.setString("fluid", fluidIdent.modId + ":" + fluidIdent.name);
        tag.setBoolean("isBeingUsed", isBeingUsed);
        
        tag.setIntArray("ruts", ruts);
        
        tag.setBoolean("flame", hasFlame);
        tag.setInteger("flameR", fr);
        tag.setInteger("flameG", fg);
        tag.setInteger("flameB", fb);
    }
    
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        readNetworkNBT(tag);
    }
    
    public void readNetworkNBT(NBTTagCompound tag)
    {
    	if (tag.hasKey("maskBlock"))
        {
        	String[] ids = tag.getString("maskBlock").split(":", 2);
        	
            maskBlock = GameRegistry.findBlock(ids[0], ids[1]);
        }
        else
        {
            maskBlock = Blocks.crafting_table;
        }

        if (tag.hasKey("maskMeta"))
        {
            maskMeta = tag.getInteger("maskMeta");
        }
        else
        {
            maskMeta = 2;
        }

        if (tag.hasKey("fluid"))
        {
        	String[] ids = tag.getString("fluid").split(":", 2);
        	
        	fluid = GameRegistry.findBlock(ids[0], ids[1]);
        }
        else
        {
            fluid = Blocks.water;
        }

        if (tag.hasKey("isBeingUsed"))
        {
            isBeingUsed = tag.getBoolean("isBeingUsed");
        }
        
        if (tag.hasKey("ruts"))
        {
        	ruts = tag.getIntArray("ruts");
        }
        
        if(tag.hasKey("flame")){
        	this.hasFlame = tag.getBoolean("flame");
        	fr = tag.getInteger("flameR");
        	fg = tag.getInteger("flameG");
        	fb = tag.getInteger("flameB");
        }
    }

//    public void onNeighborChange(){
//
//    }
//    public boolean isValidNeighbor(Block b){
//        return b == null || (!b.isOpaqueCube() && b != mod_DustMod.rutBlock);
//    }

    public void setRut(EntityPlayer p, int i, int j, int k, int l)
    {
    	if(p != null && !worldObj.canMineBlock(p, this.xCoord, this.yCoord, this.zCoord)) return;
        if (isBeingUsed)
        {
            return;
        }

        changed = true;

        if (canEdit())
        {
            if ((i == 0 || i == 2) && i == j && (k == 0 || k == 2))
            {
                return;
            }

            if ((i == 0 || i == 2) && (j == 0 || j == 2) && i != j && (k == 0 || k == 2))
            {
                return;
            }

            ruts[i * RUT_COUNT * RUT_COUNT + j * RUT_COUNT + k] = l;
        }

//        System.out.println("Setting [" + i + "," + j + "," + k + "]");
        worldObj.notifyBlockChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
        worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
    }
    public int getRut(int i, int j, int k)
    {
        return ruts[i * RUT_COUNT * RUT_COUNT + j * RUT_COUNT + k];
    }

    public void setRenderFlame(boolean val, int r, int g, int b){
    	this.hasFlame = val;
    	this.fr = r;
    	this.fg = g;
    	this.fb = b;
    	worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

	public boolean hasFlame() {
		return hasFlame;
	}
	public int[] getFlameColor(){
		return new int[]{fr,fg,fb};
	}
    
    public void resetBlock()
    {
        isDead = true;
        worldObj.setBlock(xCoord, yCoord, zCoord, maskBlock, maskMeta,3);
    }

    public boolean fluidIsFluid()
    {
        return (fluid == null || fluid == Blocks.water || fluid == Blocks.lava);
    }

    public void setFluid(Block fluid)
    {
        if (this.fluid != fluid)
        {
            this.fluid = fluid;
            worldObj.notifyBlockChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
            changed = true;
            worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
        }
    }
    public boolean canEdit()
    {
        return (fluidIsFluid() || fluid.getBlockHardness(worldObj,xCoord,yCoord,zCoord) <= hardnessStandard || DustMod.Enable_Decorative_Ruts) && !isBeingUsed;
    }

    public boolean isEmpty()
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                for (int k = 0; k < 3; k++)
                {
                    if (getRut(i, j, k) != 0)
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public Packet getDescriptionPacket()
    {
    	NBTTagCompound tag = new NBTTagCompound();
        writeNetworkNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }
    
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    	readNetworkNBT(pkt.func_148857_g());
    	worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
