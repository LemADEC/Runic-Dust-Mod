/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.runes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import dustmod.DustEvent;
import dustmod.DustMod;
import dustmod.EntityDust;
import dustmod.Sacrifice;

/**
 *
 * @author billythegoat101
 */
public class DESilkTouchEnch extends DustEvent
{
    public DESilkTouchEnch()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityDust e){
    	super.initGraphics(e);

		e.setRenderStar(true);
		e.setRenderBeam(true);
        e.setColorStarOuter(0, 0, 255);
        e.setColorBeam(0, 0, 255);
		
    }

    public void onInit(EntityDust e)
    {
        List<EntityItem> sacrifice = getItems(e);
        Item item = null;

        for (EntityItem i: sacrifice)
        {
            ItemStack is = i.getEntityItem();

            if (is.getItem() == Items.diamond_pickaxe || is.getItem() == Items.diamond_shovel)
            {
                item = is.getItem();
//                i.setDead();
                break;
            }
        }
        
        if (item == null) {
        	e.fizzle();
        	return;
        }

        ItemStack[] req = this.sacrifice(e, new ItemStack[] {new ItemStack(item, 1, 0), new ItemStack(Blocks.gold_block, 1, 0)});

        if (!checkSacrifice(req) || !takeXP(e, 10) || item == null)
        {
            e.fizzle();
            return;
        }

		e.setRenderStar(true);
		e.setRenderBeam(true);
        e.setColorStarOuter(0, 0, 255);
        e.setColorBeam(0, 0, 255);
//        e.data = item;
        e.data[1] = Item.getIdFromItem(item); //the sacrifice entity id will be set to data
//        e.sacrificeWaiting = 600;
//        this.addSacrificeList(new Sacrifice(120));
    }

    public void onTick(EntityDust e)
    {
        e.setStarScale(e.getStarScale() + 0.001F);

        DustMod.log("GROW");
        if (e.ticksExisted > 20)
        {
        	DustMod.log("Drop");
            Entity en = null;
            ItemStack create =  new ItemStack(Item.getItemById(e.data[1]), 1, 0);
//            if(e.data == mod_DustMod.spiritSword.itemID){
            create.addEnchantment(Enchantment.silkTouch, 1);
//            }
//            System.out.println("derp " + create.itemID);
            en = new EntityItem(e.worldObj, e.posX, e.posY - EntityDust.yOffset, e.posZ, create);

            if (en != null)
            {
                en.setPosition(e.posX, e.posY, e.posZ);
                e.worldObj.spawnEntityInWorld(en);
            }

            e.fade();
        }
    }
}
