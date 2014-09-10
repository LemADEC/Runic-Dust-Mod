/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.runes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import dustmod.DustEvent;
import dustmod.DustMod;
import dustmod.EntityDust;

/**
 *
 * @author billythegoat101
 */
public class DEFlatten extends DustEvent
{
    public int tickRate = 20;
	
	@Override
    public void initGraphics(EntityDust e){
    	super.initGraphics(e);

        e.setColorStarOuter(114, 53, 62);
		e.setRenderStar(true);
		
    }

    public void onInit(EntityDust e)
    {
        int a = e.dusts[9][9];
        int b = e.dusts[9][10];
        int c = e.dusts[10][9];
        int d = e.dusts[10][10];
        int dustStrength = a;

        if (a != b || b != c || c != d)
        {
            e.fizzle();
            return;
        }

        ItemStack[] sac = new ItemStack[] {new ItemStack(Blocks.iron_ore, 20)};
        sac = this.sacrifice(e, sac);
        int xp = 0;

        switch (dustStrength)
        {
            case 100:
                xp = 10;
                break;

            case 200:
                xp = 12;
                break;

            case 300:
                xp = 15;
                break;

            case 400:
                xp = 20;
                break;
        }

        if (sac[0].stackSize > 0 || !this.takeXP(e, xp))
        {
            e.fizzle();
            return;
        }

        e.setColorStarOuter(114, 53, 62);
		e.setRenderStar(true);
        e.data[0] = dustStrength;
    }

    public void onTick(EntityDust e)
    {
        if (e.ticksExisted % tickRate == 0)
        {
            int radius = 0;
            int height = 4;

            switch (e.data[0])
            {
                case 100:
                    radius = 4;
                    break;

                case 200:
                    radius = 6;
                    height = 5;
                    break;

                case 300:
                    radius = 8;
                    height = 7;
                    break;

                case 400:
                    radius = 10;
                    height = 9;
                    break;
            }

            int dist = e.ticksExisted / tickRate;

            if (dist > radius)
            {
                dist = radius;
            }

            int ix = e.getX();
            int iy = e.getY();
            int iz = e.getZ();
            World world = e.worldObj;

            for (int x = -dist; x  <= dist; x++)
            {
                for (int z = -dist; z  <= dist; z++)
                {
                    if (x == 0 && z == 0)
                    {
                        continue;
                    }

                    next:

                    for (int y = height - 1; y >= 0; y--)
                    {
                        Block block = world.getBlock(ix + x, iy + y, iz + z);
                        int meta = world.getBlockMetadata(ix + x, iy + y, iz + z);

                        if (!DustMod.isDust(block))
                        {
                            if (block instanceof BlockContainer)
                            {
                                continue;
                            }

                            Block bidu = world.getBlock(ix + x, iy + y + 1, iz + z);
                            Block bidd = world.getBlock(ix + x, iy + y - 1, iz + z);

                            if (block == Blocks.flowing_water || block == Blocks.water)
                            {
                                world.setBlock(ix + x, iy + y, iz + z, Blocks.cobblestone,0,3);
                                block = Blocks.air;
                                meta = 0;
                            }

                            if (bidu == Blocks.flowing_water || bidu == Blocks.water)
                            {
                                world.setBlock(ix + x, iy + y + 1, iz + z, Blocks.cobblestone,0,3);
                            }

                            if (bidd.getMaterial() == Material.air)
                            {
                                world.setBlock(ix + x, iy + y - 1, iz + z, block, meta,3);
                            }

                            world.setBlockToAir(ix + x, iy + y, iz + z);
                            break next;
                        }
                    }
                }
            }

            if (e.ticksExisted / tickRate > radius + height)
            {
                e.fade();
            }
        }
    }
}
