package dustmod.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustmod.DustMod;

/**
 *
 * @author billythegoat101
 */
public class BlockRut extends BlockContainer {
	public BlockRut() {
		super(Material.wood);
		this.setLightOpacity(0);
	}
	
	@Override
	public int getRenderType() {
		return DustMod.proxy.getBlockModel(this);
	}
	
	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		
		TileEntityRut ter = (TileEntityRut) world.getTileEntity(x, y, z);
		if (ter == null) {
			return true;
		}
		
		if (side.offsetX != 0) {
			return !ter.getRut(side.offsetX + 1, 0, 1)
				&& !ter.getRut(side.offsetX + 1, 1, 1)
				&& !ter.getRut(side.offsetX + 1, 2, 1)
				&& !ter.getRut(side.offsetX + 1, 1, 0)
				&& !ter.getRut(side.offsetX + 1, 1, 2);
		} else if (side.offsetY != 0) {
			return !ter.getRut(0, side.offsetY + 1, 1)
				&& !ter.getRut(1, side.offsetY + 1, 1)
				&& !ter.getRut(2, side.offsetY + 1, 1)
				&& !ter.getRut(1, side.offsetY + 1, 0)
				&& !ter.getRut(1, side.offsetY + 1, 2);
		} else {
			return !ter.getRut(0, 1, side.offsetZ + 1)
				&& !ter.getRut(1, 1, side.offsetZ + 1)
				&& !ter.getRut(2, 1, side.offsetZ + 1)
				&& !ter.getRut(1, 0, side.offsetZ + 1)
				&& !ter.getRut(1, 2, side.offsetZ + 1);
		}
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (world.isRemote) {
			return;
		}
		
		for (int ix = -1; ix <= 1; ix++) {
			for (int iy = -1; iy <= 1; iy++) {
				for (int iz = -1; iz <= 1; iz++) {
					if (ix != 0 || iy != 0 || iz != 0) {
						TileEntityRut ter = (TileEntityRut) world.getTileEntity(x, y, z);
						Block check = world.getBlock(x + ix, y + iy, z + iz);
						
						if (ter.fluidBlock == null) {
							Fluid fluid = FluidRegistry.lookupFluidForBlock(check);
							if (fluid != null) {
								ter.setFluid(check);
							} else if (check instanceof BlockStaticLiquid) {
								ter.setFluid(check);
							} else if (check instanceof BlockFluidBase) {
								ter.setFluid(check);
							}
						} else if (ter.fluidBlock == Blocks.water) {
							if (check == Blocks.lava || check == Blocks.flowing_lava) {
								ter.setFluid(Blocks.cobblestone);
							}
						} else if (ter.fluidBlock == Blocks.lava) {
							if (check == Blocks.water || check == Blocks.flowing_water) {
								ter.setFluid(Blocks.obsidian);
							}
						}
					}
				}
			}
		}
		
		world.markBlockForUpdate(x, y, z);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int face, float cx, float cy, float cz) {
		if (!world.canMineBlock(player, x, y, z)) {
			return false;
		}
		
		ItemStack itemStackCurrent = player.inventory.getCurrentItem();
		
		TileEntityRut tileEntityRut = (TileEntityRut) world.getTileEntity(x, y, z);
		
		if (tileEntityRut.isBeingUsed || tileEntityRut.maskBlock == null) {
			return false;
		}
		
		if (itemStackCurrent != null) {
			
			if (FluidContainerRegistry.isBucket(itemStackCurrent)) {
				
				FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(itemStackCurrent);
				if (fluid == null) {
					if (!player.capabilities.isCreativeMode) {
						ItemStack itemStackFilled = FluidContainerRegistry.fillFluidContainer(fluid, itemStackCurrent);
						if (itemStackFilled != null) {
							player.inventory.setInventorySlotContents(player.inventory.currentItem, itemStackFilled);
						}
					}
					tileEntityRut.setFluid(null);
					
					return true;
				} else if (fluid.getFluid().canBePlacedInWorld()) {
					tileEntityRut.setFluid(fluid.getFluid().getBlock());
					
					if (!player.capabilities.isCreativeMode) {
						player.inventory.setInventorySlotContents(player.inventory.currentItem,
								FluidContainerRegistry.drainFluidContainer(itemStackCurrent));
					}
					
					return true;
				}
				
			} else if (tileEntityRut.fluidBlock == null || tileEntityRut.fluidIsFluid()) {
				
				if (itemStackCurrent.getItem() instanceof ItemBlock) {
					Block block = Block.getBlockFromItem(itemStackCurrent.getItem());
					
					if ( block.renderAsNormalBlock() && block.isOpaqueCube()
					  && (DustMod.Enable_Decorative_Ruts || block.getBlockHardness(world, x, y, z) <= TileEntityRut.hardnessStandard)) {
						tileEntityRut.setFluid(block);
						
						if (!player.capabilities.isCreativeMode) {
							itemStackCurrent.stackSize--;
						}
						
						return true;
					}
				}
				
			} else if ( tileEntityRut.fluidBlock != null && !tileEntityRut.fluidIsFluid() 
					 && (tileEntityRut.fluidBlock.getBlockHardness(world, x, y, z) <= TileEntityRut.hardnessStandard || DustMod.Enable_Decorative_Ruts)) {
				if (itemStackCurrent.getItem() instanceof ItemSpade) {
					this.dropBlockAsItem(world, x, y + 1, z, new ItemStack(tileEntityRut.fluidBlock, 1, 0));
					tileEntityRut.setFluid(Blocks.air);
					return true;
				}
			}
		}
		
		if ( itemStackCurrent == null
		  || itemStackCurrent.getItem() != DustMod.chisel) {
			return false;
		}
		
		Block maskBlock = tileEntityRut.maskBlock;
		world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, maskBlock.stepSound.getStepResourcePath(), (maskBlock.stepSound.getVolume() + 1.0F) / 6.0F, maskBlock.stepSound.getPitch() * 0.99F);
		
		int bx, by, bz;
		bx = (int) Math.floor(cx * 3);
		by = (int) Math.floor(cy * 3);
		bz = (int) Math.floor(cz * 3);
		
		bx = Math.min(2, bx);
		by = Math.min(2, by);
		bz = Math.min(2, bz);
		
		tileEntityRut.toggleRut(player, bx, by, bz);
		
		if (tileEntityRut.isEmpty()) {
			tileEntityRut.resetBlock();
		}
		
		return true;
	}
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		
		TileEntityRut ter = (TileEntityRut) world.getTileEntity(x, y, z);
		
		if (ter == null || ter.isInvalid()) {
			return new ArrayList<ItemStack>();
		}
		
		return ter.maskBlock.getDrops(world, x, y, z, metadata, fortune);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityRut();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int meta) {
		TileEntityRut ter = (TileEntityRut) world.getTileEntity(x, y, z);
		return ter.maskBlock.getIcon(world, x, y, z, ter.maskMeta);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		TileEntityRut ter = (TileEntityRut) world.getTileEntity(x, y, z);
		return new ItemStack(ter.maskBlock, 1, ter.maskMeta);
	};
	
	/**
	 * Get the block's damage value (for use with pick block).
	 */
	@Override
	public int getDamageValue(World world, int i, int j, int k) {
		TileEntityRut ter = (TileEntityRut) world.getTileEntity(i, j, k);
		return ter.maskMeta;
	}
	
	/**
	 * Get a light value for the block at the specified coordinates, normal ranges are between 0 and 15
	 *
	 * @param world
	 *            The current world
	 * @param x
	 *            X Position
	 * @param y
	 *            Y position
	 * @param z
	 *            Z position
	 * @return The light value
	 */
	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		TileEntityRut ter = (TileEntityRut) world.getTileEntity(x, y, z);
		
		Block maskBlock = ter.maskBlock;
		Block fluidBlock = ter.fluidBlock;
		int light = lightValue;
		
		if (maskBlock != null && maskBlock != this) {
			light = Math.max(light, maskBlock.getLightValue());
		}
		
		if (fluidBlock != null && fluidBlock != this) {
			light = Math.max(light, fluidBlock.getLightValue());
			
			Fluid fluid = FluidRegistry.lookupFluidForBlock(fluidBlock);
			if (fluid != null) {
				light = Math.max(light, fluid.getLuminosity());
			}
		}
		
		return light;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
	}
}