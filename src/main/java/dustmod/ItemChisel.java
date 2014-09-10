/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 *
 * @author billythegoat101
 */
public class ItemChisel extends DustModItem
{
    private int tex;

    public ItemChisel()
    {
        super();
        
        setMaxStackSize(1);
        setMaxDamage(238);
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer p, World world, int i, int j, int k, int face,float x,float y,float z)
    {

		if(!world.canMineBlock(p, i, j, k)) return false;
    	
        Block block = world.getBlock(i, j, k);
        int meta = world.getBlockMetadata(i, j, k);

        if(block == DustMod.dust){
        	j--;
        	block = world.getBlock(i, j, k);
            meta = world.getBlockMetadata(i, j, k);
        }
        
        if (block == DustMod.rutBlock)
        {
            itemstack.damageItem(1, p);
        }

        if (block == null)
        {
            return false;
        }

        if ((block.getBlockHardness(world,i,j,k) > Blocks.log.getBlockHardness(world,i,j,k) && !DustMod.Enable_Decorative_Ruts) || block == Blocks.bedrock)
        {
            return false;
        }
        else if (!block.isOpaqueCube() || block.getRenderType() != 0 || !block.renderAsNormalBlock())
        {
            return false;
        }

        itemstack.damageItem(1, p);

//        if (!world.isRemote)
//        {
            world.setBlock(i, j, k, DustMod.rutBlock, meta,3);
            TileEntityRut ter = (TileEntityRut)world.getTileEntity(i, j, k);
            ter.maskBlock = block;
            ter.maskMeta = meta;
            DustMod.rutBlock.onBlockActivated(world, i, j, k, p,face,x,y,z);
//            System.out.println("Set");
//        }

//        System.out.println("Setting to " + blockID + " " + meta);
        return true;
    }
//    @Override
//    public String getItemName() {
//        return "dustchisel";
//    }
}