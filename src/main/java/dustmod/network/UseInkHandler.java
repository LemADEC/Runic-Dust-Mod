package dustmod.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dustmod.items.ItemInk;

public class UseInkHandler implements IMessageHandler<UseInkMessage, IMessage> {

	@Override
	public IMessage onMessage(UseInkMessage message, MessageContext ctx) {
		
    	EntityPlayer ep = ctx.getServerHandler().playerEntity;
    	ItemStack stack = ep.inventory.getStackInSlot(message.getSlot());
    	ItemInk.reduce(ep, stack, message.getAmount());
    	ep.inventory.setInventorySlotContents(message.getSlot(), stack);

		return null;
	}

}
