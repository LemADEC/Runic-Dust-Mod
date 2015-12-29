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
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REPoisonTrap extends RETrap
{
    @Override
    public void onInit(EntityRune e)
    {
        super.onInit(e);
        ItemStack[] sac = new ItemStack[] {new ItemStack(Items.spider_eye, 1)};
        sac = this.sacrifice(e, sac);

        if (sac[0].stackSize > 0)
        {
            e.fizzle();
            return;
        }
    }

    @Override
    public void trigger(EntityRune e, int dustLevel)
    {
        int rad = 0;
        int poisondambase = 0;
        int poisondamrand = 0;

        switch (dustLevel)
        {
            case 200:
                rad = 3;
                poisondambase = 5;
                poisondamrand = 2;
                break;

            case 300:
                rad = 4;
                poisondambase = 7;
                poisondamrand = 4;
                break;

            case 400:
                rad = 6;
                poisondambase = 10;
                poisondamrand = 8;
                break;
        }

        List<Entity> kill = getEntitiesExcluding(e, rad);

        for (Entity k: kill)
        {
            if (k instanceof EntityLiving)
            {
                ((EntityLiving)k).addPotionEffect(new PotionEffect(Potion.poison.id, (poisondambase + ((int)Math.floor(Math.random() * (double)poisondamrand))) * 20, 2));
            }
        }

        e.fade();
    }
}
