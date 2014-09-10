/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 *
 * @author billythegoat101
 */
public class ItemDust extends DustModItem
{
    private Block block;

    private IIcon mainIIcon;
    private IIcon subIIcon;
    
    public ItemDust(Block block)
    {
        super();
        this.block = block;
        setMaxDamage(0);
        setHasSubtypes(true);
    }
   
    
    
    public boolean onItemUse(ItemStack item, EntityPlayer p, World world, int i, int j, int k, int face, float x, float y, float z)
    {
		if(!world.canMineBlock(p, i, j, k)) return false;
		
        Block var11 = world.getBlock(i, j, k);

        if(var11 == DustMod.dust && world.getTileEntity(i, j, k) != null){
            DustMod.dust.onBlockActivated(world, i, j, k, p, face, x, y, z);
            return false;
        }
        
        if (var11 == Blocks.snow)
        {
            face = 1;
        }
        else if (var11 != Blocks.vine && var11 != Blocks.tallgrass && var11 != Blocks.deadbush)
        {
            if (face == 0)
            {
                --j;
            }

            if (face == 1)
            {
                ++j;
            }

            if (face == 2)
            {
                --k;
            }

            if (face == 3)
            {
                ++k;
            }

            if (face == 4)
            {
                --i;
            }

            if (face == 5)
            {
                ++i;
            }
        }

        if (!p.canPlayerEdit(i, j, k, 7, item))
        {
            return false;
        }
        else if (item.stackSize == 0)
        {
            return false;
        }
        else
        {
            if (world.canPlaceEntityOnSide(this.block, i, j, k, false, face, (Entity)null, item))
            {
                int var13 = block.onBlockPlaced(world, i, j, k, face, x, y, z, 0);


                if (world.setBlock(i, j, k, this.block,0,3))
                {
                    if (world.getBlock(i, j, k) == this.block)
                    {
                    	block.onBlockPlacedBy(world, i, j, k, p, item);
                    	block.onPostBlockPlaced(world, i, j, k, var13);
                    }
                    DustMod.dust.onBlockActivated(world, i, j, k, p, face, x, y, z);

                    world.playSoundEffect((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), block.stepSound.getStepResourcePath(), (block.stepSound.getVolume() + 1.0F) / 6.0F, block.stepSound.getPitch() * 0.99F);
                    --item.stackSize;
                }
            }

            return true;
        }
    }
    

    @Override
    public String getUnlocalizedName(ItemStack itemstack)
    {
    	String id = DustItemManager.getId(itemstack.getItemDamage());
    	if(id != null) return "tile.dust." + id;

        return "tile.dust";
    }
    

    @SideOnly(Side.CLIENT)
    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int i = 5; i < 1000; ++i) //i > 4 for migration from old system
        {
        	if(DustItemManager.hasDust(i)){
                par3List.add(new ItemStack(par1, 1, i));
        	}
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
    	int meta = stack.getItemDamage();
    	return pass == 0 ? DustItemManager.getPrimaryColor(meta) : DustItemManager.getSecondaryColor(meta);
    }


    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Gets an IIcon index based on an item's damage value and the given render pass
     */
    public IIcon getIIconFromDamageForRenderPass(int meta, int rend)
    {
    	if(rend == 0) return mainIIcon;
    	else return subIIcon;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {
    	this.mainIIcon = IIconRegister.registerIcon(DustMod.spritePath + "dustItem_main");
    	this.subIIcon = IIconRegister.registerIcon(DustMod.spritePath + "dustItem_sub");
    }
}