package dustmod.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustmod.DustMod;

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
        boolean notify = false;
        //DustModBouncer.notifyBlockChange(world, i, j, k, 0);
        for (int ix = -1; ix <= 1; ix++)
        {
            for (int iy = -1; iy <= 1; iy++)
            {
                for (int iz = -1; iz <= 1; iz++)
                {
                    if (ix != 0 || iy != 0 || iz != 0)
                    {
                        TileEntityRut ter = (TileEntityRut)world.getTileEntity(x, y, z);
                        Block check = world.getBlock(x + ix, y + iy, z + iz);

                        if (ter.fluid == null)
                        {
                            if (check instanceof BlockStaticLiquid) {
                                ter.setFluid(check);
                                notify = true;
                            }
                            else if (check instanceof BlockFluidBase) {
                            	ter.setFluid(check);
                            	notify = true;
                            }
                        }
                        else if (ter.fluid == Blocks.water)
                        {
                            if (check == Blocks.lava || check == Blocks.flowing_lava)
                            {
                                ter.setFluid(Blocks.cobblestone);
                                notify = true;
                            }
                        }
                        else if (ter.fluid == Blocks.lava)
                        {
                            if (check == Blocks.water || check == Blocks.flowing_water)
                            {
                                ter.setFluid(Blocks.obsidian);
                                notify = true;
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

		if (!world.canMineBlock(player, x, y, z)) {
			return false;
		}

        ItemStack current = player.inventory.getCurrentItem();

        TileEntityRut ter = (TileEntityRut)world.getTileEntity(x, y, z);

        if (ter.isBeingUsed)
        {
            return false;
        }
        
        if (current != null) {
        	
        	if (FluidContainerRegistry.isBucket(current)) {
        		
        		FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(current);
        		if (fluid.getFluid().canBePlacedInWorld()) {
        			ter.setFluid(fluid.getFluid().getBlock());
        			
        			if (!player.capabilities.isCreativeMode) {
        				player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bucket));
        			}
        			
        			return true;
        		}
        		
        	} else if (ter.fluid == null || ter.fluidIsFluid()) {
        		
        		if (current.getItem() instanceof ItemBlock) {
        			Block block = Block.getBlockFromItem(current.getItem());
        			
        			if (block.renderAsNormalBlock() && block.isOpaqueCube() && (DustMod.Enable_Decorative_Ruts || block.getBlockHardness(world, x,y,z) <= TileEntityRut.hardnessStandard)) {
        				ter.setFluid(block);
        				
        				if (!player.capabilities.isCreativeMode)
                        {
                            current.stackSize--;
                        }
        				
        				return true;
        			}
        		}
        	} else if (ter.fluid != null && !ter.fluidIsFluid() && (ter.fluid.getBlockHardness(world, x,y,z) <= TileEntityRut.hardnessStandard || DustMod.Enable_Decorative_Ruts)) {
                if (current.getItem() instanceof ItemSpade)
                {
                    this.dropBlockAsItem(world, x, y + 1, z, new ItemStack(ter.fluid, 1, 0));
                    ter.setFluid(Blocks.air);
                    return true;
                }
        	}
        }

        if (current == null || current.getItem() != DustMod.chisel)
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
        
        ter.toggleRut(player, bx, by, bz);
        
        if (ter.isEmpty()) {
        	ter.resetBlock();
        }
        
        return true;
    }
    
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
    	
        TileEntityRut ter = (TileEntityRut)world.getTileEntity(x, y, z);

        if (ter == null || ter.isInvalid())
        {
        	return new ArrayList<ItemStack>();
        }
        
        return ter.maskBlock.getDrops(world, x, y, z, metadata, fortune);
    }

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