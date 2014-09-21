/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.WorldServer;
import dustmod.DustMod;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RESpawnerReprog extends RuneEvent
{
    public RESpawnerReprog()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

		e.setRenderStar(true);
//		e.setRenderBeam(true);
        e.setStarScale(1.75F);
		
    }

    public void onInit(EntityRune e)
    {
//        int compare = mod_DustMod.compareDust(mod_DustMod.lapisDID, e.dustID);
//        if(compare < 0){
//            e.fizzle();
//            return;
//        }
		e.setRenderStar(true);
		e.setRenderBeam(true);
        e.setStarScale(1.05F);
        int entClass = -1;
        List l = getEntities(e);

        for (Object o: l)
        {
            if (o instanceof EntityItem)
            {
                EntityItem ei = (EntityItem)o;
                ItemStack item = ei.getEntityItem();

                if (item.getItem() == Items.spawn_egg)
                {
                    entClass = item.getItemDamage();
                    item.stackSize--;

                    if (item.stackSize <= 0)
                    {
                        ei.setDead();
                    }else{
                    	ei.setEntityItemStack(item);
                    }
                }
            }
        }

        ItemStack[] req = new ItemStack[] {new ItemStack(Items.ender_pearl, 2)};
        req = this.sacrifice(e, req);

        if (!checkSacrifice(req) || entClass == -1 || !takeXP(e, 10))
        {
            e.fizzle();
            return;
        }

        e.data[0] = entClass;
    }

    public void onTick(EntityRune e)
    {
        if (e.ticksExisted > 120)
        {
            String mob = EntityList.getEntityString(EntityList.createEntityByID(e.data[0], e.worldObj));
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
                TileEntityMobSpawner tems = ((TileEntityMobSpawner)e.worldObj.getTileEntity(fin[0], fin[1], fin[2]));
                tems.func_145881_a().setEntityName(mob);
                tems.validate();
                
//                e.worldObj.setBlockWithNotify(fin[0], fin[1], fin[2],0);
//                e.worldObj.setBlockWithNotify(fin[0], fin[1], fin[2], Block.mobSpawner.blockID);
                e.worldObj.markBlockForUpdate(fin[0], fin[1], fin[2]);
                e.worldObj.setTileEntity(fin[0], fin[1], fin[2],tems);
                //TODO neccessary? e.worldObj.notifyBlockChange(fin[0], fin[1], fin[2],e.worldObj.getBlock(fin[0], fin[1], fin[2]));
                
                
                //TODO neccessary? PacketDispatcher.sendPacketToAllAround(fin[0], fin[1], fin[2], 64, e.worldObj.getWorldInfo().getDimension(), tems.getDescriptionPacket());
//                if(e.ticksExisted > 100){
//                    e.worldObj.setBlockWithNotify(fin[0],fin[1],fin[2],0);
//                    e.worldObj.markBlockNeedsUpdate(fin[0],fin[1],fin[2]);
//                    EntityItem ei = new EntityItem(e.worldObj);
//                    ei.setPosition(e.posX,e.posY+e.yOffset,e.posZ);
//                    ei.item = new ItemStack(Block.mobSpawner, 1);
//                    e.worldObj.spawnEntityInWorld(ei);
//                }
            }

//            if(en != null){
//                en.setPosition(e.posX, e.posY-EntityDust.yOffset, e.posZ);
//                boolean blah = e.worldObj.spawnEntityInWorld(en);
//            }
            e.fade();
        }
        else   //if(e.ticksExisted < 10){
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
            TileEntityMobSpawner tems = ((TileEntityMobSpawner)e.worldObj.getTileEntity(fin[0], fin[1], fin[2]));
            tems.func_145881_a().field_98287_c = 0;
        }
    }
}