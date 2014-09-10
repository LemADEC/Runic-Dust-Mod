package dustmod.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SetInscriptionHandler implements IMessageHandler<SetInscriptionMessage, IMessage> {

	@Override
	public IMessage onMessage(SetInscriptionMessage message, MessageContext ctx) {

		int[] design = message.getDesign();

		EntityPlayer ep = ctx.getServerHandler().playerEntity;
		ItemStack stack = ep.getCurrentEquippedItem();
		
		// TODO item check
		if (stack != null) {
			if (stack.getItemDamage() == 0)
				stack.setItemDamage(1);
			
			NBTTagCompound tag = stack.getTagCompound();
			if (tag == null) {
				tag = new NBTTagCompound();
				stack.setTagCompound(tag);
			}
			
			tag.setIntArray("design", design);

			// EntityPlayerMP mp = (EntityPlayerMP)player;
			// mp.upsendInventoryToPlayer(); //TEST find replacement
		}

		return null;
	}

}
