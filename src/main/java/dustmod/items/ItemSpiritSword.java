/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.items;

import java.util.Random;

import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustmod.DustMod;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;

/**
 *
 * @author billythegoat101
 */
public class ItemSpiritSword extends ItemSword
{
    public ItemSpiritSword()
    {
        super(ToolMaterial.EMERALD);
        setMaxDamage(131);
    }
    
    @Override
    public EnumRarity getRarity(ItemStack p_77613_1_) {
    	return EnumRarity.epic;
    }

    @Override
    public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag)
    {
        if (!itemstack.isItemEnchanted())
        {
            itemstack.addEnchantment(Enchantment.knockback, 10);
            itemstack.addEnchantment(Enchantment.smite, 5);
        }
        super.onUpdate(itemstack, world, entity, i, flag);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Multimap getItemAttributeModifiers()
    {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.removeAll(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName());
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Tool modifier", (double)12, 0));
        return multimap;
    }
    
    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player,
    		Entity entity) {

    	Random rand = new Random();
    	double r = rand.nextDouble();
    	
    	int level = player.experienceLevel+5;
    	double tol = (double)level/25D;
    	
    	if(r < tol){
    		int amt = 1;
    		if(rand.nextDouble() < 0.5D) amt = 2;
    		EntityItem ei = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(DustMod.itemDust,amt,200));
    		ei.delayBeforeCanPickup = 0;
    	}
    	
    	return false;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(DustMod.spritePath + this.getUnlocalizedName().replace("item.", ""));
	}
}
