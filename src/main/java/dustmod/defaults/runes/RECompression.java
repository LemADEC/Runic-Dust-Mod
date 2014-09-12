/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import dustmod.DustMod;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RECompression extends RuneEvent
{
    public RECompression()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

        e.setRenderBeam(true);
        e.setRenderStar(true);
        e.setColorStarOuter(0, 0, 255);
        e.setColorBeam(0, 0, 255);
    	
    }

    public void onInit(EntityRune e)
    {
    	if (!this.takeItems(e, new ItemStack(Blocks.iron_block, 1, -1)))
        {
            e.fizzle();
            return;
        }
    	//Cant use negator
    	if (this.takeItems(e, new ItemStack(DustMod.getNegator(), 1, -1)))
        {
            e.fizzle();
            return;
        }

        int diamondAmt = 0;
        ItemStack[] req = new ItemStack[]
        {
            new ItemStack(Items.coal, 0, 0)
        };

        while (req[0].stackSize == 0)
        {
            req[0].stackSize = 32;
            req = sacrifice(e, req);

            if (req[0].stackSize <= 0)
            {
                diamondAmt++;
            }

//            System.out.println("DERP : " + diamondAmt + " " + req[0].stackSize);
        }

        System.out.println("Diamond amt " + diamondAmt);
        e.data[0] = diamondAmt;
        e.setRenderBeam(true);
        e.setRenderStar(true);
        e.setColorStarOuter(0, 0, 255);
        e.setColorBeam(0, 0, 255);
    }

    public void onTick(EntityRune e)
    {
        e.setStarScale(e.getStarScale() + 0.001F);
        
        if (e.ticksExisted > 20)
        {
            int dAmt = e.data[0];
            int stacks = (dAmt) / 64;
            int leftover = dAmt % 64;
            System.out.println("Dropping " + dAmt + " diamonds in " + stacks + "." + leftover + " stacks");

            for (int i = 0; i < stacks; i++)
            {
                Entity en = null;
                ItemStack create =  new ItemStack(Items.diamond, 64, 0);
                en = new EntityItem(e.worldObj, e.posX, e.posY - EntityRune.yOffset, e.posZ, create);

                if (en != null)
                {
                    en.setPosition(e.posX, e.posY - EntityRune.yOffset, e.posZ);
                    e.worldObj.spawnEntityInWorld(en);
                }
            }

            if (leftover > 0)
            {
                Entity en = null;
                ItemStack create =  new ItemStack(Items.diamond, leftover, 0);
                en = new EntityItem(e.worldObj, e.posX, e.posY - EntityRune.yOffset, e.posZ, create);

                if (en != null)
                {
                    en.setPosition(e.posX, e.posY - EntityRune.yOffset, e.posZ);
                    e.worldObj.spawnEntityInWorld(en);
                }
            }

            e.fade();
        }
    }
}
