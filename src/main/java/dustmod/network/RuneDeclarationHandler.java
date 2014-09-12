package dustmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dustmod.runes.RuneManager;

public class RuneDeclarationHandler implements IMessageHandler<RuneDeclarationMessage, IMessage> {

	@Override
	public IMessage onMessage(RuneDeclarationMessage message, MessageContext ctx) {
		
		RuneManager.registerRemoteDustShape(message.getShape());

		return null;
	}

}
