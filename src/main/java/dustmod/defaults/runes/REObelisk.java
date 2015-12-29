/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;

/**
 *
 * @author billythegoat101
 */
public class REObelisk extends RuneEvent
{
    public static final int ticksperblock = 20;
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

//      e.setRenderBeam(true);
        e.setColorBeam(114, 114, 62);
        e.setRenderBeam(true);
        e.setBeamType(1);
//        e.setRenderStar(true);
		
    }

    public void onInit(EntityRune entityRune)
    {
    	
        ItemStack[] sacrifice = new ItemStack[1];
        sacrifice[0] = new ItemStack(Blocks.iron_ore, 1);
        this.sacrifice(entityRune, sacrifice);

        if (sacrifice[0].stackSize != 0)
        {
            entityRune.fizzle();
            return;
        }

        entityRune.data[1] = 1;
    }

    public void onTick(EntityRune entityRune)
    {

        int height = 16;
    	
        if (entityRune.ticksExisted < ticksperblock * 2)
        {
            return;
        }

        World world = entityRune.worldObj;
        int x = entityRune.getX();
        int y = entityRune.getY();
        int z = entityRune.getZ();

        if (entityRune.ticksExisted % ticksperblock == 0 && (entityRune.data[0] < height))
        {
            if (entityRune.data[1] > 0)
            {
                List<Entity> entities = getEntitiesExcluding(entityRune, entityRune.worldObj, x + 0.5D, (double)y + (double)entityRune.data[0] + 1D, z + 0.5D, 1.5D);

                for (Entity entity : entities)
                {
                	entity.setPosition(x + 0.5D, (double)y + (double)entityRune.data[0] + 1D, z + 0.5D);
                }
            }

            if (entityRune.data[1] == 1)
            {
                for (int t = -8; t < height; t++)
                {
                    int c = -t + entityRune.data[0] - 1;

                    if (y + c <= 0)
                    {
                        entityRune.fade();
                        return;
                    }

                    int m = world.getBlockMetadata(x, y + c, z);
                    Block B = world.getBlock(x, y + c, z);

                    if ((B.getMaterial() == Material.air || B instanceof BlockLiquid) && !world.isAirBlock(x, y + c + 2, z))
                    {
                        B = Blocks.cobblestone;
                    }

                    Block nB = world.getBlock(x, y + c + 1, z);

                    if ((B != null && B instanceof BlockContainer) && (nB != null && !(nB instanceof BlockContainer)))
                    {
                        entityRune.fade();
                        return;
                    }

                    world.setBlock(x, y + c + 1, z, B, m,3);
                    world.setBlockToAir(x, y + c, z);
                }
            }
            else
            {
                for (int t = height; t >= -9; t--)
                {
                    if (y - t + entityRune.data[0] <= 0)
                    {
                        entityRune.fade();
                        return;
                    }

                    Block B = world.getBlock(x, y - t + entityRune.data[0], z);
                    int m = world.getBlockMetadata(x, y - t + entityRune.data[0], z);
                    Block nB = world.getBlock(x, y - t + entityRune.data[0] + entityRune.data[1], z);
                    
                    if ((B != null && B instanceof BlockContainer) || (nB != null && nB instanceof BlockContainer))
                    {
                        entityRune.fade();
                        return;
                    }

                    world.setBlock(x, y - t + entityRune.data[0] + entityRune.data[1], z, B, m,3);
                    world.setBlockToAir(x, y - t + entityRune.data[0], z);
                }
            }

            entityRune.data[0] += entityRune.data[1];
        }

        if (entityRune.data[0] >= height && world.isAirBlock(x, y + height - 1, z))
        {
            entityRune.data[1] = -1;
            entityRune.data[0]--;
        }

        if (entityRune.data[0] < 0)
        {
            entityRune.fade();
        }

        if (entityRune.ticksExisted - ticksperblock * (height + 2) > 36000 && entityRune.data[1] > 0)
        {
            entityRune.fade();
        }
    }
}
