package dustmod.items;

import java.util.List;

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
	
	public static int[] getDesign(ItemStack itemStack) {
		if (itemStack == null || itemStack.getItem() == DustMod.getWornInscription() || !itemStack.hasTagCompound()) {
			return null;
		}
		
		NBTTagCompound tag = itemStack.getTagCompound();
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
		if (item == null || !item.hasTagCompound()) {
			return true;
		}
		
		NBTTagCompound tag = item.getTagCompound();
		int[] design = tag.getIntArray("design");
		
		if (design == null) {
			return true;
		}
		
		for (int i = 0; i < design.length; i++) {
			if (design[i] != 0) {
				return false;
			}
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
				if (!DustMod.debug) {
					itemStack.setItemDamage(metadata + amount);
				}
			}
		}
		if (metadata >= max) {
			// Get event also logs the EventID into the item's nbt
			InscriptionEvent event = InscriptionManager.getEvent(itemStack);
			if (event != null) {
				InscriptionManager.onCreate((EntityPlayer) entity, itemStack);
				itemStack.func_150996_a(DustMod.getWornInscription());
				itemStack.setItemDamage(ItemWornInscription.max);
				
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
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean advancedItemTooltips) {
		super.addInformation(itemStack, player, list, advancedItemTooltips);
		
		String tooltip = "";
		
		if (isDesignEmpty(itemStack)) {
			tooltip += "Blank inscription.\n§bRight click§7 to draw a design using Runic inks from your hotbar.";
			
		} else {
			InscriptionEvent inscriptionEvent = InscriptionManager.getEvent(itemStack);
			if (inscriptionEvent == null) {
				tooltip += "§f§c" + "Invalid inscription design";
			} else {
				tooltip += "§f§l" + inscriptionEvent.getInscriptionName()
						+ "§7\n" + inscriptionEvent.getDescription().replace("Description\n", "")
						+ "\n\n§bWait§7 for it to dry out"
						+ "\nThen §bUse Inscription Enchant rune§7 to fuel it";
			}
			/*
			NBTTagCompound tag = itemStack.getTagCompound();
			if (tag.hasKey("eventID")) {
				tooltip += "\neventID is " + tag.getInteger("eventID");
			} else {
				tooltip += "\nNo eventID defined";
			}
			/**/
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
}
