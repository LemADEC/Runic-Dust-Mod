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
	public IMessage onMessage(SetInscriptionMessage message, MessageContext ctx) {

		int[] design = message.getDesign();

		EntityPlayerMP ep = ctx.getServerHandler().playerEntity;
		ItemStack stack = ep.inventory.getCurrentItem();
		DustMod.logger.info("SetInscription");
		
		if (stack != null && stack.getItem() == DustMod.inscription) {
			DustMod.logger.info("SetInscription 1");
			if (stack.getItemDamage() == 0)
				stack.setItemDamage(1);
			
			NBTTagCompound tag = stack.getTagCompound();
			if (tag == null) {
				tag = new NBTTagCompound();
				stack.setTagCompound(tag);
			}
			
			tag.setIntArray("design", design);

			ep.inventory.setInventorySlotContents(ep.inventory.currentItem, stack);
			ep.inventory.markDirty();
			//ep.inventoryContainer.
			ep.sendContainerToPlayer(ep.inventoryContainer);
		}

		return null;
	}

}
