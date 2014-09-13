package dustmod.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityDustTable extends TileEntity
{
    public int ticks;
    public float pageFlipping;
    public float prevPageFlipping;
    public float floatd;
    public float floate;
    public float floating;
    public float prevFloating;
    public float rotation;
    public float prevRotation;
    public float rotAmt;

    public int page = 0;

    public int dir = -1;
    
    public boolean init = false;

	public TileEntityDustTable() {
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		writeNetworkNBT(tag);
	}

	public void writeNetworkNBT(NBTTagCompound tag) {
		tag.setInteger("page", page);
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		readNetworkNBT(tag);
	}
    
    public void readNetworkNBT(NBTTagCompound tag) {
    	page = tag.getInteger("page");
    	
    	if (!init) {
    		pageFlipping = prevPageFlipping = floatd = (float) page / 2F;
    	}
    }

    public void updateEntity()
    {
    	if (!worldObj.isRemote) {
    		return;
    	}
    	
        dir = getBlockMetadata();
        floatd = (float)page / 2F;

        prevFloating = floating;
        prevRotation = rotation;
        EntityPlayer entityplayer = worldObj.getClosestPlayer((float)xCoord + 0.5F, (float)yCoord + 0.5F, (float)zCoord + 0.5F, 3D);

        if (entityplayer != null)
        {
            floating += 0.1F;
        }
        else
        {
            floating -= 0.1F;
        }

        rotation = prevRotation = rotAmt = dir * ((float)Math.PI / 2);

        if (floating < 0.0F)
        {
            floating = 0.0F;
        }

        if (floating > 1.0F)
        {
            floating = 1.0F;
        }

        ticks++;
        prevPageFlipping = pageFlipping;
        float f1 = (floatd - pageFlipping) * 0.4F;
        float f2 = 0.2F;

        if (f1 < -f2)
        {
            f1 = -f2;
        }

        if (f1 > f2)
        {
            f1 = f2;
        }

        floate += (f1 - floate) * 0.9F;
        pageFlipping = pageFlipping + floate;
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
    	init = true;
    	worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
