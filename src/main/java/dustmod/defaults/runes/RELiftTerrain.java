/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.defaults.runes;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import dustmod.blocks.TileEntityRut;
import dustmod.runes.RuneEvent;
import dustmod.runes.EntityRune;
import dustmod.runes.Sacrifice;

/**
 *
 * @author billythegoat101
 */
public class RELiftTerrain extends RuneEvent
{
    public static final int ticksperblock = 32;

    public RELiftTerrain()
    {
        super();
    }
	
	@Override
    public void initGraphics(EntityRune entityRune){
    	super.initGraphics(entityRune);

		entityRune.setRenderBeam(true);
        entityRune.setColorStar(255, 255, 0);
        entityRune.setColorFire(0,0,255);
        
        loadArea(entityRune);
		entityRune.setRenderFireOnRuts(true);
		
    }

    public void onInit(EntityRune entityRune)
    {
        ItemStack[] req = new ItemStack[] {new ItemStack(Blocks.red_flower, 1)};
        req = this.sacrifice(entityRune, req);

        if (!checkSacrifice(req) || !takeXP(entityRune, 10))
        {
            entityRune.fizzle();
            return;
        }

		entityRune.setRenderBeam(true);
        entityRune.setColorStar(255, 255, 0);
        int a, b, c, d;

        if (entityRune.dusts.length > entityRune.dusts[0].length)
        {
            a = entityRune.dusts[7][3];
            b = entityRune.dusts[8][3];
            c = entityRune.dusts[8][4];
            d = entityRune.dusts[7][4];
        }
        else
        {
            a = entityRune.dusts[3][7];
            b = entityRune.dusts[3][8];
            c = entityRune.dusts[4][8];
            d = entityRune.dusts[4][7];
        }

        int dustStrength = a;

        if (a != b || b != c || c != d)
        {
            entityRune.fizzle();
            return;
        }

        switch (dustStrength)
        {
            case 100:
                entityRune.data[1] = 12;
                break;

            case 200:
                entityRune.data[1] = 16;
                break;

            case 300:
                entityRune.data[1] = 22;
                break;

            case 400:
                entityRune.data[1] = 32;
                break;
		default:
            entityRune.fizzle();
            return;
        }
//        List<Entity> ents = this.getEntities(e, 3D);
//        boolean found = false;
//        for (Entity i : ents) {
//            if (i instanceof EntityPig || i instanceof EntitySheep || i instanceof EntityCow) {
//                found = true;
//                ((EntityLiving) i).attackEntityFrom(DamageSource.magic, 5000);
//                mod_DustMod.killEntity(i);
//                break;
//            }
//        }
//        if (!found) {
//            e.fizzle();
//            return;
//        }
        entityRune.sacrificeWaiting = 600;
        this.addSacrificeList(new Sacrifice(99));
//        e.fade();
    }

    public void onTick(EntityRune entityRune)
    {
//        e.fade();
        entityRune.setColorStar(255, 255, 255);

        if (entityRune.ticksExisted < ticksperblock * 2)
        {
            return;
        }

        if (entityRune.rutAreaPoints == null)
        {
            entityRune.fade();
        }

        World world = entityRune.worldObj;
        int x = (int) entityRune.getX();
        int y = (int) entityRune.getY();
        int z = (int) entityRune.getZ();
        int height = entityRune.data[1];

        if (entityRune.ticksExisted % ticksperblock == 0 && entityRune.data[2] <= height)
        {
        	if (entityRune.rutAreaPoints == null) {
        		loadArea(entityRune);
        	}
//            int c = e.bb - e.gb;
            for (Integer[] i : entityRune.rutAreaPoints)
            {
                x = i[0];
                y = i[1];
                z = i[2];
//                List<Entity> ents = getEntities(e.worldObj, (double) x + 0.5D, (double) y + (double) e.gb + 2D, (double) z + 0.5D, 1D);
//                for (Entity ie : ents) {
//                    if(ie instanceof EntityItem) System.out.println("dicks, things have been dropped");;
//                    double dx = ie.posX - ((double) x + 0.5D);
//                    double dz = ie.posZ - ((double) z + 0.5D);
////                    System.out.println("delta " + dx + " " + dz + " " + e.posX + " " + e.posZ + " " + (((double)x)+0.5D) + " " + (((double)z)+0.5D));
//                    if (Math.abs(dx) < 0.5D && Math.abs(dz) < 0.5D) {
//                        ie.setPosition(Math.floor(ie.posX) + 0.5D, (double) y + (double)e.gb + 3D, Math.floor(ie.posZ) + 0.5D);
//                    }
//                }

                for (int t = -height; t <= height; t++)
                {
                    int c = -t + entityRune.data[2] - 1;

                    if (y + c <= 0)
                    {
                        entityRune.fade();
                        return;
                    }

                    if (t != height)
                    {
                        Block block = world.getBlock(x, y + c, z);
                        int m = world.getBlockMetadata(x, y + c, z);
                        Block nB = world.getBlock(x, y + c + 1, z);

//                        System.out.println("fuck it all " + nb + " " + b + " " + world.getBlockId(x,y+c+2,z));;
                        if (world.isAirBlock(x, y + c + 2, z) && block.getMaterial() != Material.air)
                        {
//                            System.out.println("GOOOOOOO");
                            List<Entity> ents = getEntities(entityRune.worldObj, (double) x + 0.5D, (double) y + (double)c + 1D, (double) z + 0.5D, 1D);

                            for (Entity ie : ents)
                            {
                                if (ie == entityRune)
                                {
                                    continue;
                                }

//                                System.out.println("DICKS " + ie);
                                //                        if(ie instanceof EntityItem) System.out.println("dicks, things have been dropped");;
                                //                        double dx = ie.posX - ((double) x + 0.5D);
                                //                        double dz = ie.posZ - ((double) z + 0.5D);
                                //                    System.out.println("delta " + dx + " " + dz + " " + e.posX + " " + e.posZ + " " + (((double)x)+0.5D) + " " + (((double)z)+0.5D));
                                //                        if (Math.abs(dx) < 0.5D && Math.abs(dz) < 0.5D) {
                                ie.setPosition(Math.floor(ie.posX) + 0.5D, (double) y + (double)c + 2D + ie.yOffset, Math.floor(ie.posZ) + 0.5D);
                                //                        }
                            }
                        }

                        boolean isContainer = false;
                        TileEntity tileEntity = null;
                        NBTTagCompound tag = null;

                        //                    System.out.println("lift " + b + " " + (B != null && B instanceof BlockContainer) + " " + (nB == null || !(nB instanceof BlockContainer)));
                        if ((block != null && block instanceof BlockContainer)/* && (nB == null || !(nB instanceof BlockContainer))*/)
                        {
//                            System.out.println("IS CONTAINER************************");
                            isContainer = true;
                            tileEntity = world.getTileEntity(x, y + c, z);
                            tag = new NBTTagCompound();
                            tileEntity.writeToNBT(tag);
//                            world.removeBlockTileEntity(x, y+c, z);
                            tileEntity.invalidate();
                            //                        world.setBlockTileEntity(x,y+c,z,null);
                            //                        Chunk chunk = e.worldObj.getChunkFromBlockCoords(e.getX(), e.getZ());
                            //                        chunk.setChunkBlockTileEntity(x & 0xf, y-t+e.gb, z & 0xf, null);
                            //                        chunk.chunkTileEntityMap.put(new ChunkPosition(x&0xf,y+c,z&0xf), null);
//                            System.out.println("Rawr " + world.getTileEntity(x,y+c,z));
                        }

                        //                    else if(nB != null && (nB instanceof BlockContainer)){
                        //                        System.out.println("Failure... " + b + " " + nb);
                        //                        e.fade();
                        //                        return;
                        //                    }
                        world.setBlockToAir(x, y + c + 1, z);
                        world.setBlock(x, y + c + 1, z, block, m,3);
                        world.setBlock(x, y + c, z, Blocks.stone,0,3);

                        if (isContainer)
                        {
                            TileEntity tet = world.getTileEntity(x, y + c + 1, z);

//                            System.out.println("Fucker " + world.getBlockId(x, y + c + 1, z));
//                            System.out.println("grah " + Block.blocksList[world.getBlockId(x,y+c+1,z)].getBlockName());
                            if (tet != null)
                            {
                                tet.readFromNBT(tag);
                                tet.xCoord = x;
                                tet.yCoord = y + c + 1;
                                tet.zCoord = z;
                                tet.blockMetadata = m;
                            }

                            //                        te.validate();
//                            Chunk chunk = e.worldObj.getChunkFromBlockCoords(e.getX(), e.getZ());
//                            te.xCoord = x;
//                            te.yCoord = y+c+1;
//                            te.zCoord = z;
//
//    //                        world.removeBlockTileEntity(x,y+c+1,z);
//                            te.validate();
//                            world.setBlockTileEntity(x,y+c+1,z,te);
//                            chunk.setChunkBlockTileEntity(x & 0xf, y+c+1, z & 0xf, te);
//                            chunk.chunkTileEntityMap.put(new ChunkPosition(x&0xf,y+c+1,z&0xf), te);
//                            System.out.println("Validating " + (world.getTileEntity(x,y+c+1,z)==te));
                        }

                        world.setBlockMetadataWithNotify(x, y + c + 1, z, m,3);
                    }
                    else
                    {
//                        if(world.getTileEntity(x, y+c, z) != null)
//                            world.getTileEntity(x, y+c, z).validate();
//                        world.notifyBlockChange(x, y+c, z, 0);
                    }

//                    TileEntity FUCKER = world.getTileEntity(x,y+c,z);
//                    int BITCH = world.getBlockId(x,y+c,z);
//                    if(FUCKER != null){
//                        if(FUCKER.blockType.blockID == BITCH)
//                            System.out.println("DIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIICKS");
//                    }
                }
            }

            entityRune.data[2]++;

//            System.out.println("Data " + e.gb + " " + height);
            if (entityRune.data[2] >= height)
            {
                entityRune.fade();
            }
        }
    }

    private void loadArea(EntityRune entityRune)
    {
        this.findRutAreaFlat(entityRune, Blocks.clay);
        int min = entityRune.getY();
        int max = 0;

        for (int iter = 0; iter < entityRune.rutAreaPoints.size(); iter++)
        {
            Integer[] i = entityRune.rutAreaPoints.get(iter);
            int x = i[0];
            int z = i[1];
            int h = entityRune.worldObj.getHeightValue(x, z);

            if (h < min)
            {
                min = h;
            }

            if (h > max)
            {
                max = h;
            }

//            i[1] = e.worldObj.getHeightValue(i[0], i[2]);
//            if(i[1] > max) max = i[1];
            entityRune.rutAreaPoints.set(iter, new Integer[] {x, h, z});
//            e.worldObj.setBlockWithNotify(i[0], e.worldObj.getHeightValue(i[0], i[1]), i[1], Block.glass.blockID);
        }

        entityRune.data[2] = max - min;
//        System.out.println("offset " + e.gb);
    }

    @Override
    public void onUnload(EntityRune entityRune)
    {
        if (entityRune.rutPoints == null)
        {
            this.findRuts(entityRune, Blocks.clay);
        }

        super.onUnload(entityRune);

        if (entityRune.rutPoints != null)
        {
            for (Integer[] i : entityRune.rutPoints)
            {
                int rand = entityRune.worldObj.rand.nextInt(100);

                if (rand > 15)
                {
//                world.setBlockWithNotify(i[0], i[1], i[2], Block.melon.blockID);
                    TileEntityRut ter = (TileEntityRut) entityRune.worldObj.getTileEntity(i[0], i[1], i[2]);

                    if (ter != null)
                    {
//                        ter.fluid = Block.sand.blockID;
                    }
                }
            }
        }
    }
}
