/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REHeal extends RuneEvent
{
    public REHeal()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

        e.setStarScale(1.12F);
        e.setColorStarInner(255, 255, 255);
        e.setColorStarOuter(255, -255, -255);
		e.setRenderBeam(true);
		
    }

    public void onInit(EntityRune e)
    {
        ItemStack[] req = new ItemStack[] {new ItemStack(Items.coal, 2, -1)};
        req = this.sacrifice(e, req);

        if (!checkSacrifice(req) || !takeXP(e, 2))
        {
            e.fizzle();
            return;
        }

        e.setStarScale(1.12F);
        e.setColorStarInner(255, 255, 255);
        e.setColorStarOuter(255, -255, -255);
		e.setRenderBeam(true);
        //entityplayersp.addPotionEffect(new PotionEffect(Potion.regeneration.id, 3, 2));
    }

    public void onTick(EntityRune e)
    {
    	if(e.ticksExisted == 0){


            int dustID = e.dustID;
            int healMul = 1;
            int healDurBase = 0;

            switch (dustID)
            {
                case 100:
                    healMul = 1;
                    healDurBase = 4; //3 hearts
                    break;

                case 200:
                    healMul = 2;
                    healDurBase = 5; //n-2 hearts
                    break;

                case 300:
                    healMul = 2;
                    healDurBase = 10;
                    break;

                case 400:
                    healMul = 5;
                    healDurBase = 32;
                    break;
            }

            List<Entity> ents = getEntitiesExcluding(e, 5D);

            for (Entity i: ents)
            {
                if (i instanceof EntityLiving)
                {
                    EntityLiving l = (EntityLiving)i;
//                    System.out.println("DURR heal");
                    l.addPotionEffect(new PotionEffect(Potion.regeneration.id, healDurBase * 20, healMul));
                }

                if (i instanceof EntityPlayer)
                {
                    EntityPlayer p = (EntityPlayer)i;

                    if (dustID == 3)
                    {
                        p.getFoodStats().addStats(5, 0.6F);
                    }
                    else if (dustID == 4)
                    {
                        p.getFoodStats().addStats(8, 0.8F);
                    }
                }
            }
    	}
        if (e.ticksExisted > 100)
        {
            e.fade();
        }
    }
}
