package dustmod.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustmod.DustMod;
import dustmod.inscriptions.InscriptionEvent;
import dustmod.inscriptions.InscriptionManager;
import dustmod.inscriptions.InscriptionInventory;

public class ItemInscription extends DustModItem {
	
	public static final int max = 3600 * 2;
	
	private IIcon dryingIIcon;
	private IIcon driedIIcon;
	private IIcon blankIIcon;
	
	public ItemInscription() {
		super();
		this.setMaxStackSize(1);
		this.setMaxDamage(max);
	}
	
	public InscriptionInventory getInventory(ItemStack item) {
		return new InscriptionInventory(item);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player) {
		player.openGui(DustMod.instance, 0, world, 0, 0, 0);
		
		return item;
	}
	
	public static int[] getDesign(ItemStack item) {
		if (item == null || !item.hasTagCompound())
			return null;
		
		NBTTagCompound tag = item.getTagCompound();
		int[] design = tag.getIntArray("design");
		
		return design != null ? design.clone() : null;
	}
	
	public static void removeDesign(ItemStack item) {
		
		if (item == null || !item.hasTagCompound())
			return;
		
		NBTTagCompound tag = item.getTagCompound();
		tag.removeTag("design");
	}
	
	public static boolean isDesignEmpty(ItemStack item) {
		if (item == null || !item.hasTagCompound())
			return false;
		
		NBTTagCompound tag = item.getTagCompound();
		int[] design = tag.getIntArray("design");
		
		if (design == null)
			return true;
		
		for (int i = 0; i < design.length; i++) {
			if (design[i] != 0)
				return false;
		}
		
		return true;
	}
	
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int slotIndex, boolean isCurrentItem) {
		int metadata = itemStack.getItemDamage();
		if (isDried(itemStack)) {
			itemStack.setItemDamage(0);
			return;
		}
		if (metadata > 0) {
			if (isDesignEmpty(itemStack)) {
				itemStack.setItemDamage(0);
			} else {
				int amount = 3;
				int x = (int) entity.posX;
				int z = (int) entity.posZ;
				
				if (world.isRaining()) {
					amount = 1;
				}
				if (world.getBiomeGenForCoords(x, z).temperature > 1.0F) {
					amount = 6;
				}
				if (!DustMod.debug)
					itemStack.setItemDamage(metadata + amount);
			}
		}
		if (metadata >= max) {
			// Get event also logs the EventID into the item's nbt
			InscriptionEvent event = InscriptionManager.getEvent(itemStack);
			if (event != null) {
				InscriptionManager.onCreate((EntityPlayer) entity, itemStack);
				itemStack.func_150996_a(DustMod.getWornInscription());
				itemStack.setItemDamage(ItemInscription.max);
			} else {
				setDried(itemStack);
			}
		}
		super.onUpdate(itemStack, world, entity, slotIndex, isCurrentItem);
	}
	
	@Override
	public boolean getShareTag() {
		return true;
	}
	
	public boolean isDried(ItemStack item) {
		if (item.hasTagCompound()) {
			NBTTagCompound tag = item.getTagCompound();
			if (tag.hasKey("dried") && tag.getBoolean("dried"))
				return true;
			else {
				tag.setBoolean("dried", false);
				return false;
			}
		}
		return false;
	}
	
	public void setDried(ItemStack item) {
		item.getTagCompound().setBoolean("dried", true);
		item.setItemDamage(0);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		boolean isDried = isDried(itemstack);
		int damage = itemstack.getItemDamage();
		if (isDried) {
			return "item.driedinsc";
		} else if (damage != 0) {
			return "item.dryinginsc";
		} else {
			return "item.emptyinsc";
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int renderPass) {
		
		boolean isDried = isDried(stack);
		int damage = stack.getItemDamage();
		if (isDried) {
			return driedIIcon;
		} else if (damage != 0) {
			return dryingIIcon;
		} else {
			return blankIIcon;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister IIconRegister) {
		this.blankIIcon = IIconRegister.registerIcon(DustMod.spritePath + "blankInscription");
		this.dryingIIcon = IIconRegister.registerIcon(DustMod.spritePath + "dryingInscription");
		this.driedIIcon = IIconRegister.registerIcon(DustMod.spritePath + "driedInscription");
	}
}
