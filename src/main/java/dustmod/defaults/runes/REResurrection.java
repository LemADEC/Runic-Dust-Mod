/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import dustmod.DustMod;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REResurrection extends RuneEvent
{
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

		e.setRenderStar(true);
		e.setRenderBeam(true);
		
    }
	
    public void onInit(EntityRune e)
    {
		e.setRenderStar(true);
		e.setRenderBeam(true);
        ItemStack[] sac = new ItemStack[] {new ItemStack(Blocks.soul_sand, 4)};
        sac = this.sacrifice(e, sac);

        if (!checkSacrifice(sac))
        {
            e.fizzle();
            return;
        }

        //get sacrifice
        ArrayList<EntityItem> itemstacks = new ArrayList<EntityItem>();
        List l = getEntities(e);

        for (Object o: l)
        {
            if (o instanceof EntityItem)
            {
                EntityItem ei = (EntityItem)o;
                itemstacks.add(ei);
            }
        }

        if (itemstacks.size() == 0)
        {
            e.kill();
            return;
        }

        int entClass = -1;

        for (EntityItem ei: itemstacks)
        {
            if (entClass != -1)
            {
                break;
            }

            Item item = ei.getEntityItem().getItem();
            int m = ei.getEntityItem().getItemDamage();
            int amount;
            int amt = amount = 2;

            for (EntityItem ent: itemstacks)
            {
                if (ent.getEntityItem().getItem() == item && ent.getEntityItem().getItemDamage() == m)
                {
                    amount -= ent.getEntityItem().stackSize;
                }
            }

            if (amount <= 0 && DustMod.getEntityIDFromDrop(new ItemStack(item, 0, m), 0) != -1)
            {
                for (EntityItem ent: itemstacks)
                {
                    if (ent.getEntityItem().getItem() == item && ent.getEntityItem().getItemDamage() == m)
                    {
                        while (amt > 0 && ent.getEntityItem().stackSize > 0)
                        {
                            amt--;
                            ItemStack itemStack = ent.getEntityItem();
                            itemStack.stackSize--;
                            
                            if (itemStack.stackSize <= 0)
                            {
                                ent.setDead();
                            }else{
                            	ent.setEntityItemStack(itemStack);
                            }
                        }
                    }
                }

                entClass = DustMod.getEntityIDFromDrop(new ItemStack(item, 0, m), 0);

                if (entClass != -1)
                {
                    break;
                }
            }
        }

        if (entClass == -1)
        {
            e.fizzle();
            return;
        }

        e.data[0] = (byte)entClass;
        EntitySkeleton test;
    }

    public void onTick(EntityRune e)
    {
        e.setStarScale(e.getStarScale() + 0.001F);

        if (e.ticksExisted > 120)
        {
            Entity en = null;
            en = EntityList.createEntityByID((int)e.data[0], e.worldObj);

            if (en != null)
            {
                en.setPosition(e.posX, e.posY - EntityRune.yOffset, e.posZ);
                boolean blah = e.worldObj.spawnEntityInWorld(en);
            }

            e.fade();
        }
    }
}