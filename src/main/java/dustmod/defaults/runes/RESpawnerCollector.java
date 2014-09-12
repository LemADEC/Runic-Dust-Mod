/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityMobSpawner;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RESpawnerCollector extends RuneEvent
{
    public RESpawnerCollector()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

		e.setRenderBeam(true);
		e.setStarScale(1.05F);
		
    }
    public void onInit(EntityRune e)
    {
		e.setRenderBeam(true);
		e.setStarScale(1.05F);
        ItemStack[] req = new ItemStack[] {new ItemStack(Items.gold_ingot, 6)};
        req = this.sacrifice(e, req);

        if (!checkSacrifice(req) || !takeXP(e, 10))
        {
            e.fizzle();
            return;
        }
        

//        EntityItem ei = new EntityItem(e.worldObj);
//        ei.setPosition(e.posX, e.posY - e.yOffset, e.posZ);
//        ei.item = new ItemStack(Block.mobSpawner, 1);
//        e.worldObj.spawnEntityInWorld(ei);
    }

    public void onTick(EntityRune e)
    {
        int[] fin = new int[3];

        for (Integer[] i: e.dustPoints)
        {
            fin[0] += i[0];
            fin[1] += i[1];
            fin[2] += i[2];
        }

        fin[0] /= 8;
        fin[1] /= 8;
        fin[2] /= 8;

        if (e.worldObj.getBlock(fin[0], fin[1], fin[2]) == Blocks.mob_spawner)
        {
        	TileEntityMobSpawner tems =(TileEntityMobSpawner)e.worldObj.getTileEntity(fin[0], fin[1], fin[2]);
//        	String entID = tems.func_92015_a();
            tems.invalidate();

            if (e.ticksExisted > 100)
            {
                e.worldObj.setBlockToAir(fin[0], fin[1], fin[2]);
                EntityItem ei = new EntityItem(e.worldObj);
                ei.setPosition(e.posX, e.posY - e.yOffset, e.posZ);
                ItemStack item = new ItemStack(Blocks.mob_spawner, 1);
//                NBTTagCompound nbt = new NBTTagCompound();
//                item.setTagCompound(nbt);
//                nbt.setString("EntityID", entID); 
                ei.setEntityItemStack(item); 
                e.worldObj.spawnEntityInWorld(ei);
                e.worldObj.markBlockForUpdate(fin[0], fin[1], fin[2]);
            }
        }

        if (e.ticksExisted > 100)
        {
            e.fade();
//            e.worldObj.setBlock(fin[0], fin[1]-1, fin[2], Block.brick.blockID);
        }
    }
}
