/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REFortuneEnch extends RuneEvent
{
    public REFortuneEnch()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

		e.setRenderStar(true);
		e.setRenderBeam(true);
        e.setColorStarOuter(0, 0, 255);
        e.setColorBeam(0,0,255);
		
    }

    public void onInit(EntityRune e)
    {
        List<EntityItem> sacrifice = getItems(e);
        Item item = Items.diamond_pickaxe;

        for (EntityItem i: sacrifice)
        {
            ItemStack is = i.getEntityItem();

            if (is.getItem() == Items.diamond_pickaxe || is.getItem() == Items.diamond_sword)
            {
                item = is.getItem();
                break;
            }
        }

//        int gold = ((item == Item.pickaxeDiamond.itemID) ? Item.pickaxeGold.itemID:Item.swordGold.itemID);
        ItemStack[] req = this.sacrifice(e, new ItemStack[] {new ItemStack(item, 1, 0),
                      new ItemStack(Blocks.diamond_ore, 1, 0),
                      new ItemStack(Blocks.redstone_ore, 1, 0),
                      new ItemStack(Blocks.lapis_ore, 1, 0)
        });

        if (!checkSacrifice(req) || !takeXP(e, 15))
        {
            e.fizzle();
            return;
        }

		e.setRenderStar(true);
		e.setRenderBeam(true);
        e.setColorStarOuter(0, 0, 255);
        e.setColorBeam(0,0,255);
        e.data[0] = Item.getIdFromItem(item);
    }

    public void onTick(EntityRune e)
    {
        e.setStarScale(e.getStarScale() + 0.001F);

        if (e.ticksExisted > 20)
        {
            Entity en = null;
            ItemStack create = new ItemStack(Item.getItemById(e.data[0]), 1, 0);

            if (create.getItem() == Items.diamond_sword)
            {
                create.addEnchantment(Enchantment.looting, 4);
            }

            if (create.getItem() == Items.diamond_pickaxe)
            {
                create.addEnchantment(Enchantment.fortune, 4);
            }

//            System.out.println("derp " + create.itemID);
            en = new EntityItem(e.worldObj, e.posX, e.posY - EntityRune.yOffset, e.posZ, create);

            if (en != null)
            {
                en.setPosition(e.posX, e.posY, e.posZ);
                e.worldObj.spawnEntityInWorld(en);
            }

            e.fade();
        }
    }
}
