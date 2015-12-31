package dustmod.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dustmod.DustMod;
import dustmod.dusts.DustManager;

public class ItemPouch extends DustModItem {
	
	public static final int max = 6400;
	
	private ItemStack container = null;
	
	private IIcon bagIIcon;
	private IIcon mainIIcon;
	private IIcon subIIcon;
	
	public ItemPouch() {
		super();
		this.hasSubtypes = true;
		this.setMaxStackSize(1);
	}
	
	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (!world.canMineBlock(entityPlayer, x, y, z))
			return false;
		
		Block block = world.getBlock(x, y, z);
		
		if (block == Blocks.snow) {
			side = 1;
		} else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush) {
			if (side == 0) {
				--y;
			}
			
			if (side == 1) {
				++y;
			}
			
			if (side == 2) {
				--z;
			}
			
			if (side == 3) {
				++z;
			}
			
			if (side == 4) {
				--x;
			}
			
			if (side == 5) {
				++x;
			}
		}
		
		if (!entityPlayer.canPlayerEdit(x, y, z, 7, itemStack)) {
			return false;
		} else if (getDustAmount(itemStack) <= 0) {
			return false;
		} else {
			if (world.canPlaceEntityOnSide(DustMod.dust, x, y, z, false, side, (Entity) null, itemStack)) {
				int var13 = DustMod.dust.onBlockPlaced(world, x, y, z, side, hitX, hitY, hitZ, 0);
				
				if (world.setBlock(x, y, z, DustMod.dust, 0, 3)) {
					if (world.getBlock(x, y, z) == DustMod.dust) {
						DustMod.dust.onBlockPlacedBy(world, x, y, z, entityPlayer, itemStack);
						DustMod.dust.onPostBlockPlaced(world, x, y, z, var13);
					}
					DustMod.dust.onBlockActivated(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
					
					world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, DustMod.dust.stepSound.getStepResourcePath(),
							(DustMod.dust.stepSound.getVolume() + 1.0F) / 6.0F, DustMod.dust.stepSound.getPitch() * 0.99F);
					//                    if(!p.capabilities.isCreativeMode)subtractDust(item,1);
				}
			}
			
			return true;
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		int dust = getValue(itemstack);
		return DustManager.hasDust(dust) ? "item.pouch." + DustManager.getId(dust) : "item.pouch";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean advancedItemTooltips) {
		super.addInformation(itemStack, entityPlayer, list, advancedItemTooltips);
		int amount = ItemPouch.getDustAmount(itemStack);
		if (amount != 0) {
			list.add("Contains " + amount + " dust");
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 */
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 5; i < 1000; ++i) //i > 4 for migration from old system
		{
			if (DustManager.hasDust(i)) {
				par3List.add(new ItemStack(par1, 1, i * 2));
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		int meta = stack.getItemDamage();
		meta = meta >> 1;
		if (pass == 0)
			return super.getColorFromItemStack(stack, pass);
		return pass == 1 ? DustManager.getPrimaryColor(meta) : DustManager.getSecondaryColor(meta);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int par1, int rend) {
		switch (rend) {
		case 0:
			return bagIIcon;
		case 1:
			return mainIIcon;
		case 2:
			return subIIcon;
		default:
			return subIIcon;
		}
	}
	
	public void setContainerItemstack(ItemStack item) {
		container = item;
	}
	
	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack par1ItemStack) {
		return false;
	}
	
	@Override
	public Item getContainerItem() {
		return DustMod.pouch;
	}
	
	@Override
	public boolean hasContainerItem() {
		return true;
	}
	
	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		// TODO Auto-generated method stub
		return container;
	}
	
	public static int getDustAmount(ItemStack pouch) {
		if (pouch.getItem() == DustMod.itemDust) {
			return pouch.stackSize;
		}
		if (pouch.getItem() != DustMod.pouch)
			return -1;
		
		if (pouch.getItemDamage() % 2 == 0)
			return 0;
		
		if (!pouch.hasTagCompound()) {
			pouch.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound tag = pouch.getTagCompound();
		return tag.getInteger("dustamount");
	}
	
	public static boolean subtractDust(ItemStack pouch, int sub) {
		if (pouch.getItem() == DustMod.itemDust) {
			if (pouch.stackSize >= sub) {
				pouch.stackSize -= sub;
				return true;
			} else
				return false;
		}
		if (pouch.getItem() != DustMod.pouch)
			return false;
		
		if (!pouch.hasTagCompound()) {
			pouch.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound tag = pouch.getTagCompound();
		
		int amt = tag.getInteger("dustamount");
		if (amt >= sub) {
			tag.setInteger("dustamount", amt - sub);
			int dust = getValue(pouch);
			if (amt - sub == 0) {
				pouch.setItemDamage(dust * 2);
			} else {
				pouch.setItemDamage(dust * 2 + 1);
			}
			return true;
		} else
			return false;
	}
	
	/*
	 * @return amount remaining if attempting to add more than pouch can contain
	 */
	public static int addDust(ItemStack pouch, int add) {
		if (pouch.getItem() == DustMod.itemDust) {
			pouch.stackSize += add;
			return 0;
		}
		if (pouch.getItem() != DustMod.pouch)
			return -1;
		
		int rtn = 0;
		
		if (!pouch.hasTagCompound()) {
			pouch.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound tag = pouch.getTagCompound();
		
		int amt = tag.getInteger("dustamount");
		if (amt + add > max) {
			rtn = add - (max - amt);
			add -= rtn;
		}
		tag.setInteger("dustamount", amt + add);
		int dust = getValue(pouch);
		if (amt + add == 0) {
			pouch.setItemDamage(dust * 2);
		} else {
			pouch.setItemDamage(dust * 2 + 1);
		}
		return rtn;
	}
	
	public static void setAmount(ItemStack pouch, int amt) {
		if (pouch.getItem() == DustMod.itemDust) {
			pouch.stackSize -= amt;
		}
		if (pouch.getItem() != DustMod.pouch)
			return;
		
		if (!pouch.hasTagCompound()) {
			pouch.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound tag = pouch.getTagCompound();
		tag.setInteger("dustamount", amt);
		int dust = getValue(pouch);
		if (amt > 0) {
			pouch.setItemDamage(dust * 2 + 1);
		} else {
			pouch.setItemDamage(dust * 2);
		}
	}
	
	public static int getValue(ItemStack pouch) {
		int damage = pouch.getItemDamage();
		return damage >> 1;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister IIconRegister) {
		this.bagIIcon = IIconRegister.registerIcon(DustMod.spritePath + "dustPouch_back");
		this.mainIIcon = IIconRegister.registerIcon(DustMod.spritePath + "dustPouch_main");
		this.subIIcon = IIconRegister.registerIcon(DustMod.spritePath + "dustPouch_sub");
	}
}
