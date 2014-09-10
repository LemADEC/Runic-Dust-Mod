package dustmod;

import java.util.EnumSet;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class TickHandler {
	
	private static final EnumSet<TickEvent.Type> tickTypes = EnumSet.of(TickEvent.Type.CLIENT, TickEvent.Type.SERVER);
	
	@SubscribeEvent
	public void tickStart(TickEvent event) {
		
		if (event.phase == Phase.START && tickTypes.contains(event.type))
		{
			DustMod.proxy.tickMouseManager();
			DustMod.keyHandler.tick();
		}
	}

}
