package dustmod;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 *
 * @author billythegoat101
 */
public class BlockRut extends BlockContainer
{
    public BlockRut()
    {
        super(Material.wood);
        this.setLightOpacity(0);
    }

    @Override
    public int getRenderType()
    {
        return DustMod.proxy.getBlockModel(this);
    }
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        boolean notified = false;
        //DustModBouncer.notifyBlockChange(world, i, j, k, 0);
        for (int ix = -1; ix <= 1; ix++)
        {
            for (int iy = -1; iy <= 1; iy++)
            {
                for (int iz = -1; iz <= 1; iz++)
                {
                    if (ix == iy || ix == iz || iy == iz)
                    {
                        TileEntityRut ter = (TileEntityRut)world.getTileEntity(x, y, z);
                        Block check = world.getBlock(x + ix, y + iy, z + iz);

                        if (ter.fluid == Blocks.air)
                        {
                            if (check == Blocks.lava)
                            {
                                ter.setFluid(Blocks.lava);
                                notified = true;
//                                mod_DustMod.notifyBlockChange(world, i, j, k, 0);
                            }
                            else if (check == Blocks.water)
                            {
                                ter.setFluid(Blocks.water);
                                notified = true;
//                                mod_DustMod.notifyBlockChange(world, i, j, k, 0);
                            }
                        }

                        if (ter.fluid == Blocks.water)
                        {
                            if (check == Blocks.lava)
                            {
                                ter.setFluid(Blocks.cobblestone);
                                notified = true;
//                                mod_DustMod.notifyBlockChange(world, i, j, k, 0);
                            }
                        }

                        if (ter.fluid == Blocks.lava)
                        {
                            if (check == Blocks.water)
                            {
                                ter.setFluid(Blocks.obsidian);
                                notified = true;
//                                mod_DustMod.notifyBlockChange(world, i, j, k, 0);
                            }
                        }
                    }
                }
            }
        }

//        if(((TileEntityRut)world.getTileEntity(i, j, k)).updateNeighbors() && !notified){
//        	DustModBouncer.notifyBlockChange(world, i, j, k, 0);
//        }
        world.markBlockForUpdate(x, y, z);
    }
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int face, float cx, float cy, float cz)
    {

		if(!world.canMineBlock(player, x, y, z)) return false;
		
        ItemStack playerItemStack = player.getCurrentEquippedItem();
        boolean isNull = (playerItemStack == null);

        TileEntityRut ter = (TileEntityRut)world.getTileEntity(x, y, z);

        if (ter.isBeingUsed)
        {
            return false;
        }

//        ter.fluid = Block.obsidian.blockID;
        
        if (/*ter.fluid == 0 && */!isNull && playerItemStack.getItem() == Items.water_bucket)
        {
            if (!player.capabilities.isCreativeMode)
            {
            	player.setCurrentItemOrArmor(0, new ItemStack(Items.bucket));
            }

            ter.setFluid(Blocks.water);
            return true;
        }

        if (/*ter.fluid == 0 && */!isNull && playerItemStack.getItem() == Items.lava_bucket)
        {
            if (!player.capabilities.isCreativeMode)
            {
            	player.setCurrentItemOrArmor(0, new ItemStack(Items.bucket));
            }

            ter.setFluid(Blocks.lava);
            return true;
        }

        if (!isNull && (ter.fluid == Blocks.air || ter.fluidIsFluid()))
        {
            if (playerItemStack.getItem() instanceof ItemBlock)
            {
                Block b = Block.getBlockFromItem(playerItemStack.getItem());

                if (b.renderAsNormalBlock() && b.isOpaqueCube() && (b.getBlockHardness(world, x,y,z) <= TileEntityRut.hardnessStandard || DustMod.Enable_Decorative_Ruts))
                {
                    if (!player.capabilities.isCreativeMode)
                    {
                        playerItemStack.stackSize--;
                    }

                    ter.setFluid(b);
                    return true;
                }
            }
        }

        if (!isNull && ter.fluid != Blocks.air && !ter.fluidIsFluid() && (ter.fluid.getBlockHardness(world, x,y,z) <= TileEntityRut.hardnessStandard || DustMod.Enable_Decorative_Ruts))
        {
            if (playerItemStack.getItem() instanceof ItemSpade)
            {
                this.dropBlockAsItem(world, x, y + 1, z, new ItemStack(ter.fluid, 1, 0));
                ter.setFluid(Blocks.air);
                return true;
            }
        }

        if (isNull || playerItemStack.getItem() != DustMod.chisel)
        {
            return false;
        }

        Block maskBlock = ter.maskBlock;
        world.playSoundEffect((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, maskBlock.stepSound.getStepResourcePath(), (maskBlock.stepSound.getVolume() + 1.0F) / 6.0F, maskBlock.stepSound.getPitch() * 0.99F);
        
        int bx,by,bz;
        bx = (int)Math.floor(cx*3);
        by = (int)Math.floor(cy*3);
        bz = (int)Math.floor(cz*3);
        
        bx = (int)Math.min(2, bx);
        by = (int)Math.min(2, by);
        bz = (int)Math.min(2, bz);
        
        toggleRut(ter, player, bx,by,bz);
        
        return true;
    }

    public void toggleRut(TileEntityRut rut, EntityPlayer p, int x, int y, int z)
    {
        rut.setRut(p, x, y, z, rut.getRut(x, y, z) == 0 ? 1 : 0);

        if (rut.isEmpty())
        {
            rut.resetBlock();
        }
    }

    private static int determineOrientation(World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        if (MathHelper.abs((float)entityplayer.posX - (float)x) < 2.0F && MathHelper.abs((float)entityplayer.posZ - (float)z) < 2.0F)
        {
            double d = (entityplayer.posY + 1.8200000000000001D) - (double)entityplayer.yOffset;

            if (d - (double)y > 2D)
            {
                return 1;
            }

            if ((double)y - d > 0.0D)
            {
                return 0;
            }
        }

        int l = MathHelper.floor_double((double)((entityplayer.rotationYaw * 4F) / 360F) + 0.5D) & 3;

        if (l == 0)
        {
            return 2;
        }

        if (l == 1)
        {
            return 5;
        }

        if (l == 2)
        {
            return 3;
        }

        return l != 3 ? 0 : 4;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block b, int m) {
//		if (world.isRemote) {
//        	super.breakBlock(world, i, j, k, b, m);
//			return;
//		}
        TileEntityRut ter = (TileEntityRut)world.getTileEntity(x, y, z);

        if (ter.isDead)
        {
        	super.breakBlock(world, x, y, z, b, m);
            return;
        }

        super.onBlockDestroyedByPlayer(world, x, y, z, m);
        int meta = ter.maskMeta;
        Item drop = ter.maskBlock.getItemDropped(meta, new Random(), 0);
        int mdrop = ter.maskBlock.damageDropped(meta);
        int qdrop = ter.maskBlock.quantityDropped(new Random());
        this.dropBlockAsItem(world, x, y, z, new ItemStack(drop, qdrop, mdrop));

        if (ter.fluid != Blocks.air && !ter.fluidIsFluid() && ter.canEdit())
        {
            this.dropBlockAsItem(world, x, y, z, new ItemStack(ter.fluid, 1, 0));
        }
    	super.breakBlock(world, x, y, z, b, m);
    }

    /*
    @Override
    public int idDropped(int i, Random random, int j)
    {
        return 0;
    }*/

    @Override
    public TileEntity createNewTileEntity(World world, int par2)
    {
        return new TileEntityRut();
    }

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int meta) {
		TileEntityRut ter = (TileEntityRut) world.getTileEntity(x, y, z);
		return ter.maskBlock.getIcon(world, x, y, z, ter.maskMeta);
	}

    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
    	TileEntityRut ter = (TileEntityRut)world.getTileEntity(x, y, z);
    	return new ItemStack(ter.maskBlock);
    };

    /**
     * Get the block's damage value (for use with pick block).
     */
    public int getDamageValue(World world, int i, int j, int k)
    {
    	TileEntityRut ter = (TileEntityRut)world.getTileEntity(i, j, k);
    	return ter.maskMeta;
    }


    /**
     * Get a light value for the block at the specified coordinates, normal ranges are between 0 and 15
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return The light value
     */
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        TileEntityRut ter = (TileEntityRut)world.getTileEntity(x, y, z);

        
        Block mask = ter.maskBlock;
        Block fluid = ter.fluid;
        int light = lightValue;
        
        if (mask != null && mask != this)
        {
        	int mLight = ter.maskBlock.getLightValue();
        	if(mLight > light) light = mLight;
        }
        
        if (fluid != null && fluid != this)
        {
        	int fLight = ter.fluid.getLightValue();
        	if(fLight > light) light = fLight;
        }
        return light;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
    }
}