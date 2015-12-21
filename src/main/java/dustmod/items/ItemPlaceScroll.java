/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.items;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustmod.DustMod;
import dustmod.runes.RuneManager;
import dustmod.runes.RuneShape;

/**
 *
 * @author billythegoat101
 */
public class ItemPlaceScroll extends DustModItem {
	
	public ItemPlaceScroll() {
		super();
		setMaxDamage(0);
		setHasSubtypes(true);
		this.setMaxStackSize(4);
	}
	
	@Override
	public boolean onItemUse(ItemStack item, EntityPlayer entityPlayer, World world, int i, int j, int k, int l, float x, float y, float z) {
		if (!world.canMineBlock(entityPlayer, i, j, k))
			return false;
		
		if (entityPlayer == null) {
			return false;
		}
		
		RuneShape runeShape = RuneManager.getShapeFromID(item.getItemDamage());
		int rotation = MathHelper.floor_double((entityPlayer.rotationYaw * 4F) / 360F + 0.5D) & 3;
		
		if (DustMod.isDust(world.getBlock(i, j, k))) {
			j--;
		}
		
		try {
			if (entityPlayer.capabilities.isCreativeMode) {
				runeShape.drawOnWorldWhole(world, i, j + 1, k, entityPlayer, rotation);
			} else {
				runeShape.drawOnWorldPart(world, i, j + 1, k, entityPlayer, rotation, entityPlayer.getItemInUseCount());
			}
		} catch (Exception exception) {
			DustMod.logger.error("Unable to use scroll: " + exception.getMessage(), exception);
			exception.printStackTrace();
		}
		entityPlayer.inventory.markDirty();
		return true;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
		entityPlayer.setItemInUse(itemStack, this.getMaxItemUseDuration(itemStack));
		return itemStack;
	}
	
	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	@Override
	public EnumAction getItemUseAction(ItemStack itemStack) {
		return EnumAction.block;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D() {
		return false;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		RuneShape shape = RuneManager.getShapeFromID(itemstack.getItemDamage());
		if (shape != null)
			return "tile.scroll" + shape.name;
		else
			return "tile.scroll.error";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack item, EntityPlayer player, List list, boolean flag) {
		super.addInformation(item, player, list, flag);
		RuneShape shape = RuneManager.getShapeFromID(item.getItemDamage());
		if (shape == null) {
			return;
		}
		//        123456789012345678901234567890123456789
		list.add("Use this placing scroll to place a rune");
		list.add("");
		String sacr = shape.getNotes().replace("Sacrifice\n", "");
		String[] split = sacr.split("\n");
		list.add("Required sacrifice:");
		for (String line : split) {
			if (!line.isEmpty()) {
				if (line.charAt(0) != '-') {
					break;
				}
				String lineRemaining = line;
				while (lineRemaining.length() > 40) {
					int index = lineRemaining.substring(0, 40).lastIndexOf(' ');
					if (index == -1) {
						index = lineRemaining.length();
					}
					
					String add = lineRemaining.substring(0, index);
					if (!add.isEmpty()) {
						list.add(add);
					}
					lineRemaining = lineRemaining.substring(index);
				}
				list.add(lineRemaining);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List list) {
		for (RuneShape i : RuneManager.getShapes()) {
			list.add(new ItemStack(this, 1, i.id));
		}
	}
	
	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 72000;
	}
	
	public int[] getClickedBlock(Entity wielder, ItemStack item) {
		MovingObjectPosition click = this.getMovingObjectPositionFromPlayer(wielder.worldObj, (EntityPlayer) wielder, false);
		if (click != null && click.typeOfHit == MovingObjectType.BLOCK) {
			int tx = click.blockX;
			int ty = click.blockY;
			int tz = click.blockZ;
			return new int[] { tx, ty, tz };
		}
		return null;
	}
	
	protected MovingObjectPosition lastMOP = null;
	protected long lastCheck = 0;
	
	@Override
	public MovingObjectPosition getMovingObjectPositionFromPlayer(World world, EntityPlayer par2EntityPlayer, boolean par3) {
		// System.out.println("MOP Check " + world.getWorldTime() + " " + lastCheck);
		if (lastCheck > world.getWorldTime())
			lastCheck = world.getWorldTime();
		if (lastMOP != null && world.getWorldTime() - lastCheck < 0) {
			// System.out.println("MOP Cache");
			return lastMOP;
		}
		lastCheck = world.getWorldTime();
		float var4 = 1.0F;
		float var5 = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * var4;
		float var6 = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * var4;
		double var7 = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * var4;
		double var9 = par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * var4 + 1.62D - par2EntityPlayer.yOffset;
		double var11 = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * var4;
		Vec3 var13 = Vec3.createVectorHelper(var7, var9, var11);
		float var14 = MathHelper.cos(-var6 * 0.017453292F - (float) Math.PI);
		float var15 = MathHelper.sin(-var6 * 0.017453292F - (float) Math.PI);
		float var16 = -MathHelper.cos(-var5 * 0.017453292F);
		float var17 = MathHelper.sin(-var5 * 0.017453292F);
		float var18 = var15 * var16;
		float var20 = var14 * var16;
		double var21 = 65.0D;
		// if (par2EntityPlayer instanceof EntityPlayerMP) {
		// 	var21 = ((EntityPlayerMP)par2EntityPlayer).theItemInWorldManager.getBlockReachDistance();
		// }
		Vec3 var23 = var13.addVector(var18 * var21, var17 * var21, var20 * var21);
		lastMOP = world.rayTraceBlocks(var13, var23, !par3);
		return lastMOP;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister IIconRegister) {
		this.itemIcon = IIconRegister.registerIcon(DustMod.spritePath + "dustScroll");
	}
}