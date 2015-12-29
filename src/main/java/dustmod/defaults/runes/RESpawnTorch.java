/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import dustmod.DustMod;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RESpawnTorch extends RuneEvent
{
    public RESpawnTorch()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

		e.setRenderStar(true);
		
    }

    public void onInit(EntityRune e)
    {
        e.data[0] = 0;
        ItemStack[] sac = new ItemStack[] {new ItemStack(Items.flint, 1)};
        sac = this.sacrifice(e, sac);

        if (sac[0].stackSize <= 0)
        {
            e.data[0] = 1;
        }

        World world = e.worldObj;
        int x = e.getX();
        int y = e.getY();
        int z = e.getZ();

        if (e.data[0] == 1)
        {
    		e.setRenderBeam(true);
            e.setColorBeam(255,255,255);
        }
        else
        {
        	e.setIgnoreRune(true);
            e.posY += 0.35;
    		e.setRenderStar(true);
    		e.setRenderBeam(false);
        }
    }

    @Override
    protected void onTick(EntityRune entityRune)
    {
        super.onTick(entityRune);
        if(entityRune.data[0] == 0 && entityRune.ticksExisted == 0){
            World world = entityRune.worldObj;
            int x = entityRune.getX();
            int y = entityRune.getY();
            int z = entityRune.getZ();
            world.setBlockToAir(x, y, z);
            world.setBlock(x, y, z, Blocks.torch, 0,3);
        }
        
        if(entityRune.data[0] == 1 && entityRune.ticksExisted % 10 == 0){
            List<EntityItem> items = this.getItems(entityRune, 1.0D);
            for(EntityItem i:items){
                ItemStack item = i.getEntityItem();
                if(item.getItem() == Items.dye && entityRune.data[1] != item.getItemDamage()){
                    entityRune.data[1] = item.getItemDamage();
                    int[] color = this.getColor(item.getItemDamage());
                    entityRune.setColorBeam(color[0], color[1], color[2]);
                    item.stackSize--;
                    if(item.stackSize <= 0){
                        i.setDead();
                        break;
                    }else{
                    	i.setEntityItemStack(item);
                    }
                }
            }
            
            if(entityRune.getRenderBeam() && entityRune.isPowered()){
            	entityRune.setRenderBeam(false);
            	entityRune.updateDataWatcher();
            }else if(!entityRune.getRenderBeam() && !entityRune.isPowered()){
            	entityRune.setRenderBeam(true);
            	entityRune.updateDataWatcher();
            }
        }
        if (entityRune.data[0] == 0)
        {
            if (entityRune.worldObj.getBlock(entityRune.getX(), entityRune.getY(), entityRune.getZ()) != Blocks.torch)
            {
                entityRune.fade();
                entityRune.kill();
            }
        }
    }
    
    public int[] getColor(int meta){
        int[][] ref = new int[][]
        {
            {25,21,21},
            {151,52,49},
            {53,71,28},
            {80,51,31},
            {47,57,142},
            {126,61,181},
            {46,110,136},
            {155,161,161},
            {64,64,64},
            {208,132,153},
            {66,175,57},
            {178,167,40},
            {106,137,200},
            {180,80,189},
            {219,124,61},
            {255,255,255},
        };
        return ref[meta];
    }
    
}
