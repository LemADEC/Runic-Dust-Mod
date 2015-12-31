/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustmod.DustMod;

/**
 * 
 * @author billythegoat101
 */
public class ItemSpiritPickaxe extends ItemPickaxe {
	
	public ItemSpiritPickaxe() {
		super(ToolMaterial.EMERALD);
		setMaxDamage(ToolMaterial.IRON.getMaxUses());
	}
	
	@Override
	public EnumRarity getRarity(ItemStack p_77613_1_) {
		return EnumRarity.epic;
	}
	
	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 72000;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.bow;
	}
	
	@Override
	public void onUpdate(ItemStack itemStackTool, World world, Entity entity, int slotIndexTool, boolean isCurrentItem) {
		super.onUpdate(itemStackTool, world, entity, slotIndexTool, isCurrentItem);
		DustMod.repairToolWithDust(itemStackTool, world, entity, slotIndexTool, isCurrentItem, 300);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		entityPlayer.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
		return super.onItemRightClick(itemStack, world, entityPlayer);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityPlayer entityPlayer, int itemInUseCount) {
		Vec3 playerHeadPosition = Vec3.createVectorHelper(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ);
		playerHeadPosition.yCoord += entityPlayer.getEyeHeight();
		float ticks = 1F;
		Vec3 look = entityPlayer.getLook(ticks);
		double distance = 7D;
		Vec3 result = playerHeadPosition.addVector(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance);
		
		MovingObjectPosition mopCollision = entityPlayer.worldObj.rayTraceBlocks(playerHeadPosition, result);
		if (mopCollision == null) {
			return;
		}
		int x = mopCollision.blockX;
		int y = mopCollision.blockY;
		int z = mopCollision.blockZ;
		
		int level = entityPlayer.experienceLevel + 1;
		double dropChance = Math.min(1.0D, level * level / 900D) * 0.50D;
		
		int use = getMaxItemUseDuration(itemStack) - itemInUseCount;
		int radius = 0;
		if (use <= 5) {
			return;
		} else if (use > 50) {
			radius = 2;
			entityPlayer.addExhaustion(1);
		} else if (use > 25) {
			radius = 1;
		}
		
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					if (itemStack.getItemDamage() >= itemStack.getMaxDamage()) {
						return;
					}
					DustMod.breakAblock(entityPlayer, itemStack, world, x + dx, y + dy, z + dz, dropChance, 300);
				}
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(DustMod.spritePath + getUnlocalizedName().replace("item.", ""));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean advancedItemTooltips) {
		super.addInformation(itemStack, entityPlayer, list, advancedItemTooltips);
		list.add("Consume lapis dust to repair itself");
	}
}
