package dustmod.network;

import java.util.Random;

import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ParticleHandler implements IMessageHandler<ParticleMessage, IMessage> {

	@Override
	public IMessage onMessage(ParticleMessage message, MessageContext ctx) {

		for (int l = 0; l < message.getLocLength() / 3; l++) {
			double x = message.getLoc(l * 3);
			double y = message.getLoc(l * 3 + 1);
			double z = message.getLoc(l * 3 + 2);
			
			// Because client side message handlers get instantiated on server side
			World world = FMLClientHandler.instance().getClientPlayerEntity().worldObj;
			
			Random rand = new Random((long) (x + y + z + world.getWorldTime()));
			
			for (int i = 0; i < message.getAmt(); i++) {
				double nx = x + rand.nextDouble() * (message.getRx() * 2) - message.getRx();
				double ny = y + rand.nextDouble() * (message.getRy() * 2) - message.getRy();
				double nz = z + rand.nextDouble() * (message.getRz() * 2) - message.getRz();
				world.spawnParticle(message.getType(), nx, ny, nz, message.getVelx(), message.getVely(), message.getVelz());
			}
		}

		return null;
	}

}
