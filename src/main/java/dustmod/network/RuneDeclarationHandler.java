package dustmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dustmod.DustManager;

public class RuneDeclarationHandler implements IMessageHandler<RuneDeclarationMessage, IMessage> {

	@Override
	public IMessage onMessage(RuneDeclarationMessage message, MessageContext ctx) {
		
		DustManager.registerRemoteDustShape(message.getShape());

		return null;
	}

}
