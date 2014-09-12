/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import dustmod.runes.EntityRune;
import dustmod.runes.PoweredEvent;

/**
 *
 * @author billythegoat101
 */
public class REFireRain extends PoweredEvent
{
    public REFireRain()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

		e.setRenderBeam(true);
        e.setColorStarOuter(255, 0, 0);
        e.setColorBeam(255, 0, 0);
    	
    }

    public void onInit(EntityRune e)
    {
        super.onInit(e);
		e.setRenderBeam(true);
        e.setColorBeam(255, 0, 0);
        ItemStack[] req = new ItemStack[] {new ItemStack(Items.blaze_rod, 2)};
        req = this.sacrifice(e, req);

        if (!checkSacrifice(req))
        {
            e.fizzle();
            return;
        }
    }

    public void onTick(EntityRune e)
    {
        super.onTick(e);
        int rad = 100;
        int amt = 20;

        for (int i = 0; i < amt && e.ticksExisted % 5 == 0; i++)
        {
            EntityArrow ea = new EntityArrow(e.worldObj, e.posX + Math.random() * rad * 2 - rad, 158, e.posZ + Math.random() * rad * 2 - rad);
            ea.motionX = 0;
            ea.motionY = -2D;
            ea.motionZ = 0;
            ea.setFire(100);
            ea.canBePickedUp = 0;
            e.worldObj.spawnEntityInWorld(ea);
        }
    }

    @Override
    public int getStartFuel()
    {
        return dayLength / 2;
    }

    @Override
    public int getMaxFuel()
    {
        return dayLength * 3;
    }

    @Override
    public int getStableFuelAmount(EntityRune e)
    {
        return dayLength / 2;
    }

    @Override
    public boolean isPaused(EntityRune e)
    {
        return false;
    }
}
