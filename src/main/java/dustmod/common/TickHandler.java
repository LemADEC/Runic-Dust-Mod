package dustmod.common;

import java.util.EnumSet;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import dustmod.DustMod;

public class TickHandler {
	
	private static final EnumSet<TickEvent.Type> tickTypes = EnumSet.of(TickEvent.Type.CLIENT, TickEvent.Type.SERVER);
	
	@SubscribeEvent
	public void tickStart(TickEvent event) {
		
		if (event.phase == Phase.START && tickTypes.contains(event.type))
		{
			DustMod.proxy.tickMouseManager();
			if (event.side == Side.SERVER)
				DustMod.keyHandler.tick();
		}
	}

}
