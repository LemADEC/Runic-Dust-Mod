/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
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
public class REHideout extends RuneEvent
{
    public static final int thick = 2;
    public REHideout()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityRune e){
    	super.initGraphics(e);

        e.setRenderStar(true);
        e.setColorStar(255, 255, 255);
		
    }

    @Override
    public void onInit(EntityRune e)
    {
        super.onInit(e);

        if (!this.takeHunger(e, 4))
        {
            e.fizzle();
            return;
        }

        e.setRenderStar(true);
        e.setColorStar(255, 255, 255);
        int x = e.getX();
        int y = e.getY();
        int z = e.getZ();
        World world = e.worldObj;

        int r = 1;
        int h = 3;

        Block b = world.getBlock(x, y - h - thick - 1, z);

        
        if(world.isAirBlock(x,y-thick-1,z)){
            doCheck(e);
//            if (b != null && !(b instanceof BlockFluid))
//            {
                world.setBlock(x, y - h - thick - 1, z, Blocks.cobblestone,0,0);
                world.setBlock(x, y - h - thick, z, Blocks.torch,0,0);
//            }
            return;
        }
        
        switch(e.dustID){
            case 100:
                r = 1;
                h = 3;
                break;
            case 200:
                r = 2;
                h = 3;
                break;
            case 300:
                r = 2;
                h = 5;
                break;
            case 400:
                r = 4;
                h = 6;
                break;
        }
        for (int i = -r; i <= r; i++)
        {
            for (int k = -r; k <= r; k++)
            {
                for (int j = -thick; j >= -h - thick; j--)
                {
                    if (j == -thick)
                    {
                        Block above = world.getBlock(x + i, y + j + 1, z + k);

                        if (above != null && above instanceof BlockSand)
                        {
                            world.setBlock(x + i, y + j, z + k, Blocks.sandstone,0,3);
                        }

//                            world.setBlockWithNotify(x+i, y+j, z+k, Block.brick.blockID);
//                        else
//                            world.setBlockWithNotify(x+i, y+j, z+k, Block.sandStone.blockID);
                    }
                    else if(canBreakBlock(e, x + i, y + j, z + k))
                    {
                        world.setBlockToAir(x + i, y + j, z + k);
                    }
                }
            }
        }
        
//        world.setBlockID(x,y-thick,z, Block.brick.blockID);

//        Block b = Block.blocksList[world.getBlockId(x, y - h - thick - 1, z)];
//
        if (b != null && !(b instanceof BlockLiquid))
        {
            world.setBlock(x, y - h - thick - 1, z, Blocks.cobblestone,0,0);
            world.setBlock(x, y - h - thick, z, Blocks.torch,0,0);
        }

        doCheck(e);
    }

    @Override
    public void onTick(EntityRune e)
    {
        super.onTick(e);

        if (e.ticksExisted % 10 == 0)
        {
            yCheck(e);
        }

//        e.worldObj.setBlock(e.getX(), e.data[0], e.getZ(), Block.glowStone.blockID);
        List<Entity> ents;

        if (e.ram <= 0)
        {
            e.setColorStar(255, 255, 255);
            ents = this.getEntities(e, 0.2D);

            for (Entity ei: ents)
            {
                if (ei instanceof EntityPlayer)
                {
                    EntityPlayer ep = (EntityPlayer)ei;
                    e.ram = 45;
//                    ep.setVelocity(0, 0, 0);
                    ep.setPositionAndUpdate((double)e.getX() + 0.5D,  e.data[0] + 1/* + ei.yOffset*/ +0.5D, (double)e.getZ() + 0.5D);
                    ep.fallDistance = 0;
                }
            }

            ents = this.getEntities(e.worldObj, e.getX(), e.data[0] + 2, e.getZ(), 0.5D);

            for (Entity ei: ents)
            {
                if (ei instanceof EntityPlayer && ei.isSneaking())
                {
                    EntityPlayer ep = (EntityPlayer)ei;
//                    ep.setVelocity(0, 0, 0);
                    ep.setPositionAndUpdate((double)e.getX() + 0.5D, (double)e.getY() /*+ 0 + ei.yOffset*/ +0.5D, (double)e.getZ() + 0.5D);
                    ep.fallDistance = 0;
                    e.ram = 45;
                }
            }
        }
        else
        {
            e.setColorStar(255, 255, 0);
            e.ram --;
        }
    }
    
    public boolean canBreakBlock(EntityRune e, int x, int y, int z){
    	
    	if(!e.canAlterBlock(x, y, z)) return false;
    	
    	Block b = e.worldObj.getBlock(x, y, z);
    	if(b.getMaterial() == Material.air) return false;
    	
    	if(b.getBlockHardness(e.worldObj, x, y, z) >= Blocks.obsidian.getBlockHardness(e.worldObj, x, y, z)){
    		return false;
    	}else if(b == Blocks.bedrock){
        	return false;
    	}
    	return true;
    }

    private void yCheck(EntityRune e)
    {
        int x = e.getX();
        int y = e.data[0];
        int z = e.getZ();
        World w = e.worldObj;
        Block b1 = w.getBlock(x, y, z);
        Block b2 = w.getBlock(x, y + 1, z);

        if (!b1.isOpaqueCube() || b1.getMaterial() == Material.air)
        {
            doCheck(e);
        }
        else if (b2.isOpaqueCube())
        {
            doCheck(e);
        }
    }
    private void doCheck(EntityRune e)
    {
        int y;

        for (y = e.getY() - 1 - thick; y > 3 && y > e.getY() - 1 - thick - 64; y--)
        {
            Block block = e.worldObj.getBlock(e.getX(), y, e.getZ());

            if (block.isOpaqueCube())
            {
                break;
            }
        }

        e.data[0] = y;
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
