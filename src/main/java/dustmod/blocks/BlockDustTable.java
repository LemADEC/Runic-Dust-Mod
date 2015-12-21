package dustmod.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustmod.DustMod;
import dustmod.runes.RuneManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockDustTable extends BlockContainer {
	
	private IIcon topTex;
	private IIcon sideTex;
	private IIcon botTex;
	
	public BlockDustTable() {
		super(Material.wood);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
		setLightOpacity(0);
		this.setHardness(3F);
		this.setHardness(2.5F);
		this.setStepSound(Block.soundTypeWood);
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack item) {
		int l = MathHelper.floor_double(entityliving.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		
		if (l == 0) {
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);
		}
		
		if (l == 1) {
			world.setBlockMetadataWithNotify(x, y, z, 0, 2);
		}
		
		if (l == 2) {
			world.setBlockMetadataWithNotify(x, y, z, 1, 2);
		}
		
		if (l == 3) {
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		}
		
		if (item.hasDisplayName()) {
			((TileEntityFurnace) world.getTileEntity(x, y, z)).func_145951_a(item.getDisplayName());
		}
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		if (side == 1) {
			return topTex;
		}
		
		if (side == 0) {
			return botTex;
		}
		
		return sideTex;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int dir, float cx, float cy, float cz) {
		if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == DustMod.runicPaper) {
			int page = (((TileEntityDustTable) world.getTileEntity(x, y, z)).page - 1);
			
			if (page == -1) {
				return true;
			}
			
			page = RuneManager.getShape(page).id;
			ItemStack to = new ItemStack(DustMod.dustScroll, 1, page);
			ItemStack cur = player.getCurrentEquippedItem();
			
			if (cur.stackSize == 1) {
				player.setCurrentItemOrArmor(0, new ItemStack(DustMod.dustScroll, 1, to.getItemDamage()));
			} else {
				player.inventory.addItemStackToInventory(to);
				cur.stackSize--;
			}
			
			return true;
		} else {
			if (world.isRemote) {
				return true;
			}
			
			if (player.isSneaking()) {
				onBlockClicked(world, x, y, z, player);
				return true;
			}
			
			TileEntityDustTable tedt = (TileEntityDustTable) world.getTileEntity(x, y, z);
			tedt.page--;
			
			if (tedt.page < 0) {
				tedt.page = RuneManager.getNames().size() - DustMod.numSec;
			}
			
			tedt.markDirty();
			world.markBlockForUpdate(x, y, z);
			
			return true;
		}
	}
	
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer entityplayer) {
		if (world.isRemote) {
			return;
		}
		
		TileEntityDustTable tedt = (TileEntityDustTable) world.getTileEntity(x, y, z);
		tedt.page++;
		
		if (tedt.page >= RuneManager.getNames().size() - DustMod.numSec + 1) {
			tedt.page = 0;
		}
		
		tedt.markDirty();
		world.markBlockForUpdate(x, y, z);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityDustTable();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.topTex = iconRegister.registerIcon(DustMod.spritePath + "table_top");
		this.sideTex = iconRegister.registerIcon(DustMod.spritePath + "table_side");
		this.botTex = iconRegister.registerIcon(DustMod.spritePath + "table_bottom");
	}
}
