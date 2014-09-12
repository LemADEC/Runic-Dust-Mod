/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustexample.examplerunes;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import dustmod.blocks.TileEntityDust;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REMakeBlockFromDustLevel extends RuneEvent
{
    
    public REMakeBlockFromDustLevel()
    {
        super();
    }
	
	/**
	 * Called to set the graphical components of the rune
     * @param e EntityDust instance
	 */
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);
    	
		//no graphics!
    }
    
    @Override
    public void onInit(EntityRune e)
    {
        super.onInit(e);
        
        Block block = Blocks.air;
        //Since it is a solid rune, e.dustID returns the dust it is made of
        //And depending on that dust, we select a block we want to use
        switch(e.dustID){
            case 1:
            	block = Blocks.log;
                break;
            case 2:
            	block = Blocks.tnt;
                break;
            case 3:
            	block = Blocks.lapis_block;
                break;
            case 4:
            	block = Blocks.nether_brick;
                break;
        }
        
        World world = e.worldObj;
        //Loop through all the block coordinates beneath those taken up by the runes.
        for(Integer[] i:e.dustPoints){
            int x = i[0];
            int y = i[1] -1;
            int z = i[2];
            world.setBlock(x, y, z, block,0,3);
        }
        
        e.fade();
    }
    
    @Override
    public void onTick(EntityRune e)
    {
        super.onTick(e);
        //everything is done in init(), so there is no use for this method
    }
    
    @Override
    public void onRightClick(EntityRune e, TileEntityDust ted, EntityPlayer p)
    {
        super.onRightClick(e, ted, p);
    }
    
    @Override
    public void onUnload(EntityRune e)
    {
        super.onUnload(e);
    }
}
