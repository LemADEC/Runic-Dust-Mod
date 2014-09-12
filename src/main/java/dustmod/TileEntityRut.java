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
import net.minecraftforge.fluids.FluidRegistry;
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
    public boolean[] ruts;
    public boolean isBeingUsed = false;
    public int ticksExisted = 0;
    
    public int dustEntID;

    private boolean hasFlame = false;
    private int fr,fg,fb; //flame rgb
    
    public boolean[] neighborSolid = null;

    public TileEntityRut()
    {
    	if(hardnessStandard == -1){
    		 hardnessStandard = Blocks.gravel.getBlockHardness(worldObj,xCoord,yCoord,zCoord);
    	}
    	
        ruts = new boolean[RUT_COUNT * RUT_COUNT * RUT_COUNT];
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        if (neighborSolid == null)
        {
            updateNeighbors();
        }

        if (isEmpty() || (maskBlock instanceof BlockFalling && BlockFalling.func_149831_e(worldObj, xCoord, yCoord - 1, zCoord)))
        {
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
            neighborSolid = new boolean[3 * 3 * 3];
        }

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
					boolean prev = neighborSolid[(x + 1) * 9 + (y + 1) * 3 + z + 1];
					Block block = worldObj.getBlock(xCoord + x, yCoord + y, zCoord + z);
					boolean next = (block != null && (block.isOpaqueCube() || block == DustMod.rutBlock));

					if (prev != next)
						rtn = true;

					neighborSolid[(x + 1) * 9 + (y + 1) * 3 + z + 1] = next;
                }
            }
        }
        return rtn;
    }

    public boolean isNeighborSolid(int ix, int iy, int iz)
    {
        if (neighborSolid == null)
        {
            updateNeighbors();
        }

        return neighborSolid[(ix + 1) * 9 + (iy + 1) * 3 + iz + 1];
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

        if (maskIdent != null) {
        	tag.setString("maskBlock", maskIdent.modId + ":" + maskIdent.name);
        	tag.setInteger("maskMeta", maskMeta);
        }
        if (fluidIdent != null) {
        	tag.setString("fluid", fluidIdent.modId + ":" + fluidIdent.name);
        }
        
        tag.setBoolean("isBeingUsed", isBeingUsed);
        
        int rutValue = 0;
        
        for (int i = 0; i < RUT_COUNT * RUT_COUNT * RUT_COUNT; i++) {
        	if (ruts[i]) {
        		rutValue |= 1 << i;
        	}
        }
        
        tag.setInteger("ruts", rutValue);
        
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
            fluid = null;
        }

        if (tag.hasKey("isBeingUsed"))
        {
            isBeingUsed = tag.getBoolean("isBeingUsed");
        }
        
        if (tag.hasKey("ruts"))
        {
        	int rutValue = tag.getInteger("ruts");
        	
            for (int i = 0; i < RUT_COUNT * RUT_COUNT * RUT_COUNT; i++) {
            	ruts[i] = (rutValue & (1 << i)) != 0;
            }
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

    public void setRut(EntityPlayer p, int x, int y, int z, boolean value)
    {
    	if(p != null && !worldObj.canMineBlock(p, this.xCoord, this.yCoord, this.zCoord)) return;
        if (isBeingUsed)
        {
            return;
        }

        if (canEdit())
        {
        	// Center
        	if (x == 1 && y == 1 && z == 1) {
        		return;
        	}
        	
        	// Edges
        	if (x != 1 && y != 1 && z != 1) {
        		return;
        	}

        	int index = x + y * RUT_COUNT + z * RUT_COUNT * RUT_COUNT;
        	if (ruts[index] != value) {
        		ruts[index] = value;
        		markDirty();
        		worldObj.notifyBlockChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
                worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
        	}
        }
    }
    
    public void toggleRut(EntityPlayer p, int x, int y, int z) {
    	if (p != null && !worldObj.canMineBlock(p, this.xCoord, this.yCoord, this.zCoord)) return;
        if (isBeingUsed)
        {
            return;
        }

        if (canEdit())
        {
        	// Center
        	if (x == 1 && y == 1 && z == 1) {
        		return;
        	}
        	
        	// Edges
        	if (x != 1 && y != 1 && z != 1) {
        		return;
        	}

        	int index = x + y * RUT_COUNT + z * RUT_COUNT * RUT_COUNT;
        	
            ruts[index] = !ruts[index];
            
            markDirty();
            worldObj.notifyBlockChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
            worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
        }
        
    }
    
    public boolean getRut(int x, int y, int z)
    {
        return ruts[x + y * RUT_COUNT + z * RUT_COUNT * RUT_COUNT];
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
        this.invalidate();
        worldObj.setBlock(xCoord, yCoord, zCoord, maskBlock, maskMeta, 3);
    }

    public boolean fluidIsFluid()
    {
    	return FluidRegistry.lookupFluidForBlock(fluid) != null;
    }

    public void setFluid(Block fluid)
    {
        if (this.fluid != fluid)
        {
            this.fluid = fluid;
            markDirty();
            worldObj.notifyBlockChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
            worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
        }
    }
    public boolean canEdit()
    {
        return (fluid == null || fluidIsFluid() || fluid.getBlockHardness(worldObj,xCoord,yCoord,zCoord) <= hardnessStandard || DustMod.Enable_Decorative_Ruts) && !isBeingUsed;
    }

    public boolean isEmpty()
    {
    	for (boolean rut: ruts) {
    		if (rut) {
    			return false;
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
