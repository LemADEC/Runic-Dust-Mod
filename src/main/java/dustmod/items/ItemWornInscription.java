package dustmod.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustmod.DustMod;
import dustmod.inscriptions.InscriptionEvent;
import dustmod.inscriptions.InscriptionManager;

public class ItemWornInscription extends ItemArmor implements ISpecialArmor {

	public static final int max = 1001;
	
	public ItemWornInscription() {
		super(ArmorMaterial.CLOTH, 0, 1);
		// this.hasSubtypes = true;
		this.setMaxDamage(max);
	}
	
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
		
		int prevented = (int) damage - InscriptionManager.getPreventedDamage(player, armor, source, (int) damage);
		
		ArmorProperties rtn = new ArmorProperties(0, 1d, prevented);
		// rtn.AbsorbRatio = 0.5D;
		rtn.Slot = 1;
		return rtn;
	}
	
	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		
		return InscriptionManager.getArmor(player, armor);
	}
	
	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
		InscriptionManager.onDamage(entity, stack, source, damage);
	}
	
	// public
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return DustMod.spritePath + "wornInscription.png";
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		if (itemstack.getItemDamage() == -1 || InscriptionManager.getEvent(itemstack) == null) {
			return "item.inscblank";
		}
		return "item.insc." + InscriptionManager.getEvent(itemstack).idName;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List list) {
		for (InscriptionEvent i : InscriptionManager.getEvents()) {
			ItemStack item = new ItemStack(par1, 1, 0);
			
			if (!item.hasTagCompound())
				item.setTagCompound(new NBTTagCompound());
			
			item.getTagCompound().setInteger("eventID", i.id);
			list.add(item);
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer p) {
		return InscriptionManager.onItemRightClick(p, item);
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
		// if (par2EntityPlayer instanceof EntityPlayerMP)
		// {
		// var21 =
		// ((EntityPlayerMP)par2EntityPlayer).theItemInWorldManager.getBlockReachDistance();
		// }
		Vec3 var23 = var13.addVector(var18 * var21, var17 * var21, var20 * var21);
		lastMOP = world.rayTraceBlocks(var13, var23, !par3);
		return lastMOP;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int par1, int par2) {
		return this.itemIcon;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon(DustMod.spritePath + "wornInscription");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean advancedItemTooltips) {
		super.addInformation(itemStack, player, list, advancedItemTooltips);
		
		String tooltip = "";
		
		InscriptionEvent inscriptionEvent = InscriptionManager.getEvent(itemStack);
		if (inscriptionEvent == null) {
			tooltip += "§f§c" + "Invalid inscription design";
		} else {
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
				tooltip = "Required sacrifice:\n"
						+ inscriptionEvent.getNotes().replace("Sacrifice\n", "")
						+ "\n\n§bInscription Enchant Rune§7 will consume those sacrifice to recharge the inscription.";
				
				// remove notes
				int indexNotes = tooltip.indexOf("\n\nNotes");
				if (indexNotes != -1) {
					tooltip = tooltip.substring(0, indexNotes);
				}
			} else {
				tooltip += "§7\n" + inscriptionEvent.getDescription().replace("Description\n", "") + "\n\n§b<Press shift to show sacrifices>";
			}
		}
		/*
		NBTTagCompound tag = itemStack.getTagCompound();
		if (tag.hasKey("eventID")) {
			tooltip += "\neventID is " + tag.getInteger("eventID");
		} else {
			tooltip += "\nNo eventID defined";
		}
		/**/
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
}
