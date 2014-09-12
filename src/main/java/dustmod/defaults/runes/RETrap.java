/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.entity.Entity;
import dustmod.DustMod;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public abstract class RETrap extends RuneEvent
{
    protected double range = 1D;

    public RETrap()
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
        int compare = DustMod.compareDust(DustMod.gunDID, e.dustID);

        if (compare < 0)
        {
            e.fizzle();
            return;
        }

		e.setRenderStar(true);
    }
    public void onTick(EntityRune e)
    {
		e.setRenderStar(true);

        if (e.ticksExisted < 80)
        {
            e.setColorStarInner(140, 140, 140);
            e.setColorStarOuter(140, 140, 140);
            return;
        }

        e.setColorStarInner(0, 0, 255);
        e.setColorStarOuter(0, 0, 255);
        List<Entity> entities = this.getEntitiesExcluding(e.worldObj, e, e.posX, e.posY, e.posZ, 2D);

        if (entities.size() > 0)
        {
            trigger(e, e.dustID);
            e.fade();
        }
    }

    public abstract void trigger(EntityRune e, int dustLevel);
}
