/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustexample.examplerunes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import dustmod.DustEvent;
import dustmod.EntityDust;
import dustmod.TileEntityDust;

/**
 *
 * Rune of Making it Rain
 * 
 * Good example of:
 * -How to use the onRightClick function
 * -Health sacrifices
 * 
 * @author billythegoat101
 */
public class DEMakeItRain extends DustEvent
{
    
    public DEMakeItRain()
    {
        super();
    }
	
	/**
	 * Called to set the graphical components of the rune
     * @param e EntityDust instance
	 */
	@Override
    public void initGraphics(EntityDust e){
    	super.initGraphics(e);
    	
		//no graphics!
    	
    }
    
    @Override
    public void onInit(EntityDust e)
    {
        super.onInit(e);
        //This is a really lazily made event.
    }
    
    @Override
    public void onTick(EntityDust e)
    {
        super.onTick(e);
    }
    
    @Override
    public void onRightClick(EntityDust e, TileEntityDust ted, EntityPlayer p)
    {
        super.onRightClick(e, ted, p);
        World world = e.worldObj;
        //Takes 2 full hearts (4 halves) from the closest player
        if(this.takeLife(e, 4, true)){
            
            if(world.getWorldInfo().isRaining()){
            	world.getWorldInfo().setRainTime(world.rand.nextInt(168000) + 12000);
            	world.getWorldInfo().setRaining(false);
            }else{
            	world.getWorldInfo().setRainTime(0);
            }
        }
    }

	@Override
    public void onUnload(EntityDust e)
    {
        super.onUnload(e);
    }
}
