/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

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
		setMaxStackSize(4);
	}
	
	@Override
	public boolean onItemUse(ItemStack item, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!world.canMineBlock(entityPlayer, x, y, z)) {
			return false;
		}
		
		if (entityPlayer == null) {
			return false;
		}
		
		RuneShape runeShape = RuneManager.getShapeFromID(item.getItemDamage());
		int rotation = Math.round(6F - entityPlayer.rotationYaw / 90F) % 4;
		
		if (DustMod.isDust(world.getBlock(x, y, z))) {
			y--;
		}
		
		try {
			if (entityPlayer.capabilities.isCreativeMode) {
				runeShape.drawOnWorldWhole(world, x, y + 1, z, entityPlayer, rotation);
			} else {
				runeShape.drawOnWorldPart(world, x, y + 1, z, entityPlayer, rotation);
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
		if (shape != null) {
			return "item.scroll";
		} else {
			return "item.scroll.error";
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack item, EntityPlayer player, List list, boolean flag) {
		super.addInformation(item, player, list, flag);
		RuneShape shape = RuneManager.getShapeFromID(item.getItemDamage());
		if (shape == null) {
			return;
		}
		
		String tooltip;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			tooltip = "Required sacrifice:\n" + shape.getNotes().replace("Sacrifice\n", "");
			
			// remove notes
			int indexNotes = tooltip.indexOf("\n\nNotes");
			if (indexNotes != -1) {
				tooltip = tooltip.substring(0, indexNotes);
			}
		} else {
			tooltip = "§f§l" + shape.getRuneName() + "§7\n" + shape.getDescription().replace("Description\n", "") + "\n\n§b<Press shift to show sacrifices>";
		}
		tooltip = tooltip.replace("§", "" + (char)167);
		
		String[] split = tooltip.split("\n");
		for (String line : split) {
			String lineRemaining = line;
			while (lineRemaining.length() > 38) {
				int index = lineRemaining.substring(0, 38).lastIndexOf(' ');
				if (index == -1) {
					list.add(lineRemaining);
					lineRemaining = "";
				} else {
					list.add(lineRemaining.substring(0, index));
					lineRemaining = lineRemaining.substring(index + 1);
				}
			}
			
			list.add(lineRemaining);
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