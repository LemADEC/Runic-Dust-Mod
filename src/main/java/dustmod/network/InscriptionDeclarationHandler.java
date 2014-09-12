package dustmod.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dustmod.inscriptions.InscriptionManager;

public class InscriptionDeclarationHandler implements IMessageHandler<InscriptionDeclarationMessage, IMessage> {

	@Override
	public IMessage onMessage(InscriptionDeclarationMessage message, MessageContext ctx) {
		
		InscriptionManager.registerRemoteInscriptionEvent(message.getInscription());
		
		return null;
	}

}
