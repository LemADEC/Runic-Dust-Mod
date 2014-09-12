/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.items;

import dustmod.DustMod;
import dustmod.runes.RuneManager;
import dustmod.runes.RuneShape;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 *
 * @author billythegoat101
 */
public class ItemRunicTome  extends DustModItem
{

    public ItemRunicTome()
    {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer p)
    {
        if (p.isSneaking())
        {
            DustMod.proxy.openTomeGUI(itemstack, p);
        }

        return super.onItemRightClick(itemstack, world, p);
    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer p, World world, int i, int j, int k, int l, float x, float y, float z)
    {
    	

    	int page = itemstack.getItemDamage();

        if (world.isRemote || page == 0 || p.isSneaking() || !p.capabilities.isCreativeMode)
        {
            return false;
        }

        RuneShape ds = RuneManager.getShape(page - 1);
        int r = (MathHelper.floor_double((double)(p.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);

        if (DustMod.isDust(world.getBlock(i, j, k)))
        {
            if (world.getBlockMetadata(i, j, k) == DustMod.DustMetaUsed)
            {
                world.setBlockToAir(i, j, k);
                j--;
            }
            else
            {
                return false;
            }
        }
    	
        return true;
    }
}