/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RELightning extends RETrap
{
	
    @Override
    public void onInit(EntityRune e)
    {
        super.onInit(e);
        ItemStack[] sac = new ItemStack[] {new ItemStack(Items.iron_ingot, 3)};
        sac = this.sacrifice(e, sac);

        if (sac[0].stackSize > 0)
        {
            e.fizzle();
            return;
        }
    }

    public RELightning()
    {
        super();
    }
    public void trigger(EntityRune e, int level)
    {
        List<Entity> entities = getEntities(e, 2D * level/100);

        for (Entity i: entities)
        {
            if (i instanceof EntityLiving && e.getDistanceToEntity(i) < 2D * level/100)
            {
                e.worldObj.addWeatherEffect(new EntityLightningBolt(e.worldObj, e.posX, e.posY - EntityRune.yOffset, e.posZ));
                e.fade();
            }
        }
    }
}
