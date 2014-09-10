/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod;

import java.util.Random;
import java.util.logging.Level;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * 
 * @author billythegoat101
 */
public class ItemSpiritPickaxe extends ItemPickaxe {
	private static Block blocksEffectiveAgainst[];

	public ItemSpiritPickaxe(ToolMaterial enumtoolmaterial) {
		super(enumtoolmaterial);
		setMaxDamage(250);
		efficiencyOnProperMaterial = 16F;
	}

	public EnumRarity func_40398_f(ItemStack itemstack) {
		return EnumRarity.epic;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 72000;
	}

	/**
	 * returns the action that specifies what animation to play when the items
	 * is being used
	 */
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.bow;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));

		return par1ItemStack;
	}

	public void onPlayerStoppedUsing(ItemStack item, World world, EntityPlayer player, int useAmt) {
		int use = this.getMaxItemUseDuration(item) - useAmt;

		float ticks = 1F;
		double distance = 7D;

		Vec3 pos = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
		pos.yCoord += player.getEyeHeight();
		Vec3 look = player.getLook(ticks);
		Vec3 result = pos.addVector(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance);

		MovingObjectPosition click = player.worldObj.rayTraceBlocks(pos, result);

		if (click == null) {
			return;
		}

		int x, y, z;
		x = (int) click.blockX;
		y = (int) click.blockY;
		z = (int) click.blockZ;

		Random rand = new Random();
		int level = player.experienceLevel + 1;
		level *= level;
		double tol = (double) level / 900D;

		if (use > 25) {
			boolean creative = player.capabilities.isCreativeMode;
			boolean playedSound = false;
			int rad = 1;
			for (int i = -rad; i <= rad; i++) {
				for (int j = -rad; j <= rad; j++) {
					for (int k = -rad; k <= rad; k++) {
						if (item.getItemDamage() >= item.getMaxDamage())
							continue;
						Block block = world.getBlock(x + i, y + j, z + k);
						int meta = world.getBlockMetadata(x + i, y + j, z + k);
						try {

							if (block != null) // block is not null (air)
							{
								if (block.getMaterial() == Material.rock && block != Blocks.bedrock) // if block is made of rock
								{
									if (!playedSound) {
										world.playSoundEffect((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), block.stepSound.getStepResourcePath(),
												(block.stepSound.getVolume() + 1.0F) / 6.0F, block.stepSound.getPitch() * 0.99F);
										playedSound = true;
									}
									if (rand.nextDouble() < tol) {
										EntityItem ei = new EntityItem(player.worldObj, x + 0.5 + i, y + 0.5 + j, z + 0.5 + k, new ItemStack(DustMod.idust, 1, 300));
										world.spawnEntityInWorld(ei);
									}
									boolean success = block.removedByPlayer(world, player, x + i, y + j, z + k, false);

									if (success) {
										block.onBlockDestroyedByPlayer(world, x + i, y + j, z + k, meta);
									}
									// update world
									world.setBlockToAir(i + x, j + y, k + z); 
									// destroy block
									block.onBlockDestroyedByPlayer(world, i + x, j + y, k + z, world.getBlockMetadata(i + x, j + y, k + z)); 
									// drop block
									block.dropBlockAsItem(world, i + x, j + y, k + z, world.getBlockMetadata(i + x, j + y, k + z), 0);
									if (!creative)
										item.damageItem(1, player);
								}
							}
						} catch (Exception e) {
							DustMod.logger.warn("Error breaking block " + block.getUnlocalizedName(), e);
							e.printStackTrace();
						}// fracking mods
					}
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(DustMod.spritePath + this.getUnlocalizedName().replace("item.", ""));
	}

}
