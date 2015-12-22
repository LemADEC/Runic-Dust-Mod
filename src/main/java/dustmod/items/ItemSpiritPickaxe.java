/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
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
	
	public ItemSpiritPickaxe(ToolMaterial enumtoolmaterial) {
		super(enumtoolmaterial);
		setMaxDamage(250);
		efficiencyOnProperMaterial = 16F;
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
	
	static private int damageRepairedPerDust = 4;
	@Override
	public void onUpdate(ItemStack itemStackTool, World world, Entity entity, int slotIndexTool, boolean isCurrentItem) {
		super.onUpdate(itemStackTool, world, entity, slotIndexTool, isCurrentItem);
		if (!(entity instanceof EntityPlayer) || itemStackTool.getItemDamage() == 0) {
			return;
		}
		
		EntityPlayer entityPlayer = (EntityPlayer)entity;
		int repairDelay = isCurrentItem ? 6 : 70;
		if (world.getWorldTime() % repairDelay == 0) {
			return;
		}
		
		// search for lapis dust
		int dustAvailable = 0;
		if (entityPlayer.capabilities.isCreativeMode) {
			dustAvailable = 256;
		} else {
			for (int slotIndexScan = 0; slotIndexScan < entityPlayer.inventory.mainInventory.length; slotIndexScan++) {
				ItemStack itemStack = entityPlayer.inventory.mainInventory[slotIndexScan];
				if (itemStack == null) {
					continue;
				}
				if (itemStack.getItem() == DustMod.itemDust && itemStack.getItemDamage() == 300) {
					dustAvailable += itemStack.stackSize;
				} else if (itemStack.getItem() == DustMod.pouch) {
					if (ItemPouch.getValue(itemStack) == 300) {
						dustAvailable += ItemPouch.getDustAmount(itemStack);
					}
				}
			}
		}
		if (dustAvailable <= 0) {
			return;
		}
		
		int maxRepairable = (int)Math.ceil(itemStackTool.getItemDamage() / (float)damageRepairedPerDust);
		int dustToConsume = Math.min(5, Math.min(maxRepairable, dustAvailable));
		int dustConsumed = 0;
		for (int slotIndexScan = 0; slotIndexScan < entityPlayer.inventory.mainInventory.length; slotIndexScan++) {
			if (dustConsumed >= dustToConsume) {
				break;
			}
			ItemStack itemStack = entityPlayer.inventory.mainInventory[slotIndexScan];
			if (itemStack == null) {
				continue;
			}
			if (itemStack.getItem() == DustMod.itemDust && itemStack.getItemDamage() == 300) {
				int subStack = Math.min(itemStack.stackSize, dustToConsume);
				itemStack.stackSize -= subStack;
				dustConsumed -= subStack;
				
				if (itemStack.stackSize == 0) {
					entityPlayer.inventory.mainInventory[slotIndexScan] = null;
				}
			} else if (itemStack.getItem() == DustMod.pouch) {
				if (ItemPouch.getValue(itemStack) == 300) {
					int subStack = Math.min(ItemPouch.getDustAmount(itemStack), dustToConsume);
					ItemPouch.subtractDust(itemStack, subStack);
					dustConsumed -= subStack;
				}
			}
		}
		itemStackTool.setItemDamage(itemStackTool.getItemDamage() - damageRepairedPerDust * dustToConsume);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		entityPlayer.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
		
		return itemStack;
	}
	
	public static void breakAblock(EntityPlayer entityPlayer, ItemStack itemStack, World world, int x, int y, int z, double dropChance) {
		if (!world.blockExists(x, y, z) || world.isRemote) {
			return;
		}
		
		Block block = world.getBlock(x, y, z);
		int metadata = world.getBlockMetadata(x, y, z);
		
		Material material = block.getMaterial();
		if (!block.isAir(world, x, y, z) && block.getPlayerRelativeBlockHardness(entityPlayer, world, x, y, z) > 0) {
			if (!block.canHarvestBlock(entityPlayer, metadata)) {
				return;
			}
			if (material != Material.rock && material != Material.iron && material != Material.ice && material != Material.glass && material != Material.piston && material != Material.anvil) {
				return;
			}
			
			block.onBlockHarvested(world, x, y, z, metadata, entityPlayer);
			
			if (block.removedByPlayer(world, entityPlayer, x, y, z, true)) {
				block.onBlockDestroyedByPlayer(world, x, y, z, metadata);
				block.harvestBlock(world, entityPlayer, x, y, z, metadata);
				
				if (world.rand.nextDouble() < dropChance) {
					EntityItem entityItem = new EntityItem(entityPlayer.worldObj, x + 0.5, y + 0.5, z + 0.5, new ItemStack(DustMod.itemDust, 1, 300));
					world.spawnEntityInWorld(entityItem);
				}
				
				if (!entityPlayer.capabilities.isCreativeMode) {
					itemStack.damageItem(1, entityPlayer);
				}
				
				world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (metadata << 12));
			}
		}
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
					breakAblock(entityPlayer, itemStack, world, x + dx, y + dy, z + dz, dropChance);
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
	public void addInformation(ItemStack item, EntityPlayer player, List list, boolean flag) {
		super.addInformation(item, player, list, flag);
		list.add("Consume lapis dust to repair itself");
	}
}
