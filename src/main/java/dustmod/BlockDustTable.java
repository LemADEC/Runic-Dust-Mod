package dustmod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

public class BlockDustTable extends BlockContainer 
{

	private IIcon topTex;
	private IIcon sideTex;
	private IIcon botTex;
    public BlockDustTable()
    {
        super(Material.wood);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
        setLightOpacity(0);
        this.setHardness(3F);
        this.setHardness(2.5F);
        this.setStepSound(Block.soundTypeWood);
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

//    public void randomDisplayTick(World world, int i, int j, int k, Random random)
//    {
//        super.randomDisplayTick(world, i, j, k, random);
//        for (int l = i - 2; l <= i + 2; l++)
//        {
//            for (int i1 = k - 2; i1 <= k + 2; i1++)
//            {
//                if (l > i - 2 && l < i + 2 && i1 == k - 1)
//                {
//                    i1 = k + 2;
//                }
//                if (random.nextInt(16) != 0)
//                {
//                    continue;
//                }
//                for (int j1 = j; j1 <= j + 1; j1++)
//                {
//                    if (world.getBlockId(l, j1, i1) != Block.bookShelf.blockID)
//                    {
//                        continue;
//                    }
//                    if (!world.isAirBlock((l - i) / 2 + i, j1, (i1 - k) / 2 + k))
//                    {
//                        break;
//                    }
//                    world.spawnParticle("enchantmenttable", (double)i + 0.5D, (double)j + 2D, (double)k + 0.5D, (double)((float)(l - i) + random.nextFloat()) - 0.5D, (float)(j1 - j) - random.nextFloat() - 1.0F, (double)((float)(i1 - k) + random.nextFloat()) - 0.5D);
//                }
//            }
//        }
//    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityliving, ItemStack item)
    {
    	int l = MathHelper.floor_double((double)(entityliving.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0)
        {
            world.setBlockMetadataWithNotify(x, y, z, 3, 2);
        }

        if (l == 1)
        {
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
        }

        if (l == 2)
        {
            world.setBlockMetadataWithNotify(x, y, z, 1, 2);
        }

        if (l == 3)
        {
            world.setBlockMetadataWithNotify(x, y, z, 2, 2);
        }

        if (item.hasDisplayName())
        {
            ((TileEntityFurnace)world.getTileEntity(x, y, z)).func_145951_a(item.getDisplayName());
        }
    }

    public boolean isOpaqueCube()
    {
        return false;
    }

    public IIcon getBlockTextureFromSideAndMetadata(int side, int meta)
    {
        if (side == 1)
        {
            return topTex;
        }

        if (side == 0)
        {
            return botTex;
        }

        return sideTex;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,int dir, float cx, float cy, float cz)
    {
        if (/*world.isRemote*/false)
        {
            return true;
        }
        else if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == DustMod.runicPaper)
        {
            int page = (((TileEntityDustTable)world.getTileEntity(x, y, z)).page - 1);

            if (page == -1)
            {
                return true;
            }

            page = DustManager.getShape(page).id;
            ItemStack to = new ItemStack(DustMod.dustScroll, 1, page);
            ItemStack cur = player.getCurrentEquippedItem() ;

            if (cur.stackSize == 1)
            {
            	player.setCurrentItemOrArmor(0, new ItemStack(DustMod.dustScroll, 1, to.getItemDamage()));
            }
            else
            {
                player.inventory.addItemStackToInventory(to);
                cur.stackSize--;
            }

            return true;
        }
        else
        {
            if (player.isSneaking())
            {
                onBlockClicked(world, x, y, z, player);
                return true;
            }

            TileEntityDustTable tedt = (TileEntityDustTable)world.getTileEntity(x, y, z);
            tedt.page --;

            if (tedt.page < 0)
            {
                tedt.page = DustManager.getNames().size() - DustMod.numSec;
            }

            return true;
        }
    }

    @Override
    public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer)
    {
        if (/*world.multiplayerWorld*/false)
        {
            return;
        }
        else
        {
            TileEntityDustTable tedt = (TileEntityDustTable)world.getTileEntity(i, j, k);
            tedt.page++;

            if (tedt.page >= DustManager.getNames().size() - DustMod.numSec + 1)
            {
                tedt.page = 0;
            }
        }
    }

	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
        return new TileEntityDustTable();
	}

    @SideOnly(Side.CLIENT)
    public void func_94332_a(IIconRegister iconRegister)
    {
        this.topTex = iconRegister.registerIcon(DustMod.spritePath + "table_top");
        this.sideTex = iconRegister.registerIcon(DustMod.spritePath + "table_side");
        this.botTex = iconRegister.registerIcon(DustMod.spritePath + "table_bottom");
    }
}
