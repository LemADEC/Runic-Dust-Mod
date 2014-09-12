/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Random;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import dustmod.DustMod;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RESpawnRecord extends RuneEvent
{
    public RESpawnRecord()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

		e.setRenderStar(true);
		e.setRenderBeam(true);
        e.setColorStarOuter(0, 255, 0);
        e.setColorBeam(0, 255, 0);
		
    }

    public void onInit(EntityRune e)
    {
		e.setRenderStar(true);
		e.setRenderBeam(true);
        e.setColorStarOuter(0, 255, 0);
        e.setColorBeam(0, 255, 0);
        ItemStack[] sacrifice = new ItemStack[] {new ItemStack(Items.diamond, 1)};
        this.sacrifice(e, sacrifice);

        if (sacrifice[0].stackSize > 0)
        {
            e.fizzle();
            return;
        }
    }

    @SuppressWarnings("unchecked")
	public void onTick(EntityRune e)
    {
        e.setStarScale(e.getStarScale() + 0.001F);

        if (e.ticksExisted > 120)
        {
            Random r = new Random();
            
			try {
				
				//TODO test
				Field recordMapField = ItemRecord.class.getField("field_150928_b");
	            recordMapField.setAccessible(true);
	            ItemRecord[] recordList = (ItemRecord[]) ((Map<String, ItemRecord>) recordMapField.get(null)).values().toArray();
	            int recordNr = r.nextInt(recordList.length);
	            
	            EntityItem en = new EntityItem(e.worldObj, e.posX, e.posY - EntityRune.yOffset - 1, e.posZ, new ItemStack(recordList[recordNr], 1, 0));
	            e.worldObj.spawnEntityInWorld(en);
	            e.fade();
			} catch (NoSuchFieldException e1) {
				DustMod.logger.catching(e1);
			} catch (SecurityException e1) {
				DustMod.logger.catching(e1);
			} catch (IllegalArgumentException e1) {
				DustMod.logger.catching(e1);
			} catch (IllegalAccessException e1) {
				DustMod.logger.catching(e1);
			}

        }
    }
}
