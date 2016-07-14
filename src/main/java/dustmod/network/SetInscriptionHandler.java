package dustmod.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dustmod.DustMod;

public class SetInscriptionHandler implements IMessageHandler<SetInscriptionMessage, IMessage> {
	
	@Override
	public IMessage onMessage(SetInscriptionMessage message, MessageContext messageContext) {
		
		int[] design = message.getDesign();
		
		EntityPlayerMP entityPlayer = messageContext.getServerHandler().playerEntity;
		ItemStack itemStack = entityPlayer.inventory.getCurrentItem();
		
		if (itemStack != null && itemStack.getItem() == DustMod.inscription) {
			// starts drying
			if (itemStack.getItemDamage() == 0) {
				itemStack.setItemDamage(1);
			}
			
			// save design
			NBTTagCompound tag = itemStack.getTagCompound();
			if (tag == null) {
				tag = new NBTTagCompound();
				itemStack.setTagCompound(tag);
			}
			tag.setIntArray("design", design);
			
			// update player inventory
			entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, itemStack);
			entityPlayer.inventory.markDirty();
			entityPlayer.sendContainerToPlayer(entityPlayer.inventoryContainer);
		}
		
		return null;
	}
}
