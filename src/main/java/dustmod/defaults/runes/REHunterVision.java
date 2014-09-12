/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import dustmod.blocks.TileEntityDust;
import dustmod.runes.EntityRune;
import dustmod.runes.PoweredEvent;

/**
 *
 * @author billythegoat101
 */
public class REHunterVision extends PoweredEvent
{
    public REHunterVision()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);
    	
		
    }

    @Override
    public void onInit(EntityRune e)
    {
        super.onInit(e);
        ItemStack[] req = new ItemStack[] {new ItemStack(Items.blaze_powder, 3), new ItemStack(Items.ender_eye, 1)};
        req = this.sacrifice(e, req);

        if (!checkSacrifice(req) || !takeXP(e, 12))
        {
            e.fizzle();
            return;
        }
    }

    @Override
    public void onTick(EntityRune e)
    {
        super.onTick(e);
//        DustMod.hunterVisionActive = e.data[0] % 2 == 0;
    }

    @Override
    public void onRightClick(EntityRune e, TileEntityDust ted, EntityPlayer p)
    {
        super.onRightClick(e, ted, p);
        e.data[0]++;
    }

    @Override
    public void onUnload(EntityRune e)
    {
        super.onUnload(e);
//        DustMod.hunterVisionActive = false;
    }

    @Override
    public int getStartFuel()
    {
        return dayLength;
    }

    @Override
    public int getMaxFuel()
    {
        return dayLength * 2;
    }

    @Override
    public int getStableFuelAmount(EntityRune e)
    {
        return dayLength;
    }

    @Override
    public boolean isPaused(EntityRune e)
    {
        return e.data[0] % 2 == 1;
    }
}
