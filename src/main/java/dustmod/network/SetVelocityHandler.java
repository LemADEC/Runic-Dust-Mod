package dustmod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SetVelocityHandler implements IMessageHandler<SetVelocityMessage, IMessage> {

	@Override
	public IMessage onMessage(SetVelocityMessage message, MessageContext ctx) {

		EntityLivingBase ent = (EntityLivingBase) Minecraft.getMinecraft().theWorld.getEntityByID(message.getEntityId());

		if (ent != null) {
			ent.addVelocity(-ent.motionX + message.getMotionX(), -ent.motionY + message.getMotionY(), -ent.motionZ + message.getMotionZ());
			ent.jumpMovementFactor = message.getJumpMovementFactor();
		}

		return null;
	}

}
