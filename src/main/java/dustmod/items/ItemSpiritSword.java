/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.items;

import java.util.List;

import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustmod.DustMod;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
public class ItemSpiritSword extends ItemSword {
	public ItemSpiritSword() {
		super(ToolMaterial.EMERALD);
		setMaxDamage(ToolMaterial.GOLD.getMaxUses());
	}
	
	@Override
	public EnumRarity getRarity(ItemStack p_77613_1_) {
		return EnumRarity.epic;
	}
	
	@Override
	public void onUpdate(ItemStack itemStackTool, World world, Entity entity, int slotIndexTool, boolean isCurrentItem) {
		super.onUpdate(itemStackTool, world, entity, slotIndexTool, isCurrentItem);
		DustMod.repairToolWithDust(itemStackTool, world, entity, slotIndexTool, isCurrentItem, 300);
	}
	
	@Override
	public Multimap getItemAttributeModifiers() {
		Multimap multimap = super.getItemAttributeModifiers();
		multimap.removeAll(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName());
		multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Tool modifier", 12.0D, 0));
		return multimap;
	}
	
	@Override
	public boolean hitEntity(ItemStack itemStackTool, EntityLivingBase entityLivingBaseHit, EntityLivingBase entityLivingBaseAttacker) {
		if (entityLivingBaseAttacker instanceof EntityPlayer && !entityLivingBaseHit.worldObj.isRemote) {
			EntityPlayer entityPlayer = (EntityPlayer) entityLivingBaseAttacker;
			
			int level = entityPlayer.experienceLevel + 5;
			double dropChance = Math.min(1.0D, level / 25.0D) * 0.80D;
			
			if (entityPlayer.worldObj.rand.nextDouble() < dropChance) {
				int amount = 1 + entityPlayer.worldObj.rand.nextInt(4);
				EntityItem entityItem = new EntityItem(entityPlayer.worldObj, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, new ItemStack(DustMod.itemDust, amount, 200));
				entityItem.delayBeforeCanPickup = 0;
				entityPlayer.worldObj.spawnEntityInWorld(entityItem);
			}
		}
		return super.hitEntity(itemStackTool, entityLivingBaseHit, entityLivingBaseAttacker);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(DustMod.spritePath + this.getUnlocalizedName().replace("item.", ""));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean advancedItemTooltips) {
		super.addInformation(itemStack, entityPlayer, list, advancedItemTooltips);
		list.add("Consume lapis dust to repair itself");
	}
}
