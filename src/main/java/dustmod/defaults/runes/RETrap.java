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
    public void onTick(EntityRune entityRune)
    {
		entityRune.setRenderStar(true);

        if (entityRune.ticksExisted < 80)
        {
            entityRune.setColorStarInner(140, 140, 140);
            entityRune.setColorStarOuter(140, 140, 140);
            return;
        }

        entityRune.setColorStarInner(0, 0, 255);
        entityRune.setColorStarOuter(0, 0, 255);
        List<Entity> entities = getEntitiesExcluding(entityRune, entityRune.worldObj, entityRune.posX, entityRune.posY, entityRune.posZ, 2D);

        if (entities.size() > 0)
        {
            trigger(entityRune, entityRune.dustID);
            entityRune.fade();
        }
    }

    public abstract void trigger(EntityRune e, int dustLevel);
}
