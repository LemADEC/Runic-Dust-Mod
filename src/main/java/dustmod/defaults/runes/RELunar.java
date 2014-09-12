/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RELunar extends RuneEvent
{
    public static boolean oneActive = false;
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

        e.setRenderStar(true);
		
    }
	
    public void onInit(EntityRune e)
    {
        e.setRenderStar(true);
        ItemStack[] req = this.sacrifice(e, new ItemStack[] {new ItemStack(Items.nether_wart, 4), new ItemStack(Items.dye, 1, 4)});

        if (req[0].stackSize != 0 || req[1].stackSize != 0)
        {
            e.fizzle();
            return;
        }

        e.data[0] = (byte)0;
    }

    public void onTick(EntityRune e)
    {
        if (e.data[0] == 1 && !oneActive)
        {
            oneActive = false;
            e.data[0] = 0;
    		e.setRenderBeam(false);
            e.setRenderStar(true);
        }

        long time = e.worldObj.getWorldTime() + 1000;

        if (e.data[0] == 1 && e.worldObj.isDaytime())
        {
            e.worldObj.setWorldTime(e.worldObj.getWorldTime() + 25);
        }
        else if (e.data[0] == 0 && e.worldObj.isDaytime() && !oneActive)
        {
            oneActive = true;
            e.data[0] = (byte)1;
        }

        if (e.data[0] == 1)
        {
    		e.setRenderBeam(true);
    		e.setRenderStar(false);
        }

        if (e.data[0] == 1 && !e.worldObj.isDaytime())
        {
            e.kill();
        }
    }

    @Override
    protected void onUnload(EntityRune e)
    {
        super.onUnload(e);

        if (e.data[0] == 1)
        {
            oneActive = false;
        }
    }
}