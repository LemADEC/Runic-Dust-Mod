package dustmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dustmod.DustMod;

public class MouseHandler implements IMessageHandler<MouseMessage, IMessage> {

	@Override
	public IMessage onMessage(MouseMessage message, MessageContext ctx) {
		
		DustMod.keyHandler.setKey(ctx.getServerHandler().playerEntity, message.getKeyId(), message.isPressed());
		
		return null;
	}

}
