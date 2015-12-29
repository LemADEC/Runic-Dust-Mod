/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.runes;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import dustmod.blocks.TileEntityDust;
import dustmod.defaults.runes.REPowerRelay;

/**
 *
 * @author billythegoat101
 */
public abstract class PoweredEvent extends RuneEvent
{
    public static final int dayLength = 24000;
    public boolean consumeItems = true;
    public PoweredEvent()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);
    	
    	e.setRenderStar(true);
    	e.setStarScale(1.0F);
    	e.setColorStar(255,255,255);
		
    }

    @Override
    public void onInit(EntityRune e)
    {
        super.onInit(e);

        if (this.getClass() != REPowerRelay.class)
        {
            List<EntityRune> ents = REPowerRelay.findDustEntities(e);

            for (EntityRune i: ents)
            {
            	if(i.event == null) continue;
                if (i.event.getClass() == REPowerRelay.class)
                {
                    ((REPowerRelay)i.event).registerSelfTo(i, e);
                }
            }
        }

        e.setFuel(this.getStartFuel());
        e.requiresFuel = true;
    	e.setRenderStar(true);
    	e.setStarScale(1.0F);
    	e.setColorStar(255,255,255);
    }

    @Override
    public void onTick(EntityRune e)
    {
        super.onTick(e);

        if (e.getFuel() <= 0 && !this.isPaused(e))
        {
            e.fade();
            return;
        }

        if (!this.isPaused(e))
        {
            subtractFuel(e);
        }

        if (consumeItems)
        {
            List<Entity> ents = this.getEntitiesExcluding(e, 1.0D);

            for (Entity i: ents)
            {
                if (!i.isDead && i instanceof EntityItem)
                {
                    EntityItem ei = (EntityItem)i;
                    ei.attackEntityFrom(null, -20);
                    //                ei.delayBeforeCanPickup = 20;
                    ItemStack is = ei.getEntityItem();

                    if (TileEntityFurnace.getItemBurnTime(is) != 0)
                    {
                        addFuel(e, TileEntityFurnace.getItemBurnTime(is) * is.stackSize);
                        ei.setDead();
                        continue;
                    }
                }
            }
        }

        if (e.isFueledExternally())
        {
            e.setStarScale(1.04F);
        }
        else
        {
            e.setStarScale(1F);
        }

        if (!this.isPaused(e))
        {
            double powerPercent = (double)e.getFuel() / (double)this.getStableFuelAmount(e);
            int c = (int)(255D * powerPercent);

            if (c > 255)
            {
                c = 255;
            }

            e.setColorStar(255, c, c);
        }
        else
        {
            e.setColorStar(255, 255, 0);
        }

//        System.out.println("PowerPercent " + powerPercent + " Color " + c + " " + e.ri + ":" + e.gi + ":" + e.bi);
    }

    @Override
    public void onRightClick(EntityRune e, TileEntityDust ted, EntityPlayer p)
    {
        super.onRightClick(e, ted, p);
    }

    @Override
    public void onUnload(EntityRune e)
    {
        super.onUnload(e);

        if (this.getClass() != REPowerRelay.class)
        {
            List<EntityRune> ents = REPowerRelay.findDustEntities(e);

            for (EntityRune i: ents)
            {
            	if(i.event == null) continue;
                if (i.event.getClass() == REPowerRelay.class)
                {
                    ((REPowerRelay)i.event).removeSelfFrom(i, e);
                }
            }
        }
    }

    public void subtractFuel(EntityRune e)
    {
        e.setFuel(e.getFuel() - 1);
    }
    public void addFuel(EntityRune e, int amt)
    {
        if (e.getFuel() + amt > this.getMaxFuel())
        {
            return;
        }

        e.setFuel(e.getFuel() + amt);
    }

    public abstract int getStartFuel();     //amount of fuel to start with
    public abstract int getMaxFuel();       //Max amount of fuel
    public abstract int getStableFuelAmount(EntityRune e);    //amount of fuel requested
    public abstract boolean isPaused(EntityRune e); //pause the fuel depletion

    /**
     * Called to check if the rune is requesting anymore power or if it is 
     * currently stable where it is.
     * @param e EntityDust instance
     * @return The amount of power it wants, if needed
     */
    public int powerWanted(EntityRune e)
    {
        int cur = e.getFuel();
        int rtn = 0;
        int stable = this.getStableFuelAmount(e);

//        System.out.println("DICKS " + stable + " " + cur + " " + (stable-cur) + " " + this);
        if (stable > cur)
        {
            rtn = stable - cur;
        }

        return rtn;
    }
}
