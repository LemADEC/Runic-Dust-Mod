/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dustmod.client;

import java.util.HashMap;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
//forge

/**
 *
 * @author billythegoat101
 */
public class RenderLastHandler  /*implements IRenderWorldLastHandler */{ //[forge

    public static HashMap<Object[], IRenderLast> map = new HashMap<Object[], IRenderLast>();

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent evt)
    {
    	float partialTicks;
    	partialTicks = evt.partialTicks;
        for (Object[] o: map.keySet())
        {
//            System.out.println("Render");
            map.get(o).renderLast(o, partialTicks);
        }

//        System.out.println("Reset");
        map = new HashMap<Object[], IRenderLast>();
    }
    public static void registerLastRender(IRenderLast rend, Object[] o)
    {
//        System.out.println("Regist");
        map.put(o, rend);
    }
}
