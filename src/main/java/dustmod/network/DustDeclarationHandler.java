package dustmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dustmod.dusts.Dust;
import dustmod.dusts.DustManager;

public class DustDeclarationHandler implements IMessageHandler<DustDeclarationMessage, IMessage> {

	@Override
	public IMessage onMessage(DustDeclarationMessage message, MessageContext ctx) {
		
		Dust dust = message.getDust();
		int value = message.getValue();
		
		DustManager.registerRemoteDust(value, dust);

		return null;
	}

}
