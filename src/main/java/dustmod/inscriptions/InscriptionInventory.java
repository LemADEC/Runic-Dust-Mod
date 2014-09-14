package dustmod.inscriptions;

import dustmod.DustMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InscriptionInventory implements IInventory {

	
	public int[] inv;
	public ItemStack[] items;
	
	public int width;
	public int height;
	
	public NBTTagCompound tag;
	
	public InscriptionInventory(ItemStack inscription){
		this(inscription, 16,16);
	}
	
	public InscriptionInventory(ItemStack inscription, int width, int height){
		this.width = width;
		this.height = height;
		inv = new int[width*height];
		items = new ItemStack[10];

		tag = inscription.getTagCompound();
		
		if (tag == null) {
			tag = new NBTTagCompound();
			inscription.setTagCompound(tag);
		} else {
			if (tag.hasKey("design")) {
				inv = tag.getIntArray("design").clone();
			}
		}
		
	}
	
	@Override
	public int getSizeInventory() {
		return items.length + inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int loc) {
		if(loc < 10) return items[loc];
		return new ItemStack(DustMod.inscription, 1, inv[loc-10]);
	}

	@Override
	public ItemStack decrStackSize(int loc, int amt) {
		if(loc < 10) {
			items[loc].stackSize -= amt;
			if(items[loc].stackSize <= 0) items[loc] = null;
			return items[loc];
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int loc) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int loc, ItemStack value) {
		if(loc < 10) items[loc] = value;
		else {
			if(value != null) inv[loc-10] = value.getItemDamage();
			else inv[loc-10] = -1;
		}
	}

	public boolean canEdit(){
		if(tag.hasKey("dried")) return !tag.getBoolean("dried");
		tag.setBoolean("dried",false);
		return true;
	}
	
	
	@Override
	public String getInventoryName() {
		return "RunicInscription";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}
	
	@Override
	public boolean hasCustomInventoryName() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
		// TODO Auto-generated method stub
		return false;
	}

}
