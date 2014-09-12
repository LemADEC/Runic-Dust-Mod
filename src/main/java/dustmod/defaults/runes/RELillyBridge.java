/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class RELillyBridge extends RuneEvent
{
    public RELillyBridge()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

		e.setRenderStar(true);
        e.setColorStarOuter(0, 255, 0);
		
    }

    public void onInit(EntityRune e)
    {
        World world = e.worldObj;
        ItemStack[] req = this.sacrifice(e, new ItemStack[] {new ItemStack(Blocks.leaves, 4, -1)});

        if (req[0].stackSize != 0)
        {
            e.fizzle();
            return;
        }

        e.rotationYaw = ((e.rot+1)%4)*90;

		e.setRenderStar(true);
        e.setColorStarOuter(0, 255, 0);
    }

    public void onTick(EntityRune e)
    {
        int period = 20;

        if (e.ticksExisted % period == 0)
        {
            World world = e.worldObj;
            int dist = (int)(e.ticksExisted / period + 1) * 2;
            int y = e.getY() - 1;
            int x = e.getX();
            int z = e.getZ();

            if (e.rotationYaw == 90)
            {
                x -= dist;
            }
            else if (e.rotationYaw == 270)
            {
                x += dist;
            }
            else if (e.rotationYaw == 180)
            {
                z -= dist;
            }
            else if (e.rotationYaw == 0)
            {
                z += dist;
            }

            for (int i = -1; i <= 1; i++)
            {
                if (world.getBlock(x, y + i - 1, z).getMaterial() == Material.water &&
                        world.isAirBlock(x, y + i, z))
                {
                    world.setBlock(x, y + i, z, Blocks.waterlily,0,3);
                }
            }

//            world.setBlockWithNotify(x,y,z,Block.brick.blockID);
        }

        if (e.ticksExisted > 16 * period)
        {
            e.fade();
        }
    }
}
