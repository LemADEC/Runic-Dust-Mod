/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RESpeed extends RuneEvent
{
    public RESpeed()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

        e.setStarScale(1.12F);
        e.setColorStarOuter(0, 255, 0);
        e.setRenderStar(true);
		
    }
    
    public void onInit(EntityRune entityRune)
    {
        ItemStack[] req = new ItemStack[]
        {
            new ItemStack(Items.sugar, 3, -1),
            new ItemStack(Items.blaze_powder, 1, -1),
        };
        sacrifice(entityRune, req);

        if (req[0].stackSize > 0 || req[1].stackSize > 0)
        {
            entityRune.fizzle();
            return;
        }

        int dustId = entityRune.dusts[entityRune.dusts.length - 1][entityRune.dusts[0].length - 1];
        int p = 0;
        int d = 0;

        switch (dustId)
        {
            case 100:
                p = 1;
                d = 25 * 30;
                break;

            case 200:
                p = 1;
                d = 25 * 60;
                break;

            case 300:
                p = 2;
                d = 25 * 120;
                break;

            case 400:
                p = 4;
                d = 25 * 180;
                break;
        }

        List<Entity> ents = this.getEntitiesExcluding(entityRune, 3D);

        for (Entity i: ents)
        {
            if (i instanceof EntityLiving)
            {
                ((EntityLiving)i).addPotionEffect(new PotionEffect(Potion.moveSpeed.id, d, p));
            }
        }

        entityRune.setStarScale(1.12F);
        entityRune.setColorStarOuter(0, 255, 0);
        entityRune.setRenderStar(true);
        entityRune.fade();
    }

    public void onTick(EntityRune e)
    {
    }
}
